package info.passdaily.camrelconvertapp.typeofuser.parent.gallery.image

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import coil.load
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.lib.photoview.HackyViewPager
import info.passdaily.camrelconvertapp.lib.photoview.PhotoView
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global.Companion.albumImageList
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.CreateObjectiveExam


@SuppressLint("ValidFragment")
class SlideshowDialogFragment : DialogFragment() {
    var TAG = "SlideshowDialogFragment"
    var viewPager: HackyViewPager? = null
    var myViewPagerAdapter: MyViewPagerAdapter? = null
    var countText: TextView? = null
    private var selectedPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image_slider, container, false)
        viewPager = view.findViewById(R.id.viewpager) as HackyViewPager
        countText = view.findViewById(R.id.countText) as TextView

        selectedPosition = requireArguments().getInt("position")

        var imageViewClose = view.findViewById(R.id.imageViewClose) as ImageView

        imageViewClose.setOnClickListener {
            cancelFrg()
        }


        myViewPagerAdapter = MyViewPagerAdapter(requireActivity(),albumImageList)
        viewPager!!.adapter = myViewPagerAdapter!!
        viewPager!!.addOnPageChangeListener(viewPagerPageChangeListener)
        setCurrentItem(selectedPosition)
        return view
    }

    private fun setCurrentItem(position: Int) {
        viewPager!!.setCurrentItem(position, false)
        displayMetaInfo(selectedPosition)
    }

    private fun cancelFrg() {
        val prev = requireActivity().supportFragmentManager.findFragmentByTag(TAG)
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
        }
    }

    //  page change listener
    private var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            displayMetaInfo(position)
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    @SuppressLint("SetTextI18n")
    private fun displayMetaInfo(position: Int) {
        countText!!.text =
            (position + 1).toString() + " of " + albumImageList.size
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleWhite)
        //Theme_Black_NoTitleBar_Fullscreen
    }

    //  adapter
    inner class MyViewPagerAdapter(
        var context : Context,
        var albumImageList: ArrayList<CustomImageModel>
    ) : PagerAdapter() {
        private var layoutInflater: LayoutInflater? = null
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = LayoutInflater.from(context)
            val view = layoutInflater!!.inflate(R.layout.image_fullscreen_preview, container, false)

            val imageView : PhotoView = view.findViewById(R.id.imageView)
            val image = albumImageList[position].imageFile
            imageView.load(image)

//            val imageView : ZoomImageView = view.findViewById(R.id.imageView)
//            val image = image_url + "/Image/" + albumImageList[position].aLBUMFILE
////            imageView.debugInfoVisible = true
//            imageView.load(image)
////            imageView.swipeToDismissEnabled = true
//            imageView.currentZoom = 1.5f
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return albumImageList.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj as View
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }

    companion object {
        fun newInstance(): SlideshowDialogFragment {
            return SlideshowDialogFragment()
        }
    }

//    override fun onStop() {
//        super.onStop()
//        albumImageList = ArrayList<CustomImageModel>()
//    }
//

}