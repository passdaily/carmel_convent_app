package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group

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
import androidx.cardview.widget.CardView
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
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class ManageGroupFragment : Fragment(),GroupListener {

    var TAG = "ManageGroupFragment"
    private lateinit var groupViewModel: GroupViewModel
    private var _binding: FragmentManageGroupBinding? = null
    private val binding get() = _binding!!

    lateinit var bottomSheet : BottomSheetCreateGroup
    lateinit var bottomSheetUpdate  : BottomSheetUpdateGroup

    // http://demostaff.passdaily.in/ElixirApi/Group/GroupAdd
    var group_type = arrayOf("Select Group Type", "Student Group","Public Group")
    var type = arrayOf("Select Status","Unpublished", "Publish")
    var groupTypeStr ="-1"
    var typeStr ="-1"
    var isWorking= false
    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0

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
        toolBarClickListener?.setToolbarName("Manage Group")
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


        val groupTypeAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, group_type)
        groupTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAcademic?.adapter = groupTypeAdapter
        spinnerAcademic?.onItemSelectedListener = object :
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
                    initFunction(groupTypeStr,typeStr)
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val status= ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, type)
        status.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClass?.adapter = status
        spinnerClass?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                if(isWorking) {
                    when (position) {
                        0 -> { typeStr = "-1"}
                        1 -> { typeStr = "0" }
                        2 -> { typeStr = "1" }
                    }
                    initFunction(groupTypeStr,typeStr)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }


        initFunction(groupTypeStr,typeStr)

        binding.fab.visibility = View.VISIBLE

        binding.buttonSubmit.visibility = View.GONE
        binding.fab.setOnClickListener {
            bottomSheet = BottomSheetCreateGroup(this)
            bottomSheet.show(requireActivity().supportFragmentManager, "TAG")
        }

        bottomSheet = BottomSheetCreateGroup()
        bottomSheetUpdate = BottomSheetUpdateGroup()

    }


    fun initFunction(groupTypeStr : String,typeStr : String){

        // postParam.put("GROUP_NAME",group_name);
        //        postParam.put("GROUP_TYPE",type_str);
        //        postParam.put("GROUP_STATUS",status );
        val jsonObject = JSONObject()
        try {
            //schoolId
            jsonObject.put("SCHOOL_ID", schoolId)
            jsonObject.put("GROUP_NAME", "")
            jsonObject.put("GROUP_TYPE", groupTypeStr)
            jsonObject.put("GROUP_STATUS", typeStr)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")

        val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())


        groupViewModel.getGroupList(submitItems)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            isWorking = true
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getGroupList= response.groupList as ArrayList<GroupListModel.Group>
                            if(getGroupList.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewVideo!!.adapter =
                                        GroupAdapter(
                                            this,
                                            getGroupList,
                                            requireActivity())
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

                            Glide.with(requireActivity())
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text =  resources.getString(R.string.no_internet)
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            recyclerViewVideo?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getGroupList = ArrayList<GroupListModel.Group>()
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

    class GroupAdapter(var groupListener: GroupListener,
                       var groupList: ArrayList<GroupListModel.Group>,
                       var context: Context)
        : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textGroupName: TextView = view.findViewById(R.id.textGroupName)
            var textGroupType: TextView = view.findViewById(R.id.textGroupType)
//            var textStatus : TextView = view.findViewById(R.id.textStatus)
//            var cardViewParent : CardView = view.findViewById(R.id.cardViewParent)
            var imageViewBg  : ImageView = view.findViewById(R.id.imageViewBg)
            var textViewStatus : TextView = view.findViewById(R.id.textViewStatus)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.group_list_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textGroupName.text = groupList[position].gROUPNAME

            if(groupList[position].gROUPTYPE == 1){
                holder.textGroupType.text = "Student Group"
            }else if(groupList[position].gROUPTYPE == 2){
                holder.textGroupType.text = "Public Group"
            }

            if(groupList[position].gROUPSTATUS == 1){
             //   holder.cardViewParent.setCardBackgroundColor(context.resources.getColor(R.color.green_light500))
                Glide.with(context)
                    .load(context.resources.getDrawable(R.drawable.ic_approved))
                    .into(holder.imageViewBg)
                holder.textViewStatus.setTextColor(context.resources.getColor(R.color.fresh_green_200))
                holder.textViewStatus.text = "Published"
            }else{
              //  holder.cardViewParent.setCardBackgroundColor(context.resources.getColor(R.color.color100_physics))
                Glide.with(context)
                    .load(context.resources.getDrawable(R.drawable.ic_rejected))
                    .into(holder.imageViewBg)
                holder.textViewStatus.setTextColor(context.resources.getColor(R.color.fresh_red_200))
                holder.textViewStatus.text = "Unpublished"
            }

            holder.itemView.setOnClickListener {
                groupListener.onUpdateItem(groupList[position])
            }


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
           return groupList.size
        }

    }

    override fun onShowMessage(message: String) {
        Log.i(TAG,"onCreateClick ")
        Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)

    }

    override fun onCreateClick(url : String,submitItems: RequestBody?,successMsg : String, failerMsg : String) {

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
                                    initFunction("-1","-1")
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

    override fun onUpdateItem(groupItem: GroupListModel.Group) {

        bottomSheetUpdate = BottomSheetUpdateGroup(this,groupItem.gROUPID,groupItem.gROUPNAME!!,
            groupItem.gROUPTYPE,groupItem.gROUPSTATUS)
        bottomSheetUpdate.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onDeleteItem(gROUPID:Int){

        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure want to delete Group?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
               deleteFunction(gROUPID)
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
        groupViewModel.getDeleteGroupItem(gROUPID)
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
                                    initFunction("-1","-1")
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

interface GroupListener{
    fun onShowMessage(message: String)
    fun onCreateClick(url : String,submitItems: RequestBody?,successMsg : String, failerMsg : String)
    fun onUpdateItem(groupItem: GroupListModel.Group)

    fun onDeleteItem(gROUPID:Int)
}