package dev.niza.autodroid

import android.Manifest
import android.text.InputType
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import dev.niza.autodroid.engine.LinkOpenedTrigger
import dev.niza.autodroid.engine.NotifyAction
import dev.niza.autodroid.engine.Rule

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Runtime.initialize(this)
        Runtime.loadRules(this)
        requestNotificationPermission()
        val listView = ListView(this)
        setContentView(listView)
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

    private fun createSampleRule() {
        val sequence = Runtime.engine.rules().size + 1
        Runtime.engine.register(
            Rule(
                id = "sample-$sequence",
                name = "Sample rule $sequence",
                trigger = LinkOpenedTrigger("trigger-$sequence", host = "example.com"),
                actions = listOf(NotifyAction("action-$sequence", "AutoDroid", "Link matched")),
            ),
        )
    }

    private fun editRule(rule: Rule, listView: ListView) {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT
            setText(rule.name)
        }
        AlertDialog.Builder(this)
            .setTitle("Edit rule")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                Runtime.engine.replace(rule.copy(name = input.text.toString()))
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

    private companion object {
        private const val REQUEST_NOTIFICATIONS = 1001
    }

    private fun render(listView: ListView) {
        val rules = Runtime.engine.rules()
        listView.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            rules.map { "${it.name} • ${it.trigger::class.simpleName}" },
        )
    }
}
