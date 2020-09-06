package model

import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import model.AnswersAnalysisResultFields.{
  HighFrequencyAuthorsField, HottestQuestionsField, KeywordsField, MostLikedAuthorsField, MostVotedAnswersField, RelatedKeywordsField, SentimentDistributionField,
HeatTrendField, SentimentTrendField}

import scala.collection.mutable


class AnswersAnalysisResult {


  //analysis the sentiment percentage distribution of all articles
  var sentimentDistribution: Array[(Int, Int)] = _

  //word segment of answers content
  var keywords: Array[(String,Int)] = _

  //keywords related to $keywords, make sure keyword pair exist in keywords
  var relatedKeywords: Array[(String,Int)] = _

  //questions which gain most answers
  var hottestQuestions: Array[(Question,Int)] = _

  //answers gain most votes
  var mostVotedAnswers: Array[(Answer,User,Question)] = _

  //take n authors appear most frequently, return [(user, appear times), ...]
  var highFrequencyAuthors: Array[(User,Int)] = _

  //take n authors have most vote ups, return [(user, votes num), ...]
  var mostLikedAuthors: Array[(User,Int)] = _

  //get nums answers updated per hour , (begin timestamp, end timestamp, new answers per hour)
  var heatTrend: (Array[(String, Float)], Array[(String, Float)], Array[(String,Float)]) = _

  var sentimentTrend: (Array[(String, Float)], Array[(String, Float)], Array[(String,Float)]) = _

  //nameMap is use to map target name to self defined name
  var nameMap:mutable.HashMap[AnswersAnalysisResultFields.Value,String] = new mutable.HashMap[AnswersAnalysisResultFields.Value,String]()
  Map(
    SentimentDistributionField -> "sentimentDistribution",
    KeywordsField -> "keywords",
    RelatedKeywordsField -> "relatedKeywords",
    HottestQuestionsField -> "hottestQuestions",
    MostVotedAnswersField -> "mostVotedAnswers",
    HighFrequencyAuthorsField -> "highFrequencyAuthors",
    MostLikedAuthorsField -> "mostLikedAuthors",
    HeatTrendField -> "heatTrend",
    SentimentTrendField -> "sentimentTrend",
  ).foreach( kv => nameMap.put(kv._1, kv._2))


  def setNameMap(map: Map[AnswersAnalysisResultFields.Value,String]): Unit = {
    map.foreach(kv=>{nameMap.put(kv._1, kv._2)})
  }


  def toJson: JsObject = {
    var result = Json.obj()
    if(keywords!=null) {
      result = result + (nameMap(KeywordsField) -> JsArray(keywords.map(
        item => Json.obj(
          "word" -> item._1,
          "count" -> item._2)
      )))
    }
    if(relatedKeywords!=null) {
      result = result + (nameMap(RelatedKeywordsField) -> JsArray(relatedKeywords.map(
        item => Json.obj(
          "word" -> item._1,
          "count" -> item._2)
      )))
    }
    if(sentimentDistribution!=null) {
      result = result + (nameMap(SentimentDistributionField) -> JsArray(sentimentDistribution.map(
        item => Json.obj(
          "type" -> item._1,
          "count" -> item._2)
      )))
    }
    if(hottestQuestions!=null) {
      result = result + (nameMap(HottestQuestionsField) -> JsArray(hottestQuestions.map(
        item => Json.obj(
          "question" -> item._1,
          "answersNum" -> item._2)
      )))
    }
    if(mostVotedAnswers!=null) {
      result = result + (nameMap(MostVotedAnswersField) -> JsArray(mostVotedAnswers.map(
        item => Json.obj(
          "answer" -> item._1,
          "user" -> item._2,
          "question" -> item._3)
      )))
    }
    if(highFrequencyAuthors!=null) {
      result = result + (nameMap(HighFrequencyAuthorsField) -> JsArray(highFrequencyAuthors.map(
        item => Json.obj(
          "user" -> item._1,
          "times" -> item._2
        )
      )))
    }
    if(mostLikedAuthors!=null) {
      result = result + (nameMap(MostLikedAuthorsField) -> JsArray(mostLikedAuthors.map(
        item => Json.obj(
          "user" -> item._1,
          "count" -> item._2)
      )))
    }
    if(heatTrend!=null) {
      result = result + (nameMap(HeatTrendField) -> Json.obj(
          "hourTrend" -> JsArray(heatTrend._1.map(
            item=>(Json.obj(
              "time" -> item._1,
              "value" -> item._2
            )))),
          "dayTrend" -> JsArray(heatTrend._2.map(
            item=>(Json.obj(
              "time" -> item._1,
              "value" -> item._2
            )))),
          "dayPredict" -> JsArray(heatTrend._3.map(
            item=>(Json.obj(
              "time" -> item._1,
              "value" -> item._2
            )))),
      ))
    }
    if(sentimentTrend!=null) {
      result = result + (nameMap(SentimentTrendField) -> Json.obj(
        "hourTrend" -> JsArray(sentimentTrend._1.map(
          item=>(Json.obj(
            "time" -> item._1,
            "value" -> item._2
          )))),
        "dayTrend" -> JsArray(sentimentTrend._2.map(
          item=>(Json.obj(
            "time" -> item._1,
            "value" -> item._2
          )))),
        "dayPredict" -> JsArray(sentimentTrend._3.map(
          item=>(Json.obj(
            "time" -> item._1,
            "value" -> item._2
          )))),
      ))
    }

    result
  }



}


