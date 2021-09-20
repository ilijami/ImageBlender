package com.fp.image_blender.view

import com.fp.image_blender.Layer
import com.fp.image_blender.view.Program.{blendView, project}
import javafx.scene.control.Button
import scalafx.scene.Node
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, GridPane, VBox}

object Layer {

  def createLayerPreview(layer: Layer): Node = {
    val image = new Image("file:" + layer.imagePath, 150, 150, true, true)
    val removeButton = new Button("Remove") {
      setOnAction((e: javafx.event.ActionEvent)=> {
        ImageBlend.removeItemFromBlendView(layer.name, blendView)
        project.removeLayer(layer)
      })
      setMaxWidth(Double.MaxValue)
    }
    val opacitySlider = new Slider(0, 1, layer.opacity.value) {
      layer.opacity.bindBidirectional(value)
    }
    val layerPreviewContent = new GridPane(){
      vgap = 10
      hgap = 10
    }
    val selectLayerCheckBox = new CheckBox(){
      selected = layer.isActive.value
      selected.bindBidirectional(layer.isActive)
    }
    layerPreviewContent.add(selectLayerCheckBox, 0, 0, 1, 2)
    layerPreviewContent.add(new ImageView(image), 1, 0, 1, 2)
    layerPreviewContent.add(new Label("Opa:"),0, 2, 2, 1)
    layerPreviewContent.add(opacitySlider,1, 2, 1, 1)
    layerPreviewContent.add(removeButton,0, 3, 3, 1)

    val layerPreview = new TitledPane(){
      text = if (layer.isActive.value) layer.name + " (selected)" else layer.name
      maxHeight(200)
      content = layerPreviewContent
      expanded = layer.isExpanded.value
      layer.isExpanded.bindBidirectional(expanded)
      layer.isActive.onChange((_,_,newValue) => {
        this.text = if (newValue) layer.name + " (selected)" else layer.name
        if (!newValue) {
          ImageBlend.removeItemFromBlendView(layer.name, blendView)
        } else {
          val firstNonImage = blendView.children.find(c => c.getClass.getSimpleName != "ImageView")
          firstNonImage match {
            case Some(fni) =>
              blendView.children.insert(blendView.children.indexOf(fni), ImageBlend.createImageView(layer))
            case _ => blendView.children += ImageBlend.createImageView(layer)
          }
        }
      })
    }
    layerPreview
  }

  def createLayerPreviewList(parentNode: BorderPane): ScrollPane = {
    val layerPreviewList = new VBox()
    for(layer <- project.layers) {
      val child = createLayerPreview(layer)
      child.maxWidth(parentNode.prefWidth.value - 10)
      layerPreviewList.children += child
    }

    val layerPreviewTitled = new TitledPane(){
      text = "Layers"
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
