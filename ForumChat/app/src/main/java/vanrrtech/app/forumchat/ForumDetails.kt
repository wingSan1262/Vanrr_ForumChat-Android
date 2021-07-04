package vanrrtech.app.forumchat

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
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
 * Use the [ForumDetails.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForumDetails : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var myView : View? = null

    var position : Int? = null

    var mHandler : Handler? = null

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
                        ConstantDefine.SHOW_REQUEST_DELETE_FORUM_SUCCEDED -> {
                            val alertDialog = AlertDialog.Builder(context)
                            val jsonString = msg.obj as String
                            val mJsonObject = JSONObject(jsonString)
                            val message = mJsonObject.getString("response")
                            alertDialog.setTitle("Room Delete $message")
                                .setPositiveButton("OK") { dialog, which ->
                                    // doNothing
                                    var activity = this@ForumDetails.activity as HomeActivity
                                    activity.getBottomSheet()?.state = BottomSheetBehavior.STATE_HIDDEN
                                    UserDataModel.mForumArrayList?.removeAt(position!!)
                                    var fragment = activity.currentFragment as ForumListFragment
//                                    fragment.forumRecylerView?.removeViewAt(position!!)
                                    fragment.forumRecylerView?.adapter?.notifyItemRemoved(position!!)
                                    fragment.forumRecylerView?.adapter?.notifyItemRangeChanged(position!!, UserDataModel.mForumArrayList!!.size )
                                    fragment.forumRecylerView?.adapter?.notifyDataSetChanged()
                                }
                            alertDialog.show()
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
        myView = inflater.inflate(R.layout.fragment_forum_details, container, false)
        return myView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ForumDetails.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ForumDetails().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun deleteForumChat(){
        myView?.findViewById<TextView>(R.id.delete_forum_btn)?.setOnClickListener(null)
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            try {

                /**
                 * // $input = json_decode(file_get_contents("php://input"));
                $userEmail = $_POST["email"]; //login
                $userPassword = $_POST["password"]; //login

                $roomChat = $_POST["room_name"]; //1
                $roomId = $_POST["room_id"]; //2
                 */
                val forumData = UserDataModel.mForumArrayList?.get(position!!)
                val stringParam = "email=${UserDataModel.mUserInformation?.userEmail}&" +
                        "password=${UserDataModel.mUserInformation?.password}&" +
                        "room_name=${forumData?.forumName}&" +
                        "room_id=${forumData?.forumId}"
                //creating a URL
                val url = URL("https://vanrrbackend.000webhostapp.com/forum_chat_backend/DeleteForum.php")


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
                val message = mHandler?.obtainMessage(ConstantDefine.SHOW_REQUEST_DELETE_FORUM_SUCCEDED, sb.toString())
                if (message != null) {
                    mHandler?.sendMessage(message)
                }
            } catch (e: Exception) {
                Log.e("error", "onCreate: " + e.message )
            }
        }
    }

    fun setForumDetails (position: Int?){
        this.position = position
        val forumData = UserDataModel.mForumArrayList?.get(position!!)
        var profileImage = myView?.findViewById<ImageView>(R.id.forum_pictures)
        Picasso.with(this.context).load(forumData?.forumPhoto).into(profileImage)
//        myView?.findViewById<TextView>(R.id.tes_tv)?.text = UserDataModel.mForumArrayList?.get(this.position!!).toString()
        myView?.findViewById<TextView>(R.id.forum_id)?.text = forumData?.forumId.toString()
        myView?.findViewById<TextView>(R.id.forum_name)?.text = forumData?.forumName
        myView?.findViewById<TextView>(R.id.creation_date)?.text = forumData?.creationDate
        myView?.findViewById<TextView>(R.id.forum_details)?.text = forumData?.forumDetails
        myView?.findViewById<ConstraintLayout>(R.id.bottom_sheet_forum_details)?.setBackgroundColor(Color.parseColor("#F96167"))
        if(position!!%2 == 0){
            myView?.findViewById<ConstraintLayout>(R.id.bottom_sheet_forum_details)?.setBackgroundColor(Color.parseColor("#4640b1"))
        }
        val userNameData = UserDataModel.mUserInformation?.userName
        if(forumData?.creatorUserName.equals(userNameData)){
            myView?.findViewById<TextView>(R.id.delete_forum_btn)?.visibility = View.VISIBLE
            myView?.findViewById<TextView>(R.id.delete_forum_btn)?.setOnClickListener {
                deleteForumChat()
            }
        } else{
            myView?.findViewById<TextView>(R.id.delete_forum_btn)?.setOnClickListener(null)
            myView?.findViewById<TextView>(R.id.delete_forum_btn)?.visibility = View.GONE
        }
    }
}