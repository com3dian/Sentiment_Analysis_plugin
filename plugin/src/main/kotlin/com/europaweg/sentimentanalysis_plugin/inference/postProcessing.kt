package com.europaweg.sentimentanalysis_plugin.inference

fun postProcessing(floatArray: FloatArray?): Array<String>? {
    if (floatArray != null) {
        if (floatArray.isEmpty()) {
            return null
        }
        require(floatArray.size % 2 == 0) { "Input array must have an even length (2N)" }

        val answerArray = Array(floatArray.size / 2) {
            val index = it * 2
            if (floatArray[index] > floatArray[index + 1]) {
                "negative"
            } else {
                "positive"
            }
        }
        return answerArray
    }
    return null
}