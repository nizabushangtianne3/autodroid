package dev.niza.autodroid

import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import dev.niza.autodroid.engine.LinkOpenedTrigger
import dev.niza.autodroid.engine.NotifyAction
import dev.niza.autodroid.engine.Rule
import dev.niza.autodroid.engine.summary
import dev.niza.autodroid.engine.value
import dev.niza.autodroid.ui.RuleEditor
import dev.niza.autodroid.ui.RuleEditorLogic

fun MainActivity.setupViews() {
    listView = findViewById(R.id.list_rules)
    val logView = findViewById<ListView>(R.id.list_logs)
    val fab = findViewById<FloatingActionButton>(R.id.fab_add)
    val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
    toolbar.subtitle = getString(R.string.toolbar_subtitle)
    setupFilterSpinners()
    setupLogTab(logView)
    setupImportExportMenu(listView)
    render(listView)

    fab.setOnClickListener { createRule(listView) }
    listView.setOnItemClickListener { _, _, position, _ ->
        RuleEditor(this).editRule(visibleRules()[position], listView)
    }
    listView.setOnItemLongClickListener { _, _, position, _ ->
        Runtime.engine.remove(visibleRules()[position].id)
        Runtime.saveRules(this)
        DataSmsRegistrar.refresh(this)
        ScheduleRegistrar.refresh(this)
        render(listView)
        Toast.makeText(this, R.string.delete_confirm, Toast.LENGTH_SHORT).show()
        true
    }
}

private fun MainActivity.setupFilterSpinners() {
    val triggerSpinner = findViewById<android.widget.Spinner>(R.id.spinner_trigger_filter)
    val actionSpinner = findViewById<android.widget.Spinner>(R.id.spinner_action_filter)
    triggerSpinner.adapter =
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf(
                "全部触发器", "打开链接", "应用启动", "数据短信", "定时器", "屏幕开关", "电量", "飞行模式", "蓝牙", "充电",
                "耳机", "热点", "静音", "WiFi", "WiFi名称", "信号", "底座", "VPN", "GPS", "解锁", "剪贴板",
                "深色模式", "媒体按键", "通知",
            ),
        )
    actionSpinner.adapter =
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("全部动作", "显示通知", "打开网址", "发送数据短信"),
        )
    triggerSpinner.onItemSelectedListener =
        simpleSpinnerListener {
            triggerFilter = it
            render(listView)
        }
    actionSpinner.onItemSelectedListener =
        simpleSpinnerListener {
            actionFilter = it
            render(listView)
        }
}

private fun MainActivity.simpleSpinnerListener(onSelect: (Int) -> Unit) =
    object : android.widget.AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: android.widget.AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long,
        ) {
            onSelect(position)
        }

        override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
    }

private fun MainActivity.setupLogTab(logView: ListView) {
    val tabs = findViewById<TabLayout>(R.id.tab_layout)
    val fab = findViewById<FloatingActionButton>(R.id.fab_add)
    tabs.addTab(tabs.newTab().setText(R.string.tab_rules))
    tabs.addTab(tabs.newTab().setText(R.string.tab_logs))
    tabs.addOnTabSelectedListener(
        object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val showRules = tab.position == 0
                findViewById<View>(R.id.rules_page).visibility = if (showRules) View.VISIBLE else View.GONE
                findViewById<View>(R.id.logs_page).visibility = if (showRules) View.GONE else View.VISIBLE
                fab.visibility = if (showRules) View.VISIBLE else View.GONE
                if (!showRules) renderLog(logView)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) = Unit

            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        },
    )
    logView.setOnItemClickListener { _, _, position, _ ->
        val entry = Runtime.engine.executionLog().reversed().getOrNull(position) ?: return@setOnItemClickListener
        showLogDetail(entry)
    }
}

private fun MainActivity.setupImportExportMenu(listView: ListView) {
    val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
    toolbar.inflateMenu(R.menu.menu_main)
    toolbar.setOnMenuItemClickListener { item ->
        when (item.itemId) {
            R.id.menu_export -> {
                exportRules()
                true
            }
            R.id.menu_import -> {
                importRules(listView)
                true
            }
            else -> false
        }
    }
}

private fun MainActivity.createRule(listView: ListView) {
    val existingIds = Runtime.engine.rules().map { it.id }.toSet()
    val sequence = generateSequence(1) { it + 1 }.first { "sample-$it" !in existingIds }
    val rule =
        Rule(
            id = "sample-$sequence",
            name = "规则 $sequence",
            trigger = LinkOpenedTrigger("trigger-$sequence", host = ""),
            actions = listOf(NotifyAction("action-$sequence", "AutoDroid", "触发成功")),
        )
    RuleEditor(this).editRule(rule, listView)
}

