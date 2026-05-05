package com.huskymingle.app.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    @SerializedName("first_name") val firstName: String = "",
    @SerializedName("last_name") val lastName: String = "",
    @SerializedName("display_name") val displayName: String = "",
    val bio: String = "",
    val avatar: String? = null,
    val major: String = "",
    @SerializedName("graduation_year") val graduationYear: Int? = null,
    val interests: List<String> = emptyList(),
    val skills: List<String> = emptyList(),
    val languages: List<String> = emptyList(),
    @SerializedName("followers_count") val followersCount: Int = 0,
    @SerializedName("following_count") val followingCount: Int = 0,
    @SerializedName("is_verified") val isVerified: Boolean = false,
    @SerializedName("onboarding_completed") val onboardingCompleted: Boolean = false
)

data class Post(
    val id: String = "",
    val content: String = "",
    val author: User = User(),
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("likes_count") val likesCount: Int = 0,
    @SerializedName("comments_count") val commentsCount: Int = 0,
    @SerializedName("is_liked") val isLiked: Boolean = false,
    val hashtags: List<String> = emptyList(),
    val images: List<String> = emptyList(),
    val type: String = "text"
)

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    @SerializedName("start_time") val startTime: String = "",
    @SerializedName("end_time") val endTime: String = "",
    val organizer: User = User(),
    @SerializedName("attendees_count") val attendeesCount: Int = 0,
    @SerializedName("is_attending") val isAttending: Boolean = false,
    val category: String = "",
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("is_virtual") val isVirtual: Boolean = false
)

data class Job(
    val id: String = "",
    val title: String = "",
    val company: String = "",
    val description: String = "",
    val location: String = "",
    val type: String = "",
    val salary: String? = null,
    @SerializedName("posted_at") val postedAt: String = "",
    @SerializedName("apply_url") val applyUrl: String? = null,
    val tags: List<String> = emptyList()
)

data class Conversation(
    val id: String = "",
    val participant: User = User(),
    @SerializedName("last_message") val lastMessage: Message? = null,
    @SerializedName("unread_count") val unreadCount: Int = 0,
    @SerializedName("updated_at") val updatedAt: String = ""
)

data class Message(
    val id: String = "",
    val content: String = "",
    val sender: User = User(),
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("is_read") val isRead: Boolean = false
)

data class Community(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    @SerializedName("members_count") val membersCount: Int = 0,
    @SerializedName("is_member") val isMember: Boolean = false,
    val category: String = "",
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("created_at") val createdAt: String = ""
)

data class MarketplaceItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val seller: User = User(),
    val category: String = "",
    val condition: String = "",
    val images: List<String> = emptyList(),
    @SerializedName("is_sold") val isSold: Boolean = false,
    @SerializedName("created_at") val createdAt: String = ""
)

data class Notification(
    val id: String = "",
    val type: String = "",
    val message: String = "",
    @SerializedName("is_read") val isRead: Boolean = false,
    @SerializedName("created_at") val createdAt: String = "",
    val actor: User? = null,
    @SerializedName("entity_id") val entityId: String? = null
)

data class SearchResult(
    val users: List<User> = emptyList(),
    val posts: List<Post> = emptyList(),
    val events: List<Event> = emptyList(),
    val communities: List<Community> = emptyList()
)

data class MatchUser(
    val user: User = User(),
    val score: Double = 0.0,
    @SerializedName("shared_interests") val sharedInterests: List<String> = emptyList(),
    @SerializedName("is_following") val isFollowing: Boolean = false
)

data class Poll(
    val id: String = "",
    val question: String = "",
    val options: List<PollOption> = emptyList(),
    val author: User = User(),
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("expires_at") val expiresAt: String? = null,
    @SerializedName("total_votes") val totalVotes: Int = 0,
    @SerializedName("user_voted_option") val userVotedOption: String? = null
)

data class PollOption(
    val id: String = "",
    val text: String = "",
    val votes: Int = 0
)

data class QaItem(
    val id: String = "",
    val question: String = "",
    val body: String = "",
    val author: User = User(),
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("answers_count") val answersCount: Int = 0,
    @SerializedName("upvotes_count") val upvotesCount: Int = 0,
    val tags: List<String> = emptyList()
)

data class Course(
    val id: String = "",
    val code: String = "",
    val name: String = "",
    val description: String = "",
    val instructor: String = "",
    val credits: Int = 0,
    val department: String = "",
    @SerializedName("enrolled_count") val enrolledCount: Int = 0,
    @SerializedName("is_enrolled") val isEnrolled: Boolean = false
)

data class AudioRoom(
    val id: String = "",
    val title: String = "",
    val host: User = User(),
    val topic: String = "",
    @SerializedName("listeners_count") val listenersCount: Int = 0,
    @SerializedName("is_live") val isLive: Boolean = false,
    @SerializedName("created_at") val createdAt: String = ""
)

data class Reel(
    val id: String = "",
    val title: String = "",
    val videoUrl: String = "",
    val thumbnail: String? = null,
    val author: User = User(),
    @SerializedName("likes_count") val likesCount: Int = 0,
    @SerializedName("views_count") val viewsCount: Int = 0,
    @SerializedName("created_at") val createdAt: String = ""
)

data class LiveStream(
    val id: String = "",
    val title: String = "",
    val streamer: User = User(),
    @SerializedName("viewers_count") val viewersCount: Int = 0,
    @SerializedName("started_at") val startedAt: String = "",
    val category: String = "",
    val thumbnail: String? = null
)

data class GamingActivity(
    val id: String = "",
    val title: String = "",
    val game: String = "",
    val host: User = User(),
    @SerializedName("players_count") val playersCount: Int = 0,
    @SerializedName("max_players") val maxPlayers: Int = 0,
    val status: String = "",
    @SerializedName("created_at") val createdAt: String = ""
)

data class BookmarkedPost(
    val id: String = "",
    val post: Post = Post(),
    @SerializedName("created_at") val createdAt: String = ""
)

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String
)
data class VerifyEmailRequest(val email: String, val code: String)
data class OnboardingRequest(
    val interests: List<String>,
    val skills: List<String>,
    val languages: List<String>,
    val major: String,
    @SerializedName("graduation_year") val graduationYear: Int?
)
data class CreatePostRequest(val content: String, val hashtags: List<String> = emptyList())
data class PollVoteRequest(@SerializedName("option_id") val optionId: String)

data class LoginResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    val user: User
)

data class FeedResponse(
    val data: List<Post> = emptyList(),
    @SerializedName("next_cursor") val nextCursor: String? = null
)
