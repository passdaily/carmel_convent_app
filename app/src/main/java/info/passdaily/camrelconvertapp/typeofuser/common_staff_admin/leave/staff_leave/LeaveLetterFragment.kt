package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.leave.staff_leave

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentLeaveLetterBinding
import info.passdaily.camrelconvertapp.model.GetYearClassExamModel
import info.passdaily.camrelconvertapp.model.LeaveStaffListModel
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.leave.LeaveViewModel
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class LeaveLetterFragment : Fragment(),StaffDetailsListener {


    var TAG = "LeaveFragment"
    private lateinit var leaveViewModel: LeaveViewModel
    private var _binding: FragmentLeaveLetterBinding? = null
    private val binding get() = _binding!!

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0
    var adminRole = 0

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo : RecyclerView? = null
    var spinnerAcademic: AppCompatSpinner? = null
    var leaveFromDate : TextInputEditText? =null
    var leaveEndDate : TextInputEditText? =null

    var isWorking= false
    var getStaffLeaveLetter = ArrayList<LeaveStaffListModel.Leave>()

    var fromStr = ""
    var toStr = ""

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var aCCADEMICID = 0

    lateinit var mAdapter : StaffAdapter

    var fab : FloatingActionButton? = null
    var toolBarClickListener : ToolBarClickListener? = null
    var mContext : Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(mContext ==null){
            mContext = context.applicationContext
        }
        try {
            toolBarClickListener = context as ToolBarClickListener
        }catch(e : Exception){
            Log.i(TAG,"Exception $e")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        adminRole = user[0].ADMIN_ROLE
        schoolId = user[0].SCHOOL_ID

        Global.screenState = "staffhomepage"
        toolBarClickListener?.setToolbarName("Manage Staff Leave")

        leaveViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[LeaveViewModel::class.java]

        _binding = FragmentLeaveLetterBinding.inflate(inflater, container, false)
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

        textEmpty?.text =  resources.getString(R.string.no_results)


        spinnerAcademic = binding.spinnerAcademic

        leaveFromDate = binding.leaveFromDate
        leaveEndDate = binding.leaveEndDate

        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                aCCADEMICID = getYears[position].aCCADEMICID
                staffLeaveLetterList()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        //        et_leave_fromdate.requestFocus();
        leaveFromDate?.setOnClickListener{
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(leaveFromDate?.windowToken, 0)
            val mcurrentDate1 = Calendar.getInstance()
            val mYear = mcurrentDate1[Calendar.YEAR]
            val mMonth = mcurrentDate1[Calendar.MONTH]
            val mDay = mcurrentDate1[Calendar.DAY_OF_MONTH]
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    fromStr = "$year-$s-$dayOfMonth"
                    if(toStr.isNotEmpty()) {
                        if (Utils.checkDatesAfter(fromStr, toStr)) {
                            leaveFromDate?.setText(Utils.dateformat(fromStr))
                            staffLeaveLetterList()
                        }else{
                            Toast.makeText(activity, "Give Valid Date", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        leaveFromDate?.setText(Utils.dateformat(fromStr))
                    }


                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("From Date")
           // mDatePicker3.datePicker.minDate = Date().time
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

            mDatePicker3.setButton(
                DialogInterface.BUTTON_NEGATIVE, "Cancel"
            ) { dialog, which ->
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    // do something
                    leaveFromDate?.setText("")
                    staffLeaveLetterList()
                    dialog.dismiss()
                }
            }
        }


        leaveEndDate?.setOnClickListener{
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(leaveEndDate?.windowToken, 0)

            val mcurrentDate1 = Calendar.getInstance()
            val mYear = mcurrentDate1[Calendar.YEAR]
            val mMonth = mcurrentDate1[Calendar.MONTH]
            val mDay = mcurrentDate1[Calendar.DAY_OF_MONTH]
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    toStr = "$year-$s-$dayOfMonth"
                    if(fromStr.isNotEmpty()) {
                        if (Utils.checkDatesAfter(fromStr, toStr)) {
                            leaveEndDate?.setText(Utils.dateformat(toStr))
                            staffLeaveLetterList()
                        }else{
                            Toast.makeText(activity, "Give Valid Date", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(activity, "Give Start Date", Toast.LENGTH_SHORT).show()
                    }
                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("To Date")
    //        mDatePicker3.datePicker.minDate = Date().time
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

            mDatePicker3.setButton(
                DialogInterface.BUTTON_NEGATIVE, "Cancel"
            ) { dialog, which ->
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    // do something
                    leaveEndDate?.setText("")
                    staffLeaveLetterList()
                    dialog.dismiss()
                }
            }
//                "CANCEL"
//            ) { dialog: DialogInterface?, which: Int ->
//                spinnerClass?.text = "Click to Select Class"
//                Arrays.fill(
//                    checkedItems,
//                    false
//                )
//            }

        }

        binding.editTextMobile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(getStaffLeaveLetter.size != 0){
                    mAdapter.getFilter().filter(s)
                }
            }
        })


        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())


        fab = binding.fab
        fab?.visibility = View.VISIBLE
        fab?.setOnClickListener {
            val dialog1 = CreateStaffLeaveDialog(this)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, CreateStaffLeaveDialog.TAG)
        }


        initFunction()
    }


    private fun initFunction() {
        leaveViewModel.getYearClassExam(adminId,schoolId)
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

                            Log.i(TAG, "initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "initFunction ERROR")
                        }
                        Status.LOADING -> {
                          //  getStaffLeaveLetter = ArrayList<LeaveStaffListModel.Leave>()
                            Log.i(TAG, "initFunction LOADING")
                        }
                    }
                }
            })
    }



    fun staffLeaveLetterList(){
        var leaveStart = ""
        if(leaveFromDate!!.text.toString().isNotEmpty()){
            leaveStart = Utils.parseDateToDDMMYYYY("MMM dd, yyyy","MM/dd/yyyy", leaveFromDate!!.text.toString())!!
        }
        var leaveEnd = ""
        if(leaveEndDate!!.text.toString().isNotEmpty()) {
            leaveEnd = Utils.parseDateToDDMMYYYY("MMM dd, yyyy", "MM/dd/yyyy", leaveEndDate!!.text.toString())!!
        }

        val jsonObject = JSONObject()
        try {
            //   "ACCADEMIC_ID": 10,
            //    "ADMIN_ID": 1,
            //    "LEAVE_FROM_DATE": "05/15/2023",
            //    "LEAVE_TO_DATE": "09/30/2023",
            //    "SCHOOL_ID": 1
            jsonObject.put("ACCADEMIC_ID", aCCADEMICID)////current Accedemic
            jsonObject.put("ADMIN_ID", adminId)
            jsonObject.put("LEAVE_FROM_DATE", leaveStart)//"SCHOOL_ID":1
            jsonObject.put("LEAVE_TO_DATE", leaveEnd)//"SCHOOL_ID":1
            jsonObject.put("SCHOOL_ID", schoolId)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")

        val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())


        leaveViewModel.getStaffLeaveLetterList(submitItems)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            isWorking = true
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getStaffLeaveLetter = response.leaveList as ArrayList<LeaveStaffListModel.Leave>
                            if(getStaffLeaveLetter.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {

                                    mAdapter = StaffAdapter(
                                       this,
                                        getStaffLeaveLetter,
                                        getStaffLeaveLetter,
                                        requireActivity(), TAG
                                    )
                                    recyclerViewVideo!!.adapter = mAdapter
//                                        StaffAdapter(
//                                            this,
//                                            getStaffList,
//                                            getStaffList,
//                                            requireActivity(),TAG)
                                }
                            }else{
                                recyclerViewVideo?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
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
                            getStaffLeaveLetter = ArrayList<LeaveStaffListModel.Leave>()
                            Glide.with(this)
                                .load(R.drawable.ic_empty_progress_report)
                                .into(imageViewEmpty!!)

                            textEmpty?.text =  resources.getString(R.string.loading)
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }


    class StaffAdapter(
        var staffDetailsListener: StaffDetailsListener,
        var staffListModel: ArrayList<LeaveStaffListModel.Leave>,
        var staffList: ArrayList<LeaveStaffListModel.Leave>,
        var context: Context, var TAG : String)
        : RecyclerView.Adapter<StaffAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewStaff: TextView = view.findViewById(R.id.textViewStaff)
            var textViewEmail: TextView = view.findViewById(R.id.textViewEmail)
            var textMobileNumber: TextView = view.findViewById(R.id.textMobileNumber)
            var textViewCode : TextView = view.findViewById(R.id.textViewCode)
            var textUpdated : TextView = view.findViewById(R.id.textUpdated)
            var shapeImageView : ShapeableImageView = view.findViewById(R.id.shapeImageView)
            var textViewStatus : TextView = view.findViewById(R.id.textViewStatus)
            var imageViewMore : ImageView = view.findViewById(R.id.imageViewMore)
            var textViewApproveDate : TextView = view.findViewById(R.id.textViewApproveDate)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.staff_leave_list_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewStaff.text =  staffListModel[position].sTAFFFNAME
            holder.textViewCode.text =  staffListModel[position].sTAFFPHONENUMBER

            holder.textMobileNumber.text =  staffListModel[position].lEAVESUBJECT

           holder.textViewEmail.text =  staffListModel[position].lEAVEDESCRIPTION

            if(!staffListModel[position].lEAVESUBMITTEDDATE.isNullOrBlank()) {
                val date: Array<String> =
                    staffListModel[position].lEAVESUBMITTEDDATE.split("T".toRegex()).toTypedArray()
                //    val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
//                holder.textStartDate.text = "Start Date : ${Utils.formattedDateWords(dddd)}"
//                holder.textStartTime.text = "${Utils.formattedTime(dddd)}"
                holder.textViewApproveDate.text = "Applied On : ${Utils.dateformat(date[0])}"
            }

            var query = staffListModel[position].lEAVESTATUS
            when (query) {
                0 -> {
                    holder.textViewStatus.text = "Waiting for Reply"
                    holder.textViewStatus.setTextColor(context.resources.getColor(R.color.blue_400))
                }
                1 -> {
                    holder.textViewStatus.text = "Approved"
                    holder.textViewStatus.setTextColor(context.resources.getColor(R.color.fresh_green_200))
                }
                3 -> {
                    holder.textViewStatus.text = "Rejected"
                    holder.textViewStatus.setTextColor(context.resources.getColor(R.color.fresh_red_200))
                }
            }

            if (staffListModel[position].sTAFFIMAGE != "") {
                Glide.with(context).load(
                    Global.event_url + "/Photos/StaffImage/" + staffListModel[position].sTAFFIMAGE
                ) //STAFF_IMAGE -> http://demo.passdaily.in/Photos/StaffImageA0D181192F902C6AE338BEDF36FC3251.jpg
                    //STAFF_IMAGE -> 1A07304FC14301B29E49B4DA301B0EA5.png
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate()
                            .placeholder(R.drawable.round_account_button_with_user_inside)
                    )
                    .thumbnail(0.5f)
                    .into(holder.shapeImageView)
            }
            holder.shapeImageView.setOnClickListener {
                staffDetailsListener.onViewProfile(staffListModel[position].sTAFFIMAGE)
            }

