package controllers

import javax.inject._
import model.{Answer,User,Question}
import play.api.libs.json.{Json, _}
import play.api.libs.functional.syntax._
import play.api.mvc._
import udal.DataFilter.{AnswersOfQuestion, AnswersWithKeywordsWithinTime, AnswersWithinTime}
import services.{AnswersAnalyzer, UsersAnalyzer, WordSegment}
import utils.{DateUtil, TimeDebugger}

/**
 * This controller creates an `Action` to handle HTTP requests to the application's analysis json.
 */
@Singleton
class AnalysisController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  //begin & end are yyyy-MM-dd date
  //start=2020-03-20&end=2020-04-05&keyword=疫情
  def handleAnswersWithKeywordsWithinTimeAnalysis(begin:String, end:String, text:String): Action[AnyContent] = Action {
    TimeDebugger.start()
    val beginTime = DateUtil.dateStringToDate("yyyy-MM-dd",begin)
    val endTime = DateUtil.dateStringToDate("yyyy-MM-dd",end)

    val keywords = WordSegment.seg(text)
    val answers = AnswersWithKeywordsWithinTime(keywords, beginTime, endTime)
    val authors = answers.map(answer=> User.loadFromHBase(answer.authorId))

    if(answers.length == 0) {
      Ok(Json.obj(
        "success" -> false,
        "message" ->"没有找到相关信息"
      ))
    } else {
      val answersAnalyzer = new AnswersAnalyzer(answers)
      val usersAnalyzer = new UsersAnalyzer(authors)
      val answersAnalysisResult = answersAnalyzer
        .analysisSentimentDistribution()
        .analysisHottestQuestions(10)
        .analysisRelatedKeywords(keywords, 50)
        .analysisHighFrequencyAuthors(10)
        .analysisMostLikedAuthors(10)
        .analysisSentimentTrend()
        .analysisHeatTrend()
        .analysisMostVotedAnswers(5)
        .getResult
        .toJson
      val usersAnalysisResult = usersAnalyzer
        .analysisGenderDistribution()
        .analysisMostFollowedUsers(10)
        .analysisCompanyDistribution(10)
        .analysisBusinessDistribution(10)
        .analysisJobDistribution(10)
        .analysisSchoolDistribution(10)
        .analysisLocationDistribution(10)
        .analysisUsersPortrait(50)
        .getResult
        .toJson

      val statisticResult = Json.obj(
        "keywords" -> keywords,
        "answersNum" -> answers.length,
        "questionsNum" -> answersAnalyzer.uniqueQuestionsNum(),
        "anonymousUsersNum" -> usersAnalyzer.anonymousUsersNum()
      )

      Ok(Json.obj(
        "success" -> true,
        "message" -> "analysis answers with keyword within time success",
        "timeCosts" -> TimeDebugger.finish(),
        "begin" -> begin,
        "end" -> end,
        "text" -> text,
        "analysisResult" -> (statisticResult++answersAnalysisResult++usersAnalysisResult)
      ))
    }

  }


  //questionsIds: 123_234_453
  def handleAnswersOfQuestionAnalysis(questionIds:String): Action[AnyContent] = Action {
    TimeDebugger.start()
    val questionRowKeys = questionIds.split("_")
    //initialize answers , authors, analyzers
    val questions = questionRowKeys
      .map(rowKey=>Question.loadFromHBase(rowKey))
      .filter(question=>question.id!=0)
    val answers = questions.flatMap(question=>AnswersOfQuestion(question))
    val authors = answers.map(answer=> User.loadFromHBase(answer.authorId))

    if(answers.length == 0) {
      Ok(Json.obj(
        "success" -> false,
        "message" ->"没有找到相关信息"
      ))
    } else {
      val answersAnalyzer = new AnswersAnalyzer(answers)
      val usersAnalyzer = new UsersAnalyzer(authors)
      val answersAnalysisResult = answersAnalyzer
        .analysisSentimentDistribution()
        .analysisKeywords(50)
        .analysisHighFrequencyAuthors(10)
        .analysisMostLikedAuthors(10)
        .analysisMostVotedAnswers(5)
        .analysisSentimentTrend()
        .analysisHeatTrend()
        .getResult
        .toJson
      val usersAnalysisResult = usersAnalyzer
        .analysisGenderDistribution()
        .analysisMostFollowedUsers(10)
        .analysisCompanyDistribution(10)
        .analysisBusinessDistribution(10)
        .analysisJobDistribution(10)
        .analysisSchoolDistribution(10)
        //.analysisMajorDistribution(10)
        .analysisLocationDistribution(10)
        .analysisUsersPortrait(50)
        .getResult
        .toJson

      val statisticResult = Json.obj(
        "questions" -> questions,
        "answersNum" -> answers.length,
        "questionsNum" -> questions.length,
        "anonymousUsersNum" -> usersAnalyzer.anonymousUsersNum()
      )

      Ok(Json.obj(
        "success" -> true,
        "message" -> "analysis answers with keyword within time success",
        "timeCosts" -> TimeDebugger.finish(),
        "analysisResult" -> (statisticResult++answersAnalysisResult++usersAnalysisResult)
      ))
    }


  }



  def handleAnswersWithinTimeAnalysis(begin:String, end:String): Action[AnyContent] = Action {
    TimeDebugger.start()
    //initialize answers , authors, analyzers
    val beginTime = DateUtil.dateStringToDate("yyyy-MM-dd",begin)
    val endTime = DateUtil.dateStringToDate("yyyy-MM-dd",end)
    val answers = AnswersWithinTime(beginTime, endTime)
    val authors = answers.map(answer=> User.loadFromHBase(answer.authorId))

    if(answers.length == 0) {
      Ok(Json.obj(
        "success" -> false,
        "message" ->"没有找到相关信息"
      ))
    } else {
      val answersAnalyzer = new AnswersAnalyzer(answers)
      val usersAnalyzer = new UsersAnalyzer(authors)
      val answersAnalysisResult = answersAnalyzer
        .analysisSentimentDistribution()
        .analysisKeywords(50)
        .analysisHighFrequencyAuthors(10)
        .analysisMostLikedAuthors(10)
        .analysisSentimentTrend()
        .analysisHeatTrend()
        .analysisHottestQuestions(10)
        .analysisMostVotedAnswers(5)
        .getResult
        .toJson
      val usersAnalysisResult = usersAnalyzer
        .analysisGenderDistribution()
        .analysisMostFollowedUsers(10)
        .analysisCompanyDistribution(10)
        .analysisBusinessDistribution(10)
        .analysisJobDistribution(10)
        .analysisSchoolDistribution(10)
        //.analysisMajorDistribution(10)
        .analysisLocationDistribution(10)
        .analysisUsersPortrait(50)
        .getResult
        .toJson

      val statisticResult = Json.obj(
        "answersNum" -> answers.length,
        "questionsNum" -> answersAnalyzer.uniqueQuestionsNum(),
        "anonymousUsersNum" -> usersAnalyzer.anonymousUsersNum()
      )

      Ok(Json.obj(
        "success" -> true,
        "message" -> "analysis answers with keyword within time success",
        "timeCosts" -> TimeDebugger.finish(),
        "begin" -> begin,
        "end" -> end,
        "analysisResult" -> (statisticResult++answersAnalysisResult++usersAnalysisResult)
      ))
    }


  }



}
