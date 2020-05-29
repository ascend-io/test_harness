package io.ascend.spark.function.test_harness.example

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.StructType

object Read extends App {
  val Array(input, output) = args

  val spark =
    SparkSession.builder()
      .master("local")
      .config("spark.ui.enabled", "false")
      .config("spark.sql.catalogImplementation", "in-memory")
      .getOrCreate()

  import spark.implicits._

  val schema =
    ScalaReflection.schemaFor[Transaction].dataType.asInstanceOf[StructType]

  val df = spark.read
    .format("csv")
    .option("header", "true")
    .load(input)
    .map { Transaction(_) }

  df.write
    .mode("overwrite")
    .format(formatFromPath(output))
    .save(output)

  spark.stop()

  def formatFromPath(path: String): String =
    path match {
      case p if p.endsWith(".json") => "json"
      case p if p.endsWith(".csv") => "csv"
      case p if p.endsWith(".parquet") => "parquet"
      case _ => ???
    }

}