//            holder.itemView.setOnClickListener {
//                staffDetailsListener.onLeaveDetailsListener(staffListModel[position])
//            }

            holder.imageViewMore.setOnClickListener(View.OnClickListener {
                val popupMenu = PopupMenu(context, holder.imageViewMore)
                popupMenu.inflate(R.menu.leave_menu)
                popupMenu.menu.findItem(R.id.menu_view).icon = context.resources.getDrawable(R.drawable.ic_icon_about_gray)
                popupMenu.menu.findItem(R.id.menu_edit).icon = context.resources.getDrawable(R.drawable.ic_icon_edit)
                popupMenu.menu.findItem(R.id.menu_report).icon = context.resources.getDrawable(R.drawable.ic_icon_about_gray)
                popupMenu.menu.findItem(R.id.menu_video).icon = context.resources.getDrawable(R.drawable.ic_icon_close)

                if(staffListModel[position].lEAVESTATUS == 0) {

                    when (staffListModel[position].lEAVEOPERATION) {
                        "DOCREDADMIN" -> {
                            popupMenu.menu.findItem(R.id.menu_view).isVisible =
                                false  ///only Leave letter view
                            popupMenu.menu.findItem(R.id.menu_edit).isVisible =
                                true  ///Leave letter edit
                            popupMenu.menu.findItem(R.id.menu_report).isVisible =
                                true ////Approval or Reject
                            popupMenu.menu.findItem(R.id.menu_video).isVisible =
                                true ////Delete operation
                        }
                        "STOPCREDADMIN" -> {
                            popupMenu.menu.findItem(R.id.menu_view).isVisible =
                                false  ///only Leave letter view
                            popupMenu.menu.findItem(R.id.menu_edit).isVisible =
                                false  ///Leave letter edit
                            popupMenu.menu.findItem(R.id.menu_report).isVisible =
                                true ////Approval or Reject
                            popupMenu.menu.findItem(R.id.menu_video).isVisible =
                                false ////Delete operation
                        }
                        "DOCREDSTAFF" -> {
                            popupMenu.menu.findItem(R.id.menu_view).isVisible =
                                false  ///only Leave letter view
                            popupMenu.menu.findItem(R.id.menu_edit).isVisible =
                                true  ///Leave letter edit
                            popupMenu.menu.findItem(R.id.menu_report).isVisible =
                                false ////Approval or Reject
                            popupMenu.menu.findItem(R.id.menu_video).isVisible =
                                true ////Delete operation
                        }
                    }
                }else{
                    popupMenu.menu.findItem(R.id.menu_view).isVisible =
                        true  ///only Leave letter view
                    popupMenu.menu.findItem(R.id.menu_edit).isVisible =
                        false  ///Leave letter edit
                    popupMenu.menu.findItem(R.id.menu_report).isVisible =
                        false ////Approval or Reject
                    popupMenu.menu.findItem(R.id.menu_video).isVisible =
                        false ////Delete operation
                }

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit -> {
                            staffDetailsListener.onLeaveEditListener(staffListModel[position])
                            true
                        }
                        R.id.menu_view -> {
                            staffDetailsListener.onLeaveDetailsListener(staffListModel[position])
                            true
                        }
                        R.id.menu_report -> {
                            staffDetailsListener.onLeaveDetailsListener(staffListModel[position])
                            true
                        }
                        R.id.menu_video -> {
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("Are you sure want to delete Leave?")
                                .setCancelable(false)
                                .setPositiveButton("Yes") { _, _ ->
                                    //Staff/LeaveRequestDelete?LeaveId=100
                                    staffDetailsListener.onDeleteClickListener("Staff/LeaveRequestDelete",staffListModel[position].lEAVEID)
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


//            Log.i(TAG,"update ${staffListModel[position].updated}")
//            Log.i(TAG,"sTAFFFNAME ${staffListModel[position].sTAFFFNAME}")
//            holder.textUpdated.isVisible = false
//            if(staffListModel[position].updated){
//                holder.textUpdated.isVisible = true
//            }
        }

        override fun getItemCount(): Int {
            return staffListModel.size
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        fun getFilter(): Filter {
            return object : Filter() {
                /* access modifiers changed from: protected */
                override fun performFiltering(charSequence: CharSequence): FilterResults {
                    val charSequence2 = charSequence.toString()
                    Log.i(TAG, "charString $charSequence2")
                    staffListModel = staffList
                    if (charSequence2.isEmpty()) {
                        //  getSalesDetailsModel = getSalesDetails
                    } else {
                        val arrayList: ArrayList<LeaveStaffListModel.Leave> = ArrayList()
                        for (unAttended in staffListModel) {
                            //  Log.i(TAG, "row $unAttended")

                            if (unAttended.sTAFFPHONENUMBER.contains(charSequence2)
                                 || unAttended.lEAVESTATUSTEXT.lowercase().contains(charSequence2.lowercase())
                                 || unAttended.sTAFFFNAME.lowercase().contains(charSequence2.lowercase())
                             //   || unAttended.lEAVETODATE.lowercase().contains(charSequence2.lowercase())
                             //   || unAttended.lEAVESUBJECT.lowercase().contains(charSequence2.lowercase())
                            ) {
                                arrayList.add(unAttended)
                            }
                        }
                        staffListModel = arrayList
                    }
                    val filterResults = FilterResults()
                    filterResults.values = staffListModel
                    Log.i(TAG, "filterResults $filterResults")
                    return filterResults
                }

                /* access modifiers changed from: protected */
                @SuppressLint("NotifyDataSetChanged")
                override fun publishResults(
                    charSequence: CharSequence,
                    filterResults: FilterResults
                ) {
                    staffListModel = filterResults.values as ArrayList<LeaveStaffListModel.Leave>
                    notifyDataSetChanged()
                }
            }
        }

    }

    override fun onCreateClick(message: String) {
        Log.i(TAG,"onCreateClick")
        staffLeaveLetterList()
        Utils.getSnackBarGreen(requireActivity(),message,constraintLayoutContent!!)
    }

    override fun onViewProfile(sTAFFIMAGE: String) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_profile_view)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window!!.attributes = lp

        var imageViewProfile = dialog.findViewById<ImageView>(R.id.imageViewProfile)
        Glide.with(requireActivity())
            .load(Global.staff_image_url+sTAFFIMAGE)
            .into(imageViewProfile)

        dialog.show()
    }


    override fun onLeaveEditListener(leaveList: LeaveStaffListModel.Leave) {
        val dialog1 = UpdateStaffLeaveDialog(this, leaveList)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.show(transaction, UpdateStaffLeaveDialog.TAG)
    }

    override fun onLeaveDetailsListener(leaveList: LeaveStaffListModel.Leave) {
        val dialog1 = LeaveDetailsDialog(this,leaveList)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.show(transaction, LeaveDetailsDialog.TAG)
    }

    override fun onDeleteClickListener(url: String, lEAVEID: Int) {
        val paramsMap: HashMap<String?, Int> = HashMap()
        paramsMap["LeaveId"] = lEAVEID
        leaveViewModel.getCommonGetFuntion(url,paramsMap)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when {
                                Utils.resultFun(response) == "DELETED" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Leave Details Deleted Successfully", constraintLayoutContent!!)
                                    initFunction()
                                }
                                Utils.resultFun(response) == "DELETION FAILED" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Leave Deletion Failed", constraintLayoutContent!!)
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

}

interface StaffDetailsListener{
    fun onCreateClick(message: String)
    fun onViewProfile(sTAFFIMAGE: String)
    fun onLeaveEditListener(leaveList: LeaveStaffListModel.Leave)
    fun onLeaveDetailsListener(leaveList: LeaveStaffListModel.Leave)
    fun onDeleteClickListener(url: String, lEAVEID: Int)
}