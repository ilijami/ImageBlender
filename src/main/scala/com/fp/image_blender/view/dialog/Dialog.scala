package com.fp.image_blender.view.dialog

import com.fp.image_blender.Selection
import com.fp.image_blender.view.{ImageBlend, Selection}
import com.fp.image_blender.view.Program.{blendView, project, stage}
import javafx.scene.control.ButtonType
import scalafx.scene.control.Button
import scalafx.beans.property.StringProperty
import scalafx.geometry.{Point2D, Pos}
import scalafx.scene.control.{Label, TextField, TextInputDialog}
import scalafx.scene.layout.GridPane
import scalafx.Includes.{jfxDialogPane2sfx, observableList2ObservableBuffer}
import scalafx.scene.image.ImageView
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

object Dialog {
  val imageChooser: FileChooser = new FileChooser {
    extensionFilters ++= Seq(
      new ExtensionFilter("JPG/PNG", Seq("*.jpg", "*.png")),
    )
  }

  def addNewLayerDialog(): Unit = {
    val layerName = new StringProperty()
    val filePathLabel = new Label()
    val filePath = new StringProperty()

    filePath.onChange( (b, a, c) => {
      filePathLabel.text = filePath.value.split('\\').last
    })

    val dialog = new TextInputDialog(defaultValue = "confirmed") {
      initOwner(stage)
      title = "Add New Layer"
      headerText = "Please enter layer details!"

      dialogPane().content = new GridPane(){
        grid =>
        val button = new Button("Choose File")
        button.setOnAction( _ => {
          val result = imageChooser.showOpenDialog(stage)
          if (result != null) {
            filePath.set(result.getPath)
          }
        })
        add(new Label("Layer Name:"), 0, 0, 1, 1)
        val t = new TextField()
        layerName.bind(t.text)
        add(t, 1, 0, 1, 1)
        add(filePathLabel, 0, 1, 1, 1)
        add(button, 1, 1, 1, 1)
        hgap = 10
        vgap = 10
        grid.alignment = Pos.BaselineRight

      }
      dialogPane().lookupButton(ButtonType.OK).disableProperty().bind(layerName.length().lessThan(1).or(filePath.length().lessThan(1)))
    }
    val result = dialog.showAndWait()
    if (result.isDefined) {
      project.addNewLayer(layerName.value, filePath.value)
    }
  }

  def addNewSelectionDialog(): Unit = {
    val dialog = new TextInputDialog(defaultValue = "Selection " + (project.selections.length + 1)) {
      initOwner(stage)
      title = "Add New Selection"
      headerText = "Provide selection name!!"
      contentText = "Please enter selection name:"
      dialogPane().lookupButton(ButtonType.OK).disableProperty().bind(editor.text.length().lessThan(1))
    }
    val result = dialog.showAndWait()

    if (result.isDefined) {
      val pointA = new Point2D(Selection.newSelectionPointA.value.x, Selection.newSelectionPointA.value.y)
      val pointB = new Point2D(Selection.newSelectionPointB.value.x, Selection.newSelectionPointB.value.y)

      val selection = new Selection(result.get, true, pointA, pointB)
      project.selections += selection
      blendView.children += ImageBlend.createSelection(selection)
    }
    Selection.newSelectionPointA.value = new Point2D(0, 0)
    Selection.newSelectionPointB.value = new Point2D(0, 0)
  }
}
