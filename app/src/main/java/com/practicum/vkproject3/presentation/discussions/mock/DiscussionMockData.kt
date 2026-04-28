package com.practicum.vkproject3.presentation.discussions.mock

import com.practicum.vkproject3.presentation.discussions.ReviewComment

object DiscussionMockData {

    val userNicknames: List<String> = listOf(
        "anna_reads",
        "booklover",
        "maria"
    )

    val reviewTexts: List<String> = listOf(
        "Очень атмосферная книга. Особенно понравился стиль автора и то, как постепенно раскрываются герои.",
        "История интересная, но местами показалась немного затянутой. В целом книга оставила хорошее впечатление.",
        "Сильная книга, после которой еще долго думаешь о сюжете и персонажах. Мне очень понравилась."
    )

    val reviewDates: List<String> = listOf(
        "сегодня",
        "вчера",
        "2 дня назад"
    )

    val commentTexts: List<String> = listOf(
        "Согласен, книга правда цепляет.",
        "Мне финал показался спорным, но в целом тоже понравилось."
    )

    const val publishedReviewWelcomeComment: String =
        "Добро пожаловать в обсуждение!"

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
