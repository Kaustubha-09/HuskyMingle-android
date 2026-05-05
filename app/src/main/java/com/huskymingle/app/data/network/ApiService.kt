package com.huskymingle.app.data.network

import com.huskymingle.app.data.model.*
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): Map<String, Any>

    @POST("auth/verify-email")
    suspend fun verifyEmail(@Body body: VerifyEmailRequest): Map<String, Any>

    @GET("auth/me")
    suspend fun getMe(): User

    @POST("users/onboarding")
    suspend fun completeOnboarding(@Body body: OnboardingRequest): User

    @GET("posts/feed")
    suspend fun getFeed(
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 20
    ): List<Post>

    @POST("posts")
    suspend fun createPost(@Body body: CreatePostRequest): Post

    @POST("posts/{id}/like")
    suspend fun likePost(@Path("id") id: String): Map<String, Any>

    @DELETE("posts/{id}/like")
    suspend fun unlikePost(@Path("id") id: String): Map<String, Any>

    @GET("matching/recommendations")
    suspend fun getMatches(): List<MatchUser>

    @POST("users/{id}/follow")
    suspend fun followUser(@Path("id") id: String): Map<String, Any>

    @DELETE("users/{id}/follow")
    suspend fun unfollowUser(@Path("id") id: String): Map<String, Any>

    @GET("events")
    suspend fun getEvents(
        @Query("limit") limit: Int = 100,
        @Query("cursor") cursor: String? = null
    ): List<Event>

    @POST("events/{id}/attend")
    suspend fun attendEvent(@Path("id") id: String): Map<String, Any>

    @GET("jobs")
    suspend fun getJobs(): List<Job>

    @GET("messages/conversations")
    suspend fun getConversations(): List<Conversation>

    @GET("communities")
    suspend fun getCommunities(): List<Community>

    @POST("communities/{id}/join")
    suspend fun joinCommunity(@Path("id") id: String): Map<String, Any>

    @GET("marketplace")
    suspend fun getMarketplace(): List<MarketplaceItem>

    @GET("notifications")
    suspend fun getNotifications(): List<Notification>

    @PATCH("notifications/read-all")
    suspend fun markAllNotificationsRead(): Map<String, Any>

    @GET("search")
    suspend fun search(@Query("q") query: String): SearchResult

    @GET("users/{username}")
    suspend fun getUserProfile(@Path("username") username: String): User

    @GET("gaming")
    suspend fun getGaming(): List<GamingActivity>

    @GET("polls")
    suspend fun getPolls(): List<Poll>

    @POST("polls/{id}/vote")
    suspend fun votePoll(@Path("id") id: String, @Body body: PollVoteRequest): Poll

    @GET("qa")
    suspend fun getQa(): List<QaItem>

    @GET("courses")
    suspend fun getCourses(): List<Course>

    @GET("courses/enrolled")
    suspend fun getEnrolledCourses(): List<Course>

    @POST("courses/{id}/enroll")
    suspend fun enrollCourse(@Path("id") id: String): Map<String, Any>

    @GET("audio")
    suspend fun getAudioRooms(): List<AudioRoom>

    @GET("reels")
    suspend fun getReels(): List<Reel>

    @GET("streaming")
    suspend fun getLiveStreams(): List<LiveStream>

    @GET("bookmarks")
    suspend fun getBookmarks(): List<BookmarkedPost>

    @POST("bookmarks/{postId}")
    suspend fun addBookmark(@Path("postId") postId: String): Map<String, Any>

    @DELETE("bookmarks/{postId}")
    suspend fun removeBookmark(@Path("postId") postId: String): Map<String, Any>
}
