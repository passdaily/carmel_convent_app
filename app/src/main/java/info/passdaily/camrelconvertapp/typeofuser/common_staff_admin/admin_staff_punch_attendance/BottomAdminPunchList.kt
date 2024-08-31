package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.admin_staff_punch_attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.imageview.ShapeableImageView
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.AdminFragmentStaffAttendanceBinding
import info.passdaily.camrelconvertapp.databinding.BottomAdminStaffPunchReportBinding
import info.passdaily.camrelconvertapp.databinding.BottomSheetUpdateStaffBinding
import info.passdaily.camrelconvertapp.model.AdminPunchingOperationModel
import info.passdaily.camrelconvertapp.model.GuardianListModel
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.manage_guardian.GuardianListener
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_staff.StaffViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.staff_punch_attendance.StaffPunchViewModel

class BottomAdminPunchList  : BottomSheetDialogFragment {

    var TAG = "BottomAdminPunchList"
    private lateinit var staffPunchViewModel: StaffPunchViewModel
    private var _binding: BottomAdminStaffPunchReportBinding? = null
    private val binding get() = _binding!!


    lateinit var punchingAction : AdminPunchingOperationModel.PunchingAction
    lateinit var punchingHistory: ArrayList<AdminPunchingOperationModel.PunchingHistory>

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var adminRole = 0
    var schoolId = 0

    var shapeImageView : ShapeableImageView? = null
    var textViewStaff  : TextView? = null
    var recyclerList : RecyclerView? = null
    var textViewNoReport : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    constructor()

    constructor(punchingAction: AdminPunchingOperationModel.PunchingAction,
                punchingHistory: ArrayList<AdminPunchingOperationModel.PunchingHistory>) {
        this.punchingAction = punchingAction
        this.punchingHistory = punchingHistory
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
        adminRole = user[0].ADMIN_ROLE
        schoolId = user[0].SCHOOL_ID
        // Inflate the layout for this fragment
        staffPunchViewModel = ViewModelProviders.of(
            requireActivity(),
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[StaffPunchViewModel::class.java]


        _binding = BottomAdminStaffPunchReportBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        shapeImageView  = binding.shapeImageView
        textViewStaff  = binding.textViewStaff
        recyclerList  = binding.recyclerList
        textViewNoReport = binding.textViewNoReport


        if (punchingAction.sTAFFIMAGE != "") {
            Glide.with(requireActivity()).load(
                Global.event_url + "/Photos/StaffImage/" + punchingAction.sTAFFIMAGE
            ) //STAFF_IMAGE -> http://demo.passdaily.in/Photos/StaffImageA0D181192F902C6AE338BEDF36FC3251.jpg
                //STAFF_IMAGE -> 1A07304FC14301B29E49B4DA301B0EA5.png
                .apply(
                    RequestOptions.centerCropTransform()
                        .dontAnimate()
                        .placeholder(R.drawable.round_account_button_with_user_inside)
                )
                .thumbnail(0.5f)
                .into(shapeImageView!!)
        }
        textViewStaff!!.text = punchingAction.sTAFFFNAME


    }

}