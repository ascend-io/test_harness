package io.spark.function.test_harness

import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.sql.types.StructType
import scala.util.Try

object Harness extends scala.App {

  args match {
    case Array(className, jar, output, inputs@_*) =>

      implicit val sparkSession =
        SparkSession.builder().master("local")
          .config("spark.jars", jar)
          .config("spark.ui.enabled", "false")
          .config("spark.sql.catalogImplementation", "in-memory")
          .getOrCreate()
      val sparkContext = sparkSession.sparkContext

      val classLoader =
        Thread.currentThread()
          .getContextClassLoader.asInstanceOf[org.apache.spark.util.MutableURLClassLoader]
      classLoader.addURL(new java.io.File(jar).toURI().toURL())
      val cls = classLoader.loadClass(className)
      val instance = cls.getConstructor().newInstance()

      import sparkSession.implicits._

      val maybeInputSchemas =
        Try {
          cls.getMethod("schemas", classOf[SparkSession], classOf[Seq[_]])
        }.toOption.map {
          _.invoke(instance, sparkSession, inputs).asInstanceOf[Seq[Option[StructType]]]
        }.getOrElse {
          Seq.fill(inputs.length){ None }
        }

      val inputDatasets =
        inputs.zip(maybeInputSchemas).map { case (input, maybeSchema) =>
          val reader = sparkSession.read

          val maybeWithSchema = maybeSchema.map {
            reader.schema(_)
          }.getOrElse { reader }

          maybeWithSchema.format(formatFromPath(input)).load(input)
        }

      val transform = cls.getMethod("transform", classOf[SparkSession], classOf[Seq[_]])
      val transformed =
        transform.invoke(instance, sparkSession, inputDatasets).asInstanceOf[Dataset[_]]

      transformed
        .write
        .mode("overwrite")
        .format(formatFromPath(output))
        .save(output)

    case _ =>
      System.err.println("usage: TestHarnesss className jar output-location input-locations*")
      System.exit(-1)
  }

  def formatFromPath(path: String): String =
    path match {
      case p if p.endsWith(".json") => "json"
      case p if p.endsWith(".parquet") => "parquet"
      case p if p.endsWith(".csv") => "csv"
    }
}
