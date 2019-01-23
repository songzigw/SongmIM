#!/bin/sh

# 1. 检测操作系统
cygwin=false
darwin=false
os400=false
hpux=false
case "`uname`" in
CYGWIN*) cygwin=true;;
Darwin*) darwin=true;;
OS400*)  os400=true;;
HP-UX*)  hpux=true;;
esac

# 2. 获取当前脚本所在的路径
PRG="$0"
cd `dirname "$PRG"`

# 判断是否为软链接
if [ -h $PRG ]; then
  ls=`ls -l "$PRG"`
  link=`expr "$ls" : '.*->\(.*\)$'`
  cd `dirname "$link"`
fi

# 保存当前脚本的绝对路径
PRGDIR=`pwd`

# 3. 返回当前脚本所在的上一级目录
cd ../
[ -z "$APP_HOME"] && APP_HOME=`pwd`

# --------------------------

if [ -r "$APP_HOME/bin/setenv.sh" ]; then
  . "$APP_HOME/bin/setenv.sh"
fi

# --------------------------
if $cygwin; then
[ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
[ -n "$JRE_HOME" ] && JRE_HOME=`cygpath --unix "$JRE_HOME"`
[ -n "$APP_HOME" ] && APP_HOME=`cygpath --unix "$APP_HOME"`
[ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# For OS400
if $os400; then
  # Set job priority to standard for interactive (interactive - 6) by using
  # the interactive priority - 6, the helper threads that respond to requests
  # will be running at the same priority as interactive jobs.
  COMMAND='chgjob job('$JOBNAME') runpty(6)'
  system $COMMAND

  # Enable multi threading
  export QIBM_MULTI_THREADED=Y
fi

# Get standard Java environment variables
if $os400; then
  # -r will Only work on the os400 if the files are:
  # 1. owned by the user
  # 2. owned by the PRIMARY group of the user
  # this will not work if the user belongs in secondary groups
  . "$APP_HOME"/bin/setclasspath.sh
else
  if [ -r "$APP_HOME"/bin/setclasspath.sh ]; then
    . "$APP_HOME"/bin/setclasspath.sh
  else
    echo "Cannot find $APP_HOME/bin/setclasspath.sh"
    echo "This file is needed to run this program"
    exit 1
  fi
fi

# Set CLASSPATH
if [ -z "$CLASSPATH" ]; then
  CLASSPATH="$APP_HOME/config"
else
  CLASSPATH="$CLASSPATH":"$APP_HOME/config"
fi

if [ -r "$APP_HOME/lib/" ]; then
  for i in "$APP_HOME"/lib/*.jar; do
    CLASSPATH="$CLASSPATH":"$i"
  done
fi

# When no TTY is available, don't output to console
have_tty=0
if [ "`tty`" != "not a tty" ]; then
have_tty=1
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  JRE_HOME=`cygpath --absolute --windows "$JRE_HOME"`
  APP_HOME=`cygpath --absolute --windows "$APP_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi

# only output this if we have a TTY
#if [ $have_tty -eq 1 ]; then
#echo "Using APP_HOME:   $APP_HOME"
#echo "Using JAVA_HOME:  $JAVA_HOME"
#echo "Using JRE_HOME:   $JRE_HOME"
#echo "Using CLASSPATH:  $CLASSPATH"
#fi

#需要启动的Java主程序（main方法类）
APP_MAINCLASS=cn.songm.im.server.Main


###################################
#(函数)判断程序是否已启动
#
#说明：
#使用JDK自带的JPS命令及grep命令组合，准确查找pid
#jps 加 l 参数，表示显示java的完整包路径
#使用awk，分割出pid ($1部分)，及Java程序名称($2部分)
###################################
#初始化psid变量（全局）
psid=0

checkpid() {
  javaps=`$JAVA_HOME/bin/jps -l | grep $APP_MAINCLASS`
  if [ -n "$javaps" ]; then
    psid=`echo $javaps | awk '{print $1}'`
  else
    psid=0
  fi
}


###################################
#(函数)启动程序
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则提示程序已启动
#3. 如果程序没有被启动，则执行启动命令行
#4. 启动命令执行后，再次调用checkpid函数
#5. 如果步骤4的结果能够确认程序的pid,则打印[OK]，否则打印[Failed]
#注意：echo -n 表示打印字符后，不换行
#注意: "nohup 某命令 >/dev/null 2>&1 &" 的用法
###################################
start() {
  checkpid

  if [ $psid -ne 0 ]; then
    echo "================================"
    echo "warn: $APP_MAINCLASS already started! (pid=$psid)"
    echo "================================"
  else
    echo "Starting $APP_MAINCLASS ..."
    nohup "$JAVA_HOME/bin/java" $JAVA_OPTS -classpath $CLASSPATH $APP_MAINCLASS >/dev/null 2>&1 &
    checkpid
    if [ $psid -ne 0 ]; then
      echo "(pid=$psid) [OK]"
    else
      echo "[Failed]"
    fi
  fi
}

###################################
#(函数)停止程序
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则开始执行停止，否则，提示程序未运行
#3. 使用kill -9 pid命令进行强制杀死进程
#4. 执行kill命令行紧接其后，马上查看上一句命令的返回值: $?
#5. 如果步骤4的结果$?等于0,则打印[OK]，否则打印[Failed]
#6. 为了防止java程序被启动多次，这里增加反复检查进程，反复杀死的处理（递归调用stop）。
#注意：echo -n 表示打印字符后，不换行
#注意: 在shell编程中，"$?" 表示上一句命令或者一个函数的返回值
###################################
stop() {
  checkpid

  if [ $psid -ne 0 ]; then
    echo -n "Stopping $APP_MAINCLASS ...(pid=$psid) "
    kill $psid
    if [ $? -eq 0 ]; then
      echo "[OK]"
    else
      echo "[Failed]"
    fi

    checkpid
    if [ $psid -ne 0 ]; then
      stop
    fi
  else
    echo "================================"
    echo "warn: $APP_MAINCLASS is not running"
    echo "================================"
  fi
}

###################################
#(函数)检查程序运行状态
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则提示正在运行并表示出pid
#3. 否则，提示程序未运行
###################################
status() {
  checkpid

  if [ $psid -ne 0 ];  then
    echo "$APP_MAINCLASS is running! (pid=$psid)"
  else
    echo "$APP_MAINCLASS is not running"
  fi
}

###################################
#(函数)打印系统环境参数
###################################
info() {
echo "System Information:"
echo "****************************"
echo `uname -a`
echo
echo "JAVA_HOME=$JAVA_HOME"
echo `$JAVA_HOME/bin/java -version`
echo
echo "APP_HOME=$APP_HOME"
echo "APP_MAINCLASS=$APP_MAINCLASS"
echo "****************************"
}

###################################
#读取脚本的第一个参数($1)，进行判断
#参数取值范围：{start|stop|restart|status|info}
#如参数不在指定范围之内，则打印帮助信息
###################################
case "$1" in
  'start')   start;;
  'stop')    stop;;
  'restart') stop start;;
  'status')  status;;
  'info')    info;;
  *) echo "Usage: $0 {start|stop|restart|status|info}"; exit 1;;
esac