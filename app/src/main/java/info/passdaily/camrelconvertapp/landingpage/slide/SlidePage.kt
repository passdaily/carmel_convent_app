package info.passdaily.camrelconvertapp.landingpage.slide

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.landingpage.firstpage.FirstScreenActivity
import info.passdaily.camrelconvertapp.services.Utils

class SlidePage : AppCompatActivity() {

    var TAG= "SlidePage"
    var springDotsIndicator: DotsIndicator? = null
    var viewPager: ViewPager? = null

    var extendedFab : ExtendedFloatingActionButton?= null
    var scrPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slide_page)

        extendedFab = findViewById(R.id.extendedFab)
        springDotsIndicator = findViewById(R.id.dotsIndicator)
        viewPager = findViewById(R.id.recyclerView)

        val adapter = MyPagerAdapter(supportFragmentManager)
        adapter.addFragment(Slide1Fragment(), "")
        adapter.addFragment(Slide2Fragment(), "")
        adapter.addFragment(Slide3Fragment(), "")

        // adapter.addFragment(new DMKOfficial(), "Tweets");
        viewPager?.adapter = adapter
        viewPager?.currentItem = 0
        springDotsIndicator?.setViewPager(viewPager!!)
        extendedFab?.isEnabled = true

        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                scrPosition = position
                if(position == 0 || position == 1){
                    extendedFab?.setBackgroundColor(resources.getColor(R.color.gray_400));//gray_400
                }else {
                    extendedFab?.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark));
                }

            }

        })
        extendedFab?.setOnClickListener {
            when (scrPosition) {
                0 -> {
                    viewPager?.setCurrentItem(scrPosition+1, true);
                }
                1 -> {
                    viewPager?.setCurrentItem(scrPosition+1, true);
                }
                else -> {
                    startActivity(Intent(this, FirstScreenActivity::class.java))
                    finish()
                }
            }

        }
        Utils.setStatusBarColorZoom(this)



    }

//    private fun setStatusBarColor() {
//        if (Build.VERSION.SDK_INT >= 21) {
//            val window = window
//            //  window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            window.statusBarColor = Color.parseColor("#D5D8DC")
//        }
//    }

    @Suppress("DEPRECATION")
    class MyPagerAdapter(fragmentManager: FragmentManager?) :
        FragmentPagerAdapter(fragmentManager!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentTitleList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getItemPosition(`object`: Any): Int {
            // POSITION_NONE makes it possible to reload the PagerAdapter
            return POSITION_NONE
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }
}