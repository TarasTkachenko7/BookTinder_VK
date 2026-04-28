package com.practicum.vkproject3.presentation.discussions.mock

import com.practicum.vkproject3.presentation.discussions.ReviewComment

object DiscussionMockData {

    val userNicknames: List<String> = listOf(
        "anna_reads",
        "booklover",
        "maria"
    )

    val reviewTexts: List<String> = listOf(
        "РћС‡РµРЅСЊ Р°С‚РјРѕСЃС„РµСЂРЅР°СЏ РєРЅРёРіР°. РћСЃРѕР±РµРЅРЅРѕ РїРѕРЅСЂР°РІРёР»СЃСЏ СЃС‚РёР»СЊ Р°РІС‚РѕСЂР° Рё С‚Рѕ, РєР°Рє РїРѕСЃС‚РµРїРµРЅРЅРѕ СЂР°СЃРєСЂС‹РІР°СЋС‚СЃСЏ РіРµСЂРѕРё.",
        "РСЃС‚РѕСЂРёСЏ РёРЅС‚РµСЂРµСЃРЅР°СЏ, РЅРѕ РјРµСЃС‚Р°РјРё РїРѕРєР°Р·Р°Р»Р°СЃСЊ РЅРµРјРЅРѕРіРѕ Р·Р°С‚СЏРЅСѓС‚РѕР№. Р’ С†РµР»РѕРј РєРЅРёРіР° РѕСЃС‚Р°РІРёР»Р° С…РѕСЂРѕС€РµРµ РІРїРµС‡Р°С‚Р»РµРЅРёРµ.",
        "РЎРёР»СЊРЅР°СЏ РєРЅРёРіР°, РїРѕСЃР»Рµ РєРѕС‚РѕСЂРѕР№ РµС‰Рµ РґРѕР»РіРѕ РґСѓРјР°РµС€СЊ Рѕ СЃСЋР¶РµС‚Рµ Рё РїРµСЂСЃРѕРЅР°Р¶Р°С…. РњРЅРµ РѕС‡РµРЅСЊ РїРѕРЅСЂР°РІРёР»Р°СЃСЊ."
    )

    val reviewDates: List<String> = listOf(
        "СЃРµРіРѕРґРЅСЏ",
        "РІС‡РµСЂР°",
        "2 РґРЅСЏ РЅР°Р·Р°Рґ"
    )

    val commentTexts: List<String> = listOf(
        "РЎРѕРіР»Р°СЃРµРЅ, РєРЅРёРіР° РїСЂР°РІРґР° С†РµРїР»СЏРµС‚.",
        "РњРЅРµ С„РёРЅР°Р» РїРѕРєР°Р·Р°Р»СЃСЏ СЃРїРѕСЂРЅС‹Рј, РЅРѕ РІ С†РµР»РѕРј С‚РѕР¶Рµ РїРѕРЅСЂР°РІРёР»РѕСЃСЊ."
    )

    const val publishedReviewWelcomeComment: String =
        "Р”РѕР±СЂРѕ РїРѕР¶Р°Р»РѕРІР°С‚СЊ РІ РѕР±СЃСѓР¶РґРµРЅРёРµ!"

    fun buildSeedComments(postId: Int): List<ReviewComment> {
        return listOf(
            ReviewComment(
                id = 1,
                postId = postId,
                text = commentTexts[0]
            ),
            ReviewComment(
                id = 2,
                postId = postId,
                text = commentTexts[1]
            )
        )
    }
}
