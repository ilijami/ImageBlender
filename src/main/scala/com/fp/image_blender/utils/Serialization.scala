package com.fp.image_blender.utils

import com.fp.image_blender.{Layer, Selection}
import com.fp.image_blender.Project.Project
import scalafx.geometry.Point2D
import scalafx.scene.paint.Color

// TODO: Use json or xml formats
object Serialization {
  def SerializeProjectToDPB(project: Project): Array[Byte] = {
    var serialized = project.name.value + "|" + project.layers.length + "|" + project.selections.length
    for(layer <- project.layers) {
      serialized += "\n" + layer.toString
    }

    for(selection <- project.selections) {
      serialized += "\n" + selection.toString
    }
    java.util.Base64.getEncoder.encode(serialized.getBytes())
  }

  def DeserializeProjectFromDPB(payload: String): Project = {
    val lines = payload.split('\n')
    val header = lines(0).split('|')
    val layerCount = header(1).toInt
    val selectionCount = header(2).toInt

    val project = new Project(header(0))
    for(index <- 1 until lines.length) {
      if (index < layerCount + 1) {
        val serializedLayer = lines.apply(index).split('|')
        val layer = new Layer(serializedLayer(0), serializedLayer(1), serializedLayer(2).toBoolean, false, serializedLayer(3).toDouble)
        project.addNewLayer(layer)
      } else {
        val selectionStr = lines.apply(index).split('|')
        val selection = new Selection(
          selectionStr(0),
          selectionStr(1).toBoolean,
          new Point2D(selectionStr(2).toDouble, selectionStr(3).toDouble),
          new Point2D(selectionStr(4).toDouble, selectionStr(5).toDouble),
          Color(selectionStr(6).toDouble, selectionStr(7).toDouble, selectionStr(8).toDouble, selectionStr(9).toDouble)
        )
        project.addNewSelection(selection)
      }
    }
    project
  }
}
