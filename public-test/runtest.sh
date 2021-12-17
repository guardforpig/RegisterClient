## modified by luxun@2020

## 将文件结尾从CRLF改为LF，解决了cd 错误问题
cd /home/mingqiu/oomall
git pull
#mvn clean install -Dmaven.test.skip=true
#
#cd /home/mingqiu/privilegegateway
#git pull
#mvn clean install -Dmaven.test.skip=true

cd /home/mingqiu/test

if [ -d $1 ];then
  rm -r "$1"
fi

mkdir "$1"
mkdir "$1/public"
#mkdir "$1/private"

cp -rf /home/mingqiu/oomall/public-test/* $1/public
#cp -rf /home/mybaby/private-test/* $1/private

sed -i "s/\${ooad.group}/$1/g" $1/public/pom.xml
sed -i "s/\${ooad.testdir}/$2/g" $1/public/pom.xml
#sed -i "s/\${ooad.group}/$1/g" $1/private/pom.xml
#sed -i "s/\${ooad.testdir}/$2/g" $1/private/pom.xml

## 在主项目下使用 -pl -am 编译子项目，否则找不到依赖
## $1 代表第一个参数，$0代表命令名
cd $1/public
mvn clean test -Dmanagement.gate=$3 -Dmall.gate=$4
mvn site:site site:deploy

#cd ../private
#mvn surefire-report:report -Dmanagement.gate=$3 -Dmall.gate=$4
#mvn site:deploy
