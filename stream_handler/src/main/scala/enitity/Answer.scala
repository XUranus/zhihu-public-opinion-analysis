package enitity


import com.google.gson.JsonParser
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.util.Bytes
import udal.HBaseManpulator

case class Answer(
                   id:Long,
                   questionId:Long,
                   createdTime:Long,
                   updatedTime:Long,
                   voteUpCount:Int,
                   commentCount:Int,
                   var content:String, //for inject purify
                   authorUrlToken:String,
                   authorId:String,

                   var sentiment:Int = 0, //for inject purify
                   var contentWordsSeg:String = ""
                 )


object Answer {


  def parseJsonString(text:String): Answer = {
    val jsonObj = JsonParser.parseString(text).getAsJsonObject
    Answer(
      jsonObj.get("answer_id").getAsLong,
      jsonObj.get("question_id").getAsLong,
      jsonObj.get("created_time").getAsLong,
      jsonObj.get("updated_time").getAsLong,
      jsonObj.get("voteup_count").getAsInt,
      jsonObj.get("comment_count").getAsInt,
      jsonObj.get("content").getAsString,
      jsonObj.get("author_url_token").getAsString,
      jsonObj.get("author_id").getAsString,
      0
    )
  }


  def insertToHBase(answer: Answer): Unit = {
    val (namespace, tableName) = ("zhihu", "answer")
    val rowKey = answer.updatedTime.toString + answer.id.toString
    //TODO：try better way to reduce key length
    if(!HBaseManpulator.existRow(namespace, tableName, rowKey)) {
      HBaseManpulator.insertData(namespace, tableName, rowKey, "property",
        Array(
          ("id", answer.id.toString),
          ("questionId", answer.questionId.toString),
          ("createdTime", answer.createdTime.toString),
          ("updatedTime", answer.updatedTime.toString),
          ("voteUpCount", answer.voteUpCount.toString),
          ("commentCount", answer.commentCount.toString),
          ("content", answer.content),
          ("authorUrlToken", answer.authorUrlToken),
          ("authorId", answer.authorId)
        ))
      HBaseManpulator.insertData(namespace, tableName, rowKey, "extra",
        Array(
          ("sentiment", answer.sentiment.toString),
          ("content_words_seg", answer.contentWordsSeg)
        ))
    }
  }


  def loadFromHBase(rowKey:String): Answer = {
    val result = HBaseManpulator.getRow("zhihu", "answer", rowKey)
    result match {
      case Some(result) => loadFromHBase(result)
      case _ => Answer(0,0,0,0,0,0,"","","",0)
    }
  }

  def loadFromHBase(row:Result): Answer = {
    Answer(
      Bytes.toString(row.getValue("property".getBytes(), "id".getBytes())).toLong,
      Bytes.toString(row.getValue("property".getBytes(), "questionId".getBytes())).toLong,
      Bytes.toString(row.getValue("property".getBytes(), "createdTime".getBytes())).toLong,
      Bytes.toString(row.getValue("property".getBytes(), "updatedTime".getBytes())).toLong,
      Bytes.toString(row.getValue("property".getBytes(), "voteUpCount".getBytes())).toInt,
      Bytes.toString(row.getValue("property".getBytes(), "commentCount".getBytes())).toInt,
      Bytes.toString(row.getValue("property".getBytes(), "content".getBytes())),
      Bytes.toString(row.getValue("property".getBytes(), "authorUrlToken".getBytes())),
      Bytes.toString(row.getValue("property".getBytes(), "authorId".getBytes())),
      Bytes.toString(row.getValue("extra".getBytes(), "sentiment".getBytes())).toInt,
      Bytes.toString(row.getValue("extra".getBytes(), "content_words_seg".getBytes()))
    )
  }


}