fun MainActivity.render(listView: ListView) {
    val rules = visibleRules()
    listView.adapter = RuleAdapter(rules)
    findViewById<View>(R.id.rules_empty).visibility = if (rules.isEmpty()) View.VISIBLE else View.GONE
    listView.visibility = if (rules.isEmpty()) View.GONE else View.VISIBLE
}

fun MainActivity.visibleRules(): List<Rule> =
    Runtime.engine.rules().filter { rule ->
        RuleEditorLogic.ruleMatchesTriggerFilter(rule.trigger, triggerFilter) &&
            RuleEditorLogic.ruleMatchesActionFilter(rule.actions.firstOrNull(), actionFilter)
    }

private fun MainActivity.renderLog(logView: ListView) {
    val entries = Runtime.engine.executionLog().reversed()
    logView.adapter =
        ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            entries.map { entry ->
                val format = java.text.SimpleDateFormat("MM-dd HH:mm:ss", java.util.Locale.getDefault())
                val time = format.format(java.util.Date(entry.timestamp))
                val status = if (entry.report.success) "成功" else "失败"
                "$time • $status • 匹配 ${entry.report.matchedRuleCount} 个宏 • 执行 ${entry.report.actionResults.size} 个动作"
            },
        )
    findViewById<View>(R.id.logs_empty).visibility = if (entries.isEmpty()) View.VISIBLE else View.GONE
    logView.visibility = if (entries.isEmpty()) View.GONE else View.VISIBLE
}

private fun MainActivity.showLogDetail(entry: dev.niza.autodroid.engine.ExecutionLogEntry) {
    val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
    val time = format.format(java.util.Date(entry.timestamp))
    val details =
        buildString {
            append("匹配宏：${entry.report.matchedRuleCount}")
            append('\n')
            append("执行结果：${if (entry.report.success) "成功" else "失败"}")
            entry.report.actionResults.forEach { result ->
                append('\n')
                append("${result.actionId}：${if (result.success) "成功" else "失败"}")
                result.message?.let { append(" • $it") }
            }
        }
    AlertDialog
        .Builder(this)
        .setTitle(time)
        .setMessage(details)
        .setPositiveButton("关闭", null)
        .show()
}

private fun MainActivity.exportRules() {
    exportRules.launch("autodroid-rules.json")
}

private fun MainActivity.importRules(listView: ListView) {
    importRules.launch(arrayOf("application/json"))
}

private class RuleAdapter(
    private val rules: List<Rule>,
) : BaseAdapter() {
    override fun getCount() = rules.size

    override fun getItem(position: Int) = rules[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
    ): View {
        val context = parent.context
        val row =
            convertView as? android.widget.LinearLayout ?: android.widget.LinearLayout(context).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                val density = resources.displayMetrics.density
                setPadding(
                    (16 * density).toInt(),
                    (14 * density).toInt(),
                    (16 * density).toInt(),
                    (14 * density).toInt(),
                )
            }
        val rule = getItem(position)
        val title =
            row.findViewWithTag<TextView>("title") ?: TextView(context).apply {
                tag = "title"
                textSize = 16f
                setTypeface(typeface, Typeface.BOLD)
                row.addView(this)
            }
        val detail =
            row.findViewWithTag<TextView>("detail") ?: TextView(context).apply {
                tag = "detail"
                textSize = 13f
                setTextColor(0xFF606060.toInt())
                row.addView(this)
            }
        val state =
            row.findViewWithTag<TextView>("state") ?: TextView(context).apply {
                tag = "state"
                textSize = 12f
                row.addView(this)
            }
        val trigger = RuleEditorLogic.triggerLabel(rule.trigger)
        val action = RuleEditorLogic.actionLabel(rule.actions.firstOrNull())
        val triggerValue = rule.trigger.value.orEmpty()
        val actionValue = rule.actions.firstOrNull()?.summary.orEmpty()
        title.text = rule.name
        detail.text = "触发器：$trigger $triggerValue\n动作：$action $actionValue"
        state.text = if (rule.enabled) "已启用" else "已停用"
        state.setTextColor(if (rule.enabled) 0xFF176B4C.toInt() else 0xFF9A3B3B.toInt())
        return row
    }
}
