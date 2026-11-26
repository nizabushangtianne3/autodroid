# AutoDroid

独立开源的 Android 自动化应用，提供由触发器、规则与动作组成的自动化模型。项目不复制、不反编译、不引用 MacroDroid 或其他专有应用的代码与资源；实现全部基于公开 Android API。

## 当前模块

- `engine`：纯 JVM 自动化核心，包含触发器事件匹配、规则注册、全局变量（标量/字典/数组）与动作执行模型。
- `app`：Android 应用外壳，提供规则列表、创建、编辑、删除、启用/禁用，以及系统事件入口。

## 当前功能

### 触发器

- **Link**：应用可作为链接处理器，按 host 匹配后派发事件。
- **App**：通过无障碍服务监听应用前台窗口变化；已过滤 AutoDroid 自身，并对同一包名做 1 秒防抖。编辑时可从已安装应用自动补全，候选显示 `应用名 • 包名`，选中后只保存包名。
- **Data SMS**：按规则中的目标端口动态注册/注销系统数据短信接收器；可选匹配发送方。
- **Schedule**：`AlarmManager` 精确周期触发，最小间隔 1000ms。
- **Screen On/Off**：屏幕点亮或熄灭。
- **Battery Level**：电量高于/低于指定百分比（0-100）。
- **Airplane Mode**：飞行模式开/关。
- **Bluetooth**：蓝牙开/关。
- **External Power**：电源连接/断开。
- **Headphones**：耳机插入/拔出。
- **Hotspot**：热点开/关。
- **Silent Mode**：静音/振动模式。
- **WiFi Connection**：WiFi 连接/断开。
- **WiFi SSID**：按 SSID 匹配。
- **Signal On/Off**：蜂窝信号开/关。
- **Dock**：底座状态变化。
- **VPN**：VPN 连接/断开。
- **GPS**：GPS 开/关。

## 规模

| 维度 | 数量 |
| --- | --- |
| 触发器模型 | 113 |
| 触发器可选项（编辑器目录） | 83 |
| 动作模型 | 138 |
| 动作可选项（编辑器目录） | 81 |
| engine 单元测试 | 64 |

所有模型均通过 `kotlinx.serialization` 往返序列化验证；编辑器目录与模型双向绑定，
保证每个可选项都能构建出对应模型、每个模型都能被目录反向识别（见 `CatalogCoverageTest`）。

除了上面这些「直接派发」触发器，完整清单还覆盖：设备开关与状态、连接变化、
传感器（光线/距离/折叠角度）、通话与短信、时间与位置（含地理围栏、日出日落）、
界面交互（点击/滑动/悬浮文本）、系统与应用生命周期、变量变化、Webhook 与 HTTP 服务等。
触发器与动作的完整可编辑目录见 `engine/.../TriggerCatalog.kt` 与 `engine/.../ActionCatalog.kt`。

### 动作

- **Notify**：创建系统通知，Android 13+ 会在缺少 `POST_NOTIFICATIONS` 权限时明确失败。
- **Open URL**：通过系统 VIEW Intent 打开链接。
- **Send Data SMS**：通过 `SmsManager.sendDataMessage` 发送端口短信，兼容 Android 12+ 与旧版本 API。
- **Set Variable**：设置变量，供后续条件和动作引用。
- **If / Else / End If**：条件分支，表达式支持 `{变量}` 引用和数值/字符串比较（`== != >= <= > <`）。
- **Wait**：等待指定毫秒。

### 应用管理

- 规则本地 JSON 持久化。
- 可创建、编辑、删除规则。
- 可启用或禁用规则；禁用规则不会派发动作。
- 规则列表显示触发类型、触发值、动作类型、动作摘要和禁用状态。
- 触发类型切换时，字段提示动态更新。
- Android 启动时请求通知与短信权限。
- 触发和动作字段保存前会做必填与格式校验。
- 支持通过系统文件选择器导出/导入 JSON；导入按规则 ID 合并，同 ID 覆盖、不同 ID 追加。
- Link / App / Data SMS 触发执行后会发布结果通知，显示匹配规则数、每个动作成功或失败状态和错误信息；点击通知可打开应用。

