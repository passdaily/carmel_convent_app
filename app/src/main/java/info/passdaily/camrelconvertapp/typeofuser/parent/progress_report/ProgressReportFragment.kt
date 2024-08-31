package info.passdaily.camrelconvertapp.typeofuser.parent.progress_report

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.imageview.ShapeableImageView
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentProgressReportBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentFCMHelper
import info.passdaily.camrelconvertapp.typeofuser.parent.study_material.ItemClickListener


@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class ProgressReportFragment(var title: String) : Fragment(), ItemClickListener, ProgressClickListener {

    var TAG = "ProgressReportFragment"
    lateinit var binding: FragmentProgressReportBinding

    private lateinit var progressViewModel: ProgressViewModel

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.
    var STUDENT_ID = 0

    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null

    var mContext: Context? = null
    var ExamId = 0

    var recyclerViewMaterialList: RecyclerView? = null
    var recyclerView: RecyclerView? = null
    var cardViewLayout: CardView? = null

    var textStudentName: TextView? = null
    var textClassName: TextView? = null
    var textTotalMark: TextView? = null
    var textPassOrFail: TextView? = null
    var textPercentage: TextView? = null
    var textNoOfPass : TextView? = null
    var cardLayoutPassFail : CardView? = null

    var progressBar : ProgressBar? = null

    var shimmerViewContainer: ShimmerFrameLayout? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext;
        }
        Log.i(TAG, "onAttach ")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Global.currentPage = 17
        Global.screenState = "landingpage"

        progressViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[ProgressViewModel::class.java]

        // Inflate the layout for this fragment
        binding = FragmentProgressReportBinding.inflate(inflater, container, false)

        return binding.root
        // Inflate the layout for this fragment
        /// return inflater.inflate(R.layout.fragment_progress_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var studentDBHelper = StudentDBHelper(requireActivity())
        var student = studentDBHelper.getStudentById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID
        STUDENT_ROLL_NO = student.STUDENT_ROLL_NO

        var studentFCMHelper = StudentFCMHelper(requireActivity())
        var studentFcm = studentFCMHelper.getProductByStudent(Global.studentId.toString())

        STUDENT_ID = studentFcm.STUDENT_ID
        var STUDENT_NAME = studentFcm.STUDENT_NAME
        val CLASS_NAME = studentFcm.CLASS_NAME

        if (!STUDENT_NAME.isNullOrBlank()) {
            val dateC: Array<String> = STUDENT_NAME.split(" - ".toRegex()).toTypedArray()
            STUDENT_NAME = dateC[0]
        }

        var textViewTitle = binding.textViewTitle
        textViewTitle.text = title
        ///load subjects
        recyclerView = binding.tabLayout
        recyclerView?.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        //   viewModel.getSubjects(student.CLASS_ID, student.STUDENT_ID)
//        tabLayout?.tabRippleColor = null;

        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        if (isAdded) {
            Glide.with(mContext!!)
                .load(R.drawable.ic_empty_progress_report)
                .into(imageViewEmpty!!)
        }
        textEmpty?.text = "No Progress Details"
        cardViewLayout = binding.cardViewLayout

        textStudentName = binding.textStudentName
        textClassName = binding.textClassName
        textTotalMark = binding.textTotalMark
        textPassOrFail = binding.textPassOrFail
        textPercentage = binding.textPercentage
        textNoOfPass = binding.textNoOfPass
        progressBar = binding.progressBar
        cardLayoutPassFail  = binding.cardLayoutPassFail

        textStudentName?.text = STUDENT_NAME
        textClassName?.text = CLASS_NAME

//AccademicId=7&ClassId=2&StudentId=1

