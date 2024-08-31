package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.mark_absent

import android.annotation.SuppressLint
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.os.Bundle
import info.passdaily.camrelconvertapp.R
import android.view.LayoutInflater
import android.view.ViewGroup
import android.util.Log
import android.view.View
import android.widget.*
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import info.passdaily.camrelconvertapp.databinding.BottomSheetMarkAbsentCalendarBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class BottomSheetMarkAbsent : BottomSheetDialogFragment {


    private var _binding: BottomSheetMarkAbsentCalendarBinding? = null
    private val binding get() = _binding!!

    lateinit var markAbsentListener: MarkAbsentListener
    var compactCalendarView: CompactCalendarView? = null
    var textViewMonth: TextView? = null
    var imageViewPreview: ImageView? = null
    var imageViewNext: ImageView? = null

    var strDate = ""

    var strDateFormatDDMMMYYYY = ""

    constructor()
    constructor(markAbsentListener: MarkAbsentListener){
        this.markAbsentListener = markAbsentListener
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

        _binding = BottomSheetMarkAbsentCalendarBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val date = Calendar.getInstance().time
        val df = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        val dateFormat: DateFormat =
            SimpleDateFormat("yyyy-MM-dd")
        strDate = dateFormat.format(date)
        strDateFormatDDMMMYYYY = df.format(date)

        imageViewPreview = binding.imageViewPreview
        imageViewNext = binding.imageViewNext
        compactCalendarView = binding.compactCalendarView
        textViewMonth = binding.textViewMonth
        compactCalendarView?.setFirstDayOfWeek(Calendar.SUNDAY)
       // val date = Calendar.getInstance().time
        @SuppressLint("SimpleDateFormat") val dateFormatMMM: DateFormat = SimpleDateFormat("MMM")
        @SuppressLint("SimpleDateFormat") val dateFormatYYYY: DateFormat = SimpleDateFormat("yyyy")
        val strMonth = dateFormatMMM.format(date)
        textViewMonth?.text = strMonth + " " + dateFormatYYYY.format(date)

        compactCalendarView?.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                var milliseconds: Long = 0
                @SuppressLint("SimpleDateFormat") val dateFormat: DateFormat =
                    SimpleDateFormat("yyyy-MM-dd")
                @SuppressLint("SimpleDateFormat") val dateFormatDDMMMYYYY: DateFormat =
                    SimpleDateFormat("dd MMM, yyyy")
                strDate = dateFormat.format(dateClicked!!)
                strDateFormatDDMMMYYYY = dateFormatDDMMMYYYY.format(dateClicked)
                Log.i(TAG,"strDate $strDate")
                Log.i(TAG,"strDateFormatDDMMMYYYY $strDateFormatDDMMMYYYY")
//                try {
//                    val d = dateFormat.parse(strDate)
//                    milliseconds = d!!.time
//                } catch (e: ParseException) {
//                    e.printStackTrace()
//                }
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                @SuppressLint("SimpleDateFormat") val dateFormat: DateFormat =
                    SimpleDateFormat("yyyy-MMM-dd hh:mm:ss")
                @SuppressLint("SimpleDateFormat") val dateFormat_MMM: DateFormat =
                    SimpleDateFormat("MMM")
                @SuppressLint("SimpleDateFormat") val dateFormat_YYYY: DateFormat =
                    SimpleDateFormat("yyyy")
                //val strDate = dateFormat.format(firstDayOfNewMonth)
                val str_Month = dateFormat_MMM.format(firstDayOfNewMonth!!)
                textViewMonth?.text = str_Month + " " + dateFormat_YYYY.format(firstDayOfNewMonth)
            }
        })


        imageViewNext?.setOnClickListener { compactCalendarView?.scrollRight() }
        imageViewPreview?.setOnClickListener { compactCalendarView?.scrollLeft() }

        binding.buttonSubmit.setOnClickListener {

            markAbsentListener.onFullPresentListener(strDate,strDateFormatDDMMMYYYY)
        }
    }


    companion object {
        var TAG = "BottomSheetFragment"
    }
}