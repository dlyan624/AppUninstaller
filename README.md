# 垃圾应用清理器 (App Uninstaller)

一款功能强大的 Android 应用卸载工具，支持白名单管理、批量卸载等功能。

## 功能特性

### 核心功能
1. **自动扫描** - 自动识别手机中所有已安装的第三方应用
2. **白名单管理** - 用户可自由添加/移除应用到白名单
3. **持久化存储** - 白名单数据自动保存，重启应用后依然保留
4. **一键卸载** - 批量卸载所有不在白名单中的选中应用
5. **智能保护** - 白名单中的应用不会被误删

### 详细功能说明

#### 1. 应用列表展示
- 显示所有已安装的第三方应用（排除系统应用）
- 展示应用图标、名称、包名信息
- 按字母顺序排序

#### 2. 选择操作
- **全选**：一键选中所有非白名单应用
- **取消全选**：取消所有选择
- **单独选择**：通过复选框选择单个应用

#### 3. 白名单管理
- 点击每个应用右侧的按钮可将应用加入/移出白名单
- 白名单中的应用会显示锁定图标
- 加入白名单的应用会自动取消选中状态
- 白名单数据保存在本地 SharedPreferences 中

#### 4. 保存功能
- 点击"保存白名单"按钮保存当前白名单配置
- 数据持久化存储，不会丢失

#### 5. 一键卸载
- 点击"一键卸载全部"按钮
- 系统会弹出确认对话框，显示待卸载数量
- 仅卸载已选中且不在白名单中的应用
- 每个应用都会调用系统标准卸载流程

## 项目结构

```
AppUninstaller/
├── app/
│   ├── build.gradle                    # 应用构建配置
│   └── src/main/
│       ├── AndroidManifest.xml         # 应用清单文件
│       ├── java/com/cleaner/appuninstaller/
│       │   ├── MainActivity.java       # 主界面Activity
│       │   ├── AppInfo.java            # 应用信息数据模型
│       │   ├── AppAdapter.java         # RecyclerView适配器
│       │   ├── AppScanner.java         # 应用扫描器
│       │   └── WhitelistManager.java   # 白名单管理器
│       └── res/
│           ├── layout/
│           │   ├── activity_main.xml   # 主界面布局
│           │   └── item_app.xml        # 应用列表项布局
│           └── values/
│               ├── strings.xml         # 字符串资源
│               └── themes.xml          # 主题样式
├── build.gradle                        # 项目级构建配置
├── settings.gradle                     # 项目设置
└── gradle.properties                   # Gradle属性配置
```

## 技术实现

### 1. 应用扫描 (AppScanner)
```java
// 使用 PackageManager 获取已安装应用
PackageManager pm = context.getPackageManager();
List<PackageInfo> packages = pm.getInstalledPackages(0);

// 过滤第三方应用（非系统应用）
if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
    // 收集应用信息
}
```

### 2. 白名单管理 (WhitelistManager)
- 使用 SharedPreferences 存储白名单包名集合
- 支持增删查操作
- 提供获取非白名单应用列表的方法

### 3. 卸载机制
```java
// 调用系统卸载界面
Intent intent = new Intent(Intent.ACTION_DELETE, 
    Uri.parse("package:" + packageName));
startActivity(intent);
```

## 权限说明

- `REQUEST_DELETE_PACKAGES` - 请求删除应用权限
- `QUERY_ALL_PACKAGES` - 查询所有已安装应用（Android 11+）

## 使用流程

1. **首次启动**
   - 应用自动扫描所有已安装的第三方应用
   - 显示应用列表和统计信息

2. **管理白名单**
   - 浏览应用列表
   - 点击应用右侧按钮将重要应用加入白名单
   - 白名单应用显示锁定图标

3. **选择要卸载的应用**
   - 勾选需要卸载的应用（白名单中的无法勾选）
   - 可使用全选/取消全选快速操作

4. **保存配置**
   - 点击"保存白名单"保存当前设置

5. **执行卸载**
   - 点击"一键卸载全部"
   - 确认卸载操作
   - 系统逐个打开卸载页面

## 安全提示

⚠️ **重要提醒**：
- 本工具仅用于卸载您明确选择的应用
- 请务必将重要应用加入白名单
- 卸载操作不可撤销，请谨慎操作
- 建议在执行大批量卸载前备份重要数据

## 编译要求

- Android Studio Hedgehog | 2023.1.1 或更高版本
- Gradle 8.2+
- compileSdk: 34
- minSdk: 24 (Android 7.0)
- targetSdk: 34

## 依赖库

- AndroidX AppCompat 1.6.1
- Material Components 1.11.0
- ConstraintLayout 2.1.4

## 开发环境搭建

1. 克隆或下载本项目到本地
2. 用 Android Studio 打开项目
3. 等待 Gradle 同步完成
4. 连接 Android 设备或启动模拟器
5. 点击 Run 运行应用

## 扩展功能建议

未来可以考虑添加的功能：
- [ ] 按应用大小排序
- [ ] 按安装时间筛选
- [ ] 批量导出/导入白名单
- [ ] 云端同步白名单
- [ ] 应用分类管理（游戏、工具、社交等）
- [ ] 定期清理提醒
- [ ] 卸载历史记录

## 许可证

MIT License

## 作者

App Uninstaller Development Team

## 版本历史

- v1.0.0 (2024-01) - 初始版本发布
  - 基础扫描功能
  - 白名单管理
  - 一键卸载功能
