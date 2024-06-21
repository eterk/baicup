package io.github.eterk.baicup


import io.circe.{Decoder, Encoder}


import org.scalatest.funsuite.AnyFunSuiteLike

class MainTest extends AnyFunSuiteLike {

  //  def align(seq: Seq[String]): Seq[String] = {
  //
  //    val keys = seq.map(_.length).distinct
  //    val maxSize = keys.max
  //    val map =
  //      keys
  //        .map(k => k -> getInsertLoc(k, maxSize))
  //        .toMap
  //    seq.map(str => insert(str, map(str.length)))
  //  }

  /**
   *
   * @param size    seq 长度
   * @param maxSize 需要填充后的长度
   * @return 一个二元组集合，第一个元素表示插入的索引位置（第一个元素前面插入索引为-1， 最后一个元素后面插入索引为 size），第二个元素是在该位置插入的元素的个数
   */
  //    def getInsertLoc(size: Int, maxSize: Int): Seq[(Int, Int)]
  //
  //
  //    def insert(str: String, index: Seq[(Int, Int)]): String



  test("dsa") {
    val seq = Seq("as", "werid", "matter", "lemon", "ask", "banana", "ice cream")
    //    align(seq).foreach(println)
  }

  test("b") {


    val str = ControlParam.toJSON(ControlParam.proto)
    println(str)
    require(ControlParam.fromJSONStr(str).nonEmpty)


  }




}

case class Foo(yes: Boolean)