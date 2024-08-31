package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.student_remarks.student_remark_layout

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomMarkRegisterCeBinding
import info.passdaily.camrelconvertapp.databinding.BottomMarkRegisterMspsLpupBinding
import info.passdaily.camrelconvertapp.databinding.BottomSheetRemarkRegisterBinding
import info.passdaily.camrelconvertapp.databinding.BottomSheetStudentRemarkBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Utils


@Suppress("DEPRECATION")
class BottomMarkStudentRemark : BottomSheetDialogFragment {

    lateinit var remarkRegisterClicker: StuRemarkRegisterClicker
    var sTUDENTNAME = ""
    var remark = ArrayList<StudentRemarksModel.Remarks>()
    var position = 0
    var recyclerView: RecyclerView? = null

    private var _binding: BottomSheetStudentRemarkBinding? = null
    private val binding get() = _binding!!



    var type = 0

    var editPassMark = ""
    var editOutOffMark = ""
    var editPassMarkInternal = ""
    var editOutOffMarkInternal = ""
    constructor()

    constructor(remark: ArrayList<StudentRemarksModel.Remarks>,
                position: Int,
                remarkRegisterClicker: StuRemarkRegisterClicker) {
        this.remarkRegisterClicker = remarkRegisterClicker
        this.remark = remark
        this.position = position
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

        _binding = BottomSheetStudentRemarkBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        hashMapArrayList= new ArrayList <>();
//        binding.textViewName.text = mark[position].sTUDENTFNAME
//
//        if (mark[position].gRADETE != "N") {
//            binding.editTextCE.setText(mark[position].gRADECE)
//        }
//
//        if (mark[position].gRADECE != "N") {
//            binding.editTextTE.setText(mark[position].gRADETE)
//        }

        binding.buttonSubmit.setOnClickListener {
            if (binding.editTextWork.text.toString().isNotEmpty()){
                remarkRegisterClicker.onSubmitClickListener(binding.editTextWork.text.toString(),remark[position],position)
            }else{
                remarkRegisterClicker.onMessageClick("Remark Field Cannot be empty")
            }
        }

        binding.imageViewLeft.setOnClickListener {
            if (position <= 0) {
                remarkRegisterClicker.onMessageClick("No Previous Student")
            } else {
                position--
                initShown(position)
            }
        }



        binding.imageViewRight.setOnClickListener {
            if ((position + 1) == remark.size) {
                remarkRegisterClicker.onMessageClick("Student Ends Here")
            } else {
                position++
                initShown(position)
            }
        }

        initShown(position)
    }

    fun initShown(position : Int){

        binding.editTextWork.clearFocus();

        binding.textViewName.text = remark[position].sTUDENTFNAME
        binding.textRollNo.text = "Roll.No : ${remark[position].sTUDENTROLLNUMBER}"

        if (remark[position].sTUDENTEXTRA != null) {
            binding.editTextWork.setText(remark[position].sTUDENTEXTRA.toString())
        }else{
            binding.editTextWork.setText("")
        }
    }

    companion object {
        var TAG = "BottomSheetFragment"
    }
}