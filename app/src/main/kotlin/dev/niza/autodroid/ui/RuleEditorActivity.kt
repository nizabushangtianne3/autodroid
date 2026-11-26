package dev.niza.autodroid.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.appbar.MaterialToolbar
import dev.niza.autodroid.DataSmsRegistrar
import dev.niza.autodroid.R
import dev.niza.autodroid.Runtime
import dev.niza.autodroid.ScheduleRegistrar
import dev.niza.autodroid.engine.Action
import dev.niza.autodroid.engine.Constraint
import dev.niza.autodroid.engine.LinkOpenedTrigger
import dev.niza.autodroid.engine.NotifyAction
import dev.niza.autodroid.engine.OpenUrlAction
import dev.niza.autodroid.engine.Rule
import dev.niza.autodroid.engine.SendDataSmsAction
import dev.niza.autodroid.engine.summary
import dev.niza.autodroid.engine.value

class RuleEditorActivity : AppCompatActivity() {
    private val constraints = mutableListOf<Constraint>()
    private val actions = mutableListOf<Action>()
    private lateinit var ruleId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Runtime.initialize(this)
        Runtime.loadRules(this)
        setContentView(R.layout.activity_rule_editor)
        ruleId = intent.getStringExtra(EXTRA_RULE_ID) ?: "sample-1"
        val rule =
            Runtime.engine.rules().firstOrNull { it.id == ruleId }
                ?: defaultRule(ruleId)
        constraints += rule.constraints
        setupToolbar()
        setupTrigger(rule)
        setupAction(rule)
        actions += rule.actions
        renderActions()
        setupConstraints(rule)
        setupSave(rule)
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.editor_toolbar).setNavigationOnClickListener { finish() }
    }

    private fun setupTrigger(rule: Rule) {
        val spinner = findViewById<AppCompatSpinner>(R.id.spinner_trigger_type)
        val first = findViewById<EditText>(R.id.input_trigger_value)
        val second = findViewById<EditText>(R.id.input_trigger_sender)
        val third = findViewById<EditText>(R.id.input_trigger_extra)
        spinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                RuleEditorLogic.triggerEntries.map { it.label },
            )
        spinner.setSelection(RuleEditorLogic.triggerPosition(rule.trigger))
        val fields = RuleEditorLogic.triggerFields(spinner.selectedItemPosition)
        applyFieldHints(fields, first, second, third)
        first.setText(triggerValues(rule).first)
        second.setText(triggerValues(rule).second)
        third.setText(triggerValues(rule).third)
        (first as? AutoCompleteTextView)?.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                launchablePackages(),
            ),
        )
        spinner.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long,
                ) {
                    applyFieldHints(RuleEditorLogic.triggerFields(position), first, second, third)
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
            }
    }

    private fun applyFieldHints(
        fields: List<dev.niza.autodroid.engine.TriggerDescriptor.Field>,
        first: EditText,
        second: EditText,
        third: EditText,
    ) {
        listOf(first, second, third).forEachIndexed { index, field ->
            val type = fields.getOrNull(index)
            field.hint = type?.let { RuleEditorLogic.fieldHint(it) } ?: ""
        }
    }

    /** Reads the three editor fields back out of an existing trigger so it can be re-saved. */
    private fun triggerValues(rule: Rule): Triple<String, String, String> =
        when (val trigger = rule.trigger) {
            is dev.niza.autodroid.engine.LinkOpenedTrigger ->
                Triple(
                    trigger.host.orEmpty(),
                    trigger.scheme.orEmpty(),
                    trigger.pathPrefix.orEmpty(),
                )
            is dev.niza.autodroid.engine.AppLaunchedTrigger -> Triple(trigger.packageName, "", "")
            is dev.niza.autodroid.engine.DataSmsReceivedTrigger -> Triple(trigger.destinationPort.toString(), trigger.sender.orEmpty(), "")
            is dev.niza.autodroid.engine.ScheduleTrigger -> Triple(trigger.intervalMillis.toString(), "", "")
            is dev.niza.autodroid.engine.BatteryLevelTrigger -> Triple(trigger.level.toString(), trigger.direction, "")
            is dev.niza.autodroid.engine.BatteryTemperatureTrigger -> Triple(trigger.temperatureCelsius.toString(), trigger.direction, "")
            is dev.niza.autodroid.engine.LightSensorTrigger -> Triple(trigger.lux.toString(), trigger.direction, "")
            is dev.niza.autodroid.engine.WifiSSIDTrigger -> Triple(trigger.ssid, "", "")
            is dev.niza.autodroid.engine.DockTrigger -> Triple(trigger.state, "", "")
            is dev.niza.autodroid.engine.IncomingCallTrigger -> Triple(trigger.phoneNumber.orEmpty(), "", "")
            is dev.niza.autodroid.engine.IncomingSMSTrigger -> Triple(trigger.sender.orEmpty(), trigger.contentMatch.orEmpty(), "")
            is dev.niza.autodroid.engine.MediaButtonTrigger -> Triple(trigger.button, "", "")
            is dev.niza.autodroid.engine.IntentReceivedTrigger -> Triple(trigger.action, "", "")
            is dev.niza.autodroid.engine.LogcatTrigger -> Triple(trigger.pattern, "", "")
            is dev.niza.autodroid.engine.VariableTrigger -> Triple(trigger.variableName, trigger.expression, "")
            is dev.niza.autodroid.engine.MacroFinishedTrigger -> Triple(trigger.macroId, "", "")
            is dev.niza.autodroid.engine.ShortcutTrigger -> Triple(trigger.shortcutName, "", "")
            is dev.niza.autodroid.engine.GeofenceTrigger ->
                Triple(
                    trigger.name,
                    trigger.radiusMeters.toString(),
                    if (trigger.enter) "true" else "false",
                )
            is dev.niza.autodroid.engine.DataUsedTrigger -> Triple(trigger.bytes.toString(), trigger.direction, "")
            is dev.niza.autodroid.engine.FailedLoginTrigger -> Triple(trigger.attempts.toString(), "", "")
            is dev.niza.autodroid.engine.StopwatchTrigger -> Triple(trigger.durationMillis.toString(), "", "")
            is dev.niza.autodroid.engine.HttpServerTrigger -> Triple(trigger.port.toString(), "", "")
            is dev.niza.autodroid.engine.DayOfMonthTrigger -> Triple(trigger.day.toString(), "", "")
            is dev.niza.autodroid.engine.AppLifecycleTrigger -> Triple(if (trigger.added) "installed" else "removed", "", "")
            else -> Triple(rule.trigger.value.orEmpty(), "", "")
        }

    private fun setupAction(rule: Rule) {
        val spinner = findViewById<AppCompatSpinner>(R.id.spinner_action_type)
        val value = findViewById<EditText>(R.id.input_action_value)
        val extra = findViewById<EditText>(R.id.input_action_extra)
        spinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                RuleEditorLogic.actionEntries.map { it.label },
            )
        spinner.setSelection(RuleEditorLogic.actionPosition(rule.actions.firstOrNull()))
        val fields = RuleEditorLogic.actionFields(spinner.selectedItemPosition)
        applyFieldHints(fields, value, extra, findViewById(R.id.input_action_port))
        val existing = actionValues(rule.actions.firstOrNull())
        value.setText(existing.first)
        extra.setText(existing.second)
        findViewById<EditText>(R.id.input_action_port).setText(existing.third)
        spinner.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long,
                ) {
                    applyFieldHints(
                        RuleEditorLogic.actionFields(position),
                        value,
                        extra,
                        findViewById(R.id.input_action_port),
                    )
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
            }
        findViewById<ListView>(R.id.list_actions).setOnItemLongClickListener { _, _, position, _ ->
            actions.removeAt(position)
            renderActions()
            Toast.makeText(this, "已删除动作", Toast.LENGTH_SHORT).show()
            true
        }
        findViewById<com.google.android.material.button.MaterialButton>(R.id.button_add_action)
            .setOnClickListener {
                val typePosition = findViewById<AppCompatSpinner>(R.id.spinner_action_type).selectedItemPosition
                val value = findViewById<EditText>(R.id.input_action_value).text.toString().trim()
                val extra = findViewById<EditText>(R.id.input_action_extra).text.toString().trim()
                val port = findViewById<EditText>(R.id.input_action_port).text.toString().trim()
                val error =
                    RuleEditorLogic.validateCatalogFields(
                        RuleEditorLogic.actionFields(typePosition),
                        listOf(value, extra, port),
                    )
                if (error != null) {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val actionId = "action-${actions.size + 1}-${System.currentTimeMillis()}"
                actions += RuleEditorLogic.buildCatalogAction(actionId, typePosition, value, extra, port)
                renderActions()
                findViewById<EditText>(R.id.input_action_value).setText("")
                findViewById<EditText>(R.id.input_action_extra).setText("")
                findViewById<EditText>(R.id.input_action_port).setText("")
            }
    }

    private fun actionValues(action: Action?): Triple<String, String, String> =
        when (action) {
            null -> Triple("", "", "")
            is dev.niza.autodroid.engine.NotifyAction -> Triple(action.title, action.body, "")
            is dev.niza.autodroid.engine.OpenUrlAction -> Triple(action.url, "", "")
            is dev.niza.autodroid.engine.SendDataSmsAction ->
                Triple(action.sender, action.destinationPort.toString(), action.payload.joinToString("") { "%02X".format(it) })
            is dev.niza.autodroid.engine.SetVariableAction -> Triple(action.name, action.value, "")
            is dev.niza.autodroid.engine.WaitAction -> Triple(action.durationMillis.toString(), "", "")
            is dev.niza.autodroid.engine.RepeatStartAction -> Triple(action.count.toString(), "", "")
            is dev.niza.autodroid.engine.LaunchAppAction -> Triple(action.packageName, "", "")
            is dev.niza.autodroid.engine.ClipboardAction -> Triple(action.text, "", "")
            is dev.niza.autodroid.engine.SpeakTextAction -> Triple(action.text, "", "")
            is dev.niza.autodroid.engine.VibrateAction -> Triple(action.durationMillis.toString(), "", "")
            is dev.niza.autodroid.engine.SetVolumeAction -> Triple(action.stream, action.level.toString(), "")
            is dev.niza.autodroid.engine.SetBrightnessAction -> Triple(action.level.toString(), action.automatic.toString(), "")
            is dev.niza.autodroid.engine.ToastAction -> Triple(action.text, "", "")
            is dev.niza.autodroid.engine.SendSMSAction -> Triple(action.recipient, action.message, "")
            is dev.niza.autodroid.engine.MakeCallAction -> Triple(action.phoneNumber, "", "")
            is dev.niza.autodroid.engine.IfConditionAction -> Triple(action.expression, "", "")
            is dev.niza.autodroid.engine.WriteToFileAction -> Triple(action.path, action.content, "")
            is dev.niza.autodroid.engine.LogAction -> Triple(action.message, "", "")
            is dev.niza.autodroid.engine.IterateDictionaryAction -> Triple(action.dictionaryName, action.keyVariable, action.valueVariable)
            is dev.niza.autodroid.engine.SetDictionaryEntryAction -> Triple(action.dictionaryName, action.key, action.value)
            is dev.niza.autodroid.engine.ShareTextAction -> Triple(action.subject, action.text, "")
            else -> Triple(action.summary, "", "")
        }

    private fun setupConstraints(rule: Rule) {
        val spinner = findViewById<AppCompatSpinner>(R.id.spinner_constraint_type)
        val value = findViewById<EditText>(R.id.input_constraint_value)
        val extra = findViewById<EditText>(R.id.input_constraint_extra)
        spinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ConstraintEditorLogic.labels)
        spinner.setSelection(ConstraintEditorLogic.TYPE_WIFI)
        value.hint = ConstraintEditorLogic.valueHint(ConstraintEditorLogic.TYPE_WIFI)
        extra.hint = ConstraintEditorLogic.extraHint(ConstraintEditorLogic.TYPE_WIFI)
        spinner.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long,
                ) {
                    value.hint = ConstraintEditorLogic.valueHint(position)
                    extra.hint = ConstraintEditorLogic.extraHint(position)
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
            }
        findViewById<com.google.android.material.button.MaterialButton>(R.id.button_add_constraint)
            .setOnClickListener {
                val type = spinner.selectedItemPosition
                val valueText = value.text.toString().trim()
                val extraText = extra.text.toString().trim()
                val error = ConstraintEditorLogic.validate(type, valueText, extraText)
                if (error != null) {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val built = ConstraintEditorLogic.build("constraint-${constraints.size + 1}", type, valueText, extraText)
                constraints.removeAll { ConstraintEditorLogic.typePosition(it) == type }
                constraints += built
                renderConstraints()
                value.setText("")
                extra.setText("")
            }
        findViewById<ListView>(R.id.list_constraints).setOnItemLongClickListener { _, _, position, _ ->
            constraints.removeAt(position)
            renderConstraints()
            Toast.makeText(this, "已删除约束", Toast.LENGTH_SHORT).show()
            true
        }
        findViewById<SwitchCompat>(R.id.switch_enabled).isChecked = rule.enabled
        renderConstraints()
    }

    private fun setupSave(rule: Rule) {
        findViewById<EditText>(R.id.input_macro_name).setText(rule.name)
        findViewById<com.google.android.material.button.MaterialButton>(R.id.button_save_macro)
            .setOnClickListener {
                val triggerType = findViewById<AppCompatSpinner>(R.id.spinner_trigger_type).selectedItemPosition
                val triggerFirst = findViewById<EditText>(R.id.input_trigger_value).text.toString().trim()
                val triggerSecond = findViewById<EditText>(R.id.input_trigger_sender).text.toString().trim()
                val triggerThird = findViewById<EditText>(R.id.input_trigger_extra).text.toString().trim()
                val triggerError =
                    RuleEditorLogic.validateCatalogFields(
                        RuleEditorLogic.triggerFields(triggerType),
                        listOf(triggerFirst, triggerSecond, triggerThird),
                    )
                if (triggerError != null) {
                    Toast.makeText(this, triggerError, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val name =
                    findViewById<EditText>(R.id.input_macro_name).text.toString().ifBlank { "Untitled Macro" }
                val updated =
                    rule.copy(
                        name = name,
                        trigger =
                            RuleEditorLogic.buildCatalogTrigger(
                                rule.trigger.id,
                                triggerType,
                                triggerFirst,
                                triggerSecond,
                                triggerThird,
                            ),
                        actions = actions.toList(),
                        constraints = constraints.toList(),
                        enabled = findViewById<SwitchCompat>(R.id.switch_enabled).isChecked,
                    )
                Runtime.engine.replace(updated)
                Runtime.saveRules(this)
                DataSmsRegistrar.refresh(this)
                ScheduleRegistrar.refresh(this)
                setResult(RESULT_OK, Intent().putExtra(EXTRA_RULE_ID, updated.id))
                finish()
            }
    }

    private fun renderConstraints() {
        findViewById<ListView>(R.id.list_constraints).adapter =
            ArrayAdapter(this, R.layout.item_constraint, constraints.map(ConstraintEditorLogic::label))
    }

    private fun renderActions() {
        findViewById<ListView>(R.id.list_actions).adapter =
            ArrayAdapter(this, R.layout.item_constraint, actions.map { it.summary })
    }

    private fun launchablePackages(): List<String> =
        packageManager
            .queryIntentActivities(
                android.content.Intent(android.content.Intent.ACTION_MAIN).addCategory(android.content.Intent.CATEGORY_LAUNCHER),
                0,
            )
            .mapNotNull { info ->
                val packageName = info.activityInfo?.packageName ?: return@mapNotNull null
                val label = runCatching { info.loadLabel(packageManager).toString() }.getOrNull()
                if (label.isNullOrBlank() || label == packageName) packageName else "$label • $packageName"
            }
            .distinct()
            .sorted()

    private fun defaultRule(id: String): Rule =
        Rule(
            id = id,
            name = "Macro ${id.substringAfterLast('-')}",
            trigger = LinkOpenedTrigger("trigger-$id", host = ""),
            actions = listOf(NotifyAction("action-$id", "AutoDroid", "Triggered")),
        )

    companion object {
        const val EXTRA_RULE_ID = "extra_rule_id"
    }
}
