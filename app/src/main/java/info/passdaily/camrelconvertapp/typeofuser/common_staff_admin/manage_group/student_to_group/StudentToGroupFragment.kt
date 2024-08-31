package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.student_to_group

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
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.GroupViewModel
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class StudentToGroupFragment : Fragment(),StudentListener {

    var TAG = "StudentToGroup"
    private lateinit var groupViewModel: GroupViewModel
    private var _binding: FragmentManageGroupBinding? = null
    private val binding get() = _binding!!
    var aCCADEMICID = 0
    var cLASSID = 0

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()


    var type = arrayOf("Select Status","Unpublished", "Publish")
    var groupTypeStr ="-1"
    var typeStr ="-1"
    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0
    lateinit var bottomSheet : BottomSheetSendStudent

    var getStudentList = ArrayList<StudentListModel.Parent>()
    var getTempStudentDetails = ArrayList<StudentListModel.Parent>()

    var selectedValues = ArrayList<Int>()
    var listCount = 0

    lateinit var mAdapter :  StudentAdapter
    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo : RecyclerView? = null

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null
    var toolBarClickListener : ToolBarClickListener? = null
    var pb: ProgressDialog? = null

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
        Global.screenState = "staffhomepage"
        toolBarClickListener?.setToolbarName("Student To Group")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        groupViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[GroupViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentManageGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pb = ProgressDialog(requireActivity())
        pb?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pb?.setMessage("Loading...")
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
                view: View, position: Int, id: Long) {
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
                view: View, position: Int, id: Long) {
                cLASSID = getClass[position].cLASSID
              getFinalList(aCCADEMICID,cLASSID)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.fab.visibility = View.GONE

        binding.buttonSubmit.visibility = View.GONE
        binding.buttonSubmit.text = resources.getString(R.string.group_students)
        binding.buttonSubmit.setOnClickListener {
            Log.i(TAG,"selectedValues $selectedValues")
            bottomSheet = BottomSheetSendStudent(this,selectedValues,getStudentList)
            bottomSheet.show(requireActivity().supportFragmentManager, "TAG")
        }
//        initFunction(groupTypeStr,typeStr)
        initFunction()
        bottomSheet = BottomSheetSendStudent()

    }

    private fun initFunction() {
        groupViewModel.getYearClassExam(adminId,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getYears = response.years as ArrayList<GetYearClassExamModel.Year>
                            var years = Array(getYears.size){""}
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
                            var classX = Array(getClass.size){""}
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


                            Log.i(TAG,"initFunction SUCCESS")

                        }
                        Status.ERROR -> {
                            Log.i(TAG,"initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"initFunction LOADING")
                        }
                    }
                }
            })
    }

    fun getFinalList(aCCADEMICID: Int, cLASSID: Int){
        groupViewModel.getStudentListStaff(aCCADEMICID,cLASSID,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getStudentList = response.parentList as ArrayList<StudentListModel.Parent>
                            if(getStudentList.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {

                                       mAdapter =  StudentAdapter(
                                            this,
                                            getStudentList,
                                            requireActivity())
                                    recyclerViewVideo!!.adapter = mAdapter
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
                            getStudentList = ArrayList<StudentListModel.Parent>()
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


    class StudentAdapter(
        var studentListener: StudentListener,
        var getStudentList: ArrayList<StudentListModel.Parent>,
        context: Context)
        : RecyclerView.Adapter<StudentAdapter.ViewHolder>() {

        var mylist = ArrayList<Int>()
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textName: TextView = view.findViewById(R.id.textName)
            var textGuardianName: TextView = view.findViewById(R.id.textGuardianName)
            var imageViewCheck : ImageView = view.findViewById(R.id.imageMessage)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.student_to_group_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textName.text = getStudentList[position].sTUDENTFNAME

            holder.textGuardianName.text =
                "Class : ${getStudentList[position].cLASSNAME} | Admission.No : ${getStudentList[position].aDMISSIONNUMBER}"


            if (getStudentList[position].isChecked) {
                // viewHolder.checkBox.setChecked(true);
                holder.imageViewCheck.setImageResource(R.drawable.ic_rect_check_box_filled)
                mylist.add(position)
                studentListener.onShowMessage(getStudentList,mylist)

            } else {
                //viewHolder.checkBox.setChecked(false);
                holder.imageViewCheck.setImageResource(R.drawable.ic_rect_check_box_outlined)
                mylist.remove(position)
                studentListener.onShowMessage(getStudentList,mylist)
            }

            holder.itemView.setOnClickListener {
                getStudentList[position].isChecked = !getStudentList[position].isChecked
                notifyItemChanged(position)
            }

//            holder.itemView.setOnClickListener {

 //           }


            // if(feedlist.get(position).get("GROUP_TYPE").equals("1")){
            //                holder.input3.setText("Student Group");
            //            }else if(feedlist.get(position).get("GROUP_TYPE").equals("2")){
            //                holder.input3.setText("Public Group");
            //            }
            //
            //            if(feedlist.get(position).get("GROUP_STATUS").equals("1")){
            //                holder.input4.setText("Publish");
            //            }else{
            //                holder.input4.setText("UnPublish");
            //            }
        }

        override fun getItemCount(): Int {
           return getStudentList.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

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


    override fun onShowMessage(
        getStudentList: ArrayList<StudentListModel.Parent>,
        selectedValue: ArrayList<Int>
    ) {
        listCount = 0
        getTempStudentDetails = ArrayList<StudentListModel.Parent>()
        getTempStudentDetails = getStudentList
       // selectedValues = selectedValue
        for (i in getTempStudentDetails.indices) {
            if (getTempStudentDetails[i].isChecked) {
                listCount++
            }
        }


        if(listCount > 0){
            binding.buttonSubmit.visibility = View.VISIBLE
        }else{
            binding.buttonSubmit.visibility = View.GONE
        }
    }

    override fun sendStudentsList(aCCADEMICID: Int, gROUPID: Int) {
        //Create json array for filter
//        if (selectedValues.size > 0) {
//
//            for (i in selectedValues.indices) {
//                val jsonParam = JSONObject()
//                try {
//                    jsonParam.put("STUDENT_Name", getStudentList[i].sTUDENTFNAME)
//                    jsonParam.put("STUDENT_ID", getStudentList[i].sTUDENTID)
//                    jsonParam.put("ADMISSION_NUMBER", getStudentList[i].aDMISSIONNUMBER)
//                    jsonParam.put("CLASS_ID", getStudentList[i].cLASSID)
//                    jsonParam.put("ACCADEMIC_ID", aCCADEMICID)
//                    jsonParam.put("GROUP_ID", gROUPID)
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//                Log.i(TAG,"jsonParam $jsonParam")
//                submitStudentListJson(jsonParam, i)
//            }
//        } else {
//
//        }

        if(listCount <= 0){
            Utils.getSnackBar4K(requireActivity(),"Select atleast one student",binding.constraintLayoutContent)
        }else {
            progressStart();
            var pos = 0
            for (i in getTempStudentDetails.indices) {
                if (getTempStudentDetails[i].isChecked) {
                    pos++
                    val jsonParam = JSONObject()
                    try {
                        jsonParam.put("STUDENT_ID", getStudentList[i].sTUDENTID)
                        jsonParam.put("ADMISSION_NUMBER", getStudentList[i].aDMISSIONNUMBER)
                        jsonParam.put("CLASS_ID", getStudentList[i].cLASSID)
                        jsonParam.put("ACCADEMIC_ID", aCCADEMICID)
                        jsonParam.put("GROUP_ID", gROUPID)

                        Log.i(TAG, "jsonParam $jsonParam")
                        submitStudentListJson(jsonParam, pos, listCount)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    }

    private fun submitStudentListJson(jsonObject: JSONObject, pos: Int, listCount: Int) {

        val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        groupViewModel.getCommonPostFun("Group/GroupStudents",submitItems)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                         //   progressStop()
                            bottomSheet.dismiss()
                            when {
                                Utils.resultFun(response) == "INSERTED" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Student Added to group Successfully", constraintLayoutContent!!)
                                  //  initFunction()
                                }
                                Utils.resultFun(response) == "UPDATED" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Student Added to group Successfully", constraintLayoutContent!!)
                                    //  initFunction()
                                }
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Adding students failed", constraintLayoutContent!!)
                                }
                            }
                            //  if (RESULT.equals("INSERTED")) {
                            //                                Toast.makeText(getActivity(), "Student Added to group Successfully", Toast.LENGTH_SHORT).show();
                            //                            }else if (RESULT.equals("UPDATED")) {
                            //                                Toast.makeText(getActivity(), "Student Added to group Successfully", Toast.LENGTH_SHORT).show();
                            //                            }else if (RESULT.equals("0")) {
                            //                                Toast.makeText(getActivity(), "Adding students failed", Toast.LENGTH_SHORT).show();
                            //                            }
                            Log.i(TAG,"selectedValues $listCount")
                            Log.i(TAG,"selectedValues $pos")
                            if (listCount == pos) {
                                Log.i(TAG,"final pos $pos")
                                progressStop()
                                getFinalList(aCCADEMICID,cLASSID)
                            }
                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            progressStop()
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })

    }


}

interface StudentListener {
    fun onShowMessage(
        getStudentList: ArrayList<StudentListModel.Parent>,
        selectedValue: ArrayList<Int>
    )

    fun sendStudentsList(aCCADEMICID: Int, gROUPID: Int)
}
