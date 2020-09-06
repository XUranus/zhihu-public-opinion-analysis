package utils

//preload all configuration

import com.typesafe.config.{Config, ConfigFactory}

object ConfigLoader {

  val configuration: Config = ConfigFactory.load()

  val ZOOKEEPER_QUORUM:String = configuration.getString("appConfig.ZOOKEEPER_QUORUM")
  val ZOOKEEPER_PORT:String = configuration.getString("appConfig.ZOOKEEPER_PORT")

  val SPARK_MASTER:String = configuration.getString("appConfig.SPARK_MASTER")
  val SPARK_APP_NAME:String = configuration.getString("appConfig.SPARK_APP_NAME")
  val SPARK_SQL_SHUFFLE_PARTITIONS:String = configuration.getString("appConfig.SPARK_SQL_SHUFFLE_PARTITIONS")
  val SPARK_TESTING_MEMORY:String = configuration.getString("appConfig.SPARK_TESTING_MEMORY")

  val REDIS_HOST:String = configuration.getString("appConfig.REDIS_HOST")
  val REDIS_PORT:Int = configuration.getInt("appConfig.REDIS_PORT")

  val LOCATION_FILE_PATH:String = configuration.getString("appConfig.LOCATION_FILE_PATH")
  val STOP_WORDS_FILE_PATH:String = configuration.getString("appConfig.STOP_WORDS_FILE_PATH")

  /*
  val ZOOKEEPER_QUORUM = "localhost"
  val ZOOKEEPER_PORT = "2181"

  val SPARK_MASTER = "local[*]"
  val SPARK_APP_NAME = "zhihu"
  val SPARK_SQL_SHUFFLE_PARTITIONS = "5"
  val SPARK_TESTING_MEMORY = "512000000"

  val REDIS_HOST = "localhost"
  val REDIS_PORT = 6379

  val LOCATION_FILE_PATH = "/home/xuranus/workspace/china_regions/json/location.json"
  val STOP_WORDS_FILE_PATH = "/home/xuranus/Desktop/stopwords.txt"
   */
}
