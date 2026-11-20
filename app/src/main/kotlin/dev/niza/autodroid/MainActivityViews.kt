package dev.niza.autodroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.niza.autodroid.engine.LinkOpenedTrigger
import dev.niza.autodroid.engine.NotifyAction
import dev.niza.autodroid.engine.Rule
import dev.niza.autodroid.ui.RuleEditorActivity

fun MainActivity.setupViews() {
    val macroList = findViewById<ListView>(R.id.list_rules)
    listView = macroList
    setupHomeTiles(macroList)
    setupNavigation()
    setupMacroFilters(macroList)
    setupImportExportMenu(macroList)
    render(listView)

    findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {
        createRule(macroList)
    }

    macroList.setOnItemClickListener { _, _, position, _ ->
        openEditor(visibleRules()[position].id)
    }
    macroList.setOnItemLongClickListener { _, _, position, _ ->
        Runtime.engine.remove(visibleRules()[position].id)
        saveAndRefresh(macroList)
        Toast.makeText(this, R.string.delete_confirm, Toast.LENGTH_SHORT).show()
        true
    }
}

private fun MainActivity.setupHomeTiles(macroList: ListView) {
    val tileActions =
        mapOf(
            R.id.tile_macros to { showPage(R.id.macros_page, getString(R.string.rules_title)) },
            R.id.tile_add_macro to { createRule(macroList) },
            R.id.tile_action_blocks to { showToast(getString(R.string.tile_action_blocks)) },
            R.id.tile_stopwatches to { showToast(getString(R.string.tile_stopwatches)) },
            R.id.tile_system_log to { showLogDialog() },
            R.id.tile_videos to { showToast(getString(R.string.tile_videos)) },
            R.id.tile_last_macro to { openLastRule(macroList) },
            R.id.tile_favourites to { showToast(getString(R.string.tile_favourites)) },
            R.id.tile_geofences to { showToast(getString(R.string.tile_geofences)) },
            R.id.tile_cell_towers to { showToast(getString(R.string.tile_cell_towers)) },
            R.id.tile_plugins to { showToast(getString(R.string.tile_plugins)) },
            R.id.tile_quick_run to { showToast(getString(R.string.tile_quick_run)) },
        )
    tileActions.forEach { (tileId, action) ->
        findViewById<View>(tileId).setOnClickListener { action() }
    }
}

private fun MainActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

private fun MainActivity.openLastRule(macroList: ListView) {
    val rule = Runtime.engine.rules().lastOrNull()
    if (rule == null) {
        createRule(macroList)
    } else {
        openEditor(rule.id)
    }
}

private fun MainActivity.setupNavigation() {
    val navigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
    navigation.selectedItemId = R.id.nav_home
    navigation.setOnItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> showPage(R.id.home_page, getString(R.string.nav_home))
            R.id.nav_macros -> showPage(R.id.macros_page, getString(R.string.rules_title))
            R.id.nav_templates -> showPage(R.id.templates_page, getString(R.string.nav_templates))
            else -> showPage(R.id.settings_page, getString(R.string.nav_settings))
        }
        true
    }
}

private fun MainActivity.showPage(
    pageId: Int,
    title: String,
) {
    listOf(R.id.home_page, R.id.macros_page, R.id.templates_page, R.id.settings_page).forEach {
        findViewById<View>(it).visibility = if (it == pageId) View.VISIBLE else View.GONE
    }
    findViewById<MaterialToolbar>(R.id.toolbar).title = title
    findViewById<FloatingActionButton>(R.id.fab_add).visibility =
        if (pageId == R.id.macros_page) View.VISIBLE else View.GONE
    if (pageId == R.id.settings_page) renderSettings()
}

private fun MainActivity.setupMacroFilters(listView: ListView) {
    val filters = listOf(R.id.filter_all, R.id.filter_enabled, R.id.filter_disabled)
    filters.forEachIndexed { index, id ->
        findViewById<AppCompatCheckedTextView>(id).setOnClickListener {
            filters.forEachIndexed { filterIndex, filterId ->
                findViewById<AppCompatCheckedTextView>(filterId).isChecked = filterIndex == index
            }
            render(listView)
        }
    }
}

