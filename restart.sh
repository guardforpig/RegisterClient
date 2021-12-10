#!bin/bash

JAR=$1-0.0.1-SNAPSHOT
echo "$JAR"
curl --user ooad_javaee:$2 -T http://172.16.4.1/webdav/deploy/${JAR}.jar ..
bash shutdown.sh ${JAR}
bash startup.sh ${JAR}


