package com.example.foodplannerapplication.core.functions

object CountryFlagMapper {
    val AREA_TO_COUNTRY_CODE = mapOf(
        "American" to "US",
        "British" to "GB",
        "Canadian" to "CA",
        "Chinese" to "CN",
        "Croatian" to "HR",
        "Dutch" to "NL",
        "Egyptian" to "EG",
        "Filipino" to "PH",
        "French" to "FR",
        "Greek" to "GR",
        "Indian" to "IN",
        "Irish" to "IE",
        "Italian" to "IT",
        "Jamaican" to "JM",
        "Japanese" to "JP",
        "Kenyan" to "KE",
        "Malaysian" to "MY",
        "Mexican" to "MX",
        "Moroccan" to "MA",
        "Polish" to "PL",
        "Portuguese" to "PT",
        "Russian" to "RU",
        "Spanish" to "ES",
        "Thai" to "TH",
        "Tunisian" to "TN",
        "Turkish" to "TR",
        "Ukrainian" to "UA",
        "Uruguayan" to "UY",
        "Vietnamese" to "VN",
        "Unknown" to null
    )

    fun getFlagEmoji(area: String?): String {
        val countryCode = AREA_TO_COUNTRY_CODE[area] ?: return "❓"

        return countryCode.uppercase().map {
            Character.toChars(127397 + it.code).concatToString()
        }.joinToString("")
    }

    fun getFlagUrl(area: String?): String {
        val countryCode = AREA_TO_COUNTRY_CODE[area] ?: return ""
        return "https://flagcdn.com/w320/${countryCode.lowercase()}.png"
    }
}
