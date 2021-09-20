package com.fp.image_blender.view

import com.fp.image_blender.Project.Project
import com.fp.image_blender.view.dialog.Dialog
import com.fp.image_blender.view.menu.Menu
import com.fp.image_blender.{Layer, Selection}
import javafx.scene.control.Button
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Point2D, Pos}
import scalafx.scene.control._
import javafx.scene.input.MouseEvent
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.scene.{Cursor, Group, Scene}

object Program extends JFXApp3 {

  var layerPreview: ScrollPane = null
  var mainView: SplitPane = null
  var blendView: Group = null

  var project: Project = new Project("Image Blender")

  override def start(): Unit = {

    project.layers.onChange( (b, a) => {
      populateLeftSideMenu(mainView)
    })
    project.selections.onChange( (b, a) => {
      populateLeftSideMenu(mainView)
    })
    stage = new PrimaryStage {
      title.bind(project.name)
      scene = new Scene(800, 800) {
        root =>
        mainView = new SplitPane(){
          setDividerPosition(0, .25)
          prefHeight.bind(root.height)
          prefWidth.bind(root.width)
        }
        // Menu Bar
        val menuBar: MenuBar =  Menu.createMenuBar()
        menuBar.prefWidthProperty.bind(root.widthProperty)

        // Layer and selection preview
        populateLeftSideMenu(mainView)

        // Image blend
        populateImageBlend(mainView)

        // Operation preview
        populateRightSideMenu(mainView)

        addMouseActionListeners(root)

        mainView.prefHeight.bind(root.height-50)
        mainView.prefWidth.bind(root.width)
        val rootView = new BorderPane()
        rootView.top = menuBar
        rootView.center = mainView
        content = List(rootView)
      }
    }
  }

  def addMouseActionListeners(scene: Scene): Unit ={
    blendView.onMousePressed = (event: MouseEvent) => {
       Selection.newSelectionPointA.value = new Point2D(event.getX, event.getY)
    }
    blendView.onMouseDragged = (event: MouseEvent) => {
      Selection.newSelectionPointB.value = new Point2D(event.getX, event.getY)
    }

    blendView.onMouseReleased = (event: MouseEvent) => {
      val diagonal = Math.sqrt(Math.exp(Math.abs(Selection.newSelectionPointA.value.x - Selection.newSelectionPointB.value.x)) + Math.exp(Math.abs(Selection.newSelectionPointA.value.y - Selection.newSelectionPointB.value.y)))
      println(diagonal)
      if (diagonal > 500){
        Dialog.addNewSelectionDialog()
      } else {
        Selection.newSelectionPointA.value = new Point2D(0, 0)
        Selection.newSelectionPointB.value = new Point2D(0, 0)
      }
    }

    blendView.onMouseEntered = (event: MouseEvent) => {
      scene.cursor = Cursor.Hand
    }

    blendView.onMouseExited = (event: MouseEvent) => {
      scene.cursor = Cursor.Default
    }
  }
  def populateImageBlend(parentNode: SplitPane): Unit = {
    val scrollPane = new ScrollPane()
    if (blendView == null) blendView = new Group(); else ImageBlend.removeAllItemsFromBlendView(blendView)

    for(layer <- project.layers) {
      if (layer.isActive.value) blendView.children += ImageBlend.createImageView(layer)
    }
    for(selection <- project.selections) {
      if (selection.isActive.value) blendView.children += ImageBlend.createSelection(selection)
    }

    scrollPane.content = blendView
    if (parentNode.items.length > 1) {
      parentNode.items.remove(1, 1)
    }
    parentNode.items.insert(1, scrollPane)
  }

  def populateLeftSideMenu(parentNode: SplitPane): Unit = {
    val newLayerButton = new Button("Add New Layer") {
      setOnAction((e: javafx.event.ActionEvent)=> Dialog.addNewLayerDialog())
      prefWidthProperty().bind(parentNode.prefWidth)
    }
    val newLayerButtonBox = new VBox{
      children += newLayerButton
      maxWidth = Double.MaxValue
      alignment = Pos.BaselineCenter
    }
    val border: BorderPane = new BorderPane(){
      maxWidth = 250
      minWidth = 250
      prefHeight.bind(parentNode.prefHeight- 10)
      prefWidth.bind(parentNode.prefWidth)
      margin = Insets(0, 0, 20, 0)
      val layerPreviewList: ScrollPane = Layer.createLayerPreviewList(this)
      layerPreviewList.maxHeight.bind(parentNode.prefHeight - newLayerButton.heightProperty())

      val selectionPreviewList: ScrollPane = Selection.createSelectionPreviewList(this)
      selectionPreviewList.maxHeight.bind(parentNode.prefHeight - newLayerButton.heightProperty() - layerPreviewList.heightProperty())

      top = layerPreviewList
      center = selectionPreviewList
      bottom = newLayerButtonBox
    }
    if (parentNode.items.length > 0) {
      parentNode.items.remove(0, 1)
    }
    parentNode.items.prepend(border)
  }

  def populateRightSideMenu(parentNode: SplitPane): Unit = {
    val border: BorderPane = new BorderPane(){
      maxWidth = 300
      minWidth = 300
      prefHeight.bind(parentNode.prefHeight- 10)
      prefWidth.bind(parentNode.prefWidth)

      top = Operation.createOperationView(this)
    }
    if (parentNode.items.length > 2) {
      parentNode.items.remove(2, 1)
    }
    parentNode.items.insert(2, border)
  }


}
