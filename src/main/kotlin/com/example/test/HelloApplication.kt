package com.example.test

import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.stage.Stage
import java.time.LocalDateTime

class HelloApplication : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("hello-view.fxml"))
        val layout: Parent = fxmlLoader.load()
        stage.height = 700.0
        stage.width = 900.0
        fxmlLoader.getController<HelloController>().stage = stage
        fxmlLoader.getController<HelloController>().bindToSizes()
        val scene = Scene(layout)
        stage.title = "ScreenshotApp"
        stage.scene = scene
        stage.show()
    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}