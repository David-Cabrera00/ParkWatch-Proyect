package com.david.parkwatch.domain.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceParkingParserTest {

    @Test
    fun parseFullSentenceFromProductFlow() {
        val input = "Piso 2, zona B, fila 14, puesto 27"
        val result = VoiceParkingParser.parse(input)

        assertEquals("P2", result.floor)
        assertEquals("B", result.zone)
        assertEquals("14", result.row)
        assertEquals("27", result.spot)
        assertTrue(result.isComplete)
    }

    @Test
    fun parseBasementAndWordNumbers() {
        val input = "Sótano 1 sector A hilera tres lugar cinco"
        val result = VoiceParkingParser.parse(input)

        assertEquals("B1", result.floor)
        assertEquals("A", result.zone)
        assertEquals("3", result.row)
        assertEquals("5", result.spot)
        assertTrue(result.isComplete)
    }

    @Test
    fun parsePartialInformation() {
        val input = "Piso P3 zona C"
        val result = VoiceParkingParser.parse(input)

        assertEquals("P3", result.floor)
        assertEquals("C", result.zone)
        assertEquals(null, result.row)
        assertEquals(null, result.spot)
        assertTrue(result.hasData)
    }
}

