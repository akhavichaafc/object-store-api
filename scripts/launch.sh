#!/bin/bash
cd /app

./waitForDatabase.sh

echo "checking for users"
./checkUsers.sh
echo "url=$(echo '$spring.datasource.url' | awk  -f envSubstitution.awk)"
VERSION=$(cat ./pom.xml | grep -m 1 '<version>' | awk -F"[><]" '{print $3}')
export VERSION
echo "Version: '$VERSION'"
echo "executing java"
# enable remote debugging
exec java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000 -jar $1-$VERSION.jar
# exec java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -jar $1-$VERSION.jar
