package com.david.parkwatch.domain.parser

import com.david.parkwatch.domain.model.VehicleType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceParkingParserTest {

    @Test
    fun parse_fullSpanishPhrase_extractsAllComponents() {
        val result = VoiceParkingParser.parse("Piso 2, zona B, fila 14, puesto 27")

        assertEquals("P2", result.floor)
        assertEquals("B", result.zone)
        assertEquals("14", result.row)
        assertEquals("27", result.spot)
        assertTrue(result.hasData)
        assertTrue(result.isComplete)
    }

    @Test
    fun parse_wordsToNumbers_normalizesCorrectly() {
        val result = VoiceParkingParser.parse("piso dos zona A fila cuatro puesto diez")

        assertEquals("P2", result.floor)
        assertEquals("A", result.zone)
        assertEquals("4", result.row)
        assertEquals("10", result.spot)
        assertTrue(result.isComplete)
    }

    @Test
    fun parse_basementFloor_prefixesWithB() {
        val result1 = VoiceParkingParser.parse("sótano 1 zona C fila 5")
        assertEquals("B1", result1.floor)
        assertEquals("C", result1.zone)
        assertEquals("5", result1.row)

        val result2 = VoiceParkingParser.parse("subsuelo dos zona D fila 3")
        assertEquals("B2", result2.floor)
        assertEquals("D", result2.zone)
        assertEquals("3", result2.row)
    }

    @Test
    fun parse_alreadyFormattedP1OrB1_preservesPrefix() {
        val result = VoiceParkingParser.parse("p1 zona A fila 3")
        assertEquals("P1", result.floor)
        assertEquals("A", result.zone)
        assertEquals("3", result.row)
    }

    @Test
    fun parse_partialInfo_hasDataTrueButIsCompleteFalse() {
        val result = VoiceParkingParser.parse("piso 3 zona B")

        assertEquals("P3", result.floor)
        assertEquals("B", result.zone)
        assertNull(result.row)
        assertNull(result.spot)
        assertTrue(result.hasData)
        assertFalse(result.isComplete)
    }

    @Test
    fun parse_emptyOrIrrelevantText_returnsEmptyParsedInfo() {
        val emptyResult = VoiceParkingParser.parse("")
        assertFalse(emptyResult.hasData)
        assertFalse(emptyResult.isComplete)

        val gibberishResult = VoiceParkingParser.parse("hola reloj como estas")
        assertFalse(gibberishResult.hasData)
        assertFalse(gibberishResult.isComplete)
    }

    @Test
    fun parse_synonyms_recognizesSynonymsCorrectly() {
        val result1 = VoiceParkingParser.parse("nivel 3 sector C hilera 8 plaza 15")
        assertEquals("P3", result1.floor)
        assertEquals("C", result1.zone)
        assertEquals("8", result1.row)
        assertEquals("15", result1.spot)

        val result2 = VoiceParkingParser.parse("planta 4 bloque B línea 2 cajón 5")
        assertEquals("P4", result2.floor)
        assertEquals("B", result2.zone)
        assertEquals("2", result2.row)
        assertEquals("5", result2.spot)
    }

    @Test
    fun parse_vehicleType_detectsVehicleKeywords() {
        val moto = VoiceParkingParser.parse("Dejé la moto en el piso 1 zona A fila 3")
        assertEquals(VehicleType.MOTORCYCLE, moto.vehicleType)
        assertEquals("P1", moto.floor)

        val bici = VoiceParkingParser.parse("Parqueé la bicicleta en piso 2 zona B fila 1")
        assertEquals(VehicleType.BICYCLE, bici.vehicleType)

        val scooter = VoiceParkingParser.parse("Patineta en sótano 1 zona C fila 2")
        assertEquals(VehicleType.SCOOTER, scooter.vehicleType)

        val auto = VoiceParkingParser.parse("Carro en nivel 3 sector D hilera 4")
        assertEquals(VehicleType.CAR, auto.vehicleType)

        val neutral = VoiceParkingParser.parse("Piso 1 zona A fila 2")
        assertNull(neutral.vehicleType)
    }
}
