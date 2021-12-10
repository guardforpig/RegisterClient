#!/bin/bash

target_dir=`pwd`

SERVER=$1-0.0.1-SNAPSHOT

pid=`ps ax | grep -i $SERVER | grep ${target_dir} | grep java | grep -v grep | awk '{print $SERVER}'`
if [ -z "$pid" ] ; then
        echo "No $SERVER Server running."
        exit -1;
fi

echo "The Server(${pid}) is running..."

kill ${pid}

echo "Send shutdown request to Server(${pid}) OK"
