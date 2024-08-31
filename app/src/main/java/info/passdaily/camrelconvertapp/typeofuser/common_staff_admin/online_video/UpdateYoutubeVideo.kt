package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.online_video

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogCreateYoutubeVideoBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.parent.online_video.VideoClickListner
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class UpdateYoutubeVideo : DialogFragment {

    lateinit var videoClickListener: VideoClickListner

    companion object {
        var TAG = "UpdateYoutubeVideo"
    }

    private var _binding: DialogCreateYoutubeVideoBinding? = null
    private val binding get() = _binding!!

    private lateinit var onlineVideoStaffViewModel : OnlineVideoStaffViewModel


    var aCCADEMICID = 0
    var cLASSID = 0
    var sUBJECTID = 0
    var cHAPTERID = 0
    var adminId = 0
    var schoolId = 0
    var typeStr =""

    var toolbar : Toolbar? = null
    var constraintLayoutContent : ConstraintLayout? = null

    var spinnerAcademic: AppCompatSpinner? = null
    var spinnerClass: AppCompatSpinner? = null
    var spinnerSubject: AppCompatSpinner? = null
    var spinnerChapter: AppCompatSpinner? = null
    var spinnerStatus : AppCompatSpinner? = null

    var editTextTitle : TextInputEditText? =null
    var editTextDesc : TextInputEditText? =null
    var editTextLink : TextInputEditText? =null

    var buttonSubmit : AppCompatButton? =null

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getSubject = ArrayList<SubjectsModel.Subject>()
    var getChapter = ArrayList<ChaptersListStaffModel.Chapters>()
    private lateinit var localDBHelper : LocalDBHelper

    var type = arrayOf("Unpublished", "Published")
    var typStr = -1
    var youtubeList: YoutubeListStaffModel.Youtube

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleUpdate)
    }

    constructor(videoClickListener: VideoClickListner, youtubeList: YoutubeListStaffModel.Youtube) {
        this.videoClickListener = videoClickListener
        this.youtubeList = youtubeList
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        onlineVideoStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[OnlineVideoStaffViewModel::class.java]

        _binding = DialogCreateYoutubeVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID
        constraintLayoutContent = binding.constraintLayoutContent

        //        pb = new ProgressDialog(getActivity());
//        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pb.setIndeterminate(true);
        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_white)
        toolbar?.title = "Update Online Video"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.white))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }

        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass
        spinnerSubject = binding.spinnerSubject
        spinnerChapter = binding.spinnerChapter
        spinnerStatus  = binding.spinnerStatus

        editTextTitle  = binding.editTextTitle
        editTextDesc  = binding.editTextDesc
        editTextLink  = binding.editTextLink

        editTextTitle?.setText(youtubeList.yOUTUBETITLE)
        editTextLink?.setText(youtubeList.yOUTUBELINK)
        editTextDesc?.setText(youtubeList.yOUTUBEDESCRIPTION)

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

        spinnerSubject?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                sUBJECTID = getSubject[position].sUBJECTID
                getChapterList(aCCADEMICID,cLASSID,sUBJECTID,schoolId)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        spinnerChapter?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                cHAPTERID = getChapter[position].cHAPTERID

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        var str = ""
        typStr = youtubeList.yOUTUBESTATUS
        str = if (typStr == 0) {
            "UnPublished~Published"
        } else {
            "Published~UnPublished"
        }
        type = str.split("~").toTypedArray()
        val status = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, type)
        status.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus?.adapter = status
        spinnerStatus?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    typeStr = position.toString()
                } else if (position == 1) {
                    typeStr = position.toString()
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        buttonSubmit = binding.buttonSubmit
        buttonSubmit?.setBackgroundResource(R.drawable.round_orage400)
        buttonSubmit?.setTextAppearance(
            requireActivity(),
            R.style.RoundedCornerButtonOrange400
        )
        buttonSubmit?.text = requireActivity().resources.getString(R.string.update_video)
        buttonSubmit?.setOnClickListener {
            if(onlineVideoStaffViewModel.validateField(editTextTitle!!,"Title field cannot be empty",requireActivity(),constraintLayoutContent!!)
                && onlineVideoStaffViewModel.validateField(editTextLink!!,"Youtube Link field cannot be empty",requireActivity(),constraintLayoutContent!!)
                && onlineVideoStaffViewModel.validateField(editTextDesc!!,"Description field cannot be empty",requireActivity(),constraintLayoutContent!!)){


                val url = "OnlineVideo/VideoSetById"
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("ACCADEMIC_ID", aCCADEMICID)
                    jsonObject.put("CLASS_ID", cLASSID)
                    jsonObject.put("SUBJECT_ID", sUBJECTID)
                    jsonObject.put("CHAPTER_ID", cHAPTERID)
                    jsonObject.put("YOUTUBE_TITLE", editTextTitle?.text.toString())
                    jsonObject.put("YOUTUBE_DESCRIPTION", editTextDesc?.text.toString())
                    jsonObject.put("YOUTUBE_LINK", editTextLink?.text.toString())
                    jsonObject.put("YOUTUBE_STATUS", typeStr)
                    jsonObject.put("YOUTUBE_CREATED_BY", adminId)
                    jsonObject.put("YOUTUBE_ID", youtubeList.yOUTUBEID)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                onlineVideoStaffViewModel.getCommonPostFun(url,accademicRe)
                    .observe(requireActivity(), Observer {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    val response = resource.data?.body()!!
                                    progressStop()
                                    when {
                                        Utils.resultFun(response) == "1" -> {
                                            videoClickListener.onCreateClick("Youtube Video Updated")
                                            cancelFrg()
                                        }
                                        Utils.resultFun(response) == "0" -> {
                                            Utils.getSnackBar4K(requireActivity(), "Failed while Youtube Video Creation", constraintLayoutContent!!)
                                        }
                                        Utils.resultFun(response) == "-1" -> {
                                            Utils.getSnackBar4K(requireActivity(), "Video Already Exist", constraintLayoutContent!!)
                                        }
                                    }
                                }
                                Status.ERROR -> {
                                    progressStop()
                                    Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLayoutContent!!)
                                }
                                Status.LOADING -> {
                                    progressStart()
                                    Log.i(TAG,"loading")
                                }
                            }
                        }
                    })

            }

        }
        val constraintLayoutContent = binding.constraintLayoutContent
        constraintLayoutContent.setOnClickListener {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(constraintLayoutContent.windowToken, 0)
        }

        initFunction()
        Utils.setStatusBarColor(requireActivity())
    }


    private fun initFunction() {
        onlineVideoStaffViewModel.getYearClassExam(adminId,schoolId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            var aCCADEMICID = 0
                            var cLASSID = 0
                            getYears = response.years as ArrayList<GetYearClassExamModel.Year>
                            var years = Array(getYears.size){""}
                            for (i in getYears.indices) {
                                years[i] = getYears[i].aCCADEMICTIME
                                if (youtubeList.aCCADEMICID == getYears[i].aCCADEMICID) {
                                    aCCADEMICID = i
                                }
                            }
                            if (spinnerAcademic != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    years
                                )
                                spinnerAcademic?.adapter = adapter
                            }
                            spinnerAcademic?.setSelection(aCCADEMICID)



                            getClass = response.classList as ArrayList<GetYearClassExamModel.Class>
                            var classX = Array(getClass.size){""}
                            for (i in getClass.indices) {
                                classX[i] = getClass[i].cLASSNAME
                                if (youtubeList.cLASSID == getClass[i].cLASSID) {
                                    cLASSID = i
                                }
                            }
                            if (spinnerClass != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    classX
                                )
                                spinnerClass?.adapter = adapter
                            }
                            spinnerClass?.setSelection(cLASSID)

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
        onlineVideoStaffViewModel.getSubjectStaff(cLASSID,adminId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            var sUBJECTID = 0
                            getSubject = response.subjects as ArrayList<SubjectsModel.Subject>
                            var subject = Array(getSubject.size){""}
                            if(subject.isNotEmpty()){
                                for (i in getSubject.indices) {
                                    subject[i] = getSubject[i].sUBJECTNAME
                                    if (youtubeList.sUBJECTID == getSubject[i].sUBJECTID) {
                                        sUBJECTID = i
                                    }
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
                            spinnerSubject?.setSelection(sUBJECTID)

                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            getSubject = ArrayList<SubjectsModel.Subject>()
                            getChapter = ArrayList<ChaptersListStaffModel.Chapters>()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }

    fun getChapterList(aCCADEMICID : Int,cLASSID : Int,sUBJECTID : Int,schoolId:Int){

        onlineVideoStaffViewModel.getChapterStaff(aCCADEMICID,cLASSID,sUBJECTID,schoolId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            getChapter = response.chaptersList as ArrayList<ChaptersListStaffModel.Chapters>
                            var cHAPTERID = 0
                            var chapter = Array(getChapter.size){""}
                            if(chapter.isNotEmpty()) {
                                for (i in getChapter.indices) { chapter[i] = getChapter[i].cHAPTERNAME
                                    if (youtubeList.cHAPTERID == getChapter[i].cHAPTERID) {
                                        cHAPTERID = i
                                    }
                                }
                            }

                            if (spinnerChapter != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    chapter
                                )
                                spinnerChapter?.adapter = adapter
                            }
                            spinnerChapter?.setSelection(cHAPTERID)

                            Log.i(TAG,"getChapterList SUCCESS")
                        }
                        Status.ERROR -> {

                            Log.i(TAG,"getChapterList ERROR")
                        }
                        Status.LOADING -> {
                            getChapter = ArrayList<ChaptersListStaffModel.Chapters>()
                            Log.i(TAG,"getChapterList LOADING")
                        }
                    }
                }
            })
    }

    private fun cancelFrg() {
        val prev = requireActivity().supportFragmentManager.findFragmentByTag(TAG)
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
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
}