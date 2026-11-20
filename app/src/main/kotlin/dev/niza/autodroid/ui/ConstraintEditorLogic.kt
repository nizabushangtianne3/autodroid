package dev.niza.autodroid.ui

import dev.niza.autodroid.engine.AirplaneModeConstraint
import dev.niza.autodroid.engine.BatteryLevelConstraint
import dev.niza.autodroid.engine.BatterySaverConstraint
import dev.niza.autodroid.engine.BluetoothConstraint
import dev.niza.autodroid.engine.CellConnectionConstraint
import dev.niza.autodroid.engine.Constraint
import dev.niza.autodroid.engine.DayTimeConstraint
import dev.niza.autodroid.engine.DeviceLockedConstraint
import dev.niza.autodroid.engine.ExternalPowerConstraint
import dev.niza.autodroid.engine.GPSEnabledConstraint
import dev.niza.autodroid.engine.HeadphonesConstraint
import dev.niza.autodroid.engine.NetworkTypeConstraint
import dev.niza.autodroid.engine.ScreenOnOffConstraint
import dev.niza.autodroid.engine.SilentModeConstraint
import dev.niza.autodroid.engine.TriggerEnabledConstraint
import dev.niza.autodroid.engine.VariableConstraint
import dev.niza.autodroid.engine.WifiConstraint

object ConstraintEditorLogic {
    const val TYPE_WIFI = 0
    const val TYPE_BATTERY_LEVEL = 1
    const val TYPE_BATTERY_SAVER = 2
    const val TYPE_BLUETOOTH = 3
    const val TYPE_CELL_CONNECTION = 4
    const val TYPE_DAY_TIME = 5
    const val TYPE_DEVICE_LOCKED = 6
    const val TYPE_EXTERNAL_POWER = 7
    const val TYPE_GPS_ENABLED = 8
    const val TYPE_HEADPHONES = 9
    const val TYPE_NETWORK_TYPE = 10
    const val TYPE_SCREEN_ON_OFF = 11
    const val TYPE_SILENT_MODE = 12
    const val TYPE_TRIGGER_ENABLED = 13
    const val TYPE_VARIABLE = 14
    const val TYPE_AIRPLANE_MODE = 15

    val labels =
        listOf(
            "WiFi", "Battery Level", "Battery Saver", "Bluetooth", "Cell Connection",
            "Day / Time", "Device Locked", "External Power", "GPS Enabled", "Headphones",
            "Network Type", "Screen On / Off", "Silent Mode", "Trigger Enabled",
            "Variable", "Airplane Mode",
        )

    fun valueHint(typePosition: Int): String =
        when (typePosition) {
            TYPE_BATTERY_LEVEL -> "电量百分比"
            TYPE_CELL_CONNECTION, TYPE_NETWORK_TYPE -> "连接类型，例如 wifi / none"
            TYPE_DAY_TIME -> "08:00-18:00"
            TYPE_TRIGGER_ENABLED -> "触发器 ID"
            TYPE_VARIABLE -> "条件，例如 {count} > 10"
            else -> "true / false"
        }

    fun extraHint(typePosition: Int): String =
        when (typePosition) {
            TYPE_BATTERY_LEVEL -> "above / below"
            TYPE_DAY_TIME -> "星期 1-7，用逗号分隔"
            else -> "可留空"
        }

    fun validate(
        typePosition: Int,
        value: String,
        extra: String,
    ): String? {
        if (value.isBlank()) return "请输入约束值"
        return when (typePosition) {
            TYPE_BATTERY_LEVEL -> {
                if (value.toIntOrNull() !in 0..100) "电量必须是 0-100" else null
            }
            TYPE_WIFI,
            TYPE_BATTERY_SAVER,
            TYPE_BLUETOOTH,
            TYPE_DEVICE_LOCKED,
            TYPE_EXTERNAL_POWER,
            TYPE_GPS_ENABLED,
            TYPE_HEADPHONES,
            TYPE_SCREEN_ON_OFF,
            TYPE_SILENT_MODE,
            TYPE_AIRPLANE_MODE,
            -> if (value.toBooleanStrictOrNull() == null) "请输入 true 或 false" else null
            TYPE_DAY_TIME -> if (parseTimeRange(value) == null) "格式必须是 HH:mm-HH:mm" else null
            TYPE_VARIABLE -> if (extra.isNotBlank()) "表达式写在值里，补充项留空" else null
            else -> null
        }
    }

