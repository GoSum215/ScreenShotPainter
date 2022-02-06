package com.example.test

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.geometry.Rectangle2D
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.CheckBox
import javafx.scene.control.ColorPicker
import javafx.scene.control.ScrollPane
import javafx.scene.control.Slider
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.image.WritablePixelFormat
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.robot.Robot
import javafx.scene.shape.ArcType
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.stage.Screen
import javafx.stage.Stage
import java.util.*
import kotlin.concurrent.schedule

class HelloController {
    var stage: Stage? = null
    private var delay: Int = 0
    private var minimizeOnScreenshot: Boolean = false
    private var robot: Robot = Robot()

    @FXML
    private lateinit var controlsAnchorPane: AnchorPane

    @FXML
    private lateinit var controlsHBox: HBox

    @FXML
    private lateinit var main: GridPane

    @FXML
    private lateinit var minimizeCheckbox: CheckBox

    @FXML
    private lateinit var delaySlider: Slider

    @FXML
    private lateinit var imageContainer: StackPane

    @FXML
    private lateinit var scroll: ScrollPane

    @FXML
    private lateinit var imageView: ImageView

    @FXML
    private lateinit var canvas: Canvas

    @FXML
    private lateinit var colorPicker: ColorPicker

    @FXML
    private lateinit var widthSlider: Slider

    @FXML
    private lateinit var clearCheckbox: CheckBox

    private var clickDrawHandler = EventHandler { event: MouseEvent ->
        canvas.graphicsContext2D.fillArc(
            event.x - widthSlider.value / 2,
            event.y - widthSlider.value / 2,
            widthSlider.value,
            widthSlider.value,
            0.0,
            360.0,
            ArcType.OPEN
        )
    }

    private var pressDrawHandler = EventHandler { event: MouseEvent ->
        canvas.graphicsContext2D.fillArc(
            event.x - widthSlider.value / 2,
            event.y - widthSlider.value / 2,
            widthSlider.value,
            widthSlider.value,
            0.0,
            360.0,
            ArcType.OPEN
        )
        canvas.graphicsContext2D.moveTo(event.x, event.y)
        canvas.graphicsContext2D.beginPath()
    }

    private var dragDrawHandler = EventHandler { event: MouseEvent ->
        canvas.graphicsContext2D.stroke = colorPicker.value
        canvas.graphicsContext2D.lineWidth = widthSlider.value
        canvas.graphicsContext2D.lineTo(event.x, event.y)
        canvas.graphicsContext2D.stroke()
    }

    private var releaseDrawHandler = EventHandler { event: MouseEvent ->
        canvas.graphicsContext2D.stroke = colorPicker.value
        canvas.graphicsContext2D.lineWidth = widthSlider.value
        canvas.graphicsContext2D.lineTo(event.x, event.y)
        canvas.graphicsContext2D.stroke()
        canvas.graphicsContext2D.closePath()
    }

    private var clickEraseHandler = EventHandler { event: MouseEvent ->
        canvas.graphicsContext2D.clearRect(
            event.x - widthSlider.value / 2,
            event.y - widthSlider.value / 2,
            widthSlider.value,
            widthSlider.value
        )
    }

    private var pressEraseHandler = EventHandler { event: MouseEvent ->
        canvas.graphicsContext2D.clearRect(
            event.x - widthSlider.value / 2,
            event.y - widthSlider.value / 2,
            widthSlider.value,
            widthSlider.value
        )
    }

    private var dragEraseHandler = EventHandler { event: MouseEvent ->
        canvas.graphicsContext2D.clearRect(
            event.x - widthSlider.value / 2,
            event.y - widthSlider.value / 2,
            widthSlider.value,
            widthSlider.value
        )
    }

    private var releaseEraseHandler = EventHandler { event: MouseEvent ->
        canvas.graphicsContext2D.clearRect(
            event.x - widthSlider.value / 2,
            event.y - widthSlider.value / 2,
            widthSlider.value,
            widthSlider.value
        )
    }

    fun initialize() {
        canvas.graphicsContext2D.fill = colorPicker.value
        canvas.graphicsContext2D.lineJoin = StrokeLineJoin.ROUND
        canvas.graphicsContext2D.lineCap = StrokeLineCap.ROUND
        delaySlider.valueProperty().addListener { _, _, new ->
            delay = new.toInt()
        }

        canvas.onMouseClicked = clickDrawHandler
        canvas.onMousePressed = pressDrawHandler
        canvas.onMouseDragged = dragDrawHandler
        canvas.onMouseReleased = releaseDrawHandler
    }

    fun bindToSizes() {
        main.prefHeightProperty().bind(stage?.heightProperty())
        main.prefWidthProperty().bind(stage?.widthProperty())
        controlsAnchorPane.prefWidthProperty().bind(main.prefWidthProperty())
        controlsHBox.prefWidthProperty().bind(controlsAnchorPane.prefWidthProperty())
        imageContainer.minHeightProperty().bind(scroll.heightProperty())
        imageContainer.minWidthProperty().bind(scroll.widthProperty())
        imageView.isPreserveRatio = true
    }

    @FXML
    private fun screenshotButtonPressed() {
        if (minimizeOnScreenshot) {
            stage?.isIconified = true
        }
        Timer().schedule(500 + delay.toLong() * 1000) {
            Platform.runLater {
                val screen = Screen.getPrimary().bounds
                val imgOut: WritableImage = robot.getScreenCapture(null, Rectangle2D(0.0, 0.0, screen.width, screen.height))

                imageView.image = imgOut
                imageView.fitWidth = imgOut.width
                imageView.fitHeight = imgOut.height
                canvas.widthProperty().bind(imageView.fitWidthProperty())
                canvas.heightProperty().bind(imageView.fitHeightProperty())
                imageContainer.prefHeightProperty().bind(imageView.fitHeightProperty())
                imageContainer.prefWidthProperty().bind(imageView.fitWidthProperty())
                canvas.graphicsContext2D.clearRect(0.0, 0.0, canvas.width, canvas.height)
                stage?.isIconified = false
            }
        }
    }

    @FXML
    fun colorPicked() {
        canvas.graphicsContext2D.fill = colorPicker.value
    }

    @FXML
    fun clearCheckboxTouched() {
        if (clearCheckbox.isSelected) {
            canvas.onMouseClicked = clickEraseHandler
            canvas.onMousePressed = pressEraseHandler
            canvas.onMouseDragged = dragEraseHandler
            canvas.onMouseReleased = releaseEraseHandler
        } else {
            canvas.onMouseClicked = clickDrawHandler
            canvas.onMousePressed = pressDrawHandler
            canvas.onMouseDragged = dragDrawHandler
            canvas.onMouseReleased = releaseDrawHandler
        }
    }

    @FXML
    private fun minimizeCheckboxTouched() {
        minimizeOnScreenshot = minimizeCheckbox.isSelected
    }
}