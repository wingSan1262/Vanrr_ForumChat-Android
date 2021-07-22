package vanrrtech.app.forumchat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.*
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*


class ChatActivity : AppCompatActivity() {


    var mToolbar : Toolbar? = null
    var mForumData : UserDataModel.ForumData? = null

    var mHandler : Handler? = null

    var mRecyclerView : RecyclerView? = null

    var messageEditText : EditText? = null
    var mButtonSendMessage : Button? = null

    var imageHolder : Uri? = null
    var mUploadImage : ImageView? = null
    var imageName : TextView? = null

    var forumIcon : ImageView? = null


    var resultLauncher : ActivityResultLauncher<Intent>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mForumData = intent.getSerializableExtra(ConstantDefine.FORUM_DATA) as UserDataModel.ForumData

        mRecyclerView = findViewById(R.id.chat_recycle_view)
        messageEditText = findViewById(R.id.message_edit_text)
        mButtonSendMessage = findViewById(R.id.send_message_button)
        mUploadImage = findViewById(R.id.image_attach_upload)
        imageName = findViewById(R.id.image_upload_name)

//        val height = findViewById<LinearLayout>(R.id.chatlayout_parent).layoutParams.height
//        findViewById<LinearLayout>(R.id.spacer_for_chat_list).layoutParams.height = height

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if(result.data?.action == Intent.ACTION_GET_CONTENT){
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
                    val myUri = data?.data
                    imageHolder = myUri
                    imageName?.text = getFileName(myUri!!)
                    findViewById<LinearLayout>(R.id.layout_image_name).visibility = View.VISIBLE
                }
//            } else {
//                TODO("hmm not yet")
//            }
        }

        mToolbar = findViewById(R.id.toolbar)
        mToolbar?.setBackgroundResource(R.color.grey_toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = mForumData?.forumName
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mUploadImage?.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            resultLauncher?.launch(intent)
        }

        mHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    ConstantDefine.FORUM_CHAT_QUERY_RESULT -> {
                        val jsonString = msg.obj as String
                        if (jsonString.contains("fail", true)){
                            Toast.makeText(this@ChatActivity, "No chat here!", Toast.LENGTH_LONG).show()
                            return
                        }
                        UserDataModel.setSingletonChatContents(parsingChatJson(jsonString))

                        val manager = LinearLayoutManager(this@ChatActivity)
                        var horizontalLayout = LinearLayoutManager(
                            this@ChatActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
//                        horizontalLayout.reverseLayout = true

                        mRecyclerView?.layoutManager = manager
                        mRecyclerView?.layoutManager = horizontalLayout
                        val adapter = ChatRecycleViewAdapter(UserDataModel.mForumChatContentList!!, this@ChatActivity!!, this@ChatActivity)


                        mRecyclerView?.adapter = adapter
                        mRecyclerView?.scrollToPosition((mRecyclerView?.adapter as ChatRecycleViewAdapter).itemCount-1)
                        var player = MediaPlayer.create(this@ChatActivity, R.raw.messagepop)
                        player!!.start()

                        mHandler?.sendEmptyMessageDelayed(ConstantDefine.PERIODIC_UPDATE_CHAT, 500)

                    }
                    ConstantDefine.FORUM_CHAT_QUERY -> {
                        queryForumChats(ConstantDefine.FORUM_CHAT_QUERY_RESULT)
                    }

                    ConstantDefine.PERIODIC_UPDATE_CHAT -> {
                        if(mRecyclerView?.adapter == null){
                            queryForumChats(ConstantDefine.FORUM_CHAT_QUERY_RESULT)
                            return
                        }
                        queryForumChats(ConstantDefine.FORUM_CHAT_UPDATE)

                    }

                    ConstantDefine.FORUM_CHAT_REQUEST_CHAT -> {
                        messageEditText?.setText("", TextView.BufferType.EDITABLE)
                        if(mRecyclerView?.adapter == null){
                            queryForumChats(ConstantDefine.FORUM_CHAT_QUERY_RESULT)
                            return
                        }
                        findViewById<LinearLayout>(R.id.layout_image_name).visibility = View.GONE
                        findViewById<ProgressBar>(R.id.chat_send).visibility = View.GONE
                        imageHolder = null
                        findViewById<TextView>(R.id.image_upload_name).text = ""
                        queryForumChats(ConstantDefine.FORUM_CHAT_UPDATE)
                    }

                    ConstantDefine.FORUM_CHAT_UPDATE -> {
                        val jsonString = msg.obj as String
                        var array = parsingChatJson(jsonString)
                        if(array.size == UserDataModel.mForumChatContentList?.size){
                            mHandler?.sendEmptyMessageDelayed(ConstantDefine.PERIODIC_UPDATE_CHAT, 500)

                            return
                        }

                        UserDataModel.setSingletonChatContents(array)

                        val mAdapter = mRecyclerView?.adapter as ChatRecycleViewAdapter
                        mAdapter.updateForumArrayList(UserDataModel.mForumChatContentList)
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView?.scrollToPosition((mRecyclerView?.adapter as ChatRecycleViewAdapter).itemCount-1)

                        var player = MediaPlayer.create(this@ChatActivity, R.raw.messagepop)
                        player!!.start()

                        mHandler?.sendEmptyMessageDelayed(ConstantDefine.PERIODIC_UPDATE_CHAT, 50)

                    }

                }
            }
        }

        mHandler?.sendEmptyMessageDelayed(ConstantDefine.FORUM_CHAT_QUERY, 1000)
        mButtonSendMessage?.setOnClickListener {
            findViewById<ProgressBar>(R.id.chat_send).visibility = View.VISIBLE
            mHandler?.removeMessages(ConstantDefine.FORUM_CHAT_REQUEST_CHAT)
            requestSendChat()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler?.removeMessages(ConstantDefine.FORUM_CHAT_REQUEST_CHAT)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_toolbar_menu, menu)
        val icon = menu?.findItem(R.id.room_image)
        val forumPhoto = mForumData?.forumPhoto
        val target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: LoadedFrom?) {
                val d: Drawable = BitmapDrawable(resources, bitmap)
                icon?.setIcon(d)
            }

            override fun onBitmapFailed(errorDrawable: Drawable?) {}
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        }
        if(!forumPhoto.equals("null")){
            val load = Picasso.with(this)
                .load(forumPhoto)

            load.into(target)
        } else {
            icon?.setIcon(null)
        }

        return true
    }

    fun parsingChatJson (jsonString : String): ArrayList<UserDataModel.ForumChatContents> {
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
                mJsonObject.getString("user_profile_picture"),
                mJsonObject.getString("room_name"),
                mJsonObject.getString("date_chat"),
                mJsonObject.getString("message"),
                mJsonObject.getString("chat_image_link")
            )
            mForumChatContent.add(mForumData)
        }

        return mForumChatContent
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item?.itemId){
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
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

                HTTPRESTClient.getHttpRestClient()?.sendPostRequest(url, stringParam, mHandler!!, null, message)


            } catch (e: Exception) {
                Log.e("error", "onCreate: " + e.message )
            }
        }
    }

    fun requestSendChat(){
        var myImageString64: String? = null
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            try {

                if(imageHolder != null){
                    val myBitmap = MediaStore.Images.Media.getBitmap(this@ChatActivity.contentResolver, imageHolder)
                    val myByteArrayOutputStream = ByteArrayOutputStream()
                    myBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, myByteArrayOutputStream)
                    var myByte = myByteArrayOutputStream.toByteArray()
                    val base64Endoce = Base64.encodeToString(myByte, Base64.DEFAULT)
                    myImageString64 = URLEncoder.encode(base64Endoce, "utf-8")
                } else {
                    myImageString64 = "null"
                }

                /**
                 * $userPassword = $_POST["password"]; //login
                $userEmail = $_POST["email"]; //login
                $userName = $_POST["user_name"]; //2
                $userProfilePicture = $_POST["user_profile_picture"]; //2
                $userRoomName = $_POST["room_name"]; //1
                $userDateChat = $_POST["date_forum"]; //3
                $userMessage = $_POST["message"]; //4
                $userImageCode = $_POST["image_base64"]; //5
                $userImageName = $_POST["image_name"]; //5
                $forumId = $_POST["forum_id"]; // 6
                 */

                val myDateFormat = SimpleDateFormat("MM-dd-yyyy @HH:mm:ss")
                val currentDate = myDateFormat.format(Calendar.getInstance().time)

                val imageName = imageName?.text.toString() + "-" + mForumData?.forumName+ "-" + currentDate

                val stringParam = "email=${UserDataModel.mUserInformation?.userEmail}&" +
                        "password=${UserDataModel.mUserInformation?.password}&" +
                        "user_name=${UserDataModel.mUserInformation?.userName}&" +
                        "user_profile_picture=${UserDataModel.mUserInformation?.userPhoto}&" +
                        "room_name=${mForumData?.forumName}&" +
                        "date_forum=${currentDate}&" +
                        "message=${messageEditText?.text.toString()}&" +
                        "image_base64=${myImageString64}&" +
                        "image_name=${imageName}&" +
                        "forum_id=${mForumData?.forumId}"


                //creating a URL
                val url = URL("https://vanrrbackend.000webhostapp.com/forum_chat_backend/InputChatToForum.php")

                HTTPRESTClient.getHttpRestClient()?.sendPostRequest(url, stringParam,
                    mHandler!!, null, ConstantDefine.FORUM_CHAT_REQUEST_CHAT)
            } catch (e: Exception) {
                Log.e("error", "onCreate: " + e.message )
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun getFileName(uri: Uri): String? {
        // Obtain a cursor with information regarding this uri
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor?.getCount()!! <= 0) {
            cursor?.close()
            throw IllegalArgumentException("Can't obtain file name, cursor is empty")
        }
        cursor?.moveToFirst()
        val fileName: String =
            cursor?.getString(cursor?.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
        cursor?.close()
        return fileName
    }
}