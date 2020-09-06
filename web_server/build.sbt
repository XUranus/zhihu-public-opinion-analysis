name := "zhihu_server"
 
version := "1.0"
      
lazy val `zhihu_server` = (project in file(".")).enablePlugins(PlayScala)




resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

resolvers ++= Seq(
  "Apache Repository" at "https://repository.apache.org/content/repositories/releases/",
  "Cloudera repo" at "//repository.cloudera.com/artifactory/cloudera-repos/"
)


resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

//resolvers += "aliyun" at "http://maven.aliyun.com/nexus/content/groups/public/"

resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.10"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )


// https://mvnrepository.com/artifact/com.typesafe.play/play-json
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.7.3"

// https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-scala
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.8"


//play above, project below


// https://mvnrepository.com/artifact/com.google.code.gson/gson
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.6"

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.5"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.5"

// https://mvnrepository.com/artifact/org.ansj/ansj_seg
libraryDependencies += "org.ansj" % "ansj_seg" % "5.1.6"

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-client
libraryDependencies += "org.apache.hbase" % "hbase-client" % "1.3.6"

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-common
libraryDependencies += "org.apache.hbase" % "hbase-common" % "1.3.6"

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-protocol
//libraryDependencies += "org.apache.hbase" % "hbase-protocol" % "1.3.6"

// https://mvnrepository.com/artifact/redis.clients/jedis
libraryDependencies += "redis.clients" % "jedis" % "3.2.0"

// https://mvnrepository.com/artifact/org.deeplearning4j/deeplearning4j-core
//libraryDependencies += "org.deeplearning4j" % "deeplearning4j-core" % "1.0.0-alpha"

// https://mvnrepository.com/artifact/org.deeplearning4j/deeplearning4j-modelimport
//libraryDependencies += "org.deeplearning4j" % "deeplearning4j-modelimport" % "1.0.0-alpha"

//dependencyOverrides += "com.google.guava" % "guava" % "15.0"

// https://mvnrepository.com/artifact/com.google.protobuf/protobuf-java
dependencyOverrides += "com.google.protobuf" % "protobuf-java" % "2.6.0"

// https://mvnrepository.com/artifact/com.googlecode.efficient-java-matrix-library/ejml
libraryDependencies += "com.googlecode.efficient-java-matrix-library" % "ejml" % "0.25"

// https://mvnrepository.com/artifact/org.apache.spark/spark-mllib
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.4.5"

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.41"
)