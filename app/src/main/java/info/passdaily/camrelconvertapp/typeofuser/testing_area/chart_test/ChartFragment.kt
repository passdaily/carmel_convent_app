package info.passdaily.camrelconvertapp.typeofuser.testing_area.chart_test

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Funnel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentChartBinding
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener


class ChartFragment : Fragment() {

    var TAG  = "ChartFragment"

    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!

    // variable for our bar chart
    var barChart: BarChart? = null
    // variable for our bar data.
    var barData: BarData? = null
    // variable for our bar data set.
    var barDataSet: BarDataSet? = null
    // array list for storing entries.
    var barEntriesArrayList =  ArrayList<BarEntry>()


    var pieDataSet: PieDataSet? = null
    var pieData: PieData? = null
    var pieChart: PieChart? = null
    // array list for storing entries.
    var pieEntriesArrayList =  ArrayList<PieEntry>()

    var toolBarClickListener : ToolBarClickListener? = null


    var waveChart: LineChart? = null

    var lineChartEntries =  ArrayList<Entry>()

    var lineDataSet: LineDataSet? = null

    var lineData :LineData? = null


    var pointChart : LineChart? = null

    var firstEntries =  ArrayList<Entry>()
    var secondEntries =  ArrayList<Entry>()
    var thirdEntries =  ArrayList<Entry>()

    var pointDataSet: LineDataSet? = null

    var funnelChart  : AnyChartView? = null


    var mContext : Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(mContext ==null){
            mContext = context.applicationContext
        }
        try {
            toolBarClickListener = context as ToolBarClickListener
        }catch(e : Exception){
            Log.i(TAG,"Exception $e")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_chart, container, false)
        toolBarClickListener?.setToolbarName("Chart Page")

        _binding = FragmentChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initializing variable for bar chart.
        // initializing variable for bar chart.
        barChart = binding.barChart

        pieChart = binding.pieChart

        waveChart = binding.waveChart

        pointChart = binding.pointChart


        funnelChart = binding.funnelChart

        barChart!!.axisLeft?.setDrawGridLines(false)
        val bxAxis: XAxis = barChart!!.xAxis
        bxAxis.setDrawGridLines(false)
        bxAxis.setDrawAxisLine(false)
        //remove right y-axis
        barChart!!.axisRight.isEnabled = false

        // calling method to get bar entries.
        getBarEntries()

        // creating a new bar data set.
        barDataSet = BarDataSet(barEntriesArrayList, "Bar Chart")


        // creating a new bar data and
        // passing our bar data set.
        barData = BarData(barDataSet)


        // below line is to set data
        // to our bar chart.
        barChart!!.data = barData

        // adding color to our bar data set.
        barDataSet!!.setColors(*ColorTemplate.MATERIAL_COLORS)

        // setting text color.
        barDataSet!!.valueTextColor = Color.BLACK

        // setting text size
        barDataSet!!.valueTextSize = 16f
        barChart!!.description.isEnabled = false

        barChart!!.animateY(300);

        getPieEntries()
        pieDataSet = PieDataSet(pieEntriesArrayList, "Pie Chart")

        pieDataSet!!.setColors(*ColorTemplate.JOYFUL_COLORS);
        pieDataSet!!.sliceSpace = 2f;
        pieDataSet!!.valueTextColor = Color.WHITE;
        pieDataSet!!.valueTextSize = 12f;

        pieData = PieData(pieDataSet)

        pieChart!!.data = pieData;

        pieChart!!.animateY(300);




        ///

        waveChart!!.setDrawGridBackground(false);
        waveChart!!.setDrawBorders(false);
        waveChart!!.legend.isEnabled = false;
        waveChart!!.isAutoScaleMinMaxEnabled = true;
        waveChart!!.setTouchEnabled(true);
        waveChart!!.isDragEnabled = true;
        waveChart!!.setScaleEnabled(true);
        waveChart!!.setPinchZoom(true);
        waveChart!!.isDoubleTapToZoomEnabled = false;
       /// waveChart!!.setBackgroundColor(Color.parseColor("#FF8000"));
        waveChart!!.axisRight.isEnabled = false;
        waveChart!!.description.isEnabled = false;

        getLineEntries()

