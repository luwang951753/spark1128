package com.atguigu

import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

/**
  * Author lzc
  * Date 2019-04-28 15:40
  */
object SqlDemo {
    def main(args: Array[String]): Unit = {



      val conf = new SparkConf()

      val spark = SparkSession
        .builder()
        .master("local[*]")
        //        .config("spark.some.config.option","some-vale")
        .config(conf)
        .appName("aaa")
        .getOrCreate()

      import spark.implicits._

      val connectionProperties = new Properties()
      connectionProperties.put("user", "root")
      connectionProperties.put("password", "000000")
      val jdbcDF2 = spark.read
        .jdbc("jdbc:mysql://hadoop102:3306/rdd", "rddtable", connectionProperties)
      jdbcDF2.show()

      //      val df = spark.read.json("input/user.json")
//
//      df.show()
//
//      df.filter($"age">10).show
//      df.createOrReplaceTempView("persons")
//
//      spark.sql("select * from persons where age > 11").show

//        val spark: SparkSession = SparkSession
//            .builder()
//            .master("local[*]")
//            .appName("Test")
//            .getOrCreate()
//        // 在rdd -> ds rdd->df df->ds的时候需要隐式转换
//        import spark.implicits._
//
//        // 2. spark.read...
//        val df: DataFrame = spark.read.json("C:\\Users\\lzc\\Desktop\\class_code\\2018_11_28\\06_scala\\spark1128\\spark-sql\\src\\main\\resources\\user.json")
//
////        df.createTempView("user")
//
////        spark.sql("select name from user").show
//        /*
//        df.rdd.collect.foreach{
//            row => println(row.getLong(0))
//        }*/
//        val ds: Dataset[User] = df.as[User]
//        ds.show
//
//        spark.stop()
    }
}

case class User(name: String, age: Long)