package com.europaweg.sentimentanalysis_plugin.data

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.swing.table.DefaultTableModel

class DataCenterTest {

    private lateinit var dataCenter: DataCenter

    @BeforeEach
    fun setUp() {
        // Reset DataCenter before each test
        DataCenter.reset()
        dataCenter = DataCenter.getInstance()
    }

    @Test
    fun addToMap() {
        val key = "TestKey"
        val valueList1 = arrayListOf("Comment1", "Comment2")
        val valueList2 = arrayOf("positive", "negative")

        dataCenter.addToMap(key, valueList1, valueList2)

        assertTrue(DataCenter.Companion.outputsMap.containsKey(key))
        assertEquals(2, DataCenter.Companion.outputsMap[key]?.size)

        val pairs = DataCenter.Companion.outputsMap[key]
        assertEquals(Pair("Comment1", "positive"), pairs?.get(0))
        assertEquals(Pair("Comment2", "negative"), pairs?.get(1))
    }

    @Test
    fun addToTable() {
        val key = "TestKey"
        val valueList1 = arrayListOf("Comment1", "Comment2")
        val valueList2 = arrayOf("positive", "negative")

        dataCenter.addToTable(key, valueList1, valueList2)

        assertEquals(2, DataCenter.Companion.tableModel.rowCount)

        assertEquals("TestKey", DataCenter.Companion.tableModel.getValueAt(0, 0))
        assertEquals("Comment1", DataCenter.Companion.tableModel.getValueAt(0, 1))
        assertEquals("positive", DataCenter.Companion.tableModel.getValueAt(0, 2))

        assertEquals("TestKey", DataCenter.Companion.tableModel.getValueAt(1, 0))
        assertEquals("Comment2", DataCenter.Companion.tableModel.getValueAt(1, 1))
        assertEquals("negative", DataCenter.Companion.tableModel.getValueAt(1, 2))
    }

    @Test
    fun getContent() {
        val key = "TestKey"
        val valueList1 = arrayListOf("Comment1", "Comment2")
        val valueList2 = arrayOf("positive", "negative")

        dataCenter.addToMap(key, valueList1, valueList2)

        val content = DataCenter.getContent("TestTitle")

        assertTrue(content.contains("# TestTitle"))
        assertTrue(content.contains("## TestKey"))
        assertTrue(content.contains("```kotlin\nComment1\n  ```\n"))
        assertTrue(content.contains("Sentiment Analysis Result: **positive**"))
        assertTrue(content.contains(":smile:"))
        assertTrue(content.contains("```kotlin\nComment2\n  ```\n"))
        assertTrue(content.contains("Sentiment Analysis Result: **negative**"))
        assertTrue(content.contains(":weary:"))
    }

    @Test
    fun reset() {
        val key = "TestKey"
        val valueList1 = arrayListOf("Comment1", "Comment2")
        val valueList2 = arrayOf("positive", "negative")

        dataCenter.addToMap(key, valueList1, valueList2)

        DataCenter.reset()

        assertTrue(DataCenter.Companion.outputsMap.isEmpty())
        assertEquals(0, DataCenter.Companion.tableModel.rowCount)
    }
}
