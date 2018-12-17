#!/bin/bash

java -Xmx4096m -Xms1024m -Dlog4j.configuration=config/log4j.properties -cp "config/*:lib/*:target/*" gov.va.vinci.ef.Service
