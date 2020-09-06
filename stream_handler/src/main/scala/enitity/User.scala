package enitity


import com.google.gson.JsonParser
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.util.Bytes
import udal.HBaseManpulator

case class User(
                 id: String,
                 urlToken: String,
                 avatarUrl: String,
                 gender: Int,
                 headline: String,
                 name: String,
                 followerCount:Int,
                 userType: String,
                 description: String,
                 location: String,
                 company: String,
                 job: String,
                 business: String,
                 school: String,
                 major: String,

                 var descriptionWordsSeg:String = "",
                 var headlineWordsSeg:String = ""
               )



object User {


  def parseJsonString(text:String): User = {
    val jsonObj = JsonParser.parseString(text).getAsJsonObject
    User(
      jsonObj.get("user_id").getAsString,
      jsonObj.get("url_token").getAsString,
      jsonObj.get("avatar_url").getAsString,
      jsonObj.get("gender").getAsInt,
      jsonObj.get("headline").getAsString,
      jsonObj.get("name").getAsString,
      jsonObj.get("follower_count").getAsInt,
      jsonObj.get("type").getAsString,
      jsonObj.get("description").getAsString,
      jsonObj.get("location").getAsString,
      jsonObj.get("company").getAsString,
      jsonObj.get("job").getAsString,
      jsonObj.get("business").getAsString,
      jsonObj.get("school").getAsString,
      jsonObj.get("major").getAsString
    )
  }


  def insertToHBase(user:User): Unit = {
    val (namespace, tableName) = ("zhihu", "user")
    val rowKey = user.id
    if(!HBaseManpulator.existRow(namespace, tableName, rowKey)) {
      HBaseManpulator.insertData(namespace, tableName, rowKey, "property",
        Array(
          ("id", user.id),
          ("urlToken", user.urlToken),
          ("avatarUrl", user.avatarUrl),
          ("gender", user.gender.toString),
          ("headline", user.headline),
          ("name", user.name),
          ("followerCount", user.followerCount.toString),
          ("userType", user.userType),
          ("description", user.description),
          ("location", user.location),
          ("company", user.company),
          ("job", user.job),
          ("business", user.business),
          ("school", user.school),
          ("major", user.major)
        ))
      HBaseManpulator.insertData(namespace, tableName, rowKey, "extra",
        Array(
          ("headline_words_seg", user.headlineWordsSeg),
          ("description_words_seg", user.descriptionWordsSeg)
        ))
    }
  }

  def loadFromHBase(rowKey:String): User = {
    val result = HBaseManpulator.getRow("zhihu", "user", rowKey)
    result match {
      case Some(row) => loadFromHBase(row)
      case _ => User("", "", "", 0, "", "", 0, "", "", "", "", "", "", "", "")
    }
  }

  def loadFromHBase(row:Result): User = {
    User(
      Bytes.toString(row.getValue("property".getBytes(), "id".getBytes())),
      Bytes.toString(row.getValue("property".getBytes(), "urlToken".getBytes())),
      Bytes.toString(row.getValue("property".getBytes(), "avatarUrl".getBytes())),
      Bytes.toString(row.getValue("property".getBytes(), "gender".getBytes())).toInt,
      Bytes.toString(row.getValue("property".getBytes(), "headline".getBytes())),
      Bytes.toString(row.getValue("property".getBytes(), "name".getBytes())),
      Bytes.toString(row.getValue("property".getBytes(), "followerCount".getBytes())).toInt,
      Bytes.toString(row.getValue("property".getBytes(), "userType".getBytes())),
      Bytes.toString(row.getValue("property".getBytes(), "description".getBytes())),
      Bytes.toString(row.getValue("property".getBytes(), "location".getBytes())),
      Bytes.toString(row.getValue("property".getBytes(), "company".getBytes())),
      Bytes.toString(row.getValue("property".getBytes(), "job".getBytes())),
      Bytes.toString(row.getValue("property".getBytes(), "business".getBytes())),
      Bytes.toString(row.getValue("property".getBytes(), "school".getBytes())),
      Bytes.toString(row.getValue("property".getBytes(), "major".getBytes())),

      Bytes.toString(row.getValue("extra".getBytes(), "description_words_seg".getBytes())),
      Bytes.toString(row.getValue("extra".getBytes(), "headline_words_seg".getBytes()))
    )
  }


}
