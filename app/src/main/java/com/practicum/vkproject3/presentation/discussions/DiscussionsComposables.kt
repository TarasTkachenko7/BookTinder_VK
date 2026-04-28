import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.practicum.vkproject3.R
import com.practicum.vkproject3.ui.common.UserAvatar
import com.practicum.vkproject3.presentation.discussions.ReviewPost

@Composable
fun ReviewPostCard(post: ReviewPost, onClick: () -> Unit) {
    val ratingAndMembersText = stringResource(
        R.string.discussion_card_rating_members_text,
        post.bookRating.toString(),
        post.membersCount
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.discussion_spacing_8))
            .clickable { onClick() }
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = dimensionResource(R.dimen.discussion_card_corner_radius_12),
                topEnd = dimensionResource(R.dimen.discussion_card_corner_radius_12)
            ),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF3E5A47))
        ) {
            Row(
                modifier = Modifier.padding(dimensionResource(R.dimen.discussion_spacing_12)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (post.bookCoverUrl.isNotBlank()) {
                    AsyncImage(
                        model = post.bookCoverUrl,
                        contentDescription = post.bookTitle,
                        modifier = Modifier
                            .width(dimensionResource(R.dimen.discussion_book_cover_width_medium))
                            .height(dimensionResource(R.dimen.discussion_book_cover_height_medium))
                            .clip(RoundedCornerShape(dimensionResource(R.dimen.discussion_card_corner_radius_8))),
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.discussion_book_cover_size_medium))
                            .clip(RoundedCornerShape(dimensionResource(R.dimen.discussion_card_corner_radius_8)))
                            .background(Color.Gray)
                    )
                }

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.discussion_spacing_12)))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        post.bookTitle,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        post.bookAuthor,
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                    Text(
                        ratingAndMembersText,
                        color = Color.White,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Card(
            shape = RoundedCornerShape(
                bottomStart = dimensionResource(R.dimen.discussion_card_corner_radius_12),
                bottomEnd = dimensionResource(R.dimen.discussion_card_corner_radius_12)
            ),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE9E9E9)),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = dimensionResource(R.dimen.discussion_offset_minus_4))
        ) {
            Column(modifier = Modifier.padding(dimensionResource(R.dimen.discussion_spacing_12))) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UserAvatar(
                        nickname = post.userNickname,
                        avatarUrl = post.userAvatarUrl,
                        modifier = Modifier.size(dimensionResource(R.dimen.discussion_avatar_size_medium))
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.discussion_spacing_8)))
                    Text(
                        post.userNickname,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.discussion_spacing_6)))

                Text(
                    post.reviewText,
                    fontSize = 13.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    post.date,
                    modifier = Modifier.align(Alignment.End),
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
