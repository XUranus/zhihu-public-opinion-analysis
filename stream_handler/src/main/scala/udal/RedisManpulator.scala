package udal

import enitity.Answer
import redis.clients.jedis.JedisPool
import utils.ConfigLoader

object RedisManpulator {

  val REDIS_HOST: String = ConfigLoader.REDIS_HOST
  val REDIS_PORT: Int = ConfigLoader.REDIS_PORT
  val pool = new JedisPool(REDIS_HOST, REDIS_PORT)

  //util
  def incrKeyValue(key:String, append:Int): Unit = {
    val redis = pool.getResource
    if(!redis.exists(key)) {
      redis.set(key,append.toString)
    } else {
      val lastValue = redis.get(key).toInt
      val newValue = append + lastValue
      redis.set(key, newValue.toString)
    }
    redis.close()
  }


  def appendAnswersNum(newAnswersNum:Int, publish:Boolean = false): Unit = {
    if(newAnswersNum > 0) {
      incrKeyValue(RedisKeyManager.todayAnswersNum,newAnswersNum)
      incrKeyValue(RedisKeyManager.totalAnswersNum,newAnswersNum)
      if(publish)
        publishAnswersAndQuestionsNum()
    }
  }

  def appendQuestionsNum(newQuestionsNum:Int, publish:Boolean = false): Unit = {
    if(newQuestionsNum > 0) {
      incrKeyValue(RedisKeyManager.todayQuestionsNum, newQuestionsNum)
      incrKeyValue(RedisKeyManager.totalQuestionsNum, newQuestionsNum)
      if(publish)
        publishAnswersAndQuestionsNum()
    }
  }

  def publishAnswersAndQuestionsNum(): Unit = {
    val redis = pool.getResource
    val channel = RedisKeyManager.dataNumChannel
    val message = redis.get(RedisKeyManager.todayAnswersNum) + " " + redis.get(RedisKeyManager.todayQuestionsNum) + " " + redis.get(RedisKeyManager.totalAnswersNum) + " " + redis.get(RedisKeyManager.totalQuestionsNum)
    redis.publish(channel,message)
    redis.close()
  }

  def appendWordsCount(words:Array[(String,Int)]): Unit = {
    val redis = pool.getResource
    words.foreach(pair=>{
      val (key,append) = pair
      val result = redis.hget(RedisKeyManager.hotWords, key)
      var prev = 0
      if(result!=null)
        prev = result.toInt
      redis.hset(RedisKeyManager.hotWords, key, (prev + append).toString)
    })
    redis.close()
    //TODO::dic value decline
  }


  def appendQuestionTotalAnswersCount(answers:Array[Answer]): Unit = {
    answers.foreach(answer => RedisManpulator.appendQuestionTotalAnswersCount(answer.questionId))
  }

  def appendQuestionTotalAnswersCount(questionId:Long): Unit = {
    val redis = pool.getResource
    val result = redis.hget(RedisKeyManager.questionsTotalAnswerNum, questionId.toString)
    var prev = 0
    if(result!=null)
      prev = result.toInt
    redis.hset(RedisKeyManager.questionsTotalAnswerNum, questionId.toString, (prev + 1).toString)
    redis.close()
  }

  def appendQuestionNegativeAnswersCount(answers:Array[Answer]): Unit = {
    answers
      .filter(answer => answer.sentiment == 0) //TODO::negative
      .foreach(answer => RedisManpulator.appendQuestionNegativeAnswersCount(answer.questionId))
  }

  def appendQuestionNegativeAnswersCount(questionId:Long): Unit = {
    val redis = pool.getResource
    val result = redis.hget(RedisKeyManager.questionsNegativeAnswersNum, questionId.toString)
    var prev = 0
    if(result!=null)
      prev = result.toInt
    redis.hset(RedisKeyManager.questionsNegativeAnswersNum, questionId.toString, (prev + 1).toString)
    redis.close()
  }


}
