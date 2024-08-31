package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_album

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
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
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.CreateAssignmentDialog
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.public_member.BottomSheetUpdatePublicMember
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import okhttp3.RequestBody

@Suppress("DEPRECATION")
class ManageAlbumFragment : Fragment(),AlbumListener {

    var TAG = "ManageAlbumFragment"
    private lateinit var manageAlbumViewModel: ManageAlbumViewModel
    private var _binding: FragmentManageGroupBinding? = null
    private val binding get() = _binding!!

    var aCCADEMICID = 0
    var gROUPTYPE = 0
    var getYears = ArrayList<GetYearClassExamModel.Year>()

    lateinit var bottomSheetUpdate : BottomSheetUpdateAlbum

    var album = arrayOf("Select type", "Photo Album", "Video Album")
    var groupTypeStr ="-1"

    var isWorking= false
    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0
    var getAlbumCategory = ArrayList<AlbumCategoryModel.AlbumCategory>()

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo : RecyclerView? = null

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null
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
        Global.screenState = "staffhomepage"
        toolBarClickListener?.setToolbarName("Manage Album Name")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        manageAlbumViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ManageAlbumViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentManageGroupBinding.inflate(inflater, container, false)
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

        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())

        binding.accedemicText.text = requireActivity().resources.getText(R.string.select_year)
        binding.classText.text = requireActivity().resources.getText(R.string.select_album)

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

        val status= ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, album)
        status.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClass?.adapter = status
        spinnerClass?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                if(isWorking){
                    when (position) {
                        0 -> { groupTypeStr = "-1" }
                        1 -> { groupTypeStr = position.toString() }
                        2 -> { groupTypeStr = position.toString() }
                    }
                }
                getFinalList(aCCADEMICID,groupTypeStr.toInt())
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }


        initFunction()

        binding.fab.visibility = View.VISIBLE

        binding.buttonSubmit.visibility = View.GONE
        binding.fab.setOnClickListener {
            val dialog1 = CreateAlbumCategory(this,adminId)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, CreateAlbumCategory.TAG)
        }

        bottomSheetUpdate = BottomSheetUpdateAlbum()
    }

    private fun initFunction() {
        manageAlbumViewModel.getYearClassExam(adminId, schoolId )
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
                            Log.i(CreateAssignmentDialog.TAG, "initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(CreateAssignmentDialog.TAG, "initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(CreateAssignmentDialog.TAG, "initFunction LOADING")
                        }
                    }
                }
            })
    }


    fun getFinalList(aCCADEMICID: Int, groupTypeStr: Int){
        manageAlbumViewModel.getManageAlbumList(aCCADEMICID,groupTypeStr)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            isWorking = true
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getAlbumCategory = response.albumCategory as ArrayList<AlbumCategoryModel.AlbumCategory>
                            if(getAlbumCategory.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewVideo!!.adapter =
                                        AlbumAdapter(
                                            this,
                                            getAlbumCategory,
                                            requireActivity()
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
                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewVideo?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE
                            if (isAdded) {
                                Glide.with(this)
                                    .load(R.drawable.ic_no_internet)
                                    .into(imageViewEmpty!!)
                                textEmpty?.text = resources.getString(R.string.no_internet)
                                Log.i(TAG, "getSubjectList ERROR")
                            }
                        }
                        Status.LOADING -> {
                            recyclerViewVideo?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getAlbumCategory = ArrayList<AlbumCategoryModel.AlbumCategory>()
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




    class AlbumAdapter(var albumListener: AlbumListener,
                       var getAlbumCategory: ArrayList<AlbumCategoryModel.AlbumCategory>,
                       var context: Context)
        : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textGroupName: TextView = view.findViewById(R.id.textGroupName)
            var textStatus: TextView = view.findViewById(R.id.textStatus)
            var textDescription : TextView = view.findViewById(R.id.textDescription)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.album_categories_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.textGroupName.text = getAlbumCategory[position].aLBUMCATEGORYNAME
            holder.textDescription.text = getAlbumCategory[position].aLBUMCATEGORYDISCRIPTION

            //ALBUM_CATEGORY_TYPE
            if (getAlbumCategory[position].aLBUMCATEGORYTYPE != null) {
                when (getAlbumCategory[position].aLBUMCATEGORYTYPE!!.toInt() ) {
                    1 -> holder.textStatus.text = "Photo"
                    2 -> holder.textStatus.text = "Video"
                }
            }


            holder.itemView.setOnClickListener {
                albumListener.onBottomDialogItem(getAlbumCategory[position])
            }


        }

        override fun getItemCount(): Int {
           return getAlbumCategory.size
        }

    }



    override fun onBottomDialogItem(albumCategory: AlbumCategoryModel.AlbumCategory) {
        bottomSheetUpdate = BottomSheetUpdateAlbum(this,albumCategory)
        bottomSheetUpdate.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onShowMessage(message: String) {
        Log.i(TAG,"onCreateClick ")
        Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)

    }

    override fun onUpdateClick(url : String,submitItems: RequestBody?,successMsg : String, failerMsg : String) {

        manageAlbumViewModel.getCommonPostFun(url,submitItems)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "1" -> {
                                    Utils.getSnackBarGreen(requireActivity(), successMsg, constraintLayoutContent!!)
                                    bottomSheetUpdate.dismiss()
                                    getFinalList(aCCADEMICID,groupTypeStr.toInt())
                                }
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBar4K(requireActivity(), failerMsg, constraintLayoutContent!!)
                                }
                                Utils.resultFun(response) == "-1" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Album Already Existing", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }

    override fun onCreateClick(url : String,submitItems: RequestBody?,successMsg : String, failerMsg : String) {

        manageAlbumViewModel.getCommonPostFun(url,submitItems)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    Utils.getSnackBarGreen(requireActivity(), successMsg, constraintLayoutContent!!)
                                  //  bottomSheetUpdate.dismiss()
                                    getFinalList(aCCADEMICID,groupTypeStr.toInt())
                                }
                                Utils.resultFun(response) == "EXIST" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Album Already Existing", constraintLayoutContent!!)
                                }

                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }



    override fun onDeleteItem(aLBUMCATEGORYID:Int){

        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure want to delete album?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
               deleteFunction(aLBUMCATEGORYID)
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

    fun deleteFunction(gROUPID: Int) {
        manageAlbumViewModel.getAlbumCategoryDelete("Teacher/AlbumCategoryDelete",gROUPID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Group Deleted Successfully", constraintLayoutContent!!)
                                    bottomSheetUpdate.dismiss()
                                    getFinalList(aCCADEMICID,groupTypeStr.toInt())
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Group Deletion Failed", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG,"getSubjectList LOADING")
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

interface AlbumListener{
    fun onShowMessage(message: String)
    fun onCreateClick(url : String,submitItems: RequestBody?,successMsg : String, failerMsg : String)
    fun onUpdateClick(url : String,submitItems: RequestBody?,successMsg : String, failerMsg : String)
    fun onBottomDialogItem(albumCategory: AlbumCategoryModel.AlbumCategory)

    fun onDeleteItem(aLBUMCATEGORYID:Int)
}