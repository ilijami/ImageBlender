package com.fp.image_blender.operation

// Mask is `xxxxxRGB`
object ByteArrayOperationApplier {

  def applyUnary(mask: Byte, pixels: Array[Byte], operation: Byte => Byte): Array[Byte] = {
    val newPixels = new Array[Byte](pixels.length)
    for (i <- pixels.indices by 4) {
      newPixels(i) = if ((mask & 0x01) != 0) operation(pixels(i)) else pixels(i)
      newPixels(i + 1) = if ((mask & 0x02) != 0) operation(pixels(i + 1)) else pixels(i + 1)
      newPixels(i + 2) = if ((mask & 0x04) != 0) operation(pixels(i + 2)) else pixels(i + 2)
      newPixels(i + 3) = pixels(i + 3)
    }
    newPixels
  }

  def applyBinary(mask: Byte, pixels: Array[Byte], operation: (Byte, Byte) => Byte, value: Byte): Array[Byte] = {
    val newPixels = new Array[Byte](pixels.length)
    for (i <- pixels.indices by 4) {
      newPixels(i) = if ((mask & 0x01) != 0) operation(pixels(i), value) else pixels(i)
      newPixels(i + 1) = if ((mask & 0x02) != 0) operation(pixels(i + 1), value) else pixels(i + 1)
      newPixels(i + 2) = if ((mask & 0x04) != 0) operation(pixels(i + 2), value) else pixels(i + 2)
      newPixels(i + 3) = pixels(i + 3)
    }
    newPixels
  }

  def applyMedian(mask: Byte, w: Int, h: Int, pixels: Array[Byte], n: Int): Array[Byte] = {
    val newPixels = new Array[Byte](pixels.length)
    for (i <- pixels.indices by 4) {
      val neighbourPixels = findNeighbourPixels((i / 4) % w , (i / 4) / w , w, h, pixels, n)
      val bPixs = neighbourPixels.grouped(4).map(_.head).toArray
      val gPixs = neighbourPixels.drop(1).grouped(4).map(_.head).toArray
      val rPixs = neighbourPixels.drop(2).grouped(4).map(_.head).toArray
      newPixels(i) = if ((mask & 0x01) != 0) FilterOperation.median(bPixs) else pixels(i)
      newPixels(i + 1) = if ((mask & 0x02) != 0) FilterOperation.median(gPixs) else pixels(i + 1)
      newPixels(i + 2) = if ((mask & 0x04) != 0) FilterOperation.median(rPixs) else pixels(i + 2)
      newPixels(i + 3) = pixels(i + 3)
    }
    newPixels
  }

  def applyPonderedAverage(mask: Byte, w: Int, h: Int, pixels: Array[Byte], ponders: Array[Int], n: Int): Array[Byte] = {
    val newPixels = new Array[Byte](pixels.length)
    for (i <- pixels.indices by 4) {
      val neighbourPixels = findNeighbourPixels((i / 4) % w , (i / 4) / w , w, h, pixels, n)
      val bPixs = neighbourPixels.grouped(4).map(_.head).toArray
      val gPixs = neighbourPixels.drop(1).grouped(4).map(_.head).toArray
      val rPixs = neighbourPixels.drop(2).grouped(4).map(_.head).toArray
      newPixels(i) = if ((mask & 0x01) != 0) FilterOperation.ponderedAverage(bPixs, ponders) else pixels(i)
      newPixels(i + 1) = if ((mask & 0x02) != 0) FilterOperation.ponderedAverage(gPixs, ponders) else pixels(i + 1)
      newPixels(i + 2) = if ((mask & 0x04) != 0) FilterOperation.ponderedAverage(rPixs, ponders) else pixels(i + 2)
      newPixels(i + 3) = pixels(i + 3)
    }
    newPixels
  }

  def applyGrayscale(pixels: Array[Byte]): Array[Byte] = {
    val newPixels = new Array[Byte](pixels.length)
    for (i <- pixels.indices by 4) {
      val newColor = Math.min(((pixels(i).toInt & 0xFF) + (pixels(i +1).toInt & 0xFF) + (pixels(i + 2).toInt & 0xFF))/3, 255).toByte
      newPixels(i) = newColor
      newPixels(i + 1) = newColor
      newPixels(i + 2) = newColor
      newPixels(i + 3) = pixels(i + 3)
    }
    newPixels
  }

  def findNeighbourPixels(c: Int, r: Int, w: Int, h: Int, pixels: Array[Byte], n: Int): Array[Int] = {
    val relevantPixels = new Array[Int]((2*n + 1) * (2*n + 1) * 4)
    var i = 0
    for (row <- r-n until r + n + 1){
      for (col <- c - n until c + n + 1){
        if (row < 0 || row > h - 1 || col < 0 || col > w - 1) {
          relevantPixels(i) = 0
          relevantPixels(i + 1) = 0
          relevantPixels(i + 2) = 0
          relevantPixels(i + 3) = 0
          i += 4
        } else {
          relevantPixels(i) = pixels(row*w*4 + col*4).toInt & 0xFF
          relevantPixels(i + 1) = pixels(row*w*4 + col*4 + 1).toInt & 0xFF
          relevantPixels(i + 2) = pixels(row*w*4 + col*4 + 2).toInt & 0xFF
          relevantPixels(i + 3) = pixels(row*w*4 + col*4 + 3).toInt & 0xFF
          i += 4
        }
      }
    }
    relevantPixels
  }
}
