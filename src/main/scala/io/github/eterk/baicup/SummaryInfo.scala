package io.github.eterk.baicup

import java.time.LocalDateTime

case class SummaryInfo(scanTime: LocalDateTime,
                       map: Map[String, FormatInfo]) {
  override def toString: String = {


    val str =
      scanTime.toString +:
        map
          .toSeq
          .sortBy(x => x._2.fileSize)
          .reverse
          .map(_.toString())

    str.mkString("\n")

  }
}