package services

import utils.{LocationRecognition, SparkCreator, TimeDebugger}
import model.{User, UserSegCache, UsersAnalysisResult}



class UsersAnalyzer(val users:Array[User]) {

  /*
  caution:
   users are mapped from 'answer.authorId' ,
   which means authors may not be distinct!
  */

  val usersAnalysisResult = new UsersAnalysisResult

  //gender distribution
  def genderDistribution(): Array[(Int,Int)] = {
    val sc = SparkCreator.newContext()
    val result = sc.parallelize(users)
      .map(user => (user.gender, 1))
      .reduceByKey(_+_)
      .collect()
    result
  }


  //take n users have most follower (big V)
  def mostFollowedUsers(n:Int = 10): Array[User] = {
    val sc = SparkCreator.newContext()
    val result = sc.parallelize(users)
      .sortBy(user => user.followerCount, ascending = false)
      .filter(_.name!="")
      .take(10 * n)  //accelerate
      .distinct
      .take(n)
    result
  }


  //analysis location
  def locationDistribution(n:Int): Array[(String,Int)] = {
    val sc = SparkCreator.newContext()
    val result = sc.parallelize(users)
      .map(user => (LocationRecognition.recognize(user.location), 1))
      .reduceByKey(_+_)
      .filter(x=>x._1!="")
      .sortBy(pair => pair._2, ascending = false)
      .collect()
    result
  }

  //analysis business
  def businessDistribution(n:Int): Array[(String,Int)] = {
    val sc = SparkCreator.newContext()
    val result = sc.parallelize(users)
      .map(user => (user.business, 1))
      .filter(x=>x._1!="")
      .reduceByKey(_+_)
      .sortBy(pair => pair._2, ascending = false)
      .take(n)
    result
  }

  //analysis company
  def companyDistribution(n:Int): Array[(String,Int)] = {
    val sc = SparkCreator.newContext()
    val result = sc.parallelize(users)
      .map(user => (user.company, 1))
      .filter(x=>x._1!="")
      .reduceByKey(_+_)
      .sortBy(pair => pair._2, ascending = false)
      .take(n)
    result
  }

  //analysis job
  def jobDistribution(n:Int): Array[(String,Int)] = {
    val sc = SparkCreator.newContext()
    val result = sc.parallelize(users)
      .map(user => (user.job, 1))
      .filter(x=>x._1!="")
      .reduceByKey(_+_)
      .sortBy(pair => pair._2, ascending = false)
      .take(n)
    result
  }

  //analysis school
  def schoolDistribution(n:Int): Array[(String,Int)] = {
    val sc = SparkCreator.newContext()
    val result = sc.parallelize(users)
      .map(user => (user.school, 1))
      .filter(x=>x._1!="")
      .reduceByKey(_+_)
      .sortBy(pair => pair._2, ascending = false)
      .take(n)
    result
  }

  //analysis major
  def majorDistribution(n:Int): Array[(String,Int)] = {
    val sc = SparkCreator.newContext()
    val result = sc.parallelize(users)
      .map(user => (user.major, 1))
      .filter(x=>x._1!="")
      .reduceByKey(_+_)
      .sortBy(pair => pair._2, ascending = false)
      .take(n)
    result
  }

  //user portrait words (just another word cloud)
  def usersPortrait(n: Int): Array[(String,Int)] = {
    val sc = SparkCreator.newContext()
    val filterWords = Array("公众")

    /*
     val result = sc.parallelize(users)
       .flatMap(user => (user.headlineWordsSeg + user.descriptionWordsSeg).split(" ").filter(x=>x!=" "))
       .filter(x => !filterWords.contains(x))
       .map((_,1))
       .reduceByKey(_+_)
       .sortBy(pair => pair._2, ascending = false)
       .take(n)*/

    val textRDD = sc.parallelize(users)
        .map(user => user.headlineWordsSeg + user.descriptionWordsSeg)
    val result = TFIDFKeyWord
      .generateKeywords(textRDD,n)
      .filter(x => !filterWords.contains(x._1))

    result
  }

  def anonymousUsersNum():Int = {
    val sc = SparkCreator.newContext()
    val result = sc.parallelize(users)
    result.filter(user => user.id=="").collect().length
  }



  def analysisGenderDistribution():UsersAnalyzer = {
    TimeDebugger.enterSection("UsersAnalyzer.analysisGenderDistribution")
    usersAnalysisResult.genderDistribution = genderDistribution()
    TimeDebugger.exitSection()
    this
  }

  def analysisMostFollowedUsers(n:Int = 10):UsersAnalyzer = {
    TimeDebugger.enterSection("UsersAnalyzer.analysisMostFollowedUsers")
    usersAnalysisResult.mostFollowedUsers = mostFollowedUsers(n)
    TimeDebugger.exitSection()
    this
  }

  def analysisLocationDistribution(n:Int = 10):UsersAnalyzer = {
    TimeDebugger.enterSection("UsersAnalyzer.analysisLocationDistribution")
    usersAnalysisResult.locationDistribution = locationDistribution(n)
    TimeDebugger.exitSection()
    this
  }

  def analysisBusinessDistribution(n:Int = 10):UsersAnalyzer = {
    TimeDebugger.enterSection("UsersAnalyzer.analysisBusinessDistribution")
    usersAnalysisResult.businessDistribution = businessDistribution(n)
    TimeDebugger.exitSection()
    this
  }

  def analysisCompanyDistribution(n:Int = 10):UsersAnalyzer = {
    TimeDebugger.enterSection("UsersAnalyzer.analysisCompanyDistribution")
    usersAnalysisResult.companyDistribution = companyDistribution(n)
    TimeDebugger.exitSection()
    this
  }

  def analysisJobDistribution(n:Int = 10):UsersAnalyzer = {
    TimeDebugger.enterSection("UsersAnalyzer.analysisJobDistribution")
    usersAnalysisResult.jobDistribution = jobDistribution(n)
    TimeDebugger.exitSection()
    this
  }

  def analysisSchoolDistribution(n:Int = 10):UsersAnalyzer = {
    TimeDebugger.enterSection("UsersAnalyzer.analysisSchoolDistribution")
    usersAnalysisResult.schoolDistribution = schoolDistribution(n)
    TimeDebugger.exitSection()
    this
  }

  def analysisMajorDistribution(n:Int = 10):UsersAnalyzer = {
    TimeDebugger.enterSection("UsersAnalyzer.analysisMajorDistribution")
    usersAnalysisResult.majorDistribution = majorDistribution(n)
    TimeDebugger.exitSection()
    this
  }

  def analysisUsersPortrait(n:Int = 100):UsersAnalyzer = {
    TimeDebugger.enterSection("UsersAnalyzer.analysisUsersPortrait")
    usersAnalysisResult.usersPortrait = usersPortrait(n)
    TimeDebugger.exitSection()
    this
  }

  def getResult:UsersAnalysisResult = {usersAnalysisResult}


}