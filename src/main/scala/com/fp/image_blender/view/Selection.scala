package com.fp.image_blender.view

import com.fp.image_blender.Selection
import com.fp.image_blender.view.ImageBlend
import com.fp.image_blender.view.Program.{blendView, project}
import javafx.scene.control.Button
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Point2D
import scalafx.scene.Node
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control.{CheckBox, ColorPicker, ScrollPane, TitledPane}
import scalafx.scene.layout.{BorderPane, GridPane, VBox}
import scalafx.scene.paint.Color

object Selection {
  val newSelectionPredefinedName = "new_selection"
  var newSelectionPointA: ObjectProperty[Point2D] = new ObjectProperty[Point2D](){
    value = new Point2D(0, 0)
    onChange((_,_,_) => {
      ImageBlend.removeItemFromBlendView(newSelectionPredefinedName, blendView)
      blendView.children += ImageBlend.createSelection(new Selection(newSelectionPredefinedName, true, newSelectionPointA.value, newSelectionPointB.value))
    })
  }
  var newSelectionPointB: ObjectProperty[Point2D] = new ObjectProperty[Point2D](){
    value = new Point2D(0, 0)
    onChange((_,_,_) => {
      ImageBlend.removeItemFromBlendView(newSelectionPredefinedName, blendView)
      blendView.children += ImageBlend.createSelection(new Selection(newSelectionPredefinedName, true, newSelectionPointA.value, newSelectionPointB.value))
    })
  }

  def createSelectionPreview(selection: Selection): Node = {
    val removeButton = new Button("Remove") {
      setOnAction((e: javafx.event.ActionEvent)=> {
        if (selection.isActive.value) ImageBlend.removeItemFromBlendView(selection.name, blendView)
        project.removeSelection(selection)
      })
      setMaxWidth(Double.MaxValue)
    }

    val selectionPreviewContent = new GridPane(){
      vgap = 10
      hgap = 10
    }
    val selectSelectionCheckBox = new CheckBox(){
      selected = selection.isActive.value
      selected.bindBidirectional(selection.isActive)
    }

    val colorPicker = new ColorPicker(selection.color.value)
    colorPicker.value.onChange((_, _, newValue) => {
      selection.color.value = Color(newValue.getRed, newValue.getGreen, newValue.getBlue, newValue.getOpacity)
      ImageBlend.removeItemFromBlendView(selection.name, blendView)
      blendView.children += ImageBlend.createSelection(selection)
      //ImageBlend.fillActiveLayersWithColor(selection, project.layers.filter(l => l.isActive.value))
    })
    selectionPreviewContent.add(selectSelectionCheckBox, 0, 0, 1, 1)
    selectionPreviewContent.add(colorPicker,1, 0, 1, 1)
    selectionPreviewContent.add(removeButton,0, 1, 2, 1)

    val selectionPreview = new TitledPane(){
      text = if (selection.isActive.value) selection.name + " (selected)" else selection.name
      maxHeight(200)
      content = selectionPreviewContent
      expanded = selection.isExpanded.value
      selection.isExpanded.bindBidirectional(expanded)
      selection.isActive.onChange((aa,bb,newValue) => {
        println("IsActive value changed " + selection.name)
        this.text = if (newValue) selection.name + " (selected)" else selection.name
        if (!newValue) {
          ImageBlend.removeItemFromBlendView(selection.name, blendView)
        } else {
          blendView.children += ImageBlend.createSelection(selection)
        }
      })
      selection.pointA.onChange((_,_,_) => {
        ImageBlend.removeItemFromBlendView(selection.name, blendView)
        blendView.children += ImageBlend.createSelection(selection)
      })
      selection.pointB.onChange((_,_,_) => {
        ImageBlend.removeItemFromBlendView(selection.name, blendView)
        blendView.children += ImageBlend.createSelection(selection)
      })
    }
    selectionPreview
  }

  def createSelectionPreviewList(parentNode: BorderPane): ScrollPane = {
    val layerPreviewList = new VBox()
    for(selection <- project.selections) {
      val child = createSelectionPreview(selection)
      child.maxWidth(parentNode.prefWidth.value - 10)
      layerPreviewList.children += child
    }

    val layerPreviewTitled = new TitledPane(){
      text = "Selections"
      content = layerPreviewList
      prefWidth = 250
    }
    val scrollPane = new ScrollPane() {
      hbarPolicy = ScrollBarPolicy.Never
      content = layerPreviewTitled
      prefWidth.bind(parentNode.prefWidth)
    }
    scrollPane
  }
}
