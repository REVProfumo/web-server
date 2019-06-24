#!/bin/bash

mvn clean install

java -cp target/web-server-1.0-SNAPSHOT-jar-with-dependencies.jar Server