private fun MainActivity.setupImportExportMenu(listView: ListView) {
    val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
    toolbar.inflateMenu(R.menu.menu_main)
    toolbar.setOnMenuItemClickListener { item ->
        when (item.itemId) {
            R.id.menu_import -> {
                importRules.launch(arrayOf("application/json"))
                true
            }
            R.id.menu_export -> {
                exportRules.launch("autodroid-rules.json")
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
            name = "Macro $sequence",
            trigger = LinkOpenedTrigger("trigger-$sequence", host = ""),
            actions = listOf(NotifyAction("action-$sequence", "AutoDroid", "Triggered")),
        )
    Runtime.engine.replace(rule)
    Runtime.saveRules(this)
    openEditor(rule.id)
}

internal fun MainActivity.openEditor(ruleId: String) {
    startActivity(android.content.Intent(this, RuleEditorActivity::class.java).putExtra(RuleEditorActivity.EXTRA_RULE_ID, ruleId))
}

fun MainActivity.render(listView: ListView) {
    val rules = visibleRules()
    listView.adapter = MacroAdapter(rules)
    findViewById<TextView>(R.id.macros_count).text =
        getString(R.string.macro_count, Runtime.engine.rules().size)
    findViewById<View>(R.id.rules_empty).visibility = if (rules.isEmpty()) View.VISIBLE else View.GONE
    listView.visibility = if (rules.isEmpty()) View.GONE else View.VISIBLE
}

private fun MainActivity.saveAndRefresh(listView: ListView) {
    Runtime.saveRules(this)
    DataSmsRegistrar.refresh(this)
    ScheduleRegistrar.refresh(this)
    render(listView)
}

fun MainActivity.visibleRules(): List<Rule> =
    Runtime.engine.rules().filter { rule ->
        when {
            findViewById<AppCompatCheckedTextView>(R.id.filter_enabled).isChecked -> rule.enabled
            findViewById<AppCompatCheckedTextView>(R.id.filter_disabled).isChecked -> !rule.enabled
            else -> true
        }
    }

private fun MainActivity.showLogDialog() {
    val entries = Runtime.engine.executionLog().reversed()
    val format = java.text.SimpleDateFormat("MM-dd HH:mm:ss", java.util.Locale.getDefault())
    val text =
        if (entries.isEmpty()) {
            getString(R.string.logs_empty)
        } else {
            entries.joinToString("\n") { entry ->
                "${format.format(java.util.Date(entry.timestamp))} • ${if (entry.report.success) "成功" else "失败"}"
            }
        }
    AlertDialog.Builder(this).setTitle(R.string.logs_title).setMessage(text).setPositiveButton("关闭", null).show()
}

private fun MainActivity.renderSettings() {
    val rules = Runtime.engine.rules()
    val triggerTypes =
        rules
            .map { dev.niza.autodroid.ui.RuleEditorLogic.triggerLabel(it.trigger) }
            .distinct()
            .size
    findViewById<TextView>(R.id.settings_status).text =
        "宏 ${rules.size} 个\n启用 ${rules.count { it.enabled }} 个\n触发器类型 $triggerTypes 种"
}

private class MacroAdapter(
    private val rules: List<Rule>,
) : BaseAdapter() {
    private val states = rules.map { it.toUiState() }

    override fun getCount() = states.size

    override fun getItem(position: Int) = rules[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
    ): View {
        val holder =
            (convertView?.tag as? Holder)
                ?: Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_macro, parent, false))
        val state = states[position]
        holder.name.text = state.name
        holder.lastRun.text = state.lastRun
        holder.trigger.text = state.triggerLabel
        holder.action.text = state.actionLabel
        holder.constraint.text = state.constraintLabel
        holder.switch.setOnCheckedChangeListener(null)
        holder.switch.isChecked = state.enabled
        holder.switch.setOnCheckedChangeListener { _, checked ->
            Runtime.engine.replace(getItem(position).copy(enabled = checked))
            Runtime.saveRules(parent.context.applicationContext)
            DataSmsRegistrar.refresh(parent.context.applicationContext)
            ScheduleRegistrar.refresh(parent.context.applicationContext)
        }
        return holder.row
    }

    private class Holder(val row: View) {
        val name = row.findViewById<TextView>(R.id.macro_name)
        val lastRun = row.findViewById<TextView>(R.id.macro_last_run)
        val trigger = row.findViewById<TextView>(R.id.trigger_value)
        val action = row.findViewById<TextView>(R.id.action_value)
        val constraint = row.findViewById<TextView>(R.id.constraint_count)
        val switch = row.findViewById<SwitchCompat>(R.id.macro_switch)
    }
}
