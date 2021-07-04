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
            val mIntent = Intent (this, LoginActivity::class.java)
            startActivity(mIntent)
            finish()
        },2000)
    }
}