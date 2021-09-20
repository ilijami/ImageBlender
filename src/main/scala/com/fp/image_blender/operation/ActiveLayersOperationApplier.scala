package com.fp.image_blender.operation

import com.fp.image_blender.{Layer, Selection}
import com.fp.image_blender.view.ImageBlend.rectanglesOverlap
import javafx.scene.shape.Rectangle
import scalafx.collections.ObservableBuffer
import scalafx.scene.image.PixelFormat

object ActiveLayersOperationApplier {
  def applyGrayscale(activeLayers: ObservableBuffer[Layer], activeSelections: ObservableBuffer[Selection]): Unit = {
    for (layer <- activeLayers) {
      val pw = layer.writableImage.pixelWriter
      val pr = layer.writableImage.pixelReader.get
      if (activeSelections.length > 0) {
        for (activeSelection <- activeSelections) {
          val width = activeSelection.width().toInt
          val height = activeSelection.height().toInt

          val buffer = new Array[Byte](width * height * 4)
          val pixelFormat = PixelFormat.getByteBgraPreInstance

          if (rectanglesOverlap(new Rectangle(0, 0, layer.writableImage.width.value.toInt, layer.writableImage.height.value.toInt), new Rectangle(activeSelection.pointA.value.x.toInt, activeSelection.pointA.value.y.toInt, width, height))) {

            pr.getPixels(activeSelection.pointA.value.x.toInt, activeSelection.pointA.value.y.toInt, width, height, pixelFormat, buffer, 0, width * 4)

            val newPixels = ByteArrayOperationApplier.applyGrayscale(buffer)
            pw.setPixels(activeSelection.pointA.value.x.toInt, activeSelection.pointA.value.y.toInt, width, height, pixelFormat, newPixels, 0, width * 4)
          }
        }
      } else {
        val width = layer.writableImage.width.value.toInt
        val height = layer.writableImage.height.value.toInt

        val buffer = new Array[Byte](width * height * 4)
        val pixelFormat = PixelFormat.getByteBgraPreInstance

        pr.getPixels(0, 0, width, height, pixelFormat, buffer, 0, width * 4)

        val newPixels = ByteArrayOperationApplier.applyGrayscale(buffer)
        pw.setPixels(0, 0, width, height, pixelFormat, newPixels, 0, width * 4)
      }
    }
  }

  def applyUnary(r: Boolean, g: Boolean, b: Boolean, operation: Byte => Byte, activeLayers: ObservableBuffer[Layer], activeSelections: ObservableBuffer[Selection]): Unit = {
    for (layer <- activeLayers) {
      val pw = layer.writableImage.pixelWriter
      val pr = layer.writableImage.pixelReader.get
      if (activeSelections.length > 0) {
        for (activeSelection <- activeSelections) {
          val width = activeSelection.width().toInt
          val height = activeSelection.height().toInt

          val buffer = new Array[Byte](width * height * 4)
          val pixelFormat = PixelFormat.getByteBgraPreInstance

          var mask: Byte = 0
          if (r) mask = (mask | 0x04).toByte
          if (g) mask = (mask | 0x02).toByte
          if (b) mask = (mask | 0x01).toByte

          if (rectanglesOverlap(new Rectangle(0, 0, layer.writableImage.width.value.toInt, layer.writableImage.height.value.toInt), new Rectangle(activeSelection.pointA.value.x.toInt, activeSelection.pointA.value.y.toInt, width, height))) {

            pr.getPixels(activeSelection.pointA.value.x.toInt, activeSelection.pointA.value.y.toInt, width, height, pixelFormat, buffer, 0, width * 4)

            val newPixels = ByteArrayOperationApplier.applyUnary(mask, buffer, operation)
            pw.setPixels(activeSelection.pointA.value.x.toInt, activeSelection.pointA.value.y.toInt, width, height, pixelFormat, newPixels, 0, width * 4)
          }
        }
      } else {
        val width = layer.writableImage.width.value.toInt
        val height = layer.writableImage.height.value.toInt

        val buffer = new Array[Byte](width * height * 4)
        val pixelFormat = PixelFormat.getByteBgraPreInstance

        var mask: Byte = 0;
        if (r) mask = (mask | 0x04).toByte
        if (g) mask = (mask | 0x02).toByte
        if (b) mask = (mask | 0x01).toByte

        pr.getPixels(0, 0, width, height, pixelFormat, buffer, 0, width * 4)

        val newPixels = ByteArrayOperationApplier.applyUnary(mask, buffer, operation)
        pw.setPixels(0, 0, width, height, pixelFormat, newPixels, 0, width * 4)
      }
    }
  }

