package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_scheduled

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentZoomScheduleBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class ZoomScheduledFragment : Fragment(), ZoomScheduledListener {

    var TAG = "ObjectiveExamStaffFragment"
    private lateinit var zoomScheduledViewModel: ZoomScheduledViewModel
    private var _binding: FragmentZoomScheduleBinding? = null
    private val binding get() = _binding!!


    var toolBarClickListener: ToolBarClickListener? = null

    private lateinit var localDBHelper: LocalDBHelper
    var adminId = 0
    var schoolId =0
    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getSubject = ArrayList<SubjectsModel.Subject>()

    var getZoomList = ArrayList<ZoomScheduleStaffModel.Zoom>()

    var spinnerAcademic: AppCompatSpinner? = null
    var spinnerClass: AppCompatSpinner? = null
    var editTextDate: TextInputEditText? = null


    var constraintLayoutContent: ConstraintLayout? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo: RecyclerView? = null

    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null


        var aCCADEMICID = 0
        var cLASSID = 0
        var fromStr = ""
        var startDate = ""
        var typeStatus = 1


    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null

    var cardViewPublish : CardView? = null
    var cardViewUnPublish : CardView? = null

    var textPublish : TextView? = null
    var textUnPublish : TextView? = null


    var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext
        }
        try {
            toolBarClickListener = context as ToolBarClickListener
        } catch (e: Exception) {
            Log.i(TAG, "Exception $e")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.screenState = "staffhomepage"
        toolBarClickListener?.setToolbarName("Zoom Scheduled List")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID
        // Inflate the layout for this fragment
        zoomScheduledViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ZoomScheduledViewModel::class.java]

        _binding = FragmentZoomScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        constraintLayoutContent = binding.constraintLayoutContent
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        Glide.with(this)
            .load(R.drawable.ic_empty_progress_report)
            .into(imageViewEmpty!!)
        shimmerViewContainer = binding.shimmerViewContainer

        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass

        editTextDate = binding.editTextDate
        editTextDate?.inputType = InputType.TYPE_NULL
        editTextDate?.keyListener = null

         cardViewPublish = binding.cardViewPublish
         cardViewUnPublish = binding.cardViewUnPublish

        textPublish = binding.textPublish
        textUnPublish = binding.textUnPublish

        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())

        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                aCCADEMICID = getYears[position].aCCADEMICID
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        spinnerClass?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                cLASSID = getClass[position].cLASSID
                getZoomSchedultList()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        editTextDate?.setOnClickListener {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editTextDate?.windowToken, 0)
            val mcurrentDate1 = Calendar.getInstance()
            val mYear = mcurrentDate1[Calendar.YEAR]
            val mMonth = mcurrentDate1[Calendar.MONTH]
            val mDay = mcurrentDate1[Calendar.DAY_OF_MONTH]
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    fromStr = "$year-$s-$dayOfMonth"
                    startDate = "$s/$dayOfMonth/$year"
                    editTextDate?.setText(Utils.dateformat(fromStr))
                    getZoomSchedultList()
                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("Start Date")
