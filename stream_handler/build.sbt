name := "zhihu_realtime"

version := "0.1"

scalaVersion := "2.12.1"

libraryDependencies += "com.typesafe" % "config" % "1.4.0"

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.0"

// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.4.0" //% "provided"

// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming-kafka-0-10
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.4.0"

// https://mvnrepository.com/artifact/org.apache.flume/flume-ng-core
libraryDependencies += "org.apache.flume" % "flume-ng-core" % "1.9.0"

// https://mvnrepository.com/artifact/redis.clients/jedis
libraryDependencies += "redis.clients" % "jedis" % "3.2.0"

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-client
libraryDependencies += "org.apache.hbase" % "hbase-client" % "1.3.6"

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-common
libraryDependencies += "org.apache.hbase" % "hbase-common" % "1.3.6"

// https://mvnrepository.com/artifact/com.google.code.gson/gson
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.6"

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.0"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.0"

// https://mvnrepository.com/artifact/org.ansj/ansj_seg
libraryDependencies += "org.ansj" % "ansj_seg" % "5.1.6"

// https://mvnrepository.com/artifact/org.deeplearning4j/deeplearning4j-core
libraryDependencies += "org.deeplearning4j" % "deeplearning4j-core" % "1.0.0-beta6"

// https://mvnrepository.com/artifact/org.deeplearning4j/deeplearning4j-nlp
libraryDependencies += "org.deeplearning4j" % "deeplearning4j-nlp" % "1.0.0-beta6"

// https://mvnrepository.com/artifact/org.deeplearning4j/deeplearning4j-modelimport
libraryDependencies += "org.deeplearning4j" % "deeplearning4j-modelimport" % "1.0.0-beta6"

//backend
libraryDependencies += "org.nd4j" % "nd4j-native-platform" % "1.0.0-beta6"