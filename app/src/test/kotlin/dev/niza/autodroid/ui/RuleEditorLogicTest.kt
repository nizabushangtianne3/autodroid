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
}
