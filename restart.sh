#!bin/bash

wget --user ooad_javaee:$2 -T http://172.16.4.1/webdav/deploy/$1.jar ..
bash shutdown.sh $1
bash startup.sh $1


