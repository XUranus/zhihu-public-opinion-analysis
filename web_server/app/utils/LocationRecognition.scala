package utils

import com.google.gson.JsonParser
import play.mvc.BodyParser.Json

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object LocationRecognition {

  //recognize province city
  val locationFile: String = ConfigLoader.LOCATION_FILE_PATH
  val data: Array[(String,String)] = loadData //province city


  def loadData:Array[(String,String)] = {
    val source = Source.fromFile(locationFile,"UTF-8")
    val content = source.mkString
    val jsonObj = JsonParser.parseString(content).getAsJsonArray
    source.close()
    val data = new ArrayBuffer[(String,String)]()
    jsonObj.forEach(obj=>{
      data.append((obj.getAsJsonObject.get("province").getAsString, obj.getAsJsonObject.get("name").getAsString))
    })
    data.toArray
  }

  def recognize(locationString:String):String = {
    if(locationString=="") return ""
    var province = ""
    data.foreach(pair => {
      if(pair._1.contains(locationString) || pair._2.contains(locationString)) {
        province = pair._1
      }
    })
    province
  }


}
