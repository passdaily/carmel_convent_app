package info.passdaily.camrelconvertapp.typeofuser.parent.objective_exam

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
import info.passdaily.camrelconvertapp.databinding.ActivityOexamResultBinding
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
class OExamResultActivity : AppCompatActivity() {
    var TAG = "OExamResultActivity"

    private lateinit var binding: ActivityOexamResultBinding
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

    var OEXAM_ID = 0
    var OEXAM_ATTEMPT_ID = 0

    var textExamName : TextView? = null
    var textSubjectName : TextView? = null
    var textStudentName : TextView? = null
    var textClassName : TextView? = null
    var textDuration : TextView? = null
    var textTimeTaken : TextView? = null
    var textLevel : TextView? = null


    var textIncorrect : TextView? = null
    var textUnanswered : TextView? = null
    var textCorrect : TextView? = null
    var textResult : TextView? = null

    var textResultPublished  : TextView? = null

    var buttonPreview : AppCompatButton? =null

    var recyclerViewTopper : RecyclerView? =null

    private lateinit var oExamAreaViewModel: OExamAreaViewModel

    ///
    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val extras = intent.extras
        if (extras != null) {
            OEXAM_ID = extras.getInt("OEXAM_ID")
            OEXAM_ATTEMPT_ID = extras.getInt("OEXAM_ATTEMPT_ID")
            ATTEMPTED_ON = extras.getString("ATTEMPTED_ON")
        }

        oExamAreaViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[OExamAreaViewModel::class.java]

        var studentDBHelper = StudentDBHelper(this)
        var student = studentDBHelper.getStudentById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID

        binding = ActivityOexamResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "View Details"
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

        textCorrect = binding.textCorrect
        textUnanswered = binding.textTotQuestion
        textIncorrect = binding.textUnanswered

        textResultPublished = binding.textResultPublished

        textResult = binding.textResult
        buttonPreview = binding.buttonPreview

        recyclerViewTopper = binding.recyclerViewTopper
        recyclerViewTopper?.layoutManager = LinearLayoutManager(this)

        getObjectiveExamResult()
        Utils.setStatusBarColor(this)

    }


    private fun getObjectiveExamResult() {
        oExamAreaViewModel.getObjectiveExamResult(
            ACADEMICID,
            STUDENTID,
            OEXAM_ID,
            OEXAM_ATTEMPT_ID
        )
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            textExamName?.text =  response.onlineExamResult.oEXAMNAME
                            textClassName?.text =  response.onlineExamResult.cLASSNAME
                            textStudentName?.text =  response.onlineExamResult.sTUDENTNAME
                            textSubjectName?.text =  response.onlineExamResult.sUBJECTNAME

                            textDuration?.text =  response.onlineExamResult.oEXAMDURATION.toString() +" Minutes"
                            textTimeTaken?.text =  Utils.timeConversion(response.onlineExamResult.eLAPSEDTIME)
                            textLevel?.text =  "Medium"

                            val unAnswer: Int = response.onlineExamResult.tOTALQUESTIONS - response.onlineExamResult.aNSWEREDQUESTIONS
                            val inCorrect: Int = response.onlineExamResult.aNSWEREDQUESTIONS - response.onlineExamResult.cORRECTANSWERCOUNT

                            textCorrect?.text =  response.onlineExamResult.cORRECTANSWERCOUNT.toString()
                            textUnanswered?.text =  unAnswer.toString()
                            textIncorrect?.text =  inCorrect.toString()


                            textResult?.text = "Result : "+response.onlineExamResult.cORRECTANSWERCOUNT.toString() + " / " + response.onlineExamResult.tOTALQUESTIONS.toString()


                            // Log.i(TAG,"TopperLi "+TopperLi);
                            if (response.topperList.isNotEmpty()) {

                                textResultPublished?.text = "Result Published"
                                recyclerViewTopper?.visibility = View.VISIBLE
                                val mAdapter = TopperListAdapter(
                                    response.topperList, this@OExamResultActivity)
                                recyclerViewTopper?.adapter = mAdapter

//                                ASSIGNMENT_FILE_ID: 8,
//                                        ASSIGNMENT_SUBMIT_ID: 3,
//                                        ASSIGNMENT_FILE: "059589BACE69C20756638998070326D8.jpg"
                            } else {
                                textResultPublished?.text = "Result Not Published"
                                recyclerViewTopper?.visibility = View.GONE
                            }


                            buttonPreview?.setOnClickListener{
                                val intent = Intent(this@OExamResultActivity, OExamPreViewActivity::class.java)
                                intent.putExtra("CLASS_NAME", response.onlineExamResult.cLASSNAME)
                                intent.putExtra("STUDENT_NAME", response.onlineExamResult.sTUDENTNAME)
                                intent.putExtra("OEXAM_ID", response.onlineExamResult.oEXAMID)
                                intent.putExtra("OEXAM_ATTEMPT_ID", response.onlineExamResult.oEXAMATTEMPTID)
                                startActivity(intent)
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