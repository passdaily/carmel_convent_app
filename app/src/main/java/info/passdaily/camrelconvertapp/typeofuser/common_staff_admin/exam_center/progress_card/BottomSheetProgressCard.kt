package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.progress_card

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetKgViiiProgressCardBinding
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff


@Suppress("DEPRECATION")
class BottomSheetProgressCard : BottomSheetDialogFragment {
    private lateinit var progressCardViewModel: ProgressCardViewModel


    private var _binding: BottomSheetKgViiiProgressCardBinding? = null
    private val binding get() = _binding!!

    //      int headerFieldCount =this.subWisePass.length;
    var addExtraString = arrayOf("")
    var markList = arrayOf("")
    var subjectList: ArrayList<HashMap<String, String>> = ArrayList<HashMap<String, String>>()

    var sampleObject: SampleObject


    var textStudentName: TextView? = null
    var textViewParent: TextView? = null
    var textClassName: TextView? = null
    var textViewMobile: TextView? = null
    var textChargeName: TextView? = null
    var textTotalMark: TextView? = null
    var textPassOrFail: TextView? = null
    var textPercentage: TextView? = null
   // var textNoOfPass: TextView? = null
    var textNoOfPass : ChipGroup? = null
    var progressBar: ProgressBar? = null
    var cardLayoutPassFail: CardView? = null

    var cLASSNAME = ""
    var STAFF_FNAME = ""

    var recyclerViewMaterialList: RecyclerView? = null

    var constraintMarkLpUp : ConstraintLayout? = null
    var constraintMarkOther: ConstraintLayout? = null
    var constraintMarkCE: ConstraintLayout? = null
    var constraintMspsLp: ConstraintLayout? = null
    var constraintMspsHs: ConstraintLayout? = null

    var type = ""

    constructor(
        addExtraString: Array<String>,
        sampleObject: SampleObject,
        cLASSNAME: String,
        STAFF_FNAME: String,
        markList: Array<String>,
        subjectList: ArrayList<HashMap<String, String>>,
        type: String
    ) {
        this.addExtraString = addExtraString
        this.sampleObject = sampleObject
        this.cLASSNAME = cLASSNAME
        this.STAFF_FNAME = STAFF_FNAME
        this.markList = markList
        this.subjectList = subjectList
        this.type = type

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

        progressCardViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ProgressCardViewModel::class.java]

        _binding = BottomSheetKgViiiProgressCardBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        textStudentName = binding.textStudentName
        textViewParent = binding.textViewParent
        textClassName = binding.textClassName
        textViewMobile = binding.textViewMobile

        textTotalMark = binding.textTotalMark
        textPassOrFail = binding.textPassOrFail
        textPercentage = binding.textPercentage
        textNoOfPass = binding.textNoOfPass
        textChargeName = binding.textChargeName
        cardLayoutPassFail = binding.cardLayoutPassFail

        progressBar = binding.progressBar

        constraintMarkLpUp  = binding.constraintMarkLpUp
        constraintMarkOther = binding.constraintMarkOther
        constraintMarkCE = binding.constraintMarkCE
        constraintMspsLp = binding.constraintMspsLp
        constraintMspsHs = binding.constraintMspsHs

        when (type) {
            "LpUp" -> {
                cardLayoutPassFail?.visibility = View.GONE
                constraintMarkLpUp?.visibility = View.VISIBLE
                constraintMarkOther?.visibility = View.GONE
                constraintMarkCE?.visibility = View.GONE
                constraintMspsLp?.visibility = View.GONE
                constraintMspsHs?.visibility = View.GONE
            }
            "other" -> {
                cardLayoutPassFail?.visibility = View.VISIBLE
                constraintMarkLpUp?.visibility = View.GONE
                constraintMarkOther?.visibility = View.VISIBLE
                constraintMarkCE?.visibility = View.GONE
                constraintMspsLp?.visibility = View.GONE
                constraintMspsHs?.visibility = View.GONE
            }
            "ce" -> {
                cardLayoutPassFail?.visibility = View.VISIBLE
                constraintMarkLpUp?.visibility = View.GONE
                constraintMarkOther?.visibility = View.GONE
                constraintMarkCE?.visibility = View.VISIBLE
                constraintMspsLp?.visibility = View.GONE
                constraintMspsHs?.visibility = View.GONE
            }
            "MspsLp" -> {
                cardLayoutPassFail?.visibility = View.VISIBLE
                constraintMarkLpUp?.visibility = View.GONE
                constraintMarkOther?.visibility = View.GONE
                constraintMarkCE?.visibility = View.GONE
                constraintMspsLp?.visibility = View.VISIBLE
                constraintMspsHs?.visibility = View.GONE
            }
            "MspsHs" -> {
                cardLayoutPassFail?.visibility = View.VISIBLE
                constraintMarkLpUp?.visibility = View.GONE
                constraintMarkOther?.visibility = View.GONE
                constraintMarkCE?.visibility = View.GONE
                constraintMspsLp?.visibility = View.GONE
                constraintMspsHs?.visibility = View.VISIBLE
            }
        }

