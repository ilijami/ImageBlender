package com.fp.image_blender.view

import com.fp.image_blender.operation.{ActiveLayersOperationApplier, BinaryOperation, FilterOperation, UnaryOperation}
import com.fp.image_blender.view.Program.project
import javafx.event.ActionEvent
import javafx.scene.control.Button
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control._
import scalafx.scene.layout.GridPane.{getColumnIndex, getRowIndex}
import scalafx.scene.layout.{BorderPane, FlowPane, GridPane, VBox}
import scalafx.scene.paint.Color

object Operation {

  private def createUnaryOperationView(name: String, operation: Byte => Byte): TitledPane = {
    val rCheckBox = new CheckBox(){
      text = "Red"
      selected = false
    }

    val gCheckBox = new CheckBox(){
      text = "Green"
      selected = false
    }

    val bCheckBox = new CheckBox(){
      text = "Blue"
      selected = false
    }

    val singleOperationContent = new GridPane(){
      vgap = 10
      hgap = 10
    }

    singleOperationContent.add(rCheckBox, 0, 0, 1, 1)
    singleOperationContent.add(gCheckBox, 1, 0, 1, 1)
    singleOperationContent.add(bCheckBox, 2, 0, 1, 1)

    val applyOperations = new Button("Apply"){
      setPrefWidth(Int.MaxValue)
      setOnAction((e: ActionEvent) => {
        ActiveLayersOperationApplier.applyUnary(
          rCheckBox.selected.value,
          gCheckBox.selected.value,
          bCheckBox.selected.value,
          operation,
          project.layers.filter(l => l.isActive.value),
          project.selections.filter(s => s.isActive.value && s.color.value == Color.Transparent))
      })
    }
    singleOperationContent.add(applyOperations, 0, 1, 3, 1)

    new TitledPane(){
      text = name
      content = singleOperationContent
      expanded = false
    }
  }

  private def createBinaryOperationView(name: String, label: String, min: Short, max: Short, operation: (Byte, Byte) => Byte): TitledPane = {
    val rCheckBox = new CheckBox(){
      text = "Red"
      selected = false
    }

    val gCheckBox = new CheckBox(){
      text = "Green"
      selected = false
    }

    val bCheckBox = new CheckBox(){
      text = "Blue"
      selected = false
    }

    val operationSlider = new Slider(min, max, min)  {
    }
    val operationSliderValue = new Label(min + "")
    operationSlider.value.onChange((_, _, newValue) => {
      operationSliderValue.text = (math round  newValue.doubleValue()) + ""
    })

    val singleOperationContent = new GridPane(){
      vgap = 10
      hgap = 10
    }

    singleOperationContent.add(rCheckBox, 1, 0, 4, 1)
    singleOperationContent.add(gCheckBox, 5, 0, 4, 1)
    singleOperationContent.add(bCheckBox, 9, 0, 4, 1)

    singleOperationContent.add(new Label(label), 1, 1, 3, 1)
    singleOperationContent.add(operationSlider,4, 1, 6, 1)
    singleOperationContent.add(operationSliderValue,10, 1, 4, 1)

    val applyOperations = new Button("Apply"){
      setPrefWidth(Int.MaxValue)
      setOnAction((e: ActionEvent) => {
        ActiveLayersOperationApplier.applyBinary(
          rCheckBox.selected.value,
          gCheckBox.selected.value,
          bCheckBox.selected.value,
          operation, project.layers.filter(l => l.isActive.value),
          project.selections.filter(s => s.isActive.value && s.color.value == Color.Transparent),
          math round operationSlider.value.value.doubleValue())
      })
    }
    singleOperationContent.add(applyOperations, 1, 2, 13, 1)

    new TitledPane(){
      text = name
      content = singleOperationContent
      expanded = false
    }
  }

