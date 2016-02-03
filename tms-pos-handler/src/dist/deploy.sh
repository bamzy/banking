#!/bin/bash
PROJECT_NAME="tms-pos-handler"
PROJECT_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
#parent folder name is artifact name
ARTIFACT=$(basename ${PROJECT_HOME})
if [ "$1" == "-k" ]
then
    pkill -9 -f "${PROJECT_NAME}.*.jar"
fi
if pgrep -f "${ARTIFACT}.jar" > /dev/null
then
    echo "It is Running..."
else
    cd ${PROJECT_HOME}
    nohup bash bin/${PROJECT_NAME} < /dev/null > ${PROJECT_HOME}/../../../output.log 2>&1 &
fi
