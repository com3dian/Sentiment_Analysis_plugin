package com.europaweg.sentimentanalysis_plugin.settings

import com.intellij.ide.util.PropertiesComponent

class PluginState {

    private val properties = PropertiesComponent.getInstance()

    var modelPath: String
        get() = properties.getValue("modelPath", "empty")
        set(value) = properties.setValue("modelPath", value)

    var tokenizerPath: String
        get() = properties.getValue("tokenizerPath", "empty")
        set(value) = properties.setValue("tokenizerPath", value)

    fun init() {
        // Perform any initialization tasks here
        // For example, set default values if needed
        if (modelPath.isEmpty()) {
            modelPath = "empty"
        }
        if (tokenizerPath.isEmpty()) {
            tokenizerPath = "empty"
        }
    }
}