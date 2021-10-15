#!/bin/bash
clear
AGENT_NAME="data-structure-analysis-agent"
AGENT_DIR="../$AGENT_NAME"
AGENT_JAR="$AGENT_DIR/target/$AGENT_NAME.jar"
mvn -f "$AGENT_DIR/pom.xml" clean package
mvn clean package

echo $AGENT_JAR
java -javaagent:"$AGENT_JAR" -jar target/sample.jar
