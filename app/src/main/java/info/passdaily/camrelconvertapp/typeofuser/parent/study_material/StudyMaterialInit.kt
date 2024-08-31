package info.passdaily.camrelconvertapp.typeofuser.parent.study_material

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.tabs.TabLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.StudyMaterialFragmentBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Global.Companion.subjectId
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper


@Suppress("DEPRECATION")
class StudyMaterialInit : Fragment(),ItemClickListener {

    var TAG = "StudyMaterialInit"
    //subjetc_url= Global.url+"OnlineExam/GetSubjectByClass?ClassId="+student2.class_id+"&StudentId="+student2.stu_id;
    private var _binding: StudyMaterialFragmentBinding? = null
    private val binding get() = _binding!!

    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    var recyclerViewMaterialList: RecyclerView? = null
    var recyclerView: RecyclerView? = null
    private lateinit var viewModel: StudyInitViewModel
    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0

    var constraintEmpty: ConstraintLayout? =null
    var imageViewEmpty: ImageView? =null
    var textEmpty : TextView? =null


    var mContext : Context? =null

    var shimmerViewContainer : ShimmerFrameLayout? =null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext;
        }
        Log.i(TAG,"onAttach ")

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.currentPage = 4
        Global.screenState = "landingpage"
      // viewModel = ViewModelProvider(this).get(StudyInitViewModel::class.java)

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[StudyInitViewModel::class.java]
        _binding = StudyMaterialFragmentBinding.inflate(inflater, container, false)
        return binding.root
      //  return inflater.inflate(R.layout.study_material_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var studentDBHelper = StudentDBHelper(requireActivity())
        var student = studentDBHelper.getStudentById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID
        ///load subjects
        recyclerView = binding.tabLayout
        recyclerView?.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
     //   viewModel.getSubjects(student.CLASS_ID, student.STUDENT_ID)
//        tabLayout?.tabRippleColor = null;

        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        if(isAdded) {
            Glide.with(mContext!!)
                .load(R.drawable.ic_empty_state_assignment)
                .into(imageViewEmpty!!)
        }
        textEmpty?.text = "No Material"

        Log.i(TAG, "STUDENT_ID " + student.STUDENT_ID)
        Log.i(TAG, "ACCADEMIC_ID " + student.ACCADEMIC_ID)
        Log.i(TAG, "CLASS_ID " + student.CLASS_ID)
        Log.i(TAG, "subjectID $subjectId")

//AccademicId=7&ClassId=2&StudentId=1

