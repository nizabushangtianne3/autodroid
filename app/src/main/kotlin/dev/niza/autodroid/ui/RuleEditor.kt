package dev.niza.autodroid.ui

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import dev.niza.autodroid.DataSmsRegistrar
import dev.niza.autodroid.MainActivity
import dev.niza.autodroid.Runtime
import dev.niza.autodroid.engine.AppLaunchedTrigger
import dev.niza.autodroid.engine.DataSmsReceivedTrigger
import dev.niza.autodroid.engine.LinkOpenedTrigger
import dev.niza.autodroid.engine.NotifyAction
import dev.niza.autodroid.engine.OpenUrlAction
import dev.niza.autodroid.engine.Rule
import dev.niza.autodroid.engine.SendDataSmsAction
import dev.niza.autodroid.engine.value

class RuleEditor(private val activity: MainActivity) {
    fun editRule(
        rule: Rule,
        listView: ListView,
    ) {
        val name =
            EditText(activity).apply {
                hint = "Rule name"
                setText(rule.name)
            }
        val triggerType =
            Spinner(activity).apply {
                adapter =
                    ArrayAdapter(
                        activity,
                        android.R.layout.simple_spinner_dropdown_item,
                        listOf("Link", "App", "Data SMS", "Schedule"),
                    )
                setSelection(
                    when (rule.trigger) {
                        is AppLaunchedTrigger -> 1
                        is DataSmsReceivedTrigger -> 2
                        else -> 0
                    },
                )
            }
        val triggerHintFor = RuleEditorLogic::triggerHint
        val triggerValue =
            AutoCompleteTextView(activity).apply {
                hint =
                    triggerHintFor(
                        when (rule.trigger) {
                            is AppLaunchedTrigger -> RuleEditorLogic.TYPE_APP
                            is DataSmsReceivedTrigger -> RuleEditorLogic.TYPE_DATA_SMS
                            is dev.niza.autodroid.engine.ScheduleTrigger -> RuleEditorLogic.TYPE_SCHEDULE
                            else -> RuleEditorLogic.TYPE_LINK
                        },
                    )
                setText(rule.trigger.value)
            }
        triggerValue.setAdapter(
            ArrayAdapter(
                activity,
                android.R.layout.simple_dropdown_item_1line,
                launchablePackages(activity),
            ),
        )
        triggerValue.setOnItemClickListener { _, _, position, _ ->
            val selected = (triggerValue.adapter.getItem(position) as? String) ?: return@setOnItemClickListener
            triggerValue.setText(selected.substringAfterLast(" • "))
        }
        triggerType.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long,
                ) {
                    triggerValue.hint = triggerHintFor(position)
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
            }
        val triggerSender =
            EditText(activity).apply {
                hint = "SMS sender (optional)"
                setText((rule.trigger as? DataSmsReceivedTrigger)?.sender.orEmpty())
            }
        val actionType =
            Spinner(activity).apply {
                adapter =
                    ArrayAdapter(
                        activity,
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
        val actionHintFor = RuleEditorLogic::actionHint
        val actionExtraHintFor = RuleEditorLogic::actionExtraHint
        val actionPort =
            EditText(activity).apply {
                hint = "Data SMS destination port"
                inputType = android.text.InputType.TYPE_CLASS_NUMBER
                setText((rule.actions.firstOrNull() as? SendDataSmsAction)?.destinationPort?.toString().orEmpty())
            }
        val actionValue =
            EditText(activity).apply {
                hint =
                    actionHintFor(
                        when (rule.actions.firstOrNull()) {
                            is OpenUrlAction -> RuleEditorLogic.ACTION_OPEN_URL
                            is SendDataSmsAction -> RuleEditorLogic.ACTION_SEND_DATA_SMS
                            else -> RuleEditorLogic.ACTION_NOTIFY
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
        val actionExtra =
            EditText(activity).apply {
                hint =
                    actionExtraHintFor(
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
        actionType.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long,
                ) {
                    actionValue.hint = actionHintFor(position)
                    actionExtra.hint = actionExtraHintFor(position)
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
            }
        val enabled =
            CheckBox(activity).apply {
                text = "Enabled"
                isChecked = rule.enabled
            }
        val form =
            LinearLayout(activity).apply {
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
        AlertDialog.Builder(activity)
            .setTitle("Edit rule")
            .setView(form)
            .setPositiveButton("Save") { _, _ ->
                val sequence = rule.id.substringAfterLast('-')
                val value = triggerValue.text.toString()
                val triggerValidationError = RuleEditorLogic.validateTrigger(triggerType.selectedItemPosition, value)
                if (triggerValidationError != null) {
                    android.widget.Toast.makeText(activity, triggerValidationError, android.widget.Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val actionValueText = actionValue.text.toString()
                val actionExtraText = actionExtra.text.toString()
                val actionPortText = actionPort.text.toString()
                val actionValidationError =
                    RuleEditorLogic.validateAction(actionType.selectedItemPosition, actionValueText, actionExtraText, actionPortText)
                if (actionValidationError != null) {
                    android.widget.Toast.makeText(activity, actionValidationError, android.widget.Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val sender = triggerSender.text.toString().trim().ifBlank { null }
                val triggerId = rule.trigger.id
                val trigger =
                    when (triggerType.selectedItemPosition) {
                        RuleEditorLogic.TYPE_APP -> AppLaunchedTrigger(triggerId, value)
                        RuleEditorLogic.TYPE_DATA_SMS ->
                            DataSmsReceivedTrigger(
                                triggerId,
                                destinationPort = value.toIntOrNull() ?: 0,
                                sender = sender,
                            )
                        RuleEditorLogic.TYPE_SCHEDULE ->
                            dev.niza.autodroid.engine.ScheduleTrigger(
                                triggerId,
                                intervalMillis = value.toLongOrNull() ?: 0,
                            )
                        else -> LinkOpenedTrigger(triggerId, host = value)
                    }
                val updated =
                    rule.copy(
                        name = name.text.toString(),
                        trigger = trigger,
                        actions =
                            listOf(
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
                Runtime.saveRules(activity)
                DataSmsRegistrar.refresh(activity)
                activity.render(listView)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun buildAction(
        sequence: String,
        typePosition: Int,
        value: String,
        extra: String,
        port: String,
    ): dev.niza.autodroid.engine.Action {
        val actionId = "action-$sequence"
        return when (typePosition) {
            RuleEditorLogic.ACTION_OPEN_URL -> OpenUrlAction(actionId, value)
            RuleEditorLogic.ACTION_SEND_DATA_SMS ->
                SendDataSmsAction(
                    actionId,
                    destinationPort = port.toIntOrNull() ?: 0,
                    sender = value,
                    payload = RuleEditorLogic.parseHex(extra),
                )
            else -> NotifyAction(actionId, title = value, body = extra)
        }
    }

    private fun launchablePackages(activity: MainActivity): List<String> =
        activity.packageManager
            .queryIntentActivities(
                android.content.Intent(android.content.Intent.ACTION_MAIN).addCategory(
                    android.content.Intent.CATEGORY_LAUNCHER,
                ),
                0,
            )
            .mapNotNull { info ->
                val packageName = info.activityInfo?.packageName ?: return@mapNotNull null
                val label =
                    runCatching {
                        info.loadLabel(activity.packageManager).toString()
                    }.getOrNull()
                if (label.isNullOrBlank() || label == packageName) {
                    packageName
                } else {
                    "$label • $packageName"
                }
            }
            .distinct()
            .sorted()
}
