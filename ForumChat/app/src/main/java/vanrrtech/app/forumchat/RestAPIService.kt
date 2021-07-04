package vanrrtech.app.forumchat

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestAPIService {
    fun addUser(userData: UserDataModel.UserData, onResult: (UserDataModel.UserData?) -> Unit){
        var retrofit = RetrofitUtils.create().create(InterfaceRestAPI::class.java)
        retrofit.addUser(userData).enqueue( object : Callback<UserDataModel.UserData> {
            override fun onResponse(
                call: Call<UserDataModel.UserData>,
                response: Response<UserDataModel.UserData>
            ) {
                val addedUser = response.body()
                onResult(addedUser)
            }

            override fun onFailure(call: Call<UserDataModel.UserData>, t: Throwable) {
                onResult(null)
            }

        })
    }
}