#!/bin/bash

cd `dirname $0`

case "$1" in
    start)
        echo "-->> start server"
    ;;

    stop)
        echo "-->> stop server"
    ;;

    *)
        echo "Usage: server.sh {start|stop}"
    ;;
esac

exit 0
