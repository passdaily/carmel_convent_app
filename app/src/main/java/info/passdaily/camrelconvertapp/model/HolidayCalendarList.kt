package info.passdaily.camrelconvertapp.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class HolidayCalendarList(
    @SerializedName("HolidayList")
    val holidayList: ArrayList<HolidayList>){
        @Keep
        data class HolidayList(
            @SerializedName("WORKING_ID")
            val wORKINGID: Int,
            @SerializedName("WORKING_DAY")
            val wORKINGDAY: Int,
            @SerializedName("WORKING_MONTH")
            val wORKINGMONTH: Int,
            @SerializedName("WORKING_YEAR")
            val wORKINGYEAR: Int,
            @SerializedName("ACCADEMIC_YEAR")
            val aCCADEMICYEAR: Int,
            @SerializedName("WORKING_DESCRIPTION")
            val wORKINGDESCRIPTION: String,
            @SerializedName("WORKING_DATE")
            val wORKINGDATE: String,
            @SerializedName("WORKING_STATUS")
            val wORKING_STATUS: Any,
            @SerializedName("ACCADEMIC_TIME")
            val aCCADEMICTIME: String
        )
    }
