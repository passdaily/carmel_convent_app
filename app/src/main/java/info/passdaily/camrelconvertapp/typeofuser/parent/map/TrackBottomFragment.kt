package info.passdaily.camrelconvertapp.typeofuser.parent.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.services.Utils

class TrackBottomFragment : BottomSheetDialogFragment {

    var date = ""
    var speed =""
    var powerAcc= ""
    var VEHICLE_NAME = ""

    var textViewTitle : TextView? = null
    var textViewTime : TextView? = null
    var textViewIdle : TextView? = null
    var textViewSpeed: TextView? = null
    var textViewDate: TextView? = null

    var imageViewPower : ImageView? = null
    var imageViewSignal : ImageView? = null
    var imageViewBattery : ImageView? = null

    constructor(VEHICLE_NAME: String,date: String, speed: String, powerAcc: String){
        this.VEHICLE_NAME = VEHICLE_NAME
        this.date = date
        this.speed = speed
        this.powerAcc = powerAcc
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_track_bottom, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewDate = view.findViewById(R.id.textViewDate) as TextView
        textViewTime = view.findViewById(R.id.textViewTime) as TextView
        textViewTitle = view.findViewById(R.id.textViewTitle) as TextView
        textViewIdle = view.findViewById(R.id.textViewIdle) as TextView
        textViewSpeed = view.findViewById(R.id.textViewSpeed) as TextView

        val date1: Array<String> = date.split("T".toRegex()).toTypedArray()
        val ddddd: Long = Utils.longconversion(date1[0] + " " + date1[1])

        textViewTime?.text =Utils.formattedTime(ddddd)
        textViewDate?.text =Utils.dateformat(date1[0])
        textViewTitle?.text =VEHICLE_NAME
        textViewSpeed?.text = "$speed Km/hr"
        textViewIdle?.text = "00:00 hr"
    }

    companion object {
        var TAG = "TrackBottomFragment"
    }
}