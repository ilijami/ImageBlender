package com.fp.image_blender

import com.fp.image_blender.utils.Serialization.{DeserializeProjectFromDPB, SerializeProjectToDPB}
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.embed.swing.SwingFXUtils

import java.awt.image.BufferedImage
import java.io.{BufferedOutputStream, BufferedWriter, File, FileInputStream, FileOutputStream, FileWriter}
import javax.imageio.ImageIO
object Project {
  class Project {
    var name: StringProperty = new StringProperty()
    var layers: ObservableBuffer[Layer] = ObservableBuffer()
    var selections: ObservableBuffer[Selection] = ObservableBuffer()
    var initialized = false

    def this(name: String) = {
      this()
      this.name.set(name)
      initialized = true
      println("New project created: " + name)
    }

    def initProject(name: String): Unit = {
      this.initialized = true
      this.layers.remove(0, this.layers.length)
      this.selections.remove(0, this.selections.length)
      this.name.set(name)
    }

    def isInitialized: Boolean = {
      initialized
    }

    def loadFromFile(file: File) {
      println("Project loaded from file" + file.getName)
      val is = new FileInputStream(file.getPath)
      val cnt = is.available
      val bytes = Array.ofDim[Byte](cnt)
      is.read(bytes)
      is.close()
      val payload = new String(java.util.Base64.getDecoder.decode(bytes))
      val tmp = DeserializeProjectFromDPB(payload)
      this.name.set(tmp.name.value)
      this.layers.addAll(tmp.layers.toArray[Layer])
      this.selections.addAll(tmp.selections.toArray[Selection])
      initialized = true
    }

    def saveToFile(file: File) {
      for(layer <- layers) {
        val combinedImage = new BufferedImage(layer.writableImage.width.value.toInt, layer.writableImage.height.value.toInt, BufferedImage.TYPE_INT_ARGB)
        val graphics = combinedImage.getGraphics
        graphics.drawImage(SwingFXUtils.fromFXImage(layer.writableImage, null), 0, 0, null)
        val newPath = "resources" + File.separator + layer.name + ".png";
        ImageIO.write(combinedImage, "png", new File(newPath))
        layer.imagePath = newPath
      }
      val serializedProject = SerializeProjectToDPB(this)
      val bos = new BufferedOutputStream(new FileOutputStream(file.getPath))
      bos.write(serializedProject)
      bos.close()
      println("Project saved to file " + file.getName)
    }


    def addNewLayer(name: String, filePath: String): Unit = {
      val newLayer = new Layer(name, filePath, false, true)
      layers += newLayer
    }

    def addNewSelection(selection: Selection): Unit = {
      selections += selection
    }

    def removeSelection(selection: Selection): Unit = {
      selections.remove(selection)
      println("Selection removed:", selection.name)
    }

    def addNewLayer(layer: Layer): Unit = {
      layers += layer
    }

    def removeLayer(layer: Layer): Unit = {
      layers.remove(layer)
      println("Layer removed:", layer.name)
    }

  }
}
