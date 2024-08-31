package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class LibraryModel(
    @SerializedName("BookIssueDetails")
    val bookIssueDetails: ArrayList<BookIssueDetail>
){
    @Keep
    data class BookIssueDetail(
        @SerializedName("BOOK_AUTHOR")
        val bOOKAUTHOR: String,
        @SerializedName("BOOK_CODE")
        val bOOKCODE: String,
        @SerializedName("BOOK_NAME")
        val bOOKNAME: String,
        @SerializedName("BOOK_PRICE")
        val bOOKPRICE: String,
        @SerializedName("BOOK_SUMMARY")
        val bOOKSUMMARY: String,
        @SerializedName("EXPECTED_DATE")
        val eXPECTEDDATE: String,
        @SerializedName("FINE_AMOUNT")
        val fINEAMOUNT: Any,
        @SerializedName("FINE_STATUS")
        val fINESTATUS: Int,
        @SerializedName("ISSUE_DATE")
        val iSSUEDATE: String,
        @SerializedName("PAID_AMOUNT")
        val pAIDAMOUNT: Int,
        @SerializedName("RETURNED_DATE")
        val rETURNEDDATE: String,
        @SerializedName("RETURN_STATUS")
        val rETURNSTATUS: Int,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int
    )
}