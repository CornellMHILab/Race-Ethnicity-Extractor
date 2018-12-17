#!/bin/bash

java -Xmx4096m -Xms1024m -Dlog4j.configuration=file:config/log4j.properties -cp "lib/*:target/*" gov.va.vinci.ef.Client -clientConfigFile "config/ClientConfig.groovy" -readerConfigFile "config/readers/BatchDatabaseCollectionReaderConfig.groovy" -listenerConfigFile "config/listeners/DatabaseListenerConfig.groovy"