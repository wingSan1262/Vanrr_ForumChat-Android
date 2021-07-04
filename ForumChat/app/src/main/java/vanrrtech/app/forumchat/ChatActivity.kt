package vanrrtech.app.forumchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    var mToolbar : Toolbar? = null
    var mForumData : UserDataModel.ForumData? = null

    var mHandler : Handler? = null

    var mRecyclerView : RecyclerView? = null

    var messageEditText : EditText? = null
    var mButtonSendMessage : Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mForumData = intent.getSerializableExtra(ConstantDefine.FORUM_DATA) as UserDataModel.ForumData

        mRecyclerView = findViewById(R.id.chat_recycle_view)
        messageEditText = findViewById(R.id.message_edit_text)
        mButtonSendMessage = findViewById(R.id.send_message_button)

        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = "Room Name"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    ConstantDefine.FORUM_CHAT_QUERY_RESULT -> {
                        val jsonString = msg.obj as String

                        UserDataModel.setSingletonChatContents(queryChatJson(jsonString))

                        val manager = LinearLayoutManager(this@ChatActivity)
                        var horizontalLayout = LinearLayoutManager(
                            this@ChatActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
//                        horizontalLayout.reverseLayout = true

                        mRecyclerView?.layoutManager = manager
                        mRecyclerView?.layoutManager = horizontalLayout
                        val adapter = ChatRecycleViewAdapter(UserDataModel.mForumChatContentList!!, this@ChatActivity!!)


                        mRecyclerView?.adapter = adapter

                    }
                    ConstantDefine.FORUM_CHAT_QUERY -> {
                        queryForumChats(ConstantDefine.FORUM_CHAT_QUERY_RESULT)
                    }

                    ConstantDefine.FORUM_CHAT_REQUEST_CHAT -> {
                        queryForumChats(ConstantDefine.FORUM_CHAT_UPDATE)
                    }

                    ConstantDefine.FORUM_CHAT_UPDATE -> {
                        val jsonString = msg.obj as String
                        UserDataModel.setSingletonChatContents(queryChatJson(jsonString))

                        val mAdapter = mRecyclerView?.adapter as ChatRecycleViewAdapter
                        mAdapter.updateForumArrayList(UserDataModel.mForumChatContentList)
                        mAdapter.notifyDataSetChanged();
                    }

                }
            }
        }

        mHandler?.sendEmptyMessageDelayed(ConstantDefine.FORUM_CHAT_QUERY, 1000)
        mButtonSendMessage?.setOnClickListener {
            requestSendChat()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_toolbar_menu, menu)
        return true
    }

    fun queryChatJson (jsonString : String): ArrayList<UserDataModel.ForumChatContents> {
        var jsonArray = JSONArray(jsonString)
        var mForumChatContent = ArrayList<UserDataModel.ForumChatContents>()
        for (i in 0..jsonArray.length()-1){
            var mJsonObject = jsonArray.getJSONObject(i)

            /**
             * {"chat_id":"4",
             * "forum_id":"20",
             * "user_email":"wira1262@gmail.com",
             * "user_name":"~wings",
             * "room_name":"share your scary meme here",
             * "date_chat":"06-21-2021 @11:13:22",
             * "message":"i am angry !!!!! . . .",
             * "chat_image_link":"https:\/\/vanrrbackend.000webhostapp.com\/forum_chat_backend\/chat_image\/start bug1.jpg"}
             */
            var mForumData = UserDataModel.ForumChatContents(
                Integer.valueOf(mJsonObject.getString("chat_id")),
                Integer.valueOf(mJsonObject.getString("forum_id")),
                mJsonObject.getString("user_email"),
                mJsonObject.getString("user_name"),
                mJsonObject.getString("room_name"),
                mJsonObject.getString("date_chat"),
                mJsonObject.getString("message"),
                mJsonObject.getString("chat_image_link")
            )
            mForumChatContent.add(mForumData)
        }

        return mForumChatContent
    }

    fun queryForumChats(message: Int){
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            try {

                /**
                 * $userEmail = $_POST["user_email"];
                $userPassword = $_POST["password"];
                $forumId = $_POST["forum_id"];
                 */


                val stringParam = "user_email=${UserDataModel.mUserInformation?.userEmail}&" +
                        "password=${UserDataModel.mUserInformation?.password}&" +
                        "forum_id=${mForumData?.forumId}"


                //creating a URL
                val url = URL("https://vanrrbackend.000webhostapp.com/forum_chat_backend/QueryAvailableChat.php")

                //Opening the URL using HttpURLConnection
                val conn = url.openConnection() as HttpURLConnection

                conn.requestMethod = "POST"
                val os: OutputStream = conn.getOutputStream()
                val writer = BufferedWriter(
                    OutputStreamWriter(os, "UTF-8")
                )
                writer.write(stringParam)

                writer.flush()
                writer.close()
                os.close()
                conn.connect()

                //StringBuilder object to read the string from the service
                val sb = StringBuilder()

                BufferedReader(
                    InputStreamReader(conn.inputStream, "utf-8")
                ).use { br ->
                    var responseLine: String? = null
                    while (br.readLine().also { responseLine = it } != null) {
                        sb.append(responseLine!!.trim { it <= ' ' })
                    }
                }
                val message = mHandler?.obtainMessage(message, sb.toString())
                if (message != null) {
                    mHandler?.sendMessage(message)
                }
            } catch (e: Exception) {
                Log.e("error", "onCreate: " + e.message )
            }
        }
    }

    fun requestSendChat(){
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            try {

                /**
                 * $userPassword = $_POST["password"]; //login
                $userEmail = $_POST["email"]; //login
                $userName = $_POST["user_name"]; //2
                $userRoomName = $_POST["room_name"]; //1
                $userDateChat = $_POST["date_forum"]; //3
                $userMessage = $_POST["message"]; //4
                $userImageCode = $_POST["image_base64"]; //5
                $userImageName = $_POST["image_name"]; //5
                $forumId = $_POST["forum_id"]; // 6
                 */

                val myDateFormat = SimpleDateFormat("MM-dd-yyyy @HH:mm:ss")
                val currentDate = myDateFormat.format(Calendar.getInstance().time)

                val imageName = UserDataModel.mUserInformation?.userName + mForumData?.forumName + currentDate

                val stringParam = "email=${UserDataModel.mUserInformation?.userEmail}&" +
                        "password=${UserDataModel.mUserInformation?.password}&" +
                        "user_name=${UserDataModel.mUserInformation?.userName}&" +
                        "room_name=${mForumData?.forumName}&" +
                        "date_forum=${currentDate}&" +
                        "message=${messageEditText?.text.toString()}&" +
                        "image_base64=null&" +
                        "image_name=${imageName}&" +
                        "forum_id=${mForumData?.forumId}"


                //creating a URL
                val url = URL("https://vanrrbackend.000webhostapp.com/forum_chat_backend/InputChatToForum.php")

                //Opening the URL using HttpURLConnection
                val conn = url.openConnection() as HttpURLConnection

                conn.requestMethod = "POST"
                val os: OutputStream = conn.getOutputStream()
                val writer = BufferedWriter(
                    OutputStreamWriter(os, "UTF-8")
                )
                writer.write(stringParam)

                writer.flush()
                writer.close()
                os.close()
                conn.connect()

                //StringBuilder object to read the string from the service
                val sb = StringBuilder()

                BufferedReader(
                    InputStreamReader(conn.inputStream, "utf-8")
                ).use { br ->
                    var responseLine: String? = null
                    while (br.readLine().also { responseLine = it } != null) {
                        sb.append(responseLine!!.trim { it <= ' ' })
                    }
                }
                    mHandler?.sendEmptyMessage(ConstantDefine.FORUM_CHAT_REQUEST_CHAT)
            } catch (e: Exception) {
                Log.e("error", "onCreate: " + e.message )
            }
        }
    }
}