    fun build(
        id: String,
        typePosition: Int,
        value: String,
        extra: String,
    ): Constraint =
        when (typePosition) {
            TYPE_WIFI -> WifiConstraint(id, connected = value.toBoolean())
            TYPE_BATTERY_LEVEL ->
                BatteryLevelConstraint(
                    id,
                    level = value.toInt(),
                    direction = extra.ifBlank { "above" },
                )
            TYPE_BATTERY_SAVER -> BatterySaverConstraint(id, enabled = value.toBoolean())
            TYPE_BLUETOOTH -> BluetoothConstraint(id, enabled = value.toBoolean())
            TYPE_CELL_CONNECTION -> CellConnectionConstraint(id, requiredType = value)
            TYPE_DAY_TIME -> {
                val range = parseTimeRange(value)
                DayTimeConstraint(
                    id,
                    startMinuteOfDay = range?.first ?: 0,
                    endMinuteOfDay = range?.second ?: 0,
                    days = parseDays(extra),
                )
            }
            TYPE_DEVICE_LOCKED -> DeviceLockedConstraint(id, locked = value.toBoolean())
            TYPE_EXTERNAL_POWER -> ExternalPowerConstraint(id, connected = value.toBoolean())
            TYPE_GPS_ENABLED -> GPSEnabledConstraint(id, enabled = value.toBoolean())
            TYPE_HEADPHONES -> HeadphonesConstraint(id, connected = value.toBoolean())
            TYPE_NETWORK_TYPE -> NetworkTypeConstraint(id, requiredType = value)
            TYPE_SCREEN_ON_OFF -> ScreenOnOffConstraint(id, screenOn = value.toBoolean())
            TYPE_SILENT_MODE -> SilentModeConstraint(id, enabled = value.toBoolean())
            TYPE_TRIGGER_ENABLED -> TriggerEnabledConstraint(id, triggerId = value)
            TYPE_VARIABLE -> VariableConstraint(id, expression = value)
            else -> AirplaneModeConstraint(id, enabled = value.toBoolean())
        }

    fun typePosition(constraint: Constraint): Int =
        when (constraint) {
            is WifiConstraint -> TYPE_WIFI
            is BatteryLevelConstraint -> TYPE_BATTERY_LEVEL
            is BatterySaverConstraint -> TYPE_BATTERY_SAVER
            is BluetoothConstraint -> TYPE_BLUETOOTH
            is CellConnectionConstraint -> TYPE_CELL_CONNECTION
            is DayTimeConstraint -> TYPE_DAY_TIME
            is DeviceLockedConstraint -> TYPE_DEVICE_LOCKED
            is ExternalPowerConstraint -> TYPE_EXTERNAL_POWER
            is GPSEnabledConstraint -> TYPE_GPS_ENABLED
            is HeadphonesConstraint -> TYPE_HEADPHONES
            is NetworkTypeConstraint -> TYPE_NETWORK_TYPE
            is ScreenOnOffConstraint -> TYPE_SCREEN_ON_OFF
            is SilentModeConstraint -> TYPE_SILENT_MODE
            is TriggerEnabledConstraint -> TYPE_TRIGGER_ENABLED
            is VariableConstraint -> TYPE_VARIABLE
            else -> TYPE_AIRPLANE_MODE
        }

    fun label(constraint: Constraint): String =
        when (constraint) {
            is WifiConstraint -> "WiFi · ${constraint.connected}"
            is BatteryLevelConstraint -> "Battery · ${constraint.direction} ${constraint.level}"
            is BatterySaverConstraint -> "Battery Saver · ${constraint.enabled}"
            is BluetoothConstraint -> "Bluetooth · ${constraint.enabled}"
            is CellConnectionConstraint -> "Cell · ${constraint.requiredType}"
            is DayTimeConstraint -> "Time · ${constraint.startMinuteOfDay}-${constraint.endMinuteOfDay}"
            is DeviceLockedConstraint -> "Locked · ${constraint.locked}"
            is ExternalPowerConstraint -> "Power · ${constraint.connected}"
            is GPSEnabledConstraint -> "GPS · ${constraint.enabled}"
            is HeadphonesConstraint -> "Headphones · ${constraint.connected}"
            is NetworkTypeConstraint -> "Network · ${constraint.requiredType}"
            is ScreenOnOffConstraint -> "Screen · ${constraint.screenOn}"
            is SilentModeConstraint -> "Silent · ${constraint.enabled}"
            is TriggerEnabledConstraint -> "Trigger · ${constraint.triggerId}"
            is VariableConstraint -> "Variable · ${constraint.expression}"
            else -> "Airplane · ${(constraint as AirplaneModeConstraint).enabled}"
        }

    fun parseTimeRange(value: String): Pair<Int, Int>? {
        val parts = value.split("-")
        if (parts.size != 2) return null
        val start = parseMinute(parts[0]) ?: return null
        val end = parseMinute(parts[1]) ?: return null
        return start to end
    }

    private fun parseMinute(value: String): Int? {
        val parts = value.split(":")
        if (parts.size != 2) return null
        val hour = parts[0].toIntOrNull() ?: return null
        val minute = parts[1].toIntOrNull() ?: return null
        if (hour !in 0..23 || minute !in 0..59) return null
        return hour * 60 + minute
    }

    private fun parseDays(value: String): List<Int> {
        val days = value.split(',', ',').mapNotNull { it.trim().toIntOrNull() }.filter { it in 1..7 }
        return days.distinct().ifEmpty { listOf(1, 2, 3, 4, 5, 6, 7) }
    }
}
