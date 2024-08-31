package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.student_remarks.remarks

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
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Utils


@Suppress("DEPRECATION")
class BottomMarkRemarkRegister : BottomSheetDialogFragment {

    lateinit var remarkRegisterClicker: RemarkRegisterClicker
    var sTUDENTNAME = ""
    var remark = ArrayList<RemarkRegisterListModel.Remarks>()
    var position = 0
    var recyclerView: RecyclerView? = null

    private var _binding: BottomSheetRemarkRegisterBinding? = null
    private val binding get() = _binding!!



    var type = 0

    var editPassMark = ""
    var editOutOffMark = ""
    var editPassMarkInternal = ""
    var editOutOffMarkInternal = ""
    constructor()

    constructor(remark: ArrayList<RemarkRegisterListModel.Remarks>,
                position: Int,
                remarkRegisterClicker: RemarkRegisterClicker) {
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

        _binding = BottomSheetRemarkRegisterBinding.inflate(inflater, container, false)
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
        binding.editTextWork.inputType = InputType.TYPE_NULL
        binding.editTextWork.keyListener = null
        binding.editTextArt.inputType = InputType.TYPE_NULL
        binding.editTextArt.keyListener = null
        binding.editTextHealth.inputType = InputType.TYPE_NULL
        binding.editTextHealth.keyListener = null
        binding.editTextDiscipline.inputType = InputType.TYPE_NULL
        binding.editTextDiscipline.keyListener = null


        binding.editTextWork.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            type = 1
        }
        binding.editTextArt.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            type = 2
        }
        binding.editTextHealth.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            type = 3
        }
        binding.editTextDiscipline.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            type = 4
        }


        binding.ImageViewA.setOnClickListener {
            when (type) {
                1 -> {
                    binding.editTextWork.setText("A")
                }
                2 -> {
                    binding.editTextArt.setText("A")
                }
                3 -> {
                    binding.editTextHealth.setText("A")
                }
                4 -> {
                    binding.editTextDiscipline.setText("A")
                }
            }
        }

        binding.ImageViewB.setOnClickListener {
            when (type) {
                1 -> {
                    binding.editTextWork.setText("B")
                }
                2 -> {
                    binding.editTextArt.setText("B")
                }
                3 -> {
                    binding.editTextHealth.setText("B")
                }
                4 -> {
                    binding.editTextDiscipline.setText("B")
                }
            }
        }

        binding.ImageViewC.setOnClickListener {
            when (type) {
                1 -> {
                    binding.editTextWork.setText("C")
                }
                2 -> {
                    binding.editTextArt.setText("C")
                }
                3 -> {
                    binding.editTextHealth.setText("C")
                }
                4 -> {
                    binding.editTextDiscipline.setText("C")
                }
            }
        }

        binding.ImageViewD.setOnClickListener {
            when (type) {
                1 -> {
                    binding.editTextWork.setText("D")
                }
                2 -> {
                    binding.editTextArt.setText("D")
                }
                3 -> {
                    binding.editTextHealth.setText("D")
                }
                4 -> {
                    binding.editTextDiscipline.setText("D")
                }
            }
        }

        binding.ImageViewE.setOnClickListener {
            when (type) {
                1 -> {
                    binding.editTextWork.setText("E")
                }
                2 -> {
                    binding.editTextArt.setText("E")
                }
                3 -> {
                    binding.editTextHealth.setText("E")
                }
                4 -> {
                    binding.editTextDiscipline.setText("E")
                }
            }
        }



        binding.ImageBackSpace.setOnClickListener {

            if(type == 1){
                val text: String = binding.editTextWork.text.toString()
                val len = text.length
                if (len > 0) {
                    if (text == "ABS" || text == "NIL") {
                        binding.editTextWork.setText("")
                    } else {
                        binding.editTextWork.setText(text.substring(0, text.length - 1))
                    }
                }
            }else if(type == 2){
                val text: String = binding.editTextArt.text.toString()
                val len = text.length
                if (len > 0) {
                    if (text == "ABS" || text == "NIL") {
                        binding.editTextArt.setText("")
                    } else {
                        binding.editTextArt.setText(text.substring(0, text.length - 1))
                    }
                }
            }else if(type == 3){
                val text: String = binding.editTextHealth.text.toString()
                val len = text.length
                if (len > 0) {
                    if (text == "ABS" || text == "NIL") {
                        binding.editTextHealth.setText("")
                    } else {
                        binding.editTextHealth.setText(text.substring(0, text.length - 1))
                    }
                }
            }else if(type == 4){
                val text: String = binding.editTextDiscipline.text.toString()
                val len = text.length
                if (len > 0) {
                    if (text == "ABS" || text == "NIL") {
                        binding.editTextDiscipline.setText("")
                    } else {
                        binding.editTextDiscipline.setText(text.substring(0, text.length - 1))
                    }
                }
            }


        }

        binding.buttonSubmit.setOnClickListener {
            if (binding.editTextWork.text.toString().isNotEmpty() || binding.editTextArt.text.toString().isNotEmpty()
                || binding.editTextHealth.text.toString().isNotEmpty() || binding.editTextDiscipline.text.toString().isNotEmpty()){
                remarkRegisterClicker.onSubmitClickListener(binding.editTextWork.text.toString(),
                    binding.editTextArt.text.toString(),binding.editTextHealth.text.toString(),
                    binding.editTextDiscipline.text.toString(),remark[position],position)
            }else{
                remarkRegisterClicker.onMessageClick("Mark Field Cannot be empty")
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
        binding.editTextArt.clearFocus();
        binding.editTextHealth.clearFocus();
        binding.editTextDiscipline.clearFocus();

        binding.textViewName.text = remark[position].sTUDENTFNAME
        binding.textRollNo.text = "Roll.No : ${remark[position].sTUDENTROLLNUMBER}"

        if (remark[position].rEMARKCOLOUMN1 != "N") {
            binding.editTextWork.setText(remark[position].rEMARKCOLOUMN1)
        }else{
            binding.editTextWork.setText("")
        }

        if (remark[position].rEMARKCOLOUMN2 != "N") {
            binding.editTextArt.setText(remark[position].rEMARKCOLOUMN2)
        }else{
            binding.editTextArt.setText("")
        }
        if (remark[position].rEMARKCOLOUMN3 != "N") {
            binding.editTextHealth.setText(remark[position].rEMARKCOLOUMN3)
        }else{
            binding.editTextHealth.setText("")
        }
        if (remark[position].rEMARKCOLOUMN4 != "N") {
            binding.editTextDiscipline.setText(remark[position].rEMARKCOLOUMN4)
        }else{
            binding.editTextDiscipline.setText("")
        }
    }

    companion object {
        var TAG = "BottomSheetFragment"
    }
}