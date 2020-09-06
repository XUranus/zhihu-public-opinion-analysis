package model

import com.google.gson.JsonParser
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.util.Bytes
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}
import udal.HBaseManpulator

case class Question(
                     id:Long,
                     title:String,
                     createTime:Long,
                     updateTime:Long
                   )


object Question {


  implicit val questionWrites: Writes[Question] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "title").write[String] and
      (JsPath \ "createdTime").write[Long] and
      (JsPath \ "updatedTime").write[Long]
    )(unlift(Question.unapply))


  def parseJsonString(text:String): Question = {
    val jsonObj = JsonParser.parseString(text).getAsJsonObject
    Question(
      jsonObj.get("question_id").getAsLong,
      jsonObj.get("title").getAsString,
      jsonObj.get("created_time").getAsLong,
      jsonObj.get("update_time").getAsLong
    )
  }

  def insertToHBase(question: Question): Unit = {
    val (namespace, tableName) = ("zhihu", "question")
    val rowKey = question.id.toString
    if(!HBaseManpulator.existRow(namespace, tableName, rowKey)) {
      HBaseManpulator.insertData(namespace, tableName, rowKey, "property",
        Array(
          ("id", question.id.toString),
          ("title", question.title),
          ("createTime", question.createTime.toString),
          ("updateTime", question.updateTime.toString)
        ))
    }
  }

  def loadFromHBase(rowKey:String): Question = {
    val result = HBaseManpulator.getRow("zhihu", "question", rowKey)
    result match {
      case Some(result) => loadFromHBase(result)
      case _ => Question(0 , "", 0, 0)
    }
  }

  def loadFromHBase(row:Result): Question = {
    Question(
      Bytes.toString(row.getValue("property".getBytes(), "id".getBytes())).toLong,
      Bytes.toString(row.getValue("property".getBytes(), "title".getBytes())),
      Bytes.toString(row.getValue("property".getBytes(), "createTime".getBytes())).toLong,
      Bytes.toString(row.getValue("property".getBytes(), "updateTime".getBytes())).toLong
    )
  }

}