## 变量系统

规则条件表达式支持 `{变量名}` 引用，动作执行前会自动解析。变量来源：

1. `Set Variable` 动作显式设置。
2. 触发器派发时自动注入上下文变量。

可用触发器变量：

| 触发器 | 变量 |
| --- | --- |
| Link | `{link_host}` `{link_scheme}` `{link_path}` |
| App | `{app_package}` |
| Data SMS | `{sms_sender}` `{sms_port}` |
| Screen On/Off | `{screen_on}` |
| Battery Level | `{battery_level}` |
| Airplane Mode | `{airplane_mode}` |
| Bluetooth | `{bluetooth_on}` |
| External Power | `{power_connected}` |
| Headphones | `{headphones_connected}` |
| Hotspot | `{hotspot_on}` |
| Silent Mode | `{silent_mode}` |
| WiFi | `{wifi_connected}` `{wifi_ssid}` |
| Signal | `{signal_on}` |
| Dock | `{dock_state}` |
| VPN | `{vpn_connected}` |
| GPS | `{gps_enabled}` |

条件示例：`{battery_level} <= 20`、`{app_package} == com.example.app`、`{wifi_connected} == true`

### 逻辑运算符

条件表达式支持 `&&`（与）和 `||`（或）组合多个比较，`&&` 优先级高于 `||`：

```
{battery_level} <= 20 && {power_connected} == false
{app_package} == com.example.app || {app_package} == com.example.app2
```

### 执行日志

每次规则派发都会记录执行日志（上限 100 条，超出自动淘汰最旧记录）：

- 时间戳
- 匹配规则数
- 每个动作的成功/失败状态和错误消息

通过 `AutomationEngine.executionLog()` 获取。

### Repeat 重复块

`Repeat Start` / `Repeat End` 动作对中间的动作块按指定次数循环执行。循环内 `Set Variable` 支持 `{loop_index}` 占位符（从 1 递增）：

```
Repeat Start (count = 3)
  Set Variable: index = {loop_index}
Repeat End
```

## 构建

```bash
./gradlew :engine:test
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleDebug
```

## 测试与代码质量

- `engine` 使用 Kotlin Test 覆盖规则匹配、启用/禁用、编辑替换与 JSON 序列化。
- `app` 使用 Robolectric 覆盖动态 Data SMS 端口注册和注销。
- `RuleEditorLogic` 是无 Android 依赖的表单校验纯逻辑，并有单元测试覆盖三类触发和三类动作。
- 项目使用 ktlint；CI 执行 `ktlintCheck`、`engine` 测试、Android 单元测试和 debug APK 构建。

项目使用 Kotlin、Android Gradle Plugin 与 Gradle Wrapper。CI 使用 JDK 21。

## 发布

推送 `v*` 格式的 tag 会触发 GitHub Actions：

1. 执行 ktlint、engine 测试、Android 单元测试。
2. 构建并上传 signed debug APK 与 release APK（release 当前使用 debug 签名，便于本地安装测试）。
3. 创建对应的 GitHub Release 并附加 `AutoDroid-<tag>-release.apk` 和 `AutoDroid-<tag>-debug.apk`。

Release APK 使用仓库 Secrets 中配置的专用 keystore 签名：

- `AUTODROID_RELEASE_STORE_BASE64`
- `AUTODROID_RELEASE_STORE_PASSWORD`
- `AUTODROID_RELEASE_KEY_ALIAS`
- `AUTODROID_RELEASE_KEY_PASSWORD`

Secrets 未配置时，CI 无法签名 release APK；本地开发可回退 debug 签名。

```bash
git tag v0.1.0
git push origin v0.1.0
```

## 法律与设计边界

本仓库是独立实现。所有名称、接口、数据结构与代码均为原创；不包含 MacroDroid 的 APK、DEX、资源、图标、文案或反编译产物。若要增加与特定商业应用相似的系统能力，请依据 Android 公开文档设计并自行测试。
