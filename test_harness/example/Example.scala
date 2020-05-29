package io.ascend.spark.function.test_harness.example

import org.apache.spark.sql.{Dataset, SparkSession}

class Example  {
  def transform(spark: SparkSession, inputs: Seq[Dataset[_]]): Dataset[_] = {
    import spark.implicits._
    val input = inputs(0).as[Transaction]
    input
  }
}
