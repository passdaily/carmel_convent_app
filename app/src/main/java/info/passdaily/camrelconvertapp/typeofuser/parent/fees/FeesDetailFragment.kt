package info.passdaily.camrelconvertapp.typeofuser.parent.fees

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FeesPaymentLayourBinding
import info.passdaily.camrelconvertapp.databinding.FragmentTrackBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import java.text.NumberFormat

@Suppress("DEPRECATION")
class FeesDetailFragment(var feesDetailsModel: ArrayList<FeesDetailsModel.FeesPaidDetail>) : Fragment() {

    var TAG = "TrackFragment"
    lateinit var binding : FeesPaymentLayourBinding

    private lateinit var feesDetailViewModel: FeesDetailViewModel

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.

    var recyclerViewFees : RecyclerView? = null
    var constraintEmpty: ConstraintLayout? =null
    var imageViewEmpty: ImageView? =null
    var textEmpty : TextView? =null

    var shimmerViewContainer : ShimmerFrameLayout? =null
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
    ): View{


        feesDetailViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[FeesDetailViewModel::class.java]

        // Inflate the layout for this fragment
        binding = FeesPaymentLayourBinding.inflate(inflater, container, false)


        // Inflate the layout for this fragment
     //   return inflater.inflate(R.layout.fragment_fees_detail, container, false)
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

        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        Glide.with(requireActivity())
            .load(R.drawable.ic_empty_state_assignment)
            .into(imageViewEmpty!!)
        textEmpty?.text = "No Fees Detail"


        recyclerViewFees = binding.recyclerView
        recyclerViewFees?.layoutManager =  LinearLayoutManager(requireActivity())

        shimmerViewContainer = binding.shimmerViewContainer

        shimmerViewContainer?.visibility = View.VISIBLE


        if(feesDetailsModel.isNotEmpty()){
            constraintEmpty?.visibility = View.GONE
            recyclerViewFees?.visibility = View.VISIBLE
            shimmerViewContainer?.visibility = View.GONE
            if (isAdded) {
                recyclerViewFees?.adapter =
                    FeesAdapter(
                        feesDetailsModel,
                        mContext!!
                    )
            }
        }else{
            constraintEmpty?.visibility = View.VISIBLE
            recyclerViewFees?.visibility = View.GONE
            shimmerViewContainer?.visibility = View.GONE
            if (isAdded) {
                Glide.with(mContext!!)
                    .load(R.drawable.ic_empty_state_absent)
                    .into(imageViewEmpty!!)
            }
            textEmpty?.text =  requireActivity().resources.getString(R.string.no_results)
        }




        //initMethod()
    }

//    private fun initMethod() {
//        //Fees/FeesPaidListGet?ClassId=1&AccademicId=8&StudentId=533&StudentRollNo=1
//        feesDetailViewModel.getFeesDetails(CLASSID,ACADEMICID,STUDENTID,STUDENT_ROLL_NO)
//            .observe(requireActivity(), Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//                            if(response.feesPaidDetails.isNotEmpty()){
//                                constraintEmpty?.visibility = View.GONE
//                                recyclerViewFees?.visibility = View.VISIBLE
//                                shimmerViewContainer?.visibility = View.GONE
//                                if (isAdded) {
//                                    recyclerViewFees?.adapter =
//                                        FeesAdapter(
//                                            response.feesPaidDetails,
//                                            mContext!!
//                                        )
//                                }
//                            }else{
//                                constraintEmpty?.visibility = View.VISIBLE
//                                recyclerViewFees?.visibility = View.GONE
//                                shimmerViewContainer?.visibility = View.GONE
//                                if (isAdded) {
//                                    Glide.with(mContext!!)
//                                        .load(R.drawable.ic_empty_state_absent)
//                                        .into(imageViewEmpty!!)
//                                }
//                                textEmpty?.text =  requireActivity().resources.getString(R.string.no_results)
//                            }
//                        }
//                        Status.ERROR -> {
//                            if (isAdded) {
//                                Glide.with(mContext!!)
//                                    .load(R.drawable.ic_no_internet)
//                                    .into(imageViewEmpty!!)
//                            }
//                            textEmpty?.text =  requireActivity().resources.getString(R.string.no_internet)
//                            constraintEmpty?.visibility = View.VISIBLE
//                            recyclerViewFees?.visibility = View.GONE
//                            shimmerViewContainer?.visibility = View.GONE
//
//                        }
//                        Status.LOADING -> {
//                            if (isAdded) {
//                                Glide.with(mContext!!)
//                                    .load(R.drawable.ic_empty_state_absent)
//                                    .into(imageViewEmpty!!)
//                            }
//                            textEmpty?.text = requireActivity().resources.getString(R.string.loading)
//                            shimmerViewContainer?.visibility = View.VISIBLE
//                            recyclerViewFees?.visibility = View.GONE
//                            constraintEmpty?.visibility = View.GONE
//                        }
//                    }
//                }
//            })
//    }


    class FeesAdapter(var feesDetailsModel : ArrayList<FeesDetailsModel.FeesPaidDetail>, var mContext: Context) :
        RecyclerView.Adapter<FeesAdapter.ViewHolder>() {
        var receiptDate = ""
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textStudentName: TextView = view.findViewById(R.id.textStudentName)
            var textViewRollNo : TextView = view.findViewById(R.id.textViewRollNo)

            var textPaidAmount: TextView = view.findViewById(R.id.textPaidAmount)
            var textViewDate: TextView = view.findViewById(R.id.textViewDate)

            //var textBalance : TextView = view.findViewById(R.id.textBalance)
            //var textPaymentMode : TextView = view.findViewById(R.id.textPaymentMode)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.fees_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if(!feesDetailsModel[position].rECEIPTDATE.isNullOrBlank()) {
                val date1: Array<String> = feesDetailsModel[position].rECEIPTDATE.split("T".toRegex()).toTypedArray()
                val date = Utils.longconversion(date1[0] + " " + date1[1])
                receiptDate = Utils.formattedDateTime(date)
            }
            val format: NumberFormat = NumberFormat.getCurrencyInstance()

            holder.textViewDate.text = receiptDate
            // holder.textPaidAmount.text = format.format(feesDetailsModel[position].rECEIPTTOTAL)
            holder.textPaidAmount.text = "${feesDetailsModel[position].rECEIPTTOTAL}"
            holder.textStudentName.text = feesDetailsModel[position].sTUDENTFNAME
            holder.textViewRollNo.text = "Roll.No : ${feesDetailsModel[position].rOLLNUMBER}"
            // holder.textViewClass.text = feesDetailsModel[position].sTUDENTFNAME
            // holder.textAccedamic.text = feesDetailsModel[position].aCCADEMICTIME


        }

        override fun getItemCount(): Int {
            return feesDetailsModel.size
        }

    }

}