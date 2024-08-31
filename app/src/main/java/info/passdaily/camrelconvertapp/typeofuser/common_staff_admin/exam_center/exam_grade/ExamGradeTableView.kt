package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.exam_grade

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import com.android.volley.*
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.progress_card.SampleObject
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.progress_card.SampleObject1
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.services.Global
import java.util.*



class ExamGradeTableView @JvmOverloads constructor(
    context: Context,
    var header: Array<String>,
    // var gradeList: ArrayList<ArrayList<GradeCommonModel.GradeList>>,
) : RelativeLayout(context) {
    var TAG = "Tablelayout"

    // var header = arrayOf<String>()

    var tableA: TableLayout? = null
    var tableB: TableLayout? = null
    var tableC: TableLayout? = null
    var tableD: TableLayout? = null
    var tableMG: TableLayout? = null
    var horizontalScrollViewB: HorizontalScrollView? = null
    var horizontalScrollViewD: HorizontalScrollView? = null
    var horizontalScrollViewMG: HorizontalScrollView? = null
    var scrollViewC: ScrollView? = null
    var scrollViewD: ScrollView? = null
    var sampleObjects: List<SampleObject>? = null
    var sampleObjects1: List<SampleObject1> = ArrayList()

    // int HeaderCellWidth[] = new int[header.length];
    lateinit var HeaderCellWidth: IntArray
    lateinit var tableCWidth: IntArray


    private fun sampleObjects() : List<SampleObject> {
        val sampleObjects: List<SampleObject> = ArrayList()

        for (y in Global.getGradeMark.indices) {
            var temp = ""

            for (z in Global.getGradeMark[y].indices) {
                temp += if(z+1 == Global.getGradeMark[y].size){
                    Global.getGradeMark[y][z].gRADECOUNT.toString()
                }else{
                    Global.getGradeMark[y][z].gRADECOUNT.toString() + "~"
                }
//                    }
                Log.i(TAG, "temp $y ${ Global.getGradeMark[y][z].gRADECOUNT}")
                //   marks += temp
            }

            var taleRowForTableD = TableRow(context)
            val info = temp.split("~".toRegex()).toTypedArray()
//                  val array3: ArrayList<String> = ArrayList()
//                  Collections.addAll(array3, *info)


            //for(int x=0 ; x<loopCount; x++){
            for (l in info.indices) {
                //    TableRow.LayoutParams params = new TableRow.LayoutParams( HeaderCellWidth[x+1],LayoutParams.MATCH_PARENT);
                val params = TableRow.LayoutParams(230, LayoutParams.MATCH_PARENT)
                params.setMargins(2, 2, 2, 0)

                // TextView textViewB = this.bodyTextView(info[x]);

                // TextView textViewB = this.bodyTextView(info[x]);
                val textViewB = bodyTextView(info[l])
                val typeface = ResourcesCompat.getFont(
                    context, R.font.poppins_medium
                )
                textViewB.setTypeface(typeface)
                textViewB.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.text_size_02)
                )
                textViewB.setTextColor(resources.getColor(R.color.gray_600))
                textViewB.setBackgroundResource(R.drawable.cellshapes_two_white)
                taleRowForTableD.addView(textViewB, params)
            }
            tableD!!.addView(taleRowForTableD)

        }

        return sampleObjects
    }


    init {
//            //MarkLnvn/ProgressReportLnvn?AccademicId=6&ClassId=5&ExamId=1&AdminId=1&Dummy=0
//            GET_MarkList =
//                FileUtils.url + "MarkLnvn/ProgressReportLnvn?AccademicId=" + 8 +
//                        "&ClassId=" + 17 + "&ExamId=" + 1 + "&AdminId=" + 1 + "&Dummy=0"
//            RunGetFunMarkList(GET_MarkList, context)

        HeaderCellWidth = IntArray(header.size)
        tableCWidth = IntArray(Global.getGradeMark[0].size + 2)

        newTable_init()
    }


    //Table Data Feed
    fun newTable_init() {
        initComponents()
        sampleObjects = sampleObjects()
        setComponentsId()
        setScrollViewAndHorizontalScrollViewTag()
        horizontalScrollViewB!!.addView(tableB)
        scrollViewC!!.addView(tableC)
        scrollViewD!!.addView(horizontalScrollViewD)
        horizontalScrollViewD!!.addView(tableD)
        addComponentToMainLayout()
        addTableRowToTableA()
        addTableRowToTableB()
        resizeHeaderHeight()
        tableRowHeaderCellWidth
        generateTableC_AndTable_B()
        resizeBodyTableRowHeight()
    }

    /////initial Components list
    private fun initComponents() {
        tableA = TableLayout(context)
        tableB = TableLayout(context)
        tableC = TableLayout(context)
        tableD = TableLayout(context)
        horizontalScrollViewB = MyHorizontalScrollView(context)
        horizontalScrollViewD = MyHorizontalScrollView(context)
        scrollViewC = MyScrollView(context)
        scrollViewD = MyScrollView(context)

        //Log.i(TAG,"subjectlist "+ this.subjectlist.size());
    }

    ///named scrollview and Horizontal Tags
    private fun setScrollViewAndHorizontalScrollViewTag() {
        horizontalScrollViewB!!.tag = "horizontal scroll view b"
        horizontalScrollViewD!!.tag = "horizontal scroll view d"
        scrollViewC!!.tag = "scroll view c"
        scrollViewD!!.tag = "scroll view d"
    }

    ////set id for tables
    @SuppressLint("ResourceType")
    private fun setComponentsId() {
        tableA!!.id = 1
        horizontalScrollViewB!!.id = 2
        scrollViewC!!.id = 3
        scrollViewD!!.id = 4
    }

    private fun addComponentToMainLayout() {
        val componentB_Params =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        componentB_Params.addRule(RIGHT_OF, tableA!!.id)
        val componentC_Params =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        componentC_Params.addRule(BELOW, tableA!!.id)
        val componentD_Params =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        componentD_Params.addRule(RIGHT_OF, scrollViewC!!.id)
        componentD_Params.addRule(BELOW, horizontalScrollViewB!!.id)
        this.addView(tableA)
        this.addView(horizontalScrollViewB, componentB_Params)
        this.addView(scrollViewC, componentC_Params)
        this.addView(scrollViewD, componentD_Params)
    }

    private fun addTableRowToTableA() {
        tableA!!.addView(componentATableRow())
        //   this.tableA.addView(this.componentA_MaxMark_TableRow());
        //    this.tableA.addView(this.componentA_PassMark_TableRow());
        //    this.tableA.addView(this.componentA_SubWisePass_TableRow());
        //   this.tableA.addView(this.componentA_SubWisePassPercentage_TableRow());
        //   this.tableA.addView(this.componentA_DummyRow_TableRow());
    }

    private fun addTableRowToTableB() {
        tableB!!.addView(componentBTableRow())
        //            this.tableB.addView(this.componentB_MaxMark_TableRow());
//            this.tableB.addView(this.componentB_PassMark_TableRow());
//
//            this.tableB.addView(this.componentB_SubWisePass_TableRow());
//            this.tableB.addView(this.componentB_SubWisePassPercentage_TableRow());
//            this.tableB.addView(this.componentB_DummyRow_TableRow());
    }

    ///// row 1 header
    fun componentATableRow(): TableRow {
        val componentATabele = TableRow(context)
        val params = TableRow.LayoutParams(330, LayoutParams.MATCH_PARENT)
        params.setMargins(2, 0, 0, 0)
        val textView = headerTextView(header[0])
        val typeface = ResourcesCompat.getFont(
            context, R.font.poppins_bold
        )
        textView.typeface = typeface
        textView.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.text_size_02)
        )
        textView.setTextColor(resources.getColor(R.color.black))
        textView.setBackgroundResource(R.drawable.cellshapes_white)
        textView.layoutParams = params
        componentATabele.setBackgroundColor(resources.getColor(R.color.white))
        componentATabele.addView(textView)
        return componentATabele
    }

    /////row 1 fields
    fun componentBTableRow(): TableRow {
        val componentBTableRow = TableRow(context)
        val headerFieldCount = Global.getGradeMark[0].size
        val params = TableRow.LayoutParams(230, LayoutParams.MATCH_PARENT)
        params.setMargins(2, 0, 2, 0)


        //may be want to changes here
        for (i in 0 until headerFieldCount) {
            //  TextView textView =this.headerTextView(array3.get(i+1).substring(0,3));
            val textView = headerTextView(Global.getGradeMark[0][i].gRADENAME)
            val typeface = ResourcesCompat.getFont(
                context, R.font.poppins_medium
            )
            textView.typeface = typeface
            textView.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(R.dimen.text_size_02)
            )
            textView.setTextColor(resources.getColor(R.color.gray_600))
            textView.layoutParams = params
            textView.setBackgroundResource(R.drawable.cellshapes_white)
            textView.setTextColor(Color.BLACK)
            componentBTableRow.addView(textView)
        }
        componentBTableRow.setBackgroundColor(resources.getColor(R.color.white))
        return componentBTableRow
    }

    private fun resizeHeaderHeight() {
        val productNameHeaderTableRow = tableA!!.getChildAt(0) as TableRow
        val produceInfoTableRow = tableB!!.getChildAt(0) as TableRow
        val rowAheight = viewHeight(productNameHeaderTableRow)
        val rowBheight = viewHeight(produceInfoTableRow)
        val tableRow =
            if (rowAheight < rowBheight) productNameHeaderTableRow else produceInfoTableRow
        val finalHeight = if (rowAheight > rowBheight) rowAheight else rowBheight
        matchLayoutHeight(tableRow, finalHeight)
    }

    val tableRowHeaderCellWidth: Unit
        get() {
            val tableAChildCount = (tableA!!.getChildAt(0) as TableRow).childCount
            val tableBChildCount = (tableB!!.getChildAt(0) as TableRow).childCount
            for (i in 0 until tableAChildCount + tableBChildCount) {
                if (i == 0) {
                    HeaderCellWidth[i] =
                        viewWidth((tableA!!.getChildAt(0) as TableRow).getChildAt(i))
                } else {
                    tableCWidth[i] =
                        viewWidth((tableB!!.getChildAt(0) as TableRow).getChildAt(i - 1))
                }
            }
        }

    private fun generateTableC_AndTable_B() {
        for (i in 1 until header.size) {
            val tableRowForTableC = tableRowForTableC(header[i])
            // TableRow tableRowForTableD =this.tableRowForTableD(sampleObject,j);
            tableC!!.addView(tableRowForTableC)
        }
    }

    fun tableRowForTableC(subName: String?): TableRow {
        // TableRow.LayoutParams params = new TableRow.LayoutParams( HeaderCellWidth[0],LayoutParams.MATCH_PARENT);
        val params = TableRow.LayoutParams(330, LayoutParams.MATCH_PARENT)
        params.setMargins(2, 2, 2, 0)
        val tableRowForTableC = TableRow(context)
        val textView = bodyTextView(subName)
        //     TextView textView = this.bodyTextView(header[0]);
        val typeface = ResourcesCompat.getFont(
            context, R.font.poppins_bold
        )
        textView.typeface = typeface
        textView.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.text_size_02)
        )
        textView.setTextColor(resources.getColor(R.color.gray_600))
        tableRowForTableC.addView(textView, params)
        textView.setBackgroundResource(R.drawable.cellshapes_white)
        return tableRowForTableC
    }

    // resize body table row height
    fun resizeBodyTableRowHeight() {
        val tableC_ChildCount = tableC!!.childCount
        for (x in 0 until tableC_ChildCount) {
            val productNameHeaderTableRow = tableC!!.getChildAt(x) as TableRow
            val productInfoTableRow = tableD!!.getChildAt(x) as TableRow
            val rowAHeight = viewHeight(productNameHeaderTableRow)
            val rowBHeight = viewHeight(productInfoTableRow)
            val tableRow =
                if (rowAHeight < rowBHeight) productNameHeaderTableRow else productInfoTableRow
            val finalHeight = if (rowAHeight > rowBHeight) rowAHeight else rowBHeight
            matchLayoutHeight(tableRow, finalHeight)
        }
    }

    private fun matchLayoutHeight(tableRow: TableRow, finalHeight: Int) {
        val tableRowChildCount = tableRow.childCount
        if (tableRow.childCount == 1) {
            val view = tableRow.getChildAt(0)
            val params = view.layoutParams as TableRow.LayoutParams
            params.height = finalHeight - (params.bottomMargin + params.topMargin)
        }
        for (i in 0 until tableRowChildCount) {
            val view = tableRow.getChildAt(i)
            val params = view.layoutParams as TableRow.LayoutParams
            if (!isTheHeighestLayout(tableRow, i)) {
                params.height = finalHeight - (params.bottomMargin + params.topMargin)
                return
            }
        }
    }

    private fun isTheHeighestLayout(tableRow: TableRow, layoutposition: Int): Boolean {
        val tableRowChildCount = tableRow.childCount
        var heightViewPosition = -1
        var viewHeight = 0
        for (i in 0 until tableRowChildCount) {
            val view = tableRow.getChildAt(i)
            val height = viewHeight(view)
            if (viewHeight < height) {
                heightViewPosition = i
                viewHeight = height
            }
        }
        return heightViewPosition == layoutposition
    }

    private fun viewWidth(view: View): Int {
        view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        return view.measuredWidth
    }

    private fun viewHeight(view: View): Int {
        view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        return view.measuredHeight
    }

    // table cell standard TextView
    fun bodyTextView(label: String?): TextView {
        val bodyTextView = TextView(context)
        bodyTextView.setBackgroundColor(Color.WHITE)
        bodyTextView.text = label
        bodyTextView.gravity = Gravity.CENTER
        bodyTextView.setPadding(10, 10, 10, 10)
        return bodyTextView
    }

    // header standard TextView
    fun headerTextView(label: String?): TextView {
        val headerTextView = TextView(context)
        headerTextView.setBackgroundColor(Color.WHITE)
        headerTextView.text = label
        headerTextView.gravity = Gravity.CENTER
        headerTextView.setPadding(15, 15, 15, 15)
        return headerTextView
    }

    internal inner class MyHorizontalScrollView(context: Context?) :
        HorizontalScrollView(context) {
        override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
            val tag = this.tag as String
            if (tag.equals("horizontal scroll view b", ignoreCase = true)) {
                horizontalScrollViewD!!.scrollTo(l, 0)
            } else {
                horizontalScrollViewB!!.scrollTo(l, 0)
            }
        }
    }

    // scroll view custom class
    internal inner class MyScrollView(context: Context?) :
        ScrollView(context) {
        override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
            val tag = this.tag as String
            if (tag.equals("scroll view c", ignoreCase = true)) {
                scrollViewD!!.scrollTo(0, t)
            } else {
                scrollViewC!!.scrollTo(0, t)
            }
        }
    }
}

