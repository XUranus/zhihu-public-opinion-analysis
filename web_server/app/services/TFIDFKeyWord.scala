package services

import org.apache.spark.ml.feature.{HashingTF, IDF, Tokenizer}
import org.apache.spark.ml.linalg.SparseVector
import org.apache.spark.mllib.feature
import org.apache.spark.rdd.RDD
import udal.DataFilter.AnswersWithinTime
import utils.{DateUtil, SparkCreator}

object TFIDFKeyWord {

  def main(args: Array[String]): Unit = {
    val (begin,end) = ("2020-03-23","2020-04-05")
    val beginTime = DateUtil.dateStringToDate("yyyy-MM-dd",begin)
    val endTime = DateUtil.dateStringToDate("yyyy-MM-dd",end)
    val answers = AnswersWithinTime(beginTime, endTime)
    val textRDD = SparkCreator.newContext().parallelize(answers).map(_.contentWordsSeg)

    generateKeywords(textRDD,50).foreach(println)
  }

  def generateKeywords(textRDD:RDD[String],n:Int,numFeatures:Int = 30000,eachTextKeyWordsNum:Int = 30 ): Array[(String,Int)] = {
    val sqlContext = SparkCreator.newSqlContext()
    import sqlContext.implicits._

    val dataFrame = textRDD.toDF("sentence")
    val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
    val words = tokenizer.transform(dataFrame)
    val hashingTF = new HashingTF().setInputCol("words").setOutputCol("rawFeatures").setNumFeatures(numFeatures)
    val featuredData = hashingTF.transform(words)

    val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
    val idfModel = idf.fit(featuredData)
    val idfData = idfModel.transform(featuredData)

    val t = new feature.HashingTF(numFeatures).setBinary(hashingTF.getBinary)
    val wordMap = featuredData.select("words").rdd.flatMap(
      row=>row.getAs[Seq[String]](0).map(w => (t.indexOf(w),w))
    ).collect().toMap

    val keyWords = idfData.select("features").rdd.map(
      x=>{
        val v = x.getAs[SparseVector](0)
        v.indices.zip(v.values).sortWith((a,b)=>a._2>b._2).take(eachTextKeyWordsNum).map(pair=>(wordMap.getOrElse(pair._1, ""),pair._2))
      }
    )

    val result = keyWords.flatMap(x=>x)
      .filter(x=>x._1!="")
      .map(x=>(x._1,1))
      .reduceByKey(_+_)
      .sortBy(x=>x._2,false)
      .take(n)

    result
  }

}