/////////////load study Material list
        recyclerViewMaterialList = binding.recyclerView
        recyclerViewMaterialList?.layoutManager = LinearLayoutManager(requireActivity())


        shimmerViewContainer = binding.shimmerViewContainer

        intiFunction()
        onClick(-1)
    }

    private fun intiFunction() {
        viewModel.getSubjects(CLASSID, STUDENTID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if (isAdded) {
                                recyclerView?.adapter = SubjectAdapter(
                                    this,
                                    response.subjects as ArrayList<SubjectsModel.Subject>,
                                    mContext!!)
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

    fun getStudyMaterialFun(subjectId : Int) {

        viewModel.getStudyMaterial( ACADEMICID,
            CLASSID,
            STUDENTID,
            subjectId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            if(response.studyMaterials.isNotEmpty()){
                                constraintEmpty?.visibility = View.GONE
                                recyclerViewMaterialList?.visibility = View.VISIBLE
                                shimmerViewContainer?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewMaterialList?.adapter = StudyMaterialAdapter(
                                         response.studyMaterials,mContext!!)
                                }
                            }else{
                                constraintEmpty?.visibility = View.VISIBLE
                                recyclerViewMaterialList?.visibility = View.GONE
                                shimmerViewContainer?.visibility = View.GONE
                                if (isAdded) {
                                    Glide.with(mContext!!)
                                        .load(R.drawable.ic_empty_state_absent)
                                        .into(imageViewEmpty!!)
                                }
                                textEmpty?.text = "No Result Founded"

                            }
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewMaterialList?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_no_internet)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text = "Oops no internet"

                            Log.i(TAG, "Status.ERROR ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            shimmerViewContainer?.visibility = View.VISIBLE
                            recyclerViewMaterialList?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE

                        }
                    }
                }
            })
    }

    class SubjectAdapter(
        val itemClickListener: ItemClickListener,
        var subjects: ArrayList<SubjectsModel.Subject>,
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

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textSubject.text = subjects[position].sUBJECTNAME
            holder.cardView.setOnClickListener {
                itemClickListener.onClick(subjects[position].sUBJECTID)
                index = position;
                notifyDataSetChanged()
            }

            if (index == position) {
                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.green_400))
                holder.textSubject.setTextColor(Color.parseColor("#FFFFFFFF"))
            } else {
                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.white))
                holder.textSubject.setTextColor(context.resources.getColor(R.color.green_400))
            }
        }

        override fun getItemCount(): Int {
            return subjects.size
        }

    }



    /////////////////////get StudyMaterial
    class StudyMaterialAdapter(var studyMaterials: List<StudyMaterialModel.StudyMaterial>,
                               var context: Context) :
        RecyclerView.Adapter<StudyMaterialAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var cardView9: CardView = view.findViewById(R.id.cardViewStatus)
            var textClassName: TextView = view.findViewById(R.id.textClassName)
            var textSubjectName: TextView = view.findViewById(R.id.textSubjectName)
            //textViewDate
            var textTitle: TextView = view.findViewById(R.id.textAssignmentName)
            var textDescription: TextView = view.findViewById(R.id.textDescription)
            var buttonJoinLive: AppCompatButton = view.findViewById(R.id.buttonJoinLive)
            //imageViewSubject
            var imageSubject: ImageView = view.findViewById(R.id.imageViewSubject)
            var textViewTotal: TextView = view.findViewById(R.id.textViewTotal)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.studylist_adapter, parent, false)
            return ViewHolder(itemView)
        }
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textClassName.text ="Class : "+ studyMaterials[position].cLASSNAME
            holder.textSubjectName.text =studyMaterials[position].sUBJECTNAME
            holder.textTitle.text = studyMaterials[position].sTUDYMETERIALTITLE
            holder.textViewTotal.text ="Total Files : "+ studyMaterials[position].tOTALFILE.toString()
            holder.textDescription.text =studyMaterials[position].sTUDYMETERIALDESCRIPTION

            when (studyMaterials[position].sUBJECTICON) {
                "English.png" -> {
                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_english))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_english))
                    Glide.with(context)
                        .load(R.drawable.ic_study_english)
                        .into(holder.imageSubject)
                }
                "Chemistry.png" -> {
                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_chemistry))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_chemistry))
                    Glide.with(context)
                        .load(R.drawable.ic_study_chemistry)
                        .into(holder.imageSubject)
                }
                "Biology.png" -> {
                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_bio))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_bio))
                    Glide.with(context)
                        .load(R.drawable.ic_study_biology)
                        .into(holder.imageSubject)
                }
                "Maths.png" -> {
                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_maths))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_maths))
                    Glide.with(context)
                        .load(R.drawable.ic_study_maths)
                        .into(holder.imageSubject)
                }
                "Hindi.png" -> {
                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_hindi))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_hindi))
                    Glide.with(context)
                        .load(R.drawable.ic_study_hindi)
                        .into(holder.imageSubject)
                }
                "Physics.png" -> {
                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_physics))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_physics))
                    Glide.with(context)
                        .load(R.drawable.ic_study_physics)
                        .into(holder.imageSubject)
                }
                "Malayalam.png" -> {
                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_malayalam))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_malayalam))
                    Glide.with(context)
                        .load(R.drawable.ic_study_malayalam)
                        .into(holder.imageSubject)
                }
                "Arabic.png" -> {
                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_arabic))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_arabic))
                    Glide.with(context)
                        .load(R.drawable.ic_study_arabic)
                        .into(holder.imageSubject)
                }
                "Accountancy.png" -> {
                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_accounts))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_accounts))
                    Glide.with(context)
                        .load(R.drawable.ic_study_accountancy)
                        .into(holder.imageSubject)
                }
                "Social.png" -> {
                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_social))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_social))
                    Glide.with(context)
                        .load(R.drawable.ic_study_social)
                        .into(holder.imageSubject)
                }
                "Economics.png" -> {
                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_econonics))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_econonics))
                    Glide.with(context)
                        .load(R.drawable.ic_study_economics)
                        .into(holder.imageSubject)
                }
                "BasicScience.png" -> {
                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_bio))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_bio))
                    Glide.with(context)
                        .load(R.drawable.ic_study_biology)
                        .into(holder.imageSubject)
                }
                "Computer.png" -> {
                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_computer))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_computer))
                    Glide.with(context)
                        .load(R.drawable.ic_study_computer)
                        .into(holder.imageSubject)
                }
                "General.png" -> {
                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_computer))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_computer))
                    Glide.with(context)
                        .load(R.drawable.ic_study_computer)
                        .into(holder.imageSubject)
                }
            }

            holder.buttonJoinLive.setOnClickListener {
                val intent  = Intent(context, MaterialViewListActivity::class.java)
                intent.putExtra("SUBJECT_NAME", studyMaterials[position].sUBJECTNAME)
                intent.putExtra("TITLE", studyMaterials[position].sTUDYMETERIALTITLE)
                intent.putExtra("DESCRIPTION", studyMaterials[position].sTUDYMETERIALDESCRIPTION)
                intent.putExtra("STUDY_MATERIAL_ID", studyMaterials[position].sTUDYMETERIALID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent)

                //StudyMaterial/FileList?StudyMeterialId=
            }
        }

        override fun getItemCount(): Int {
            return studyMaterials.size
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
    }

    override fun onClick(id: Int) {
      Log.i(TAG, "onClick callback $id")
        viewModel.getStudyMaterial( ACADEMICID,
            CLASSID,
            STUDENTID,
            id)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            if(response.studyMaterials.isNotEmpty()){
                                constraintEmpty?.visibility = View.GONE
                                recyclerViewMaterialList?.visibility = View.VISIBLE
                                shimmerViewContainer?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewMaterialList?.adapter = StudyMaterialAdapter(
                                        response.studyMaterials,mContext!!)
                                }
                            }else{
                                constraintEmpty?.visibility = View.VISIBLE
                                recyclerViewMaterialList?.visibility = View.GONE
                                shimmerViewContainer?.visibility = View.GONE
                                if (isAdded) {
                                    Glide.with(mContext!!)
                                        .load(R.drawable.ic_empty_state_absent)
                                        .into(imageViewEmpty!!)
                                }
                                textEmpty?.text = requireActivity().resources.getString(R.string.no_results)

                            }
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewMaterialList?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_no_internet)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text = requireActivity().resources.getString(R.string.no_internet)

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
                            recyclerViewMaterialList?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE

                        }
                    }
                }
            })
    }


    override fun onDetach() {
        super.onDetach()
        mContext = null
        Log.i(TAG,"onDetach ")
    }

    override fun onDestroy() {
        super.onDestroy()
        mContext = null
        _binding = null
        Log.i(TAG,"onDestroy ")
    }
}
interface ItemClickListener{
    fun onClick(id: Int)
}