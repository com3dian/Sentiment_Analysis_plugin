package com.europaweg.sentimentanalysis_plugin.inference

import io.kinference.ort.data.tensor.ORTTensor
import io.kinference.ort.ORTEngine
import io.kinference.ort.model.ORTModel
import kotlinx.coroutines.runBlocking

class RobertaInference(private val ortModel: ORTModel) {
    suspend fun predict(array: LongArray, shape: LongArray): FloatArray {
        val orttensor = ORTTensor(array, shape, "input")

        val prediction = ortModel.predict(listOf(orttensor))
        ortModel.close()
        val output = prediction.get("output")
        val outputTensor = output as ORTTensor
        val outputValues = outputTensor.toFloatArray()
        return outputValues
    }
}