package model

enum class Currency {
    RUB, USD, EUR;

    companion object {
        fun fromString(s: String): Currency {
            val normalizedName = s.toUpperCase();
            val availableValues = values().map { it.name }
            if (availableValues.contains(normalizedName)) {
                return valueOf(normalizedName)
            } else {
                throw IllegalArgumentException("Unknown currency: $s")
            }
        }
    }
}