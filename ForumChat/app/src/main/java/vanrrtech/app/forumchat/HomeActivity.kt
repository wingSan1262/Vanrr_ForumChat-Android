package vanrrtech.app.forumchat

import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior

class HomeActivity : AppCompatActivity() {
    var logoutTv : TextView? = null

    var fragment : ForumDetails? = null

    var currentFragment : Fragment? = null

    var mToolbar : Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        configureBackdrop()

//        logoutTv = findViewById(R.id.logout)
//        logoutTv?.setOnClickListener {
//            UserDataModel.logoutUserData(applicationContext)
//            var myIntent = Intent (this, LoginActivity::class.java)
//            startActivity(myIntent)
//            finish()
//        }
        setCurrentFragment(ForumListFragment.newInstance(mBottomSheetBehavior, fragment!!))
        var bottomNavBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavBar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    setCurrentFragment(ForumListFragment.newInstance(mBottomSheetBehavior, fragment!!))
                }
                R.id.account -> {
                    setCurrentFragment(AccountFragment.newInstance())
                }

                R.id.add -> {
                    setCurrentFragment(CreateForumChatFragment.newInstance())
                }
            }
            true
        }

//        var iterator = bottomNavBar.allViews.iterator()
//
//        while (iterator.hasNext()){
//            var myView = iterator.next()
//            if (myView.id == R.id.home || myView.id == R.id.add ||
//                myView.id == R.id.search || myView.id == R.id.account){
//                Log.e("Navbar", myView.toString())
//                var myLayoutParam = myView.layoutParams
//                myView.measure(0,0)
//                myLayoutParam.width = myView.measuredHeight - 10
//                myLayoutParam.height = myView.measuredWidth - 10
//                myView.layoutParams = myLayoutParam
//            }
//        }

        mToolbar = findViewById(R.id.toolbar)
        mToolbar?.elevation = 0f;
        mToolbar?.setBackgroundResource(R.color.white)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
//                setCurrentFragment(AccountFragment.newInstance())
            }
            R.id.log_out -> {
            UserDataModel.logoutUserData(applicationContext)
            var myIntent = Intent (this, LoginActivity::class.java)
            startActivity(myIntent)
            finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    @JvmName("setCurrentFragment1")
    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
        currentFragment = fragment
    }

    override fun onResume() {
        super.onResume()
        val mediaPlayer = MediaPlayer.create(this, R.raw.longpop)
        mediaPlayer!!.start()
    }


    private var mBottomSheetBehavior: BottomSheetBehavior<View?>? = null

    public fun getBottomSheet():BottomSheetBehavior<View?>?{
        return mBottomSheetBehavior
    }

    private fun configureBackdrop() {
        // Get the fragment reference
        fragment = supportFragmentManager.findFragmentById(R.id.filter_fragment) as ForumDetails?
        fragment?.let {
            // Get the BottomSheetBehavior from the fragment view
            BottomSheetBehavior.from(it.view as View)?.let { bsb ->
                // Set the initial state of the BottomSheetBehavior to HIDDEN
                bsb.state = BottomSheetBehavior.STATE_HIDDEN

                // Set the reference into class attribute (will be used latter)
                mBottomSheetBehavior = bsb
            }
        }
    }
}
