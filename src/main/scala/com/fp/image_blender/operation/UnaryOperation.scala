package com.fp.image_blender.operation

object UnaryOperation {

  def log(op1: Byte): Byte = {
    Math.min(Math.log(op1.toInt & 0xFF), 255).toByte
  }

  def abs(op1: Byte): Byte = {
    Math.min(Math.abs(op1.toInt & 0xFF), 255).toByte
  }

  def inversion(op1: Byte): Byte = {
    (255 - (op1.toInt & 0xFF)).toByte
  }


}
