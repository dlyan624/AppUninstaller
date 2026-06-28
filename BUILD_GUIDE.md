# APK 编译完整指南

## 📋 环境要求检查清单

在开始编译前，请确保您的开发环境满足以下条件：

### 必需软件
- [x] **JDK 17 或更高版本** (推荐 Eclipse Temurin / Adoptium)
- [x] **Android Studio Hedgehog (2023.1.1) 或更新版本**
- [x] **Android SDK Build-Tools 34.0.0**
- [x] **Android SDK Platform API 34**
- [x] **Gradle 8.2** (项目会自动下载)

### 可选工具
- [ ] Git（用于版本控制）
- [ ] 一台 Android 真机或模拟器（用于测试）

---

## 🔧 方案一：使用 Android Studio 编译（推荐）

### 步骤 1：安装 Android Studio

1. 访问官网下载：https://developer.android.com/studio
2. 运行安装程序
3. 安装时选择 **Standard** 安装类型
4. 完成后启动 Android Studio

### 步骤 2：配置 SDK

1. 打开 Android Studio
2. 点击 **More Actions** → **SDK Manager**
3. 在 **SDK Platforms** 标签页中：
   - ✅ 勾选 **Android 14.0 (API 34)**
   - 点击 **Apply** 并等待下载完成
4. 在 **SDK Tools** 标签页中：
   - ✅ 勾选 **Android SDK Build-Tools 34.0.0**
   - ✅ 勾选 **Android SDK Command-line Tools (latest)**
   - ✅ 勾选 **Android SDK Platform-Tools**
   - 点击 **Apply** 并等待安装

### 步骤 3：打开项目

1. 启动 Android Studio
2. 选择 **Open** → 导航到 `AppUninstaller` 文件夹
3. 点击 **OK**
4. 等待 Gradle 同步完成（首次可能需要 5-10 分钟）

> ⚠️ **注意**：如果提示 Gradle 版本不匹配，点击对话框中的 **Update Gradle** 按钮

### 步骤 4：构建 Release APK

#### 方法 A：通过菜单（图形界面）
```
菜单栏 → Build → Build Bundle(s) / APK(s) → Build APK(s)
```

#### 方法 B：通过快捷键
```
按 F7 键（或 Shift+F10 选择 build APK）
```

#### 方法 C：使用终端
```
1. 底部栏点击 "Terminal" 标签
2. 输入命令：
   ./gradlew assembleRelease    (Mac/Linux)
   gradlew.bat assembleRelease   (Windows)
3. 按回车执行
```

### 步骤 5：找到生成的 APK

构建成功后，APK 文件位于：

```
AppUninstaller/app/build/outputs/apk/release/
└── app-release.apk          ← 这就是你要的文件！
```

**文件大小**：约 3-6 MB（取决于混淆和资源优化）

---

## 💻 方案二：命令行编译（高级用户）

### 前提条件
已安装 JDK 和 Android SDK，并配置好环境变量。

### Windows 用户

```batch
# 1. 打开 CMD 或 PowerShell
# 2. 进入项目目录
cd C:\path\to\AppUninstaller

# 3. 运行构建脚本（双击或执行）
build-apk.bat

# 或者手动执行：
gradlew.bat assembleRelease
```

### Mac/Linux 用户

```bash
# 1. 打开终端
# 2. 进入项目目录
cd /path/to/AppUninstaller

# 3. 赋予执行权限
chmod +x build-apk.sh gradlew

# 4. 运行构建脚本
./build-apk.sh

# 或者手动执行：
./gradlew assembleRelease
```

---

## 🌐 方案三：在线编译（无需本地环境）

如果不想安装 Android Studio，可以使用以下在线服务：

### 选项 1：GitHub Actions（推荐）

1. 将项目上传到 GitHub
2. 创建 `.github/workflows/build.yml` 文件
3. 推送代码后自动构建
4. 从 Artifacts 下载 APK

### 选项 2：Cloud Build Services

- **Codemagic**: https://codemagic.io/
- **Bitrise**: https://www.bitrise.io/
- **AppCenter**: https://appcenter.ms/

---

## ⚡ 快速编译流程图

```
┌─────────────────────────────────────┐
│         准备工作（一次性）            │
│  • 安装 JDK 17+                     │
│  • 安装 Android Studio              │
│  • 下载 SDK API 34                  │
└──────────────┬──────────────────────┘
               ▼
┌─────────────────────────────────────┐
│        打开项目                      │
│  File → Open → 选择 AppUninstaller  │
│  等待 Gradle Sync 完成              │
└──────────────┬──────────────────────┘
               ▼
┌─────────────────────────────────────┐
│        构建 APK                     │
│  Build → Build APK(s)              │
│  或运行: gradlew assembleRelease    │
└──────────────┬──────────────────────┘
               ▼
┌─────────────────────────────────────┐
│        获取 APK                     │
│  app/build/outputs/apk/release/     │
│       └─ app-release.apk           │
└─────────────────────────────────────┘
```

