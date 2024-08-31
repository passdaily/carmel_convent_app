package info.passdaily.camrelconvertapp.typeofuser.parent.description_exam

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityDexamResultBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class DExamResultActivity : AppCompatActivity() {
    var TAG = "OExamResultActivity"

    private lateinit var binding: ActivityDexamResultBinding
    var toolbar: Toolbar? = null

    var EXAM_ATTEMPT_ID: Int = 0
    var IS_AUTO_ENDED: Int = 0
    var IS_SUBMITTED: Int = 0
    var STUDENT_NAME:String? = null
    var EXAM_DURATION:Long = 0
    var CLASS_NAME:String? = null
    var START_TIME: String? = null
    var END_TIME:String? = null
    var TIME_NOW:String? = null
    var PAUSED_COUNT:Int = 0
    var STATUS:String? = null
    var TOTAL_OUTOFF_MARK:String? = null
    var TOTAL_ANSWERED_QUESTIONS:String? = null
    var ATTEMPTED_ON: String? = null
    var ALLOWED_PAUSE:Int = 0
    var EXAM_ATTEMPT_STATUS:String? = null
    var EXAM_NAME: String? = null
    var EXAM_DESCRIPTION:String? = null
    var SUBJECT_NAME = ""
    var EXAM_ID:Int = 0


    var textExamName : TextView? = null
    var textSubjectName : TextView? = null
    var textStudentName : TextView? = null
    var textClassName : TextView? = null
    var textDuration : TextView? = null
    var textTimeTaken : TextView? = null
    var textLevel : TextView? = null


    var textTotQuestion : TextView? = null
    var textUnanswered : TextView? = null
    var textAnswered : TextView? = null
    var textResult : TextView? = null

    var textResultPublished  : TextView? = null

    var buttonPreview : AppCompatButton? =null

    var recyclerViewTopper : RecyclerView? =null

    private lateinit var dExamAreaViewModel: DExamAreaViewModel


    ///
    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val extras = intent.extras
        if (extras != null) {
            EXAM_ID = extras.getInt("EXAM_ID")
            EXAM_ATTEMPT_ID = extras.getInt("EXAM_ATTEMPT_ID")
            ATTEMPTED_ON = extras.getString("ATTEMPTED_ON")
        }

        dExamAreaViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[DExamAreaViewModel::class.java]

        var studentDBHelper = StudentDBHelper(this)
        var student = studentDBHelper.getStudentById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID

        binding = ActivityDexamResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "Result"
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            //   toolbar.setTitle("About-Us");
            toolbar?.setNavigationOnClickListener { // perform whatever you want on back arrow click
                onBackPressed()
            }

        }
        textExamName = binding.textExamName
        textSubjectName = binding.textSubjectName
        textStudentName = binding.textStudentName
        textClassName = binding.textClassName

        textDuration = binding.textDuration
        textTimeTaken = binding.textTimeTaken
        textLevel = binding.textLevel

        textAnswered = binding.textAnswered
        textTotQuestion = binding.textTotQuestion
        textUnanswered = binding.textUnanswered

        textResultPublished = binding.textResultPublished

        textResult = binding.textResult
        buttonPreview = binding.buttonPreview

        recyclerViewTopper = binding.recyclerViewTopper
        recyclerViewTopper?.layoutManager = LinearLayoutManager(this)

        getDescriptiveExamResult()

   //     Utils.setStatusBarColor(this)
        Utils.setStatusBarColor(this)
    }


    private fun getDescriptiveExamResult() {
        dExamAreaViewModel.getDescriptiveExamResult(
            ACADEMICID,
            STUDENTID,
            EXAM_ID,
            EXAM_ATTEMPT_ID
        )
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            Global.dOnlineExamResult = response.dOnlineExamResult

                            val onlineExamDetails = response.onlineExamDetails

                            if (onlineExamDetails == null) {
                                textExamName?.setBackgroundColor(resources.getColor(R.color.gray_150))
                                textClassName?.setBackgroundColor(resources.getColor(R.color.gray_150))
                                textStudentName?.setBackgroundColor(resources.getColor(R.color.gray_150))
                                textSubjectName?.setBackgroundColor(resources.getColor(R.color.gray_150))
                                textDuration?.setBackgroundColor(resources.getColor(R.color.gray_150))
                                textTimeTaken?.setBackgroundColor(resources.getColor(R.color.gray_150))
                                textLevel?.setBackgroundColor(resources.getColor(R.color.gray_150))
                                textResultPublished?.setBackgroundColor(resources.getColor(R.color.gray_150))
                                textResult?.setBackgroundColor(resources.getColor(R.color.gray_150))
                                recyclerViewTopper?.setBackgroundColor(resources.getColor(R.color.gray_150))
                                buttonPreview?.text = "No Result Found"
                                buttonPreview?.isEnabled = false
                            } else {
                                val eXAMNAME = response.onlineExamDetails.eXAMNAME
                                textExamName?.text = response.onlineExamDetails.eXAMNAME
                                textClassName?.text = response.onlineExamDetails.cLASSNAME
                                textStudentName?.text = response.onlineExamDetails.sTUDENTNAME
                                textSubjectName?.text = response.onlineExamDetails.sUBJECTNAME

                                textDuration?.text =
                                    response.onlineExamDetails.eXAMDURATION.toString() + " Minutes"
                                textTimeTaken?.text =
                                    Utils.timeConversion(response.onlineExamDetails.eLAPSEDTIME)
                                textLevel?.text = "Medium"

                                val eXAMATTEMPTSTATUS = response.onlineExamDetails.eXAMATTEMPTSTATUS
                                val tOTALMARKSCORED = response.onlineExamDetails.tOTALMARKSCORED

                                val tOTALQUESTIONS = response.onlineExamDetails.tOTALQUESTIONS
                                val aNSWEREDQUESTIONS = response.onlineExamDetails.aNSWEREDQUESTIONS
                                val unAnswer = tOTALQUESTIONS - aNSWEREDQUESTIONS

                                textAnswered?.text = aNSWEREDQUESTIONS.toString()
                                textUnanswered?.text = unAnswer.toString()
                                textTotQuestion?.text = tOTALQUESTIONS.toString()

                                Log.i(TAG, " tOTALQUESTIONS $tOTALQUESTIONS")
                                Log.i(TAG, " aNSWEREDQUESTIONS $aNSWEREDQUESTIONS")
                                Log.i(TAG, " unAnswer $unAnswer")


                                if (eXAMATTEMPTSTATUS != 1) {
                                    textResultPublished?.text = "Exam Marks Published"
                                    textResult?.text =
                                        "Result : ${tOTALMARKSCORED.toString()} / ${tOTALQUESTIONS.toString()}"
                                } else {
                                    textResultPublished?.text = "Exam Marks Not Published"
                                    textResult?.text = "Result : (N/A) / ${tOTALQUESTIONS.toString()}"
                                }

                                // SUBJECT_NAME = extras.getString("SUBJECT_NAME")!!
                                //            EXAM_ID = extras.getInt("EXAM_ID")
                                //            EXAM_DURATION = extras.getLong("EXAM_DURATION")
                                //            EXAM_NAME = extras.getString("EXAM_NAME")!!
                                //            ALLOWED_PAUSE = extras.getInt("ALLOWED_PAUSE")
                                //            PAUSED_COUNT = extras.getInt("PAUSED_COUNT")
                                //            TIME_NOW = extras.getString("TIME_NOW")!!

                                buttonPreview?.setOnClickListener {
                                    val intent = Intent(this@DExamResultActivity, DExamPreviewActivity::class.java)
                                    intent.putExtra("eXAMNAME", eXAMNAME)
                                    intent.putExtra("STUDENT_NAME", response.onlineExamDetails.sTUDENTNAME)
                                    intent.putExtra("EXAM_ID", response.onlineExamDetails.eXAMID)
                                    intent.putExtra("EXAM_ATTEMPT_ID", response.onlineExamDetails.eXAMATTEMPTID)
                                    startActivity(intent)
                                }
                            }
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "LOADING")
                        }
                    }
                }
            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Global.dOnlineExamResult = ArrayList<OnlineDExamResultModel.DOnlineExamResult>()
    }

    class TopperListAdapter(var topperList: List<ObjectiveExamResultModel.Topper>, context: Context)
        : RecyclerView.Adapter<TopperListAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewName: TextView = view.findViewById(R.id.textViewName)
            var textViewClass: TextView = view.findViewById(R.id.textViewClass)
            var textViewMark: TextView = view.findViewById(R.id.textViewMark)

        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.topper_list_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
           holder.textViewName.text = topperList[position].sTUDENTNAME
            holder.textViewClass.text = topperList[position].cLASSNAME
            holder.textViewMark.text = "Marks : "+topperList[position].cORRECTANSWERCOUNT +" / "+topperList[position].tOTALQUESTIONS
        }

        override fun getItemCount(): Int {
            return topperList.size
        }

    }
}