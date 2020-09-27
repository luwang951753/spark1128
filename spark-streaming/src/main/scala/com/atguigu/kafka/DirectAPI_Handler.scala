package com.atguigu.kafka

import kafka.common.TopicAndPartition
import kafka.message.MessageAndMetadata
import kafka.serializer.StringDecoder
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.{HasOffsetRanges, KafkaUtils, OffsetRange}

/**
  * 手动维护offset方式(推荐)
  * 这样可以结合mysql做到精准一次消费
  */

/**
  * @Author lw
  * @Create2020-09-26 22:24
  */
object DirectAPI_Handler {
  def main(args: Array[String]): Unit = {

    val brokers = "localhost:9092"
    val topic = "atguigu"
    val group = "bigdata"
    val deserialization = "org.apache.kafka.common.serialization.StringDeserializer"
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("WordCount")

    var params: Map[String, String] = Map(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.GROUP_ID_CONFIG -> group
    )

    //获取上一次消费的位置信息 -->最好是从mysql中获取上一次消费的位置信息
    val fromOffsets =Map[TopicAndPartition,Long](
      TopicAndPartition("atguigu",0) -> 24700
    )

    val ssc = new StreamingContext(conf, Seconds(3))
    //使用DirectAPI手动维护offset的方式消费数据
    val kafkaDStream = KafkaUtils.createDirectStream[String, String, StringDecoder,StringDecoder,String](
      ssc,
      params,
      fromOffsets,
      (m: MessageAndMetadata[String, String]) => m.message()
    )
    //定义空集合用于存放数据的offset
    var offsetRanges = Array.empty[OffsetRange]

    //将当前消费到的offset进行保存
    val dstream = kafkaDStream.transform {
      rdd => {
        offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        rdd
      }
    }.foreachRDD{
      rdd =>
        rdd.foreach(println)
        //将当前批次消费的数据位置信息写入mysql
        for(o <- offsetRanges){
          println(s"${o.fromOffset}-${o.untilOffset}")
        }
    }

    ssc.start()
    ssc.awaitTermination()


  }
}
