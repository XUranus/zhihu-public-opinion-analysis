package model

import model.AnswersAnalysisResultFields.{HighFrequencyAuthorsField, HottestQuestionsField, KeywordsField, MostLikedAuthorsField, MostVotedAnswersField, RelatedKeywordsField, SentimentDistributionField}
import model.UsersAnalysisResultFields.{BusinessDistributionField, CompanyDistributionField, GenderDistributionField, JobDistributionField, LocationDistributionField, MajorDistributionField, MostFollowedUsersField, SchoolDistributionField, UsersPortraitField}
import play.api.libs.json.{JsArray, JsObject, Json}

import scala.collection.mutable

class UsersAnalysisResult {

  //author gender distribution
  var genderDistribution: Array[(Int,Int)] = _

  //take n users have most follower (big V)
  var mostFollowedUsers: Array[User] = _

  //analysis location
  var locationDistribution: Array[(String,Int)] = _

  //analysis business
  var businessDistribution: Array[(String,Int)] = _

  //analysis company
  var companyDistribution: Array[(String,Int)] = _

  //analysis job
  var jobDistribution: Array[(String,Int)] = _

  //analysis school
  var schoolDistribution: Array[(String,Int)] = _

  //analysis major
  var majorDistribution: Array[(String,Int)] = _

  //user portrait words (just another word cloud)
  var usersPortrait: Array[(String,Int)] = _


  //nameMap is use to map target name to self defined name
  var nameMap:mutable.HashMap[UsersAnalysisResultFields.Value,String] = new mutable.HashMap[UsersAnalysisResultFields.Value,String]()
  Map(
    GenderDistributionField -> "genderDistribution",
    MostFollowedUsersField -> "mostFollowedUsers",
    LocationDistributionField -> "locationDistribution",
    BusinessDistributionField -> "businessDistribution",
    CompanyDistributionField -> "companyDistribution",
    JobDistributionField -> "jobDistribution",
    SchoolDistributionField -> "schoolDistribution",
    MajorDistributionField -> "majorDistribution",
    UsersPortraitField  -> "usersPortrait"
  ).foreach( kv => nameMap.put(kv._1, kv._2))


  def setNameMap(map: Map[UsersAnalysisResultFields.Value,String]): Unit = {
    map.foreach(kv=>{nameMap.put(kv._1, kv._2)})
  }


  def toJson: JsObject = {
    var result = Json.obj()
    if(genderDistribution!=null) {
      result = result + (nameMap(GenderDistributionField) -> JsArray(genderDistribution.map(
        item => Json.obj(
          "type" -> item._1,
          "count" -> item._2)
      )))
    }
    if(mostFollowedUsers!=null) {
      result = result ++ Json.obj(nameMap(MostFollowedUsersField) ->  mostFollowedUsers)
    }
    if(locationDistribution!=null) {
      result = result + (nameMap(LocationDistributionField) -> JsArray(locationDistribution.map(
        item => Json.obj(
          "name" -> item._1,
          "count" -> item._2)
      )))
    }
    if(businessDistribution!=null) {
      result = result + (nameMap(BusinessDistributionField) -> JsArray(businessDistribution.map(
        item => Json.obj(
          "name" -> item._1,
          "count" -> item._2)
      )))
    }
    if(companyDistribution!=null) {
      result = result + (nameMap(CompanyDistributionField) -> JsArray(companyDistribution.map(
        item => Json.obj(
          "name" -> item._1,
          "count" -> item._2)
      )))
    }
    if(jobDistribution!=null) {
      result = result + (nameMap(JobDistributionField) -> JsArray(jobDistribution.map(
        item => Json.obj(
          "name" -> item._1,
          "count" -> item._2)
      )))
    }
    if(schoolDistribution!=null) {
      result = result + (nameMap(SchoolDistributionField) -> JsArray(schoolDistribution.map(
        item => Json.obj(
          "name" -> item._1,
          "count" -> item._2)
      )))
    }
    if(majorDistribution!=null) {
      result = result + (nameMap(MajorDistributionField) -> JsArray(majorDistribution.map(
        item => Json.obj(
          "name" -> item._1,
          "count" -> item._2)
      )))
    }
    if(usersPortrait!=null) {
      result = result + (nameMap(UsersPortraitField) -> JsArray(usersPortrait.map(
        item => Json.obj(
          "word" -> item._1,
          "count" -> item._2)
      )))
    }


    result
  }

}
