JAVA_OPTS="-server -d64 -Xms512M -Xmx512M -Xss512k\
 -XX:+AggressiveOpts -XX:AutoBoxCacheMax=20000\
 -XX:+DisableExplicitGC -XX:MaxTenuringThreshold=15\
 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC\
 -XX:+CMSParallelRemarkEnabled\
 -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods\
 -XX:+UseCMSInitiatingOccupancyOnly -Djava.awt.headless=true\
 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath==$APP_HOME/logs"

JAVA_OPTS=$JAVA_OPTS" -Dapp.log.path=$APP_HOME/logs\
 -Dapp.config.path=$APP_HOME/config"


#OPTS="-Dcom.sun.management.jmxremote\
# -Djava.rmi.server.hostname=$2\
# -Dcom.sun.management.jmxremote.port=$3\
# -Dcom.sun.management.jmxremote.ssl=false\
# -Dcom.sun.management.jmxremote.authenticate=false"