package services

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.factory.Nd4j

object SentimentAnalysis {

  val SENTIMENT_MODEL_FILE_PATH = "/home/xuranus/Desktop/sentimentClassification/senti4.h5"
  val EMBEDDING_TABLE_FILE_PATH = "/home/xuranus/Desktop/sentimentClassification/sgns.zhihu.bigram-char"

  val word2Vec: Word2Vec = WordVectorSerializer.readWord2VecModel(EMBEDDING_TABLE_FILE_PATH)
  val model: MultiLayerNetwork = KerasModelImport.importKerasSequentialModelAndWeights(SENTIMENT_MODEL_FILE_PATH)

  //String -> Array[Int]
  def sentence2Tokens(text:String, pad:Boolean = false, maxLen:Int = 100): Array[Int] ={
    val words = WordSegment.segWhole(text)
    val tokens = words.map(word => {
      if(word2Vec.hasWord(word)) {
        word2Vec.indexOf(word)
      } else {
        0
      }
    })
    if(pad) {
      sequencePaddingPre(tokens, maxLen)
    } else {
      tokens
    }
  }

  //Array[Int] -> String
  def tokens2Sentence(tokens:Array[Int]): String = {
    val vocab = word2Vec.vocab()
    val words = tokens.map(token => vocab.wordAtIndex(token))
    words.foldLeft("")((pre, next) => pre + next)
  }


  //pad zeros
  def sequencePaddingPre(tokens:Array[Int], maxLen: Int) :Array[Int] = {
    if(tokens.length >= maxLen) {
      tokens.slice(0, maxLen)
    } else {
      val padPre = new Array[Int](maxLen - tokens.length)
      padPre ++ tokens
    }
  }

  def classify(text:String): Int = {
    //TODO:fake sentiment classification
    //return text.length % 2
    val tokens = sentence2Tokens(text, pad = true, maxLen = 80)
    val input = Nd4j.createFromArray(tokens.map(x => new Integer(x))).reshape(Array(1,80))
    val output = model.output(input)
    //println(output)
    /*
    if(output.getFloat(0L) >= 0.5) {
      1
    } else {
      0
    }
     */
    output.argMax(1).getInt(0)
  }


  /**
   * def main(args: Array[String]): Unit = {
   * println(classify("谢天实在是太牛逼啦"))
   * println(classify("这个居然这么强"))
   * println(classify("质量太差了不喜欢"))
   * println(classify("开心开心真开心开心开心真开心开心开心真开心开心开心真开心开心开心真开心开心开心真开心开心开心真开心"))
   * println(classify("可恶的谢天可恶的谢天可恶的谢天可恶的谢天可恶的谢天可恶的谢天可恶的谢天可恶的谢天可恶的谢天"))
   * }
   */

}
