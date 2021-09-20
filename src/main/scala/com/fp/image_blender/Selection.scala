package com.fp.image_blender

import scalafx.beans.property.{BooleanProperty, ObjectProperty}
import scalafx.geometry.Point2D
import scalafx.scene.paint.Color

class Selection {
  var name: String = ""
  var isActive = new BooleanProperty()
  var pointA: ObjectProperty[Point2D] = new ObjectProperty[Point2D]()
  var pointB: ObjectProperty[Point2D] = new ObjectProperty[Point2D]()
  var isExpanded = new BooleanProperty()
  var color: ObjectProperty[Color] = new ObjectProperty[Color](){
    value = Color.Transparent
  }

  def this(name: String) = {
    this()
    this.pointA.value = new Point2D(0, 0)
    this.pointB.value = new Point2D(0, 0)
    this.name = name
    this.isActive.value = false
  }

  def this(name: String, isActive: Boolean) = {
    this(name)
    this.isActive.value = isActive
  }

  def this(name: String, isActive: Boolean, pointA: Point2D, pointB: Point2D) = {
    this(name)
    this.isActive.value = isActive
    this.pointA.value = pointA
    this.pointB.value = pointB
    this.isExpanded.value = false
  }

  def this(name: String, isActive: Boolean, pointA: Point2D, pointB: Point2D, color: Color) = {
    this(name, isActive, pointA, pointB)
    this.color.value = color
  }

  def width(): Double = {
    pointB.value.x - pointA.value.x
  }
  def height(): Double = {
    pointB.value.y - pointA.value.y
  }

  override def toString: String = {
    name + "|" + isActive.value + "|" + pointA.value.x + "|" + pointA.value.y + "|" + pointB.value.x + "|" + pointB.value.y + "|"+ color.value.red + "|" + color.value.green + "|" + color.value.blue + "|" + color.value.opacity
  }
}
