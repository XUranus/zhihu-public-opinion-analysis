import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import services.RecordResolver
import udal.RedisManpulator
import utils.ConfigLoader.KAFKA_BROKERS

import scala.collection.mutable


object Main {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
      .setAppName("zhihuKafka")
      .setMaster("local[*]")

    val ssc = new StreamingContext(conf, Seconds(2))

    val topicsSet = Array("zhihu_answers","zhihu_users","zhihu_questions")
    val kafkaParams = mutable.HashMap[String, String]()
    //必须添加以下参数，否则会报错
    kafkaParams.put("bootstrap.servers", KAFKA_BROKERS)
    kafkaParams.put("group.id", "group1")
    kafkaParams.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaParams.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer")

    val messages = KafkaUtils.createDirectStream[String, Array[Byte]](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, Array[Byte]](topicsSet, kafkaParams)
    )

    println("zhihu realtime server start")

    val data = messages.map(pair=>DataParser.parse(pair.value()))

    val answersRDD = data.filter(x=>x._1=="answer").map(_._2)
    val usersRDD = data.filter(x=>x._1=="user").map(_._2)
    val questionsRDD = data.filter(x=>x._1=="question").map(_._2)

    answersRDD.foreachRDD(
      answerRDD => {
        val pairs = answerRDD.map(text => RecordResolver.resolveAnswerRecord(text,false)).collect()
        val answers = pairs.map(_._1)
        val words = pairs.flatMap(_._2).map((_,1)).groupBy(_._1).map(x=>(x._1,x._2.length)).toArray
        println(answers.length+" answers resolved")
        RedisManpulator.appendAnswersNum(answers.length, publish = true)
        RedisManpulator.appendWordsCount(words)
        RedisManpulator.appendQuestionTotalAnswersCount(answers)
        RedisManpulator.appendQuestionNegativeAnswersCount(answers)
      }
    )

    questionsRDD.foreachRDD(
      questionRDD => {
        val questions = questionRDD.map(text => RecordResolver.resolveQuestionRecord(text, false)).collect()
        println(questions.length+" questions resolved")
        RedisManpulator.appendQuestionsNum(questions.length, publish = true)
      }
    )

    usersRDD.foreachRDD(
      userRDD => {
        val users = userRDD.map(text => RecordResolver.resolveUserRecord(text, false)).collect()
        println(users.length+" users resolved")
      }
    )

    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }

}
