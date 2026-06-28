#!/bin/bash

# 垃圾应用清理器 - Linux/Mac 构建脚本

set -e

echo "========================================"
echo "  垃圾应用清理器 - APK 构建脚本 (Unix)"
echo "========================================"
echo ""

# 检查 Java
if ! command -v java &> /dev/null; then
    echo "[错误] 未找到 Java/JDK"
    echo "请安装 JDK 17 或更高版本"
    exit 1
fi

java -version 2>&1 | head -1
echo ""

# 检查 Android SDK
if [ -z "$ANDROID_HOME" ]; then
    if [ -d "$HOME/Library/Android/sdk" ]; then
        export ANDROID_HOME="$HOME/Library/Android/sdk"
    elif [ -d "$HOME/Android/Sdk" ]; then
        export ANDROID_HOME="$HOME/Android/Sdk"
    fi
fi

if [ -z "$ANDROID_HOME" ]; then
    echo "[错误] 未找到 Android SDK"
    echo "请设置 ANDROID_HOME 环境变量或安装 Android Studio"
    exit 1
fi

echo "[信息] Android SDK 路径: $ANDROID_HOME"
echo ""

# 设置环境变量
export PATH="$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/build-tools"

# 创建 Gradle Wrapper（如果不存在）
if [ ! -f "./gradlew" ]; then
    echo "[信息] 正在初始化 Gradle Wrapper..."
    gradle wrapper --gradle-version 8.2
fi

chmod +x gradlew

echo ""
echo "[开始] 开始构建 Release APK..."
echo "========================================"

# 构建 Release APK
./gradlew assembleRelease

if [ $? -eq 0 ]; then
    echo ""
    echo "========================================"
    echo "[成功] APK 构建成功！"
    echo "========================================"
    echo ""
    echo "输出文件位置:"
    ls -lh app/build/outputs/apk/release/*.apk
    
    # 复制到 output 目录
    mkdir -p ../output
    cp -f app/build/outputs/apk/release/*.apk ../output/
    echo ""
    echo "[完成] APK 已复制到 output 目录！"
else
    echo ""
    echo "[失败] 构建失败，请检查上方错误信息"
fi

echo ""
