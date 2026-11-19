package dev.niza.autodroid.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RuleEditorLogicTest {
    @Test
    fun `hints change by trigger type`() {
        assertEquals("Link host", RuleEditorLogic.triggerHint(RuleEditorLogic.TYPE_LINK))
        assertEquals("App package name", RuleEditorLogic.triggerHint(RuleEditorLogic.TYPE_APP))
        assertEquals("Data SMS destination port", RuleEditorLogic.triggerHint(RuleEditorLogic.TYPE_DATA_SMS))
    }

    @Test
    fun `trigger validation covers all types`() {
        assertEquals("Link host is required", RuleEditorLogic.validateTrigger(RuleEditorLogic.TYPE_LINK, " "))
        assertNull(RuleEditorLogic.validateTrigger(RuleEditorLogic.TYPE_LINK, "example.com"))
        assertEquals(
            "Invalid Android package name",
            RuleEditorLogic.validateTrigger(RuleEditorLogic.TYPE_APP, "example"),
        )
        assertNull(RuleEditorLogic.validateTrigger(RuleEditorLogic.TYPE_APP, "com.example.app"))
        assertEquals(
            "Data SMS port must be 1-65535",
            RuleEditorLogic.validateTrigger(RuleEditorLogic.TYPE_DATA_SMS, "0"),
        )
        assertNull(RuleEditorLogic.validateTrigger(RuleEditorLogic.TYPE_DATA_SMS, "65535"))
    }

    @Test
    fun `action validation covers all types`() {
        assertEquals(
            "Notification title is required",
            RuleEditorLogic.validateAction(RuleEditorLogic.ACTION_NOTIFY, "", "body", ""),
        )
        assertEquals(
            "Notification body is required",
            RuleEditorLogic.validateAction(RuleEditorLogic.ACTION_NOTIFY, "title", " ", ""),
        )
        assertEquals(
            "URL is required",
            RuleEditorLogic.validateAction(RuleEditorLogic.ACTION_OPEN_URL, "", "", ""),
        )
        assertEquals(
            "Destination phone number is required",
            RuleEditorLogic.validateAction(RuleEditorLogic.ACTION_SEND_DATA_SMS, "", "0102", "1"),
        )
        assertEquals(
            "Action port must be 1-65535",
            RuleEditorLogic.validateAction(RuleEditorLogic.ACTION_SEND_DATA_SMS, "+100", "0102", "0"),
        )
        assertEquals(
            "Payload hex is required",
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
