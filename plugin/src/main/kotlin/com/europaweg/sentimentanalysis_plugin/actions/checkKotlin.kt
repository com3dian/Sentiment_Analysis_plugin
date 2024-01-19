package com.europaweg.sentimentanalysis_plugin.actions

import java.util.*

fun isKotlinFile(filePath: String): Boolean {
    val kotlinFileExtensions = listOf(".kt", ".kts")
    val lowerCaseFilePath = filePath.lowercase()

    return kotlinFileExtensions.any { lowerCaseFilePath.endsWith(it) }
}
