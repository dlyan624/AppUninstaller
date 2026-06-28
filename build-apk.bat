@echo off
chcp 65001 >nul 2>&1
echo ========================================
echo   垃圾应用清理器 - APK 构建脚本
echo ========================================
echo.

:: 检查 Java
where java >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [错误] 未找到 Java/JDK
    echo 请安装 JDK 17 或更高版本: https://adoptium.net/
    pause
    exit /b 1
)

java -version 2>&1 | findstr "version"
echo.

:: 检查 Android SDK
if not defined ANDROID_HOME (
    if exist "%LOCALAPPDATA%\Android\Sdk" (
        set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
    ) else if exist "C:\Android\Sdk" (
        set "ANDROID_HOME=C:\Android\Sdk"
    )
)

if not defined ANDROID_HOME (
    echo [错误] 未找到 Android SDK
    echo 请安装 Android Studio: https://developer.android.com/studio
    pause
    exit /b 1
)

echo [信息] Android SDK 路径: %ANDROID_HOME%
echo.

:: 设置环境变量
set "JAVA_HOME="
for /f "delims=" %%i in ('where java') do (
    for %%j in ("%%i\..") do set "JAVA_HOME=%%~dpnj"
)
echo [信息] JAVA_HOME: %JAVA_HOME%

:: 创建 Gradle Wrapper（如果不存在）
if not exist "gradlew.bat" (
    echo [信息] 正在初始化 Gradle Wrapper...
    call gradle wrapper --gradle-version 8.2
)

echo.
echo [开始] 开始构建 Release APK...
echo ========================================

:: 构建 Release APK
call gradlew.bat assembleRelease

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo [成功] APK 构建成功！
    echo ========================================
    echo.
    echo 输出文件位置:
    dir /b app\build\outputs\apk\release\*.apk
    
    :: 复制到 output 目录
    if not exist "..\output" mkdir "..\output"
    copy /y app\build\outputs\apk\release\*.apk "..\output\"
    echo.
    echo [完成] APK 已复制到 output 目录！
) else (
    echo.
    echo [失败] 构建失败，请检查上方错误信息
)

echo.
pause
