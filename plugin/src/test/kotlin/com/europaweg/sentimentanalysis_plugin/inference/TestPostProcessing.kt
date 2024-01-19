package com.europaweg.sentimentanalysis_plugin.inference

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.assertThrows

class TestPostProcessing {

    @Test
    fun testPostProcessing() {
        // Test case 1: Valid input
        val input1 = floatArrayOf(1.0f, 2.0f, 3.0f, 4.0f)
        val expectedOutput1 = arrayOf("positive", "positive")
        assertArrayEquals(expectedOutput1, postProcessing(input1))

        // Test case 2: Valid input with different order
        val input2 = floatArrayOf(4.0f, 3.0f, 2.0f, 1.0f)
        val expectedOutput2 = arrayOf("negative", "negative")
        assertArrayEquals(expectedOutput2, postProcessing(input2))

        // Test case 3: Null input
        val input3 = null
        assertArrayEquals(null, postProcessing(input3))

        // Test case 4: Null input
        val input4 = floatArrayOf()
        assertArrayEquals(null, postProcessing(input4))

        // Test case 5: Invalid input with odd length
        val input5 = floatArrayOf(1.0f, 2.0f, 3.0f)
        assertThrows<IllegalArgumentException> { postProcessing(input5) }
    }
}
