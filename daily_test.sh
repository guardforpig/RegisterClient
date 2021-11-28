#|bin/bash
## 将文件结尾从CRLF改为LF，解决了cd 错误问题
time=$(date "+%Y-%m-%d-%H-%M-%S")
origin_dir='oomall-testreport'
daily_dir='daily-report/'$time
echo $daily_dir
echo '-------------------building annotation-------------------------'
cd /home/mingqiu/privilegegateway/annotation
git checkout pom.xml
git pull
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean install
mvn site:site site:deploy

cd /home/mingqiu/oomall/sql
echo '-------------------initializing database-------------------------'
mysql -h 172.16.1.254 -udbuser -p12345678 -D oomall < goods-batch.sql

echo '-------------------building core-------------------------'
cd /home/mingqiu/oomall/core
git checkout pom.xml
git pull
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean install
mvn site:site site:deploy

echo '-------------------building comment-------------------------'
cd /home/mingqiu/oomall/comment
git checkout pom.xml
git pull
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean test
mvn site:site site:deploy

echo '-------------------building feight-------------------------'
cd /home/mingqiu/oomall/freight
git checkout pom.xml
git pull
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean test
mvn site:site site:deploy

echo '-------------------building shop-------------------------'
cd /home/mingqiu/oomall/shop
git checkout pom.xml
git pull
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean test
mvn site:site site:deploy

echo '-------------------building goods-------------------------'
cd /home/mingqiu/oomall/goods
git checkout pom.xml
git pull
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean test
mvn site:site site:deploy

echo '-------------------building coupon-------------------------'
cd /home/mingqiu/oomall/coupon
git checkout pom.xml
git pull
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean test
mvn site:site site:deploy

echo '-------------------building activity-------------------------'
cd /home/mingqiu/oomall/activity
git checkout pom.xml
git pull
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean test
mvn site:site site:deploy
curl --user ooad_javaee:12345678 -T /home/mingqiu/logs/daily_test.log http://172.16.4.1/webdav/daily-report/$time/console.log

