#|bin/bash
## 将文件结尾从CRLF改为LF，解决了cd 错误问题
time=$(date "+%Y-%m-%d-%H-%M-%S")
origin_dir='oomall-testreport'
daily_dir='daily-report/'$time
echo $daily_dir

echo '-------------------initializing privilege database-------------------------'
cd /home/mingqiu/privilegegateway/sql
mysql -h 172.16.1.254 -udbuser -p12345678 -D privilege_gateway < privilegegateway-batch.sql

cd /home/mingqiu/privilegegateway
git checkout annotation/pom.xml
git checkout privilegeservice/pom.xml
git pull
echo '-------------------building annotation-------------------------'
cd /home/mingqiu/privilegegateway/annotation
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean install
mvn site:site site:deploy

echo '-------------------building comment-------------------------'
cd /home/mingqiu/privilegegateway/privilegeservice
git checkout pom.xml
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean test
mvn site:site site:deploy

cd /home/mingqiu/oomall
git checkout core/pom.xml
git checkout comment/pom.xml
git checkout freight/pom.xml
git checkout shop/pom.xml
git checkout goods/pom.xml
git checkout coupon/pom.xml
git checkout activity/pom.xml
git pull

echo '-------------------initializing oomall database-------------------------'
cd /home/mingqiu/oomall/sql
mysql -h 172.16.1.254 -udbuser -p12345678 -D oomall < goods-batch.sql

echo '-------------------building core-------------------------'
cd /home/mingqiu/oomall/core
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean install
mvn site:site site:deploy

echo '-------------------building comment-------------------------'
cd /home/mingqiu/oomall/comment
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean test
mvn site:site site:deploy

echo '-------------------building feight-------------------------'
cd /home/mingqiu/oomall/freight
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean test
mvn site:site site:deploy

echo '-------------------building shop-------------------------'
cd /home/mingqiu/oomall/shop
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean test
mvn site:site site:deploy

echo '-------------------building goods-------------------------'
cd /home/mingqiu/oomall/goods
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean test
mvn site:site site:deploy

echo '-------------------building coupon-------------------------'
cd /home/mingqiu/oomall/coupon
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean test
mvn site:site site:deploy

echo '-------------------building activity-------------------------'
cd /home/mingqiu/oomall/activity
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean test
mvn site:site site:deploy


curl --user ooad_javaee:12345678 -T /home/mingqiu/logs/daily_test.log http://172.16.4.1/webdav/daily-report/$time/console.log