        val yAxis: YAxis = waveChart!!.axisLeft
        yAxis.setLabelCount(4, true)
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        yAxis.valueFormatter = DefaultAxisValueFormatter(2)
        yAxis.textColor = Color.BLACK
        yAxis.textSize = 12f //not in your original but added

        yAxis.gridColor = Color.argb(102, 255, 255, 255)
        yAxis.axisLineColor = Color.TRANSPARENT
        yAxis.axisMinimum = 0f //not in your original but added


        val xAxis: XAxis = waveChart!!.xAxis
        xAxis.setDrawLimitLinesBehindData(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE //changed to match your spec

        xAxis.textSize = 12f //not in your original but added
        xAxis.textColor = Color.BLACK
        xAxis.disableGridDashedLine()
        xAxis.setDrawGridLines(false)
        xAxis.gridColor = Color.argb(204, 255, 255, 255)

        xAxis.axisLineColor = Color.TRANSPARENT
        xAxis.valueFormatter = DefaultAxisValueFormatter(2) //not in your original but added

        //xAxis.setValueFormatter(new TimestampValueFormatter());
        //xAxis.setValueFormatter(new TimestampValueFormatter());
        xAxis.labelCount = 4
        xAxis.setAvoidFirstLastClipping(true)
        //xAxis.setSpaceMin(10f); //DON'T NEED THIS!!

        //xAxis.setSpaceMin(10f); //DON'T NEED THIS!!


//        waveChart!!.setViewPortOffsets(0f, 0f, 0f, 0f)
//        val xMax: Float = waveChart!!.data.getDataSetByIndex(0).xMax
//        val xMin = 0f
//        xAxis.axisMaximum = xMax
//        xAxis.axisMinimum = xMin




        lineDataSet = LineDataSet(lineChartEntries, "Wave Chart")
        lineDataSet!!.setDrawCircleHole(true);
        lineDataSet!!.setCircleColor(Color.GREEN)
        lineDataSet!!.lineWidth = 3f
    //    lineDataSet!!.setDrawCircles(false)
        lineDataSet!!.mode = LineDataSet.Mode.CUBIC_BEZIER

        lineDataSet!!.fillColor = ContextCompat.getColor(requireActivity(), R.color.green_100);
        lineDataSet!!.color = ContextCompat.getColor(requireActivity(),R.color.green_light600);

        lineDataSet!!.valueTextSize = 9f
        lineDataSet!!.valueTextColor = Color.BLUE
        lineDataSet!!.setDrawValues(false)
        lineDataSet!!.setDrawFilled(true)
        lineDataSet!!.formLineWidth = 1f
        lineDataSet!!.formSize = 15f

        lineData = LineData(lineDataSet)
//        lineData!!.setValueTextSize(10f);
//        lineData!!.setValueTextColor(Color.BLUE)

        waveChart!!.data = lineData;

        getPointEntries()
        val dataSets: ArrayList<ILineDataSet> = ArrayList()

        val highLineDataSet = LineDataSet(
            firstEntries,
            "first"
        )
        highLineDataSet.setDrawCircles(true)
        highLineDataSet.circleRadius = 4f
        highLineDataSet.setDrawValues(false)
        highLineDataSet.lineWidth = 3f
        highLineDataSet.color = Color.GREEN
        highLineDataSet.setCircleColor(Color.GREEN)
        dataSets.add(highLineDataSet)


        val lowLineDataSet = LineDataSet(
            secondEntries,
            "Second"
        )
        lowLineDataSet.setDrawCircles(true)
        lowLineDataSet.circleRadius = 4f
        lowLineDataSet.setDrawValues(false)
        lowLineDataSet.lineWidth = 3f
        lowLineDataSet.color = Color.RED
        lowLineDataSet.setCircleColor(Color.RED)
        dataSets.add(lowLineDataSet)


        val closeLineDataSet = LineDataSet(
            thirdEntries,
            "third"
        )
        closeLineDataSet.setDrawCircles(true)
        closeLineDataSet.circleRadius = 4f
        closeLineDataSet.setDrawValues(false)
        closeLineDataSet.lineWidth = 3f
        closeLineDataSet.color = Color.rgb(255, 165, 0)
        closeLineDataSet.setCircleColor(Color.rgb(255, 165, 0))
        dataSets.add(closeLineDataSet)
        val pxAxis: XAxis = pointChart!!.xAxis
        pxAxis.setDrawGridLines(false)


        pointChart!!.axisRight.isEnabled = false;
        pointChart!!.description.isEnabled = false;
        val lineData = LineData(dataSets)
        pointChart!!.data = lineData
        pointChart!!.invalidate()


        val funnel: Funnel = AnyChart.funnel()

        val data: MutableList<DataEntry> = ArrayList()
        data.add(ValueDataEntry("Website Visits", 528756))
        data.add(ValueDataEntry("Downloads", 164052))
        data.add(ValueDataEntry("Valid Contacts", 112167))
        data.add(ValueDataEntry("Interested to Buy", 79128))
        data.add(ValueDataEntry("Purchased", 79128))

        funnel.data(data)

        funnel.margin(arrayOf("10", "20%", "10", "20%"))
        funnel.baseWidth("70%")
            .neckWidth("17%")

        funnel.labels()
            .position("outsideleft")
            .format("{%X} - {%Value}")

        funnel.animation(true)
//        var paginator = funnel.legend().paginator();
//        paginator.o

        funnelChart!!.setChart(funnel)


    }

