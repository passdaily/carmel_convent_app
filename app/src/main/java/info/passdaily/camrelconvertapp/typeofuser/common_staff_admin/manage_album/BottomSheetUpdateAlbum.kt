package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_album

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetUpdateAlbumBinding
import info.passdaily.camrelconvertapp.databinding.BottomSheetUpdatePublicMemberBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.GroupViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.public_member.BottomSheetUpdatePublicMember
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class BottomSheetUpdateAlbum : BottomSheetDialogFragment {



    private var _binding: BottomSheetUpdateAlbumBinding? = null
    private val binding get() = _binding!!
    lateinit var albumListener: AlbumListener

    private lateinit var manageAlbumViewModel: ManageAlbumViewModel


    var aCCADEMICID = 0


    var getYears = ArrayList<GetYearClassExamModel.Year>()



    var spinnerAcademic: AppCompatSpinner? = null
    var spinnerSubject: AppCompatSpinner? = null


    var editTextTitle : TextInputEditText? =null
    var editDescription : TextInputEditText? =null

    lateinit var albumCategory: AlbumCategoryModel.AlbumCategory

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0
    constructor()

    constructor(albumListener: AlbumListener,albumCategory: AlbumCategoryModel.AlbumCategory){
        this.albumListener = albumListener
        this.albumCategory = albumCategory

//        this.gMEMBERID = gMEMBERID
//        this.gROUPNAME = gROUPNAME
//        this.gMEMBERNUMBER = gMEMBERNUMBER
//        this.aCCADEMICID = aCCADEMICID
//        this.gROUPID = gROUPID
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        manageAlbumViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ManageAlbumViewModel::class.java]

        _binding = BottomSheetUpdateAlbumBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        editTextTitle  = binding.editTextTitle
        editDescription  = binding.editDescription

        editTextTitle?.setText(albumCategory.aLBUMCATEGORYNAME)
        editDescription?.setText(albumCategory.aLBUMCATEGORYDISCRIPTION)



        binding.buttonSubmit.setOnClickListener {
            if(binding.editTextTitle.text.toString().isNotEmpty() &&
                binding.editDescription.text.toString().isNotEmpty()){
                val url = "Teacher/AlbumCategoryEdit"
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("ALBUM_CATEGORY_NAME",binding.editTextTitle.text.toString())
                        jsonObject.put("ALBUM_CATEGORY_DISCRIPTION", binding.editDescription.text.toString())
                        jsonObject.put("ALBUM_CATEGORY_TYPE", albumCategory.aLBUMCATEGORYTYPE)
                        jsonObject.put("ACCADEMIC_ID", albumCategory.aCCADEMICID)
                        jsonObject.put("ALBUM_CATEGORY_CREATED", albumCategory.aLBUMCATEGORYCREATED)
                        jsonObject.put("ALBUM_CATEGORY_ID", albumCategory.aLBUMCATEGORYID)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    Log.i(TAG,"jsonObject $jsonObject")

                    val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    albumListener.onUpdateClick(url,submitItems,
                        "Album Updated Successfully","Album Updation Failed")
            }else{
                albumListener.onShowMessage("Don't leave fields empty")
            }
        }

        binding.textDeleteIcon.setOnClickListener {
            albumListener.onDeleteItem(albumCategory.aLBUMCATEGORYID)
        }

        initFunction()


    }


    private fun initFunction() {
        manageAlbumViewModel.getYearClassExam(adminId, schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getYears = response.years as java.util.ArrayList<GetYearClassExamModel.Year>
                            var years = Array(getYears.size){""}
                            for (i in getYears.indices) {
                                years[i] = getYears[i].aCCADEMICTIME
                                if (albumCategory.aCCADEMICID == getYears[i].aCCADEMICID) {
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

    companion object {
        var TAG = "BottomSheetFragment"
    }
}