package vanrrtech.app.forumchat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.*
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateForumChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateForumChatFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var uriImageHolder : Uri? = null

    var mHandler : Handler? = null

    var myLoadingLayout : LinearLayout? = null
    var loadingText : TextView? = null

    var myRequesData : UserDataModel.CreateForumData? = null


    var forumNameEditText : EditText? = null
    var forumDetailsEditText : EditText? = null

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            val myUri = data?.data
            val myImageView  = myView?.findViewById<ImageView>(R.id.forum_pictures)
            uriImageHolder = myUri
            Picasso.with(this.context).load(myUri).into(myImageView)
        }
    }


    var myView : View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        mHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                if (msg != null) {
                    when (msg.what) {
                        ConstantDefine.SHOW_REQUEST_FORUM_SUCCEDED -> {
//                            val myAlertDialogBuilder = AlertDialog.Builder(context)
//
//                            myAlertDialogBuilder.setTitle("Forum Chat Created")
//                                .setMessage("Chat room" + myRequesData?.forumName + "was sucessfully created!")
//                                .setPositiveButton("OK", DialogInterface.OnClickListener{
//                                    dialogInterface, which ->
//                                    //do nothing
//                                })
//                                .show()

                            loadingText?.setText("Room created ! ")
                            val loadingHandler = Handler(Looper.getMainLooper())
                            loadingHandler?.postDelayed({
                                myLoadingLayout?.visibility = View.GONE
                                loadingText?.setText("Creating room")
                            },1000)
                        }
                    }
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_create_forum_chat, container, false)
        setUpAllUi(myView!!)
        return myView
    }

    @SuppressLint("NewApi")
    fun setUpAllUi(view: View){
        myLoadingLayout = view.findViewById(R.id.create_loading)
        loadingText = view.findViewById(R.id.create_room_text)
        forumNameEditText = view.findViewById(R.id.forum_name)
        forumDetailsEditText = view.findViewById(R.id.forum_details)
        view.findViewById<ImageButton>(R.id.edit_forum_image).setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            resultLauncher.launch(intent)
        }
        view.findViewById<Button>(R.id.create_room_button).setOnClickListener {
            requestCreateForum()
        }
    }

    fun requestCreateForum(){
        myLoadingLayout?.visibility = View.VISIBLE
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            try {
                var myImageString64:String? = null
                if(uriImageHolder != null){
                    val myBitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uriImageHolder)
                    val myByteArrayOutputStream = ByteArrayOutputStream()
                    myBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, myByteArrayOutputStream)
                    var myByte = myByteArrayOutputStream.toByteArray()
                    val base64Endoce = Base64.encodeToString(myByte, Base64.DEFAULT)
                    myImageString64 = URLEncoder.encode(base64Endoce, "utf-8")
                } else {
                    myImageString64 = null
                }

//                uploadImageOnly(myImageString64!!)

                val myDateFormat = SimpleDateFormat("MM-dd-yyyy @HH:mm:ss")
                val currentDate = myDateFormat.format(Calendar.getInstance().time)


                myRequesData = UserDataModel.CreateForumData(
                    UserDataModel.mUserInformation?.userEmail,
                    UserDataModel.mUserInformation?.password,
                    forumNameEditText?.text.toString(),
                    UserDataModel.mUserInformation?.userName,
                    currentDate,
                    forumDetailsEditText?.text.toString(),
                    myImageString64
                )

                val stringParam = "email=${myRequesData?.userEmail}&" +
                        "password=${myRequesData?.password}&" +
                        "room_name=${myRequesData?.forumName}&" +
                        "user_name=${myRequesData?.creatorUserName}&" +
                        "date_forum=${myRequesData?.creationDate}&" +
                        "details=${myRequesData?.forumDetails}&" +
                        "image_base64=${myRequesData?.forumPhoto}"
                //creating a URL
                val url = URL("https://${HTTPRESTClient.theDomain}/forum_chat_backend/InputForumChat.php")


                HTTPRESTClient.getHttpRestClient()?.sendPostRequest(url, stringParam,
                    mHandler!!, null, ConstantDefine.SHOW_REQUEST_FORUM_SUCCEDED)
            } catch (e: Exception) {
                Log.e("error", "onCreate: " + e.message )
            }
        }
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateForumChatFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            CreateForumChatFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}