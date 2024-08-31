package info.passdaily.camrelconvertapp.typeofuser.parent.leave_enquires

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentLibraryBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class LeaveFragment : Fragment(),LeaveClickListener {

    var TAG = "LeaveFragment"

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    var recyclerViewLeaveList: RecyclerView? = null

    var constraintLayout : ConstraintLayout? = null

    private lateinit var leaveViewModel: LeaveEnquiryViewModel

    var fab: ExtendedFloatingActionButton? = null

    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null

    var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext;
        }
        Log.i(TAG, "onAttach ")

    }

    var shimmerViewContainer : ShimmerFrameLayout? =null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.currentPage = 11
        Global.screenState = "landingpage"
        // Inflate the layout for this fragment
//        leaveViewModel = ViewModelProvider(this).get(LeaveEnquiryViewModel::class.java)
//        return inflater.inflate(R.layout.fragment_library, container, false)

        leaveViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[LeaveEnquiryViewModel::class.java]

        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var studentDBHelper = StudentDBHelper(requireActivity())
        var student = studentDBHelper.getProductById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID
        STUDENT_ROLL_NO = student.STUDENT_ROLL_NO

        constraintLayout = binding.constraintLayout

        var textViewTitle = binding.textView32
        textViewTitle.text = "Leave"
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        if (isAdded) {
            Glide.with(mContext!!)
                .load(R.drawable.ic_empty_state_assignment)
                .into(imageViewEmpty!!)
        }
        textEmpty?.text = "No Leave"

        fab = binding.fab
        fab?.visibility = View.VISIBLE
        fab?.setOnClickListener {
            val dialog1 = CreateLeaveDialog(this)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, CreateLeaveDialog.TAG)
        }


//        leaveViewModel.getLeaveList(ACADEMICID, CLASSID, STUDENTID, STUDENT_ROLL_NO,"","")
//        val leaveMap: ArrayList<Leave> = ArrayList<Leave>()
//        val leave = Leave()
//        leave.ACCADEMIC_ID = ACADEMICID
//        leave.CLASSID = CLASSID
//        leave.STUDENTID = STUDENTID
//        leave.STUDENT_ROLL_NO = STUDENT_ROLL_NO
//        leave.LEAVE_SUBMITTED_DATE = ""
//        leave.TODate = ""
//        leaveMap.add(leave)

//        val hashMap:HashMap<String,String> = HashMap<String,String>(3)
//        hashMap["ACCADEMIC_ID"] = ACADEMICID.toString()
//        hashMap["CLASSID"] = CLASSID.toString()
//        hashMap["STUDENTID"] = STUDENTID.toString()
//        hashMap["STUDENT_ROLL_NO"] = STUDENT_ROLL_NO.toString()
//        hashMap["LEAVE_SUBMITTED_DATE"] = ""
//        hashMap["TODate"] = ""


        shimmerViewContainer = binding.shimmerViewContainer

        recyclerViewLeaveList = binding.recyclerView
        recyclerViewLeaveList?.layoutManager = LinearLayoutManager(requireActivity())

        intiFunction()
    }

    //    {
