package vanrrtech.app.forumchat

import android.content.Context
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.ArrayList

class UserDataModel {

    //user_id, user_email, user_name, password, information, photo_server
    data class UserData (
        @SerializedName("user_id") val userId: Int?,
        @SerializedName("user_email") val userEmail: String?,
        @SerializedName("user_name") val userName: String?,
        @SerializedName("password") val password: String?,
        @SerializedName("information") val userInformation: String?,
        @SerializedName("photo_server") val userPhoto: String?
    )

    data class ForumData (@SerializedName("forum_id") val forumId: Int?,
                          @SerializedName("room_name") val forumName: String?,
                          @SerializedName("creator_username") val creatorUserName: String?,
                          @SerializedName("date_forum") val creationDate: String?,
                          @SerializedName("details") val forumDetails: String?,
                          @SerializedName("forum_imagelink") val forumPhoto: String?
                          ) : Serializable

    data class CreateForumData (@SerializedName("user_email") val userEmail: String?,
                                @SerializedName("password") val password: String?,
                                @SerializedName("room_name") val forumName: String?,
                                @SerializedName("creator_username") val creatorUserName: String?,
                                @SerializedName("date_forum") val creationDate: String?,
                                @SerializedName("details") val forumDetails: String?,
                                @SerializedName("forum_imagelink") val forumPhoto: String?
    )

    /**
     *[{"chat_id":"2",
     * "forum_id":"2",
     * "user_email":"wira1262@gmail.com",
     * "user_name":"~wing",
     * "room_name":"Gabut Projects",
     * "date_chat":"06-21-2021 @11:13:22",
     * "message":"let's start messaging using our app and check for Bug hehe . . .",
     * "chat_image_link":"https:\/\/vanrrbackend.000webhostapp.com\/forum_chat_backend\/chat_image\/start bug.jpg"},
     */
    data class ForumChatContents (
        @SerializedName("chat_id") val chatId: Int?,
        @SerializedName("forum_id") val forumId: Int?,
        @SerializedName("user_email") val chatUserEmail: String?,
        @SerializedName("user_name") val chatUserName: String?,
        @SerializedName("room_name") val forumName: String?,
        @SerializedName("date_chat") val dateChat: String?,
        @SerializedName("message") val chatMessage: String?,
        @SerializedName("chat_image_link") val chatImageLink: String?
    )

    companion object {
        var mUserInformation : UserData? = null

        var mForumArrayList : ArrayList<ForumData>? = null

        var mForumChatContentList : ArrayList<ForumChatContents>? = null

        fun setSingletonUserData(userData : UserData){
            mUserInformation = userData
        }

        fun setSingletonmForumArrayList (parsedArrayList : ArrayList<ForumData>){
            mForumArrayList = parsedArrayList
        }

        fun setSingletonChatContents (parsedChatList : ArrayList<ForumChatContents>){
            mForumChatContentList = parsedChatList
        }

        fun inputUserData(context: Context, userData : UserData){
            val sp = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString("user_id", userData.userId.toString())
            editor.putString("user_email", userData.userEmail)
            editor.putString("user_name", userData.userName)
            editor.putString("password", userData.password)
            editor.putString("information", userData.userInformation)
            editor.putString("photo_server", userData.userPhoto)
            editor.apply()
        }
        fun isUserDataSaved (context: Context) : Boolean{
            val sp = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
            val userName = sp.getString("user_email", null)
            val userPassword = sp.getString("password", null)

            if(userName != null && userPassword != null){
                return true
            }

            return false
        }
        fun obtainUserData(context: Context) : UserData{
            val sp = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
            val userId = sp.getString("user_id", null)
            val userEmail = sp.getString("user_email", null)
            val userName = sp.getString("user_name", null)
            val userPassword = sp.getString("password", null)
            val userInformation = sp.getString("information", null)
            val userPhoto = sp.getString("photo_server", null)
            val userData = UserData (
                Integer.valueOf(userId!!),
                userEmail,
                userName,
                userPassword,
                userInformation,
                userPhoto
                )

            return userData
        }

        fun logoutUserData(context: Context){
            val sp = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString("user_id", null)
            editor.putString("user_email", null)
            editor.putString("user_name", null)
            editor.putString("password", null)
            editor.putString("information", null)
            editor.putString("photo_server", null)
            editor.apply()
        }
    }


}