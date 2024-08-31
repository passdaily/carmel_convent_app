package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.online_video

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityOfflineStoreBinding
import info.passdaily.camrelconvertapp.lib.ExoPlayerActivity
import info.passdaily.camrelconvertapp.lib.video.Video_Activity
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.localDB.parent.OfflineStoreDbHelper
import info.passdaily.camrelconvertapp.typeofuser.parent.MainActivityParent
import java.io.File

class OfflineStoreStaffActivity : AppCompatActivity(),OfflineClickListner {
    var TAG = "OfflineStoreActivity"
    var toolbar: Toolbar? = null
    private lateinit var binding: ActivityOfflineStoreBinding
    var arraylist = ArrayList<OfflineStoreDbHelper.OfflineModel>()
    var recyclerViewOffline : RecyclerView? = null
    var constraintEmpty : ConstraintLayout? = null
    var mainClass: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline_store)

        binding = ActivityOfflineStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "Offline Videos"
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            //   toolbar.setTitle("About-Us");
            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
                onBackPressed()
            })
        }

        constraintEmpty = binding.constraintEmpty
        var imageViewEmpty = binding.imageViewEmpty
        var textEmpty = binding.textEmpty
        Glide.with(this)
            .load(R.drawable.ic_empty_state_video)
            .into(imageViewEmpty)
        textEmpty.text = getString(R.string.no_offline_video)

        val offlineStoreDbHelper = OfflineStoreDbHelper(this)
        arraylist = offlineStoreDbHelper.viewOfflineStore()

        recyclerViewOffline = binding.recyclerViewOffline
        recyclerViewOffline?.layoutManager = LinearLayoutManager(this)

        onViewClick()

        recyclerViewOffline?.adapter = OfflineVideoListAdapter(this,
            arraylist, this@OfflineStoreStaffActivity, TAG)


        mainClass = 1
        Utils.setStatusBarColor(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    class OfflineVideoListAdapter(var offlineClickListner: OfflineClickListner,
                                  var  youtubeList: ArrayList<OfflineStoreDbHelper.OfflineModel>,
                                  var context: Context, TAG: String) : RecyclerView.Adapter<OfflineVideoListAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewSubject: TextView = view.findViewById(R.id.textViewSubject)
            var textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
            var textViewDesc: TextView = view.findViewById(R.id.textViewDesc)
            var textViewChapter: TextView = view.findViewById(R.id.textViewChapter)
            var textViewClass: TextView = view.findViewById(R.id.textViewClass)
            var cardViewStatus : CardView = view.findViewById(R.id.cardViewStatus)
            var imageSubject : ImageView = view.findViewById(R.id.imageView)
            var imageViewMore :ImageView = view.findViewById(R.id.imageViewMore)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.offline_store_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            when (youtubeList[position].SUBJECT_ICON) {
                "English.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_english)
                        .into(holder.imageSubject)
                }
                "Chemistry.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_chemistry)
                        .into(holder.imageSubject)
                }
                "Biology.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_biology)
                        .into(holder.imageSubject)
                }
                "Maths.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_maths)
                        .into(holder.imageSubject)
                }
                "Hindi.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_hindi)
                        .into(holder.imageSubject)
                }
                "Physics.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_physics)
                        .into(holder.imageSubject)
                }
                "Malayalam.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_malayalam)
                        .into(holder.imageSubject)
                }
                "Arabic.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_arabic)
                        .into(holder.imageSubject)
                }
                "Accountancy.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_accountancy)
                        .into(holder.imageSubject)
                }
                "Social.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_social)
                        .into(holder.imageSubject)
                }
                "Economics.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_economics)
                        .into(holder.imageSubject)
                }
                "BasicScience.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_biology)
                        .into(holder.imageSubject)
                }
                "Computer.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_computer)
                        .into(holder.imageSubject)
                }
                "General.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_computer)
                        .into(holder.imageSubject)
                }
            }
            holder.cardViewStatus.setCardBackgroundColor(Color.parseColor("#FFFFFF"));

            holder.textViewSubject.text = youtubeList[position].SUBJECT_NAME
            holder.textViewTitle.text = youtubeList[position].CHAPTER_NAME
            holder.textViewDesc.text = "Title : ${youtubeList[position].VIDEO_TITLE}"
            holder.textViewChapter.text = youtubeList[position].VIDEO_DESCRIPTION
            holder.textViewClass.text = youtubeList[position].STUDENT_NAME



            holder.cardViewStatus.setOnClickListener {
                if (!Global.blockStatus) {
                    val intent = Intent(context, ExoPlayerActivity::class.java)
                    intent.putExtra("ALBUM_TITLE", "")
                    intent.putExtra(
                        "ALBUM_FILE", Utils.getRootDirPath(context) + "/catch/data/staff/encryp/pics/"
                                + youtubeList[position].ORIGINAL_FILE
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent)
                }
            }

            holder.imageViewMore.setOnClickListener {

                val builder = AlertDialog.Builder(context)
                builder.setMessage("Are You sure want to Delete?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { _, _ ->

                        val file = File(Utils.getRootDirPath(context) + "/catch/data/encryp/pics/"
                                + youtubeList[position].ORIGINAL_FILE)
                        if (file.exists()) {
                            val delete = file.delete()
                        }
                        val repo1 = OfflineStoreDbHelper(context)
//                        val productList1: ArrayList<OfflineStoreDbHelper.OfflineModel> = repo1.viewOfflineVideo()
                        repo1.deleteOfflineItem(youtubeList[position].YOUTUBE_ID)
                        // updateList(arraylist);
                        youtubeList.removeAt(position)
                        notifyDataSetChanged()
                        offlineClickListner.onViewClick()
                    }
                    .setNegativeButton(
                        "No"
                    ) { dialog, _ -> //  Action for 'NO' Button
                        dialog.cancel()
                    }


                //Creating dialog box
                val alert = builder.create()
                //Setting the title manually
                alert.setTitle("Delete")
                alert.show()
                val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                buttonbackground.setTextColor(Color.BLACK)
                val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                buttonbackground1.setTextColor(Color.BLACK)
            }
        }

        override fun getItemCount(): Int {
            return  youtubeList.size
        }

    }

    override fun onViewClick() {
        if(arraylist.isNotEmpty()){
            constraintEmpty?.visibility = View.GONE
            recyclerViewOffline?.visibility = View.VISIBLE
        }else{
            constraintEmpty?.visibility = View.VISIBLE
            recyclerViewOffline?.visibility = View.GONE
        }
    }


    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")
        //  player.pause();
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
    }


    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
        // player.start();
        //  play=true;
        // player.setAutoPlay(play);
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }
}

interface OfflineClickListner {
    fun onViewClick()
}