package com.fp.image_blender.operation

object FilterOperation {

  def median(s: Array[Int]): Byte = {
    val (lower, upper) = s.sortWith((l, r) => l < r).splitAt(s.length / 2)
    val ret = if (s.length % 2 == 0) ((lower.last + upper.head) / 2).toByte else upper.head.toByte
    ret
  }

  def ponderedAverage(s: Array[Int], ponders: Array[Int]): Byte = {
    if (s.length != ponders.length) {
      println("Invalid ponder list provided")
      return 0
    }
    var sum = 0
    var pondersSum = 0
    for(i <- s.indices) {
      sum += s(i) * ponders(i)
      pondersSum += ponders(i)
    }

    (sum / pondersSum).toByte
  }
}

