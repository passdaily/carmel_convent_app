package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityConveyorBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper

@Suppress("DEPRECATION")
class StaffActivity : AppCompatActivity() {
    var TAG = "StaffActivity"
    private lateinit var binding: ActivityConveyorBinding
    private lateinit var staffHomeViewModel: StaffHomeViewModel

    private lateinit var localDBHelper: LocalDBHelper
    var adminId = 0
    var schoolId = 0
    var toolbar: Toolbar? = null

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()

    var aCCADEMICID = 0
    var cLASSID = 0

    var spinnerAcademic: AppCompatSpinner? = null
    var spinnerClass: AppCompatSpinner? = null

    var constraintLayoutContent: ConstraintLayout? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var getStaffList = ArrayList<StaffListModel.Staff>()

    var recyclerViewVideo: RecyclerView? = null

    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    lateinit var mAdapter : ConveyorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localDBHelper = LocalDBHelper(this)
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID
        // Inflate the layout for this fragment
        staffHomeViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[StaffHomeViewModel::class.java]

        binding = ActivityConveyorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "Staff"
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            //   toolbar.setTitle("About-Us");
            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
                onBackPressed()
            })
        }

        constraintLayoutContent = binding.constraintLayoutContent
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        Glide.with(this)
            .load(R.drawable.ic_empty_progress_report)
            .into(imageViewEmpty!!)
        shimmerViewContainer = binding.shimmerViewContainer

        spinnerAcademic = binding.spinnerAcademic
//        spinnerClass = binding.spinnerClass

        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(this)

        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                aCCADEMICID = getYears[position].aCCADEMICID
                getConveyorList()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }


        initFunction()

        Utils.setStatusBarColor(this)
        
    }

    private fun initFunction() {
        staffHomeViewModel.getYearClassExam(adminId, schoolId )
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getYears = response.years as ArrayList<GetYearClassExamModel.Year>
                            var years = Array(getYears.size) { "" }
                            for (i in getYears.indices) {
                                years[i] = getYears[i].aCCADEMICTIME
                            }
                            if (spinnerAcademic != null) {
                                val adapter = ArrayAdapter(
                                    this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    years
                                )
                                spinnerAcademic?.adapter = adapter
                            }

                            Log.i(TAG, "initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "initFunction LOADING")
                        }
                    }
                }
            })
    }

    private fun getConveyorList() {
        staffHomeViewModel.getStaffListStaff(aCCADEMICID,schoolId)
            .observe(this, Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getStaffList = response.staffList as ArrayList<StaffListModel.Staff>
                            if(getStaffList.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                mAdapter = ConveyorAdapter(
                                    this,
                                    getStaffList,
                                    TAG
                                )
                                recyclerViewVideo!!.adapter = mAdapter

                            }else{
                                recyclerViewVideo?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_state_pta)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text =  resources.getString(R.string.no_results)
                            }

                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewVideo?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text =  resources.getString(R.string.no_internet)
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            recyclerViewVideo?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getStaffList = ArrayList<StaffListModel.Staff>()
                            Glide.with(this)
                                .load(R.drawable.ic_empty_state_pta)
                                .into(imageViewEmpty!!)

                            textEmpty?.text =  resources.getString(R.string.loading)
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }

    class ConveyorAdapter(var context: Context,
                          var getStaffList : ArrayList<StaffListModel.Staff>,
                          var TAG: String) : RecyclerView.Adapter<ConveyorAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            var textName: TextView = view.findViewById(R.id.textName)
            var textGuardianName: TextView = view.findViewById(R.id.textGuardianName)
            var textGuardianNumber: TextView = view.findViewById(R.id.textGuardianNumber)
            var imageViewCall : ImageView = view.findViewById(R.id.imageMessage)
            var imageMessage : ImageView = view.findViewById(R.id.imageCall)
        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.student_adapter, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            when (getStaffList[position].sTAFFGENDER) {
                0 -> {
                    holder.textGuardianName.text = "Gender : Male"
                }
                1 -> {
                    holder.textGuardianName.text = "Gender : Female"
                }
                2 -> {
                    holder.textGuardianName.text = "Gender : Other"
                }
            }

            holder.textName.text = getStaffList[position].sTAFFFNAME
            holder.textGuardianNumber.text = "Mobile.No : ${getStaffList[position].sTAFFPHONENUMBER}"

            holder.imageViewCall.setOnClickListener {
                try {
                    var phone = getStaffList[position].sTAFFPHONENUMBER
                    val intent = Intent("android.intent.action.DIAL")
                    intent.data = Uri.parse("tel:$phone")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.i(TAG, "Exception $e")
                }
            }


            holder.imageMessage.setOnClickListener(View.OnClickListener {
                try {
                    var message  = getStaffList[position].sTAFFPHONENUMBER
                    val uri = Uri.parse("smsto:$message")
                    val intent = Intent(Intent.ACTION_SENDTO, uri)
                    intent.putExtra("sms_body", "")
                    context.startActivity(intent)
                } catch (e: java.lang.Exception) {
                    Log.i(TAG, "Exception $e")
                }
            })
        }

        override fun getItemCount(): Int {
            return getStaffList.size
        }

    }

}