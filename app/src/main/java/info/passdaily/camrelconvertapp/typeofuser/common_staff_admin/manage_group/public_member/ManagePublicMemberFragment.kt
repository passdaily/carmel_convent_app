package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.public_member

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Html
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
import okhttp3.RequestBody

@Suppress("DEPRECATION")
class ManagePublicMemberFragment : Fragment(),PublicMemberListener {

    var TAG = "ManagePublicMember"
    private lateinit var groupViewModel: GroupViewModel
    private var _binding: FragmentManageGroupBinding? = null
    private val binding get() = _binding!!

    var isWorking= false
    // http://demostaff.passdaily.in/ElixirApi/Group/GroupAdd
    var aCCADEMICID = 0
    var gROUPID = 0

    lateinit var bottomSheet : BottomSheetCreatePublicMember

    lateinit var bottomSheetUpdate : BottomSheetUpdatePublicMember

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var   schoolId = 0
    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getGroupList = ArrayList<GroupListModel.Group>()

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo : RecyclerView? = null

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null
    var toolBarClickListener : ToolBarClickListener? = null

    var getPublicMembers = ArrayList<PublicMembersModel.PublicMember>()
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
        toolBarClickListener?.setToolbarName("Manage Public Member")
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
        binding.classText.text = requireActivity().resources.getText(R.string.select_group)

        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                aCCADEMICID = getYears[position].aCCADEMICID
                groupListFun(aCCADEMICID)
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
                gROUPID = getGroupList[position].gROUPID
                getFinalList(aCCADEMICID,gROUPID)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        initFunction()

        //binding.fab.visibility = View.GONE
        binding.buttonSubmit.visibility = View.GONE

        binding.fab.setOnClickListener {

            bottomSheet = BottomSheetCreatePublicMember(this)
            bottomSheet.show(requireActivity().supportFragmentManager, "TAG")
        }
        bottomSheet = BottomSheetCreatePublicMember()
        bottomSheetUpdate = BottomSheetUpdatePublicMember()
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

    private fun groupListFun(aCCADEMICID: Int) {
        // /////Public member
        //    //http://demostaff.passdaily.in/ElixirApi/PublicGroup/GroupListGet?AccademicId=0
        groupViewModel.getGroupListForStudentDelete("PublicGroup/GroupListGet",aCCADEMICID,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getGroupList = response.groupList as ArrayList<GroupListModel.Group>
                            var group = Array(getGroupList.size){""}
                            for (i in getGroupList.indices) {
                                group[i] = getGroupList[i].gROUPNAME!!
                            }
                            if (spinnerClass != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    group
                                )
                                spinnerClass?.adapter = adapter
                            }

                            if(getGroupList.isEmpty()){
                                recyclerViewVideo?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text =  resources.getString(R.string.no_results)
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

    fun getFinalList(aCCADEMICID: Int, gROUPID: Int){
        groupViewModel.getPublicMember(aCCADEMICID,gROUPID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getPublicMembers = response.publicMembers as ArrayList<PublicMembersModel.PublicMember>
                            if(getPublicMembers.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewVideo!!.adapter =
                                        PublicMemberAdapter(
                                            this,
                                            getPublicMembers,
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
                            getPublicMembers = ArrayList<PublicMembersModel.PublicMember>()
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


    class PublicMemberAdapter(var publicMemberListener: PublicMemberListener,
                              var publicMemberList: ArrayList<PublicMembersModel.PublicMember>,
                              context: Context)
        : RecyclerView.Adapter<PublicMemberAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textGroupName: TextView = view.findViewById(R.id.textGroupName)
            var textGroupType: TextView = view.findViewById(R.id.textGroupType)
            var textMobile : TextView = view.findViewById(R.id.textMobile)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.public_member_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textGroupName.text = publicMemberList[position].gMEMBERNAME
            holder.textMobile.text = publicMemberList[position].gROUPNAME
            holder.textGroupType.text = "Mobile : ${publicMemberList[position].gMEMBERNUMBER}"

            holder.itemView.setOnClickListener {
                publicMemberListener.onUpdateItem(publicMemberList[position])
            }

        }

        override fun getItemCount(): Int {
           return publicMemberList.size
        }

    }

    override fun onShowMessage(message: String) {
        Log.i(TAG,"onCreateClick ")
        Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)
    }

    override fun onCreateClick(url: String, submitItems: RequestBody?, successMsg: String, failerMsg: String,existMsg :String) {

        groupViewModel.getCommonPostFun(url,submitItems)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "1" -> {
                                    Utils.getSnackBarGreen(requireActivity(), successMsg, constraintLayoutContent!!)
                                    bottomSheet.dismiss()
                                    bottomSheetUpdate.dismiss()
                                    initFunction()
                                }
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBar4K(requireActivity(), failerMsg, constraintLayoutContent!!)
                                }
                                Utils.resultFun(response) == "-1" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Group Already Existing", constraintLayoutContent!!)
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

    override fun onUpdateItem(publicMember: PublicMembersModel.PublicMember) {
        bottomSheetUpdate = BottomSheetUpdatePublicMember(this, publicMember)
        bottomSheetUpdate.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onDeleteItem(gMEMBERID: Int,gMEMBERNAME:String) {

        val builder = AlertDialog.Builder(context)
        builder.setMessage(
            Html.fromHtml(
                "Do you want conform delete <font><b>$gMEMBERNAME</b></font> football group?"
            ))
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                deleteFunction(gMEMBERID)
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

    fun deleteFunction(gMEMBERID: Int) {

//        /   ///Group/PublicMemberDropById?GmemberId=
        groupViewModel.getDeleteGroupStudentItem("Group/PublicMemberDropById",gMEMBERID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Member Deleted Successfully", constraintLayoutContent!!)
                                    bottomSheetUpdate.dismiss()
                                    initFunction()
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Member Deletion Failed", constraintLayoutContent!!)
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


}

interface PublicMemberListener{
    fun onShowMessage(message: String)
    fun onCreateClick(url : String,submitItems: RequestBody?,successMsg : String, failerMsg : String,existMsg :String)
    fun onUpdateItem(publicMember: PublicMembersModel.PublicMember)

    fun onDeleteItem(gMEMBERID:Int,gMEMBERNAME:String)
}

