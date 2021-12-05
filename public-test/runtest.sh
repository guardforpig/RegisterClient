## modified by luxun@2020
time=$(date "+%Y-%m-%d-%H-%M-%S")

## 将文件结尾从CRLF改为LF，解决了cd 错误问题
cd /home/mingqiu/oomall/public-test
git pull
mvn clean

cd /home/mmingqiu/privilegegateway/annotation
mvn clean install -Dmaven.test.skip=true

cd /home/mmingqiu/oomall/core
mvn clean install -Dmaven.test.skip=true

cd /home/mingqiu/test

if [ -d $1 ];then
  rm -r "$1"
fi

mkdir "$1"
mkdir "$1/public"

cp -rf /home/mingqiu/oomall/public-test/* $1/public

sed -i "s/\${ooad.group}/$1/g" $1/public/pom.xml
sed -i "s/\${ooad.testdir}/$time/g" $1/public/pom.xml

## 在主项目下使用 -pl -am 编译子项目，否则找不到依赖
## $1 代表第一个参数，$0代表命令名
cd $1/public
mvn surefire-report:report -Dgateway.ip=$2
mvn site:deploy

