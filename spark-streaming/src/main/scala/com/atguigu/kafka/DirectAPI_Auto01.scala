package com.atguigu.kafka


import kafka.serializer.StringDecoder
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * @Author lw
  * @Create2020-09-27 10:20
  */
object DirectAPI_Auto01 {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("DirectAPI_Auto01").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf,Seconds(3))

    val kafkaParams = Map[String, String](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
      ConsumerConfig.GROUP_ID_CONFIG -> "bigdata191122"

    )


    val kafkaDStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams,Set("atguigu"))
    kafkaDStream
      .map(_._2)
      .flatMap(_.split( " "))
      .map((_,1))
      .reduceByKey(_+_)
      .print()


    ssc.start()
    ssc.awaitTermination()

  }
}
