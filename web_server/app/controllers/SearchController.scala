package controllers

import javax.inject._
import play.api.libs.json._
import play.api.mvc._
import services.WordSegment

import udal.DataFilter.{QuestionsWithKeywords}
@Singleton
class SearchController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {


  def searchQuestionByKeywords(text:String): Action[AnyContent] = Action {
    val keywords = WordSegment.seg(text)
    val questions = QuestionsWithKeywords(keywords,50)

    Ok(Json.obj(
      "success" -> true,
      "keywords" -> keywords,
      "questions" -> questions
    ))
  }


}