package dev.niza.autodroid

import android.Manifest
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
import dev.niza.autodroid.engine.value
import dev.niza.autodroid.engine.Rule

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Runtime.initialize(this)
        Runtime.loadRules(this)
        requestNotificationPermission()
        requestSmsPermission()
        val listView = ListView(this)
        setupAddButton(listView)
        render(listView)

        listView.setOnItemClickListener { _, _, position, _ ->
            editRule(Runtime.engine.rules()[position], listView)
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            Runtime.engine.remove(Runtime.engine.rules()[position].id)
            Runtime.saveRules(this)
            render(listView)
            true
        }
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
        val sequence = Runtime.engine.rules().size + 1
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
        val title = EditText(this).apply {
            hint = "Notification title"
            setText((rule.actions.firstOrNull() as? NotifyAction)?.title.orEmpty())
        }
        val body = EditText(this).apply {
            hint = "Notification body"
            setText((rule.actions.firstOrNull() as? NotifyAction)?.body.orEmpty())
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
            addView(title)
            addView(body)
            addView(enabled)
        }
        AlertDialog.Builder(this)
            .setTitle("Edit rule")
            .setView(form)
            .setPositiveButton("Save") { _, _ ->
                val sequence = rule.id.substringAfterLast('-')
                val value = triggerValue.text.toString()
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
                        NotifyAction(
                            id = "action-$sequence",
                            title = title.text.toString(),
                            body = body.text.toString(),
                        ),
                    ),
                    enabled = enabled.isChecked,
                )
                Runtime.engine.replace(updated)
                Runtime.saveRules(this)
                render(listView)
            }
            .setNegativeButton("Cancel", null)
            .show()
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
                "${rule.name} • $kind • ${rule.trigger.value.orEmpty()}"
                    .let { if (!rule.enabled) "$it • disabled" else it }
            },
        )
        listView.choiceMode = ListView.CHOICE_MODE_NONE
    }
}
