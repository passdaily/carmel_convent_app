package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.mark_register

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomMarkRegisterCeBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Utils


@Suppress("DEPRECATION")
class BottomMarkRegisterCE : BottomSheetDialogFragment {

    lateinit var markRegisterCEClicker: MarkRegisterCEClicker
    var sTUDENTNAME = ""
    var mark = ArrayList<MarkRegisterMsesLpUpModel.Mark>()
    var position = 0
    var recyclerView: RecyclerView? = null

    private var _binding: BottomMarkRegisterCeBinding? = null
    private val binding get() = _binding!!

    var type = 0

    var editPassMark = ""
    var editOutOffMark = ""
    var editPassMarkInternal = ""
    var editOutOffMarkInternal = ""
    constructor()

    constructor(editPassMark : String,
                editOutOffMark : String,
                editPassMarkInternal : String,
                editOutOffMarkInternal : String,
                mark: ArrayList<MarkRegisterMsesLpUpModel.Mark>,
                position: Int,
                markRegisterCEClicker: MarkRegisterCEClicker) {
        this.editPassMark = editPassMark
        this.editOutOffMark = editOutOffMark
        this.editPassMarkInternal = editPassMarkInternal
        this.editOutOffMarkInternal = editOutOffMarkInternal
        this.markRegisterCEClicker = markRegisterCEClicker
        this.mark = mark
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

        _binding = BottomMarkRegisterCeBinding.inflate(inflater, container, false)
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
        binding.editTextExternal.inputType = InputType.TYPE_NULL
        binding.editTextExternal.keyListener = null
        binding.editTextInternal.inputType = InputType.TYPE_NULL
        binding.editTextInternal.keyListener = null



        binding.editTextExternal.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            type = 1
        }

