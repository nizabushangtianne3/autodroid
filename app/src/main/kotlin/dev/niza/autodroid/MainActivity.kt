package dev.niza.autodroid

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.LinearLayout
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import dev.niza.autodroid.engine.LinkOpenedTrigger
import dev.niza.autodroid.engine.AppLaunchedTrigger
import dev.niza.autodroid.engine.DataSmsReceivedTrigger
import dev.niza.autodroid.engine.NotifyAction
import dev.niza.autodroid.engine.OpenUrlAction
import dev.niza.autodroid.engine.SendDataSmsAction
import dev.niza.autodroid.engine.value
import dev.niza.autodroid.engine.Rule

class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Runtime.initialize(this)
        Runtime.loadRules(this)
        DataSmsRegistrar.refresh(this)
        requestNotificationPermission()
        requestSmsPermission()
        listView = ListView(this)
        setupAddButton(listView)
        render(listView)

        handleNotificationTarget(intent, listView)

        listView.setOnItemClickListener { _, _, position, _ ->
            editRule(Runtime.engine.rules()[position], listView)
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            Runtime.engine.remove(Runtime.engine.rules()[position].id)
            Runtime.saveRules(this)
            DataSmsRegistrar.refresh(this)
            render(listView)
            true
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (this::listView.isInitialized) handleNotificationTarget(intent, listView)
    }

    private fun handleNotificationTarget(intent: Intent?, listView: ListView) {
        val ruleId = intent?.getStringExtra(Runtime.EXTRA_RULE_ID) ?: return
        intent.removeExtra(Runtime.EXTRA_RULE_ID)
        val rule = Runtime.engine.rules().firstOrNull { it.id == ruleId } ?: return
        editRule(rule, listView)
    }

    private fun setupAddButton(listView: ListView) {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val addButton = com.google.android.material.floatingactionbutton.FloatingActionButton(this).apply {
            setImageResource(android.R.drawable.ic_input_add)
            setOnClickListener { createRule(listView) }
        }
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        ).apply { setMargins(24, 24, 24, 24) }
        addButton.layoutParams = params
        container.addView(listView)
        container.addView(addButton)
        setContentView(container)
    }

    private fun createRule(listView: ListView) {
        val existingIds = Runtime.engine.rules().map { it.id }.toSet()
        val sequence = generateSequence(1) { it + 1 }.first { "sample-$it" !in existingIds }
        val rule = Rule(
            id = "sample-$sequence",
            name = "Sample rule $sequence",
            trigger = LinkOpenedTrigger("trigger-$sequence", host = ""),
            actions = listOf(NotifyAction("action-$sequence", "AutoDroid", "Link matched")),
        )
        editRule(rule, listView)
    }

    private fun editRule(rule: Rule, listView: ListView) {
        val name = EditText(this).apply {
            hint = "Rule name"
            setText(rule.name)
        }
        val triggerType = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_dropdown_item,
                listOf("Link", "App", "Data SMS"),
            )
            setSelection(
                when (rule.trigger) {
                    is AppLaunchedTrigger -> 1
                    is DataSmsReceivedTrigger -> 2
                    else -> 0
                },
            )
        }
        val triggerHintFor = { typePosition: Int ->
            when (typePosition) {
                1 -> "App package name"
                2 -> "Data SMS destination port"
                else -> "Link host"
            }
        }
        val triggerValue = EditText(this).apply {
            hint = triggerHintFor(
                when (rule.trigger) {
                    is AppLaunchedTrigger -> 1
                    is DataSmsReceivedTrigger -> 2
                    else -> 0
                },
            )
            setText(rule.trigger.value)
        }
        triggerType.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                triggerValue.hint = triggerHintFor(position)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
        val triggerSender = EditText(this).apply {
            hint = "SMS sender (optional)"
            setText((rule.trigger as? DataSmsReceivedTrigger)?.sender.orEmpty())
        }
        val actionType = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_dropdown_item,
                listOf("Notify", "Open URL", "Send Data SMS"),
            )
            setSelection(
                when (rule.actions.firstOrNull()) {
                    is OpenUrlAction -> 1
                    is SendDataSmsAction -> 2
                    else -> 0
                },
            )
        }
        val actionHintFor = { typePosition: Int ->
            when (typePosition) {
                1 -> "URL"
                2 -> "Destination phone number"
                else -> "Notification title"
            }
        }
        val actionExtraHintFor = { typePosition: Int ->
            when (typePosition) {
                1 -> "Unused"
                2 -> "Payload hex"
                else -> "Notification body"
            }
        }
        val actionPort = EditText(this).apply {
            hint = "Data SMS destination port"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText((rule.actions.firstOrNull() as? SendDataSmsAction)?.destinationPort?.toString().orEmpty())
        }
        val actionValue = EditText(this).apply {
            hint = actionHintFor(
                when (rule.actions.firstOrNull()) {
                    is OpenUrlAction -> 1
                    is SendDataSmsAction -> 2
                    else -> 0
                },
            )
            setText(
                when (val action = rule.actions.firstOrNull()) {
                    is NotifyAction -> action.title
                    is OpenUrlAction -> action.url
                    is SendDataSmsAction -> action.sender
                    else -> ""
                },
            )
        }
        val actionExtra = EditText(this).apply {
            hint = actionExtraHintFor(
                when (rule.actions.firstOrNull()) {
                    is OpenUrlAction -> 1
                    is SendDataSmsAction -> 2
                    else -> 0
                },
            )
            setText(
                when (val action = rule.actions.firstOrNull()) {
                    is NotifyAction -> action.body
                    is SendDataSmsAction -> action.payload.joinToString("") { "%02X".format(it) }
                    else -> ""
                },
            )
        }
        actionType.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                actionValue.hint = actionHintFor(position)
                actionExtra.hint = actionExtraHintFor(position)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
        val enabled = CheckBox(this).apply {
            text = "Enabled"
            isChecked = rule.enabled
        }
        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
            addView(name)
            addView(triggerType)
            addView(triggerValue)
            addView(triggerSender)
            addView(actionType)
            addView(actionValue)
            addView(actionPort)
            addView(actionExtra)
            addView(enabled)
        }
        AlertDialog.Builder(this)
            .setTitle("Edit rule")
            .setView(form)
            .setPositiveButton("Save") { _, _ ->
                val sequence = rule.id.substringAfterLast('-')
                val value = triggerValue.text.toString()
                val packageNamePattern = Regex("^[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)+$")
                if (triggerType.selectedItemPosition == 1 && !packageNamePattern.matches(value)) {
                    android.widget.Toast.makeText(this, "Invalid Android package name", android.widget.Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (triggerType.selectedItemPosition == 0 && value.isBlank()) {
                    android.widget.Toast.makeText(this, "Link host is required", android.widget.Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val triggerPort = value.toIntOrNull()
                if (triggerType.selectedItemPosition == 2 && (triggerPort == null || triggerPort !in 1..65535)) {
                    android.widget.Toast.makeText(this, "Data SMS port must be 1-65535", android.widget.Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val actionValueText = actionValue.text.toString()
                val actionExtraText = actionExtra.text.toString()
                val actionPortText = actionPort.text.toString()
                val actionValidationError = validateAction(actionType.selectedItemPosition, actionValueText, actionExtraText, actionPortText)
                if (actionValidationError != null) {
                    android.widget.Toast.makeText(this, actionValidationError, android.widget.Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val sender = triggerSender.text.toString().trim().ifBlank { null }
                val triggerId = rule.trigger.id
                val trigger = when (triggerType.selectedItemPosition) {
                    1 -> AppLaunchedTrigger(triggerId, value)
                    2 -> DataSmsReceivedTrigger(triggerId, destinationPort = value.toIntOrNull() ?: 0, sender = sender)
                    else -> LinkOpenedTrigger(triggerId, host = value)
                }
                val updated = rule.copy(
                    name = name.text.toString(),
                    trigger = trigger,
                    actions = listOf(
                        buildAction(
                            sequence,
                            actionType.selectedItemPosition,
                            actionValueText,
                            actionExtraText,
                            actionPortText,
                        ),
                    ),
                    enabled = enabled.isChecked,
                )
                Runtime.engine.replace(updated)
                Runtime.saveRules(this)
                DataSmsRegistrar.refresh(this)
                render(listView)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun validateAction(typePosition: Int, value: String, extra: String, port: String): String? {
        if (value.isBlank()) {
            return when (typePosition) {
                1 -> "URL is required"
                2 -> "Destination phone number is required"
                else -> "Notification title is required"
            }
        }
        return when (typePosition) {
            1 -> null
            2 -> when {
                port.toIntOrNull() !in 1..65535 -> "Action port must be 1-65535"
                parseHex(extra).isEmpty() -> "Payload hex is required"
                else -> null
            }
            else -> if (extra.isBlank()) "Notification body is required" else null
        }
    }

    private fun buildAction(sequence: String, typePosition: Int, value: String, extra: String, port: String): dev.niza.autodroid.engine.Action {
        val actionId = "action-$sequence"
        return when (typePosition) {
            1 -> OpenUrlAction(actionId, value)
            2 -> SendDataSmsAction(
                actionId,
                destinationPort = port.toIntOrNull() ?: 0,
                sender = value,
                payload = parseHex(extra),
            )
            else -> NotifyAction(actionId, title = value, body = extra)
        }
    }

    private fun parseHex(value: String): ByteArray {
        val clean = value.removePrefix("0x").filterNot { it.isWhitespace() }
        if (clean.isEmpty() || clean.length % 2 != 0) return ByteArray(0)
        return clean.chunked(2).mapNotNull { it.toIntOrNull(16)?.toByte() }.toByteArray()
    }

    private fun requestNotificationPermission() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATIONS,
            )
        }
    }

    private fun requestSmsPermission() {
        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SEND_SMS),
                REQUEST_SMS,
            )
        }
    }

    private companion object {
        private const val REQUEST_NOTIFICATIONS = 1001
        private const val REQUEST_SMS = 1002
    }

    private fun render(listView: ListView) {
        val rules = Runtime.engine.rules()
        listView.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            rules.map { rule ->
                val kind = when (rule.trigger) {
                    is AppLaunchedTrigger -> "App"
                    is DataSmsReceivedTrigger -> "SMS"
                    else -> "Link"
                }
                val actionKind = when (rule.actions.firstOrNull()) {
                    is OpenUrlAction -> "Open URL"
                    is SendDataSmsAction -> "Send SMS"
                    else -> "Notify"
                }
                "${rule.name} • $kind • ${rule.trigger.value.orEmpty()} • $actionKind"
                    .let { if (!rule.enabled) "$it • disabled" else it }
            },
        )
        listView.choiceMode = ListView.CHOICE_MODE_NONE
    }
}
