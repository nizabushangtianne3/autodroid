package dev.niza.autodroid.engine

/** Editor catalog for actions, mirroring [TriggerCatalog]. */
object ActionCatalog {
    val entries: List<EntrySpec> =
        listOf(
            EntrySpec(
                "notify",
                "显示通知",
                "标题 内容",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { NotifyAction(it.id, title = it.first, body = it.second) },
            EntrySpec("open_url", "打开网页", "网址", listOf(TriggerDescriptor.Field.TEXT)) { OpenUrlAction(it.id, url = it.first) },
            EntrySpec(
                "send_data_sms",
                "发送数据短信",
                "号码 端口 HEX",
                listOf(TriggerDescriptor.Field.PHONE, TriggerDescriptor.Field.NUMBER, TriggerDescriptor.Field.HEX),
            ) {
                SendDataSmsAction(
                    it.id,
                    destinationPort = it.second.toIntOrNull() ?: 0,
                    sender = it.first,
                    payload = parseHexText(it.third),
                )
            },
            EntrySpec("send_sms", "发送短信", "号码 内容", listOf(TriggerDescriptor.Field.PHONE, TriggerDescriptor.Field.TEXT)) {
                SendSMSAction(it.id, recipient = it.first, message = it.second)
            },
            EntrySpec(
                "send_email",
                "发送邮件",
                "收件人 主题 内容",
                listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT),
            ) {
                SendEmailAction(it.id, recipient = it.first, subject = it.second, body = it.third)
            },
            EntrySpec(
                "forward_sms",
                "转发短信",
                "转发号码",
                listOf(TriggerDescriptor.Field.PHONE),
            ) { ForwardSMSAction(it.id, recipient = it.first) },
            EntrySpec("make_call", "拨打电话", "号码", listOf(TriggerDescriptor.Field.PHONE)) { MakeCallAction(it.id, phoneNumber = it.first) },
            EntrySpec("answer_call", "接听来电", "", emptyList()) { AnswerCallAction(it.id) },
            EntrySpec("reject_call", "拒接来电", "", emptyList()) { RejectCallAction(it.id) },
            EntrySpec("speaker_phone", "扬声器", "开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                SpeakerPhoneAction(it.id, enabled = it.bool())
            },
            EntrySpec("mute_mic", "麦克风静音", "静音 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                MuteMicrophoneAction(it.id, muted = it.bool())
            },
            EntrySpec("record_mic", "录制麦克风", "路径 秒数", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.NUMBER)) {
                RecordMicrophoneAction(it.id, path = it.first, durationSeconds = it.int())
            },
            EntrySpec(
                "read_sound",
                "读取音量",
                "变量名",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { ReadSoundLevelAction(it.id, variableName = it.first) },
            EntrySpec("set_variable", "设置变量", "变量名 变量值", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT)) {
                SetVariableAction(it.id, name = it.first, value = it.second)
            },
            EntrySpec("clear_variables", "清空局部变量", "", emptyList()) { ClearVariablesAction(it.id) },
            EntrySpec(
                "set_dict_entry",
                "写入字典",
                "字典名 键 值",
                listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT),
            ) {
                SetDictionaryEntryAction(it.id, dictionaryName = it.first, key = it.second, value = it.third)
            },
            EntrySpec("clear_dict_entry", "删除字典项", "字典名 键", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT)) {
                ClearDictionaryEntryAction(it.id, dictionaryName = it.first, key = it.second)
            },
            EntrySpec(
                "iterate_dict",
                "遍历字典",
                "字典名 键变量 值变量",
                listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT),
            ) {
                IterateDictionaryAction(it.id, dictionaryName = it.first, keyVariable = it.second, valueVariable = it.third)
            },
            EntrySpec("iterate_dict_end", "结束遍历字典", "", emptyList()) { IterateDictionaryEndAction(it.id, dictionaryName = it.first) },
            EntrySpec(
                "set_array_entry",
                "写入数组",
                "数组名 索引 值",
                listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.NUMBER, TriggerDescriptor.Field.TEXT),
            ) {
                SetArrayEntryAction(it.id, arrayName = it.first, index = it.int(), value = it.third)
            },
            EntrySpec(
                "if_condition",
                "如果",
                "条件表达式",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { IfConditionAction(it.id, expression = it.first) },
            EntrySpec("else_condition", "否则", "", emptyList()) { ElseConditionAction(it.id) },
            EntrySpec("end_if", "结束如果", "", emptyList()) { EndIfAction(it.id) },
            EntrySpec("wait", "等待", "毫秒数", listOf(TriggerDescriptor.Field.NUMBER)) { WaitAction(it.id, durationMillis = it.long()) },
            EntrySpec("wait_trigger", "等待触发器", "触发器 ID 超时毫秒", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.NUMBER)) {
                WaitUntilTriggerAction(it.id, triggerId = it.first, timeoutMillis = it.second.toLongOrNull() ?: 0)
            },
            EntrySpec("repeat_start", "重复", "次数", listOf(TriggerDescriptor.Field.NUMBER)) {
                RepeatStartAction(it.id, count = it.int().coerceAtLeast(1))
            },
            EntrySpec("repeat_end", "结束重复", "", emptyList()) { RepeatEndAction(it.id) },
            EntrySpec("break", "跳出循环", "", emptyList()) { BreakFromLoopAction(it.id) },
            EntrySpec("continue", "继续循环", "", emptyList()) { ContinueLoopAction(it.id) },
            EntrySpec("goto", "跳转标记", "标记名", listOf(TriggerDescriptor.Field.TEXT)) { GotoAction(it.id, label = it.first) },
            EntrySpec("mutex", "互斥锁", "名称", listOf(TriggerDescriptor.Field.TEXT)) { MutexAction(it.id, name = it.first) },
            EntrySpec("mutex_end", "结束互斥锁", "名称", listOf(TriggerDescriptor.Field.TEXT)) { MutexEndAction(it.id, name = it.first) },
            EntrySpec("stop_macro", "停止宏", "", emptyList()) { CancelActiveMacroAction(it.id) },
            EntrySpec("pause", "暂停", "毫秒数", listOf(TriggerDescriptor.Field.NUMBER)) { PauseAction(it.id, durationMillis = it.long()) },
            EntrySpec("log", "记录日志", "日志内容", listOf(TriggerDescriptor.Field.TEXT)) { LogAction(it.id, message = it.first) },
            EntrySpec("clear_log", "清空日志", "", emptyList()) { ClearLogAction(it.id) },
            EntrySpec("export_log", "导出日志", "文件路径", listOf(TriggerDescriptor.Field.TEXT)) { ExportLogAction(it.id, path = it.first) },
            EntrySpec(
                "launch_app",
                "启动应用",
                "应用包名",
                listOf(TriggerDescriptor.Field.PACKAGE),
            ) { LaunchAppAction(it.id, packageName = it.first) },
            EntrySpec("launch_activity", "启动界面", "包名 类名", listOf(TriggerDescriptor.Field.PACKAGE, TriggerDescriptor.Field.TEXT)) {
                LaunchActivityAction(it.id, packageName = it.first, activityClass = it.second)
            },
            EntrySpec("launch_home", "回到桌面", "", emptyList()) { LaunchHomeScreenAction(it.id) },
            EntrySpec(
                "close_app",
                "关闭应用",
                "应用包名",
                listOf(TriggerDescriptor.Field.PACKAGE),
            ) { CloseApplicationAction(it.id, packageName = it.first) },
            EntrySpec(
                "kill_app",
                "结束后台应用",
                "应用包名",
                listOf(TriggerDescriptor.Field.PACKAGE),
            ) { KillBackgroundAppAction(it.id, packageName = it.first) },
            EntrySpec("press_back", "返回键", "", emptyList()) { PressBackAction(it.id) },
            EntrySpec("input_key", "发送按键", "按键名", listOf(TriggerDescriptor.Field.TEXT)) { InputKeyEventAction(it.id, keyCode = it.first) },
            EntrySpec("touch", "点击屏幕", "X Y", listOf(TriggerDescriptor.Field.NUMBER, TriggerDescriptor.Field.NUMBER)) {
                TouchScreenAction(it.id, x = it.int(), y = it.second.toIntOrNull() ?: 0)
            },
            EntrySpec("toast", "Toast 提示", "文本内容", listOf(TriggerDescriptor.Field.TEXT)) { ToastAction(it.id, text = it.first) },
            EntrySpec("popup", "弹窗", "文本内容", listOf(TriggerDescriptor.Field.TEXT)) { PopUpAction(it.id, text = it.first) },
            EntrySpec("message_dialog", "消息对话框", "标题 内容", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT)) {
                MessageDialogAction(it.id, title = it.first, message = it.second)
            },
            EntrySpec("option_dialog", "选项对话框", "标题 选项(竖线分隔)", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT)) {
                OptionDialogAction(it.id, title = it.first, options = it.second)
            },
            EntrySpec(
                "selection_dialog",
                "选择对话框",
                "标题 变量名 选项",
                listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT),
            ) {
                SelectionDialogAction(it.id, title = it.first, variableName = it.second, options = it.third)
            },
            EntrySpec("floating_text", "悬浮文本", "文本 秒数", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.NUMBER)) {
                FloatingTextAction(it.id, text = it.first, durationSeconds = it.second.toIntOrNull() ?: 5)
            },
            EntrySpec("floating_button", "悬浮按钮", "按钮标签", listOf(TriggerDescriptor.Field.TEXT)) {
                FloatingButtonConfigureAction(it.id, label = it.first)
            },
            EntrySpec("clipboard", "复制到剪贴板", "文本内容", listOf(TriggerDescriptor.Field.TEXT)) { ClipboardAction(it.id, text = it.first) },
            EntrySpec(
                "update_clipboard",
                "追加剪贴板",
                "文本内容",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { UpdateClipboardAction(it.id, text = it.first) },
            EntrySpec("share_text", "分享文本", "主题 内容", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT)) {
                ShareTextAction(it.id, subject = it.first, text = it.second)
            },
            EntrySpec(
                "share_location",
                "分享位置",
                "消息",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { ShareLocationAction(it.id, message = it.first) },
            EntrySpec(
                "text_manipulation",
                "文本处理",
                "源变量 目标变量 操作",
                listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT),
            ) {
                TextManipulationAction(
                    it.id,
                    sourceVariable = it.first,
                    targetVariable = it.second,
                    operation = it.third.ifBlank { "none" },
                )
            },
            EntrySpec(
                "encrypt",
                "加密或解密",
                "源变量 目标变量 模式",
                listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT),
            ) {
                EncryptDecryptTextAction(
                    it.id,
                    sourceVariable = it.first,
                    targetVariable = it.second,
                    mode = it.third.ifBlank { "encrypt" },
                )
            },
            EntrySpec(
                "json_parse",
                "解析 JSON",
                "源变量 路径 目标变量",
                listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT),
            ) {
                JsonParseAction(it.id, sourceVariable = it.first, jsonPath = it.second, targetVariable = it.third)
            },
            EntrySpec("send_intent", "发送广播", "Action 附加参数", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT)) {
                SendIntentAction(it.id, action = it.first, extras = it.second.ifBlank { null })
            },
            EntrySpec("http_request", "HTTP 请求", "网址 方法", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT)) {
                HttpRequestAction(it.id, url = it.first, method = it.second.ifBlank { "GET" }, responseVariable = it.third.ifBlank { null })
            },
            EntrySpec(
                "udp",
                "UDP 指令",
                "主机 端口 内容",
                listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.NUMBER, TriggerDescriptor.Field.TEXT),
            ) {
                UDPCommandAction(it.id, host = it.first, port = it.int(), payload = it.third)
            },
            EntrySpec("write_file", "写入文件", "路径 内容", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT)) {
                WriteToFileAction(it.id, path = it.first, content = it.second)
            },
            EntrySpec("read_file", "读取文件", "路径 变量名", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT)) {
                ReadFileAction(it.id, path = it.first, variableName = it.second)
            },
            EntrySpec("file_operation", "文件操作", "操作 源路径", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT)) {
                FileOperationAction(it.id, operation = it.first, sourcePath = it.second)
            },
            EntrySpec(
                "pick_file",
                "选择文件",
                "变量名",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { FileSelectionAction(it.id, variableName = it.first) },
            EntrySpec("set_volume", "设置音量", "音量类型 音量值", listOf(TriggerDescriptor.Field.CHOICE_STREAM, TriggerDescriptor.Field.NUMBER)) {
                SetVolumeAction(it.id, stream = it.first.ifBlank { "media" }, level = it.int())
            },
            EntrySpec(
                "volume_adjust",
                "增减音量",
                "音量类型 方向 步数",
                listOf(TriggerDescriptor.Field.CHOICE_STREAM, TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.NUMBER),
            ) {
                VolumeIncrementDecrementAction(
                    it.id,
                    stream =
                        it.first.ifBlank {
                            "media"
                        },
                    direction = it.second.ifBlank { "increment" },
                    steps = it.third.toIntOrNull() ?: 1,
                )
            },
            EntrySpec(
                "set_brightness",
                "设置亮度",
                "亮度 自动 true/false",
                listOf(TriggerDescriptor.Field.NUMBER, TriggerDescriptor.Field.BOOLEAN),
            ) {
                SetBrightnessAction(it.id, level = it.int(), automatic = it.bool())
            },
            EntrySpec(
                "dim_screen",
                "调暗屏幕",
                "已调暗 true / false",
                listOf(TriggerDescriptor.Field.BOOLEAN),
            ) { DimScreenAction(it.id, dimmed = it.bool()) },
            EntrySpec(
                "screen_timeout",
                "息屏时间",
                "秒数",
                listOf(TriggerDescriptor.Field.NUMBER),
            ) { SetScreenTimeoutAction(it.id, seconds = it.int()) },
            EntrySpec("auto_rotate", "自动旋转", "开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                SetAutoRotateAction(it.id, enabled = it.bool())
            },
            EntrySpec("force_rotation", "强制方向", "方向名", listOf(TriggerDescriptor.Field.CHOICE_ORIENTATION)) {
                ForceScreenRotationAction(it.id, orientation = it.first)
            },
            EntrySpec("ringer_mode", "响铃模式", "normal / vibrate / silent", listOf(TriggerDescriptor.Field.CHOICE_RINGER)) {
                SetRingerModeAction(it.id, mode = it.first.ifBlank { "normal" })
            },
            EntrySpec("vibrate_on", "开启振动", "开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                SetVibrateAction(it.id, enabled = it.bool())
            },
            EntrySpec("silent_mode", "静音模式", "normal / vibrate / silent", listOf(TriggerDescriptor.Field.CHOICE_RINGER)) {
                SilentModeAction(it.id, mode = it.first.ifBlank { "vibrate" })
            },
            EntrySpec("vibrate", "振动", "毫秒数", listOf(TriggerDescriptor.Field.NUMBER)) { VibrateAction(it.id, durationMillis = it.long()) },
            EntrySpec("speak", "朗读文本", "文本内容", listOf(TriggerDescriptor.Field.TEXT)) { SpeakTextAction(it.id, text = it.first) },
            EntrySpec("play_sound", "播放声音", "声音名", listOf(TriggerDescriptor.Field.TEXT)) { PlaySoundAction(it.id, sound = it.first) },
            EntrySpec(
                "wifi",
                "WiFi 设置",
                "开启 true / false",
                listOf(TriggerDescriptor.Field.BOOLEAN),
            ) { SetWifiAction(it.id, enabled = it.bool()) },
            EntrySpec("bluetooth", "蓝牙设置", "开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                SetBluetoothAction(it.id, enabled = it.bool())
            },
            EntrySpec("airplane", "飞行模式", "开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                SetAirplaneModeAction(it.id, enabled = it.bool())
            },
            EntrySpec(
                "mobile_data",
                "移动数据",
                "开启 true / false",
                listOf(TriggerDescriptor.Field.BOOLEAN),
            ) { SetDataAction(it.id, enabled = it.bool()) },
            EntrySpec(
                "hotspot",
                "热点",
                "开启 true / false",
                listOf(TriggerDescriptor.Field.BOOLEAN),
            ) { SetHotspotAction(it.id, enabled = it.bool()) },
            EntrySpec(
                "gps",
                "位置服务",
                "开启 true / false",
                listOf(TriggerDescriptor.Field.BOOLEAN),
            ) { SetGPSAction(it.id, enabled = it.bool()) },
            EntrySpec(
                "nfc",
                "NFC 设置",
                "开启 true / false",
                listOf(TriggerDescriptor.Field.BOOLEAN),
            ) { SetNFCAction(it.id, enabled = it.bool()) },
            EntrySpec("auto_sync", "自动同步", "开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                SetAutoSyncAction(it.id, enabled = it.bool())
            },
            EntrySpec("torch", "手电筒", "开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                CameraFlashLightAction(it.id, enabled = it.bool())
            },
            EntrySpec("screenshot", "截屏", "文件路径", listOf(TriggerDescriptor.Field.TEXT)) {
                TakeScreenshotAction(it.id, path = it.first.ifBlank { null })
            },
            EntrySpec(
                "screen_on",
                "屏幕开关",
                "点亮 true / false",
                listOf(TriggerDescriptor.Field.BOOLEAN),
            ) { ScreenOnAction(it.id, on = it.bool()) },
            EntrySpec(
                "keep_awake",
                "保持唤醒",
                "秒数",
                listOf(TriggerDescriptor.Field.NUMBER),
            ) { KeepAwakeAction(it.id, durationSeconds = it.int()) },
            EntrySpec("immersive", "沉浸模式", "开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                ImmersiveModeAction(it.id, enabled = it.bool())
            },
            EntrySpec("status_bar", "展开通知栏", "展开 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                ExpandCollapseStatusBarAction(it.id, expanded = it.bool())
            },
            EntrySpec(
                "dark_theme",
                "深色模式",
                "开启 true / false",
                listOf(TriggerDescriptor.Field.BOOLEAN),
            ) { DarkThemeAction(it.id, enabled = it.bool()) },
            EntrySpec("battery_saver", "省电模式", "开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                BatterySaverAction(it.id, enabled = it.bool())
            },
            EntrySpec("heads_up", "悬浮通知", "开启 true / false", listOf(TriggerDescriptor.Field.BOOLEAN)) {
                HeadsUpNotificationsAction(it.id, enabled = it.bool())
            },
            EntrySpec("clear_notifications", "清除通知", "包名，可留空", listOf(TriggerDescriptor.Field.PACKAGE)) {
                ClearNotificationsAction(it.id, packageName = it.first.ifBlank { null })
            },
            EntrySpec(
                "configure_notifications",
                "配置通知",
                "包名 开启 true/false",
                listOf(TriggerDescriptor.Field.PACKAGE, TriggerDescriptor.Field.BOOLEAN),
            ) {
                ConfigureAppNotificationsAction(it.id, packageName = it.first, enabled = it.bool())
            },
            EntrySpec(
                "media_control",
                "媒体控制",
                "play / pause / next / previous / stop",
                listOf(TriggerDescriptor.Field.CHOICE_MEDIA_COMMAND),
            ) {
                ControlMediaAction(it.id, command = it.first.ifBlank { "pause" })
            },
            EntrySpec(
                "enable_trigger",
                "启用触发器",
                "触发器 ID",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { EnableTriggerAction(it.id, triggerId = it.first) },
            EntrySpec(
                "disable_trigger",
                "停用触发器",
                "触发器 ID",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { DisableTriggerAction(it.id, triggerId = it.first) },
            EntrySpec(
                "force_macro",
                "强制执行宏",
                "宏 ID",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { ForceMacroRunAction(it.id, macroId = it.first) },
            EntrySpec(
                "disable_macro",
                "停用宏",
                "宏 ID",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { DisableMacroAction(it.id, macroId = it.first) },
            EntrySpec("set_mode", "设置模式", "模式名", listOf(TriggerDescriptor.Field.TEXT)) { SetModeAction(it.id, modeName = it.first) },
            EntrySpec(
                "set_wallpaper",
                "设置壁纸",
                "图片地址",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { SetWallpaperAction(it.id, imageUri = it.first) },
            EntrySpec(
                "set_ringtone",
                "设置铃声",
                "铃声地址",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { SetRingtoneAction(it.id, ringtoneUri = it.first) },
            EntrySpec("set_notify_sound", "设置通知音", "音频地址", listOf(TriggerDescriptor.Field.TEXT)) {
                SetNotificationSoundAction(it.id, soundUri = it.first)
            },
            EntrySpec("set_language", "设置语言", "区域代码", listOf(TriggerDescriptor.Field.TEXT)) { SetLanguageAction(it.id, locale = it.first) },
            EntrySpec("alarm", "设置闹钟", "时间毫秒 消息", listOf(TriggerDescriptor.Field.NUMBER, TriggerDescriptor.Field.TEXT)) {
                SetAlarmClockAction(it.id, timeMillis = it.long(), message = it.second)
            },
            EntrySpec(
                "add_calendar",
                "添加日程",
                "标题 开始毫秒 分钟",
                listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.NUMBER, TriggerDescriptor.Field.NUMBER),
            ) {
                AddCalendarEntryAction(
                    it.id,
                    title = it.first,
                    startTimeMillis = it.second.toLongOrNull() ?: 0,
                    durationMinutes = it.third.toIntOrNull() ?: 30,
                )
            },
            EntrySpec("clear_call_log", "清空通话记录", "", emptyList()) { ClearCallLogAction(it.id) },
            EntrySpec("delete_sms", "删除短信", "发送方，可留空", listOf(TriggerDescriptor.Field.PHONE)) {
                DeleteSMSAction(it.id, sender = it.first.ifBlank { null })
            },
            EntrySpec(
                "get_contacts",
                "读取联系人",
                "变量名",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { GetContactsAction(it.id, variableName = it.first) },
            EntrySpec(
                "get_apps",
                "读取应用列表",
                "变量名",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { GetInstalledAppsAction(it.id, variableName = it.first) },
            EntrySpec("get_notifications", "读取通知", "变量名", listOf(TriggerDescriptor.Field.TEXT)) {
                GetNotificationsAction(it.id, variableName = it.first)
            },
            EntrySpec(
                "get_light",
                "读取光线",
                "变量名",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { GetLightLevelAction(it.id, variableName = it.first) },
            EntrySpec("sensor_read", "读取传感器", "传感器名 变量名", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT)) {
                SensorReadAction(it.id, sensor = it.first, variableName = it.second)
            },
            EntrySpec("connectivity", "网络连通检查", "主机 变量名", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT)) {
                ConnectivityCheckAction(it.id, host = it.first, variableName = it.second)
            },
            EntrySpec("confirm", "确认动作", "提示内容", listOf(TriggerDescriptor.Field.TEXT)) { ConfirmNextAction(it.id, message = it.first) },
            EntrySpec("shell", "执行脚本", "脚本内容", listOf(TriggerDescriptor.Field.TEXT)) { ShellScriptAction(it.id, script = it.first) },
            EntrySpec("secure_setting", "安全设置", "键 值", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT)) {
                SecureSettingsAction(it.id, key = it.first, value = it.second)
            },
            EntrySpec("system_setting", "系统设置", "键 值", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT)) {
                SystemSettingAction(it.id, key = it.first, value = it.second)
            },
            EntrySpec("reboot", "重启设备", "", emptyList()) { RebootAction(it.id) },
            EntrySpec("qr", "生成二维码", "内容 路径", listOf(TriggerDescriptor.Field.TEXT, TriggerDescriptor.Field.TEXT)) {
                GenerateQRCodeAction(it.id, content = it.first, path = it.second)
            },
            EntrySpec("icon", "设置应用图标", "图标名", listOf(TriggerDescriptor.Field.TEXT)) { SetMacroDroidIconAction(it.id, icon = it.first) },
            EntrySpec(
                "javascript",
                "JavaScript",
                "脚本内容",
                listOf(TriggerDescriptor.Field.TEXT),
            ) { JavaScriptAction(it.id, script = it.first) },
        )

    fun indexOfAction(action: Action): Int = entries.indexOfFirst { matches(it, action) }

    private fun matches(
        spec: EntrySpec,
        action: Action,
    ): Boolean =
        when (action) {
            is NotifyAction -> spec.key == "notify"
            is OpenUrlAction -> spec.key == "open_url"
            is SendDataSmsAction -> spec.key == "send_data_sms"
            is SendSMSAction -> spec.key == "send_sms"
            is SendEmailAction -> spec.key == "send_email"
            is ForwardSMSAction -> spec.key == "forward_sms"
            is MakeCallAction -> spec.key == "make_call"
            is AnswerCallAction -> spec.key == "answer_call"
            is RejectCallAction -> spec.key == "reject_call"
            is SpeakerPhoneAction -> spec.key == "speaker_phone"
            is MuteMicrophoneAction -> spec.key == "mute_mic"
            is RecordMicrophoneAction -> spec.key == "record_mic"
            is ReadSoundLevelAction -> spec.key == "read_sound"
            is SetVariableAction -> spec.key == "set_variable"
            is ClearVariablesAction -> spec.key == "clear_variables"
            is SetDictionaryEntryAction -> spec.key == "set_dict_entry"
            is ClearDictionaryEntryAction -> spec.key == "clear_dict_entry"
            is IterateDictionaryAction -> spec.key == "iterate_dict"
            is IterateDictionaryEndAction -> spec.key == "iterate_dict_end"
            is SetArrayEntryAction -> spec.key == "set_array_entry"
            is IfConditionAction -> spec.key == "if_condition"
            is ElseConditionAction -> spec.key == "else_condition"
            is EndIfAction -> spec.key == "end_if"
            is WaitAction -> spec.key == "wait"
            is WaitUntilTriggerAction -> spec.key == "wait_trigger"
            is RepeatStartAction -> spec.key == "repeat_start"
            is RepeatEndAction -> spec.key == "repeat_end"
            is BreakFromLoopAction -> spec.key == "break"
            is ContinueLoopAction -> spec.key == "continue"
            is GotoAction -> spec.key == "goto"
            is MutexAction -> spec.key == "mutex"
            is MutexEndAction -> spec.key == "mutex_end"
            is CancelActiveMacroAction -> spec.key == "stop_macro"
            is PauseAction -> spec.key == "pause"
            is LogAction -> spec.key == "log"
            is ClearLogAction -> spec.key == "clear_log"
            is ExportLogAction -> spec.key == "export_log"
            is LaunchAppAction -> spec.key == "launch_app"
            is LaunchActivityAction -> spec.key == "launch_activity"
            is LaunchHomeScreenAction -> spec.key == "launch_home"
            is CloseApplicationAction -> spec.key == "close_app"
            is KillBackgroundAppAction -> spec.key == "kill_app"
            is PressBackAction -> spec.key == "press_back"
            is InputKeyEventAction -> spec.key == "input_key"
            is TouchScreenAction -> spec.key == "touch"
            is ToastAction -> spec.key == "toast"
            is PopUpAction -> spec.key == "popup"
            is MessageDialogAction -> spec.key == "message_dialog"
            is OptionDialogAction -> spec.key == "option_dialog"
            is SelectionDialogAction -> spec.key == "selection_dialog"
            is FloatingTextAction -> spec.key == "floating_text"
            is FloatingButtonConfigureAction -> spec.key == "floating_button"
            is ClipboardAction -> spec.key == "clipboard"
            is UpdateClipboardAction -> spec.key == "update_clipboard"
            is ShareTextAction -> spec.key == "share_text"
            is ShareLocationAction -> spec.key == "share_location"
            is TextManipulationAction -> spec.key == "text_manipulation"
            is EncryptDecryptTextAction -> spec.key == "encrypt"
            is JsonParseAction -> spec.key == "json_parse"
            is SendIntentAction -> spec.key == "send_intent"
            is HttpRequestAction -> spec.key == "http_request"
            is UDPCommandAction -> spec.key == "udp"
            is WriteToFileAction -> spec.key == "write_file"
            is ReadFileAction -> spec.key == "read_file"
            is FileOperationAction -> spec.key == "file_operation"
            is FileSelectionAction -> spec.key == "pick_file"
            is SetVolumeAction -> spec.key == "set_volume"
            is VolumeIncrementDecrementAction -> spec.key == "volume_adjust"
            is SetBrightnessAction -> spec.key == "set_brightness"
            is DimScreenAction -> spec.key == "dim_screen"
            is SetScreenTimeoutAction -> spec.key == "screen_timeout"
            is SetAutoRotateAction -> spec.key == "auto_rotate"
            is ForceScreenRotationAction -> spec.key == "force_rotation"
            is SetRingerModeAction -> spec.key == "ringer_mode"
            is SetVibrateAction -> spec.key == "vibrate_on"
            is SilentModeAction -> spec.key == "silent_mode"
            is VibrateAction -> spec.key == "vibrate"
            is SpeakTextAction -> spec.key == "speak"
            is PlaySoundAction -> spec.key == "play_sound"
            is SetWifiAction -> spec.key == "wifi"
            is SetBluetoothAction -> spec.key == "bluetooth"
            is SetAirplaneModeAction -> spec.key == "airplane"
            is SetDataAction -> spec.key == "mobile_data"
            is SetHotspotAction -> spec.key == "hotspot"
            is SetGPSAction -> spec.key == "gps"
            is SetNFCAction -> spec.key == "nfc"
            is SetAutoSyncAction -> spec.key == "auto_sync"
            is CameraFlashLightAction -> spec.key == "torch"
            is TakeScreenshotAction -> spec.key == "screenshot"
            is ScreenOnAction -> spec.key == "screen_on"
            is KeepAwakeAction -> spec.key == "keep_awake"
            is ImmersiveModeAction -> spec.key == "immersive"
            is ExpandCollapseStatusBarAction -> spec.key == "status_bar"
            is DarkThemeAction -> spec.key == "dark_theme"
            is BatterySaverAction -> spec.key == "battery_saver"
            is HeadsUpNotificationsAction -> spec.key == "heads_up"
            is ClearNotificationsAction -> spec.key == "clear_notifications"
            is ConfigureAppNotificationsAction -> spec.key == "configure_notifications"
            is ControlMediaAction -> spec.key == "media_control"
            is EnableTriggerAction -> spec.key == "enable_trigger"
            is DisableTriggerAction -> spec.key == "disable_trigger"
            is ForceMacroRunAction -> spec.key == "force_macro"
            is DisableMacroAction -> spec.key == "disable_macro"
            is SetModeAction -> spec.key == "set_mode"
            is SetWallpaperAction -> spec.key == "set_wallpaper"
            is SetRingtoneAction -> spec.key == "set_ringtone"
            is SetNotificationSoundAction -> spec.key == "set_notify_sound"
            is SetLanguageAction -> spec.key == "set_language"
            is SetAlarmClockAction -> spec.key == "alarm"
            is AddCalendarEntryAction -> spec.key == "add_calendar"
            is ClearCallLogAction -> spec.key == "clear_call_log"
            is DeleteSMSAction -> spec.key == "delete_sms"
            is GetContactsAction -> spec.key == "get_contacts"
            is GetInstalledAppsAction -> spec.key == "get_apps"
            is GetNotificationsAction -> spec.key == "get_notifications"
            is GetLightLevelAction -> spec.key == "get_light"
            is SensorReadAction -> spec.key == "sensor_read"
            is ConnectivityCheckAction -> spec.key == "connectivity"
            is ConfirmNextAction -> spec.key == "confirm"
            is ShellScriptAction -> spec.key == "shell"
            is SecureSettingsAction -> spec.key == "secure_setting"
            is SystemSettingAction -> spec.key == "system_setting"
            is RebootAction -> spec.key == "reboot"
            is GenerateQRCodeAction -> spec.key == "qr"
            is SetMacroDroidIconAction -> spec.key == "icon"
            is JavaScriptAction -> spec.key == "javascript"
            else -> false
        }
}

fun parseHexText(value: String): ByteArray {
    val clean = value.removePrefix("0x").filterNot { it.isWhitespace() }
    if (clean.isEmpty() || clean.length % 2 != 0) return ByteArray(0)
    return clean.chunked(2).mapNotNull { it.toIntOrNull(16)?.toByte() }.toByteArray()
}
