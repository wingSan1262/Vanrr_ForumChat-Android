package vanrrtech.app.forumchat

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var drawer: DrawerLayout? = null
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

        mToolbar = findViewById(R.id.toolbar)
        mToolbar?.elevation = 0f;
        mToolbar?.setBackgroundResource(R.color.white)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        drawer = findViewById(R.id.drawer_layout)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.itemIconTintList = null
        navigationView.setNavigationItemSelectedListener(this)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
//                setCurrentFragment(AccountFragment.newInstance())
                if(drawer?.isDrawerOpen(GravityCompat.START) == false){
                    drawer!!.openDrawer(GravityCompat.START)
                }
                Picasso.with(this).load(UserDataModel.mUserInformation?.userPhoto)
                    .into(findViewById<ImageView>(R.id.user_image))
                findViewById<TextView>(R.id.user_name_side_bar).text = UserDataModel.mUserInformation?.userName
                findViewById<TextView>(R.id.email_side_bar).text = UserDataModel.mUserInformation?.userEmail
            }
            R.id.log_out -> {
                onLogout()
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

    fun onLogout(){
        UserDataModel.logoutUserData(applicationContext)
        var myIntent = Intent (this, LoginActivity::class.java)
        startActivity(myIntent)
        finish()
    }
    override fun onBackPressed() {
        if(drawer?.isDrawerOpen(GravityCompat.START) == true){
            drawer!!.closeDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.log_out_button -> onLogout()
        }
        drawer?.closeDrawer(GravityCompat.START)
        return true
    }
}
