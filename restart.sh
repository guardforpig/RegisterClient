#!bin/bash

curl --user ooad_javaee:$2 -T http://172.16.4.1/webdav/deploy/$1-0.0.1-SNAPSHOT.jar ..
bash shutdown.sh $1
bash startup.sh $1


