package com.fp.image_blender.view.menu

import com.fp.image_blender.view.ImageBlend
import com.fp.image_blender.view.Program.{blendView, mainView, populateImageBlend, project, stage}
import com.fp.image_blender.view.dialog.Dialog
import scalafx.Includes.observableList2ObservableBuffer
import scalafx.embed.swing.SwingFXUtils
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{Menu, MenuBar, MenuItem, SeparatorMenuItem, TextInputDialog}
import scalafx.scene.paint.Color
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

object Menu {
  val projectFileChooser: FileChooser = new FileChooser {
    extensionFilters ++= Seq(
      new ExtensionFilter("Image Blender Project Files", Seq("*.dpb")),
    )
  }

  def createMenuBar(): MenuBar = {
    val menuBar = new MenuBar()
    val fileMenu = new Menu("Project")
    val newProject = new MenuItem("New Project")
    newProject.onAction = _ => {
      val projectNameDialog = new TextInputDialog()
      projectNameDialog.setTitle("New Project")
      projectNameDialog.headerText = ""
      projectNameDialog.contentText = "Enter Project Name"
      val result = projectNameDialog.showAndWait()
      if (result.isDefined) {
        project.initProject(result.get)
        ImageBlend.removeAllItemsFromBlendView(blendView)
      }
    }
    val openProject = new MenuItem("Open Project")
    openProject.onAction = _ => {
      val result = projectFileChooser.showOpenDialog(stage)
      if (result != null) {
        println("Loaded project from file " + result)
        project.loadFromFile(result)
        populateImageBlend(mainView)
      }
    }
    val save = new MenuItem("Save")
    save.onAction = _ => {
      val result = projectFileChooser.showSaveDialog(stage)
      if (result != null) {
        project.saveToFile(result)
        println("Saved project to file " + result)
      }
    }
    val export = new MenuItem("Export")
    export.onAction = _ => {
      val result = Dialog.imageChooser.showSaveDialog(stage)
      if (result != null) {
        var format = if (result.getName.endsWith("jpg")) "jpg" else "png"

        val activeLayers = project.layers.filter(l => l.isActive.value)
        var maxX = 0.0
        var maxY = 0.0
        for(activeLayer <- activeLayers) {
          if(activeLayer.writableImage.width.value > maxX){
            maxX = activeLayer.writableImage.width.value
          }
          if(activeLayer.writableImage.height.value > maxY){
            maxY = activeLayer.writableImage.height.value
          }
        }
        val imageType = if (result.getName.endsWith("jpg")) BufferedImage.TYPE_INT_RGB else BufferedImage.TYPE_INT_ARGB
        val combinedImage = new BufferedImage(maxX.toInt, maxY.toInt, imageType)
        val graphics = combinedImage.getGraphics

        val canvas = new Canvas(maxX.toInt, maxY.toInt)
        val gc = canvas.getGraphicsContext2D


        for(activeLayer <- activeLayers) {
          gc.setGlobalAlpha(activeLayer.opacity.value)
          gc.drawImage(activeLayer.writableImage, 0, 0)
          //graphics.drawImage(SwingFXUtils.fromFXImage(activeLayer.writableImage, null), 0, 0, null)
        }

        val activeSelections = project.selections.filter(l => l.isActive.value && l.color.value != Color.Transparent)
        for(selection <- activeSelections) {
          gc.setFill(selection.color.value)
          gc.fillRect(selection.pointA.value.x,
            selection.pointA.value.y,
            selection.width().toInt,
            selection.height().toInt
          )
        }
        //ImageIO.write(combinedImage, "png", result)

        graphics.drawImage(SwingFXUtils.fromFXImage(canvas.snapshot(null, null), null), 0, 0, null)

        println(result)
        ImageIO.write(combinedImage, format, result)

        println("Exported image to file " + result)
      }
    }
    fileMenu.items = List(newProject, openProject, new SeparatorMenuItem(),export, save)

    val operationsMenu = new Menu("Operations")
    menuBar.menus = List(fileMenu, operationsMenu)
    menuBar
  }
}
