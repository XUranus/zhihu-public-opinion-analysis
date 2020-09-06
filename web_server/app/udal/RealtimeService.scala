package udal

import model.Question
import redis.clients.jedis.JedisPool
import utils.ConfigLoader.{REDIS_HOST, REDIS_PORT}

import scala.collection.mutable

object RealtimeService {

  val pool = new JedisPool(REDIS_HOST, REDIS_PORT)

  def getTodayAnswersNum:Int= {
    val redis = pool.getResource
    val result = redis.get(RedisKeyManager.todayAnswersNum)
    redis.close()
    if(result==null) {
      0
    } else {
      result.toInt
    }
  }

  def getTodayQuestionsNum:Int = {
    val redis = pool.getResource
    val result = redis.get(RedisKeyManager.todayQuestionsNum)
    redis.close()
    if(result==null) {
      0
    } else {
      result.toInt
    }
  }

  def getTotalAnswersNum:Int = {
    val redis = pool.getResource
    val result = redis.get(RedisKeyManager.totalAnswersNum)
    redis.close()
    if(result==null) {
      0
    } else {
      result.toInt
    }
  }

  def getTotalQuestionsNum:Int = {
    val redis = pool.getResource
    val result = redis.get(RedisKeyManager.totalQuestionsNum)
    redis.close()
    if(result==null) {
      0
    } else {
      result.toInt
    }
  }

  def getHotWords(n:Int):Array[(String,Int)] = {
    val redis = pool.getResource
    val result = redis.hgetAll(RedisKeyManager.hotWords)
    redis.close()
    if(result==null) {
      Array()
    } else {
      val buffer = new mutable.ArrayBuffer[(String,Int)]()
      result.keySet().forEach(key=>{
        buffer.append((key,result.get(key).toInt))
      })
      buffer.toArray.sortBy(x=>x._2).reverse.take(n)
    }
  }

  def getHotQuestions(n:Int):Array[(Question,Int)] = {
    val redis = pool.getResource
    val result = redis.hgetAll(RedisKeyManager.questionsTotalAnswerNum)
    redis.close()
    val pairs:Array[(String,Int)] = if(result==null) {
      Array()
    } else {
      val buffer = new mutable.ArrayBuffer[(String,Int)]()
      result.keySet().forEach(key=>{
        buffer.append((key,result.get(key).toInt))
      })
      buffer.toArray
    }
    pairs.sortBy(x=>x._2)
      .reverse
      .take(n)
      .map(item=>(Question.loadFromHBase(item._1),item._2))
  }

  def getNegativeQuestions(n:Int):Array[(Question,Float)] = {
    val redis = pool.getResource
    val totalResult = redis.hgetAll(RedisKeyManager.questionsTotalAnswerNum)
    val negativeResult = redis.hgetAll(RedisKeyManager.questionsNegativeAnswersNum)
    redis.close()
    val negativePairs:Array[(String,Int)] = if(negativeResult==null) {
      Array()
    } else {
      val buffer = new mutable.ArrayBuffer[(String,Int)]()
      negativeResult.keySet().forEach(key=>{
        buffer.append((key,negativeResult.get(key).toInt))
      })
      buffer.toArray
    }
    val ret = negativePairs.sortBy(x=>x._2).reverse
      .take(5*n)
      .map(x=>{
        val (questionId, negativeCount) = x
        val total = totalResult.get(questionId)
        if(total==null) {
          (questionId, 0.1f)
        } else {
          (questionId, negativeCount / total.toFloat)
        }
      })
      .map(item=>(Question.loadFromHBase(item._1),item._2))
      .filter(x=>x._1.id != 0)
      .take(n)
    ret
  }


}
