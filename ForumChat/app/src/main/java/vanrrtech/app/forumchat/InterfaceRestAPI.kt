package vanrrtech.app.forumchat

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface InterfaceRestAPI {
    @Headers("Content-Type: application/json")
    @POST("forum_chat_backend/LogIn.php")
    fun addUser(@Body userData: UserDataModel.UserData): Call<UserDataModel.UserData>
}