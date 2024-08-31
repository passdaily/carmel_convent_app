package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.mark_register

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
import info.passdaily.camrelconvertapp.databinding.BottomMarkRegisterLpupBinding
import info.passdaily.camrelconvertapp.model.*


@Suppress("DEPRECATION")
class BottomMarkRegisterLpUp : BottomSheetDialogFragment {

    lateinit var markRegisterClicker: MarkRegisterClicker
    var sTUDENTNAME = ""
    var mark = ArrayList<MarkRegisterMsesLpUpModel.Mark>()
    var position = 0
    var recyclerView: RecyclerView? = null

    private var _binding: BottomMarkRegisterLpupBinding? = null
    private val binding get() = _binding!!

    var type = 0

    constructor()

    constructor(mark: ArrayList<MarkRegisterMsesLpUpModel.Mark>, position: Int, markRegisterClicker: MarkRegisterClicker) {
        this.markRegisterClicker = markRegisterClicker
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

        _binding = BottomMarkRegisterLpupBinding.inflate(inflater, container, false)
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
        binding.editTextCE.inputType = InputType.TYPE_NULL
        binding.editTextCE.keyListener = null
        binding.editTextTE.inputType = InputType.TYPE_NULL
        binding.editTextTE.keyListener = null



        binding.editTextCE.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            type = 1
        }

        binding.editTextTE.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            type = 2
        }


        binding.ImageViewA.setOnClickListener {
            if(type == 1){
                binding.editTextCE.setText("A")
            }else if(type == 2){
                binding.editTextTE.setText("A")
            }
        }

        binding.ImageViewB.setOnClickListener {
            if(type == 1){
                binding.editTextCE.setText("B")
            }else if(type == 2){
                binding.editTextTE.setText("B")
            }
        }

        binding.ImageViewC.setOnClickListener {
            if(type == 1){
                binding.editTextCE.setText("C")
            }else if(type == 2){
                binding.editTextTE.setText("C")
            }
        }

        binding.ImageViewD.setOnClickListener {
            if(type == 1){
                binding.editTextCE.setText("D")
            }else if(type == 2){
                binding.editTextTE.setText("D")
            }
        }

        binding.ImageViewE.setOnClickListener {
            if(type == 1){
                binding.editTextCE.setText("E")
            }else if(type == 2){
                binding.editTextTE.setText("E")
            }
        }

        binding.ImageViewNil.setOnClickListener {
            if(type == 1){
                binding.editTextCE.setText("NIL")
            }else if(type == 2){
                binding.editTextTE.setText("NIL")
            }
        }

        binding.ImageViewABS.setOnClickListener {
            if(type == 1){
                binding.editTextCE.setText("ABS")
            }else if(type == 2){
                binding.editTextTE.setText("ABS")
            }
        }

        binding.buttonSubmit.setOnClickListener {
            if (binding.editTextCE.text.toString().isNotEmpty() && binding.editTextCE.text.toString().isNotEmpty()){
                markRegisterClicker.onSubmitClickListener(binding.editTextCE.text.toString(),
                    binding.editTextTE.text.toString(),mark[position],position)
            }else{
                markRegisterClicker.onMessageClick("Mark Field Cannot be empty")
            }

        }

        binding.imageViewLeft.setOnClickListener {
            if (position <= 0) {
                markRegisterClicker.onMessageClick("No Previous Student")
            } else {
                position--
                initShown(position)
            }
        }



        binding.imageViewRight.setOnClickListener {
            if ((position + 1) == mark.size) {
                markRegisterClicker.onMessageClick("Student Ends Here")
            } else {
                position++
                initShown(position)
            }
        }

        initShown(position)
    }

    fun initShown(position : Int){

        binding.editTextCE.clearFocus();
        binding.editTextTE.clearFocus();

        binding.textViewName.text = mark[position].sTUDENTFNAME

        if (mark[position].gRADETE != "N") {
            binding.editTextCE.setText(mark[position].gRADECE)
        }else{
            binding.editTextCE.setText("")
        }

        if (mark[position].gRADECE != "N") {
            binding.editTextTE.setText(mark[position].gRADETE)
        }else{
            binding.editTextTE.setText("")
        }
    }

    companion object {
        var TAG = "BottomSheetFragment"
    }
}