package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.update_result

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentManageGroupBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_album.BottomSheetUpdateAlbum
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.CreateObjectiveExam
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject


@Suppress("DEPRECATION")
class UpdateResultFragment : Fragment(), UpdateResultListener {

    var TAG = "UpdateResultFragment"
    private lateinit var resultViewModel: UpdateResultViewModel
    private var _binding: FragmentManageGroupBinding? = null
    private val binding get() = _binding!!
    var aCCADEMICID = 0
    var cLASSID = 0

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()

    lateinit var bottomSheetSuccess : BottomSheetSuccess

    private lateinit var localDBHelper: LocalDBHelper
    var adminId = 0
    var schoolId = 0

    var count = 0
    var getResultList = ArrayList<ResultListModel.Result>()

    var selectedValues = ArrayList<Int>()

    lateinit var mAdapter: ResultAdapter
    var constraintLayoutContent: ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo: RecyclerView? = null

    var spinnerAcademic: AppCompatSpinner? = null
    var spinnerClass: AppCompatSpinner? = null
    var toolBarClickListener: ToolBarClickListener? = null
    var pb: ProgressDialog? = null

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
        toolBarClickListener?.setToolbarName("Update Result")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        resultViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[UpdateResultViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentManageGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pb = ProgressDialog(requireActivity())
        pb?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pb?.setMessage("Result Publishing")
        pb?.isIndeterminate = true
        pb?.setCancelable(false)

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

        binding.accedemicText.text = requireActivity().resources.getText(R.string.select_year)
        binding.classText.text = requireActivity().resources.getText(R.string.select_class)

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
                getFinalList(aCCADEMICID, cLASSID)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.fab.visibility = View.VISIBLE
        binding.buttonSubmit.visibility = View.GONE

        binding.fab.setOnClickListener {
            //progressStart()
       //     pb?.show()
            Utils.getSnackBarGreen(requireActivity(), "Result Uploading....", constraintLayoutContent!!)
            Log.i(TAG,"getResultList ${getResultList.size}")
            Log.i(TAG,"count $count")
            for(i in getResultList.indices){
                if(getResultList[i].isChecked){
                    jsonUpdation(i)
                }
//
            }
//            progressStop()
        }


//        initFunction(groupTypeStr,typeStr)
        initFunction()
        bottomSheetSuccess = BottomSheetSuccess()
    }