  def applyBinary(r: Boolean, g: Boolean, b: Boolean, operation: (Byte, Byte) => Byte, activeLayers: ObservableBuffer[Layer], activeSelections: ObservableBuffer[Selection], value: Double): Unit = {
    for (layer <- activeLayers) {
      val pw = layer.writableImage.pixelWriter
      val pr = layer.writableImage.pixelReader.get
      if (activeSelections.length > 0) {
        for (activeSelection <- activeSelections) {
          val width = activeSelection.width().toInt
          val height = activeSelection.height().toInt

          val buffer = new Array[Byte](width * height * 4)
          val pixelFormat = PixelFormat.getByteBgraPreInstance

          var mask: Byte = 0
          if (r) mask = (mask | 0x04).toByte
          if (g) mask = (mask | 0x02).toByte
          if (b) mask = (mask | 0x01).toByte

          if (rectanglesOverlap(new Rectangle(0, 0, layer.writableImage.width.value.toInt, layer.writableImage.height.value.toInt), new Rectangle(activeSelection.pointA.value.x.toInt, activeSelection.pointA.value.y.toInt, width, height))) {
            pr.getPixels(activeSelection.pointA.value.x.toInt, activeSelection.pointA.value.y.toInt, width, height, pixelFormat, buffer, 0, width * 4)

            val newPixels = ByteArrayOperationApplier.applyBinary(mask, buffer, operation, value.toByte)
            pw.setPixels(activeSelection.pointA.value.x.toInt, activeSelection.pointA.value.y.toInt, width, height, pixelFormat, newPixels, 0, width * 4)
          }
        }
      } else {
        val width = layer.writableImage.width.value.toInt
        val height = layer.writableImage.height.value.toInt

        val buffer = new Array[Byte](width * height * 4)
        val pixelFormat = PixelFormat.getByteBgraPreInstance

        var mask: Byte = 0
        if (r) mask = (mask | 0x04).toByte
        if (g) mask = (mask | 0x02).toByte
        if (b) mask = (mask | 0x01).toByte

        pr.getPixels(0, 0, width, height, pixelFormat, buffer, 0, width * 4)

        val newPixels = ByteArrayOperationApplier.applyBinary(mask, buffer, operation, value.toByte)
        pw.setPixels(0, 0, width, height, pixelFormat, newPixels, 0, width * 4)
      }
    }
  }