//        "ACCADEMIC_ID":"7",
//        "CLASS_ID":"2",
//        "STUDENT_ID":"1",
//        "STUDENT_ROLL_NUMBER":"1",
//        "LEAVE_SUBMITTED_DATE":"",
//        "TODate":""
//
//    }

    private fun intiFunction() {

        val jsonObject = JSONObject()
        try {
            jsonObject.put("ACCADEMIC_ID", ACADEMICID)
            jsonObject.put("CLASS_ID", CLASSID)
            jsonObject.put("STUDENT_ID", STUDENTID)
            jsonObject.put("STUDENT_ROLL_NUMBER", STUDENT_ROLL_NO)
            jsonObject.put("LEAVE_SUBMITTED_DATE", "")
            jsonObject.put("TODate", "")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject ${jsonObject.toString()}")

        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        leaveViewModel.getLeaveList(accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if(response.leaveDetails.isNotEmpty()){
                                constraintEmpty?.visibility = View.GONE
                                shimmerViewContainer?.visibility = View.GONE
                                recyclerViewLeaveList?.visibility = View.VISIBLE
                                if (isAdded) {
                                    recyclerViewLeaveList?.adapter =
                                        LeaveAdapter(this,response.leaveDetails,requireActivity())
                                }
                            }else{
                                constraintEmpty?.visibility = View.VISIBLE
                                recyclerViewLeaveList?.visibility = View.GONE
                                shimmerViewContainer?.visibility = View.GONE
                                if (isAdded) {
                                    Glide.with(mContext!!)
                                        .load(R.drawable.ic_empty_state_absent)
                                        .into(imageViewEmpty!!)
                                }
                                textEmpty?.text = requireActivity().resources.getString(R.string.no_results)
                            }
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewLeaveList?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_no_internet)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text = requireActivity().resources.getString(R.string.no_internet)
                        }
                        Status.LOADING -> {
                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_empty_state_absent)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text =  requireActivity().resources.getString(R.string.loading)
                            shimmerViewContainer?.visibility = View.VISIBLE
                            recyclerViewLeaveList?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                        }
                    }
                }
            })
