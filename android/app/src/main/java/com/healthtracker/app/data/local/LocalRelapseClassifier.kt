package com.healthtracker.app.data.local

/**
 * Tiny on-device "classifier" (rule-based) inspired by the server ML labels.
 * Keeps the UX of "insights" without any network call.
 */
object LocalRelapseClassifier {

    fun classify(bucket: String, stress: Int, reason: String): Pair<String, String> {
        val text = "$bucket $reason".lowercase()
        return when {
            stress >= 7 && any(text, "stress", "anxiety", "panic", "deadline", "pressure", "стрес", "тривог", "дедлайн") ->
                "stress_triggered" to "Патерн: стрес або емоційний тиск"

            any(text, "party", "friends", "bar", "wedding", "social", "друз", "вечірк", "бар", "свят") ->
                "social_situation" to "Патерн: соціальне оточення"

            any(text, "bored", "nothing", "habit", "idle", "нудьг", "звичк", "нічого робити") ->
                "boredom_or_habit" to "Патерн: нудьга або автоматична звичка"

            any(text, "tired", "sleep", "exhausted", "night shift", "втом", "сон", "нічна зміна") ->
                "fatigue_or_sleep" to "Патерн: втома або сон"

            else ->
                "unknown_pattern" to "Патерн: змішані або неочевидні тригери"
        }
    }

    private fun any(text: String, vararg needles: String): Boolean =
        needles.any { text.contains(it) }
}