//            mDatePicker3.datePicker.minDate = Date().time
            mDatePicker3.show()
            mDatePicker3.setButton(DialogInterface.BUTTON_NEGATIVE, "Clear Date") { _, _ ->
                editTextDate?.setText("")
                getZoomSchedultList()
            }
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }



        cardViewPublish?.setOnClickListener {
            cardViewPublish?.setCardBackgroundColor(resources.getColor(R.color.blue_100))
            textPublish?.setTextColor(resources.getColor(R.color.green_400))
            cardViewUnPublish?.setCardBackgroundColor(resources.getColor(R.color.gray_100))
            textUnPublish?.setTextColor(resources.getColor(R.color.gray_400))

            typeStatus = 1
            getZoomSchedultList()
        }

        cardViewUnPublish?.setOnClickListener {
            cardViewPublish?.setCardBackgroundColor(resources.getColor(R.color.gray_100))
            textPublish?.setTextColor(resources.getColor(R.color.gray_400))
            cardViewUnPublish?.setCardBackgroundColor(resources.getColor(R.color.blue_100))
            textUnPublish?.setTextColor(resources.getColor(R.color.green_400))
            typeStatus = 0
            getZoomSchedultList()
        }


        val fab = binding.fab
        fab.setOnClickListener {
            val dialog1 = CreateZoomScheduled(this)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, CreateZoomScheduled.TAG)
        }

        initFunction()


    }


    private fun initFunction() {
        zoomScheduledViewModel.getYearClassExam(adminId, schoolId )
            .observe(requireActivity(), Observer {
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
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    years
                                )
                                spinnerAcademic?.adapter = adapter
                            }

                            getClass = response.classList as ArrayList<GetYearClassExamModel.Class>
                            var classX = Array(getClass.size) { "" }
                            for (i in getClass.indices) {
                                classX[i] = getClass[i].cLASSNAME
                            }
                            if (spinnerClass != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    classX
                                )
                                spinnerClass?.adapter = adapter
                            }
                            Log.i(TAG, "initFunction SUCCESS")
                        }
                        Status.ERROR -> {
//                            constraintEmpty?.visibility = View.VISIBLE
//                            if(isAdded) {
//                                Glide.with(this)
//                                    .load(R.drawable.ic_empty_progress_report)
//                                    .into(imageViewEmpty!!)
//
//                                textEmpty?.text = resources.getString(R.string.no_internet)
//                            }
                            Log.i(TAG, "initFunction ERROR")
                        }
                        Status.LOADING -> {
//                            constraintEmpty?.visibility = View.VISIBLE
//                            getObjectiveExam = ArrayList<ObjExamListStaffModel.OnlineExam>()
//                            Glide.with(this)
//                                .load(R.drawable.ic_empty_progress_report)
//                                .into(imageViewEmpty!!)
//
//                            textEmpty?.text =  resources.getString(R.string.loading)
                            Log.i(TAG, "initFunction LOADING")
                        }
                    }
                }
            })
    }



    override fun onCreateClick(message: String) {
        Log.i(TAG,"onCreateClick")
        getZoomSchedultList()
        Utils.getSnackBarGreen(requireActivity(),message,constraintLayoutContent!!)
    }

    override fun onUpdateClickListener(zoomList: ZoomScheduleStaffModel.Zoom) {
        val dialog1 = UpdateZoomScheduled(this,zoomList)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.show(transaction, UpdateZoomScheduled.TAG)
    }

    override fun onReportClickListener(zoomList: ZoomScheduleStaffModel.Zoom) {
        val dialog1 = ZoomScheduleReportDialog(this,zoomList)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.show(transaction, ZoomScheduleReportDialog.TAG)
    }

    override fun onDeleteClickListener(url: String, zMeetingId: Int) {
        val paramsMap: HashMap<String?, Int> = HashMap()
        paramsMap["ZmeetingId"] = zMeetingId
        zoomScheduledViewModel.getCommonGetFuntion(url,paramsMap)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when {
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Deleted Successfully", constraintLayoutContent!!)
                                    initFunction()
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Deletion Failed", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLayoutContent!!)
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"loading")
                        }
                    }
                }
            })
    }


    private fun getZoomSchedultList() {

        var url = "OnlineVideo/ZoomList"

        val jsonObject = JSONObject()
        try {
            jsonObject.put("ACCADEMIC_ID", aCCADEMICID)
            jsonObject.put("CLASS_ID", cLASSID)
            jsonObject.put("MEETING_TITLE", "")
            jsonObject.put("MEETING_STATR_DATE", fromStr)
            jsonObject.put("MEETING_STATUS", typeStatus)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Log.i(TAG, "jsonObject $jsonObject")
        val academicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        zoomScheduledViewModel.getZoomScheduledStaff(url,academicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getZoomList= response.zoomList as ArrayList<ZoomScheduleStaffModel.Zoom>
                            if(getZoomList.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewVideo!!.adapter =
                                        ZoomScheduledAdapter(
                                            this,
                                            getZoomList,
                                            requireActivity(),
                                            TAG
                                        )
                                }
                            }else{
                                recyclerViewVideo?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text =  resources.getString(R.string.no_results)
                            }
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewVideo?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text =  resources.getString(R.string.no_internet)
                        }
                        Status.LOADING -> {
                            recyclerViewVideo?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getZoomList = ArrayList<ZoomScheduleStaffModel.Zoom>()
                            Glide.with(this)
                                .load(R.drawable.ic_empty_progress_report)
                                .into(imageViewEmpty!!)

                            textEmpty?.text =  resources.getString(R.string.loading)
                            Log.i(TAG,"loading")
                        }
                    }
                }
            })
    }

    class ZoomScheduledAdapter(var zoomScheduledListener: ZoomScheduledListener, var zoomList:
    ArrayList<ZoomScheduleStaffModel.Zoom>, var context: Context, var TAG: String)
        : RecyclerView.Adapter<ZoomScheduledAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
            var textMeetingId: TextView = view.findViewById(R.id.textMeetingId)
            var textMeetingPassword : TextView = view.findViewById(R.id.textMeetingPassword)
            var textViewDate : TextView = view.findViewById(R.id.textViewDate)
            var textViewTime  : TextView = view.findViewById(R.id.textViewTime)
            var textViewLink: TextView = view.findViewById(R.id.textViewLink)
            var imageViewMore : ImageView = view.findViewById(R.id.imageViewMore)

            var imageViewBg  : ImageView = view.findViewById(R.id.imageViewBg)
            var textViewStatus  : TextView = view.findViewById(R.id.textViewStatus)

        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.zoom_scheduled_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {


            when (zoomList[position].mEETINGSTATUS) {
                0 -> {
                    Glide.with(context)
                        .load(context.resources.getDrawable(R.drawable.ic_rejected))
                        .into(holder.imageViewBg)

                    holder.textViewStatus.text = "Unpublished"
                    holder.textViewStatus.setTextColor(context.resources.getColor(R.color.fresh_red_200))
                }
                1 -> {
                    Glide.with(context)
                        .load(context.resources.getDrawable(R.drawable.ic_approved))
                        .into(holder.imageViewBg)

                    holder.textViewStatus.text = "Published"
                    holder.textViewStatus.setTextColor(context.resources.getColor(R.color.fresh_green_200))
                }
            }

            holder.textViewTitle.text = zoomList[position].mEETINGTITLE
            holder.textMeetingId.text = zoomList[position].mEETINGID
            holder.textMeetingPassword.text = zoomList[position].mEETINGPASSWORD

            if(!zoomList[position].mEETINGSTATRDATE.isNullOrBlank()) {
                val date: Array<String> =
                    zoomList[position].mEETINGSTATRDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
                holder.textViewDate.text = Utils.formattedDate(dddd)
                holder.textViewTime.text = Utils.formattedTime(dddd)
            }

            holder.textViewLink.text =  zoomList[position].mEETINGLINK

            holder.imageViewMore.setOnClickListener(View.OnClickListener {
                val popupMenu = PopupMenu(context, holder.imageViewMore)
                popupMenu.inflate(R.menu.video_adapter_menu)
                popupMenu.menu.findItem(R.id.menu_edit).icon = context.resources.getDrawable(R.drawable.ic_icon_edit)
                popupMenu.menu.findItem(R.id.menu_report).icon = context.resources.getDrawable(R.drawable.ic_icon_about_gray)
                popupMenu.menu.findItem(R.id.menu_video).icon = context.resources.getDrawable(R.drawable.ic_icon_close)
                popupMenu.menu.findItem(R.id.menu_offline_video).icon = context.resources.getDrawable(R.drawable.ic_icon_delete_gray)
                popupMenu.menu.findItem(R.id.menu_download).icon = context.resources.getDrawable(R.drawable.ic_icon_download)
                popupMenu.menu.findItem(R.id.menu_open).icon = context.resources.getDrawable(R.drawable.ic_icon_download)
                popupMenu.menu.findItem(R.id.menu_open).isVisible = false
                popupMenu.menu.findItem(R.id.menu_download).isVisible = false
                popupMenu.menu.findItem(R.id.menu_offline_video).isVisible = false
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit -> {
                            zoomScheduledListener.onUpdateClickListener(zoomList[position])
                            true
                        }
                        R.id.menu_report -> {
                            zoomScheduledListener.onReportClickListener(zoomList[position])
                            true
                        }
                        R.id.menu_video -> {
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("Are you sure want to delete zoom scheduled?")
                                .setCancelable(false)
                                .setPositiveButton("Yes") { _, _ ->
                                    zoomScheduledListener.onDeleteClickListener("OnlineVideo/ZoomDrop",zoomList[position].zMEETINGID)
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
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            })


        }

        override fun getItemCount(): Int {
           return zoomList.size
        }

    }



    override fun onDestroy() {
        super.onDestroy()
        toolBarClickListener = null
        mContext = null
        Log.i(TAG,"onDestroy")
    }

}

interface ZoomScheduledListener {
    fun onCreateClick(message: String)

    fun onUpdateClickListener(zoomList: ZoomScheduleStaffModel.Zoom)

    fun onReportClickListener(zoomList: ZoomScheduleStaffModel.Zoom)

    fun onDeleteClickListener(url: String, zMeetingId: Int)
}