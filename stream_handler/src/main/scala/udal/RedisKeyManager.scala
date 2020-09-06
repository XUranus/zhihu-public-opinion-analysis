package udal

import java.text.SimpleDateFormat

object RedisKeyManager {

  val totalAnswersNum = "zhihu_total_answers_num"

  def todayAnswersNum:String = {
    val today = new SimpleDateFormat("YYYY_MM_dd").format(new java.util.Date())
    val key = "zhihu_answers_num_"+today
    key
  }

  val totalQuestionsNum = "zhihu_total_questions_num"

  def todayQuestionsNum:String = {
    val today = new SimpleDateFormat("YYYY_MM_dd").format(new java.util.Date())
    val key = "zhihu_questions_num_"+today
    key
  }

  val dataNumChannel = "zhihu_data_num_channel"

  val hotWords = "zhihu_hot_words"

  val questionsTotalAnswerNum = "zhihu_question_answer_num"
  val questionsNegativeAnswersNum = "zhihu_question_negative_num"


}