  private def createOperationListView(parentNode: VBox): Node = {
    val binaryOperationList = new VBox(){
      children += createBinaryOperationView("Addition", "Add:", 0, 255, BinaryOperation.addition)
      children += createBinaryOperationView("Subtraction", "Sub", 0, 255, BinaryOperation.subtraction)
      children += createBinaryOperationView("Inverse Subtraction", "InvSub:", 0, 255, BinaryOperation.inverseSubtraction)
      children += createBinaryOperationView("Multiplication", "Mul:", 1, 10, BinaryOperation.multiplication)
      children += createBinaryOperationView("Division", "Div:", 1, 10, BinaryOperation.division)
      children += createBinaryOperationView("Inverse Division", "InvDiv:", 0, 255, BinaryOperation.inverseDivision)
      children += createBinaryOperationView("Power", "Power:", 1, 10, BinaryOperation.power)
      children += createBinaryOperationView("Minimum", "Min:", 0, 255, BinaryOperation.min)
      children += createBinaryOperationView("Maximum", "Max:", 0, 255, BinaryOperation.max)
      children += createUnaryOperationView("Inversion", UnaryOperation.inversion)
      children += createGrayscaleOperationView()
      children += createMedianOperationView()
      children += createPonderedAverageOperationView()
    }

    val operationsView = new TitledPane() {
      text = "Operations"
      content = binaryOperationList
    }
    operationsView
  }

  private def createGrayscaleOperationView(): TitledPane = {

    val singleOperationContent = new GridPane(){
      vgap = 10
      hgap = 10
    }

    val applyOperations = new Button("Apply"){
      setPrefWidth(Int.MaxValue)
      setOnAction((e: ActionEvent) => {
        ActiveLayersOperationApplier.applyGrayscale(project.layers.filter(l => l.isActive.value), project.selections.filter(s => s.isActive.value && s.color.value == Color.Transparent))
      })
    }
    singleOperationContent.add(applyOperations, 0, 0, 3, 1)

    new TitledPane(){
      text = "Grayscale"
      content = singleOperationContent
      expanded = false
    }
  }

  private def createMedianOperationView(): TitledPane = {

    val rCheckBox = new CheckBox(){
      text = "Red"
      selected = false
    }

    val gCheckBox = new CheckBox(){
      text = "Green"
      selected = false
    }

    val bCheckBox = new CheckBox(){
      text = "Blue"
      selected = false
    }

    val operationSlider = new Slider(1, 10, 1)  {
    }
    val operationSliderValue = new Label(1 + "")
    operationSlider.value.onChange((_, _, newValue) => {
      operationSliderValue.text = "%d".format(newValue.intValue())
    })

    val filterOperationContent = new GridPane(){
      vgap = 10
      hgap = 10
    }

    filterOperationContent.add(rCheckBox, 1, 0, 4, 1)
    filterOperationContent.add(gCheckBox, 5, 0, 4, 1)
    filterOperationContent.add(bCheckBox, 9, 0, 4, 1)

    filterOperationContent.add(new Label("N:"), 1, 1, 3, 1)
    filterOperationContent.add(operationSlider,4, 1, 6, 1)
    filterOperationContent.add(operationSliderValue,10, 1, 4, 1)

    val applyOperations = new Button("Apply"){
      setPrefWidth(Int.MaxValue)
      setOnAction((e: ActionEvent) => {
        ActiveLayersOperationApplier.applyMedian(
          rCheckBox.selected.value,
          gCheckBox.selected.value,
          bCheckBox.selected.value,
          project.layers.filter(l => l.isActive.value),
          project.selections.filter(s => s.isActive.value && s.color.value == Color.Transparent),
          math round operationSlider.value.value.doubleValue())
      })
    }
    filterOperationContent.add(applyOperations, 1, 2, 13, 1)

    new TitledPane(){
      text = "Median"
      content = filterOperationContent
      expanded = false
    }
  }