        textStudentName?.text = sampleObject.header1
        textViewParent?.text = sampleObject.header2
        textViewMobile?.text = sampleObject.header3
        textClassName?.text = cLASSNAME

     //   textNoOfPass?.text = addExtraString[5]
        createChip(addExtraString[5])

        if (addExtraString[2].contains("FP")) {
            textPassOrFail?.text = "Pass"
            textPassOrFail?.setTextColor(requireActivity().resources.getColor(R.color.fresh_green_200))
            textPassOrFail?.background = ContextCompat.getDrawable(requireActivity(), R.drawable.bg_text_round_green);
            // Get the Drawable custom_progressbar
            val drawable = requireActivity().resources.getDrawable(R.drawable.custom_progress_green)
            // set the drawable as progress drawable
            progressBar!!.progressDrawable = drawable
        //    cardLayoutPassFail!!.setCardBackgroundColor(requireActivity().resources.getColor(R.color.green_light100))
        } else if (addExtraString[2].contains("FL")) {
            textPassOrFail?.text = "Fail"
            textPassOrFail?.setTextColor(requireActivity().resources.getColor(R.color.fresh_red_200))
            textPassOrFail?.background = ContextCompat.getDrawable(requireActivity(), R.drawable.bg_text_round_red);
            // Get the Drawable custom_progressbar
            val drawable = requireActivity().resources.getDrawable(R.drawable.custom_progress_red)
            // set the drawable as progress drawable
            progressBar!!.progressDrawable = drawable
        //    cardLayoutPassFail!!.setCardBackgroundColor(requireActivity().resources.getColor(R.color.red_light100))

        }

        recyclerViewMaterialList = binding.recyclerView
        recyclerViewMaterialList?.layoutManager = LinearLayoutManager(requireActivity())

        recyclerViewMaterialList?.adapter =
            MarkListAdapter(subjectList, markList, requireActivity(), type)


        textTotalMark?.text = "Total  :  ${addExtraString[3]} / ${addExtraString[6]}"
        textPercentage?.text = addExtraString[4] //


        try {
            textChargeName?.text = "In Charge Name : $STAFF_FNAME"
        } catch (e: Exception) {
            textChargeName?.text = "In Charge Name"
        }
//        /ArithmeticException
        try {
            val percent: Int = 100 * addExtraString[3].toInt() / addExtraString[6].toInt()
            progressBar?.progress = percent
        } catch (e: ArithmeticException) {
            progressBar?.progress = 0
        }


    }

    private fun createChip(label: String) {
        Log.i(TAG,"label $label")
        val listArray: Array<String> =
            label.split(",".toRegex()).toTypedArray()
        for (i in listArray.indices) {
            val chip = Chip(requireActivity())
            chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.blue_100))
            chip.text = listArray[i]
