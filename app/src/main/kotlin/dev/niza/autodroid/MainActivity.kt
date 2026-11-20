package dev.niza.autodroid

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dev.niza.autodroid.engine.Rule
import dev.niza.autodroid.engine.RuleStore

class MainActivity : AppCompatActivity() {
    internal lateinit var listView: ListView
    internal var triggerFilter = 0
    internal var actionFilter = 0
    internal val exportRules =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            uri ?: return@registerForActivityResult
            contentResolver.openOutputStream(uri)?.use { output ->
                output.write(RuleStore().encode(Runtime.engine.rules()).toByteArray())
            }
            Toast.makeText(this, "宏已导出", Toast.LENGTH_SHORT).show()
        }
    internal val importRules =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri ?: return@registerForActivityResult
            val payload =
                contentResolver.openInputStream(uri)?.use {
                    it.bufferedReader().readText()
                } ?: return@registerForActivityResult
            val imported = RuleStore().decode(payload)
            if (imported.isEmpty()) {
                Toast.makeText(this, "宏文件无效", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }
            mergeImportedRules(imported)
            Runtime.saveRules(this)
            DataSmsRegistrar.refresh(this)
            render(listView)
            Toast.makeText(this, "宏已导入", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Runtime.initialize(this)
        Runtime.loadRules(this)
        DataSmsRegistrar.refresh(this)
        DeviceStateReceiver.refresh(this)
        requestNotificationPermission()
        requestSmsPermission()
        setContentView(R.layout.activity_main)
        setupViews()
        ScheduleRegistrar.refresh(this)
        handleNotificationTarget(intent)
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val ruleId = intent.getStringExtra(Runtime.EXTRA_RULE_ID) ?: return
        intent.removeExtra(Runtime.EXTRA_RULE_ID)
        if (this::listView.isInitialized) openEditor(ruleId)
    }

    private fun handleNotificationTarget(intent: Intent?) {
        val ruleId = intent?.getStringExtra(Runtime.EXTRA_RULE_ID) ?: return
        intent.removeExtra(Runtime.EXTRA_RULE_ID)
        openEditor(ruleId)
    }
}
