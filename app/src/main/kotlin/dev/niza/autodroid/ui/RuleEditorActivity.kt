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
import dev.niza.autodroid.engine.value

class RuleEditorActivity : AppCompatActivity() {
    private val constraints = mutableListOf<Constraint>()
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
        setupConstraints(rule)
        setupSave(rule)
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.editor_toolbar).setNavigationOnClickListener { finish() }
    }

    private fun setupTrigger(rule: Rule) {
        val spinner = findViewById<AppCompatSpinner>(R.id.spinner_trigger_type)
        val input = findViewById<AutoCompleteTextView>(R.id.input_trigger_value)
        spinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                RuleEditorLogic.triggerLabels(),
            )
        spinner.setSelection(RuleEditorLogic.typePosition(rule.trigger))
        input.setText(rule.trigger.let { trigger -> trigger.value.orEmpty() })
        input.hint = RuleEditorLogic.triggerHint(spinner.selectedItemPosition)
        input.setAdapter(
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
                    input.hint = RuleEditorLogic.triggerHint(position)
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
            }
        findViewById<EditText>(R.id.input_trigger_sender).setText(
            (rule.trigger as? dev.niza.autodroid.engine.DataSmsReceivedTrigger)?.sender.orEmpty(),
        )
    }

    private fun setupAction(rule: Rule) {
        val spinner = findViewById<AppCompatSpinner>(R.id.spinner_action_type)
        val value = findViewById<EditText>(R.id.input_action_value)
        val extra = findViewById<EditText>(R.id.input_action_extra)
        spinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                RuleEditorLogic.actionLabels(),
            )
        spinner.setSelection(RuleEditorLogic.actionTypePosition(rule.actions.firstOrNull()))
        value.setText(RuleEditorLogic.actionValue(rule.actions.firstOrNull()))
        extra.setText(RuleEditorLogic.actionExtra(rule.actions.firstOrNull()))
        findViewById<EditText>(R.id.input_action_port).setText(
            (rule.actions.firstOrNull() as? SendDataSmsAction)?.destinationPort?.toString().orEmpty(),
        )
        spinner.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long,
                ) {
                    value.hint = RuleEditorLogic.actionHint(position)
                    extra.hint = RuleEditorLogic.actionExtraHint(position)
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
            }
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
                val triggerValue = findViewById<AutoCompleteTextView>(R.id.input_trigger_value).text.toString()
                val triggerError = RuleEditorLogic.validateTrigger(triggerType, triggerValue)
                if (triggerError != null) {
                    Toast.makeText(this, triggerError, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val actionType = findViewById<AppCompatSpinner>(R.id.spinner_action_type).selectedItemPosition
                val actionValue = findViewById<EditText>(R.id.input_action_value).text.toString()
                val actionExtra = findViewById<EditText>(R.id.input_action_extra).text.toString()
                val actionPort = findViewById<EditText>(R.id.input_action_port).text.toString()
                val actionError = RuleEditorLogic.validateAction(actionType, actionValue, actionExtra, actionPort)
                if (actionError != null) {
                    Toast.makeText(this, actionError, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val name =
                    findViewById<EditText>(R.id.input_macro_name).text.toString().ifBlank { "Untitled Macro" }
                val updated =
                    rule.copy(
                        name = name,
                        trigger =
                            RuleEditorLogic.buildTrigger(
                                rule.trigger.id,
                                triggerType,
                                triggerValue,
                                senderInput(),
                            ),
                        actions =
                            listOf(
                                buildAction(
                                    rule.actions.firstOrNull()?.id ?: "action-1",
                                    actionType,
                                    actionValue,
                                    actionExtra,
                                    actionPort,
                                ),
                            ),
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

    private fun senderInput(): String? = findViewById<EditText>(R.id.input_trigger_sender).text.toString().trim().ifBlank { null }

    private fun renderConstraints() {
        findViewById<ListView>(R.id.list_constraints).adapter =
            ArrayAdapter(this, R.layout.item_constraint, constraints.map(ConstraintEditorLogic::label))
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

    private fun buildAction(
        id: String,
        typePosition: Int,
        value: String,
        extra: String,
        port: String,
    ): Action =
        when (typePosition) {
            RuleEditorLogic.ACTION_OPEN_URL -> OpenUrlAction(id, value)
            RuleEditorLogic.ACTION_SEND_DATA_SMS ->
                SendDataSmsAction(
                    id,
                    destinationPort = port.toIntOrNull() ?: 0,
                    sender = value,
                    payload = RuleEditorLogic.parseHex(extra),
                )
            RuleEditorLogic.ACTION_SET_VARIABLE -> dev.niza.autodroid.engine.SetVariableAction(id, value, extra)
            RuleEditorLogic.ACTION_IF_CONDITION -> dev.niza.autodroid.engine.IfConditionAction(id, value)
            RuleEditorLogic.ACTION_ELSE_CONDITION -> dev.niza.autodroid.engine.ElseConditionAction(id)
            RuleEditorLogic.ACTION_END_IF -> dev.niza.autodroid.engine.EndIfAction(id)
            RuleEditorLogic.ACTION_WAIT -> dev.niza.autodroid.engine.WaitAction(id, value.toLongOrNull() ?: 0)
            RuleEditorLogic.ACTION_REPEAT_START -> dev.niza.autodroid.engine.RepeatStartAction(id, value.toIntOrNull() ?: 1)
            RuleEditorLogic.ACTION_REPEAT_END -> dev.niza.autodroid.engine.RepeatEndAction(id)
            else -> NotifyAction(id, title = value, body = extra)
        }

    companion object {
        const val EXTRA_RULE_ID = "extra_rule_id"
    }
}