  private def createPonderedAverageOperationView(): TitledPane = {

    val rCheckBox = new CheckBox() {
      text = "Red"
      selected = false
    }

    val gCheckBox = new CheckBox() {
      text = "Green"
      selected = false
    }

    val bCheckBox = new CheckBox() {
      text = "Blue"
      selected = false
    }

    val operationSlider = new Slider(1, 3, 1) {
    }

    val ponders = new GridPane() {
      vgap = 10
      hgap = 10
    }
    for (col <- 0 until 3) {
      for (row <- 0 until 3) {
        val textField = new TextField()
        textField.text = "1"
        ponders.add(textField, col, row)
      }
    }

    val operationSliderValue = new Label(1 + "")
    operationSlider.value.onChange((_, _, newValue) => {
      operationSliderValue.text = "%d".format(newValue.intValue())
      ponders.children.remove(0, ponders.children.length)
      for (row  <- 0 until newValue.intValue() * 2 + 1) {
        for (col <- 0 until newValue.intValue() * 2 + 1) {
          val textField = new TextField()
          textField.text = "1"
          ponders.add(textField, col, row)
        }
      }
    })
    val filterOperationContent = new GridPane() {
      vgap = 10
      hgap = 10
      margin = Insets(10)
    }

    filterOperationContent.add(rCheckBox, 1, 0, 4, 1)
    filterOperationContent.add(gCheckBox, 5, 0, 4, 1)
    filterOperationContent.add(bCheckBox, 9, 0, 4, 1)

    filterOperationContent.add(new Label("N:"), 1, 1, 3, 1)
    filterOperationContent.add(operationSlider, 4, 1, 6, 1)
    filterOperationContent.add(operationSliderValue, 10, 1, 4, 1)

    val applyOperations = new Button("Apply") {
      setPrefWidth(Int.MaxValue)
      setOnAction((e: ActionEvent) => {
        val ponderLength = Math.pow(operationSlider.value.value.intValue() * 2 + 1, 2).toInt
        val ponderValues =  new Array[Int](ponderLength)
        for (i <- 0 until ponderLength) {
          ponders.getChildren.forEach(p => {
            if (javafx.scene.layout.GridPane.getRowIndex(p) == (i / (operationSlider.value.value.intValue() * 2 + 1)) &&
              javafx.scene.layout.GridPane.getColumnIndex(p) == (i % (operationSlider.value.value.intValue() * 2 + 1))){
              ponderValues(i) = p.asInstanceOf[javafx.scene.control.TextField].textProperty().getValue.toInt
            }
          })
          //ponderValues(i) = ponders.getChildren().get(i).asInstanceOf[javafx.scene.control.TextField].textProperty().getValue.toInt
          //getRowIndex(ponders)
        }
        println(ponderValues.mkString(" "))
        ActiveLayersOperationApplier.applyPonderedAverage(
          rCheckBox.selected.value,
          gCheckBox.selected.value,
          bCheckBox.selected.value,
          project.layers.filter(l => l.isActive.value),
          project.selections.filter(s => s.isActive.value && s.color.value == Color.Transparent),
          ponderValues,
          math round operationSlider.value.value.doubleValue())
      })
    }
    filterOperationContent.add(applyOperations, 1, 2, 13, 1)

    val borderBox = new BorderPane(){

      padding = Insets(10, 10, 10, 10)
    }
    borderBox.bottom  = filterOperationContent
    borderBox.top = ponders

    new TitledPane() {
      text = "Pondered Average"
      content = borderBox
      expanded = false
    }
  }


  def createOperationView(parentNode: BorderPane): ScrollPane = {
    val operationPreviewList = new VBox(){
      prefWidth = 300
      children += createOperationListView(this)
    }
    val scrollPane = new ScrollPane() {
      hbarPolicy = ScrollBarPolicy.Never
      content = operationPreviewList
      prefWidth.bind(parentNode.prefWidth)
    }
    scrollPane
  }
}
