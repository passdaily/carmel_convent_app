package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.mark_register

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomMarkRegisterKgivBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff


@Suppress("DEPRECATION")
class BottomMarkRegisterKGToIV : BottomSheetDialogFragment {
    private lateinit var markRegisterViewModel: MarkRegisterViewModel

    lateinit var markRegisterKGToIVClicker: MarkRegisterKGToIVClicker
    var sTUDENTNAME = ""
    var mark = ArrayList<MarkRegisterKGToIVModel.Mark>()
    var position = 0
    var recyclerView: RecyclerView? = null

    private var _binding: BottomMarkRegisterKgivBinding? = null
    private val binding get() = _binding!!

    var type = 0

    constructor()

    var editPassMark = ""
    var editOutOffMark = ""

    constructor(editPassMark :String, editOutOffMark : String,
                mark: ArrayList<MarkRegisterKGToIVModel.Mark>, position: Int, markRegisterKGToIVClicker: MarkRegisterKGToIVClicker) {
        this.editPassMark = editPassMark
        this.editOutOffMark = editOutOffMark
        this.markRegisterKGToIVClicker = markRegisterKGToIVClicker
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

        markRegisterViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[MarkRegisterViewModel::class.java]

        _binding = BottomMarkRegisterKgivBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.editTextMark.inputType = InputType.TYPE_NULL
        binding.editTextMark.keyListener = null
//
//
//
//        binding.editTextMark.onFocusChangeListener = OnFocusChangeListener { _, _ ->
//            type = 1
//        }
//
//
//
        binding.ImageView1.setOnClickListener {

            var t1 = binding.editTextMark.text.toString()
            if (t1 == "ABS" || t1 == "NIL") {
                t1 = "1"
                binding.editTextMark.setText(t1)
            } else {
                t1 += "1"
                binding.editTextMark.setText(t1)
            }

        }

        binding.ImageView2.setOnClickListener {
            var t1 = binding.editTextMark.text.toString()
            if (t1 == "ABS" || t1 == "NIL") {
                t1 = "2"
                binding.editTextMark.setText(t1)
            } else {
                t1 += "2"
                binding.editTextMark.setText(t1)
            }
        }

        binding.ImageView3.setOnClickListener {
            var t1 = binding.editTextMark.text.toString()
            if (t1 == "ABS" || t1 == "NIL") {
                t1 = "3"
                binding.editTextMark.setText(t1)
            } else {
                t1 += "3"
                binding.editTextMark.setText(t1)
            }
        }


        binding.ImageView4.setOnClickListener {
            var t1 = binding.editTextMark.text.toString()
            if (t1 == "ABS" || t1 == "NIL") {
                t1 = "4"
                binding.editTextMark.setText(t1)
            } else {
                t1 += "4"
                binding.editTextMark.setText(t1)
            }
        }

        binding.ImageView5.setOnClickListener {
            var t1 = binding.editTextMark.text.toString()
            if (t1 == "ABS" || t1 == "NIL") {
                t1 = "5"
                binding.editTextMark.setText(t1)
            } else {
                t1 += "5"
                binding.editTextMark.setText(t1)
            }
        }

        binding.ImageView6.setOnClickListener {
            var t1 = binding.editTextMark.text.toString()
            if (t1 == "ABS" || t1 == "NIL") {
                t1 = "6"
                binding.editTextMark.setText(t1)
            } else {
                t1 += "6"
                binding.editTextMark.setText(t1)
            }
        }


        binding.ImageView7.setOnClickListener {
            var t1 = binding.editTextMark.text.toString()
            if (t1 == "ABS" || t1 == "NIL") {
                t1 = "7"
                binding.editTextMark.setText(t1)
            } else {
                t1 += "7"
                binding.editTextMark.setText(t1)
            }
        }

        binding.ImageView8.setOnClickListener {
            var t1 = binding.editTextMark.text.toString()
            if (t1 == "ABS" || t1 == "NIL") {
                t1 = "8"
                binding.editTextMark.setText(t1)
            } else {
                t1 += "8"
                binding.editTextMark.setText(t1)
            }
        }

        binding.ImageView9.setOnClickListener {
            var t1 = binding.editTextMark.text.toString()
            if (t1 == "ABS" || t1 == "NIL") {
                t1 = "9"
                binding.editTextMark.setText(t1)
            } else {
                t1 += "9"
                binding.editTextMark.setText(t1)
            }
        }

        binding.ImageView0.setOnClickListener {
            var t1 = binding.editTextMark.text.toString()
            if (t1 == "ABS" || t1 == "NIL") {
                t1 = "0"
                binding.editTextMark.setText(t1)
            } else {
                t1 += "0"
                binding.editTextMark.setText(t1)
            }
        }


        binding.ImageViewABS.setOnClickListener {
            binding.editTextMark.setText("ABS")
        }


        binding.ImageViewNil.setOnClickListener {
            binding.editTextMark.setText("NIL")
        }

        binding.ImageBackSpace.setOnClickListener {

            val text: String = binding.editTextMark.text.toString()
            val len = text.length
            if (len > 0) {
                if (text == "ABS" || text == "NIL") {
                    binding.editTextMark.setText("")
                } else {
                    binding.editTextMark.setText(text.substring(0, text.length - 1))
                }
            }
        }



        binding.buttonSubmit.setOnClickListener {
            Log.i(TAG,"editPassMark $editPassMark")
            Log.i(TAG,"editOutOffMark $editOutOffMark")
//            when {
//                editPassMark == "N" && editPassMark.isEmpty()-> {
//                    markRegisterKGToIVClicker.onMessageClick("Pass Mark must have some values")
//                }
//                editOutOffMark == "N" && editOutOffMark.isEmpty() -> {
//                    markRegisterKGToIVClicker.onMessageClick("Out-Off Mark must have some values")
//                }
//                editPassMark.toInt() >= editOutOffMark.toInt()->{
//                    markRegisterKGToIVClicker.onMessageClick("Pass mark is greater then Out-Off Mark")
//                }
//                else ->{

            if (/*(editPassMark != "N" && editPassMark.isNotEmpty()) && ( */ editOutOffMark != "N" && editOutOffMark.isNotEmpty() /*) */) {
             //   if (editPassMark.toInt() <= editOutOffMark.toInt()) {

                    if (binding.editTextMark.text.toString() != "ABS" && binding.editTextMark.text.toString() != "NIL") {
//                        markRegisterKGToIVClicker.onSubmitClickListener(
//                            binding.editTextMark.text.toString(),
//                            mark[position],
//                            position
//                        )
                        if (binding.editTextMark.text.toString().toInt() <= editOutOffMark.toInt()) {

                            markRegisterKGToIVClicker.onSubmitClickListener(
                                binding.editTextMark.text.toString(),
                                mark[position],
                                position
                            )

                        } else {
                            markRegisterKGToIVClicker.onMessageClick("Given Value is above Out-Off Mark")
                        }
                    }else if(binding.editTextMark.text.toString() == "ABS" || binding.editTextMark.text.toString() == "NIL"){

                        markRegisterKGToIVClicker.onSubmitClickListener(
                            binding.editTextMark.text.toString(),
                            mark[position],
                            position
                        )

                    }

//                    else {
//                        if (binding.editTextMark.text.toString().toInt() <= editOutOffMark.toInt()) {
//
//                            markRegisterKGToIVClicker.onSubmitClickListener(
//                                binding.editTextMark.text.toString(),
//                                mark[position],
//                                position
//                            )
//
//                        } else {
//                            markRegisterKGToIVClicker.onMessageClick("Given Value is above Out-Off Mark")
//                        }
//                    }

//                }else{
//                    markRegisterKGToIVClicker.onMessageClick("Pass mark is greater then Out-Off Mark")
//                }
            }else{
                ///markRegisterKGToIVClicker.onMessageClick("Pass Mark or Out-Off mark must have some values")
                markRegisterKGToIVClicker.onMessageClick("Out-Off mark must have some values")
            }



//                }
//            }

 //           if(editPassMark.toInt() <= editOutOffMark.toInt()) {


//            }else{
//                markRegisterKGToIVClicker.onMessageClick("Pass mark is greater then Out-Off Mark")
//            }

        }
//
        binding.imageViewLeft.setOnClickListener {
            if (position <= 0) {
                markRegisterKGToIVClicker.onMessageClick("No Previous Student")
            } else {
                position--
                initShown(position)
            }
        }



        binding.imageViewRight.setOnClickListener {
            if ((position + 1) == mark.size) {
                markRegisterKGToIVClicker.onMessageClick("Student Ends Here")
            } else {
                position++
                initShown(position)
            }
        }

        initShown(position)
    }

    fun initShown(position : Int){

        binding.editTextMark.clearFocus();

        binding.textViewName.text = mark[position].sTUDENTFNAME

        binding.textViewRollNo.text = "Roll.No : ${mark[position].sTUDENTROLLNUMBER}"

        if (mark[position].tOTALMARK != "N") {
            binding.editTextMark.setText(mark[position].tOTALMARK)
        }else{
            binding.editTextMark.setText("")
        }


    }

    companion object {
        var TAG = "BottomSheetFragment"
    }
}