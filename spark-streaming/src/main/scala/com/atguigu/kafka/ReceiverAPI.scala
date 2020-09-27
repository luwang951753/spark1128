package com.atguigu.kafka

import kafka.serializer.StringDecoder
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Author lzc
  * Date 2019-04-29 16:37
  */
object ReceiverAPI {

    def main(args: Array[String]): Unit = {

        //1.创建SparkConf并初始化SSC
        val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("KafkaSparkStreaming")
        val ssc = new StreamingContext(sparkConf, Seconds(5))

        //2.定义kafka参数
        val brokers = "localhost:9092"
        val topic = "atguigu"
        val consumerGroup = "spark"

        //3.将kafka参数映射为map
        val kafkaParam: Map[String, String] = Map[String, String](
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
            ConsumerConfig.GROUP_ID_CONFIG -> consumerGroup,
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers
        )


//
//        //4.通过KafkaUtil创建kafkaDSteam
        val kafkaDStream = KafkaUtils.createStream(
            ssc,
            //            kafkaParam,
            //            Set(topic),
            //            StorageLevel.MEMORY_ONLY
            "localhost:2181",
            "bigdata1122",
            Map(topic -> 1)
        )


        //5.对kafkaDSteam做计算（WordCount）
//        kafkaDStream.foreachRDD {
//            rdd => {
//                val word: RDD[String] = rdd.flatMap(_._2.split(" "))
//                val wordAndOne: RDD[(String, Int)] = word.map((_, 1))
//                val wordAndCount: RDD[(String, Int)] = wordAndOne.reduceByKey(_ + _)
//                wordAndCount.collect().foreach(println)
//            }
//        }
        val lineDStream = kafkaDStream.map(_._2)
        val word = lineDStream.flatMap(_.split(" "))
        val wordToOneDStream = word.map((_,1))
        val wordToCountDStream = wordToOneDStream.reduceByKey(_+_)
        wordToCountDStream.print()



        //6.启动SparkStreaming
        ssc.start()
        ssc.awaitTermination()
    }
}