    private fun jsonUpdation(position : Int){



        Log.i(TAG,"isChecked ${getResultList[position].sTUDENTID}")
        val url = "AnnualResult/AnnualResultUpdate"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("ACCADEMIC_ID", getResultList[position].aCCADEMICID)
            jsonObject.put("CLASS_ID", getResultList[position].cLASSID)
            jsonObject.put("STUDENT_ID", getResultList[position].sTUDENTID)
            jsonObject.put("STUDENT_ROLL_NUMBER", getResultList[position].sTUDENTROLLNUMBER)
            jsonObject.put("RESULT_STATUS", getResultList[position].rESULTSTATUS)
            jsonObject.put("RESULT_STATUS_NAME", getResultList[position].rESULTSTRING)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")
        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        resultViewModel.getCommonPostFun(url,accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            Log.i(TAG,"response $response")
                            when {
                                Utils.resultUpFun(response) == "SUCCESS" -> {
                                    if(isAdded) {
                                        Utils.getSnackBarGreen(
                                            requireActivity(),
                                            "Update Result for Roll.No : ${getResultList[position].sTUDENTROLLNUMBER}",
                                            constraintLayoutContent!!
                                        )
                                    }

                                    //                            SystemClock.sleep(1000)
                                    if(position + 1 == count){
                                        Log.i(TAG,"stop ${position + 1}")

                                        bottomSheetSuccess = BottomSheetSuccess(this,"Result Add Successfully")
                                        bottomSheetSuccess.show(requireActivity().supportFragmentManager, "TAG")

                                        //     pb?.dismiss()
                                    }
                                }
                                else -> {
                                    if(isAdded) {
                                        Utils.getSnackBar4K(
                                            requireActivity(),
                                            "Result failed for Roll.No : ${getResultList[position].sTUDENTROLLNUMBER}",
                                            constraintLayoutContent!!
                                        )
                                    }
                                }
                            }




                        }
                        Status.ERROR -> {
                            pb?.dismiss()
                            if(isAdded) {
                                Utils.getSnackBar4K(
                                    requireActivity(),
                                    "Please try again after sometime",
                                    constraintLayoutContent!!
                                )
                            }
                        }
                        Status.LOADING -> {
//                            progressStart()
                            Log.i(CreateObjectiveExam.TAG,"loading")
                        }
                    }
                }
            })






    }

    private fun initFunction() {
        resultViewModel.getYearClassExam(adminId, schoolId)
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
                            Log.i(TAG, "initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "initFunction LOADING")
                        }
                    }
                }
            })
    }

    fun getFinalList(aCCADEMICID: Int, cLASSID: Int) {
        resultViewModel.getUpdateResultList(aCCADEMICID, cLASSID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getResultList = response.result as ArrayList<ResultListModel.Result>
                            if (getResultList.isNotEmpty()) {
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    mAdapter = ResultAdapter(
                                        this,
                                        getResultList,
                                        requireActivity()
                                    )
                                    recyclerViewVideo!!.adapter = mAdapter
                                }
                            } else {
                                recyclerViewVideo?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text = resources.getString(R.string.no_results)
                            }
                            Log.i(TAG, "getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewVideo?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text = resources.getString(R.string.no_internet)
                            Log.i(TAG, "getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            recyclerViewVideo?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getResultList = ArrayList<ResultListModel.Result>()
                            Glide.with(this)
                                .load(R.drawable.ic_empty_progress_report)
                                .into(imageViewEmpty!!)

                            textEmpty?.text = resources.getString(R.string.loading)
                            Log.i(TAG, "getSubjectList LOADING")
                        }
                    }
                }
            })
    }


    class ResultAdapter(
        var updateResultListener: UpdateResultListener,
        var getResultList: ArrayList<ResultListModel.Result>,
        var context: Context
    ) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

        var mylist = ArrayList<ResultListModel.ResultCheck>()

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textName: TextView = view.findViewById(R.id.textName)
            var textRollNo: TextView = view.findViewById(R.id.textRollNo)
            var textViewClass: TextView = view.findViewById(R.id.textViewClass)
            var textViewYear: TextView = view.findViewById(R.id.textViewYear)

            var radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)
            var passButton: RadioButton = view.findViewById(R.id.passButton)
            var failButton: RadioButton = view.findViewById(R.id.failButton)
            var noResultButton: RadioButton = view.findViewById(R.id.noResultButton)


        }

        private var mRecyclerView: RecyclerView? = null


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.update_result_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textName.text = getResultList[position].sTUDENTFNAME

            holder.textViewClass.text = "Roll.No : ${getResultList[position].sTUDENTROLLNUMBER}"

            holder.textRollNo.text = "Class : ${getResultList[position].cLASSNAME}"

            holder.textViewYear.text = "Year : ${getResultList[position].aCCADEMICTIME}"

            when (getResultList[position].rESULTSTATUS) {
                1 -> {
                    holder.passButton.isChecked = true
                    holder.failButton.isChecked = false
                    holder.noResultButton.isChecked = false
                }
                0 -> {
                    holder.passButton.isChecked = false
                    holder.failButton.isChecked = true
                    holder.noResultButton.isChecked = false
                }
                3 -> {
                    holder.passButton.isChecked = false
                    holder.failButton.isChecked = false
                    holder.noResultButton.isChecked = true
                }
            }

            holder.radioGroup.setOnCheckedChangeListener { group, isChecked ->

                val selectedId = group.checkedRadioButtonId
                val radioButton = group.findViewById(selectedId) as RadioButton
                var radioButtonId: Int = group.indexOfChild(radioButton)


//                //val radioButtonId = group.findViewById<View>(group.checkedRadioButtonId)
//
//                Log.i("TAG", "radioButtonId $radioButtonId")
//
//                Log.i("TAG", "isChecked $isChecked")
//
//
//                for(i in  mylist.indices){
////                    Log.i("ResultChecked","mylist i ${mylist[i]}")
//                    if(getResultList[position].sTUDENTID == mylist[i].sTUDENTID){
//                        val result = ResultListModel.ResultChecked(
//                            getResultList[position].sTUDENTID,
//                            getResultList[position].sTUDENTROLLNUMBER,
//                            radioButtonId,
//                            isChecked
//                        )
//                        mylist[i] = result
//                       // mylist.removeAt(i)
//                    }
//                }
//
//                val result = ResultListModel.ResultChecked(
//                    getResultList[position].sTUDENTID,
//                    getResultList[position].sTUDENTROLLNUMBER,
//                    radioButtonId,
//                    isChecked
//                )
//                mylist.add(result)
//
                updateResultListener.onFinalClicker(radioButtonId, position,true)
//
            }


        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }


        override fun getItemCount(): Int {
            return getResultList.size
        }

    }

    override fun onFinalClicker(radioButtonId: Int, position: Int, isChecked: Boolean) {

        when (radioButtonId) {
            0 -> {
                getResultList[position].rESULTSTATUS = 1
                getResultList[position].rESULTSTRING = "Pass"
                getResultList[position].isChecked = isChecked
                count++
            }
            1 -> {
                getResultList[position].rESULTSTATUS = 0
                getResultList[position].rESULTSTRING = "Fails"
                getResultList[position].isChecked = isChecked
                count++
            }
            2 -> {
                getResultList[position].rESULTSTATUS = 3
                getResultList[position].rESULTSTRING = "No Result"
                getResultList[position].isChecked = isChecked
                count++
            }
        }


        Log.i(TAG, "getResultList $getResultList")
//        mAdapter.notifyItemChanged(position)
    }

    override fun onFinishListener(message: String) {
        bottomSheetSuccess.dismiss()
    }

    private fun progressStart() {
        val dialog1 = ProgressBarDialog()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.isCancelable = false
        dialog1.show(transaction, ProgressBarDialog.TAG)
    }

    fun progressStop() {
        val fragment: ProgressBarDialog? =
            requireActivity().supportFragmentManager.findFragmentByTag(ProgressBarDialog.TAG) as ProgressBarDialog?
        if (fragment != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
    }


}

interface UpdateResultListener {

    fun onFinalClicker(radioButtonId: Int, position: Int, isChecked: Boolean)

    fun onFinishListener(message : String)
}