/////////////load study Material list
        recyclerViewMaterialList = binding.recyclerView
        recyclerViewMaterialList?.layoutManager = LinearLayoutManager(requireActivity())

        shimmerViewContainer = binding.shimmerViewContainer

        intiFunction()

    }

    private fun intiFunction() {
        progressViewModel.getExamList(ExamId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            val examDetails = response.examDetails
                            if (isAdded) {
                                recyclerView?.adapter =
                                    SubjectAdapter(this, examDetails, mContext!!)
                            }
                            if (examDetails.isNotEmpty()) {
                                onClick(examDetails[0].eXAMID)
                            }
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })

    }


    class SubjectAdapter(
        val itemClickListener: ItemClickListener,
        var subjects: ArrayList<ExamDetailsModel.ExamDetail>,
        var context: Context
    ) :
        RecyclerView.Adapter<SubjectAdapter.ViewHolder>() {
        var index = 0

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textSubject: TextView = view.findViewById(R.id.textAssignmentName)
            var cardView: CardView = view.findViewById(R.id.cardView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.subjet_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textSubject.text = subjects[position].eXAMNAME
            holder.cardView.setOnClickListener {
                itemClickListener.onClick(subjects[position].eXAMID)
                index = position;
                notifyDataSetChanged()
            }

            if (index == position) {
                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.green_400))
                holder.textSubject.setTextColor(context.resources.getColor(R.color.white))
            } else {
                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.white))
                holder.textSubject.setTextColor(context.resources.getColor(R.color.green_400))
            }
        }

        override fun getItemCount(): Int {
            return subjects.size
        }
    }

    override fun onClick(id: Int) {
        Log.i(TAG, "onClick callback $id")
        progressViewModel.getExamMarkDetails(
            CLASSID,
            ACADEMICID,
            id,
            STUDENT_ROLL_NO,
            STUDENT_ID
        )
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            if (response.markDetails.isNotEmpty()) {
                                constraintEmpty?.visibility = View.GONE
                                cardViewLayout?.visibility = View.VISIBLE
                                shimmerViewContainer?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewMaterialList?.adapter =
                                        ProgressAdapter(this, response.markDetails, mContext!!)
                                }
                            } else {
                                constraintEmpty?.visibility = View.VISIBLE
                                cardViewLayout?.visibility = View.GONE
                                shimmerViewContainer?.visibility = View.GONE
                                if (isAdded) {
                                    Glide.with(mContext!!)
                                        .load(R.drawable.ic_empty_state_absent)
                                        .into(imageViewEmpty!!)
                                }
                                textEmpty?.text =  requireActivity().resources.getString(R.string.no_results)

                            }
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            cardViewLayout?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_no_internet)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text =  requireActivity().resources.getString(R.string.no_results)
                            Log.i(TAG, "Status.ERROR ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_empty_state_video)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text =  requireActivity().resources.getString(R.string.loading)
                            shimmerViewContainer?.visibility = View.VISIBLE
                            cardViewLayout?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE

                        }
                    }
                }
            })
    }

    class ProgressAdapter(
        var listener: ProgressClickListener,
        var markDetails: ArrayList<ProgressMarkDetailsModel.MarkDetail>,
        var mContext: Context
    ) :
        RecyclerView.Adapter<ProgressAdapter.ViewHolder>() {
        var expandState = SparseBooleanArray()
        var markDate = ""
        var TOTAL_MARK = 0
        var oUTOFFMARK = 0
        var PASS_MARK = 0
        var passOrFail = true
        var passCount = 0
        var failCount = 0

        var gradeArrayList = ArrayList<String>()


        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textSubjectName: TextView = view.findViewById(R.id.textSubjectName)
            var textViewGrade: TextView = view.findViewById(R.id.textViewGrade)
            var textMark: TextView = view.findViewById(R.id.textMark)
//            var textViewRollNo: TextView = view.findViewById(R.id.textViewRollNo)
//            var textDate: TextView = view.findViewById(R.id.textDate)
//            var constraintExpended : ConstraintLayout  = view.findViewById(R.id.constraintExpended)
//            var imageArrow : ImageView = view.findViewById(R.id.imageArrow)
//            var relativeExpand  : RelativeLayout = view.findViewById(R.id.relativeExpand)

            var shapeImageView: ShapeableImageView = view.findViewById(R.id.shapeImageView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.progresslist_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.textSubjectName.text = markDetails[position].sUBJECTNAME
            holder.textMark.text = "Marks : ${markDetails[position].tOTALMARK}"
            holder.textViewGrade.text = markDetails[position].mARKGRADE

            TOTAL_MARK += markDetails[position].tOTALMARK
            oUTOFFMARK += markDetails[position].oUTOFFMARK

            PASS_MARK = markDetails[position].pASSMARK

            if(markDetails[position].tOTALMARK < PASS_MARK){
                passOrFail = false
                failCount++
            }else{
                passCount++
            }


            gradeArrayList.add(markDetails[position].mARKGRADE!!)

            if ((position + 1) == markDetails.size) {

                val setMap = HashMap<String, Int>()
                for (i in gradeArrayList) {
                    if (setMap.containsKey(i)) setMap[i] = setMap[i]!! + 1 else setMap[i] = 1
                }
                Log.i("ProgressReportFragment","set $setMap")


                val percent = 100 * TOTAL_MARK / oUTOFFMARK
                listener.onViewClick(TOTAL_MARK, percent,passOrFail,passCount,failCount,setMap,oUTOFFMARK)

            }

            when (markDetails[position].sUBJECTICON) {
                "English.png" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_english)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl

                    Glide.with(mContext)
                        .load(R.drawable.ic_study_english)
                        .into(holder.shapeImageView)
                }
                "Chemistry.png" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_chemistry)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_chemistry)
                        .into(holder.shapeImageView)
                }
                "Biology.png" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_bio)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_biology)
                        .into(holder.shapeImageView)
                }
                "Maths.png" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_maths)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_maths)
                        .into(holder.shapeImageView)
                }
                "Hindi.png" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_hindi)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_hindi)
                        .into(holder.shapeImageView)
                }
                "Physics.png" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_physics)
                        .into(holder.shapeImageView)
                }
                "Malayalam.png" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_malayalam)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_malayalam)
                        .into(holder.shapeImageView)
                }
                "Arabic.png" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_arabic)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_arabic)
                        .into(holder.shapeImageView)
                }
                "Accountancy.png" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_accounts)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_accountancy)
                        .into(holder.shapeImageView)
                }
                "Social.png" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_social)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_social)
                        .into(holder.shapeImageView)
                }
                "Economics.png" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_econonics)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_economics)
                        .into(holder.shapeImageView)
                }
                "BasicScience.png" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_bio)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_biology)
                        .into(holder.shapeImageView)
                }
                "Computer.png" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_computer)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_computer)
                        .into(holder.shapeImageView)
                }
                "General.png" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_computer)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_general)
                        .into(holder.shapeImageView)
                }
            }

        }

        override fun getItemCount(): Int {
            return markDetails.size
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewClick(totalMark: Int, percentage: Int, passOrFail : Boolean,passCount : Int,
                             failCount :Int,setMap : HashMap<String, Int>,oUTOFFMARK : Int) {
        textTotalMark?.text = "Total Marks : $totalMark / $oUTOFFMARK"
        textPercentage?.text = "$percentage%"
        if(passOrFail){
            textPassOrFail?.text = "Pass"
            textPassOrFail?.setTextColor(requireActivity().resources.getColor(R.color.green_300))
            // Get the Drawable custom_progressbar
            val drawable = requireActivity().resources.getDrawable(R.drawable.custom_progress_green)
            // set the drawable as progress drawable
            progressBar!!.progressDrawable = drawable
            cardLayoutPassFail!!.setCardBackgroundColor(requireActivity().resources.getColor(R.color.green_light100))
        }else {
            textPassOrFail?.text = "Fail"
            textPassOrFail?.setTextColor(requireActivity().resources.getColor(R.color.color_physics))
            // Get the Drawable custom_progressbar
            val drawable = requireActivity().resources.getDrawable(R.drawable.custom_progress_red)
            // set the drawable as progress drawable
            progressBar!!.progressDrawable = drawable
            cardLayoutPassFail!!.setCardBackgroundColor(requireActivity().resources.getColor(R.color.red_light100))

        }
        var noOfText = ""
        for(i in setMap.keys){
            noOfText += " ,   No.of $i : ${setMap[i]}"
        }
        textNoOfPass?.text = "No.of Pass : $passCount ,   No.of Fail : $failCount$noOfText"
        progressBar?.progress = percentage
    }

}

