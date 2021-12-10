#!bin/bash

curl 'http://172.16.4.1/webdav/deploy/$1.jar'  --output '..'  --user ooad_javaee:$2
bash shutdown.sh $1
bash startup.sh $1


