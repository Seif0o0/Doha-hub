package code_grow.dohahub.app.repository

import code_grow.dohahub.app.retrofit.AppApi

class ChatRepository {
    suspend fun fetchMessages(senderId: Int, receiverId: Int) =
        AppApi.chatServices.fetchMessages(senderId, receiverId)

    suspend fun sendMessage(senderId: Int, receiverId: Int, message: String) =
        AppApi.chatServices.sendMessage(senderId, receiverId, message)
}