package dev.niza.autodroid.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CatalogCoverageTest {
    @Test
    fun `every trigger catalog entry builds a matching trigger`() {
        TriggerCatalog.entries.forEach { spec ->
            val built = buildFromSpec(spec) as Trigger
            val index = TriggerCatalog.indexOfTrigger(built)
            assertTrue(index >= 0, "${spec.key} produced $built which the index cannot read")
            assertEquals(spec.key, TriggerCatalog.entries[index].key)
        }
    }

    @Test
    fun `every action catalog entry builds a matching action`() {
        ActionCatalog.entries.forEach { spec ->
            val built = buildFromSpec(spec) as Action
            val index = ActionCatalog.indexOfAction(built)
            assertTrue(index >= 0, "${spec.key} produced $built which the index cannot read")
            assertEquals(spec.key, ActionCatalog.entries[index].key)
        }
    }

    @Test
    fun `catalog labels and hints are present`() {
        TriggerCatalog.entries.forEach { spec ->
            assertTrue(spec.label.isNotBlank(), "${spec.key} has no label")
        }
        ActionCatalog.entries.forEach { spec ->
            assertTrue(spec.label.isNotBlank(), "${spec.key} has no label")
        }
    }

    @Test
    fun `catalog keys are unique`() {
        assertEquals(TriggerCatalog.entries.size, TriggerCatalog.entries.map { it.key }.toSet().size)
        assertEquals(ActionCatalog.entries.size, ActionCatalog.entries.map { it.key }.toSet().size)
    }

    private fun buildFromSpec(spec: EntrySpec): Any {
        val input =
            EntryInput(
                id = "id-$spec.key",
                first = sampleFor(spec, 0),
                second = sampleFor(spec, 1),
                third = sampleFor(spec, 2),
            )
        return spec.build(input)
    }

    private fun sampleFor(
        spec: EntrySpec,
        position: Int,
    ): String {
        val field = spec.fields.getOrNull(position) ?: return ""
        return when (field) {
            TriggerDescriptor.Field.TEXT -> if (position == 2) "payload" else "value"
            TriggerDescriptor.Field.BOOLEAN -> "true"
            TriggerDescriptor.Field.NUMBER -> "12"
            TriggerDescriptor.Field.PHONE -> "+100"
            TriggerDescriptor.Field.PACKAGE -> "com.example.app"
            TriggerDescriptor.Field.CHOICE_ABOVE_BELOW -> if (position == 1) "above" else "40"
            TriggerDescriptor.Field.CHOICE_ENTER_EXIT ->
                if (position == 2) {
                    "true"
                } else if (position == 1) {
                    "200"
                } else {
                    "home"
                }
            TriggerDescriptor.Field.CHOICE_STREAM -> if (position == 0) "media" else "6"
            TriggerDescriptor.Field.CHOICE_RINGER -> "normal"
            TriggerDescriptor.Field.CHOICE_ORIENTATION -> "portrait"
            TriggerDescriptor.Field.CHOICE_MEDIA_COMMAND -> "play"
            TriggerDescriptor.Field.CHOICE_SETTING_TYPE -> "system"
            TriggerDescriptor.Field.TIME_RANGE -> "08:00-18:00"
            TriggerDescriptor.Field.DAY_LIST -> "1,2,3"
            TriggerDescriptor.Field.LAT_LON ->
                if (position == 0) {
                    "31.23"
                } else if (position == 1) {
                    "121.47"
                } else {
                    "500"
                }
            TriggerDescriptor.Field.HEX -> "0102"
        }
    }
}
