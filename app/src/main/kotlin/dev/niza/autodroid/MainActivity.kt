package dev.niza.autodroid

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dev.niza.autodroid.engine.AppLaunchedTrigger
import dev.niza.autodroid.engine.DataSmsReceivedTrigger
import dev.niza.autodroid.engine.LinkOpenedTrigger
import dev.niza.autodroid.engine.NotifyAction
import dev.niza.autodroid.engine.OpenUrlAction
import dev.niza.autodroid.engine.Rule
import dev.niza.autodroid.engine.RuleStore
import dev.niza.autodroid.engine.SendDataSmsAction
import dev.niza.autodroid.engine.summary
import dev.niza.autodroid.engine.value
import dev.niza.autodroid.ui.RuleEditor

class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private val exportRules =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            uri ?: return@registerForActivityResult
            contentResolver.openOutputStream(uri)?.use { output ->
                output.write(RuleStore().encode(Runtime.engine.rules()).toByteArray())
            }
            Toast.makeText(this, "Rules exported", Toast.LENGTH_SHORT).show()
        }
    private val importRules =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri ?: return@registerForActivityResult
            val payload =
                contentResolver.openInputStream(uri)?.use {
                    it.bufferedReader().readText()
                } ?: return@registerForActivityResult
            val imported = RuleStore().decode(payload)
            if (imported.isEmpty()) {
                Toast.makeText(this, "Invalid rules file", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }
            mergeImportedRules(imported)
            Runtime.saveRules(this)
            DataSmsRegistrar.refresh(this)
            render(listView)
            Toast.makeText(this, "Rules imported", Toast.LENGTH_SHORT).show()
        }

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
            RuleEditor(this).editRule(Runtime.engine.rules()[position], listView)
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            Runtime.engine.remove(Runtime.engine.rules()[position].id)
            Runtime.saveRules(this)
            DataSmsRegistrar.refresh(this)
            render(listView)
            true
        }

        showImportExportMenu(listView)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (this::listView.isInitialized) handleNotificationTarget(intent, listView)
    }

    private fun handleNotificationTarget(
        intent: Intent?,
        listView: ListView,
    ) {
        val ruleId = intent?.getStringExtra(Runtime.EXTRA_RULE_ID) ?: return
        intent.removeExtra(Runtime.EXTRA_RULE_ID)
        val rule = Runtime.engine.rules().firstOrNull { it.id == ruleId } ?: return
        val position = Runtime.engine.rules().indexOfFirst { it.id == ruleId }
        if (position >= 0) {
            listView.smoothScrollToPosition(position)
            listView.setItemChecked(position, true)
        }
        RuleEditor(this).editRule(rule, listView)
    }

    private fun setupAddButton(listView: ListView) {
        val container =
            LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
            }
        val addButton =
            com.google.android.material.floatingactionbutton.FloatingActionButton(this).apply {
                setImageResource(android.R.drawable.ic_input_add)
                setOnClickListener { createRule(listView) }
            }
        val params =
            LinearLayout.LayoutParams(
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
        val rule =
            Rule(
                id = "sample-$sequence",
                name = "Sample rule $sequence",
                trigger = LinkOpenedTrigger("trigger-$sequence", host = ""),
                actions = listOf(NotifyAction("action-$sequence", "AutoDroid", "Link matched")),
            )
        RuleEditor(this).editRule(rule, listView)
    }

    private fun showImportExportMenu(listView: ListView) {
        val container = listView.parent as LinearLayout
        val exportButton =
            com.google.android.material.floatingactionbutton.FloatingActionButton(this).apply {
                setImageResource(android.R.drawable.ic_menu_save)
                setOnClickListener { exportRules() }
            }
        val importButton =
            com.google.android.material.floatingactionbutton.FloatingActionButton(this).apply {
                setImageResource(android.R.drawable.ic_menu_upload)
                setOnClickListener { importRules(listView) }
            }
        val row =
            LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                addView(importButton)
                addView(exportButton)
            }
        container.addView(row)
    }

    private fun exportRules() {
        exportRules.launch("autodroid-rules.json")
    }

    private fun importRules(listView: ListView) {
        importRules.launch(arrayOf("application/json"))
    }

    private fun mergeImportedRules(imported: List<Rule>) {
        val current = Runtime.engine.rules().associateBy { it.id }.toMutableMap()
        imported.forEach { current[it.id] = it }
        Runtime.loadRules(current.values.toList())
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

    fun render(listView: ListView) {
        val rules = Runtime.engine.rules()
        listView.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                rules.map { rule ->
                    val kind =
                        when (rule.trigger) {
                            is AppLaunchedTrigger -> "App"
                            is DataSmsReceivedTrigger -> "SMS"
                            else -> "Link"
                        }
                    val actionKind =
                        when (rule.actions.firstOrNull()) {
                            is OpenUrlAction -> "Open URL"
                            is SendDataSmsAction -> "Send SMS"
                            else -> "Notify"
                        }
                    val actionSummary = rule.actions.firstOrNull()?.summary.orEmpty()
                    val label = "$rule.name • $kind • ${rule.trigger.value.orEmpty()} • $actionKind • $actionSummary"
                    if (rule.enabled) label else "$label • disabled"
                },
            )
        listView.choiceMode = ListView.CHOICE_MODE_NONE
    }
}
