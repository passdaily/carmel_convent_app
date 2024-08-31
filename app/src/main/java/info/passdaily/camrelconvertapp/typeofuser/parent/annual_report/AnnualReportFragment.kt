package info.passdaily.camrelconvertapp.typeofuser.parent.annual_report

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentAnnualReportBinding
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper

@Suppress("DEPRECATION")
class AnnualReportFragment : Fragment() {

    var TAG = "AnnualReportFragment"
    lateinit var binding : FragmentAnnualReportBinding

    private lateinit var annualReportViewModel : AnnualReportViewModel

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.

    var STUDENT_GENDER = -1
    var RESULT_STATUS = -1
    var RESULT_DESCRIPTION = ""
    var RESULT_REMARK = ""
    var textAdmissionNo: TextView? = null
    var textViewName:TextView? = null
    var textClassName:TextView? = null
    var textViewGender:TextView? = null
    var textRemark:TextView? = null
    var textDescription:TextView? = null
    var cardViewBackGround: CardView? = null
    var imageViewResult: ImageView? = null

    var mContext : Context? =null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext;
        }
        Log.i(TAG,"onAttach ")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.currentPage = 18
        Global.screenState = "landingpage"

        annualReportViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[AnnualReportViewModel::class.java]

        // Inflate the layout for this fragment
        binding = FragmentAnnualReportBinding.inflate(inflater, container, false)

        return binding.root
        // Inflate the layout for this fragment
     //   return inflater.inflate(R.layout.fragment_annual_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var studentDBHelper = StudentDBHelper(requireActivity())
        var student = studentDBHelper.getStudentById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID


        //        pb = new ProgressDialog(getActivity());
//        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pb.setTitle("Loading");
//        pb.setIndeterminate(true);
//        pb.setCancelable(false);
        textViewName = binding.textViewName
        textAdmissionNo = binding.textAdmissionNo
        textClassName = binding.textClassName
        textViewGender = binding.textViewGender
        textDescription = binding.textDescription
        textRemark = binding.textRemark
        cardViewBackGround = binding.cardViewBackGround
        imageViewResult = binding.imageViewResult

        initFunction()
    }

    @SuppressLint("SetTextI18n")
    private fun initFunction() {
        annualReportViewModel.getAnnualReport(STUDENTID,ACADEMICID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            var annualResult = response.annualResult
                            for(i in annualResult.indices) {
                                textViewName!!.text = annualResult[i].sTUDENTNAME
                                textAdmissionNo!!.text = annualResult[i].aDMISSIONNUMBER
                                textClassName!!.text = annualResult[i].cLASSNAME
                                textViewGender!!.text = ""
                                STUDENT_GENDER =  annualResult[i].sTUDENTGENDER
                                RESULT_DESCRIPTION =  annualResult[i].rESULTDESCRIPTION
                                RESULT_REMARK =  annualResult[i].rESULTREMARK
                            }
                            if(annualResult.size == 0){
                                cardViewBackGround!!.setCardBackgroundColor(requireActivity().resources.getColor(R.color.light_blue))
                                Glide.with(requireActivity())
                                    .load(R.drawable.ic_result_not_published)
                                    .thumbnail(0.5f)
                                    .into(imageViewResult!!)
                                textDescription!!.setTextColor(requireActivity().resources.getColor(R.color.result_blue))
                                textDescription!!.text = "Result Not Yet Published"
                                textRemark!!.text = "No Remarks"
                            }else{
                                if (STUDENT_GENDER == 1) {
                                    textViewGender!!.text = "Male"
                                } else {
                                    textViewGender!!.text = "Female"
                                }

                                textDescription!!.text = RESULT_DESCRIPTION
                                textRemark!!.text = RESULT_REMARK
                                when (RESULT_STATUS) {
                                    1 -> {
                                        cardViewBackGround!!.setCardBackgroundColor(requireActivity().resources.getColor(R.color.light_green))
                                        Glide.with(requireActivity())
                                            .load(R.drawable.ic_result_pass)
                                            .thumbnail(0.5f)
                                            .into(imageViewResult!!)
                                        textDescription!!.setTextColor(requireActivity().resources.getColor(R.color.result_green))

                                    }
                                    0 -> {
                                        cardViewBackGround!!.setCardBackgroundColor(requireActivity().resources.getColor(R.color.light_orange))
                                        Glide.with(requireActivity())
                                            .load(R.drawable.ic_result_withhold)
                                            .thumbnail(0.5f)
                                            .into(imageViewResult!!)
                                        textDescription!!.setTextColor(requireActivity().resources.getColor(R.color.result_orange))
                                    }
                                    3 -> {
                                        cardViewBackGround!!.setCardBackgroundColor(requireActivity().resources.getColor(R.color.light_blue))
                                        Glide.with(requireActivity())
                                            .load(R.drawable.ic_result_not_published)
                                            .thumbnail(0.5f)
                                            .into(imageViewResult!!)
                                        textDescription!!.setTextColor(requireActivity().resources.getColor(R.color.result_blue))
                                    }
                                }
                            }

                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Error ${Status.ERROR}")
                            textViewName?.text = ""
                            textViewName?.setBackgroundColor(resources.getColor(R.color.gray_100))
                            textAdmissionNo?.text = ""
                            textAdmissionNo?.setBackgroundColor(resources.getColor(R.color.gray_100))
                            textClassName?.text = ""
                            textClassName?.setBackgroundColor(resources.getColor(R.color.gray_100))
                            textViewGender?.text = ""
                            textViewGender?.setBackgroundColor(resources.getColor(R.color.gray_100))
                            textRemark?.text = ""
                            textRemark?.setBackgroundColor(resources.getColor(R.color.gray_100))
                            textDescription?.text = ""
                            textDescription?.setBackgroundColor(resources.getColor(R.color.gray_100))

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