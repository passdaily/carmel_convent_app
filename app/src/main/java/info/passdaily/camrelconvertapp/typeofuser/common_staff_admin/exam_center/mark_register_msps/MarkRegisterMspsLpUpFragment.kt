package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.mark_register_msps

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentMarkRegisterCeBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.mark_register.MarkRegisterViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.CreateObjectiveExam
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class MarkRegisterMspsLpUpFragment : Fragment(),MarkRegisterMspsClicker {

    private lateinit var markRegisterViewModel: MarkRegisterViewModel
    private var _binding: FragmentMarkRegisterCeBinding? = null
    private val binding get() = _binding!!

    lateinit var bottomSheet : BottomMarkRegisterMspsLpUp

    var adminId = 0
    var schoolId =0
    var TAG = "MarkRegisterMspsLpUpFragment"

    var aCCADEMICID = 0
    var cLASSID = 0
    var sUBJECTID = 0
    var eXAMID = 0
    var toolBarClickListener : ToolBarClickListener? = null

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getExam = ArrayList<GetYearClassExamModel.Exam>()
    var getSubject = ArrayList<SubjectsModel.Subject>()

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null
    var spinnerSubject : AppCompatSpinner? = null
    var spinnerExam : AppCompatSpinner? = null

    var constraintRecyclerView : ConstraintLayout? = null

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo : RecyclerView? = null
    var isLoaded = false

    lateinit var mAdapter : MarkRegisterMspsAdapter

    var marksList = ArrayList<MarkRegisterMspsLpUpModel.Mark>()

    private lateinit var localDBHelper : LocalDBHelper

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
        toolBarClickListener?.setToolbarName("Mark Register CE LP/UP")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId =  user[0].SCHOOL_ID
        // Inflate the layout for this fragment
        markRegisterViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[MarkRegisterViewModel::class.java]

        _binding = FragmentMarkRegisterCeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        constraintLayoutContent = binding.constraintLayoutContent

        constraintRecyclerView = binding.constraintRecyclerView

        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        Glide.with(this)
            .load(R.drawable.ic_empty_progress_report)
            .into(imageViewEmpty!!)
        shimmerViewContainer = binding.shimmerViewContainer

        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass
        spinnerSubject = binding.spinnerSubject
        spinnerExam = binding.spinnerExam

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
                getSubjectList(cLASSID)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }


        spinnerExam?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                eXAMID = getExam[position].eXAMID
                if(isLoaded){
                    getMarkList(aCCADEMICID,cLASSID,sUBJECTID,eXAMID)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        spinnerSubject?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                sUBJECTID = getSubject[position].sUBJECTID
                isLoaded = true
                getMarkList(aCCADEMICID,cLASSID,sUBJECTID,eXAMID)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

       // binding.editPassMarkInternal.hint = requireActivity().resources.getString(R.string.pass_mark_ce)
        binding.editOutOffMarkInternal.hint = requireActivity().resources.getString(R.string.out_off_mark_ce)

        bottomSheet = BottomMarkRegisterMspsLpUp()
        initFunction()
    }

    private fun initFunction() {
        markRegisterViewModel.getYearClassExam(adminId,schoolId)
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

                            getExam = response.exams as ArrayList<GetYearClassExamModel.Exam>
                            var examX = Array(getExam.size){""}
                            for (i in getExam.indices) {
                                examX[i] = getExam[i].eXAMNAME
                            }
                            if (spinnerExam != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    examX
                                )
                                spinnerExam?.adapter = adapter
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

    fun getSubjectList(cLASSID : Int){
        markRegisterViewModel.getSubjectStaff(cLASSID,adminId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
//                            constraintLayoutContent?.visibility = View.VISIBLE
//                            shimmerViewContainer?.visibility = View.GONE
                            getSubject = response.subjects as ArrayList<SubjectsModel.Subject>
                            var subject = Array(getSubject.size){""}
                            if(subject.isNotEmpty()){
                                for (i in getSubject.indices) {
                                    subject[i] = getSubject[i].sUBJECTNAME
                                }
                            }
                            if (spinnerSubject != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    subject
                                )
                                spinnerSubject?.adapter = adapter
                            }

                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }

    private fun getMarkList(aCCADEMICID: Int, cLASSID: Int, sUBJECTID: Int, eXAMID: Int) {
        markRegisterViewModel.getMarkRegisterMsps("MarkMspLpUp/SearchMarksMsp",aCCADEMICID,cLASSID,eXAMID,sUBJECTID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            marksList= response.marksList as ArrayList<MarkRegisterMspsLpUpModel.Mark>
                            if(marksList.isNotEmpty()){
                                constraintRecyclerView?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    mAdapter =  MarkRegisterMspsAdapter(
                                        this,
                                        marksList,
                                        requireActivity(),
                                        TAG
                                    )
                                    if(marksList.size >0){
                                       // binding.editPassMark.setText(marksList[0].pASSMARK)
                                        binding.editOutOffMark.setText(marksList[0].oUTOFFMARK)
                                       // binding.editPassMarkInternal.setText(marksList[0].pASSMARKCE)
                                        binding.editOutOffMarkInternal.setText(marksList[0].oUTOFFMARKCE)
                                    }

                                    recyclerViewVideo!!.adapter = mAdapter
                                }
                            }else{
                                constraintRecyclerView?.visibility = View.GONE
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
                            constraintRecyclerView?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text =  resources.getString(R.string.no_internet)
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            constraintRecyclerView?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            marksList = ArrayList<MarkRegisterMspsLpUpModel.Mark>()
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

    class MarkRegisterMspsAdapter(var markRegisterMspsClicker : MarkRegisterMspsClicker,
                                  var marksList: ArrayList<MarkRegisterMspsLpUpModel.Mark>,
                                  var context: Context, var TAG: String) :
        RecyclerView.Adapter<MarkRegisterMspsAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewName: TextView = view.findViewById(R.id.textViewName)
            var textViewClass: TextView = view.findViewById(R.id.textViewClass)
            var textUpdated : TextView = view.findViewById(R.id.textUpdated)
            var editTextPT: TextView = view.findViewById(R.id.editTextPT)
            var editTextPF: TextView = view.findViewById(R.id.editTextPF)
            var editTextSEA: TextView = view.findViewById(R.id.editTextSEA)
            var editTextExternal: TextView = view.findViewById(R.id.editTextExternal)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.mark_register_msps_lpup_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {


           holder.textViewName.text = marksList[position].sTUDENTFNAME
            holder.textViewClass.text = "Class : ${marksList[position].cLASSNAME}"

            if (marksList[position].tOTALMARK != "N") {
                holder.editTextExternal.text = marksList[position].tOTALMARK
            }

            if (marksList[position].tOTALMARKCE1 != "N") {
                holder.editTextPT.text = marksList[position].tOTALMARKCE1
            }

            if (marksList[position].tOTALMARKCE2 != "N") {
                holder.editTextPF.text = marksList[position].tOTALMARKCE2
            }

            if (marksList[position].tOTALMARKCE3 != "N") {
                holder.editTextSEA.text = marksList[position].tOTALMARKCE3
            }


            holder.itemView.setOnClickListener {
                markRegisterMspsClicker.onMarkDetails(marksList,position)
            }

            if(marksList[position].updated){
                holder.textUpdated.isVisible = true
            }

        }

        override fun getItemCount(): Int {
            return marksList.size
        }

    }

    override fun onMarkDetails(mark: ArrayList<MarkRegisterMspsLpUpModel.Mark>, position: Int) {
    //    Log.i(TAG,"editPassMark ${binding.editPassMark.text.toString()}")
        Log.i(TAG,"editOutOffMark ${binding.editOutOffMark.text.toString()}")
        bottomSheet = BottomMarkRegisterMspsLpUp(""/*binding.editPassMark.text.toString()*/,
            binding.editOutOffMark.text.toString(),""/*binding.editPassMarkInternal.text.toString()*/,
            binding.editOutOffMarkInternal.text.toString(),mark,position,this)
        bottomSheet.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onMessageClick(message: String) {
        Log.i(TAG,"onCreateClick")
        Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)
    }

    override fun onSubmitClickListener(textMarksExternal: String,
                                       textMarksPT: String,
                                       textMarksPF : String,
                                       textMarksSEA : String,
                                       textMarksMAA : String, mark : MarkRegisterMspsLpUpModel.Mark, position : Int){
        //http://msesstaff.passdaily.in/ElixirApi/MarkMsesLpUp/MarkAdd
//          postParam.put("ACCADEMIC_ID",s_aid);
//        postParam.put("CLASS_ID", scid);
//        postParam.put("EXAM_ID", s_eid);
//        postParam.put("SUBJECT_ID", ssid);

//        postParam.put("STUDENT_ROLL_NUMBER",feedlist4.get(position).get("STUDENT_ROLL_NUMBER"));
//        postParam.put("PASS_MARK", passmark);
//        postParam.put("OUTOFF_MARK", outofmark);
//        postParam.put("TOTAL_MARK", details);

//        postParam.put("MARKED_BY", Global.Admin_id);
//        postParam.put("MARK_GRADE", "");
//        postParam.put("STUDENT_ID", feedlist4.get(position).get("STUDENT_ID"));


//        postParam.put("PASS_MARK_CE", passmark_intrnl);
//        postParam.put("OUTOFF_MARK_CE", outofmark_intrnl);
//        postParam.put("TOTAL_MARK_CE_1", detail2);
//        postParam.put("TOTAL_MARK_CE_2", detail3);
//        postParam.put("TOTAL_MARK_CE_3", detail4);

        val url = "MarkMspLpUp/MarkAdd"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("SCHOOL_ID", schoolId)
            jsonObject.put("ACCADEMIC_ID", aCCADEMICID)
            jsonObject.put("CLASS_ID", cLASSID)
            jsonObject.put("EXAM_ID", eXAMID)
            jsonObject.put("SUBJECT_ID", sUBJECTID)

            jsonObject.put("STUDENT_ROLL_NUMBER", mark.sTUDENTROLLNUMBER)
            jsonObject.put("PASS_MARK", 0/*binding.editPassMark.text.toString()*/)
            jsonObject.put("OUTOFF_MARK", binding.editOutOffMark.text.toString())
            jsonObject.put("TOTAL_MARK", textMarksExternal)

            jsonObject.put("PASS_MARK_CE",0/* binding.editPassMarkInternal.text.toString()*/)
            jsonObject.put("OUTOFF_MARK_CE", binding.editOutOffMarkInternal.text.toString())
            jsonObject.put("TOTAL_MARK_CE_1", textMarksPT)
            jsonObject.put("TOTAL_MARK_CE_2", textMarksPF);
            jsonObject.put("TOTAL_MARK_CE_3", textMarksSEA);

            jsonObject.put("MARKED_BY",adminId)
            jsonObject.put("MARK_GRADE", "")
            jsonObject.put("STUDENT_ID", mark.sTUDENTID)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")
        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        markRegisterViewModel.getCommonPostFun(url,accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    marksList[position].updated = true
                                    marksList[position].tOTALMARK = textMarksExternal
                                    marksList[position].tOTALMARKCE1 = textMarksPT
                                    marksList[position].tOTALMARKCE2 = textMarksPF
                                    marksList[position].tOTALMARKCE3 = textMarksSEA
                                    mAdapter.notifyItemChanged(position)
                                    bottomSheet.dismiss();//to hide it
                                    Utils.getSnackBarGreen(requireActivity(), "Grade added successfully", constraintLayoutContent!!)
                                    if ((position + 1) == marksList.size) {
                                        onMessageClick("Student Ends Here")
                                    } else {
                                        onMarkDetails(marksList,position+1)
                                    }
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Grade adding process failed", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLayoutContent!!)
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(CreateObjectiveExam.TAG,"loading")
                        }
                    }
                }
            })


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
interface MarkRegisterMspsClicker{
    fun onMessageClick(message: String)

    fun onMarkDetails(mark: ArrayList<MarkRegisterMspsLpUpModel.Mark>, position: Int)

    fun onSubmitClickListener(textMarksExternal: String,
                              textMarksPT: String,
                              textMarksPF : String,
                              textMarksSEA : String,
                              textMarksMAA : String,
                              mark : MarkRegisterMspsLpUpModel.Mark,
                              position :Int)

}