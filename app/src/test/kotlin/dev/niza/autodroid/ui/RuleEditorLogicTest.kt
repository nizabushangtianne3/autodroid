package dev.niza.autodroid.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RuleEditorLogicTest {
    @Test
    fun `hints change by trigger type`() {
        assertEquals("链接主机，例如 example.com", RuleEditorLogic.triggerHint(RuleEditorLogic.TYPE_LINK))
        assertEquals("应用包名", RuleEditorLogic.triggerHint(RuleEditorLogic.TYPE_APP))
        assertEquals("数据短信目标端口", RuleEditorLogic.triggerHint(RuleEditorLogic.TYPE_DATA_SMS))
    }

    @Test
    fun `trigger validation covers all types`() {
        assertEquals("请输入链接主机", RuleEditorLogic.validateTrigger(RuleEditorLogic.TYPE_LINK, " "))
        assertNull(RuleEditorLogic.validateTrigger(RuleEditorLogic.TYPE_LINK, "example.com"))
        assertEquals(
            "请输入有效 Android 包名",
            RuleEditorLogic.validateTrigger(RuleEditorLogic.TYPE_APP, "example"),
        )
        assertNull(RuleEditorLogic.validateTrigger(RuleEditorLogic.TYPE_APP, "com.example.app"))
        assertEquals(
            "数据短信端口必须是 1-65535",
            RuleEditorLogic.validateTrigger(RuleEditorLogic.TYPE_DATA_SMS, "0"),
        )
        assertNull(RuleEditorLogic.validateTrigger(RuleEditorLogic.TYPE_DATA_SMS, "65535"))
    }

    @Test
    fun `action validation covers all types`() {
        assertEquals(
            "请输入通知标题",
            RuleEditorLogic.validateAction(RuleEditorLogic.ACTION_NOTIFY, "", "body", ""),
        )
        assertEquals(
            "请输入通知内容",
            RuleEditorLogic.validateAction(RuleEditorLogic.ACTION_NOTIFY, "title", " ", ""),
        )
        assertEquals(
            "请输入网址",
            RuleEditorLogic.validateAction(RuleEditorLogic.ACTION_OPEN_URL, "", "", ""),
        )
        assertEquals(
            "请输入手机号码",
            RuleEditorLogic.validateAction(RuleEditorLogic.ACTION_SEND_DATA_SMS, "", "0102", "1"),
        )
        assertEquals(
            "动作端口必须是 1-65535",
            RuleEditorLogic.validateAction(RuleEditorLogic.ACTION_SEND_DATA_SMS, "+100", "0102", "0"),
        )
        assertEquals(
            "请输入有效 HEX 数据",
            RuleEditorLogic.validateAction(RuleEditorLogic.ACTION_SEND_DATA_SMS, "+100", "XYZ", "1"),
        )
        assertNull(RuleEditorLogic.validateAction(RuleEditorLogic.ACTION_SEND_DATA_SMS, "+100", "0102", "1"))
    }

    @Test
    fun `parse hex accepts prefixes spaces and rejects odd input`() {
        assertEquals("01F1", RuleEditorLogic.parseHex("01 F1").joinToString("") { "%02X".format(it) })
        assertEquals("01F1", RuleEditorLogic.parseHex("0x01F1").joinToString("") { "%02X".format(it) })
        assertEquals(0, RuleEditorLogic.parseHex("0").size)
    }

    @Test
    fun `trigger filter covers schedule and device state`() {
        val link = dev.niza.autodroid.engine.LinkOpenedTrigger("t1", host = "a.com")
        val schedule = dev.niza.autodroid.engine.ScheduleTrigger("t2", intervalMillis = 5000)
        assertEquals(true, RuleEditorLogic.ruleMatchesTriggerFilter(schedule, 0))
        assertEquals(true, RuleEditorLogic.ruleMatchesTriggerFilter(schedule, 3))
        assertEquals(false, RuleEditorLogic.ruleMatchesTriggerFilter(schedule, 1))
        assertEquals(false, RuleEditorLogic.ruleMatchesTriggerFilter(link, 3))
    }

    @Test
    fun `new triggers and actions are editable and labeled`() {
        assertEquals("installed / removed", RuleEditorLogic.triggerHint(RuleEditorLogic.TYPE_APP_LIFECYCLE))
        assertEquals("省电模式开启 true / false", RuleEditorLogic.triggerHint(RuleEditorLogic.TYPE_POWER_SAVE_MODE))

        val lifecycleTrigger =
            RuleEditorLogic.buildTrigger("t1", RuleEditorLogic.TYPE_APP_LIFECYCLE, "installed", null)
        val powerSaveTrigger =
            RuleEditorLogic.buildTrigger("t2", RuleEditorLogic.TYPE_POWER_SAVE_MODE, "false", null)

        assertEquals(RuleEditorLogic.TYPE_APP_LIFECYCLE, RuleEditorLogic.typePosition(lifecycleTrigger))
        assertEquals(RuleEditorLogic.TYPE_POWER_SAVE_MODE, RuleEditorLogic.typePosition(powerSaveTrigger))
        assertEquals("应用安装", RuleEditorLogic.triggerLabel(lifecycleTrigger))
        assertEquals("省电模式", RuleEditorLogic.triggerLabel(powerSaveTrigger))

        val launchApp =
            dev.niza.autodroid.engine.LaunchAppAction("a1", "com.example.app")
        val clipboard = dev.niza.autodroid.engine.ClipboardAction("a2", "hello")

        assertEquals(RuleEditorLogic.ACTION_LAUNCH_APP, RuleEditorLogic.actionTypePosition(launchApp))
        assertEquals(RuleEditorLogic.ACTION_CLIPBOARD, RuleEditorLogic.actionTypePosition(clipboard))
        assertEquals("com.example.app", RuleEditorLogic.actionValue(launchApp))
        assertEquals("hello", RuleEditorLogic.actionValue(clipboard))
        assertEquals("启动应用", RuleEditorLogic.actionLabel(launchApp))
        assertEquals("复制到剪贴板", RuleEditorLogic.actionLabel(clipboard))
        assertNull(RuleEditorLogic.validateAction(RuleEditorLogic.ACTION_LAUNCH_APP, "com.example.app", "", ""))
        assertNull(RuleEditorLogic.validateAction(RuleEditorLogic.ACTION_CLIPBOARD, "hello", "", ""))
    }
}
