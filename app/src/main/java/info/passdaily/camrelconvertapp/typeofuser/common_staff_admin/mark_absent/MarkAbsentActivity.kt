package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.mark_absent

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.SpannedString
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentMarkAbsentBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class MarkAbsentActivity : AppCompatActivity(),MarkAbsentListener {


    var TAG = "MarkAbsentActivity"
    private lateinit var markAbsentViewModel: MarkAbsentViewModel
    private lateinit var binding: FragmentMarkAbsentBinding
    
    private lateinit var localDBHelper: LocalDBHelper

    lateinit var bottomSheet : BottomSheetMarkAbsent
    var adminId = 0
    var schoolId = 0
    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<ClassListModel.Class>()
    var getSubject = ArrayList<SubjectsModel.Subject>()

    var getStudentDetails = ArrayList<MarkAbsentModel.StudentDetail>()
    lateinit var mAdapter : MarkAbsentAdapter

    var spinnerAcademic: AppCompatSpinner? = null
    var spinnerClass: AppCompatSpinner? = null
    var editTextDate: TextInputEditText? = null


    var constraintLayoutContent: ConstraintLayout? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo: RecyclerView? = null


    var aCCADEMICID = 0
    var cLASSID = 0
    var fromStr = ""
    var startDate = ""
    var typeStatus = 0

    var className = ""

    var textStartDate : TextInputEditText? = null

    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null

    var cardViewPublish : CardView? = null
    var cardViewUnPublish : CardView? = null

    var textPublish : TextView? = null
    var textUnPublish : TextView? = null

    var  date = ""
    var  dateMMDDYYY = ""
    var  dateText = ""

    var toolbar: Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        localDBHelper = LocalDBHelper(this)
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID
        // Inflate the layout for this fragment
        markAbsentViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[MarkAbsentViewModel::class.java]

        binding = FragmentMarkAbsentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "Take Attendance Single"
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
        spinnerClass = binding.spinnerClass

        textStartDate = binding.textStartDate

        textStartDate?.inputType = InputType.TYPE_NULL
        textStartDate?.keyListener = null
        textStartDate!!.isClickable = true

        cardViewPublish = binding.cardViewPublish
        cardViewUnPublish = binding.cardViewUnPublish

        textPublish = binding.textPublish
        textUnPublish = binding.textUnPublish

        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(this)

        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                aCCADEMICID = getYears[position].aCCADEMICID
                getClassList()
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
                className  = getClass[position].cLASSNAME
                getMarkAbsentStaff()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        textStartDate?.setOnClickListener{
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(textStartDate?.windowToken, 0)
            val mcurrentDate1 = Calendar.getInstance()
            val mYear = mcurrentDate1[Calendar.YEAR]
            val mMonth = mcurrentDate1[Calendar.MONTH]
            val mDay = mcurrentDate1[Calendar.DAY_OF_MONTH]
            val mDatePicker3 = DatePickerDialog(
                this, { _, year, month, dayOfMonth ->
                    val s = month + 1
                    fromStr = "$year-$s-$dayOfMonth"
//                    date ="$year/$s/$dayOfMonth"
                    date = java.lang.String.format("%d/%02d/%02d", year, s, dayOfMonth)
                    textStartDate?.setText(Utils.dateformat(fromStr))
                    getMarkAbsentStaff()
                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("Select Date")
            // mDatePicker3.datePicker.minDate = Date().time
            mDatePicker3.datePicker.maxDate = Date().time
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }

        cardViewPublish?.setOnClickListener {
            cardViewPublish?.setCardBackgroundColor(resources.getColor(R.color.blue_100))
            textPublish?.setTextColor(resources.getColor(R.color.green_400))
            cardViewUnPublish?.setCardBackgroundColor(resources.getColor(R.color.gray_100))
            textUnPublish?.setTextColor(resources.getColor(R.color.gray_400))

            typeStatus = 0
            getMarkAbsentStaff()
        }

        cardViewUnPublish?.setOnClickListener {
            cardViewPublish?.setCardBackgroundColor(resources.getColor(R.color.gray_100))
            textPublish?.setTextColor(resources.getColor(R.color.gray_400))
            cardViewUnPublish?.setCardBackgroundColor(resources.getColor(R.color.blue_100))
            textUnPublish?.setTextColor(resources.getColor(R.color.green_400))
            typeStatus = 1
            getMarkAbsentStaff()
        }




        val fab = binding.fab
        fab.setOnClickListener {
//            val dialog1 = CreateZoomScheduled(this)
//            val transaction = this.supportFragmentManager.beginTransaction()
//            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
//            dialog1.show(transaction, CreateZoomScheduled.TAG)
        }
        date = SimpleDateFormat("yyyy/MM/dd").format(Date())
        fromStr = SimpleDateFormat("yyyy-MM-dd").format(Date())
        dateText = SimpleDateFormat("MMM dd, yyyy").format(Date())

        textStartDate!!.setText(dateText)
        initFunction()

        bottomSheet = BottomSheetMarkAbsent()

        Utils.setStatusBarColor(this)
    }

    private fun initFunction() {
        markAbsentViewModel.getYearClassExam(adminId,schoolId)
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
                            shimmerViewContainer?.visibility = View.GONE
                            Log.i(TAG, "initFunction ERROR")
                        }
                        Status.LOADING -> {
                            shimmerViewContainer?.visibility = View.VISIBLE
                            Log.i(TAG, "initFunction LOADING")
                        }
                    }
                }
            })
    }

    private fun getClassList() {
        markAbsentViewModel.getClassList(adminId)
            .observe(this, Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {

                            val response = resource.data?.body()!!

                            getClass = response.classList as ArrayList<ClassListModel.Class>
                            var classX = Array(getClass.size) { "" }
                            for (i in getClass.indices) {
                                classX[i] = getClass[i].cLASSNAME
                            }
                            if (spinnerClass != null) {
                                val adapter = ArrayAdapter(
                                    this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    classX
                                )
                                spinnerClass?.adapter = adapter
                            }
                            Log.i(TAG, "getClassList SUCCESS")
                        }
                        Status.ERROR -> {
                            shimmerViewContainer?.visibility = View.GONE
                            Log.i(TAG, "getClassList ERROR")
                        }
                        Status.LOADING -> {
                            getClass = ArrayList<ClassListModel.Class>()
                            Log.i(TAG, "getClassList LOADING")
                        }
                    }
                }
            })
    }


    @SuppressLint("SimpleDateFormat")
    private fun getMarkAbsentStaff() {

        Log.i(TAG,"typeStatus $typeStatus")
        Log.i(TAG,"date $date")
        Log.i(TAG,"cLASSID $cLASSID")
        Log.i(TAG,"aCCADEMICID $aCCADEMICID")

        markAbsentViewModel.getMarkAbsentStaff(cLASSID,aCCADEMICID,date,schoolId)
            .observe(this, Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            var getStudentDetail = response.studentDetails as ArrayList<MarkAbsentModel.StudentDetail>

                            if(typeStatus == 0) {
                                for (i in getStudentDetail.indices) {
                                    if (typeStatus == getStudentDetail[i].aBSENTS) {
                                        getStudentDetails.add(getStudentDetail[i])
                                    }
                                }
                            }else{
                                for (i in getStudentDetail.indices) {
                                    if (getStudentDetail[i].sTUDENTROLLNUMBER.toString().toFloat().toInt() == getStudentDetail[i].aBSENTS) {
                                        getStudentDetails.add(getStudentDetail[i])
                                    }
                                }
                            }

                            if(getStudentDetails.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                    mAdapter =   MarkAbsentAdapter(
                                        this,
                                        getStudentDetails,
                                        this@MarkAbsentActivity,
                                        typeStatus,
                                        TAG
                                    )
                                    recyclerViewVideo!!.adapter = mAdapter


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
                            getStudentDetails = ArrayList<MarkAbsentModel.StudentDetail>()
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

    class MarkAbsentAdapter(var markAbsentListener: MarkAbsentListener,
                            var studentDetails: ArrayList<MarkAbsentModel.StudentDetail>,
                            var context: Context, var typeStatus : Int, var TAG: String) : RecyclerView.Adapter<MarkAbsentAdapter.ViewHolder>() {
        var titleName = ""
        var titleMessage : SpannedString? = null
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
            var textViewRollNo: TextView = view.findViewById(R.id.textViewRollNo)
            var imageViewCheck : ImageView = view.findViewById(R.id.imageViewCheck)
        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.mark_absent_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MarkAbsentAdapter.ViewHolder, position: Int) {
            holder.textViewTitle.text = studentDetails[position].sTUDENTFNAME
            when {
                studentDetails[position].sTUDENTROLLNUMBER != null -> {
                    holder.textViewRollNo.text =
                        "Roll.No : ${studentDetails[position].sTUDENTROLLNUMBER.toString().toFloat().toInt()}"
                }
                else -> {
                    holder.textViewRollNo.text = "Roll.No : "
                }
            }


            if (studentDetails[position].selectedValue) {
                // viewHolder.checkBox.setChecked(true);
                holder.imageViewCheck.setImageResource(R.drawable.ic_checked_black)
                if(typeStatus == 1){
                    titleName = "Mark Present"
                    titleMessage = buildSpannedString {
                        append("Mark ")
                        bold {
                            append(studentDetails[position].sTUDENTFNAME)
                        }
                        append(" as Present?")
                    }
                     //   "Mark ${studentDetails[position].sTUDENTFNAME} as present"
                }else{
                    titleName = "Mark Absent"
                    titleMessage = buildSpannedString {
                        append("Mark ")
                        bold {
                            append(studentDetails[position].sTUDENTFNAME)
                        }
                        append(" as Absent?")
                    }
                        //"Mark ${studentDetails[position].sTUDENTFNAME} as Absent"
                }
                markAbsentListener.onDialogClickListener(position,studentDetails[position],titleName,titleMessage!!,typeStatus)
            } else {
                //viewHolder.checkBox.setChecked(false);
                holder.imageViewCheck.setImageResource(R.drawable.ic_check_gray)
            }

            holder.itemView.setOnClickListener {
                studentDetails[position].selectedValue = !studentDetails[position].selectedValue
                notifyItemChanged(position)

            }
        }

        override fun getItemCount(): Int {
            return studentDetails.size
        }


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.mark_absent_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_present) {


            bottomSheet = BottomSheetMarkAbsent(this)
            bottomSheet.show(supportFragmentManager, "TAG")

//            var titleMessage = buildSpannedString {
//                append("Mark Full Present to ")
//                bold {
//                    append(className)
//                }
//                append(" on today ")
//                bold {
//                    append(dateText)
//                }
//                append(" ?")
//            }
//            // AlertDialog builder instance to build the alert dialog
//            val builder = AlertDialog.Builder(this)
//            builder.setMessage(titleMessage)
//                .setCancelable(false)
//                .setPositiveButton("Yes") { dialog, _ ->
//                        dialog.dismiss()
//                        markFullPresent()
//
//                }
//                .setNegativeButton(
//                    "No"
//                ) { dialog1, _ -> //  Action for 'NO' Button
//                    dialog1.cancel()
//                }
//            val alert = builder.create()
//            //Setting the title manually
//            //Setting the title manually
//            alert.setTitle("Full Present")
//            alert.show()
//            val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
//            buttonbackground.setTextColor(Color.BLACK)
//            val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
//            buttonbackground1.setTextColor(Color.BLACK)
            return true
        }
        return super.onOptionsItemSelected(item)
    }



    /* access modifiers changed from: protected */
    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")
    }

    /* access modifiers changed from: protected */
    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
    }

    /* access modifiers changed from: protected */
    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

    override fun onFullPresentListener(strDate: String, strDateFormatDDMMMYYYY: String) {


        var titleMessage = buildSpannedString {
            append("Mark Full Present to class ")
            bold {
                append(className)
            }
            append(" on ")
            bold {
                append(strDateFormatDDMMMYYYY)
            }
            append(" ?")
        }
        // AlertDialog builder instance to build the alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setMessage(titleMessage)
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                bottomSheet.dismiss()
                markFullPresent(strDate)
                Log.i(TAG,"strDate $strDate")
            }
            .setNegativeButton(
                "No"
            ) { dialog1, _ -> //  Action for 'NO' Button
                dialog1.cancel()
            }
        val alert = builder.create()

        //Setting the title manually
        alert.setTitle("Full Present")
        alert.show()
        val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonbackground.setTextColor(Color.BLACK)
        val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonbackground1.setTextColor(Color.BLACK)
    }

    override fun onDialogClickListener(
        position: Int,
        studentDetails: MarkAbsentModel.StudentDetail,
        titleName: String,
        titleMessage: SpannedString,
        typeStatus: Int
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(titleMessage)
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                if(typeStatus == 1){
                    markPresentStaff(studentDetails.aTTENDANCEID,studentDetails.sTUDENTID)
                }else {
                    markAbsentFun(studentDetails)
                }
            }
            .setNegativeButton(
                "No"
            ) { dialog, _ -> //  Action for 'NO' Button
                dialog.cancel()
                getStudentDetails[position].selectedValue = false
                mAdapter.notifyItemChanged(position)
            }
        //Creating dialog box
        val alert = builder.create()
        //Setting the title manually
        alert.setTitle(titleName)
        alert.show()
        val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonbackground.setTextColor(Color.BLACK)
        val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonbackground1.setTextColor(Color.BLACK)
        Log.i(TAG,"studentDetails $studentDetails")
    }

    override fun onDialogMarkAbsentListener(
        studentDetails: ArrayList<MarkAbsentModel.StudentDetail>,
        mylist: java.util.ArrayList<Int>
    ) {
        Log.i(TAG,"studentDetails $studentDetails")
    }

    override fun onChangesListener(studentRollNumber: Int, studentDetails: ArrayList<MarkAbsentModel.StudentDetail>) {
        TODO("Not yet implemented")
    }

    override fun onAfterChangesListener(studentDetails: ArrayList<MarkAbsentModel.StudentDetail>) {
        TODO("Not yet implemented")
    }


    private fun markPresentStaff(aTTENDANCEID: Int, sTUDENTID: Int) {

        markAbsentViewModel.getMarkPresentStaff(aTTENDANCEID,sTUDENTID)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "DELETED" -> {
                                    Utils.getSnackBarGreen(this, "Successfully Marked Present", constraintLayoutContent!!)
                                    getMarkAbsentStaff()
                                }
                                Utils.resultFun(response) == "FAILED" -> {
                                    Utils.getSnackBar4K(this, "Marking Present Failed", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Utils.getSnackBar4K(this, "Please try again after sometime", constraintLayoutContent!!)
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG,"loading")
                        }
                    }
                }
            })
    }

    private fun markAbsentFun(studentDetails: MarkAbsentModel.StudentDetail) {

        var url = "Details/MarkAbsent"
        var sTUDENTROLLNUMBER = when {
            studentDetails.sTUDENTROLLNUMBER != null -> {
                studentDetails.sTUDENTROLLNUMBER.toString().toFloat().toInt() }
            else -> { 0 }
        }
        val jsonObject = JSONObject()
        try {
            jsonObject.put("STUDENT_ROLLNO", sTUDENTROLLNUMBER)
            jsonObject.put("CLASS_ID", cLASSID)
            jsonObject.put("ACCADEMIC_ID", aCCADEMICID)
            jsonObject.put("ATTENDANCE_MARKEDBY", adminId)
            jsonObject.put("ABSENT_DATE", date)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val academicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        markAbsentViewModel.getCommonPostFun(url,academicRe)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            Log.i(TAG,"response $response")
                            progressStop()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    Utils.getSnackBarGreen(this, "Absent Marked", constraintLayoutContent!!)
                                    getMarkAbsentStaff()
                                }
                                Utils.resultFun(response) == "UPDATED" -> {
                                    Utils.getSnackBar4K(this, "Already Absent Marked", constraintLayoutContent!!)
                                }
                                Utils.resultFun(response) == "NOT_EXIST" -> {
                                    Utils.getSnackBar4K(this, "Student not Found", constraintLayoutContent!!)
                                }
                                Utils.resultFun(response) == "FAILED" -> {
                                    Utils.getSnackBar4K(this, "Marking Absent failed", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Utils.getSnackBar4K(this, "Please try again after sometime", constraintLayoutContent!!)
                            Log.i(TAG,"ERROR")
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG,"loading")
                        }
                    }
                }
            })
    }


    private fun markFullPresent(strDate: String) {
        markAbsentViewModel.getMarkFullPresent(aCCADEMICID,cLASSID,"$strDate 00:00:00",adminId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            Log.i(TAG,"response $response")
                            when {
                                Utils.resultFun(response) == "1" -> {
                                    Utils.getSnackBarGreen(this, "Successfully Full Present Marked", constraintLayoutContent!!)
                                }
                                Utils.resultFun(response) == "-1" -> {
                                    Utils.getSnackBar4K(this, "Already Full Present Marked", constraintLayoutContent!!)
                                }
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBar4K(this, "Marking Full Present Failed", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"ERROR")
                            progressStop()
                            Utils.getSnackBar4K(this, "Please try again after sometime", constraintLayoutContent!!)
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG,"loading")
                        }
                    }
                }
            })
    }

    private fun progressStart() {
        val dialog1 = ProgressBarDialog()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.isCancelable = false
        dialog1.show(transaction, ProgressBarDialog.TAG)
    }

    fun progressStop() {
        val fragment: ProgressBarDialog? =
            supportFragmentManager.findFragmentByTag(ProgressBarDialog.TAG) as ProgressBarDialog?
        if (fragment != null) {
            supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
    }

}

interface MarkAbsentListener {
//    fun onCreateClick(message: String)


    fun onFullPresentListener(strDate: String, strDateFormatDDMMMYYYY: String)

    fun onDialogClickListener(
        position: Int,
        studentDetails: MarkAbsentModel.StudentDetail,
        titleName: String,
        titleMessage: SpannedString,
        typeStatus: Int
    )

    fun onDialogMarkAbsentListener(
        studentDetails: ArrayList<MarkAbsentModel.StudentDetail>,
        mylist: java.util.ArrayList<Int>,
    )

    fun onChangesListener(studentRollNumber: Int, studentDetails: ArrayList<MarkAbsentModel.StudentDetail>)
    fun onAfterChangesListener(studentDetails: ArrayList<MarkAbsentModel.StudentDetail>)

//    fun onDeleteClickListener(url: String, zMeetingId: Int)
}

