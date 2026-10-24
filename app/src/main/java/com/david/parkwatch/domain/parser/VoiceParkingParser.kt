package com.david.parkwatch.domain.parser

import com.david.parkwatch.domain.model.VehicleType

data class ParsedParkingInfo(
    val floor: String? = null,
    val zone: String? = null,
    val row: String? = null,
    val spot: String? = null,
    val vehicleType: VehicleType? = null,
) {
    val hasData: Boolean
        get() = floor != null || zone != null || row != null || spot != null || vehicleType != null

    val isComplete: Boolean
        get() = !floor.isNullOrBlank() && !zone.isNullOrBlank() && !row.isNullOrBlank()
}

object VoiceParkingParser {
    fun parse(text: String): ParsedParkingInfo {
        val lower = text.lowercase().trim()

        // 1. Piso / Nivel / Sótano
        val floorMatch = Regex("""(?:piso|nivel|planta)\s*([pPbB]?\s*\d+|[a-zA-Z]+)""").find(lower)
            ?: Regex("""(?:s[oó]tano|subsuelo)\s*(\d+|[a-zA-Z]+)""").find(lower)
            ?: Regex("""\b(p[1-9]|b[1-9])\b""").find(lower)

        var floor: String? = null
        if (floorMatch != null) {
            val raw = floorMatch.groupValues.getOrNull(1)?.trim() ?: floorMatch.value
            floor = normalizeFloor(raw, floorMatch.value)
        }

        // 2. Zona / Sector / Bloque
        val zoneMatch = Regex("""(?:zona|sector|bloque)\s*([a-zA-Z0-9])\b""").find(lower)
        var zone: String? = null
        if (zoneMatch != null) {
            zone = zoneMatch.groupValues[1].uppercase()
        }

        // 3. Fila / Hilera / Línea
        val rowMatch = Regex("""(?:fila|hilera|l[ií]nea)\s*(\d+|[a-zA-Z]+)""").find(lower)
        var row: String? = null
        if (rowMatch != null) {
            val raw = rowMatch.groupValues[1].trim()
            row = normalizeNumber(raw) ?: raw
        }

        // 4. Puesto / Plaza / Lugar / Cajón / Espacio / Número
        val spotMatch = Regex("""(?:puesto|plaza|lugar|caj[oó]n|espacio|n[uú]mero)\s*(\d+|[a-zA-Z]+)""").find(lower)
        var spot: String? = null
        if (spotMatch != null) {
            val raw = spotMatch.groupValues[1].trim()
            spot = normalizeNumber(raw) ?: raw
        }

        // 5. Tipo de Vehículo (opcional)
        val vehicleType = when {
            Regex("""\b(moto|motocicleta|motoneta|motorcycle|motorbike)\b""").containsMatchIn(lower) -> VehicleType.MOTORCYCLE
            Regex("""\b(bici|bicicleta|cicla|bike|bicycle)\b""").containsMatchIn(lower) -> VehicleType.BICYCLE
            Regex("""\b(scooter|patineta|monopat[ií]n)\b""").containsMatchIn(lower) -> VehicleType.SCOOTER
            Regex("""\b(auto|carro|coche|veh[ií]culo|camioneta|car|vehicle)\b""").containsMatchIn(lower) -> VehicleType.CAR
            else -> null
        }

        return ParsedParkingInfo(
            floor = floor,
            zone = zone,
            row = row,
            spot = spot,
            vehicleType = vehicleType,
        )
    }

    private fun normalizeFloor(raw: String, fullMatch: String): String {
        val clean = raw.replace(" ", "").uppercase()
        val isBasement = fullMatch.contains("sotano") || fullMatch.contains("sótano") || fullMatch.contains("subsuelo")
        val num = normalizeNumber(clean) ?: clean

        return when {
            isBasement -> "B$num"
            num.startsWith("P") || num.startsWith("B") -> num
            num.toIntOrNull() != null -> "P$num"
            else -> num.uppercase()
        }
    }

    private fun normalizeNumber(raw: String): String? {
        if (raw.all { it.isDigit() }) return raw
        return when (raw.lowercase()) {
            "un", "uno" -> "1"
            "dos" -> "2"
            "tres" -> "3"
            "cuatro" -> "4"
            "cinco" -> "5"
            "seis" -> "6"
            "siete" -> "7"
            "ocho" -> "8"
            "nueve" -> "9"
            "diez" -> "10"
            "once" -> "11"
            "doce" -> "12"
            "trece" -> "13"
            "catorce" -> "14"
            "quince" -> "15"
            "veinte" -> "20"
            else -> null
        }
    }
}