---

## ❓ 常见问题排查

### Q1: Gradle 同步失败
**症状**：显示 "Failed to resolve" 错误

**解决方案**：
```bash
# 方法1：清除缓存后重试
File → Invalidate Caches → Invalidate and Restart

# 方法2：手动刷新依赖
./gradlew --refresh-dependencies assembleRelease
```

### Q2: 提示 JDK 版本错误
**症状**：显示 "Unsupported class file major version"

**解决方案**：
1. File → Project Structure → SDK Location
2. 确保 JDK 路径指向 JDK 17+
3. 或设置 JAVA_HOME 环境变量

### Q3: 找不到 SDK
**症状**：显示 "SDK location not found"

**解决方案**：
1. 设置环境变量 ANDROID_HOME
2. Windows: `C:\Users\你的用户名\AppData\Local\Android\Sdk`
3. Mac: `~/Library/Android/sdk`
4. Linux: `~/Android/Sdk`

### Q4: 构建成功但找不到 APK
**解决方案**：
```bash
# 手动查找
dir app\build\outputs\apk\release\*.apk /s    # Windows
find . -name "*.apk" -type f                   # Mac/Linux
```

### Q5: 签名警告
**现象**：提示 "APK not signed"

**说明**：Debug/Release 未签名 APK 可以正常安装到手机，只是无法上架 Google Play

**如需签名**：
1. 生成签名密钥：`keytool -genkey -v -keystore my-key.jks`
2. 在 `app/build.gradle` 中配置 signingConfigs

---

## 📱 安装 APK 到手机

### 方法 1：USB 数据线
1. 手机开启 **开发者选项** → **USB 调试**
2. USB 连接电脑
3. 将 APK 复制到手机存储
4. 在手机文件管理器中点击安装

### 方法 2：ADB 命令
```bash
adb install app-release.apk
```

### 方法 3：无线传输
- 使用微信/QQ/网盘发送 APK 到手机
- 浏览器直接下载

### 方法 4：第三方安装器
- 使用应用宝、豌豆荚等工具安装

---

## 🔐 关于 APK 签名（进阶）

### Debug 版本（默认）
- 使用 debug 密钥自动签名
- 仅用于开发测试
- 有效期 1 年
- 不同电脑生成的密钥不同

### Release 版本（正式发布）
需要您自己的签名密钥：

```bash
# 生成签名密钥（只需一次）
keytool -genkeypair -v \
  -keystore release-key.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias my-app-key

# 配置 build.gradle（添加到 android {} 内部）
android {
    signingConfigs {
        release {
            storeFile file("release-key.jks")
            storePassword "你的密码"
            keyAlias "my-app-key"
            keyPassword "你的密码"
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

---

## 📊 构建时间参考

| 操作 | 首次构建 | 后续增量构建 |
|------|---------|-------------|
| Gradle Sync | 3-8 分钟 | < 30 秒 |
| Debug APK | 1-3 分钟 | 10-30 秒 |
| Release APK | 2-5 分钟 | 30 秒-2 分钟 |

*时间取决于电脑性能和网络速度*

---

## ✅ 编译成功验证清单

构建完成后，请确认以下内容：

- [ ] 终端显示 **BUILD SUCCESSFUL**
- [ ] `app/build/outputs/apk/release/` 目录存在
- [ ] `app-release.apk` 文件大小 > 1 MB
- [ ] 可以将 APK 传输到 Android 手机
- [ ] 手机上可以正常安装（需允许"未知来源"）
- [ ] 安装后可以正常打开应用
- [ ] 应用可以扫描到已安装的应用列表

---

## 🎯 下一步操作

APK 编译成功后：

1. **安装测试** - 在真机上安装并全面测试功能
2. **功能验证** - 测试白名单、卸载等核心功能
3. **问题反馈** - 如有问题请记录详细复现步骤
4. **分发分享** - 可以将 APK 分发给他人使用

---

## 📞 技术支持

如果在编译过程中遇到问题：

1. **查看日志**：Build 输出窗口中的详细错误信息
2. **搜索报错**：复制错误信息到 Google/Stack Overflow
3. **清理重建**：Build → Clean Project → Rebuild Project
4. **检查网络**：确保能访问 Google Maven 仓库（国内可能需要代理）

---

**祝您编译顺利！🚀**

如有任何问题，请随时查阅本文档或寻求帮助。
