package com.fp.image_blender

import scalafx.beans.property.{BooleanProperty, DoubleProperty}
import scalafx.scene.image.{Image, WritableImage}

class Layer {
  var name: String = ""
  var imagePath: String = ""
  var opacity = new DoubleProperty()
  var isActive = new BooleanProperty()
  var isExpanded = new BooleanProperty()
  var effects: List[String] = List()
  var writableImage: WritableImage = null

  def this(name: String, imagePath: String) = {
    this()
    this.opacity.value = 1
    this.name = name
    this.imagePath = imagePath
    this.isActive.value = false
    this.isExpanded.value = false
    val image = new Image("file:" + imagePath)
    writableImage = new WritableImage(image.pixelReader.get, image.width.value.toInt, image.height.value.toInt)
  }

  def this(name: String, imagePath: String, isActive: Boolean) = {
    this(name, imagePath)
    this.isActive.value = isActive
    this.isExpanded.value = false
  }

  def this(name: String, imagePath: String, isActive: Boolean, isExpanded: Boolean) = {
    this(name, imagePath, isActive)
    this.isExpanded.value = isExpanded
  }

  def this(name: String, imagePath: String, isActive: Boolean, isExpanded: Boolean, opacity: Double) = {
    this(name, imagePath, isActive)
    this.isExpanded.value = isExpanded
    this.opacity.value = opacity
  }

  override def toString: String = {
    name + "|" + imagePath + "|" + isActive.value + "|" + opacity.value
  }
}
