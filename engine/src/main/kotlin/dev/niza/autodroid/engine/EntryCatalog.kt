package dev.niza.autodroid.engine

/** Declarative description of a trigger entry point, used by the editor to render the right fields. */
data class TriggerDescriptor(
    val key: String,
    val label: String,
    val category: Category,
    val fields: List<Field> = emptyList(),
) {
    enum class Category {
        DEVICE,
        CONNECTIVITY,
        SENSOR,
        COMMUNICATION,
        TIME_LOCATION,
        INTERACTION,
        SYSTEM,
        APPLICATION,
    }

    enum class Field {
        TEXT,
        BOOLEAN,
        NUMBER,
        PHONE,
        PACKAGE,
        CHOICE_ABOVE_BELOW,
        CHOICE_ENTER_EXIT,
        CHOICE_STREAM,
        CHOICE_RINGER,
        CHOICE_ORIENTATION,
        CHOICE_MEDIA_COMMAND,
        CHOICE_SETTING_TYPE,
        TIME_RANGE,
        DAY_LIST,
        LAT_LON,
        HEX,
    }
}

/** Declarative description of an action, mirroring [TriggerDescriptor]. */
data class ActionDescriptor(
    val key: String,
    val label: String,
    val category: TriggerDescriptor.Category,
    val fields: List<TriggerDescriptor.Field> = emptyList(),
)

/** One row of the editor's entry picker: a trigger/action template plus how to build and read it. */
data class EntrySpec(
    val key: String,
    val label: String,
    val hint: String,
    val fields: List<TriggerDescriptor.Field> = emptyList(),
    val build: (EntryInput) -> Any,
)

/** Raw editor text, resolved into typed values before construction. */
data class EntryInput(
    val id: String = "",
    val first: String = "",
    val second: String = "",
    val third: String = "",
)

internal fun EntryInput.bool(): Boolean = first.toBooleanStrictOrNull() ?: false

internal fun EntryInput.int(): Int = first.toIntOrNull() ?: 0

internal fun EntryInput.long(): Long = first.toLongOrNull() ?: 0

internal fun EntryInput.number(): Int = first.toIntOrNull() ?: 0