//@SuppressLint("ValidFragment")
//class ExamGradeTableView : Fragment {
//    var year: String? = null
//    var exam: String? = null
//    var sclass: String? = null
//    var s_cname: String? = null
//    var s_ename: String? = null
//    var aCCADEMICID = 0
//    var cLASSID = 0
//    var eXAMID = 0
//    var adminId = 0
//    var cLASSNAME: String? = null
//
//    constructor() {}
//    constructor(aCCADEMICID: Int, cLASSID: Int, eXAMID: Int, adminId: Int, cLASSNAME: String?) {
//        this.aCCADEMICID = aCCADEMICID
//        this.cLASSID = cLASSID
//        this.eXAMID = eXAMID
//        this.adminId = adminId
//        this.cLASSNAME = cLASSNAME
//    }
//
//    override fun onCreateView(
//        layoutInflater: LayoutInflater,
//        viewGroup: ViewGroup?,
//        saveInstanceState: Bundle?
//    ): View? {
//        aCCADEMICID = 8
//        cLASSID = 17
//        eXAMID = 1
//        adminId = 1
//        cLASSNAME = "IX-A"
//        val view: ExamGrade_TableView = ExamGrade_TableView(
//            activity, year, exam, sclass
//        )
//        view.setBackgroundColor(Color.WHITE)
//        return view
//    }
//
//    inner class ExamGrade_TableView(
//        var context: Context,
//        var year: String,
//        var exam: String,
//        var sclass: String
//    ) : RelativeLayout(
//        context
//    ) {
//        var TAG = "Tablelayout"
//        var mRequestQueue: RequestQueue? = null
//
//        //    String GET_year_subject_class= Global.url+"Mark/MarkEntryPageLoad?AdminId="+Global.Admin_id;
//        //  http://demostaff.passdaily.in/ElixirApi/Mark/MarkEntryPageLoad?AdminId=1
//        var GET_MarkList = FileUtils.url + "MarkLnvn/ProgressReportLnvn?AccademicId="
//
//        ///http://demostaff.passdaily.in/ElixirApi/MarkLnvn/ProgressReportLnvn?AccademicId=6&ClassId=5&ExamId=1&AdminId=1&Dummy=0
//        var studentname: ArrayList<HashMap<String, String>>? = null
//        var subjectlist: ArrayList<HashMap<String, String>>? = null
//        var markList: ArrayList<HashMap<String, String>>? = null
//        var stafflist: ArrayList<HashMap<String, String>>? = null
//        var header = arrayOf<String>()
//        var maxMark = arrayOf(" Max Marks ", "", "", "", "", "", "", "", "")
//        var passMark = arrayOf(" Pass Marks ", "", "", "", "", "", "", "", "")
//        var subWisePass = arrayOf(" Subject Wise Pass ", "", "", "", "", "", "", "", "")
//        var subPassPercentage = arrayOf(" Subject Wise % ", "", "", "", "", "", "", "", "")
//        var dummayrow = arrayOf(" STUDENTS")
//        var titleValues = arrayOf("A1", "A2", "B1", "B2", "C1", "C2", " D ", " E ")
//        var tableA: TableLayout? = null
//        var tableB: TableLayout? = null
//        var tableC: TableLayout? = null
//        var tableD: TableLayout? = null
//        var tableMG: TableLayout? = null
//        var horizontalScrollViewB: HorizontalScrollView? = null
//        var horizontalScrollViewD: HorizontalScrollView? = null
//        var horizontalScrollViewMG: HorizontalScrollView? = null
//        var scrollViewC: ScrollView? = null
//        var scrollViewD: ScrollView? = null
//        var sampleObjects: List<SampleObject>? = null
//        var sampleObjects1: List<SampleObject1> = ArrayList()
//
//        // int HeaderCellWidth[] = new int[header.length];
//        var HeaderCellWidth: IntArray
//        private fun sampleObjects(): List<SampleObject> {
//            val sampleObjects: MutableList<SampleObject> = ArrayList()
//            var flcnt = 0
//            var passcnt = 0
//            var status = "FP"
//            var tot = 0.00
//            var outoffmark = 0.00
//            var nosub = 0
//            var a1 = 0
//            var a2 = 0
//            val i = 0
//            var subj = ""
//            for (y in subjectlist!!.indices) {
//                subj += subjectlist!![y]["SUBJECT_NAME"] + "~"
//            }
//            val subjt = subj.split("~".toRegex()).toTypedArray()
//            for (x in studentname!!.indices) {
//                var `var` = ""
//                val row1 = "-"
//                val row2 = "-"
//                val row3 = "-"
//                val row4 = "-"
//                val row5 = "-"
//                val row6 = "-"
//                val row7 = "-"
//                flcnt = 0
//                passcnt = 0
//                a1 = 0
//                a2 = 0
//                nosub = 0
//                tot = 0.00
//                outoffmark = 0.00
//                for (y in subjectlist!!.indices) {
//                    var temp = "-~"
//                    for (z in markList!!.indices) {
//                        if (markList!![z]["TOTAL_MARK"] == "-1.0") {
//                            markList!![z]["TOTAL_MARK"] = "AB"
//                        }
//                        if (studentname!![x]["STUDENT_ROLL_NUMBER"] == markList!![z]["STUDENT_ROLL_NUMBER"]) {
//                            if (subjectlist!![y]["SUBJECT_ID"] == markList!![z]["SUBJECT_ID"]) {
//                                val n = subjectlist!![y]["SUBJECT_NAME"]
//                                Log.i(TAG, "TOTAL_MARK " + markList!![z]["TOTAL_MARK"])
//                                for (c in subjt.indices) {
//                                    if (subjt[c] == n) {
//                                        val d = c + 1
//                                        if (markList!![z]["TOTAL_MARK"]!!.contains("AB")) {
//                                            temp = markList!![z]["TOTAL_MARK"] + "~"
//                                            flcnt++
//                                            val outmark = markList!![z]["OUTOFF_MARK"]!!
//                                                .toFloat()
//                                            outoffmark += outmark.toDouble()
//                                        } else {
//                                            temp = markList!![z]["MARK_GRADE"] + "~"
//                                            val passmrk = subjectlist!![y]["PASS_MARK"]!!
//                                                .toFloat()
//                                            val mark = markList!![z]["TOTAL_MARK"]!!
//                                                .toFloat()
//                                            val outmark = markList!![z]["OUTOFF_MARK"]!!
//                                                .toFloat()
//                                            outoffmark += outmark.toDouble()
//                                            tot += mark.toDouble()
//                                            if (mark >= passmrk) { // old code
//                                                //    if (mark > passmrk){
//                                                passcnt++
//                                            } else {
//                                                flcnt++
//                                            }
//                                            if (markList!![z]["MARK_GRADE"]!!.contains("A1")) {
//                                                a1++
//                                            } else if (markList!![z]["MARK_GRADE"]!!.contains("A2")) {
//                                                a2++
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    `var` += temp
//                    temp = ""
//                }
//                status = if (flcnt > 0) {
//                    "FL"
//                } else {
//                    "FP"
//                }
//                ////old code
//                //  int totper=tot/nosub;
//                nosub = subjectlist!!.size
//
///////////////////////////////////////////////percentage
//                val outc1 = tot.toString().toFloat() / nosub.toString().toFloat()
//                val df = DecimalFormat("#.#")
//                //   df.setRoundingMode(RoundingMode.DOWN);
//                // Log.i(TAG,"salary : " + df.format(outc1));
//                val v = df.format(outc1.toDouble()).toFloat() * 10
//                val per = v.toInt().toString()
//
////////////////////new corrected
//                val outc11 = tot.toString().toFloat() / outoffmark.toString().toFloat()
//                val df1 = DecimalFormat("#.#")
//                //   df.setRoundingMode(RoundingMode.DOWN);
//                // Log.i(TAG,"salary : " + df.format(outc1));
//                val v1 = outc11.toString().toFloat() * 100
//                val rr_final = Math.round(v1)
//                //                String per1 = String.valueOf((int)v1);
//
//                //    Log.i(TAG,"tot : " + df1.format(tot));
//                val sampleObject = SampleObject(
//                    studentname!![x]["STUDENT_FNAME"],
//                    studentname!![x]["STUDENT_GUARDIAN_NAME"],
//                    studentname!![x]["STUDENT_GUARDIAN_NUMBER"],
//                    row2,
//                    row3,
//                    row4,
//                    row5,
//                    row6
//                )
//                sampleObjects.add(sampleObject)
//                val taleRowForTableD = TableRow(context)
//                val info = `var`.split("~".toRegex()).toTypedArray()
//                val array3: List<String> = ArrayList()
//                Collections.addAll(array3, *info)
//
//
//                //for(int x=0 ; x<loopCount; x++){
//                for (l in 0 until array3.size) {
//                    //    TableRow.LayoutParams params = new TableRow.LayoutParams( HeaderCellWidth[x+1],LayoutParams.MATCH_PARENT);
//                    val params = TableRow.LayoutParams(230, LayoutParams.MATCH_PARENT)
//                    params.setMargins(2, 2, 2, 0)
//
//                    // TextView textViewB = this.bodyTextView(info[x]);
//                    val textViewB = bodyTextView(array3[l])
//                    val typeface = ResourcesCompat.getFont(
//                        context, info.passdaily.teach_daily_app.R.font.poppins_medium
//                    )
//                    textViewB.setTypeface(typeface)
//                    textViewB.setTextSize(
//                        TypedValue.COMPLEX_UNIT_PX,
//                        resources.getDimension(info.passdaily.teach_daily_app.R.dimen.text_size_02)
//                    )
//                    textViewB.setTextColor(resources.getColor(info.passdaily.teach_daily_app.R.color.gray_600))
//                    textViewB.setBackgroundResource(info.passdaily.teach_daily_app.R.drawable.cellshapes_two)
//                    taleRowForTableD.addView(textViewB, params)
//                }
//                tableD!!.addView(taleRowForTableD)
//            }
//            return sampleObjects
//        }
//
//        init {
//            //MarkLnvn/ProgressReportLnvn?AccademicId=6&ClassId=5&ExamId=1&AdminId=1&Dummy=0
//            GET_MarkList =
//                FileUtils.url + "MarkLnvn/ProgressReportLnvn?AccademicId=" + aCCADEMICID +
//                        "&ClassId=" + cLASSID + "&ExamId=" + eXAMID + "&AdminId=" + adminId + "&Dummy=0"
//            RunGetFunMarkList(GET_MarkList, context)
//        }
//
//        private fun RunGetFunMarkList(url: String, context: Context) {
//            Log.i(TAG, "URL $url")
//            studentname = ArrayList()
//            subjectlist = ArrayList()
//            markList = ArrayList()
//            stafflist = ArrayList()
//            val customerRequest: CustomRequest = object : CustomRequest(
//                Method.GET,
//                url,
//                null,
//                object : Response.Listener<JSONObject?> {
//                    override fun onResponse(response: JSONObject) {
//                        //  {"StudentList":[{"STUDENT_ID":67,"ADMISSION_NUMBER":"839","STUDENT_ROLL_NUMBER":1,
//                        //       "STUDENT_FNAME":"AADINATH P","CLASS_ID":5,"ACCADEMIC_ID":6}
//                        try {
//                            val jsonArray1 = response.getJSONArray("StudentList")
//                            for (i1 in 0 until jsonArray1.length()) {
//                                val STUDENT_ID = jsonArray1.getJSONObject(i1)
//                                val map1 = HashMap<String, String>()
//                                map1["STUDENT_ID"] = STUDENT_ID.getString("STUDENT_ID")
//                                map1["ADMISSION_NUMBER"] = STUDENT_ID.getString("ADMISSION_NUMBER")
//                                map1["STUDENT_ROLL_NUMBER"] =
//                                    STUDENT_ID.getString("STUDENT_ROLL_NUMBER")
//                                map1["STUDENT_FNAME"] = STUDENT_ID.getString("STUDENT_FNAME")
//                                map1["CLASS_ID"] = STUDENT_ID.getString("CLASS_ID")
//                                map1["ACCADEMIC_ID"] = STUDENT_ID.getString("ACCADEMIC_ID")
//                                map1["STUDENT_GUARDIAN_NAME"] =
//                                    STUDENT_ID.getString("STUDENT_GUARDIAN_NAME")
//                                map1["STUDENT_GUARDIAN_NUMBER"] =
//                                    STUDENT_ID.getString("STUDENT_GUARDIAN_NUMBER")
//                                studentname!!.add(map1)
//                            }
//                            ///"SubjectList":[{"SUBJECT_ID":1,"SUBJECT_NAME":"ENGLISH","PASS_MARK":3.00,
//// "OUTOFF_MARK":10.00,"SUBJECTWISE_PASS":24,"TOTAL_ATTEND":25},
//                            var head = " SUBJECTS~"
//                            var maxM = " Max Marks~"
//                            var passM = " Pass mark~"
//                            var subWsP = " Subjects Pass~"
//                            var subWsPassPer = ""
//                            val jsonArray2 = response.getJSONArray("SubjectList")
//                            for (i2 in 0 until jsonArray2.length()) {
//                                val SUBJECT_ID = jsonArray2.getJSONObject(i2)
//                                val map2 = HashMap<String, String>()
//                                map2["SUBJECT_ID"] = SUBJECT_ID.getString("SUBJECT_ID")
//                                map2["SUBJECT_NAME"] = SUBJECT_ID.getString("SUBJECT_NAME")
//                                head += SUBJECT_ID.getString("SUBJECT_NAME") + "~"
//                                map2["PASS_MARK"] = SUBJECT_ID.getString("PASS_MARK")
//                                passM += SUBJECT_ID.getString("PASS_MARK") + "~"
//                                map2["OUTOFF_MARK"] = SUBJECT_ID.getString("OUTOFF_MARK")
//                                maxM += SUBJECT_ID.getString("OUTOFF_MARK") + "~"
//                                map2["SUBJECTWISE_PASS"] = SUBJECT_ID.getString("SUBJECTWISE_PASS")
//                                subWsP += SUBJECT_ID.getString("SUBJECTWISE_PASS") + "~"
//                                map2["TOTAL_ATTEND"] = SUBJECT_ID.getString("TOTAL_ATTEND")
//                                subWsPassPer += SUBJECT_ID.getString("TOTAL_ATTEND") + "~"
//                                subjectlist!!.add(map2)
//                            }
//                            header = head.split("~".toRegex()).toTypedArray()
//                            maxMark = maxM.split("~".toRegex()).toTypedArray()
//                            passMark = passM.split("~".toRegex()).toTypedArray()
//                            subWisePass = subWsP.split("~".toRegex()).toTypedArray()
//                            subPassPercentage = subWsPassPer.split("~".toRegex()).toTypedArray()
//                            HeaderCellWidth = IntArray(header.size)
//                            //     Log.i(TAG,"inside_subjectlist "+ subjectlist.size());
//
/////"MarkList":[{"MARK_ID":838,"ACCADEMIC_ID":6,"CLASS_ID":5,"EXAM_ID":1,"SUBJECT_ID":13,
//// "STUDENT_ROLL_NUMBER":1,"STUDENT_ID":67,"PASS_MARK":3.00,"OUTOFF_MARK":10.00,"TOTAL_MARK":5.00,
//
//// "MARK_DATE":"2019-08-23T15:18:38.017","MARKED_BY":1,"MARK_GRADE":"C2"}
//                            val jsonArray3 = response.getJSONArray("MarkList")
//                            for (i3 in 0 until jsonArray3.length()) {
//                                val MARK_ID = jsonArray3.getJSONObject(i3)
//                                val map3 = HashMap<String, String>()
//                                map3["MARK_ID"] = MARK_ID.getString("MARK_ID")
//                                map3["ACCADEMIC_ID"] = MARK_ID.getString("ACCADEMIC_ID")
//                                map3["CLASS_ID"] = MARK_ID.getString("CLASS_ID")
//                                map3["SUBJECT_ID"] = MARK_ID.getString("SUBJECT_ID")
//                                map3["EXAM_ID"] = MARK_ID.getString("EXAM_ID")
//                                map3["STUDENT_ID"] = MARK_ID.getString("STUDENT_ID")
//                                map3["STUDENT_ROLL_NUMBER"] =
//                                    MARK_ID.getString("STUDENT_ROLL_NUMBER")
//                                map3["PASS_MARK"] = MARK_ID.getString("PASS_MARK")
//                                map3["OUTOFF_MARK"] = MARK_ID.getString("OUTOFF_MARK")
//                                map3["TOTAL_MARK"] = MARK_ID.getString("TOTAL_MARK")
//                                map3["MARK_DATE"] = MARK_ID.getString("MARK_DATE")
//                                map3["MARKED_BY"] = MARK_ID.getString("MARKED_BY")
//                                map3["MARK_GRADE"] = MARK_ID.getString("MARK_GRADE")
//
////                                map3.put("PASS_MARK_CE",MARK_ID.getString("PASS_MARK_CE"));
////                                map3.put("OUTOFF_MARK_CE",MARK_ID.getString("OUTOFF_MARK_CE"));
////                                map3.put("TOTAL_MARK_CE",MARK_ID.getString("TOTAL_MARK_CE"));
//                                markList!!.add(map3)
//                            }
//                            //"StaffList":[{"STAFF_ID":9,"STAFF_FNAME":"PRAVITHA ANEEESH","STAFF_MNAME":"","STAFF_LNAME":"","STAFF_PHONE_NUMBER":"9"}]}
//                            val jsonArray4 = response.getJSONArray("StaffList")
//                            for (i4 in 0 until jsonArray4.length()) {
//                                val STAFF_ID = jsonArray4.getJSONObject(i4)
//                                val map4 = HashMap<String, String>()
//                                map4["STAFF_ID"] = STAFF_ID.getString("STAFF_ID")
//                                map4["STAFF_FNAME"] = STAFF_ID.getString("STAFF_FNAME")
//                                map4["STAFF_MNAME"] = STAFF_ID.getString("STAFF_MNAME")
//                                map4["STAFF_LNAME"] = STAFF_ID.getString("STAFF_LNAME")
//                                map4["STAFF_PHONE_NUMBER"] =
//                                    STAFF_ID.getString("STAFF_PHONE_NUMBER")
//                                stafflist!!.add(map4)
//                            }
//                            newTable_init()
//                        } catch (e: JSONException) {
//                            Toast.makeText(
//                                context,
//                                "Something went wrong try after sometime.",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                },
//                Response.ErrorListener {
//                    Toast.makeText(
//                        context,
//                        "Something went wrong try after sometime.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }) {
//                override fun getParams(): Map<String, String>? {
//                    return HashMap()
//                }
//            }
//            addtoRequestQueue(customerRequest)
//        }
//
//        fun <T> addtoRequestQueue(Req: Request<T>) {
//            Req.tag = TAG
//            Req.retryPolicy = DefaultRetryPolicy(
//                20000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//            )
//            requestQueue.add(Req)
//        }
//
//        val requestQueue: RequestQueue
//            get() {
//                if (mRequestQueue == null) {
//                    mRequestQueue = Volley.newRequestQueue(context)
//                }
//                return mRequestQueue!!
//            }
//
//        //Table Data Feed
//        fun newTable_init() {
//            initComponents()
//            sampleObjects = sampleObjects()
//            setComponentsId()
//            setScrollViewAndHorizontalScrollViewTag()
//            horizontalScrollViewB!!.addView(tableB)
//            scrollViewC!!.addView(tableC)
//            scrollViewD!!.addView(horizontalScrollViewD)
//            horizontalScrollViewD!!.addView(tableD)
//            addComponentToMainLayout()
//            addTableRowToTableA()
//            addTableRowToTableB()
//            resizeHeaderHeight()
//            tableRowHeaderCellWidth
//            generateTableC_AndTable_B()
//            resizeBodyTableRowHeight()
//        }
//
//        /////initial Components list
//        private fun initComponents() {
//            tableA = TableLayout(context)
//            tableB = TableLayout(context)
//            tableC = TableLayout(context)
//            tableD = TableLayout(context)
//            horizontalScrollViewB = MyHorizontalScrollView(
//                context
//            )
//            horizontalScrollViewD = MyHorizontalScrollView(
//                context
//            )
//            scrollViewC = MyScrollView(
//                context
//            )
//            scrollViewD = MyScrollView(
//                context
//            )
//
//            //Log.i(TAG,"subjectlist "+ this.subjectlist.size());
//        }
//
//        ///named scrollview and Horizontal Tags
//        private fun setScrollViewAndHorizontalScrollViewTag() {
//            horizontalScrollViewB!!.tag = "horizontal scroll view b"
//            horizontalScrollViewD!!.tag = "horizontal scroll view d"
//            scrollViewC!!.tag = "scroll view c"
//            scrollViewD!!.tag = "scroll view d"
//        }
//
//        ////set id for tables
//        @SuppressLint("ResourceType")
//        private fun setComponentsId() {
//            tableA!!.id = 1
//            horizontalScrollViewB!!.id = 2
//            scrollViewC!!.id = 3
//            scrollViewD!!.id = 4
//        }
//
//        private fun addComponentToMainLayout() {
//            val componentB_Params =
//                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
//            componentB_Params.addRule(RIGHT_OF, tableA!!.id)
//            val componentC_Params =
//                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
//            componentC_Params.addRule(BELOW, tableA!!.id)
//            val componentD_Params =
//                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
//            componentD_Params.addRule(RIGHT_OF, scrollViewC!!.id)
//            componentD_Params.addRule(BELOW, horizontalScrollViewB!!.id)
//            this.addView(tableA)
//            this.addView(horizontalScrollViewB, componentB_Params)
//            this.addView(scrollViewC, componentC_Params)
//            this.addView(scrollViewD, componentD_Params)
//        }
//
//        private fun addTableRowToTableA() {
//            tableA!!.addView(componentATableRow())
//            //   this.tableA.addView(this.componentA_MaxMark_TableRow());
//            //    this.tableA.addView(this.componentA_PassMark_TableRow());
//            //    this.tableA.addView(this.componentA_SubWisePass_TableRow());
//            //   this.tableA.addView(this.componentA_SubWisePassPercentage_TableRow());
//            //   this.tableA.addView(this.componentA_DummyRow_TableRow());
//        }
//
//        private fun addTableRowToTableB() {
//            tableB!!.addView(componentBTableRow())
//            //            this.tableB.addView(this.componentB_MaxMark_TableRow());
////            this.tableB.addView(this.componentB_PassMark_TableRow());
////
////            this.tableB.addView(this.componentB_SubWisePass_TableRow());
////            this.tableB.addView(this.componentB_SubWisePassPercentage_TableRow());
////            this.tableB.addView(this.componentB_DummyRow_TableRow());
//        }
//
//        ///// row 1 header
//        fun componentATableRow(): TableRow {
//            val componentATabele = TableRow(context)
//            val params = TableRow.LayoutParams(330, LayoutParams.MATCH_PARENT)
//            params.setMargins(2, 0, 0, 0)
//            val textView = headerTextView(header[0])
//            val typeface = ResourcesCompat.getFont(
//                context, R.font.poppins_bold
//            )
//            textView.typeface = typeface
//            textView.setTextSize(
//                TypedValue.COMPLEX_UNIT_PX,
//                resources.getDimension(R.dimen.text_size_02)
//            )
//            textView.setTextColor(resources.getColor(R.color.black))
//            textView.setBackgroundResource(R.drawable.cellshapes)
//            textView.layoutParams = params
//            componentATabele.setBackgroundColor(resources.getColor(R.color.green_100))
//            componentATabele.addView(textView)
//            return componentATabele
//        }
//
//        /////row 1 fields
//        fun componentBTableRow(): TableRow {
//            val componentBTableRow = TableRow(context)
//            val headerFieldCount = titleValues.size
//            val params = TableRow.LayoutParams(230, LayoutParams.MATCH_PARENT)
//            params.setMargins(2, 0, 2, 0)
//
//
//            //may be want to changes here
//            for (i in 0 until headerFieldCount - 1) {
//                //  TextView textView =this.headerTextView(array3.get(i+1).substring(0,3));
//                val textView = headerTextView(titleValues[i])
//                val typeface = ResourcesCompat.getFont(
//                    context, R.font.poppins_medium
//                )
//                textView.typeface = typeface
//                textView.setTextSize(
//                    TypedValue.COMPLEX_UNIT_PX,
//                    resources.getDimension(R.dimen.text_size_02)
//                )
//                textView.setTextColor(resources.getColor(R.color.gray_600))
//                textView.layoutParams = params
//                textView.setBackgroundResource(R.drawable.cellshapes)
//                textView.setTextColor(Color.BLACK)
//                componentBTableRow.addView(textView)
//            }
//            componentBTableRow.setBackgroundColor(resources.getColor(R.color.green_100))
//            return componentBTableRow
//        }
//
//        private fun resizeHeaderHeight() {
//            val productNameHeaderTableRow = tableA!!.getChildAt(0) as TableRow
//            val produceInfoTableRow = tableB!!.getChildAt(0) as TableRow
//            val rowAheight = viewHeight(productNameHeaderTableRow)
//            val rowBheight = viewHeight(produceInfoTableRow)
//            val tableRow =
//                if (rowAheight < rowBheight) productNameHeaderTableRow else produceInfoTableRow
//            val finalHeight = if (rowAheight > rowBheight) rowAheight else rowBheight
//            matchLayoutHeight(tableRow, finalHeight)
//        }
//
//        val tableRowHeaderCellWidth: Unit
//            get() {
//                val tableAChildCount = (tableA!!.getChildAt(0) as TableRow).childCount
//                val tableBChildCount = (tableB!!.getChildAt(0) as TableRow).childCount
//                for (i in 0 until tableAChildCount + tableBChildCount) {
//                    if (i == 0) {
//                        HeaderCellWidth[i] =
//                            viewWidth((tableA!!.getChildAt(0) as TableRow).getChildAt(i))
//                    } else {
//                        HeaderCellWidth[i] =
//                            viewWidth((tableB!!.getChildAt(0) as TableRow).getChildAt(i - 1))
//                    }
//                }
//            }
//
//        private fun generateTableC_AndTable_B() {
//            for (i in 1 until header.size) {
//                val tableRowForTableC = tableRowForTableC(header[i])
//                // TableRow tableRowForTableD =this.tableRowForTableD(sampleObject,j);
//                tableC!!.addView(tableRowForTableC)
//            }
//        }
//
//        fun tableRowForTableC(subName: String?): TableRow {
//            // TableRow.LayoutParams params = new TableRow.LayoutParams( HeaderCellWidth[0],LayoutParams.MATCH_PARENT);
//            val params = TableRow.LayoutParams(330, LayoutParams.MATCH_PARENT)
//            params.setMargins(2, 2, 2, 0)
//            val tableRowForTableC = TableRow(context)
//            val textView = bodyTextView(subName)
//            //     TextView textView = this.bodyTextView(header[0]);
//            val typeface = ResourcesCompat.getFont(
//                context, R.font.poppins_bold
//            )
//            textView.typeface = typeface
//            textView.setTextSize(
//                TypedValue.COMPLEX_UNIT_PX,
//                resources.getDimension(R.dimen.text_size_02)
//            )
//            textView.setTextColor(resources.getColor(R.color.gray_600))
//            tableRowForTableC.addView(textView, params)
//            textView.setBackgroundResource(R.drawable.cellshapes)
//            return tableRowForTableC
//        }
//
//        // resize body table row height
//        fun resizeBodyTableRowHeight() {
//            val tableC_ChildCount = tableC!!.childCount
//            for (x in 0 until tableC_ChildCount) {
//                val productNameHeaderTableRow = tableC!!.getChildAt(x) as TableRow
//                val productInfoTableRow = tableD!!.getChildAt(x) as TableRow
//                val rowAHeight = viewHeight(productNameHeaderTableRow)
//                val rowBHeight = viewHeight(productInfoTableRow)
//                val tableRow =
//                    if (rowAHeight < rowBHeight) productNameHeaderTableRow else productInfoTableRow
//                val finalHeight = if (rowAHeight > rowBHeight) rowAHeight else rowBHeight
//                matchLayoutHeight(tableRow, finalHeight)
//            }
//        }
//
//        private fun matchLayoutHeight(tableRow: TableRow, finalHeight: Int) {
//            val tableRowChildCount = tableRow.childCount
//            if (tableRow.childCount == 1) {
//                val view = tableRow.getChildAt(0)
//                val params = view.layoutParams as TableRow.LayoutParams
//                params.height = finalHeight - (params.bottomMargin + params.topMargin)
//            }
//            for (i in 0 until tableRowChildCount) {
//                val view = tableRow.getChildAt(i)
//                val params = view.layoutParams as TableRow.LayoutParams
//                if (!isTheHeighestLayout(tableRow, i)) {
//                    params.height = finalHeight - (params.bottomMargin + params.topMargin)
//                    return
//                }
//            }
//        }
//
//        private fun isTheHeighestLayout(tableRow: TableRow, layoutposition: Int): Boolean {
//            val tableRowChildCount = tableRow.childCount
//            var heightViewPosition = -1
//            var viewHeight = 0
//            for (i in 0 until tableRowChildCount) {
//                val view = tableRow.getChildAt(i)
//                val height = viewHeight(view)
//                if (viewHeight < height) {
//                    heightViewPosition = i
//                    viewHeight = height
//                }
//            }
//            return heightViewPosition == layoutposition
//        }
//
//        private fun viewWidth(view: View): Int {
//            view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
//            return view.measuredWidth
//        }
//
//        private fun viewHeight(view: View): Int {
//            view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
//            return view.measuredHeight
//        }
//
//        // table cell standard TextView
//        fun bodyTextView(label: String?): TextView {
//            val bodyTextView = TextView(context)
//            bodyTextView.setBackgroundColor(Color.WHITE)
//            bodyTextView.text = label
//            bodyTextView.gravity = Gravity.CENTER
//            bodyTextView.setPadding(10, 10, 10, 10)
//            return bodyTextView
//        }
//
//        // header standard TextView
//        fun headerTextView(label: String?): TextView {
//            val headerTextView = TextView(context)
//            headerTextView.setBackgroundColor(Color.WHITE)
//            headerTextView.text = label
//            headerTextView.gravity = Gravity.CENTER
//            headerTextView.setPadding(15, 15, 15, 15)
//            return headerTextView
//        }
//
//        internal inner class MyHorizontalScrollView(context: Context?) :
//            HorizontalScrollView(context) {
//            override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
//                val tag = this.tag as String
//                if (tag.equals("horizontal scroll view b", ignoreCase = true)) {
//                    horizontalScrollViewD!!.scrollTo(l, 0)
//                } else {
//                    horizontalScrollViewB!!.scrollTo(l, 0)
//                }
//            }
//        }
//
//        // scroll view custom class
//        internal inner class MyScrollView(context: Context?) : ScrollView(context) {
//            override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
//                val tag = this.tag as String
//                if (tag.equals("scroll view c", ignoreCase = true)) {
//                    scrollViewD!!.scrollTo(0, t)
//                } else {
//                    scrollViewC!!.scrollTo(0, t)
//                }
//            }
//        }
//    }
//}