package utils

import java.io._

import services.RecordResolver
import udal.HBaseManpulator


object ManuallyImportData {
  //manually import users.txt answers.txt questions.txt to HBase

  var resolvedNum = 0

  //load data from *.txt
  def loadFromFile(fileName:String, process:(String)=>Unit): Unit = {
    val in:BufferedReader =  new BufferedReader(
      new InputStreamReader(
        new BufferedInputStream(
          new FileInputStream(
            new File(fileName))),"utf-8"))
    var str = in.readLine()
    while(str != null) {
      if(!str.equals("") && !str.equals("\n"))
        process(str)
      str = in.readLine()
      resolvedNum += 1
      if(resolvedNum%100==0) println("Resolved "+resolvedNum)
    }
    in.close()
  }

  def loadUsersFromFile(fileName:String): Unit = {
    println("building user table from "+fileName)
    loadFromFile(fileName, (text) => {RecordResolver.resolveUserRecord(text, persist = true)})
    println("Done.")
  }

  def loadQuestionsFromFile(fileName:String): Unit = {
    println("building question table from "+fileName)
    loadFromFile(fileName, (text) => {RecordResolver.resolveQuestionRecord(text, persist = true)})
    println("Done.")
  }

  def loadAnswersFromFile(fileName:String): Unit = {
    println("building answer table from "+fileName)
    loadFromFile(fileName, (text) => {RecordResolver.resolveAnswerRecord(text, persist = true)})
    println("Done.")
  }


  def buildAllSchema():Unit = {
    HBaseManpulator.createNameSpace("zhihu")
    HBaseManpulator.createHTable("zhihu","question","property","extra")
    HBaseManpulator.createHTable("zhihu","user","property","extra")
    HBaseManpulator.createHTable("zhihu","answer","property","extra")
    HBaseManpulator.createHTable("zhihu","words_index","answer_content","question_title")
    //HBaseUtil.createHTable("zhihu","words_seg","data") //for speed optimization
    //words_index answer_id-time answer_id-count
  }

  def dropAllHBase(): Unit ={
    HBaseManpulator.deleteHTable("zhihu","question")
    HBaseManpulator.deleteHTable("zhihu","user")
    HBaseManpulator.deleteHTable("zhihu","answer")
    HBaseManpulator.deleteHTable("zhihu","words_index")
    //HBaseUtil.deleteHTable("zhihu","words_seg")
    HBaseManpulator.deleteNameSpace("zhihu");
  }

  def dropAllAndRebuildFromPath(datasetPath:String): Unit = {
    dropAllHBase()
    buildAllSchema()
    loadUsersFromFile(datasetPath + "/users_v2.txt")
    loadQuestionsFromFile(datasetPath + "/questions.txt")
    loadAnswersFromFile(datasetPath + "/answers.txt")
  }

  /**
   * |-------------zhihu:question------------------|
   * |rowKey|--------------data--------------------|
   * |--id--|----|----|-----|----|-----|-----|-----|
   *
   * |-------------zhihu:words_index--------------|
   * |rowKey|--------------answers----------------|
   * |-word-|----timestamp---|----|----|-----|----|
   * |-word-|--answerRowKey--|----|-----|----|----|
   */

  def main(args:Array[String]): Unit ={
    //dropAllHBase()
    dropAllAndRebuildFromPath("/home/xuranus/Desktop/爬虫数据/4.1_4.5_final")
    //WordSegment.seg("你好我是中国人日本人").foreach(println)

  }

}
