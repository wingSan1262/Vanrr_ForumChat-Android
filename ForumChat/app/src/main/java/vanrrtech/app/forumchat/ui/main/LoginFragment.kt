package vanrrtech.app.forumchat.ui.main

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import vanrrtech.app.forumchat.databinding.FragmentLoginBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class LoginFragment : Fragment() {
    var userEmailField : EditText? = null
    var userPasswordField : EditText? = null
    var loginButton : ImageButton? = null

    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root = binding.root

        val textView: TextView
        pageViewModel.text.observe(viewLifecycleOwner, Observer {

        })
        return root
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): LoginFragment {
            return LoginFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//
//
//        userEmailField = findViewById(R.id.username)
//        userPasswordField = findViewById(R.id.password)
//        loginButton = findViewById(R.id.login_button)
//
//
//
//        val mHandler = object : Handler(this.mainLooper) {
//            override fun handleMessage(msg: Message) {
//                if (msg != null) {
//                    when (msg.what) {
//                        ConstantDefine.SAVE_USER_INFORMATION -> {
//                            val jsonString = msg.obj as String
//                            var jsonArray = JSONArray(jsonString)
//                            var jsonObject = jsonArray.getJSONObject(0)
//
//                            var userData = UserDataModel.UserData(
//                                Integer.valueOf(jsonObject.getString("user_id")),
//                                jsonObject.getString("user_email"),
//                                jsonObject.getString("user_name"),
//                                jsonObject.getString("password"),
//                                jsonObject.getString("information"),
//                                jsonObject.getString("photo_server")
//                            )
//
//                            UserDataModel.setSingletonUserData(userData)
//                            UserDataModel.inputUserData(applicationContext, userData)
//                            val myIntent = Intent (this@LoginActivity, HomeActivity::class.java)
//                            startActivity(myIntent)
//                            finish()
//                        }
//                        ConstantDefine.SIGN_IN_USING_SHARED_PREFERENCE -> {
//                            val userData = msg.obj as UserDataModel.UserData
//                            UserDataModel.setSingletonUserData(userData)
//                            val myIntent = Intent (this@LoginActivity, HomeActivity::class.java)
//                            startActivity(myIntent)
//                            finish()
//                        }
//
//                        ConstantDefine.LOGIN_FAILED -> {
//                            val builder = AlertDialog.Builder(this@LoginActivity)
//                            builder.setTitle("Login Failed")
//                            builder.setMessage("You may have entered wrong email (use email not username) or password")
//
//                            builder.setPositiveButton("Got it!") { dialog, which ->
//                                //donothing
//                            }
//                            builder.show()
//                        }
//                    }
//                }
//            }
//        }
//
//        if(UserDataModel.isUserDataSaved(applicationContext)){
//            // login sequence
//            val userData = UserDataModel.obtainUserData(applicationContext)
//            val message = mHandler.obtainMessage(ConstantDefine.SIGN_IN_USING_SHARED_PREFERENCE, userData)
//            if (message != null) {
//                mHandler.sendMessage(message)
//            }
//        }
//
//        loginButton?.setOnClickListener {
//            val scope = CoroutineScope(Job() + Dispatchers.IO)
//            scope.launch {
//                try {
//                    //creating a URL
//                    val url = URL("https://vanrrbackend.000webhostapp.com/forum_chat_backend/LogIn.php")
//
//                    val userEmail = userEmailField?.text.toString()
//                    val userPassword = userPasswordField?.text.toString()
//                    val jsonInputString = "{\"user_name\": \"$userEmail\", \"password\": \"$userPassword\"}"
//
//
//                    HTTPRESTClient.getHttpRestClient()?.sendPostRequestUsingJsonForm(url, jsonInputString,
//                        mHandler, ConstantDefine.LOGIN_FAILED, ConstantDefine.SAVE_USER_INFORMATION)
//
//                } catch (e: Exception) {
//                    Log.e("error", "onCreate: " + e.message )
//                }
//
////            val apiService = RestAPIService()
////            val userData = UserDataModel.UserData(
////                null,
////                userEmailField?.text.toString(),
////                null,
////                userPasswordField?.text.toString(),
////                null,
////                null
////            )
////
////            apiService.addUser(userData){
////                var response = it
////            }
//
//
////            val getJson = GetJSON("https://vanrrbackend.000webhostapp.com/forum_chat_backend/LogIn2.php",
////                userEmailField?.text.toString(), userPasswordField?.text.toString())
////            getJson.execute()
//            }
//        }
//    }


}

class GetJSON (url : String, email : String, password : String): AsyncTask<Void, Void, String>() {
    var urlWebService : String? = null
    var userEmail : String? = null
    var userPassword : String? = null
    init {
        urlWebService = url
        userEmail = email
        userPassword = password
    }

    //this method will be called after execution
    //so here we are displaying a toast with the json string
    override fun onPostExecute(s: String) {
        super.onPostExecute(s)
//            returnJson[0] = s
//            val message = mHandler.obtainMessage(SIGN_IN_UPDATE, s)
//            mHandler.sendMessage(message)
        //                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    //in this method we are fetching the json string
    override fun doInBackground(vararg voids: Void): String? {


        try {
            //creating a URL
            val url = URL(urlWebService)

            //Opening the URL using HttpURLConnection
            val conn = url.openConnection() as HttpURLConnection

            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.doInput = true


//            val urlParameters : String = "user_name=$userEmail&password=$userPassword"
            var jsonInputString = "{\"user_name\": \"$userEmail\", \"password\": \"$userPassword\"}"

            conn.outputStream.use { os ->
                val input: ByteArray = jsonInputString.toByteArray(StandardCharsets.UTF_8)
                os.write(input, 0, input.size)
            }

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

            //finally returning the read string
            return sb.toString().trim { it <= ' ' }
        } catch (e: Exception) {
            return null
        }

    }
}