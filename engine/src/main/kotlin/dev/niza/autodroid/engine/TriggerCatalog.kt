package dev.niza.autodroid.engine

/**
 * Single source of truth for the editor's trigger picker. Every entry maps to a concrete
 * [Trigger] model, so the UI, serialization and summaries cannot drift apart.
 */
object TriggerCatalog {
    val entries: List<EntrySpec> =
        listOf(
            EntrySpec("empty", "立即触发", "", emptyList()) { EmptyTrigger(it.id) },
            EntrySpec("link", "打开链接", "主机名，例如 example.com", listOf(TriggerDescriptor.Field.TEXT)) {
                LinkOpenedTrigger(it.id, host = it.first.ifBlank { null })
            },
            EntrySpec(
                "app_launched",
                "应用启动",
                "应用包名",
                listOf(TriggerDescriptor.Field.PACKAGE),
            ) { AppLaunchedTrigger(it.id, packageName = it.first) },
            EntrySpec("data_sms", "数据短信", "目标端口", listOf(TriggerDescriptor.Field.NUMBER)) {
                DataSmsReceivedTrigger(it.id, destinationPort = it.first.toIntOrNull() ?: 0, sender = it.second.ifBlank { null })
            },
            EntrySpec("schedule", "定时器", "间隔毫秒", listOf(TriggerDescriptor.Field.NUMBER)) {
                ScheduleTrigger(it.id, intervalMillis = it.first.toLongOrNull() ?: 1000)
            },
            EntrySpec("screen", "屏幕开关", "开启 true / 关闭 false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                ScreenOnOffTrigger(it.id, screenOn = it.bool())
            },
            EntrySpec(
                "battery_level",
                "电量变化",
                "电量百分比",
                listOf(TriggerDescriptor.Field.NUMBER, TriggerDescriptor.Field.CHOICE_ABOVE_BELOW),
            ) {
                BatteryLevelTrigger(it.id, level = it.int(), direction = it.second.ifBlank { "below" })
            },
            EntrySpec("airplane", "飞行模式", "开启 true / 关闭 false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                AirplaneModeTrigger(it.id, enabled = it.bool())
            },
            EntrySpec("bluetooth", "蓝牙", "开启 true / 关闭 false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                BluetoothTrigger(it.id, enabled = it.bool())
            },
            EntrySpec("power", "充电连接", "已连接 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                ExternalPowerTrigger(it.id, connected = it.bool())
            },
            EntrySpec("headphones", "耳机插拔", "已插入 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                HeadphonesTrigger(it.id, connected = it.bool())
            },
            EntrySpec(
                "hotspot",
                "热点",
                "已开启 true / false",
                listOf(TriggerDescriptor.Field.BOOLEAN),
            ) { HotspotTrigger(it.id, enabled = it.bool()) },
            EntrySpec(
                "silent",
                "静音模式",
                "已开启 true / false",
                listOf(TriggerDescriptor.Field.BOOLEAN),
            ) { SilentModeTrigger(it.id, enabled = it.bool()) },
            EntrySpec("wifi", "WiFi 连接", "已连接 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                WifiConnectionTrigger(it.id, connected = it.bool())
            },
            EntrySpec("wifi_ssid", "WiFi 名称", "SSID", listOf(TriggerDescriptor.Field.TEXT)) { WifiSSIDTrigger(it.id, ssid = it.first) },
            EntrySpec("signal", "蜂窝信号", "已开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                SignalOnOffTrigger(it.id, signalOn = it.bool())
            },
            EntrySpec("dock", "底座", "状态文本", listOf(TriggerDescriptor.Field.TEXT)) { DockTrigger(it.id, state = it.first) },
            EntrySpec(
                "vpn",
                "VPN",
                "已连接 true / false",
                listOf(TriggerDescriptor.Field.BOOLEAN),
            ) { VpnTrigger(it.id, connected = it.bool()) },
            EntrySpec(
                "gps",
                "GPS",
                "已开启 true / false",
                listOf(TriggerDescriptor.Field.BOOLEAN),
            ) { GPSEnabledTrigger(it.id, enabled = it.bool()) },
            EntrySpec("unlocked", "设备解锁", "", emptyList()) { DeviceUnlockedTrigger(it.id) },
            EntrySpec("clipboard", "剪贴板变化", "", emptyList()) { ClipboardChangeTrigger(it.id) },
            EntrySpec("dark_theme", "深色模式", "已开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                DarkThemeTrigger(it.id, enabled = it.bool())
            },
            EntrySpec("media_button", "媒体按键", "按键名", listOf(TriggerDescriptor.Field.TEXT)) { MediaButtonTrigger(it.id, button = it.first) },
            EntrySpec("notification", "通知", "应用包名，可留空", listOf(TriggerDescriptor.Field.PACKAGE)) {
                NotificationTrigger(it.id, packageName = it.first.ifBlank { null })
            },
            EntrySpec("app_installed", "应用安装", "installed / removed", listOf(TriggerDescriptor.Field.TEXT)) {
                AppLifecycleTrigger(it.id, added = it.first.equals("installed", ignoreCase = true))
            },
            EntrySpec("power_save", "省电模式", "已开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                PowerSaveModeTrigger(it.id, enabled = it.bool())
            },
            EntrySpec("boot", "开机完成", "", emptyList()) { BootTrigger(it.id) },
            EntrySpec("battery_temp", "电池温度", "温度摄氏度", listOf(TriggerDescriptor.Field.NUMBER, TriggerDescriptor.Field.CHOICE_ABOVE_BELOW)) {
                BatteryTemperatureTrigger(it.id, temperatureCelsius = it.int(), direction = it.second.ifBlank { "above" })
            },
            EntrySpec("cell_type", "蜂窝类型", "连接类型", listOf(TriggerDescriptor.Field.TEXT)) {
                CellConnectionTypeTrigger(it.id, connectionType = it.first)
            },
            EntrySpec("mobile_data", "移动数据", "已开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                DataOnOffTrigger(it.id, enabled = it.bool())
            },
            EntrySpec("auto_rotate", "自动旋转", "已开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                AutoRotateTrigger(it.id, enabled = it.bool())
            },
            EntrySpec("shake", "摇晃设备", "灵敏度", listOf(TriggerDescriptor.Field.NUMBER)) {
                ShakeDeviceTrigger(it.id, sensitivity = it.int().coerceAtLeast(1))
            },
            EntrySpec(
                "flip",
                "翻转设备",
                "面朝下 true / false",
                listOf(TriggerDescriptor.Field.BOOLEAN),
            ) { FlipDeviceTrigger(it.id, faceDown = it.bool()) },
            EntrySpec(
                "proximity",
                "距离感应",
                "靠近 true / false",
                listOf(TriggerDescriptor.Field.BOOLEAN),
            ) { ProximityTrigger(it.id, near = it.bool()) },
            EntrySpec("light", "光线传感器", "勒克斯", listOf(TriggerDescriptor.Field.NUMBER, TriggerDescriptor.Field.CHOICE_ABOVE_BELOW)) {
                LightSensorTrigger(it.id, lux = it.int(), direction = it.second.ifBlank { "above" })
            },
            EntrySpec("orientation", "屏幕方向", "方向名", listOf(TriggerDescriptor.Field.CHOICE_ORIENTATION)) {
                OrientationTrigger(it.id, orientation = it.first)
            },
            EntrySpec(
                "volume_button",
                "音量按键",
                "按键名",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { VolumeButtonTrigger(it.id, button = it.first) },
            EntrySpec(
                "volume_long",
                "音量长按",
                "按键名",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { VolumeLongPressTrigger(it.id, button = it.first) },
            EntrySpec("power_button", "电源按键", "事件名", listOf(TriggerDescriptor.Field.TEXT)) { PowerButtonTrigger(it.id, event = it.first) },
            EntrySpec("home_long", "主页长按", "", emptyList()) { HomeButtonLongPressTrigger(it.id) },
            EntrySpec("recents", "最近任务", "", emptyList()) { RecentAppsTrigger(it.id) },
            EntrySpec("incoming_call", "来电", "号码，可留空", listOf(TriggerDescriptor.Field.PHONE)) {
                IncomingCallTrigger(it.id, phoneNumber = it.first.ifBlank { null })
            },
            EntrySpec("outgoing_call", "去电", "号码，可留空", listOf(TriggerDescriptor.Field.PHONE)) {
                OutgoingCallTrigger(it.id, phoneNumber = it.first.ifBlank { null })
            },
            EntrySpec("missed_call", "未接来电", "号码，可留空", listOf(TriggerDescriptor.Field.PHONE)) {
                CallMissedTrigger(it.id, phoneNumber = it.first.ifBlank { null })
            },
            EntrySpec("call_ended", "通话结束", "号码，可留空", listOf(TriggerDescriptor.Field.PHONE)) {
                CallEndedTrigger(it.id, phoneNumber = it.first.ifBlank { null })
            },
            EntrySpec("call_active", "通话进行", "方向 incoming / outgoing", listOf(TriggerDescriptor.Field.TEXT)) {
                CallActiveTrigger(it.id, direction = it.first.ifBlank { "incoming" })
            },
            EntrySpec("incoming_sms", "收到短信", "发送方号码", listOf(TriggerDescriptor.Field.PHONE)) {
                IncomingSMSTrigger(it.id, sender = it.first.ifBlank { null }, contentMatch = it.second.ifBlank { null })
            },
            EntrySpec("sms_sent", "短信已发送", "收件人号码", listOf(TriggerDescriptor.Field.PHONE)) {
                SMSSentTrigger(it.id, recipient = it.first.ifBlank { null })
            },
            EntrySpec("toast", "Toast 提示", "应用包名，可留空", listOf(TriggerDescriptor.Field.PACKAGE)) {
                ToastTrigger(it.id, packageName = it.first.ifBlank { null })
            },
            EntrySpec("calendar", "日历事件", "", emptyList()) { CalendarTrigger(it.id) },
            EntrySpec(
                "shortcut",
                "快捷方式",
                "快捷方式名",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { ShortcutTrigger(it.id, shortcutName = it.first) },
            EntrySpec(
                "widget",
                "小组件按钮",
                "按钮标签",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { WidgetPressedTrigger(it.id, widgetLabel = it.first) },
            EntrySpec("qs_tile", "快捷设置磁贴", "on / off", listOf(TriggerDescriptor.Field.TEXT)) {
                QuickSettingsTileTrigger(it.id, tileState = it.first.ifBlank { "on" })
            },
            EntrySpec(
                "intent",
                "收到广播",
                "Action 名称",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { IntentReceivedTrigger(it.id, action = it.first) },
            EntrySpec("share_text", "分享文本", "匹配文本，可留空", listOf(TriggerDescriptor.Field.TEXT)) {
                ShareTextTrigger(it.id, contentMatch = it.first.ifBlank { null })
            },
            EntrySpec("share_file", "分享文件", "MIME 类型", listOf(TriggerDescriptor.Field.TEXT)) {
                ShareFileTrigger(it.id, mimeType = it.first.ifBlank { null })
            },
            EntrySpec(
                "macro_finished",
                "宏执行完成",
                "宏 ID",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { MacroFinishedTrigger(it.id, macroId = it.first) },
            EntrySpec("logcat", "日志输出", "匹配文本", listOf(TriggerDescriptor.Field.TEXT)) { LogcatTrigger(it.id, pattern = it.first) },
            EntrySpec(
                "system_setting",
                "系统设置",
                "设置项名",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { SystemSettingTrigger(it.id, settingKey = it.first) },
            EntrySpec("usb", "USB 设备", "已连接 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                UsbDeviceConnectionTrigger(it.id, connected = it.bool())
            },
            EntrySpec("usb_tethering", "USB 共享网络", "已开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                UsbTetheringTrigger(it.id, enabled = it.bool())
            },
            EntrySpec("ip_change", "IP 地址变化", "", emptyList()) { IpAddressChangeTrigger(it.id) },
            EntrySpec("transport", "网络类型变化", "传输类型", listOf(TriggerDescriptor.Field.TEXT)) {
                NetworkTransportChangeTrigger(it.id, transport = it.first)
            },
            EntrySpec("roaming", "漫游状态", "已漫游 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                NetworkRoamingTrigger(it.id, roaming = it.bool())
            },
            EntrySpec("sim", "SIM 卡变化", "状态名", listOf(TriggerDescriptor.Field.TEXT)) {
                SimChangeTrigger(it.id, state = it.first.ifBlank { "changed" })
            },
            EntrySpec(
                "sleep",
                "设备休眠",
                "已休眠 true / false",
                listOf(TriggerDescriptor.Field.BOOLEAN),
            ) { SleepTrigger(it.id, asleep = it.bool()) },
            EntrySpec("nfc_state", "NFC 开关", "已开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                NfcStateTrigger(it.id, enabled = it.bool())
            },
            EntrySpec("nfc_tag", "NFC 标签", "标签 ID，可留空", listOf(TriggerDescriptor.Field.TEXT)) {
                NfcTagTrigger(it.id, tagId = it.first.ifBlank { null })
            },
            EntrySpec("geofence", "地理围栏", "围栏名称 半径米数", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.CHOICE_ENTER_EXIT)) {
                GeofenceTrigger(
                    it.id,
                    name = it.first,
                    radiusMeters = it.second.toIntOrNull() ?: 100,
                    enter = it.third.toBooleanStrictOrNull() ?: true,
                )
            },
            EntrySpec("location", "位置区域", "纬度 经度 半径米数", listOf(TriggerDescriptor.Field.LAT_LON)) {
                LocationTrigger(
                    it.id,
                    latitude = it.first.toDoubleOrNull() ?: 0.0,
                    longitude = it.second.toDoubleOrNull() ?: 0.0,
                    radiusMeters = it.third.toIntOrNull() ?: 200,
                )
            },
            EntrySpec("cell_tower", "蜂窝基站", "基站 ID", listOf(TriggerDescriptor.Field.TEXT)) { CellTowerTrigger(it.id, cellId = it.first) },
            EntrySpec("weather", "天气", "天气状况", listOf(TriggerDescriptor.Field.TEXT)) { WeatherTrigger(it.id, condition = it.first) },
            EntrySpec("sunrise_sunset", "日出日落", "sunrise / sunset", listOf(TriggerDescriptor.Field.TEXT)) {
                SunriseSunsetTrigger(it.id, event = it.first.ifBlank { "sunrise" })
            },
            EntrySpec(
                "day_of_month",
                "每月日期",
                "日期 1-31",
                listOf(TriggerDescriptor.Field.NUMBER),
            ) { DayOfMonthTrigger(it.id, day = it.int()) },
            EntrySpec("system_time", "系统时间", "changed true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                SystemTimeTrigger(it.id, changed = it.bool())
            },
            EntrySpec(
                "stopwatch",
                "秒表",
                "时长毫秒",
                listOf(TriggerDescriptor.Field.NUMBER),
            ) { StopwatchTrigger(it.id, durationMillis = it.long()) },
            EntrySpec("webhook", "Webhook", "回调地址", listOf(TriggerDescriptor.Field.TEXT)) { WebhookTrigger(it.id, url = it.first) },
            EntrySpec(
                "floating_text",
                "悬浮文本",
                "文本内容",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { FloatingTextTrigger(it.id, text = it.first) },
            EntrySpec(
                "floating_button",
                "悬浮按钮",
                "按钮标签",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { FloatingButtonTrigger(it.id, label = it.first) },
            EntrySpec("ui_click", "界面点击", "视图 ID 或匹配文本", listOf(TriggerDescriptor.Field.TEXT)) {
                UIClickTrigger(it.id, viewId = it.first.ifBlank { null }, textMatch = it.second.ifBlank { null })
            },
            EntrySpec(
                "swipe",
                "滑动手势",
                "方向",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { SwipeTrigger(it.id, direction = it.first.ifBlank { "up" }) },
            EntrySpec("screen_content", "屏幕文本", "匹配文本，可留空", listOf(TriggerDescriptor.Field.TEXT)) {
                ScreenContentTrigger(it.id, textMatch = it.first.ifBlank { null })
            },
            EntrySpec("screenshot_content", "截屏文本", "匹配文本，可留空", listOf(TriggerDescriptor.Field.TEXT)) {
                ScreenshotContentTrigger(it.id, textMatch = it.first.ifBlank { null })
            },
            EntrySpec("accessibility", "无障碍服务状态", "已启用 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                AccessibilityServiceStateTrigger(it.id, enabled = it.bool())
            },
            EntrySpec("variable", "变量变化", "变量名 条件", listOf(TriggerDescriptor.Field.TEXT)) {
                VariableTrigger(it.id, variableName = it.first, expression = it.second)
            },
            EntrySpec(
                "macro_enabled",
                "宏启用状态",
                "宏 ID",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { MacroEnabledTrigger(it.id, macroId = it.first) },
            EntrySpec("activity", "界面启动", "包名 类名", listOf(TriggerDescriptor.Field.TEXT)) {
                ActivityLaunchedTrigger(it.id, packageName = it.first.ifBlank { null }, activityClass = it.second.ifBlank { null })
            },
            EntrySpec(
                "app_enabled",
                "应用启用状态",
                "应用包名",
                listOf(TriggerDescriptor.Field.PACKAGE),
            ) { AppEnabledTrigger(it.id, packageName = it.first) },
            EntrySpec("priority", "优先模式", "已开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                PriorityModeTrigger(it.id, enabled = it.bool())
            },
            EntrySpec(
                "auto_sync",
                "自动同步",
                "已开启 true / false",
                listOf(TriggerDescriptor.Field.BOOLEAN),
            ) { AutoSyncTrigger(it.id, enabled = it.bool()) },
            EntrySpec(
                "daydream",
                "屏幕保护",
                "运行中 true / false",
                listOf(TriggerDescriptor.Field.BOOLEAN),
            ) { DayDreamTrigger(it.id, active = it.bool()) },
            EntrySpec("drawer", "抽屉栏", "已打开 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                DrawerOpenCloseTrigger(it.id, opened = it.bool())
            },
            EntrySpec("keyboard", "输入法开合", "已打开 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                KeyboardOpenCloseTrigger(it.id, opened = it.bool())
            },
            EntrySpec("media_track", "媒体曲目变化", "包名，可留空", listOf(TriggerDescriptor.Field.PACKAGE)) {
                MediaTrackChangedTrigger(it.id, packageName = it.first.ifBlank { null })
            },
            EntrySpec(
                "music",
                "音乐播放",
                "播放中 true / false",
                listOf(TriggerDescriptor.Field.BOOLEAN),
            ) { MusicPlayingTrigger(it.id, playing = it.bool()) },
            EntrySpec("mic", "麦克风录音", "录音中 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                MicrophoneRecordingTrigger(it.id, recording = it.bool())
            },
            EntrySpec("camera_in_use", "相机占用", "占用中 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                CameraInUseTrigger(it.id, inUse = it.bool())
            },
            EntrySpec("torch", "手电筒状态", "已开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                CameraFlashlightTrigger(it.id, enabled = it.bool())
            },
            EntrySpec(
                "beacon",
                "蓝牙信标",
                "信标 ID",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { BluetoothBeaconTrigger(it.id, beaconId = it.first) },
            EntrySpec("assistant", "语音助手", "助手名", listOf(TriggerDescriptor.Field.TEXT)) {
                AssistantActivatedTrigger(it.id, assistant = it.first.ifBlank { "default" })
            },
            EntrySpec("failed_login", "登录失败", "失败次数", listOf(TriggerDescriptor.Field.NUMBER)) {
                FailedLoginTrigger(it.id, attempts = it.int().coerceAtLeast(1))
            },
            EntrySpec(
                "fingerprint",
                "指纹手势",
                "方向",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { FingerprintGestureTrigger(it.id, direction = it.first) },
            EntrySpec("activity_recognition", "运动类型", "类型名", listOf(TriggerDescriptor.Field.TEXT)) {
                ActivityRecognitionTrigger(it.id, activity = it.first)
            },
            EntrySpec("fold", "折叠角度", "角度", listOf(TriggerDescriptor.Field.NUMBER, TriggerDescriptor.Field.CHOICE_ABOVE_BELOW)) {
                FoldAngleTrigger(it.id, angle = it.int(), direction = it.second.ifBlank { "above" })
            },
            EntrySpec("file_changed", "文件变化", "文件路径", listOf(TriggerDescriptor.Field.TEXT)) { FileChangedTrigger(it.id, path = it.first) },
            EntrySpec("file_opened", "文件打开", "MIME 类型", listOf(TriggerDescriptor.Field.TEXT)) {
                FileOpenedTrigger(it.id, mimeType = it.first.ifBlank { null })
            },
            EntrySpec("photo", "拍照完成", "包名，可留空", listOf(TriggerDescriptor.Field.PACKAGE)) {
                PhotoTakenTrigger(it.id, packageName = it.first.ifBlank { null })
            },
            EntrySpec("mode", "模式进入退出", "模式名", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.CHOICE_ENTER_EXIT)) {
                ModeEnterExitTrigger(it.id, modeName = it.first, enter = it.second.toBooleanStrictOrNull() ?: true)
            },
            EntrySpec(
                "http_server",
                "本地 HTTP 服务",
                "端口",
                listOf(TriggerDescriptor.Field.NUMBER),
            ) { HttpServerTrigger(it.id, port = it.int()) },
            EntrySpec("email", "收到邮件", "发送方匹配", listOf(TriggerDescriptor.Field.TEXT)) {
                EmailReceivedTrigger(it.id, senderMatch = it.first.ifBlank { null })
            },
            EntrySpec("dial", "拨号", "号码，可留空", listOf(TriggerDescriptor.Field.PHONE)) {
                DialNumberTrigger(it.id, phoneNumber = it.first.ifBlank { null })
            },
            EntrySpec("data_used", "流量阈值", "字节数", listOf(TriggerDescriptor.Field.NUMBER, TriggerDescriptor.Field.CHOICE_ABOVE_BELOW)) {
                DataUsedTrigger(it.id, bytes = it.long(), direction = it.second.ifBlank { "above" })
            },
        )

    fun indexOfTrigger(trigger: Trigger): Int = entries.indexOfFirst { matches(it, trigger) }

    private fun matches(
        spec: EntrySpec,
        trigger: Trigger,
    ): Boolean =
        when (trigger) {
            is EmptyTrigger -> spec.key == "empty"
            is LinkOpenedTrigger -> spec.key == "link"
            is AppLaunchedTrigger -> spec.key == "app_launched"
            is DataSmsReceivedTrigger -> spec.key == "data_sms"
            is ScheduleTrigger -> spec.key == "schedule"
            is ScreenOnOffTrigger -> spec.key == "screen"
            is BatteryLevelTrigger -> spec.key == "battery_level"
            is AirplaneModeTrigger -> spec.key == "airplane"
            is BluetoothTrigger -> spec.key == "bluetooth"
            is ExternalPowerTrigger -> spec.key == "power"
            is HeadphonesTrigger -> spec.key == "headphones"
            is HotspotTrigger -> spec.key == "hotspot"
            is SilentModeTrigger -> spec.key == "silent"
            is WifiConnectionTrigger -> spec.key == "wifi"
            is WifiSSIDTrigger -> spec.key == "wifi_ssid"
            is SignalOnOffTrigger -> spec.key == "signal"
            is DockTrigger -> spec.key == "dock"
            is VpnTrigger -> spec.key == "vpn"
            is GPSEnabledTrigger -> spec.key == "gps"
            is DeviceUnlockedTrigger -> spec.key == "unlocked"
            is ClipboardChangeTrigger -> spec.key == "clipboard"
            is DarkThemeTrigger -> spec.key == "dark_theme"
            is MediaButtonTrigger -> spec.key == "media_button"
            is NotificationTrigger -> spec.key == "notification"
            is AppLifecycleTrigger -> spec.key == "app_installed"
            is PowerSaveModeTrigger -> spec.key == "power_save"
            is BootTrigger -> spec.key == "boot"
            is BatteryTemperatureTrigger -> spec.key == "battery_temp"
            is CellConnectionTypeTrigger -> spec.key == "cell_type"
            is DataOnOffTrigger -> spec.key == "mobile_data"
            is AutoRotateTrigger -> spec.key == "auto_rotate"
            is ShakeDeviceTrigger -> spec.key == "shake"
            is FlipDeviceTrigger -> spec.key == "flip"
            is ProximityTrigger -> spec.key == "proximity"
            is LightSensorTrigger -> spec.key == "light"
            is OrientationTrigger -> spec.key == "orientation"
            is VolumeButtonTrigger -> spec.key == "volume_button"
            is VolumeLongPressTrigger -> spec.key == "volume_long"
            is PowerButtonTrigger -> spec.key == "power_button"
            is HomeButtonLongPressTrigger -> spec.key == "home_long"
            is RecentAppsTrigger -> spec.key == "recents"
            is IncomingCallTrigger -> spec.key == "incoming_call"
            is OutgoingCallTrigger -> spec.key == "outgoing_call"
            is CallMissedTrigger -> spec.key == "missed_call"
            is CallEndedTrigger -> spec.key == "call_ended"
            is CallActiveTrigger -> spec.key == "call_active"
            is IncomingSMSTrigger -> spec.key == "incoming_sms"
            is SMSSentTrigger -> spec.key == "sms_sent"
            is ToastTrigger -> spec.key == "toast"
            is CalendarTrigger -> spec.key == "calendar"
            is ShortcutTrigger -> spec.key == "shortcut"
            is WidgetPressedTrigger -> spec.key == "widget"
            is QuickSettingsTileTrigger -> spec.key == "qs_tile"
            is IntentReceivedTrigger -> spec.key == "intent"
            is ShareTextTrigger -> spec.key == "share_text"
            is ShareFileTrigger -> spec.key == "share_file"
            is MacroFinishedTrigger -> spec.key == "macro_finished"
            is LogcatTrigger -> spec.key == "logcat"
            is SystemSettingTrigger -> spec.key == "system_setting"
            is UsbDeviceConnectionTrigger -> spec.key == "usb"
            is UsbTetheringTrigger -> spec.key == "usb_tethering"
            is IpAddressChangeTrigger -> spec.key == "ip_change"
            is NetworkTransportChangeTrigger -> spec.key == "transport"
            is NetworkRoamingTrigger -> spec.key == "roaming"
            is SimChangeTrigger -> spec.key == "sim"
            is SleepTrigger -> spec.key == "sleep"
            is NfcStateTrigger -> spec.key == "nfc_state"
            is NfcTagTrigger -> spec.key == "nfc_tag"
            is GeofenceTrigger -> spec.key == "geofence"
            is LocationTrigger -> spec.key == "location"
            is CellTowerTrigger -> spec.key == "cell_tower"
            is WeatherTrigger -> spec.key == "weather"
            is SunriseSunsetTrigger -> spec.key == "sunrise_sunset"
            is DayOfMonthTrigger -> spec.key == "day_of_month"
            is SystemTimeTrigger -> spec.key == "system_time"
            is StopwatchTrigger -> spec.key == "stopwatch"
            is WebhookTrigger -> spec.key == "webhook"
            is FloatingTextTrigger -> spec.key == "floating_text"
            is FloatingButtonTrigger -> spec.key == "floating_button"
            is UIClickTrigger -> spec.key == "ui_click"
            is SwipeTrigger -> spec.key == "swipe"
            is ScreenContentTrigger -> spec.key == "screen_content"
            is ScreenshotContentTrigger -> spec.key == "screenshot_content"
            is AccessibilityServiceStateTrigger -> spec.key == "accessibility"
            is VariableTrigger -> spec.key == "variable"
            is MacroEnabledTrigger -> spec.key == "macro_enabled"
            is ActivityLaunchedTrigger -> spec.key == "activity"
            is AppEnabledTrigger -> spec.key == "app_enabled"
            is PriorityModeTrigger -> spec.key == "priority"
            is AutoSyncTrigger -> spec.key == "auto_sync"
            is DayDreamTrigger -> spec.key == "daydream"
            is DrawerOpenCloseTrigger -> spec.key == "drawer"
            is KeyboardOpenCloseTrigger -> spec.key == "keyboard"
            is MediaTrackChangedTrigger -> spec.key == "media_track"
            is MusicPlayingTrigger -> spec.key == "music"
            is MicrophoneRecordingTrigger -> spec.key == "mic"
            is CameraInUseTrigger -> spec.key == "camera_in_use"
            is CameraFlashlightTrigger -> spec.key == "torch"
            is BluetoothBeaconTrigger -> spec.key == "beacon"
            is AssistantActivatedTrigger -> spec.key == "assistant"
            is FailedLoginTrigger -> spec.key == "failed_login"
            is FingerprintGestureTrigger -> spec.key == "fingerprint"
            is ActivityRecognitionTrigger -> spec.key == "activity_recognition"
            is FoldAngleTrigger -> spec.key == "fold"
            is FileChangedTrigger -> spec.key == "file_changed"
            is FileOpenedTrigger -> spec.key == "file_opened"
            is PhotoTakenTrigger -> spec.key == "photo"
            is ModeEnterExitTrigger -> spec.key == "mode"
            is HttpServerTrigger -> spec.key == "http_server"
            is EmailReceivedTrigger -> spec.key == "email"
            is DialNumberTrigger -> spec.key == "dial"
            is DataUsedTrigger -> spec.key == "data_used"
            else -> false
        }
}
