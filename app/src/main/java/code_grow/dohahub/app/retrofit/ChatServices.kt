package code_grow.dohahub.app.retrofit

import code_grow.dohahub.app.model.ApiResponse
import code_grow.dohahub.app.model.Message
import code_grow.dohahub.app.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface ChatServices {
    //?user_id=11&to_id=3
    @GET(Constants.CHAT.plus(Constants.GET_MESSAGES))
    suspend fun fetchMessages(
        @Query("user_id") senderId: Int,
        @Query("to_id") receiverId: Int
    ): ApiResponse<MutableList<Message>>

    @GET(Constants.CHAT.plus(Constants.SEND_MESSAGE))
    suspend fun sendMessage(
        @Query("user_id") senderId: Int,
        @Query("to_id") receiverId: Int,
        @Query("message") message: String
    ): ApiResponse<MutableList<Message>>
}