  def applyMedian(r: Boolean, g: Boolean, b: Boolean, activeLayers: ObservableBuffer[Layer], activeSelections: ObservableBuffer[Selection], n: Double): Unit = {
    for (layer <- activeLayers) {
      val pw = layer.writableImage.pixelWriter
      val pr = layer.writableImage.pixelReader.get
      if (activeSelections.length > 0) {
        for (activeSelection <- activeSelections) {
          val width = activeSelection.width().toInt
          val height = activeSelection.height().toInt

          val buffer = new Array[Byte](width * height * 4)
          val pixelFormat = PixelFormat.getByteBgraPreInstance

          var mask: Byte = 0
          if (r) mask = (mask | 0x04).toByte
          if (g) mask = (mask | 0x02).toByte
          if (b) mask = (mask | 0x01).toByte

          if (rectanglesOverlap(new Rectangle(0, 0, layer.writableImage.width.value.toInt, layer.writableImage.height.value.toInt), new Rectangle(activeSelection.pointA.value.x.toInt, activeSelection.pointA.value.y.toInt, width, height))) {
            pr.getPixels(activeSelection.pointA.value.x.toInt, activeSelection.pointA.value.y.toInt, width, height, pixelFormat, buffer, 0, width * 4)

            val newPixels = ByteArrayOperationApplier.applyMedian(mask, width, height, buffer, n.toInt)
            pw.setPixels(activeSelection.pointA.value.x.toInt, activeSelection.pointA.value.y.toInt, width, height, pixelFormat, newPixels, 0, width * 4)
          }
        }
      } else {
        val width = layer.writableImage.width.value.toInt
        val height = layer.writableImage.height.value.toInt

        val buffer = new Array[Byte](width * height * 4)
        val pixelFormat = PixelFormat.getByteBgraPreInstance

        var mask: Byte = 0
        if (r) mask = (mask | 0x04).toByte
        if (g) mask = (mask | 0x02).toByte
        if (b) mask = (mask | 0x01).toByte

        pr.getPixels(0, 0, width, height, pixelFormat, buffer, 0, width * 4)

        val newPixels = ByteArrayOperationApplier.applyMedian(mask, width, height, buffer, n.toInt)
        pw.setPixels(0, 0, width, height, pixelFormat, newPixels, 0, width * 4)
      }
    }
  }

  def applyPonderedAverage(r: Boolean, g: Boolean, b: Boolean, activeLayers: ObservableBuffer[Layer], activeSelections: ObservableBuffer[Selection], ponders: Array[Int], n: Double): Unit = {
    for (layer <- activeLayers) {
      val pw = layer.writableImage.pixelWriter
      val pr = layer.writableImage.pixelReader.get
      if (activeSelections.length > 0) {
        for (activeSelection <- activeSelections) {
          val width = activeSelection.width().toInt
          val height = activeSelection.height().toInt

          val buffer = new Array[Byte](width * height * 4)
          val pixelFormat = PixelFormat.getByteBgraPreInstance

          var mask: Byte = 0
          if (r) mask = (mask | 0x04).toByte
          if (g) mask = (mask | 0x02).toByte
          if (b) mask = (mask | 0x01).toByte

          if (rectanglesOverlap(new Rectangle(0, 0, layer.writableImage.width.value.toInt, layer.writableImage.height.value.toInt), new Rectangle(activeSelection.pointA.value.x.toInt, activeSelection.pointA.value.y.toInt, width, height))) {
            pr.getPixels(activeSelection.pointA.value.x.toInt, activeSelection.pointA.value.y.toInt, width, height, pixelFormat, buffer, 0, width * 4)

            val newPixels = ByteArrayOperationApplier.applyPonderedAverage(mask, width, height, buffer, ponders, n.toInt)
            pw.setPixels(activeSelection.pointA.value.x.toInt, activeSelection.pointA.value.y.toInt, width, height, pixelFormat, newPixels, 0, width * 4)
          }
        }
      } else {
        val width = layer.writableImage.width.value.toInt
        val height = layer.writableImage.height.value.toInt

        val buffer = new Array[Byte](width * height * 4)
        val pixelFormat = PixelFormat.getByteBgraPreInstance

        var mask: Byte = 0
        if (r) mask = (mask | 0x04).toByte
        if (g) mask = (mask | 0x02).toByte
        if (b) mask = (mask | 0x01).toByte

        pr.getPixels(0, 0, width, height, pixelFormat, buffer, 0, width * 4)

        val newPixels = ByteArrayOperationApplier.applyPonderedAverage(mask, width, height, buffer, ponders, n.toInt)
        pw.setPixels(0, 0, width, height, pixelFormat, newPixels, 0, width * 4)
      }
    }
  }

}
