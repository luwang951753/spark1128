package com.atguigu.kafka

import java.sql.Time
import java.util.Properties

import kafka.serializer.StringDecoder
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

/**
  * 自动维护offset的第二种方式
  * 有两个缺点
  *   1.小文件过多
  *   2.spark-streaming挂的时间越久，他就会把过去时间的数据都补上
  */


/**
  * Author lzc
  * Date 2019-04-29 16:37
  */
object DirectAPI_Auto02 {

  def getStreamingContext(): StreamingContext = {
    // kafka 参数
    //kafka参数声明
    val brokers = "localhost:9092"
    val topic = "atguigu"
    val group = "bigdata"
    val deserialization = "org.apache.kafka.common.serialization.StringDeserializer"
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("WordCount")

    // 2. 使用SparkConf创建StreamingContext
    val sctx = new StreamingContext(conf, Seconds(5))
    var params: Map[String, String] = Map(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.GROUP_ID_CONFIG -> group
      /*ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> deserialization,
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> deserialization*/
    )

    sctx.checkpoint("./ck5")

    //使用DirectAPI自动维护offset的方式读取Kafka数据创建DStream
    val kafkaDStream: InputDStream[(String, String)] = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](sctx, params, Set(topic))


    //        val wordCounts: DStream[(String, Int)] = kafkaDStream.flatMap(t=>{t._2.split(" ")}).map((_,1)).reduceByKey(_+_)
    val wordCountDStream = kafkaDStream.transform(rdd => {
      rdd.flatMap(t=>t._2.split(" ")).map((_, 1)).reduceByKey(_ + _)
    })
    wordCountDStream.print()

    sctx
  }

  def main(args: Array[String]): Unit = {

      val sctx = StreamingContext.getActiveOrCreate("./ck5",() => getStreamingContext())

      sctx.start()
      sctx.awaitTermination()


    }
}

case class People(name :String, id: Int)