    fun getPieEntries() {
        pieEntriesArrayList.add(PieEntry(24f, "Test 1"))
        pieEntriesArrayList.add(PieEntry(15f, "Test 2"))
        pieEntriesArrayList.add(PieEntry(19f, "Test 3"))
        pieEntriesArrayList.add(PieEntry(22f, "Test 4"))
        pieEntriesArrayList.add(PieEntry(20f, "Test 5"))
        pieEntriesArrayList.add(PieEntry(17f, "Test 6"))
    }


    private fun getBarEntries() {
        // creating a new array list
        barEntriesArrayList = ArrayList()

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntriesArrayList.add(BarEntry(1f, 4f))
        barEntriesArrayList.add(BarEntry(2f, 6f))
        barEntriesArrayList.add(BarEntry(3f, 8f))
        barEntriesArrayList.add(BarEntry(4f, 2f))
        barEntriesArrayList.add(BarEntry(5f, 4f))
        barEntriesArrayList.add(BarEntry(6f, 1f))
        barEntriesArrayList.add(BarEntry(7f, 5f))
        barEntriesArrayList.add(BarEntry(8f, 9f))
        barEntriesArrayList.add(BarEntry(9f, 3f))
    }


    fun getLineEntries() {
        lineChartEntries.add(Entry(1f, 2f))
        lineChartEntries.add(Entry(2f, 8f))
        lineChartEntries.add(Entry(3f, 3f))
        lineChartEntries.add(Entry(4f, 7f))
        lineChartEntries.add(Entry(5f, 5f))
        lineChartEntries.add(Entry(6f, 1f))

        lineChartEntries.add(Entry(7f, 4f))
        lineChartEntries.add(Entry(8f, 8f))
        lineChartEntries.add(Entry(9f, 3f))
        lineChartEntries.add(Entry(10f, 9f))
        lineChartEntries.add(Entry(11f, 2f))
        lineChartEntries.add(Entry(12f, 5f))

        lineChartEntries.add(Entry(13f, 7f))
        lineChartEntries.add(Entry(14f, 1f))
        lineChartEntries.add(Entry(15f, 4f))
        lineChartEntries.add(Entry(16f, 6f))
        lineChartEntries.add(Entry(17f, 9f))
        lineChartEntries.add(Entry(18f, 3f))

    }

    fun getPointEntries() {

        firstEntries.add(Entry(1f, 2f))
        firstEntries.add(Entry(2f, 8f))
        firstEntries.add(Entry(3f, 3f))
        firstEntries.add(Entry(4f, 7f))
        firstEntries.add(Entry(5f, 5f))
        firstEntries.add(Entry(6f, 1f))


        secondEntries.add(Entry(1f, 4f))
        secondEntries.add(Entry(2f, 8f))
        secondEntries.add(Entry(3f,3f))
        secondEntries.add(Entry(4f, 9f))
        secondEntries.add(Entry(5f, 2f))
        secondEntries.add(Entry(6f, 5f))


        thirdEntries.add(Entry(1f, 7f))
        thirdEntries.add(Entry(2f, 1f))
        thirdEntries.add(Entry(3f, 4f))
        thirdEntries.add(Entry(4f, 6f))
        thirdEntries.add(Entry(5f, 9f))
        thirdEntries.add(Entry(6f, 3f))
    }


}