package vanrrtech.app.forumchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior

class HomeActivity : AppCompatActivity() {
    var logoutTv : TextView? = null

    var fragment : ForumDetails? = null

    var currentFragment : Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        configureBackdrop()

        logoutTv = findViewById(R.id.logout)
        logoutTv?.setOnClickListener {
            UserDataModel.logoutUserData(applicationContext)
            var myIntent = Intent (this, LoginActivity::class.java)
            startActivity(myIntent)
            finish()
        }
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
    }

    @JvmName("setCurrentFragment1")
    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
        currentFragment = fragment
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
