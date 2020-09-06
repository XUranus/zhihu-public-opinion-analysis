package services


import java.util.Calendar

import utils.{DateUtil, SparkCreator, TimeDebugger}
import model.{Answer, AnswersAnalysisResult, Question, User}
import org.apache.spark.ml.feature.{HashingTF, IDF, Tokenizer}
import org.apache.spark.ml.linalg.SparseVector
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ArrayBuffer

class AnswersAnalyzer(val answers: Array[Answer]) {

  val answersAnalysisResult = new AnswersAnalysisResult

  //analysis the sentiment percentage distribution of all articles
  def sentimentDistribution(): Array[(Int, Int)] = {
    val sc = SparkCreator.newContext()
    val result = sc.parallelize(answers)
      .map(answer => (answer.sentiment, 1))
      .reduceByKey(_+_)
      .collect()
    result
  }


  def loadKeywords(n:Int): Array[(String,Int)] = {
    /*val sc = SparkCreator.newContext()
    val result = sc.parallelize(answers)
      .flatMap(answer => answer.contentWordsSeg.split(" ").filter(x=>x!=" "))
      .map((_,1))
      .reduceByKey(_+_)
      .sortBy(pair => pair._2, ascending = false)
      .take(n)
    result*/
    val sc = SparkCreator.newContext()
    val textRDD = sc.parallelize(answers).map(_.contentWordsSeg)
    TFIDFKeyWord.generateKeywords(textRDD,n)
  }


  //keywords related to $keywords, make sure keyword pair exist in keywords
  def relatedKeywords(keywords:Array[String], n:Int = 50): Array[(String,Int)] = {
    val words = loadKeywords(n)
    val keywordPairResult = words.find(pair => keywords.contains(pair._1))
    keywordPairResult match {
      case Some(_) => words
      case _ =>  words//relatedKeywords(keyword, 3*n)
    }
  }


  //questions which gain most answers
  def hottestQuestions(n:Int): Array[(Question,Int)] = {
    val sc = SparkCreator.newContext()
    val result = sc.parallelize(answers)
      .map(answer => (answer.questionId, 1))
      .reduceByKey(_+_)
      .sortBy(pair => pair._2, ascending = false)
      .take(n)
      .map(pair => (Question.loadFromHBase(pair._1.toString), pair._2))
    result
  }

  def uniqueQuestionsNum():Int = {
    val sc = SparkCreator.newContext()
    val result = sc.parallelize(answers)
      .map(answer => (answer.questionId, 1))
      .reduceByKey(_+_)
      .collect()
      .length
    result
  }

  //answers gain most votes
  def mostVotedAnswers(n:Int=10): Array[(Answer,User,Question)] = {
    val sc = SparkCreator.newContext()
    val result = sc.parallelize(answers)
      .sortBy(pair => pair.voteUpCount, ascending = false)
      .take(2*n)
      .distinct
      .take(n)
      .map(answer=>(
        answer,
        User.loadFromHBase(answer.authorId),
        Question.loadFromHBase(answer.questionId.toString)
      ))
    result
  }


  //take n authors appear most frequently, return [(user, appear times), ...]
  def highFrequencyAuthors(n:Int = 10): Array[(User,Int)] = {
    val sc = SparkCreator.newContext()
    val result = sc.parallelize(answers)
      .map(answer => (answer.authorId, 1))
      .filter(x=>x._1!="" && x._1!="0") //prevent anonymous user
      .reduceByKey(_+_)
      .distinct() //prevent dulplication
      .sortBy(pair => pair._2, ascending = false)
      .take(n)
      .map(pair => (User.loadFromHBase(pair._1), pair._2))
    result
  }


  //take n authors have most vote ups, return [(user, votes num), ...]
  def mostLikedAuthors(n:Int): Array[(User,Int)] = {
    val sc = SparkCreator.newContext()
    val result = sc.parallelize(answers)
      .map(answer => (answer.authorId, answer.voteUpCount))
      .reduceByKey(_+_)
      .sortBy(pair => pair._2, ascending = false)
      .take(10*n)
      .filter(x=>x._1!="" && x._1!="0") //prevent anonymous user
      .take(2*n)
      .map(pair => (User.loadFromHBase(pair._1), pair._2))
      .filter(_._1.name!="") //TODO:迫不得已
      .take(n)
    result
  }


  def sentimentTrend():(Array[(String, Float)], Array[(String, Float)], Array[(String,Float)]) ={
    val sc = SparkCreator.newContext()
    //喜悦0 愤怒 厌恶 低落
    val sequence = sc.parallelize(answers)
      .filter(answer => answer.createdTime > 0)
      .map(answer => (answer.createdTime,if(answer.sentiment==0) {0.0F} else {1.0F}))
    trend(sequence)
  }


  //get nums answers updated per hour , (begin timestamp, end timestamp, new answers per hour)
  def heatTrend(): (Array[(String, Float)], Array[(String, Float)], Array[(String,Float)]) = {
    val sc = SparkCreator.newContext()
    val sequence = sc.parallelize(answers)
      .filter(answer => answer.createdTime > 0)
      .map(answer => (answer.createdTime, 1.0F))
    trend(sequence)
  }


