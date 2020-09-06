package services

import enitity.{Answer, Question, User}
import udal.HBaseManpulator

object RecordResolver {

  //insert the word segment index from content text of answer
  def persistAnswerContentWordSegIndex(answer: Answer): Unit = {
    val (namespace, tableName) = ("zhihu", "words_index")
    val answerRowKey = answer.updatedTime.toString + answer.id.toString
    val timestamp = answer.updatedTime.toString
    val words = WordSegment.seg(answer.content)

    words.foreach(word => {
      val rowKey = word
      HBaseManpulator.insertData(namespace, tableName, rowKey, "answer_content",
        Array(
          (timestamp, answerRowKey)
        ))
    })
  }

  //insert the word segment index from title of question
  def persistQuestionTitleWordSegIndex(question: Question): Unit = {
    val (namespace, tableName) = ("zhihu", "words_index")
    val questionRowKey = question.id.toString
    val words = WordSegment.seg(question.title)
    val timestamp = question.createTime.toString

    words.foreach(word => {
      val rowKey = word
      HBaseManpulator.insertData(namespace, tableName, rowKey, "question_title",
        Array(
          (timestamp, questionRowKey)
        ))
    })
  }

  /*
  def persistWordsSegment(rowKey:String, fields:Array[(String,String)]): Boolean ={
    val (namespace, tableName) = ("zhihu", "words_seg")
    val pured = fields.map(field=>{
      val fieldName = field._1
      val content = field._2
      val words = WordSegment.seg(content)
      var result = ""
      words.foreach(word => result += (word + "/"))
      if(result.length>0)
        result = result.substring(0,result.length-1)
      (fieldName,result)
    })
    HBaseManpulator.insertData(namespace, tableName, rowKey, "data",pured)
  }
*/
  //


  def resolveAnswerRecord(text:String, persist:Boolean = false): (Answer,Array[String]) = {
    val answer = Answer.parseJsonString(text)
    answer.content = WordSegment.purifyText(answer.content)
    answer.sentiment = SentimentAnalysis.classify(answer.content)
    val words = WordSegment.seg(answer.content)
    val wholeWords = WordSegment.segWhole(answer.content)//seg -> seg whole ,user tf-idf later
    //answer.contentWordsSeg = wholeWords.foldRight("")(_+" "+_)
    answer.contentWordsSeg = words.foldRight("")(_+" "+_)

    if(persist) {
      Answer.insertToHBase(answer)
      persistAnswerContentWordSegIndex(answer) //word segment, and insert word index table
      //persistWordsSegment(answer.id+"answer", Array(("answer_content", answer.content)))
    }
    (answer,words)
  }

  def resolveQuestionRecord(text:String, persist:Boolean = false): Question = {
    val question = Question.parseJsonString(text)
    if(persist) {
      Question.insertToHBase(question)
      persistQuestionTitleWordSegIndex(question) //word segment, and insert word index table
      //persistWordsSegment(question.id+"question", Array(("question_title", question.title))) //maybe needn't?
    }
    question
  }

  def resolveUserRecord(text:String, persist:Boolean = false): User = {
    //println("resolveUserRecord")
    val user = User.parseJsonString(text)
    user.headlineWordsSeg = WordSegment.seg(user.headline).foldRight("")(_+" "+_)
    user.descriptionWordsSeg = WordSegment.seg(user.description).foldRight("")(_+" "+_)
    if(persist) {
      User.insertToHBase(user)
      //persistWordsSegment(user.id+"user", Array(("user_headline", user.headline),("user_description",user.description)))
    }
    user
  }

}