interface ProgressClickListener {

    fun onViewClick(totalMark: Int, percentage: Int,passOrFail : Boolean,passCount : Int,
                    failCount :Int,setMap : HashMap<String, Int>,oUTOFFMARK : Int)
}



//            expandState.append(position, true)
//            holder.textViewTitle.text = "Subject : " + markDetails[position].sUBJECTNAME
//            holder.textViewRollNo.text = "Roll.No  : " + markDetails[position].sTUDENTROLLNUMBER
//            holder.textGrade.text = "Grade : " + markDetails[position].mARKGRADE
//
//            if (markDetails[position].tOTALMARK == -1) {
//                holder.textViewMark.visibility = View.GONE
//                holder.textGrade.setTextColor(Color.parseColor("#000000"))
//            } else {
//                holder.textViewMark.visibility = View.VISIBLE
//                holder.textViewMark.text = "Mark   :   " + markDetails[position].tOTALMARK + "  /  " + markDetails[position].oUTOFFMARK
//                holder.textGrade.setTextColor(Color.parseColor("#000000"))
//            }
//
//
//            if(!markDetails[position].mARKDATE.isNullOrBlank()) {
//                val date1: Array<String> = markDetails[position].mARKDATE.split("T".toRegex()).toTypedArray()
//                val date = Utils.longconversion(date1[0] + " " + date1[1])
//                markDate = Utils.formattedDateTime(date)
//            }
//            holder.textDate.text = markDate
//
//            val isExpanded = expandState[position]
//            holder.constraintExpended.visibility = if (isExpanded) View.VISIBLE else View.GONE
//
//            holder.imageArrow.rotation = if (expandState[position]) 180f else 0f
//            holder.relativeExpand.setOnClickListener {
//                onClickButton(holder.constraintExpended, holder.imageArrow, position)
//            }


