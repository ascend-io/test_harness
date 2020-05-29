package io.ascend.spark.function.test_harness.example

// N.B.: spark 2 does not support java.time.LocalDate
import java.sql.Date

import org.apache.spark.sql.Row

object Transaction {

  implicit class ToOption(string: String) {
    def toOptionInt: Option[Int] =
      Option(string).flatMap { string =>
        if (string == "null") None else Some(string.replace(",", "").toInt)
      }
    def toOptionString: Option[String] =
      Option(string).flatMap { string =>
        if (string == "null") None else Some(string)
      }
  }

  def apply(row: Row): Transaction =
    Transaction(
      borough = row.getAs[String]("BOROUGH").toInt,
      neighborhood = row.getAs[String]("NEIGHBORHOOD"),
      building_class_category = row.getAs[String]("BUILDING CLASS CATEGORY"),
      tax_class_at_present = row.getAs[String]("TAX CLASS AT PRESENT"),
      block = row.getAs[String]("BLOCK").toInt,
      lot = row.getAs[String]("LOT").toInt,
      ease_ment = row.getAs[String]("EASE-MENT").toOptionString,
      building_class_at_present = row.getAs[String]("BUILDING CLASS AT PRESENT"),
      address = row.getAs[String]("ADDRESS"),
      apartment_number = row.getAs[String]("APARTMENT NUMBER"),
      zip_code = row.getAs[String]("ZIP CODE").toOptionInt,
      residential_units = row.getAs[String]("RESIDENTIAL UNITS").toOptionInt,
      commercial_units = row.getAs[String]("COMMERCIAL UNITS").toOptionInt,
      total_units = row.getAs[String]("TOTAL UNITS").toOptionInt,
      land_square_feet = row.getAs[String]("LAND SQUARE FEET").toOptionInt,
      gross_square_feet = row.getAs[String]("GROSS SQUARE FEET").toOptionInt,
      year_built = row.getAs[String]("YEAR BUILT").toOptionInt,
      tax_class_at_time_of_sale = row.getAs[String]("TAX CLASS AT TIME OF SALE"),
      building_class_at_time_of_sale = row.getAs[String]("BUILDING CLASS AT TIME OF SALE"),
      sale_price = row.getAs[String](" SALE PRICE ").replace(",", "").toLong,
      sale_date = {
        val Array(month, day, year) = row.getAs[String]("SALE DATE").split("/")
        Date.valueOf(s"20$year/$month$day")
      }
    )
}

case class Transaction(
  borough: Int,
  neighborhood: String,
  building_class_category: String,
  tax_class_at_present: String,
  block: Int,
  lot: Int,
  ease_ment: Option[String],
  building_class_at_present: String,
  address: String,
  apartment_number: String,
  zip_code: Option[Int],
  residential_units: Option[Int],
  commercial_units: Option[Int],
  total_units: Option[Int],
  land_square_feet: Option[Int],
  gross_square_feet: Option[Int],
  year_built: Option[Int],
  tax_class_at_time_of_sale: String,
  building_class_at_time_of_sale: String,
  sale_price: Long,
  sale_date: Date
)
