package vanrrtech.app.forumchat.ui.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import vanrrtech.app.forumchat.ConstantDefine
import vanrrtech.app.forumchat.HTTPRESTClient
import vanrrtech.app.forumchat.databinding.FragmentSignUpBinding
import java.io.ByteArrayOutputStream
import java.net.URL
import java.net.URLEncoder


/**
 * A placeholder fragment containing a simple view.
 */
class SignUpFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentSignUpBinding? = null
    private var uriImageHolder : Uri? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg != null) {
                when (msg.what) {
                    ConstantDefine.CREATE_ACCOUNT -> {
                        _binding?.createAccountText?.setText("Account created! Check your email for verification first before using this app")
                        val loadingHandler = Handler(Looper.getMainLooper())
                        loadingHandler?.postDelayed({
                            _binding?.createLoading?.visibility = View.GONE
                            _binding?.createAccountText?.setText("Creating Account")
                        },5000)
                    }

                    ConstantDefine.CREATE_ACCOUNT_FAIL -> {
                        _binding?.createAccountText?.setText("Fail to create account")
                        val loadingHandler = Handler(Looper.getMainLooper())
                        loadingHandler?.postDelayed({
                            _binding?.createLoading?.visibility = View.GONE
                            _binding?.createAccountText?.setText("Creating Account")
                        },5000)
                    }
                }
            }
        }
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            val myUri = data?.data
            val myImageView  = _binding?.profileImage
            uriImageHolder = myUri
            Picasso.with(this.context).load(uriImageHolder).into(myImageView)
        }
    }

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

        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val root = binding.root

        _binding?.editProfileImage?.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            resultLauncher.launch(intent)
        }

        _binding?.createAccount?.setOnClickListener {
            requestCreateAccount()
        }

        _binding?.passwordToggle?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            // do something, the isChecked will be
            // true if the switch is in the On position
            if(isChecked == true){
                _binding?.passwordField?.inputType = InputType.TYPE_CLASS_TEXT
            } else {
                _binding?.passwordField?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        })

//        val textView: TextView = binding.textView7
//        pageViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }

    fun requestCreateAccount(){

        if(_binding?.emailField?.text.toString().equals("")
            || _binding?.passwordField?.text.toString().equals("")
            || _binding?.userNameField?.text.toString().equals("")
            || _binding?.selfDescriptionField?.text.toString().equals("")){
            return
        }

        _binding?.createLoading?.visibility = View.VISIBLE
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            try {
                var myImageString64:String? = null
                if(uriImageHolder != null){
                    var myBitmap : Bitmap? = null
                    if(Build.VERSION.SDK_INT > 27) {
                        val source = ImageDecoder.createSource(requireContext().contentResolver, uriImageHolder!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        myBitmap = bitmap
                    } else{
                        myBitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uriImageHolder)
                    }
                    val myByteArrayOutputStream = ByteArrayOutputStream()
                    myBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, myByteArrayOutputStream)
                    var myByte = myByteArrayOutputStream.toByteArray()
                    val base64Endoce = Base64.encodeToString(myByte, Base64.DEFAULT)
                    myImageString64 = URLEncoder.encode(base64Endoce, "utf-8")
                } else {
                    myImageString64 = null
                }

//                uploadImageOnly(myImageString64!!)


                val stringParam = "email=${_binding?.emailField?.text.toString()}&" +
                        "password=${_binding?.passwordField?.text.toString()}&" +
                        "user_name=${_binding?.userNameField?.text.toString()}&" +
                        "information=${_binding?.selfDescriptionField?.text.toString()}&" +
                        "image_base64=${myImageString64}"
                //creating a URL
                val url = URL("https://${HTTPRESTClient.theDomain}/forum_chat_backend/RegisterAccount.php")


                HTTPRESTClient.getHttpRestClient()?.sendPostRequest(url, stringParam,
                    mHandler!!, ConstantDefine.CREATE_ACCOUNT_FAIL, ConstantDefine.CREATE_ACCOUNT)
            } catch (e: Exception) {
                Log.e("error", "onCreate: " + e.message )
            }
        }
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
        fun newInstance(sectionNumber: Int): SignUpFragment {
            return SignUpFragment().apply {
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
}