//            chip.setTextColor(resources.getColor(R.color.green_400))
            chip.setTextAppearance(R.style.chipTextAppearance)
            textNoOfPass?.addView(chip)
        }
    }


    class MarkListAdapter(
        var subjectList: ArrayList<HashMap<String, String>>,
        var markList: Array<String>,
        var context: Context,
        var type: String
    ) :
        RecyclerView.Adapter<MarkListAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewSubject: TextView = view.findViewById(R.id.textViewSubject)
            var textViewMark: TextView = view.findViewById(R.id.textViewMark)
            var textViewGrade: TextView = view.findViewById(R.id.textViewGrade)

            var constraintMarkOther: ConstraintLayout = view.findViewById(R.id.constraintMarkOther)
            var constraintMarkCE: ConstraintLayout = view.findViewById(R.id.constraintMarkCE)
            var constraintMspsLp: ConstraintLayout = view.findViewById(R.id.constraintMspsLp)

            var constraintMspsHs: ConstraintLayout = view.findViewById(R.id.constraintMspsHs)

            var textViewSubjectCE: TextView = view.findViewById(R.id.textViewSubjectCE)
            var textViewMarkCE: TextView = view.findViewById(R.id.textViewMarkCE)
            var textViewInternalCe: TextView = view.findViewById(R.id.textViewInternalCe)
            var textViewTotalCe: TextView = view.findViewById(R.id.textViewTotalCe)


            var textSubjectMspsLp: TextView = view.findViewById(R.id.textSubjectMspsLp)
            var textMarkMspsLpPT: TextView = view.findViewById(R.id.textMarkMspsLpPT)
            var textMarkMspsLpPf: TextView = view.findViewById(R.id.textMarkMspsLpPf)
            var textMarkMspsLpSea: TextView = view.findViewById(R.id.textMarkMspsLpSea)
            var textMarkMspsLp: TextView = view.findViewById(R.id.textMarkMspsLp)
            var textTotalMspsLp: TextView = view.findViewById(R.id.textTotalMspsLp)
            var textGradeMspsLp: TextView = view.findViewById(R.id.textGradeMspsLp)


            var textSubjectMspsHs: TextView = view.findViewById(R.id.textSubjectMspsHs)
            var textMarkMspsHsPT: TextView = view.findViewById(R.id.textMarkMspsHsPT)
            var textMarkMspsHsMAA: TextView = view.findViewById(R.id.textMarkMspsHsMAA)
            var textMarkMspsHsPf: TextView = view.findViewById(R.id.textMarkMspsHsPf)
            var textMarkMspsHsSea: TextView = view.findViewById(R.id.textMarkMspsHsSea)
            var textMarkMspsHs: TextView = view.findViewById(R.id.textMarkMspsHs)
            var textTotalMspsHs: TextView = view.findViewById(R.id.textTotalMspsHs)
            var textGradeMspsHs: TextView = view.findViewById(R.id.textGradeMspsHs)

        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.progress_report_mark_bottom_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            //{
            //"SUBJECT_ID": 10,
            //"SUBJECT_NAME": "Malayalam",
            //"PASS_MARK": 33,
            //"OUTOFF_MARK": 100,
            //"SUBJECTWISE_PASS": 28,
            //"TOTAL_ATTEND": 37
            //},
            holder.textViewSubject.text = subjectList[position]["SUBJECT_NAME"]
            holder.textViewSubjectCE.text = subjectList[position]["SUBJECT_NAME"]
            holder.textSubjectMspsLp.text = subjectList[position]["SUBJECT_NAME"]
            holder.textSubjectMspsHs.text = subjectList[position]["SUBJECT_NAME"]

            if (type == "LpUp") {
                holder.constraintMarkOther.visibility = View.VISIBLE
                holder.constraintMarkCE.visibility = View.GONE
                holder.constraintMspsLp.visibility = View.GONE
                holder.constraintMspsHs.visibility = View.GONE
                if (markList[position] == "-") {
                    holder.textViewMark.text = "-"
                    holder.textViewGrade.text = "-"
                } else if (markList[position] == "AB") {
                    holder.textViewMark.text = "AB"
                    holder.textViewGrade.text = "AB"
                } else {
                    val mark = markList[position].split("|").toTypedArray()
                    holder.textViewMark.text = mark[0]
                    holder.textViewGrade.text = mark[1]
                }
            }
            else if (type == "other") {
                holder.constraintMarkOther.visibility = View.VISIBLE
                holder.constraintMarkCE.visibility = View.GONE
                holder.constraintMspsLp.visibility = View.GONE
                holder.constraintMspsHs.visibility = View.GONE
                if (markList[position] == "-") {
                    holder.textViewMark.text = "-"
                    holder.textViewGrade.text = "-"
                } else if (markList[position] == "AB") {
                    holder.textViewMark.text = "AB"
                    holder.textViewGrade.text = "AB"
                } else {
                    val mark = markList[position].split("|").toTypedArray()
                    holder.textViewMark.text = mark[0]
                    holder.textViewGrade.text = mark[1]
                }
            } else if (type == "ce") {
                holder.constraintMarkOther.visibility = View.GONE
                holder.constraintMarkCE.visibility = View.VISIBLE
                holder.constraintMspsLp.visibility = View.GONE
                holder.constraintMspsHs.visibility = View.GONE
                if (markList[position] == "-") {
                    holder.textViewMarkCE.text = "-"
                    holder.textViewInternalCe.text = "-"
                    holder.textViewTotalCe.text = "-"
                } else if (markList[position] == "AB") {
                    holder.textViewMarkCE.text = "AB"
                    holder.textViewInternalCe.text = "AB"
                    holder.textViewTotalCe.text = "AB"
                } else {
                    val mark = markList[position].split("|").toTypedArray()
                    holder.textViewMarkCE.text = mark[0]
                    holder.textViewInternalCe.text = mark[1]
                    holder.textViewTotalCe.text = mark[2]

                }

            } else if (type == "MspsLp") {
                holder.constraintMarkOther.visibility = View.GONE
                holder.constraintMarkCE.visibility = View.GONE
                holder.constraintMspsLp.visibility = View.VISIBLE
                holder.constraintMspsHs.visibility = View.GONE
                if (markList[position] == "-") {
                    holder.textMarkMspsLpPT.text = "-"
                    holder.textMarkMspsLpPf.text = "-"
                    holder.textMarkMspsLpSea.text = "-"
                    holder.textMarkMspsLp.text = "-"
                    holder.textTotalMspsLp.text = "-"
                    holder.textGradeMspsLp.text = "-"
                } else if (markList[position] == "AB") {
                    holder.textMarkMspsLpPT.text = "AB"
                    holder.textMarkMspsLpPf.text = "AB"
                    holder.textMarkMspsLpSea.text = "AB"
                    holder.textMarkMspsLp.text = "AB"
                    holder.textTotalMspsLp.text = "AB"
                    holder.textGradeMspsLp.text = "AB"
                } else {
                    val mark = markList[position].split("p").toTypedArray()
                    holder.textMarkMspsLpPT.text = mark[0]
                    holder.textMarkMspsLpPf.text = mark[1]
                    holder.textMarkMspsLpSea.text = mark[2]
                    holder.textMarkMspsLp.text = mark[3]
                    holder.textTotalMspsLp.text = mark[4]
                    holder.textGradeMspsLp.text = mark[5]

                }

            } else if (type == "MspsHs") {
                holder.constraintMarkOther.visibility = View.GONE
                holder.constraintMarkCE.visibility = View.GONE
                holder.constraintMspsLp.visibility = View.GONE
                holder.constraintMspsHs.visibility = View.VISIBLE
                if (markList[position] == "-") {
                    holder.textMarkMspsHsPT.text = "-"
                    holder.textMarkMspsHsMAA.text = "-"
                    holder.textMarkMspsHsPf.text = "-"
                    holder.textMarkMspsHsSea.text = "-"
                    holder.textMarkMspsHs.text = "-"
                    holder.textTotalMspsHs.text = "-"
                    holder.textGradeMspsHs.text = "-"
                } else if (markList[position] == "AB") {
                    holder.textMarkMspsHsPT.text = "AB"
                    holder.textMarkMspsHsMAA.text = "AB"
                    holder.textMarkMspsHsPf.text = "AB"
                    holder.textMarkMspsHsSea.text = "AB"
                    holder.textMarkMspsHs.text = "AB"
                    holder.textTotalMspsHs.text = "AB"
                    holder.textGradeMspsHs.text = "AB"
                } else {
                    val mark = markList[position].split("p").toTypedArray()
                    holder.textMarkMspsHsPT.text = mark[0]
                    holder.textMarkMspsHsMAA.text = mark[1]
                    holder.textMarkMspsHsPf.text = mark[2]
                    holder.textMarkMspsHsSea.text = mark[3]
                    holder.textMarkMspsHs.text = mark[4]
                    holder.textTotalMspsHs.text = mark[5]
                    holder.textGradeMspsHs.text = mark[6]


                }

            }

        }

        override fun getItemCount(): Int {
            return subjectList.size
        }

    }


    companion object {
        var TAG = "BottomSheetFragment"
    }
}