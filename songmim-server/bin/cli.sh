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
APP_MAINCLASS=cn.songm.im.cli.CliMain
exec "$JAVA_HOME/bin/java" -classpath $CLASSPATH $APP_MAINCLASS