  def trend(sequence:RDD[(Long,Float)]): (Array[(String, Float)], Array[(String, Float)], Array[(String,Float)]) = {
    val hourTrend = sequence
      .map(pair => (DateUtil.timestampToDateString(pair._1*1000 , "yyyy-MM-dd HH"), pair._2))
      .reduceByKey(_+_)
      .collect()
      .sortBy(pair => pair._1)

    val dateTrend = hourTrend
      .map(pair => (pair._1.substring(0,10), pair._2))
      .groupBy(pair => pair._1)
      .map(group => (group._1, group._2.length.toFloat))
      .toArray
      .sortBy(pair => pair._1)
    //TODO::handle trend len = 0 or 1 2 these case

    //fit and predict
    val daysAfter = 3
    val y = dateTrend.map(pair => pair._2 + 0.0) //cast to Array[Double]
    val x = new ArrayBuffer[Double]()
    val dates = new ArrayBuffer[String]()
    for (i <- dateTrend.indices) {
      dates.append(dateTrend(i)._1)
    }
    for (i <- dateTrend.length to (dateTrend.length + daysAfter)) {
      val lastDate = DateUtil.dateStringToDate("yyyy-MM-dd",dates.last)
      val cal = Calendar.getInstance()
      cal.setTime(lastDate)
      cal.add(Calendar.DATE,1)
      val nextDate = DateUtil.timestampToDateString(cal.getTime.getTime,"yyyy-MM-dd")
      dates.append(nextDate)
    }
    for(i <- y.indices) {
      x.append(i)
    }
    val xPredict = new ArrayBuffer[Double]()
    for(i <- 0 to y.length + daysAfter) {
      xPredict.append(i)
    }
    val datePredict = new LinearRegression(x.toArray,y)//TODO::
      .fit(5)
      .predict(xPredict.toArray)
    val dateTrendResult = new ArrayBuffer[(String,Double)]()
    for(i <- datePredict.indices) {
      dateTrendResult.append((dates(i),if(datePredict(i) > 0) {datePredict(i)} else {0}))
    }

    //for the bug of beta version g2plot combo plot
    val len = dateTrend.length
    val appendTail = dates.slice(len,len + daysAfter+1).map(x=>(x,0F))

    (hourTrend, dateTrend++appendTail, dateTrendResult.map(x=>(x._1,x._2.toFloat)).toArray)
  }



  def analysisSentimentDistribution():AnswersAnalyzer = {
    TimeDebugger.enterSection("AnswersAnalyzer.analysisSentimentDistribution")
    answersAnalysisResult.sentimentDistribution = sentimentDistribution()
    TimeDebugger.exitSection()
    this
  }

  def analysisKeywords(n:Int): AnswersAnalyzer = {
    TimeDebugger.enterSection("AnswersAnalyzer.analysisKeywords")
    answersAnalysisResult.keywords = loadKeywords(n)
    TimeDebugger.exitSection()
    this
  }

  def analysisRelatedKeywords(keywords:Array[String], n:Int = 50): AnswersAnalyzer = {
    TimeDebugger.enterSection("AnswersAnalyzer.analysisRelatedKeywords")
    answersAnalysisResult.relatedKeywords = relatedKeywords(keywords, n)
    TimeDebugger.exitSection()
    this
  }

  def analysisHottestQuestions(n:Int): AnswersAnalyzer = {
    TimeDebugger.enterSection("AnswersAnalyzer.analysisHottestQuestions")
    answersAnalysisResult.hottestQuestions = hottestQuestions(n)
    TimeDebugger.exitSection()
    this
  }

  def analysisMostVotedAnswers(n:Int):AnswersAnalyzer = {
    TimeDebugger.enterSection("AnswersAnalyzer.analysisMostVotedAnswers")
    answersAnalysisResult.mostVotedAnswers =  mostVotedAnswers(10)
    TimeDebugger.exitSection()
    this
  }

  def analysisHighFrequencyAuthors(n:Int):AnswersAnalyzer = {
    TimeDebugger.enterSection("AnswersAnalyzer.analysisHighFrequencyAuthors")
    answersAnalysisResult.highFrequencyAuthors = highFrequencyAuthors(n)
    TimeDebugger.exitSection()
    this
  }

  def analysisMostLikedAuthors(n:Int):AnswersAnalyzer = {
    TimeDebugger.enterSection("AnswersAnalyzer.analysisMostLikedAuthors")
    answersAnalysisResult.mostLikedAuthors = mostLikedAuthors(n)
    TimeDebugger.exitSection()
    this
  }

  def analysisSentimentTrend():AnswersAnalyzer = {
    TimeDebugger.enterSection("AnswersAnalyzer.analysisSentimentTrend")
    answersAnalysisResult.sentimentTrend = sentimentTrend()
    TimeDebugger.exitSection()
    this
  }

  def analysisHeatTrend():AnswersAnalyzer = {
    TimeDebugger.enterSection("AnswersAnalyzer.analysisHeatTrend")
    answersAnalysisResult.heatTrend = heatTrend()
    TimeDebugger.exitSection()
    this
  }

  def getResult:AnswersAnalysisResult = {answersAnalysisResult}


}