//        leaveViewModel.getLeaveListObservable().observe(requireActivity(), {
//
//            if (it != null) {
//                if (it.leaveDetails.isNotEmpty()) {
//                    constraintEmpty?.visibility = View.GONE
//                    recyclerViewLeaveList?.visibility = View.VISIBLE
//                    if (isAdded) {
//                        recyclerViewLeaveList?.adapter =
//                            LeaveAdapter(it.leaveDetails, mContext!!)
//                    }
//                } else {
//                    constraintEmpty?.visibility = View.VISIBLE
//                    recyclerViewLeaveList?.visibility = View.GONE
//                }
//            }
//        })
    }

    class LeaveAdapter(
        var leaveClickListener: LeaveClickListener,
        var leaveDetails: List<LeaveDetailsModel.LeaveDetail>,
        var context: Activity
    ) : RecyclerView.Adapter<LeaveAdapter.ViewHolder>() {
        var leaveSubmitDate = ""
        var leaveFromDate = ""
        var leaveToDate = ""

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textLeaveTitle: TextView = view.findViewById(R.id.textLeaveTitle)
            var textLeaveDesc: TextView = view.findViewById(R.id.textLeaveDesc)

            var textViewDate: TextView = view.findViewById(R.id.textViewDate)

            var cardViewStatus: CardView = view.findViewById(R.id.cardViewStatus)
            val textViewStatus: TextView = view.findViewById(R.id.textViewStatus)

            var textViewFrom : TextView = view.findViewById(R.id.textViewFrom)
            var textViewTo : TextView = view.findViewById(R.id.textViewTo)
            var imageViewEdit  : ImageView = view.findViewById(R.id.imageViewEdit)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveAdapter.ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.leave_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textLeaveTitle.text = leaveDetails[position].lEAVESUBJECT
            holder.textLeaveDesc.text = leaveDetails[position].lEAVEDESCRIPTION
            var query = leaveDetails[position].lEAVESTATUS
            when (query) {
                0 -> {
                    holder.textViewStatus.text = "In Progress"
                    holder.textViewStatus.setTextColor(context.resources.getColor(R.color.color_chemistry))
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_chemistry))
                    holder.imageViewEdit.visibility = View.VISIBLE
                }
                1 -> {
                    holder.textViewStatus.text = "Approved"
                    holder.textViewStatus.setTextColor(context.resources.getColor(R.color.green_400))
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.green_100))
                    holder.imageViewEdit.visibility = View.GONE
                }
                3 -> {
                    holder.textViewStatus.text = "Rejected"
                    holder.textViewStatus.setTextColor(context.resources.getColor(R.color.color_physics))
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_physics))
                    holder.imageViewEdit.visibility = View.GONE
                }
            }
            holder.imageViewEdit.setOnClickListener {
                val popupMenu = PopupMenu(context, holder.imageViewEdit)
                popupMenu.menuInflater.inflate(R.menu.menu_video, popupMenu.menu)
                popupMenu.menu.findItem(R.id.menu_edit).isVisible = true
                popupMenu.menu.findItem(R.id.menu_video).title = "Delete"
                popupMenu.menu.findItem(R.id.menu_offline_video).isVisible = false
                popupMenu.menu.findItem(R.id.menu_report).isVisible = false
                popupMenu.show()
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit -> {
                            leaveClickListener.onUpdateClick(
                                leaveDetails[position].lEAVEFROMDATE,
                                leaveDetails[position].lEAVETODATE,
                                leaveDetails[position].lEAVESUBJECT,
                                leaveDetails[position].lEAVEDESCRIPTION,
                                leaveDetails[position].lEAVEID
                            )
                            true
                        }
                        R.id.menu_video -> {
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("Are you sure want to delete ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes") { dialog, _ ->
                                    leaveClickListener.onDeleteClick(leaveDetails[position].lEAVEID)
                                    dialog.dismiss()
                                }
                                .setNegativeButton(
                                    "No"
                                ) { dialog, id -> //  Action for 'NO' Button
                                    dialog.cancel()
                                }
                            //Creating dialog box
                            val alert = builder.create()
                            //Setting the title manually
                            alert.setTitle("Logout?")
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

            }

            if (!leaveDetails[position].lEAVESUBMITTEDDATE.isNullOrBlank()) {
                val date1: Array<String> =
                    leaveDetails[position].lEAVESUBMITTEDDATE.split("T".toRegex()).toTypedArray()
                val ddddd: Long = Utils.longconversion(date1[0] + " " + date1[1])
                leaveSubmitDate = Utils.formattedDateTime(ddddd)
            }
            holder.textViewDate.text = leaveSubmitDate

            if (!leaveDetails[position].lEAVEFROMDATE.isNullOrBlank()) {
                val date1: Array<String> =
                    leaveDetails[position].lEAVEFROMDATE.split("T".toRegex()).toTypedArray()
                val ddddd: Long = Utils.longconversion(date1[0] + " " + date1[1])
                leaveFromDate = Utils.formattedDateWords(ddddd)
            }
            holder.textViewFrom.text = "From : $leaveFromDate"

            if (!leaveDetails[position].lEAVETODATE.isNullOrBlank()) {
                val date1: Array<String> =
                    leaveDetails[position].lEAVETODATE.split("T".toRegex()).toTypedArray()
                val ddddd: Long = Utils.longconversion(date1[0] + " " + date1[1])
                leaveToDate = Utils.formattedDateWords(ddddd)
            }
            holder.textViewTo.text = "To : $leaveToDate"

        }

        override fun getItemCount(): Int {
            return leaveDetails.size
        }

    }




    override fun onCreateClick(message : String) {
        Log.i(TAG,"onFinishClick")
        intiFunction()
        Utils.getSnackBarGreen(requireActivity(),message,constraintLayout!!)
    }

    override fun onUpdateClick(fromDate : String,
                               toDate : String,
                               title : String,
                               description : String,
                               leaveId: Int) {
        val dialog1 = UpdateLeaveDialog(this,fromDate,
            toDate,title,description,leaveId)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.show(transaction, UpdateLeaveDialog.TAG)
    }
    override fun onDeleteClick(leaveId: Int) {

        val url = "Leave/LeaveDelete"
        leaveViewModel.getDeleteLeaveFun(url, leaveId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            intiFunction()
                            Utils.getSnackBarGreen(requireActivity(),"Delete Successfully",constraintLayout!!)
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })
    }

}

interface LeaveClickListener{
    fun onCreateClick(message : String)
    fun onUpdateClick(
        fromDate : String,
        toDate : String,
        title : String,
        description : String,
        leaveId: Int
    )
    fun onDeleteClick(leaveId: Int)
}