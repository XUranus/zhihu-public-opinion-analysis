package utils

import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}
import utils.ConfigLoader.{SPARK_APP_NAME, SPARK_MASTER, SPARK_SQL_SHUFFLE_PARTITIONS, SPARK_TESTING_MEMORY}


object SparkCreator extends Serializable {

  val spark:SparkSession = SparkSession
    .builder()
    .master(SPARK_MASTER)
    .appName(SPARK_APP_NAME)
    .config("spark.sql.shuffle.partitions", SPARK_SQL_SHUFFLE_PARTITIONS)
    .config("spark.testing.memory", SPARK_TESTING_MEMORY)
    .getOrCreate()

  def newContext():SparkContext = {
    spark.newSession().sparkContext
  }

  def newSession():SparkSession = {
    spark.newSession()
  }

  def newSqlContext():SQLContext = {
    val sqlContext = spark.sqlContext
    sqlContext
  }

}
