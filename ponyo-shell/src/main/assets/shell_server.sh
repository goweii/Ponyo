adb push shell_server /data/local/tmp
adb shell rename /data/local/tmp/shell_server /data/local/tmp/shell_server.dex
adb shell nohup app_process -Djava.class.path=/data/local/tmp/shell_server.dex /system/bin --nice-name=ponyoshell per.goweii.ponyo.shell.server.ShellServer >/dev/null 2>&1 &