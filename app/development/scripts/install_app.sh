#!/usr/bin/env bash

# assert ANDROID_HOME is set
if [ -z "$ANDROID_HOME" ]; then
  echo "ANDROID_HOME is not set"
  exit 1
fi

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)
DEVELOPMENT_DIR=$SCRIPT_DIR/..
ROOT_PROJECT_DIR=$SCRIPT_DIR/../../..

EMULATOR_DEVICE_NAME=$($ANDROID_HOME/platform-tools/adb devices | grep emulator | cut -f1)

if [ -z "$EMULATOR_DEVICE_NAME" ]; then
  echo "Emulator device name not found"
  exit 1
fi

ADB="$ANDROID_HOME/platform-tools/adb -s $EMULATOR_DEVICE_NAME"

$ADB root
sleep 3      # wait for adb to restart
$ADB remount # remount /system as writable

echo "Installing aosp_backup app..."
$ADB shell mkdir -p /system/priv-app/AospBackup
$ADB shell rm -f /system/priv-app/AospBackup/AospBackup.apk
$ADB push $ROOT_PROJECT_DIR/app/build/outputs/apk/debug/app-debug.apk /system/priv-app/AospBackup/AospBackup.apk

echo "Installing aosp_backup permissions..."
$ADB push $ROOT_PROJECT_DIR/app/aosp-config/permissions_com.stevesoltys.aosp_backup.xml /system/etc/permissions/privapp-permissions-aosp_backup.xml
$ADB push $ROOT_PROJECT_DIR/app/aosp-config/allowlist_com.stevesoltys.aosp_backup.xml /system/etc/sysconfig/allowlist-aosp_backup.xml

$ADB shell am force-stop com.stevesoltys.aosp_backup
$ADB shell am broadcast -a android.intent.action.BOOT_COMPLETED

echo "Setting aosp_backup transport..."
$ADB shell bmgr transport com.stevesoltys.aosp_backup/.transport.AppBackupTransport
