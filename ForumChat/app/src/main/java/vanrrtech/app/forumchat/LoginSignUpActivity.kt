package vanrrtech.app.forumchat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import vanrrtech.app.forumchat.databinding.ActivityLoginSignUpActivityBinding
import vanrrtech.app.forumchat.ui.main.SectionsPagerAdapter

class LoginSignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginSignUpActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginSignUpActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
    }
}