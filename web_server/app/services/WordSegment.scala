package services

import org.ansj.domain.Term
import org.ansj.recognition.impl.StopRecognition
import org.ansj.splitWord.analysis.ToAnalysis
import utils.ConfigLoader

import scala.io.Source

object WordSegment {

  val stopWordsFileName: String = ConfigLoader.STOP_WORDS_FILE_PATH
  val stopWords: Array[String] = loadStopWordsFromFile(stopWordsFileName)
  val stopRecognition:StopRecognition = loadStopRecognition()

  def loadStopWordsFromFile(stopWordsFile:String):Array[String]  ={
    val source = Source.fromFile(stopWordsFile,"UTF-8")
    val stopWords = source.getLines().toArray
    source.close()
    stopWords
  }

  //load stop words recognition
  def loadStopRecognition(): StopRecognition ={
    val stopRecognition = new StopRecognition
    stopRecognition.insertStopWords("")
    stopRecognition.insertStopWords("\n")
    stopWords.foreach(word => stopRecognition.insertStopWords(word))
    stopRecognition.insertStopNatures("m")//mount
    stopRecognition.insertStopNatures("v")//verb
    stopRecognition.insertStopNatures("a")//adj
    stopRecognition.insertStopNatures("en")//english
    stopRecognition.insertStopNatures("w")//biao dian
    stopRecognition
  }

  def purifyText(content:String): String = {
    val contentPure = content
      .replaceAll("<[^>]*>", "")
      .replaceAll("&nbsp", "")
    contentPure
  }


  def seg(content:String): Array[String] = {
    //filter html tag
    val contentPure = purifyText(content)
    ToAnalysis
      .parse(contentPure)
      .recognition(stopRecognition)//filter stop words
      .getTerms
      .toArray
      .map(x => x.asInstanceOf[Term])
      .filter(x=>x.natrue().natureStr.contains("n"))//only preserve noun
      .map(x=>x.getName)
      .filter(w=>w.length>=2 && w.length <= 10)//only preserve length > 2 and < 10
      .distinct
  }


}
