#|bin/bash
## 将文件结尾从CRLF改为LF，解决了cd 错误问题
git checkout --
git pull
mvn clean

cd ../privilegegateway
git checkout --
git pull
mvn clean

time=$(date "+%Y-%m-%d-%H-%M-%S")

origin_dir='oomall-testreport'
daily_dir='daily_report/'$time
echo $daily_dir

cd annotation
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn install site:site site:deploy

cd ../../oomall/core
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn install site:site site:deploy

cd ../activity
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn test site:site site:deploy

cd ../comment
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn test site:site site:deploy

cd ../freight
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn test site:site site:deploy

cd ../shop
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn test site:site site:deploy

cd ../goods
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn test site:site site:deploy

cd ../coupon
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn test site:site site:deploy

