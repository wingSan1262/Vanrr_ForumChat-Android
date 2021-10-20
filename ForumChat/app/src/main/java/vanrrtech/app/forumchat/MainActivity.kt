package vanrrtech.app.forumchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {
    var mHandler : Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mHandler = Handler(Looper.getMainLooper())


        mHandler?.postDelayed({

            if(UserDataModel.isUserDataSaved(this.applicationContext)){
                UserDataModel.setSingletonUserData(UserDataModel.obtainUserData(applicationContext))
                val myIntent = Intent (this, HomeActivity::class.java)
                startActivity(myIntent)
                finish()
            } else {
                val mIntent = Intent (this, LoginSignUpActivity::class.java)
                startActivity(mIntent)
                finish()
            }

        },2000)
    }
}