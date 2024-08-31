package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.mark_register_msps

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
import info.passdaily.camrelconvertapp.databinding.BottomMarkRegisterMspsHsBinding
import info.passdaily.camrelconvertapp.databinding.BottomMarkRegisterMspsLpupBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Utils


@Suppress("DEPRECATION")
class BottomMarkRegisterMspsHs : BottomSheetDialogFragment {

    lateinit var markRegisterMspsClicker: MarkRegisterMspsClicker
    var sTUDENTNAME = ""
    var mark = ArrayList<MarkRegisterMspsLpUpModel.Mark>()
    var position = 0
    var recyclerView: RecyclerView? = null

    private var _binding: BottomMarkRegisterMspsHsBinding? = null
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
                mark: ArrayList<MarkRegisterMspsLpUpModel.Mark>,
                position: Int,
                markRegisterMspsClicker: MarkRegisterMspsClicker) {
        this.editPassMark = editPassMark
        this.editOutOffMark = editOutOffMark
        this.editPassMarkInternal = editPassMarkInternal
        this.editOutOffMarkInternal = editOutOffMarkInternal
        this.markRegisterMspsClicker = markRegisterMspsClicker
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

        _binding = BottomMarkRegisterMspsHsBinding.inflate(inflater, container, false)
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
        binding.editTextPT.inputType = InputType.TYPE_NULL
        binding.editTextPT.keyListener = null
        binding.editTextMAA.inputType = InputType.TYPE_NULL
        binding.editTextMAA.keyListener = null
        binding.editTextPF.inputType = InputType.TYPE_NULL
        binding.editTextPF.keyListener = null
        binding.editTextSEA.inputType = InputType.TYPE_NULL
        binding.editTextSEA.keyListener = null


        binding.editTextPT.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            type = 1
        }
        binding.editTextMAA.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            type = 2
        }
        binding.editTextPF.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            type = 3
        }
        binding.editTextSEA.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            type = 4
        }
        binding.editTextExternal.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            type = 5
        }


        binding.ImageView1.setOnClickListener {
            onClickNumber("1",type)
        }

        binding.ImageView2.setOnClickListener {
            onClickNumber("2",type)
        }

        binding.ImageView3.setOnClickListener {
            onClickNumber("3",type)
        }

        binding.ImageView4.setOnClickListener {
            onClickNumber("4",type)
        }

        binding.ImageView5.setOnClickListener {
            onClickNumber("5",type)
        }

        binding.ImageView6.setOnClickListener {
            onClickNumber("6",type)
        }

        binding.ImageView7.setOnClickListener {
            onClickNumber("7",type)
        }

        binding.ImageView8.setOnClickListener {
            onClickNumber("8",type)
        }

        binding.ImageView9.setOnClickListener {
            onClickNumber("9",type)
        }

        binding.ImageView0.setOnClickListener {
            onClickNumber("0",type)
        }

        binding.ImageViewDot.setOnClickListener {
            onClickNumber(".",type)
        }

        binding.ImageBackSpace.setOnClickListener {

            if(type == 1){
                val text: String = binding.editTextPT.text.toString()
                val len = text.length
                if (len > 0) {
                    if (text == "ABS" || text == "NIL") {
                        binding.editTextPT.setText("")
                    } else {
                        binding.editTextPT.setText(text.substring(0, text.length - 1))
                    }
                }
            }else if(type == 2){
                val text: String = binding.editTextMAA.text.toString()
                val len = text.length
                if (len > 0) {
                    if (text == "ABS" || text == "NIL") {
                        binding.editTextMAA.setText("")
                    } else {
                        binding.editTextMAA.setText(text.substring(0, text.length - 1))
                    }
                }
            }
            else if(type == 3){
                val text: String = binding.editTextPF.text.toString()
                val len = text.length
                if (len > 0) {
                    if (text == "ABS" || text == "NIL") {
                        binding.editTextPF.setText("")
                    } else {
                        binding.editTextPF.setText(text.substring(0, text.length - 1))
                    }
                }
            }else if(type == 4){
                val text: String = binding.editTextSEA.text.toString()
                val len = text.length
                if (len > 0) {
                    if (text == "ABS" || text == "NIL") {
                        binding.editTextSEA.setText("")
                    } else {
                        binding.editTextSEA.setText(text.substring(0, text.length - 1))
                    }
                }
            }else if(type == 5){
                val text: String = binding.editTextExternal.text.toString()
                val len = text.length
                if (len > 0) {
                    if (text == "ABS" || text == "NIL") {
                        binding.editTextExternal.setText("")
                    } else {
                        binding.editTextExternal.setText(text.substring(0, text.length - 1))
                    }
                }
            }


        }

        binding.ImageViewABS.setOnClickListener {
            if(type == 5){
                binding.editTextExternal.setText("ABS")
            }
        }


        binding.ImageViewNil.setOnClickListener {
            if(type == 5){
                binding.editTextExternal.setText("NIL")
            }
        }

        binding.buttonSubmit.setOnClickListener {
            var isTestedPF = false
            var isTestedMAA = false
            var isTestedPT = false
            var isTestedSEA = false
            var isTestedE = false
            var _editTextPT = ""
            var _editTextMAA = ""
            var _editTextPF = ""
            var _editTextSEA = ""
            var _editTextExternal = ""
            var message = ""
            if (/*(editPassMark != "N" && editPassMark.isNotEmpty())  && */
                //(editOutOffMark != "N" && editOutOffMark.isNotEmpty())
               /* && (editPassMarkInternal != "N" && editPassMarkInternal.isNotEmpty()) */
               // && (editOutOffMarkInternal != "N" && editOutOffMarkInternal.isNotEmpty())
                validateField(editOutOffMark,"Out-Off mark must have some values")
                && validateField(editOutOffMarkInternal,"Out-Off Internal mark must have some values")
                && validateField(binding.editTextPT.text.toString(),"Give values to PT Field")
                && validateField(binding.editTextMAA.text.toString(),"Give values to MAA Field")
                && validateField(binding.editTextPF.text.toString(),"Give values to PF Field")
                && validateField(binding.editTextSEA.text.toString(),"Give values to SEA Field")
                && validateField(binding.editTextExternal.text.toString(),"Give values to External Field")
            ) {


                if(/*Utils.isValidInputPoint(editPassMark) && */Utils.isValidInputPoint(editOutOffMark)) {
                    if(/*Utils.isValidInputPoint(editPassMarkInternal) && */Utils.isValidInputPoint(editOutOffMarkInternal)) {

                        if(Utils.isValidInputPoint(binding.editTextPT.text.toString())
                            && Utils.isValidInputPoint(binding.editTextMAA.text.toString())
                            && Utils.isValidInputPoint(binding.editTextPF.text.toString())
                            && Utils.isValidInputPoint(binding.editTextSEA.text.toString())
                            &&Utils.isValidInputPoint(binding.editTextExternal.text.toString())) {

//                            if ((editPassMark.toDouble() <= editOutOffMark.toDouble()) &&
//                                (editPassMarkInternal.toDouble() <= editOutOffMarkInternal.toDouble())) {

                                if (binding.editTextPT.text.toString() != "ABS" && binding.editTextPT.text.toString() != "NIL") {
                                    if (binding.editTextPT.text.toString().toDouble() <= editOutOffMarkInternal.toDouble()) {

                                        _editTextPT = binding.editTextPT.text.toString()
                                        isTestedPT = true
                                    } else {
                                        isTestedPT = false
                                    //    markRegisterMspsClicker.onMessageClick("Given Value is above Out-Off CE Mark")
                                        message = "PT Mark is above Out-Off Mark CE"
                                    }
                                }
                                else if (binding.editTextPT.text.toString() == "ABS" || binding.editTextPT.text.toString() == "NIL") {
                                    isTestedPT = true
                                    _editTextPT = binding.editTextPT.text.toString()
                                }

                                if (binding.editTextMAA.text.toString() != "ABS" && binding.editTextMAA.text.toString() != "NIL") {
                                    if (binding.editTextMAA.text.toString().toDouble() <= editOutOffMarkInternal.toDouble()) {

                                        _editTextMAA = binding.editTextMAA.text.toString()
                                        isTestedMAA = true
                                    } else {
                                        isTestedMAA = false
                                        //   markRegisterMspsClicker.onMessageClick("Given Value is above Out-Off CE Mark")
                                        message = "MAA Mark is above Out-Off Mark CE"
                                    }
                                }
                                else if (binding.editTextMAA.text.toString() == "ABS" || binding.editTextMAA.text.toString() == "NIL") {
                                    isTestedMAA = true
                                    _editTextMAA = binding.editTextMAA.text.toString()
                                }

                                if (binding.editTextPF.text.toString() != "ABS" && binding.editTextPF.text.toString() != "NIL") {
                                    if (binding.editTextPF.text.toString().toDouble() <= editOutOffMarkInternal.toDouble()) {

                                        _editTextPF = binding.editTextPF.text.toString()
                                        isTestedPF = true
                                    } else {
                                        isTestedPF = false
                                     //   markRegisterMspsClicker.onMessageClick("Given Value is above Out-Off CE Mark")
                                        message = "PF Mark is above Out-Off Mark CE"
                                    }
                                }
                                else if (binding.editTextPF.text.toString() == "ABS" || binding.editTextPF.text.toString() == "NIL") {
                                    isTestedPF = true
                                    _editTextPF = binding.editTextPF.text.toString()
                                }

                                if (binding.editTextSEA.text.toString() != "ABS" && binding.editTextSEA.text.toString() != "NIL") {
                                    if (binding.editTextSEA.text.toString().toDouble() <= editOutOffMarkInternal.toDouble()) {

                                        _editTextSEA = binding.editTextSEA.text.toString()
                                        isTestedSEA = true
                                    } else {
                                        isTestedSEA = false
                                      //  markRegisterMspsClicker.onMessageClick("Given Value is above Out-Off CE Mark")
                                        message = "SEA Mark is above Out-Off Mark CE"
                                    }
                                }
                                else if (binding.editTextSEA.text.toString() == "ABS" || binding.editTextSEA.text.toString() == "NIL") {
                                    isTestedSEA = true
                                    _editTextSEA = binding.editTextSEA.text.toString()
                                }

                                if (binding.editTextExternal.text.toString() != "ABS" && binding.editTextExternal.text.toString() != "NIL") {
                                    if (binding.editTextExternal.text.toString().toDouble() <= editOutOffMark.toDouble()) {

                                        _editTextExternal = binding.editTextExternal.text.toString()
                                        isTestedE = true
                                    } else {
                                        isTestedE = false
                                        // markRegisterMspsClicker.onMessageClick("Given Value is above Out-Off Mark")
                                        message = "External Mark is above Out-Off Mark"
                                    }
                                }
                                else if (binding.editTextExternal.text.toString() == "ABS" || binding.editTextExternal.text.toString() == "NIL"){
                                    isTestedE = true
                                    _editTextExternal = binding.editTextExternal.text.toString()
                                }

                                if (isTestedPT && isTestedMAA && isTestedPF && isTestedSEA && isTestedE) {
                                    markRegisterMspsClicker.onSubmitClickListener(
                                        _editTextExternal,
                                        _editTextPT,
                                        _editTextPF,
                                        _editTextSEA,
                                        _editTextMAA,
                                        mark[position],
                                        position
                                    )
                                } else {
                                    markRegisterMspsClicker.onMessageClick(message)
                                }

//                            } else {
//                                markRegisterMspsClicker.onMessageClick("Pass mark is greater then Out-Off Mark")
//                            }
                        }else{
                            markRegisterMspsClicker.onMessageClick("Invalid decimal position in Total Marks")
                        }
                    }else{
                        markRegisterMspsClicker.onMessageClick("Invalid decimal position in Pass or Out-Off Mark CE")
                    }
                }else{
                    markRegisterMspsClicker.onMessageClick("Invalid decimal position in Pass or Out-Off Mark")
                }
            }
//            else{
//                markRegisterMspsClicker.onMessageClick("Out-Off mark must have some values")
////                markRegisterMspsClicker.onMessageClick("Pass Mark or Out-Off mark must have some values")
//            }
        }

        binding.imageViewLeft.setOnClickListener {
            if (position <= 0) {
                markRegisterMspsClicker.onMessageClick("No Previous Student")
            } else {
                position--
                initShown(position)
            }
        }



        binding.imageViewRight.setOnClickListener {
            if ((position + 1) == mark.size) {
                markRegisterMspsClicker.onMessageClick("Student Ends Here")
            } else {
                position++
                initShown(position)
            }
        }

        initShown(position)
    }


    fun validateField(edtField: String, message: String): Boolean {
        return if (Utils.validateFieldIsEmpty(edtField) || edtField =="N") {
            markRegisterMspsClicker.onMessageClick(message)
            false
        } else {
            true
        }
    }

    fun onClickNumber(number : String , type : Int){
        if(type == 1){
            var t1 = binding.editTextPT.text.toString()
            if (t1 == "ABS" || t1 == "NIL") {
                t1 = number
                binding.editTextPT.setText(t1)
            } else {
                t1 += number
                binding.editTextPT.setText(t1)
            }
        }else if(type == 2){
            var t1 = binding.editTextMAA.text.toString()
            if (t1 == "ABS" || t1 == "NIL") {
                t1 = number
                binding.editTextMAA.setText(t1)
            } else {
                t1 += number
                binding.editTextMAA.setText(t1)
            }
        } else if(type == 3){
            var t1 = binding.editTextPF.text.toString()
            if (t1 == "ABS" || t1 == "NIL") {
                t1 = number
                binding.editTextPF.setText(t1)
            } else {
                t1 += number
                binding.editTextPF.setText(t1)
            }
        }
        else if(type == 4){
            var t1 = binding.editTextSEA.text.toString()
            if (t1 == "ABS" || t1 == "NIL") {
                t1 = number
                binding.editTextSEA.setText(t1)
            } else {
                t1 += number
                binding.editTextSEA.setText(t1)
            }
        }
        else if(type == 5){
            var t1 = binding.editTextExternal.text.toString()
            if (t1 == "ABS" || t1 == "NIL") {
                t1 = number
                binding.editTextExternal.setText(t1)
            } else {
                t1 += number
                binding.editTextExternal.setText(t1)
            }
        }
    }





    fun initShown(position : Int){

        binding.editTextExternal.clearFocus();
        binding.editTextPF.clearFocus();

        binding.textViewName.text = mark[position].sTUDENTFNAME

        if (mark[position].tOTALMARK != "N") {
            binding.editTextExternal.setText(mark[position].tOTALMARK)
        }else{
            binding.editTextExternal.setText("")
        }

        if (mark[position].tOTALMARKCE1 != "N") {
            binding.editTextPT.setText(mark[position].tOTALMARKCE1)
        }else{
            binding.editTextPT.setText("")
        }
        if (mark[position].tOTALMARKCE2 != "N") {
            binding.editTextMAA.setText(mark[position].tOTALMARKCE2)
        }else{
            binding.editTextMAA.setText("")
        }
        if (mark[position].tOTALMARKCE3 != "N") {
            binding.editTextPF.setText(mark[position].tOTALMARKCE3)
        }else{
            binding.editTextPF.setText("")
        }
        if (mark[position].tOTALMARKCE4 != "N") {
            binding.editTextSEA.setText(mark[position].tOTALMARKCE4)
        }else{
            binding.editTextSEA.setText("")
        }
    }

    companion object {
        var TAG = "BottomSheetFragment"
    }
}