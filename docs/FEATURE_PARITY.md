# Feature Parity Roadmap

This document maps MacroDroid's public feature surface to AutoDroid's independent implementation plan. The mapping below was derived by static analysis of the official APK's public class inventory using REA + JADX headless. No proprietary code, resources, or assets are copied; every entry is implemented from scratch with public Android APIs.

## Analysis Source

- APK: MacroDroid v6.2.4 beta (arm64), official distribution
- Tool: `rea-agents` 6.9.0 + `jadx-headless-mcp` 0.7.1
- Evidence: `work/macro-droid-triggers.txt` (153 trigger classes), `work/macro-droid-actions.txt` (240 action classes)

## Trigger Parity

### Implemented

| MacroDroid Trigger | AutoDroid Status |
| --- | --- |
| ApplicationLaunchedTrigger | `AppLaunchedTrigger` (accessibility, self-filter, debounce) |
| DataSMSReceivedTrigger | `DataSmsReceivedTrigger` (dynamic port registration) |
| TimerTrigger | `ScheduleTrigger` (AlarmManager, exact alarms, minimum 1s) |
| EmptyTrigger | Planned (immediate dispatch) |

### Batch 1 — Implemented (device state, receiver-based)

AirplaneMode ✅, BatteryLevel ✅, Bluetooth ✅, Dock ✅, ExternalPower ✅, GPSEnabled ✅, Headphones ✅, Hotspot ✅, ScreenOnOff ✅, SilentMode ✅, WifiConnection ✅, WifiSSID ✅, SignalOnOff ✅, Vpn ✅`n`nRemaining: BatterySaver, BatteryTemperature, Boot, CellConnectionTypeChange, DataOnOff, DeviceUnlocked

### Planned — Batch 2 (user interaction, accessibility)

AccessibilityServiceState, AutoRotateChange, ClipboardChange, DarkTheme, DayDream, DeviceUnlocked, FailedLogin, FingerprintGesture, FlipDevice, FloatingButton, HomeButtonLongPress, MediaButton, Notification, ScreenContent, ShakeDevice, Swipe, UIClick, VolumeButton, VolumeLongPress

### Planned — Batch 3 (communication)

CallActive, CallEnded, CallMissed, IncomingCall, OutgoingCall, IncomingSMS, EmailReceived, DialNumber, CalendarTrigger, ToastTrigger

### Planned — Batch 4 (time / location / network)

Day, SunriseSunset, SystemTime, Geofence, LocationTriggerService, CellTower, Weather, WebHook, HttpServer, IntentReceived

### Planned — Batch 5 (advanced)

ActivityRecognition, AndroidWear, AppEnabled, ApplicationInstalledRemoved, AssistantActivated, AutoSyncChange, BluetoothBeacon, CameraFlashlight, CameraInUse, ClipboardChange, CustomSceneTrigger, DataUsed, DrawerOpenClose, FileChanged, FileOpened, FoldAngle, GoogleAssistant, InvokedByOverlay variants, NFC, SensorTrigger, ShortcutTrigger, SimChange, SleepTrigger, SpotifyTrigger, StopwatchTrigger, SystemSetting, SystemLogTrigger, USBTethering, UsbDeviceConnection, VariableTrigger, WidgetPressed, WorkProfile, SwipeTriggerService, WebTriggerApi

## Action Parity

### Implemented

| MacroDroid Action | AutoDroid Status |
| --- | --- |
| DisplayNotificationAction | `NotifyAction` |
| OpenUrlAction | `OpenUrlAction` |
| SendSMSAction (data variant) | `SendDataSmsAction` |

### Planned — Priority Order

1. SetVariable, IfCondition, Else, EndIf (logic core)
2. Wait, WaitUntilTrigger, DisableTrigger, EnableTrigger (control flow)
3. SetBrightness, SetVolume, SetAutoRotate, ToggleWifi, ToggleBluetooth, ToggleFlashlight (device settings)
4. LaunchApp, KillApp, Vibrate, SpeakText, PlaySound (interaction)
5. ShareLocation, SendEmail, SendSMS (communication)
6. CreateNotification, SetWallpaper, TurnScreenOn/Off (system)

## Constraint Parity

Planned: BatteryLevel, BatterySaver, Bluetooth, CellConnection, Day/Time, DeviceLocked/Unlocked, ExternalPower, GPSEnabled, Headphones, NetworkType, ScreenOnOff, SilentMode, TriggerEnabled, Variable, Wifi, AirplaneMode

Implemented model/runtime API: BatteryLevel ✅, BatterySaver ✅, Bluetooth ✅, CellConnection ✅, Day/Time ✅, DeviceLocked/Unlocked ✅, ExternalPower ✅, GPSEnabled ✅, Headphones ✅, NetworkType ✅, ScreenOnOff ✅, SilentMode ✅, TriggerEnabled ✅, Variable ✅, Wifi ✅, AirplaneMode ✅

Editor persistence and live Android `DeviceState` sampling remain planned for the next batch.

## Editor Parity Progress

- v0.1.9 aligned the shell around MacroDroid's bottom navigation and macro list card structure.
- Next batch adds the full-screen editor with red Trigger / blue Actions / green Constraints sections.
- Constraint editing now supports all 16 modeled constraints: BatteryLevel, BatterySaver, Bluetooth,
  CellConnection, Day/Time, DeviceLocked, ExternalPower, GPSEnabled, Headphones, NetworkType,
  ScreenOnOff, SilentMode, TriggerEnabled, Variable, Wifi, AirplaneMode.
- Live Android `DeviceState` sampling and multiple action editing remain next.

## Naming and Branding

AutoDroid uses its own class names, package namespace (`dev.niza.autodroid`), UI strings, and icons. Behavior parity does not imply code or asset parity. Every implementation is written from public Android API documentation.
