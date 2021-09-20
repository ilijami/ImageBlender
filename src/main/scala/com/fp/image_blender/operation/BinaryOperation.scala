package com.fp.image_blender.operation

object BinaryOperation {

  def addition(op1: Byte, op2: Byte): Byte = {
    Math.min((op1.toInt & 0xFF) + (op2.toInt & 0xFF), 255).toByte
  }

  def subtraction(op1: Byte, op2: Byte): Byte = {
    Math.max((op1.toInt & 0xFF) - (op2.toInt & 0xFF), 0).toByte
  }

  def inverseSubtraction(op1: Byte, op2: Byte): Byte = {
    Math.max((op2.toInt & 0xFF) - (op1.toInt & 0xFF), 0).toByte
  }

  def multiplication(op1: Byte, op2: Byte): Byte = {
    Math.min((op2.toInt & 0xFF) * (op1.toInt & 0xFF), 255).toByte
  }

  def division(op1: Byte, op2: Byte): Byte = {
    if (op2 == 0) return op1
    ((op1.toInt & 0xFF) / (op2.toInt & 0xFF)).toByte
  }

  def inverseDivision(op1: Byte, op2: Byte): Byte = {
    if (op1 == 0) return op2
    ((op2.toInt & 0xFF) / (op1.toInt & 0xFF)).toByte
  }

  def power(op1: Byte, op2: Byte): Byte = {
    Math.min(Math.pow(op1.toInt & 0xFF, op2.toInt & 0xFF), 255).toByte
  }

  def min(op1: Byte, op2: Byte): Byte = {
    Math.max(Math.min(op1.toInt & 0xFF, op2.toInt & 0xFF), 0).toByte
  }

  def max(op1: Byte, op2: Byte): Byte = {
    Math.min(Math.max(op1.toInt & 0xFF, op2.toInt & 0xFF), 255).toByte
  }


}
