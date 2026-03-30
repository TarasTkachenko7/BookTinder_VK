package com.practicum.vkproject3.presentation.discussions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.vkproject3.domain.books.BookRepository
import com.practicum.vkproject3.domain.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReviewPost(
    val id: Int,
    val bookId: String,
    val bookTitle: String,
    val bookAuthor: String,
    val bookRating: Float,
    val bookCoverUrl: String,
    val membersCount: Int,
    val userNickname: String,
    val reviewText: String,
    val date: String,
    val userAvatarUrl: String? = null
)
data class ReviewComment(
    val id: Int,
    val postId: Int,
    val text: String,
    val date: String = "только что",
    val authorNickname: String = "Пользователь ${(1..1000).random()}",
    val authorAvatarUrl: String? = null
)

data class ReviewBookUi(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val rating: Float
)

data class DiscussionsUiState(
    val isLoading: Boolean = false,
    val posts: List<ReviewPost> = emptyList(),
    val error: String? = null
)

data class CreateReviewState(
    val isBookLoading: Boolean = false,
    val selectedBook: ReviewBookUi? = null,
    val reviewText: String = "",
    val error: String? = null,
    val isPublishing: Boolean = false
) {
    val canPublish: Boolean
        get() = selectedBook != null && reviewText.isNotBlank()
}

data class BookPickerState(
    val isLoading: Boolean = false,
    val books: List<ReviewBookUi> = emptyList(),
    val error: String? = null
)

