package com.fp.image_blender.view

import com.fp.image_blender.{Layer, Selection}
import javafx.animation._
import javafx.scene.shape.{Rectangle, StrokeType}
import javafx.util.Duration
import scalafx.collections.ObservableBuffer
import scalafx.scene.Group
import scalafx.scene.image.ImageView
import scalafx.scene.paint.Color

object ImageBlend {

  def createImageView(layer: Layer): ImageView = {
    val imageView = new ImageView(layer.writableImage)
    imageView.id = layer.name
    //imageView.setFitHeight(1200)
    //imageView.setPreserveRatio(true);
    imageView.opacity.bind(layer.opacity)
    imageView
  }

  def createSelection(selection: Selection): Rectangle = {
    val rect = new Rectangle(
      selection.pointA.value.x,
      selection.pointA.value.y,
      selection.width().toInt,
      selection.height().toInt)

    rect.setId(selection.name)
    rect.setStroke(Color.Black)
    rect.strokeTypeProperty().setValue(StrokeType.INSIDE)
    rect.setFill(selection.color.value)
    rect.setStyle("-fx-background-color: transparent; -fx-stroke: black; -fx-stroke-width: 3; -fx-stroke-dash-array: 10 5; -fx-stroke-dash-offset: 6; -fx-stroke-line-cap: butt;")

    val timeline = new Timeline(
      new KeyFrame(Duration.ZERO, new KeyValue(rect.strokeDashOffsetProperty().asInstanceOf[javafx.beans.value.WritableValue[Any]], 0, Interpolator.LINEAR)),
      new KeyFrame(Duration.seconds(2), new KeyValue(rect.strokeDashOffsetProperty().asInstanceOf[javafx.beans.value.WritableValue[AnyVal]], 15, Interpolator.LINEAR))
    )

    timeline.cycleCountProperty().setValue(Animation.INDEFINITE)
    timeline.play()
    rect
  }

  def fillActiveLayersWithColor(selection: Selection, activeLayers: ObservableBuffer[Layer]): Unit = {
    for(layer <- activeLayers) {
      val pw = layer.writableImage.pixelWriter
      for(x <- selection.pointA.value.x.toInt until selection.pointB.value.x.toInt;
          y <- selection.pointA.value.y.toInt until selection.pointB.value.y.toInt)
        if (x < layer.writableImage.width.value && y < layer.writableImage.height.value) {
          pw.setColor(x, y, selection.color.value)
        }
    }
  }

  def rectanglesOverlap(r1: Rectangle, r2: Rectangle): Boolean = {
    r2.getY + r2.getHeight <= r1.getY + r1.getHeight && r2.getX + r2.getWidth <= r1.getX + r1.getWidth && r2.getX > r1.getX && r2.getY > r1.getY
  }

  def removeItemFromBlendView(itemName: String, blendView: Group): Unit ={
    // TODO: Same item added multiple times, check this
    val items = blendView.children.filter(c => c.getId == itemName)
    if (items.length > 0) {
      blendView.children.removeAll(items)
    }
  }

  def removeAllItemsFromBlendView(blendView: Group): Unit = {
    if (blendView.children.length > 0) {
      blendView.children.removeAll(blendView.children)
    }
  }

}
