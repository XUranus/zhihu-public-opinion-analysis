package udal

import java.util.Date

import model.{Answer, Question}
import org.apache.hadoop.hbase.util.Bytes
import utils.{DateUtil, SparkCreator}

import scala.collection.mutable.ArrayBuffer

object DataFilter {

  //活跃用户 vote,comment,groupBy用户ID
  //热门答案:sortBy(评论+vote)
  //热门问题：groupBy问题ID sum

  /*获取某个问题的全部答案*/
  def AnswersOfQuestion(question:Question): Array[Answer] = {
    //val questionRowKey = questionId.toString
    //val question = Question.loadFromHBase(questionRowKey)
    val startRowKey = question.createTime.toString + "0000000000"
    val stopRowKey = "9999999999" + "9999999999"
    val result = HBaseManpulator.filterWithinRowKey("zhihu", "answer", startRowKey, stopRowKey)
    result match {
      case Some(iter) => {
        val answers = new ArrayBuffer[Answer]
        while (iter.hasNext) {
          val row = iter.next()
          val answer = Answer.loadFromHBase(row)
          if(answer.questionId == question.id) // filter answer.questionId == question.id
            answers.append(answer)
        }
        answers.toArray
      }
      case _ => Array()
    }
  }

  //一群问题的答案
  def AnswersOfQuestions(questions:Array[Question]):Array[Answer] = {
    questions.flatMap(question => AnswersOfQuestion(question))
  }


  /*获取一段时间内的问题*/
  def AnswersWithinTime(startTime:Date, endTime:Date): Array[Answer] = {
    val startRowKey = DateUtil.dateToTimestampString(startTime) + "0000000000"
    val stopRowKey = DateUtil.dateToTimestampString(endTime) + "9999999999"
    //println(startRowKey,stopRowKey)
    val result = HBaseManpulator.filterWithinRowKey("zhihu", "answer", startRowKey, stopRowKey)
    result match {
      case Some(iter) => {
        val answers = new ArrayBuffer[Answer]
        while (iter.hasNext) {
          val row = iter.next()
          val answer = Answer.loadFromHBase(row)
          answers.append(answer)
          //println(Bytes.toString(row.getValue("data".getBytes(),"questionId".getBytes())))
        }
        answers.toArray
      }
      case _ => Array()
    }
  }


  /*获取关键词一段时间内关联的问题的rowKey*/
  def RowKeysOfAnswersWithKeywordWithinTime(keyword:String, beginTime:Date, endTime:Date): Array[String] = {
    val rowKey = keyword
    HBaseManpulator.getRow("zhihu","words_index", rowKey) match {
      case Some(row) => {
        val familyMap = row.getFamilyMap(Bytes.toBytes("answer_content"))
        var rowKeys = new ArrayBuffer[String]
        familyMap.entrySet().forEach(column => {
          val (timestamp, answerRowKey) = (Bytes.toString(column.getKey).toInt, Bytes.toString(column.getValue))
          if(timestamp >= beginTime.getTime/1000 && timestamp <= endTime.getTime/1000) {
            rowKeys.append(answerRowKey)
          }
        })
        //satisfied answer rowKeys
        rowKeys.toArray
      }
      case _ => {
        Array()
      }//empty answer list
    }
  }

  //包含若干关键词的回答
  def AnswersWithKeywordsWithinTime(keywords:Array[String],beginTime:Date,endTime:Date):Array[Answer] = {
    val rowKeys = keywords.flatMap(
      keyword=> RowKeysOfAnswersWithKeywordWithinTime(keyword,beginTime,endTime)
    ).distinct
    rowKeys.map(rowKey => Answer.loadFromHBase(rowKey))
  }


  /*获取标题包含一组关键词的问题*/
  def RowKeysOfQuestionsWithKeyword(keyword:String): Array[String] = {
    val rowKey = keyword
    HBaseManpulator.getRow("zhihu","words_index", rowKey) match {
      case Some(row) => {
        val familyMap = row.getFamilyMap(Bytes.toBytes("question_title"))
        var rowKeys = new ArrayBuffer[String]
        familyMap.entrySet().forEach(column => {
          val (_, questionRowKey) = (Bytes.toString(column.getKey).toInt, Bytes.toString(column.getValue))
          rowKeys.append(questionRowKey)
        })
        //satisfied answer rowKeys
        rowKeys.toArray
      }
      case _ => {
        Array()
      }//empty question list
    }
  }

  //这里完全根据关联度排 不管时间
  def QuestionsWithKeywords(keywords:Array[String],n:Int = 50):Array[Question] = {
    val sc = SparkCreator.newContext()
    val rowKeys = keywords.flatMap(keyword => RowKeysOfQuestionsWithKeyword(keyword))
    sc.parallelize(rowKeys)
      .map((_,1))
      .reduceByKey(_+_)
      .sortBy(x=>x._2,ascending = false)
      .take(n)
      .map(x=>Question.loadFromHBase(x._1))
      .filter(question=>question.id!=0)
  }


}