        binding.editTextInternal.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            type = 2
        }


        binding.ImageView1.setOnClickListener {

            if(type == 1){
                var t1 = binding.editTextExternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "1"
                    binding.editTextExternal.setText(t1)
                } else {
                    t1 += "1"
                    binding.editTextExternal.setText(t1)
                }
            }else if(type == 2){
                var t1 = binding.editTextInternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "1"
                    binding.editTextInternal.setText(t1)
                } else {
                    t1 += "1"
                    binding.editTextInternal.setText(t1)
                }
            }
        }

        binding.ImageView2.setOnClickListener {
            if(type == 1){
                var t1 = binding.editTextExternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "2"
                    binding.editTextExternal.setText(t1)
                } else {
                    t1 += "2"
                    binding.editTextExternal.setText(t1)
                }
            }else if(type == 2){
                var t1 = binding.editTextInternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "2"
                    binding.editTextInternal.setText(t1)
                } else {
                    t1 += "2"
                    binding.editTextInternal.setText(t1)
                }
            }
        }

        binding.ImageView3.setOnClickListener {
            if(type == 1){
                var t1 = binding.editTextExternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "3"
                    binding.editTextExternal.setText(t1)
                } else {
                    t1 += "3"
                    binding.editTextExternal.setText(t1)
                }
            }else if(type == 2){
                var t1 = binding.editTextInternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "3"
                    binding.editTextInternal.setText(t1)
                } else {
                    t1 += "3"
                    binding.editTextInternal.setText(t1)
                }
            }
        }

        binding.ImageView4.setOnClickListener {
            if(type == 1){
                var t1 = binding.editTextExternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "4"
                    binding.editTextExternal.setText(t1)
                } else {
                    t1 += "4"
                    binding.editTextExternal.setText(t1)
                }
            }else if(type == 2){
                var t1 = binding.editTextInternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "4"
                    binding.editTextInternal.setText(t1)
                } else {
                    t1 += "4"
                    binding.editTextInternal.setText(t1)
                }
            }
        }

        binding.ImageView5.setOnClickListener {
            if(type == 1){
                var t1 = binding.editTextExternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "5"
                    binding.editTextExternal.setText(t1)
                } else {
                    t1 += "5"
                    binding.editTextExternal.setText(t1)
                }
            }else if(type == 2){
                var t1 = binding.editTextInternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "5"
                    binding.editTextInternal.setText(t1)
                } else {
                    t1 += "5"
                    binding.editTextInternal.setText(t1)
                }
            }
        }

        binding.ImageView6.setOnClickListener {
            if(type == 1){
                var t1 = binding.editTextExternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "6"
                    binding.editTextExternal.setText(t1)
                } else {
                    t1 += "6"
                    binding.editTextExternal.setText(t1)
                }
            }else if(type == 2){
                var t1 = binding.editTextInternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "6"
                    binding.editTextInternal.setText(t1)
                } else {
                    t1 += "6"
                    binding.editTextInternal.setText(t1)
                }
            }
        }

        binding.ImageView7.setOnClickListener {
            if(type == 1){
                var t1 = binding.editTextExternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "7"
                    binding.editTextExternal.setText(t1)
                } else {
                    t1 += "7"
                    binding.editTextExternal.setText(t1)
                }
            }else if(type == 2){
                var t1 = binding.editTextInternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "7"
                    binding.editTextInternal.setText(t1)
                } else {
                    t1 += "7"
                    binding.editTextInternal.setText(t1)
                }
            }
        }

        binding.ImageView8.setOnClickListener {
            if(type == 1){
                var t1 = binding.editTextExternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "8"
                    binding.editTextExternal.setText(t1)
                } else {
                    t1 += "8"
                    binding.editTextExternal.setText(t1)
                }
            }else if(type == 2){
                var t1 = binding.editTextInternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "8"
                    binding.editTextInternal.setText(t1)
                } else {
                    t1 += "8"
                    binding.editTextInternal.setText(t1)
                }
            }
        }

        binding.ImageView9.setOnClickListener {
            if(type == 1){
                var t1 = binding.editTextExternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "9"
                    binding.editTextExternal.setText(t1)
                } else {
                    t1 += "9"
                    binding.editTextExternal.setText(t1)
                }
            }else if(type == 2){
                var t1 = binding.editTextInternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "9"
                    binding.editTextInternal.setText(t1)
                } else {
                    t1 += "9"
                    binding.editTextInternal.setText(t1)
                }
            }
        }

        binding.ImageView0.setOnClickListener {
            if(type == 1){
                var t1 = binding.editTextExternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "0"
                    binding.editTextExternal.setText(t1)
                } else {
                    t1 += "0"
                    binding.editTextExternal.setText(t1)
                }
            }else if(type == 2){
                var t1 = binding.editTextInternal.text.toString()
                if (t1 == "ABS" || t1 == "NIL") {
                    t1 = "0"
                    binding.editTextInternal.setText(t1)
                } else {
                    t1 += "0"
                    binding.editTextInternal.setText(t1)
                }
            }
        }

        binding.ImageBackSpace.setOnClickListener {

            if(type == 1){
                val text: String = binding.editTextExternal.text.toString()
                val len = text.length
                if (len > 0) {
                    if (text == "ABS" || text == "NIL") {
                        binding.editTextExternal.setText("")
                    } else {
                        binding.editTextExternal.setText(text.substring(0, text.length - 1))
                    }
                }
            }else if(type == 2){
                val text: String = binding.editTextInternal.text.toString()
                val len = text.length
                if (len > 0) {
                    if (text == "ABS" || text == "NIL") {
                        binding.editTextInternal.setText("")
                    } else {
                        binding.editTextInternal.setText(text.substring(0, text.length - 1))
                    }
                }
            }


        }

        binding.ImageViewABS.setOnClickListener {

            if(type == 1){
                binding.editTextExternal.setText("ABS")
            }
//            else if(type == 2){
//                binding.editTextInternal.setText("ABS")
//            }

        }


        binding.ImageViewNil.setOnClickListener {
            if(type == 1){
                binding.editTextExternal.setText("NIL")
            }
//            else if(type == 2){
//                binding.editTextInternal.setText("NIL")
//            }
        }

        binding.buttonSubmit.setOnClickListener {
            var isTested = false
            var isTestedE = false
            var _editTextInternal = ""
            var _editTextExternal = ""
            if (/*(editPassMark != "N" && editPassMark.isNotEmpty())*/
                //(editOutOffMark != "N" && editOutOffMark.isNotEmpty())
                /*( editPassMarkInternal != "N" && editPassMarkInternal.isNotEmpty()  )*/
              //  && (editOutOffMarkInternal != "N" && editOutOffMarkInternal.isNotEmpty())
                validateField(editOutOffMark,"Out-Off mark must have some values")
                && validateField(editOutOffMarkInternal,"Out-Off Internal mark must have some values")
                && validateField(binding.editTextExternal.text.toString(),"Give values to External Mark field")
                && validateField(binding.editTextInternal.text.toString(),"Give values to Internal Mark field")) {
           //     if ((editPassMark.toInt() <= editOutOffMark.toInt()) && (editPassMarkInternal.toInt() <= editOutOffMarkInternal.toInt())) {

                    if (binding.editTextExternal.text.toString() != "ABS" && binding.editTextExternal.text.toString() != "NIL") {
                        if (binding.editTextExternal.text.toString().toInt() <= editOutOffMark.toInt()) {
                            _editTextExternal = binding.editTextExternal.text.toString()
                            isTestedE = true
                        } else {
                            isTestedE = false
                            markRegisterCEClicker.onMessageClick("Given Value is above Out-Off Mark")
                        }
                    } else if (binding.editTextExternal.text.toString() == "ABS" || binding.editTextExternal.text.toString() == "NIL") {
                        isTestedE = true
                        _editTextExternal = binding.editTextExternal.text.toString()
                    }

                    if (binding.editTextInternal.text.toString() != "ABS" && binding.editTextInternal.text.toString() != "NIL") {
                        if (binding.editTextInternal.text.toString().toInt() <= editOutOffMarkInternal.toInt()) {
                            _editTextInternal = binding.editTextInternal.text.toString()
                            isTested = true
                        } else {
                            isTested = false
                            markRegisterCEClicker.onMessageClick("Given Value is above Out-Off Internal Mark")
                        }
                    } else if (binding.editTextInternal.text.toString() == "ABS" || binding.editTextInternal.text.toString() == "NIL") {
                        isTested = true
                        _editTextInternal = binding.editTextInternal.text.toString()
                    }

                    if(isTested && isTestedE) {
                        markRegisterCEClicker.onSubmitClickListener(
                            _editTextExternal,
                            _editTextInternal,
                            mark[position],
                            position
                        )
                    }else{
                        markRegisterCEClicker.onMessageClick("Please Check Inputs")
                    }

//                }else{
//                    markRegisterCEClicker.onMessageClick("Pass mark is greater then Out-Off Mark")
//                }
            }
//            else{
//                markRegisterCEClicker.onMessageClick("Out-Off mark must have some values")
//            }
        }

        binding.imageViewLeft.setOnClickListener {
            if (position <= 0) {
                markRegisterCEClicker.onMessageClick("No Previous Student")
            } else {
                position--
                initShown(position)
            }
        }



        binding.imageViewRight.setOnClickListener {
            if ((position + 1) == mark.size) {
                markRegisterCEClicker.onMessageClick("Student Ends Here")
            } else {
                position++
                initShown(position)
            }
        }

        initShown(position)
    }

    fun validateField(edtField: String, message: String): Boolean {
        return if (Utils.validateFieldIsEmpty(edtField) || edtField =="N") {
            markRegisterCEClicker.onMessageClick(message)
            false
        } else {
            true
        }
    }

    fun initShown(position : Int){

        binding.editTextExternal.clearFocus();
        binding.editTextInternal.clearFocus();

        binding.textViewName.text = mark[position].sTUDENTFNAME

        if (mark[position].tOTALMARKINTERNAL != "N") {
            binding.editTextInternal.setText(mark[position].tOTALMARKINTERNAL)
        }else{
            binding.editTextInternal.setText("")
        }

        if (mark[position].tOTALMARKTHEORY != "N") {
            binding.editTextExternal.setText(mark[position].tOTALMARKTHEORY)
        }else{
            binding.editTextExternal.setText("")
        }
    }

    companion object {
        var TAG = "BottomSheetFragment"
    }
}