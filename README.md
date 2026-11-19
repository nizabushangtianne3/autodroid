# AutoDroid

独立开源的 Android 自动化应用，提供由触发器、规则与动作组成的自动化模型。项目不复制、不反编译、不引用 MacroDroid 或其他专有应用的代码与资源；实现全部基于公开 Android API。

## 当前模块

- `engine`：纯 JVM 自动化核心，包含触发器事件匹配、规则注册与动作执行模型。
- `app`：Android 应用外壳，提供规则列表、创建、编辑、删除、启用/禁用，以及系统事件入口。

## 当前功能

### 触发器

- **Link**：应用可作为链接处理器，按 host 匹配后派发事件。
- **App**：通过无障碍服务监听应用前台窗口变化；已过滤 AutoDroid 自身，并对同一包名做 1 秒防抖。编辑时可从已安装应用自动补全，候选显示 `应用名 • 包名`，选中后只保存包名。
- **Data SMS**：按规则中的目标端口动态注册/注销系统数据短信接收器；可选匹配发送方。

### 动作

- **Notify**：创建系统通知，Android 13+ 会在缺少 `POST_NOTIFICATIONS` 权限时明确失败。
- **Open URL**：通过系统 VIEW Intent 打开链接。
- **Send Data SMS**：通过 `SmsManager.sendDataMessage` 发送端口短信，兼容 Android 12+ 与旧版本 API。

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

```bash
git tag v0.1.0
git push origin v0.1.0
```

## 法律与设计边界

本仓库是独立实现。所有名称、接口、数据结构与代码均为原创；不包含 MacroDroid 的 APK、DEX、资源、图标、文案或反编译产物。若要增加与特定商业应用相似的系统能力，请依据 Android 公开文档设计并自行测试。