//        private fun configureCardViewLayoutParams(cardView: CardView, numOfCardViewItemsOnScreen: Int, marginAtRecyclerViewsEnds: Float, marginLeftParam: Float, marginRightParam: Float, marginTopParam: Float, marginBottomParam: Float): ViewGroup.MarginLayoutParams {
//            val numOfGapsInBetweenItems = numOfCardViewItemsOnScreen - 1
//            var combinedGapWidth = ((marginLeftParam + marginRightParam) * numOfGapsInBetweenItems) + (marginAtRecyclerViewsEnds * 2)
//            //Provided approx. adjustment of 2dp to prevent the extreme edges of the card from being cut-off
//            combinedGapWidth += 2
//            val combinedGapWidthDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, combinedGapWidth, cardView.resources.displayMetrics).toInt()
//            //Since margins/gaps are provided in-between the items, these have to be taken to account & subtracted from the width in-order to obtain even spacing
//            cardView.layoutParams.width = (getScreenWidth(mContext) - combinedGapWidthDp) / numOfCardViewItemsOnScreen
//
//            //Margins in-between the items
//            val marginLeftOfItem = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginLeftParam, cardView.resources.displayMetrics).toInt()
//            val marginRightOfItem = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginRightParam, cardView.resources.displayMetrics).toInt()
//            val marginTopOfItem = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginTopParam, cardView.resources.displayMetrics).toInt()
//            val marginBottomOfItem = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginBottomParam, cardView.resources.displayMetrics).toInt()
//
//            //Providing the above margins to the CardView
//            val cardViewMarginParams: ViewGroup.MarginLayoutParams = cardView.layoutParams as ViewGroup.MarginLayoutParams
//            cardViewMarginParams.setMargins(marginLeftOfItem, marginTopOfItem, marginRightOfItem, marginBottomOfItem)
//            return cardViewMarginParams
//        }
//
//        private fun getScreenWidth(activity: Context): Int {
//            var width: Int = 0
//            val size = Point()
//            val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//            width = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                val bounds = windowManager.currentWindowMetrics.bounds
//                bounds.width()
//            } else {
//                val display: Display? = windowManager.defaultDisplay
//                display?.getSize(size)
//                size.x
//            }
//            return width
//        }
//
//        private fun onClickButton(
//            expandableLayout: ConstraintLayout,
//            imageArrow: ImageView,
//            i: Int
//        ) {
//            if (expandableLayout.visibility == View.VISIBLE) {
//                createRotateAnimator(imageArrow, 180f, 0f).start()
//                expandableLayout.visibility = View.GONE
//                expandState.put(i, false)
//            } else {
//                createRotateAnimator(imageArrow, 0f, 180f).start()
//                expandableLayout.visibility = View.VISIBLE
//                expandState.put(i, true)
//            }
//        }
//
//        private fun createRotateAnimator(
//            buttonLayout: ImageView, v: Float, v1: Float
//        ): ObjectAnimator {
//            val animator = ObjectAnimator.ofFloat(buttonLayout, "rotation", v, v1)
//            animator.duration = 300
//            animator.interpolator = LinearInterpolator()
//            return animator
//        }