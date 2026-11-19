# AutoDroid

独立开源的 Android 自动化应用，提供由触发器、规则与动作组成的自动化模型。项目不复制、不反编译、不引用 MacroDroid 或其他专有应用的代码与资源；实现全部基于公开 Android API。

## 当前模块

- `engine`：纯 JVM 自动化核心，包含触发器事件匹配、规则注册与动作执行模型。
- `app`：Android 应用外壳，提供规则列表、创建、编辑、删除、启用/禁用，以及系统事件入口。

## 当前功能

### 触发器

- **Link**：应用可作为链接处理器，按 host 匹配后派发事件。
- **App**：通过无障碍服务监听应用前台窗口变化；已过滤 AutoDroid 自身，并对同一包名做 1 秒防抖。
- **Data SMS**：按规则中的目标端口动态注册/注销系统数据短信接收器；可选匹配发送方。

### 动作

- **Notify**：创建系统通知，Android 13+ 会在缺少 `POST_NOTIFICATIONS` 权限时明确失败。
- **Open URL**：通过系统 VIEW Intent 打开链接。
- **Send Data SMS**：通过 `SmsManager.sendDataMessage` 发送端口短信，兼容 Android 12+ 与旧版本 API。

### 应用管理

- 规则本地 JSON 持久化。
- 可创建、编辑、删除规则。
- 可启用或禁用规则；禁用规则不会派发动作。
- 触发类型切换时，字段提示动态更新。
- Android 启动时请求通知与短信权限。

## 构建

```bash
./gradlew :engine:test
./gradlew :app:assembleDebug
```

项目使用 Kotlin、Android Gradle Plugin 与 Gradle Wrapper。CI 使用 JDK 21。

## 法律与设计边界

本仓库是独立实现。所有名称、接口、数据结构与代码均为原创；不包含 MacroDroid 的 APK、DEX、资源、图标、文案或反编译产物。若要增加与特定商业应用相似的系统能力，请依据 Android 公开文档设计并自行测试。
