package controllers

import javax.inject._
import play.api.mvc._
import play.api.db.{NamedDatabase,Database}
import play.api.libs.json._
import services.Auth


@Singleton
class AuthController @Inject()(@NamedDatabase("default") db: Database, cc: ControllerComponents)(implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc) {

  def login(mail:String, password:String): Action[AnyContent] = Action {
    new Auth(db).login(mail,password)
    println(mail,password)
    Ok(Json.obj(
      "success"->true,
      "message" -> "success login",
      "authority" -> "admin"
    ))
  }



}
