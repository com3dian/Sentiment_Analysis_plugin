package com.europaweg.sentimentanalysis_plugin.data

import javax.swing.table.DefaultTableModel

/**
 * Singleton class to manage and store data related to the Sentiment Analysis plugin.
 */
class DataCenter private constructor() {
    var selectedComment: String = ""
    var fileName: String = ""
    var fileType: String = ""

    companion object {
        var outputsMap: MutableMap<String, ArrayList<Pair<String, String>>> = mutableMapOf()
        private var instance: DataCenter? = null
        val tableModel: DefaultTableModel by lazy {
            // Create the table model with column names
            DefaultTableModel(arrayOf("File", "Comments", "Prediction"), 0)
        }

        // Access the singleton instance or create a new one if it doesn't exist
        @Synchronized
        fun getInstance(): DataCenter {
            if (instance == null) {
                instance = DataCenter()
            }
            return instance!!
        }

        // Generate document for markdown output
        fun getContent(title: String): String {
            var contents = "# $title\n\n"
            for ((key, value) in outputsMap) {
                contents += "## $key\n\n"

                for (pair in value) {
                    val (comments, prediction) = pair
                    contents += "- "
                    contents += "```kotlin\n$comments\n  ```\n"
                    contents += "  Sentiment Analysis Result: **$prediction**"
                    if (prediction == "positive") {
                        contents += " :smile:\n\n\n\n"
                    } else {
                        contents += " :weary:\n\n\n\n"
                    }
                }
            }
            return contents
        }

        fun reset(){
            outputsMap.clear()
            tableModel.setDataVector(null, arrayOf("File", "Comments", "Prediction"))
        }
    }

    fun addToMap(key: String, valueList1: ArrayList<String>, valueList2: Array<String>) {
        // Create a list of pairs from the two value lists
        val pairs = valueList1.zip(valueList2).map { Pair(it.first, it.second) }

        // Check if the key is already in the map
        if (outputsMap.containsKey(key)) {
            // retrieve the existing ArrayList and add the new pairs
            val existingList = outputsMap[key]
            existingList?.addAll(pairs)
        } else {
            // create a new ArrayList and add the pairs to it
            val newList = ArrayList(pairs)
            outputsMap[key] = newList
        }
    }

    fun addToTable(key: String, valueList1: ArrayList<String>, valueList2: Array<String>) {
        // Create a list of pairs from the two value lists
        val pairs = valueList1.zip(valueList2).map { Pair(it.first, it.second) }

        // Add each pair as a new row to the table model
        pairs.forEach { (file, prediction) ->
            tableModel.addRow(arrayOf(key, file, prediction))
        }
    }

    fun show() {
        println("Outputs Map:")
        outputsMap.forEach { (key, value) ->
            println("$key: $value")
            println("\n")
        }
    }
}
