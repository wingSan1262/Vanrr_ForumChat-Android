package vanrrtech.app.forumchat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.lang.NullPointerException
import java.net.URL
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ForumListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForumListFragment (BottomSheetBehavior: BottomSheetBehavior<View?>?, fragment: Fragment) : Fragment(), onClickListner {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var mBottomSheetBehavior: BottomSheetBehavior<View?>? = null
    var bottomSheetFragment: Fragment? = null

    init {
        mBottomSheetBehavior = BottomSheetBehavior
        bottomSheetFragment = fragment as ForumDetails
    }

    var forumRecylerView : RecyclerView? = null
    var mView : View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }


        val mHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    ConstantDefine.REQUEST_QUERY_AVAILABLE_FORUM -> {
                        val jsonString = msg.obj as String
                        var jsonArray = JSONArray(jsonString)
                        var mForumArrayList = ArrayList<UserDataModel.ForumData>()
                        for (i in 0..jsonArray.length()-1){
                            var mJsonObject = jsonArray.getJSONObject(i)
                            var mForumData = UserDataModel.ForumData(
                                Integer.valueOf(mJsonObject.getString("forum_id")),
                                mJsonObject.getString("room_name"),
                                mJsonObject.getString("creator_username"),
                                mJsonObject.getString("date_forum"),
                                mJsonObject.getString("details"),
                                mJsonObject.getString("forum_imagelink")
                            )
                            mForumArrayList.add(mForumData)
                        }
                        UserDataModel.setSingletonmForumArrayList(mForumArrayList)
                        val manager = LinearLayoutManager(context)
                        var horizontalLayout = LinearLayoutManager(
                            context,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        forumRecylerView = mView?.findViewById(R.id.forum_recycler_view)
                        forumRecylerView?.layoutManager = manager
                        forumRecylerView?.layoutManager = horizontalLayout
                        var adapter : RecycleViewAdapter? = null
                        try{
                            adapter = RecycleViewAdapter(UserDataModel.mForumArrayList!!, context!!, this@ForumListFragment)
                        } catch (e : NullPointerException){
                            return;
                        }


                        forumRecylerView?.adapter = adapter

                    }

                    ConstantDefine.SHOW_TOAST_EMPTY_FORUM_CHAT -> {
                        Toast.makeText(activity?.applicationContext, "Sorry, there is no forum yet", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            try {
                //creating a URL
                val url = URL("https://vanrrbackend.000webhostapp.com/forum_chat_backend/QueryAvailableForums.php")

                // create the json format
                val userEmail = UserDataModel.mUserInformation?.userEmail
                val userPassword = UserDataModel.mUserInformation?.password
                var jsonInputString = "{\"user_name\": \"$userEmail\", \"password\": \"$userPassword\"}";

                // send a Post request for getting the chat from yours truly
                HTTPRESTClient.getHttpRestClient()?.sendPostRequestUsingJsonForm(url, jsonInputString, mHandler,
                    ConstantDefine.SHOW_TOAST_EMPTY_FORUM_CHAT, ConstantDefine.REQUEST_QUERY_AVAILABLE_FORUM)

            } catch (e: Exception) {
                Log.e("error", "onCreate: " + e.message )
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_forum_list, container, false)
        return mView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ForumListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(BottomSheetBehavior: BottomSheetBehavior<View?>?, fragment: Fragment) =
            ForumListFragment(BottomSheetBehavior, fragment).apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
            }
    }

    override fun onItemClick(position: Int) {
        val mIntent = Intent(context, ChatActivity::class.java)
        val mForumData = UserDataModel.mForumArrayList?.get(position)
        mIntent.putExtra(ConstantDefine.FORUM_DATA, mForumData)
        startActivity(mIntent)
    }

    override fun onItemLongClick(position: Int) {
        var myFragment = bottomSheetFragment as ForumDetails
        myFragment!!.setForumDetails(position)
        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

}

interface onClickListner {
    fun onItemClick(position: Int)
    fun onItemLongClick(position: Int)
}