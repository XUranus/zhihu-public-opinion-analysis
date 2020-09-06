package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import javax.inject._
import model.Answer
import udal.WebSocketActor
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import udal.RealtimeService

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class RealtimeController @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer)
  extends AbstractController(cc) {

  def status: Action[AnyContent] = Action {
    val hotWords = RealtimeService.getHotWords(100)
    val hotQuestions = RealtimeService.getHotQuestions(5)
    val negativeQuestions = RealtimeService.getNegativeQuestions(5)
    //val todayAnswersNum = RealtimeService.getTodayAnswersNum
    //val todayQuestionsNum = RealtimeService.getTodayQuestionsNum
    //val totalAnswersNum = RealtimeService.getTotalAnswersNum
    //val totalQuestionsNum = RealtimeService.getTotalQuestionsNum

    Ok(Json.obj(
      "success" -> true,
      "message" -> "success get real time status",
      "status" -> Json.obj(
        "hotWords" -> hotWords.map(x => Json.obj(
          "word" -> x._1,
          "count" -> x._2
        )),
        "hotQuestions" -> hotQuestions.map(x => Json.obj(
          "question" -> x._1,
          "answersNum" -> x._2
        )),
        "negativeQuestions" -> negativeQuestions.map(x => Json.obj(
          "question" -> x._1,
          "rate" -> x._2
        ))
        //"todayQuestionsNum" -> todayQuestionsNum,
        //"todayAnswersNum" -> todayAnswersNum,
        //"totalQuestionsNum" -> totalQuestionsNum,
        //"totalAnswersNum" -> totalAnswersNum
      )
    ))
  }


  def socket: WebSocket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef { out =>
      WebSocketActor.props(out)
    }
  }

WebSocketActor
}