class DiscussionsViewModel(
    private val repository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscussionsUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private val _createReviewState = MutableStateFlow(CreateReviewState())
    val createReviewState = _createReviewState.asStateFlow()

    private val _bookPickerState = MutableStateFlow(BookPickerState())
    val bookPickerState = _bookPickerState.asStateFlow()

    private val _comments = MutableStateFlow<Map<Int, List<ReviewComment>>>(emptyMap())
    val comments = _comments.asStateFlow()

    init {
        loadInitialPosts()
    }

    private fun loadInitialPosts() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            try {
                val (books, _) = repository.getBooks(1)
                val seedBooks = books.take(3)

                val mockPosts = seedBooks.mapIndexed { index, book ->
                    ReviewPost(
                        id = index + 1,
                        bookId = book.id,
                        bookTitle = book.title,
                        bookAuthor = book.author,
                        bookRating = book.safeRating(),
                        bookCoverUrl = book.imageUrl,
                        membersCount = 4 + index,
                        userNickname = listOf("anna_reads", "booklover", "maria")[index % 3],
                        reviewText = listOf(
                            "Очень атмосферная книга. Особенно понравился стиль автора и то, как постепенно раскрываются герои.",
                            "История интересная, но местами показалась немного затянутой. В целом книга оставила хорошее впечатление.",
                            "Сильная книга, после которой еще долго думаешь о сюжете и персонажах. Мне очень понравилась."
                        )[index % 3],
                        date = listOf("сегодня", "вчера", "2 дня назад")[index % 3],
                        userAvatarUrl = null
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        posts = mockPosts,
                        error = null
                    )
                }

                _comments.value = mockPosts.associate { post ->
                    post.id to listOf(
                        ReviewComment(
                            id = 1,
                            postId = post.id,
                            text = "Согласен, книга правда цепляет."
                        ),
                        ReviewComment(
                            id = 2,
                            postId = post.id,
                            text = "Мне финал показался спорным, но в целом тоже понравилось."
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        posts = emptyList(),
                        error = "Не удалось загрузить рецензии"
                    )
                }
            }
        }
    }

    fun loadBookForReview(bookId: String) {
        val selected = _createReviewState.value.selectedBook
        if (selected?.id == bookId) return

        viewModelScope.launch {
            _createReviewState.update {
                it.copy(
                    isBookLoading = true,
                    selectedBook = null,
                    error = null
                )
            }

            try {
                val book = findBookById(bookId)
                android.util.Log.d("CreateReview", "bookId=$bookId, found=${book != null}")

                if (book == null) {
                    _createReviewState.update {
                        it.copy(
                            isBookLoading = false,
                            selectedBook = null,
                            error = "Книга не найдена"
                        )
                    }
                    return@launch
                }

                _createReviewState.update {
                    it.copy(
                        isBookLoading = false,
                        selectedBook = book.toReviewBookUi(),
                        error = null
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("CreateReview", "loadBookForReview error", e)
                _createReviewState.update {
                    it.copy(
                        isBookLoading = false,
                        selectedBook = null,
                        error = "Ошибка загрузки книги"
                    )
                }
            }
        }
    }

    fun loadBooksForPicker() {
        if (_bookPickerState.value.books.isNotEmpty()) return

        viewModelScope.launch {
            _bookPickerState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            try {
                val loadedBooks = mutableListOf<ReviewBookUi>()

                for (page in 1..3) {
                    val (books, _) = repository.getBooks(page)
                    loadedBooks += books.map { book -> book.toReviewBookUi() }
                }

                _bookPickerState.update {
                    it.copy(
                        isLoading = false,
                        books = loadedBooks.distinctBy { book -> book.id },
                        error = null
                    )
                }
            } catch (e: Exception) {
                _bookPickerState.update {
                    it.copy(
                        isLoading = false,
                        books = emptyList(),
                        error = "Не удалось загрузить книги"
                    )
                }
            }
        }
    }

    fun searchBooksForPicker(query: String): List<ReviewBookUi> {
        val books = _bookPickerState.value.books

        if (query.isBlank()) return books

        return books.filter { book ->
            book.title.contains(query, ignoreCase = true) ||
                    book.author.contains(query, ignoreCase = true)
        }
    }

    fun selectBookForReview(book: ReviewBookUi) {
        _createReviewState.update {
            it.copy(
                isBookLoading = false,
                selectedBook = book,
                error = null
            )
        }
    }

    fun clearSelectedBookForReview() {
        _createReviewState.update {
            it.copy(
                isBookLoading = false,
                selectedBook = null,
                error = null
            )
        }
    }

    fun setSelectedBookForReview(
        id: String,
        title: String,
        author: String,
        coverUrl: String,
        rating: Float
    ) {
        _createReviewState.update {
            it.copy(
                isBookLoading = false,
                selectedBook = ReviewBookUi(
                    id = id,
                    title = title,
                    author = author,
                    coverUrl = coverUrl,
                    rating = rating
                ),
                error = null
            )
        }
    }

    fun setSelectedBookForReview(post: ReviewPost) {
        _createReviewState.update {
            it.copy(
                isBookLoading = false,
                selectedBook = ReviewBookUi(
                    id = post.bookId,
                    title = post.bookTitle,
                    author = post.bookAuthor,
                    coverUrl = post.bookCoverUrl,
                    rating = post.bookRating
                ),
                error = null
            )
        }
    }

    fun onReviewTextChanged(text: String) {
        _createReviewState.update {
            it.copy(reviewText = text)
        }
    }

    fun resetCreateReviewState() {
        _createReviewState.value = CreateReviewState()
    }

    fun publishReview(onSuccess: () -> Unit = {}) {
        val form = _createReviewState.value
        val book = form.selectedBook ?: return

        if (!form.canPublish || form.isPublishing) return

        viewModelScope.launch {
            _createReviewState.update {
                it.copy(isPublishing = true)
            }

            val newId = (_uiState.value.posts.maxOfOrNull { it.id } ?: 0) + 1

            val newPost = ReviewPost(
                id = newId,
                bookId = book.id,
                bookTitle = book.title,
                bookAuthor = book.author,
                bookRating = book.rating,
                bookCoverUrl = book.coverUrl,
                membersCount = 1,
                userNickname = "you",
                reviewText = form.reviewText.trim(),
                date = "только что",
                userAvatarUrl = null
            )

            _uiState.update { currentState ->
                currentState.copy(
                    posts = listOf(newPost) + currentState.posts
                )
            }

            _comments.update { current ->
                current + (
                        newId to listOf(
                            ReviewComment(
                                id = 1,
                                postId = newId,
                                text = "Добро пожаловать в обсуждение!"
                            )
                        )
                        )
            }

            _createReviewState.value = CreateReviewState()
            onSuccess()
        }
    }

    fun getPostById(id: Int): ReviewPost? {
        return _uiState.value.posts.find { it.id == id }
    }

    fun getCommentsForPost(postId: Int): List<ReviewComment> {
        return _comments.value[postId].orEmpty()
    }

    fun addComment(postId: Int, text: String, authorNickname: String = "current_user") {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return

        val currentComments = _comments.value[postId].orEmpty()
        val newCommentId = (currentComments.maxOfOrNull { it.id } ?: 0) + 1

        val newComment = ReviewComment(
            id = newCommentId,
            postId = postId,
            text = trimmed,
            date = "только что",
            authorNickname = authorNickname,
            authorAvatarUrl = null  // Пока null, потом можно добавить аватар текущего пользователя
        )

        _comments.update { current ->
            current + (postId to (currentComments + newComment))
        }
    }

    private suspend fun findBookById(bookId: String): Book? {
        var page = 1

        repeat(10) {
            val (books, _) = repository.getBooks(page)
            val found = books.firstOrNull { it.id == bookId }
            if (found != null) return found
            page++
        }

        return null
    }

    private fun Book.toReviewBookUi(): ReviewBookUi {
        return ReviewBookUi(
            id = id,
            title = title,
            author = author,
            coverUrl = imageUrl,
            rating = safeRating()
        )
    }

    private fun Book.safeRating(): Float {
        return if (rating > 0) {
            rating.toFloat()
        } else {
            ((id.hashCode().toUInt().toLong() % 21) / 10f + 3.0f).coerceIn(1f, 5f)
        }
    }
}