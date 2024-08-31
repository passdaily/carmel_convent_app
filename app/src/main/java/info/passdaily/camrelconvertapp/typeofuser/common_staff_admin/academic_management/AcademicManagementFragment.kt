package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.academic_management

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.compose.ui.text.toLowerCase
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.imageview.ShapeableImageView
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.academic_management.AcademicManagementViewModel
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentAcademicManagementBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import kotlinx.coroutines.DelicateCoroutinesApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@DelicateCoroutinesApi
@Suppress("DEPRECATION")
class AcademicManagementFragment : Fragment(),AcademicClickListener {

    var TAG = "AcademicManagementFragment"

    private var _binding: FragmentAcademicManagementBinding? = null
    private val binding get() = _binding!!

    lateinit var academicManagementViewModel : AcademicManagementViewModel

    lateinit var bottomSheetUpdateSubject  : BottomSheetUpdateSubjet
    lateinit var bottomSheetUpdateSubjectCat  : BottomSheetUpdateSubjectCategory
    lateinit var bottomSheetUpdateClass : BottomSheetUpdateClass
    lateinit var bottomSheetUpdateYear : BottomSheetUpdateYear

    private lateinit var localDBHelper: LocalDBHelper
    var adminId = 0
    var schoolId=0

    var constraintLayoutContent : ConstraintLayout? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var toolBarClickListener: ToolBarClickListener? = null
//    var tabLayout: TabLayout? = null
//    var viewPager: ViewPager? = null
    var recyclerViewList : RecyclerView? = null

    var classMonth = arrayOf(
        "Subject",
        "Sub Category",
        "Class List",
        "Academic Year",
    )
    var isWorking = false

    var listIndex = 0

    var constraintEmpty : ConstraintLayout? = null
    var imageViewEmpty : ImageView? = null
    var textEmpty  : TextView? = null

    var getSubjectList = ArrayList<SubjectListModel.Subject>()
    var getSubjectCategory = ArrayList<SubCategoryListModel.Subjectcategory>()
    var getClassList = ArrayList<ClassListModel.Class>()
    var getAcaddemic = ArrayList<AcademicListModel.AccademicDetail>()

    var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext
        }
        try {
            toolBarClickListener = context as ToolBarClickListener
        } catch (e: Exception) {
            Log.i(TAG, "Exception $e")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.screenState = "staffhomepage"
        toolBarClickListener?.setToolbarName("Academic Management")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        academicManagementViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[AcademicManagementViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentAcademicManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty

        constraintLayoutContent = binding.constraintLayoutContent
        shimmerViewContainer= binding.shimmerViewContainer

        isWorking = true

        recyclerViewList = binding.recyclerViewList
        recyclerViewList?.layoutManager = LinearLayoutManager(requireActivity())

     //   viewPager = binding.pager
        var tabLayout = binding.tabLayout
        tabLayout.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)

        tabLayout.adapter = ListAdapter(
            this,
            classMonth, requireActivity()
        )


        onClick(listIndex)


        var fab = binding.fab
        fab.setOnClickListener {
            when(listIndex){
                0 -> {
                    val dialog1 = CreateSubjectDialog(this)
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                    dialog1.show(transaction, CreateSubjectDialog.TAG)
                }
                1 -> {
                    val dialog1 = CreateSubjectCategoryDialog(this)
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                    dialog1.show(transaction, CreateSubjectCategoryDialog.TAG)
                }
                2 -> {
                    val dialog1 = CreateClassDialog(this)
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                    dialog1.show(transaction, CreateClassDialog.TAG)
                }
                3 -> {
                    val dialog1 = CreateAcademicYearDialog(this)
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                    dialog1.show(transaction, CreateAcademicYearDialog.TAG)
                }
            }
        }


        bottomSheetUpdateSubject = BottomSheetUpdateSubjet()
        bottomSheetUpdateSubjectCat = BottomSheetUpdateSubjectCategory()
        bottomSheetUpdateClass = BottomSheetUpdateClass()
        bottomSheetUpdateYear = BottomSheetUpdateYear()
    }


    fun subjectFunction(){


        val jsonObject = JSONObject()
        try {
            jsonObject.put("SUBJECT_NAME", "")

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")

        val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())


        academicManagementViewModel.getSubjectDetailList(submitItems)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
//                            if(isWorking) {
                            recyclerViewList?.visibility = View.VISIBLE
                                shimmerViewContainer?.visibility = View.GONE
 //                           }
                            val response = resource.data?.body()!!
                            getSubjectList= response.subjectList as ArrayList<SubjectListModel.Subject>
                            if(getSubjectList.isNotEmpty()){
                                recyclerViewList?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE

                                if(isAdded){
                                    recyclerViewList?.adapter =  SubjectAdapter(
                                        this,
                                        getSubjectList,
                                        requireActivity(),
                                        TAG
                                    )
                                }
                            }else{
                                recyclerViewList?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text =  resources.getString(R.string.no_results)
                            }
                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            recyclerViewList?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getSubjectList = ArrayList<SubjectListModel.Subject>()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })

    }

    fun subCategoryFun(){
        ////Subject Category List
        academicManagementViewModel.getSubjectCategoryList(0,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            recyclerViewList?.visibility = View.VISIBLE
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getSubjectCategory= response.subjectCategory as ArrayList<SubCategoryListModel.Subjectcategory>
                            if(getSubjectCategory.isNotEmpty()){
                                recyclerViewList?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE

                                if(isAdded){
                                    recyclerViewList?.adapter =  SubCategoryAdapter(
                                        this,
                                        getSubjectCategory,
                                        requireActivity(),
                                        TAG
                                    )
                                }
                            }else{
                                recyclerViewList?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text =  resources.getString(R.string.no_results)
                            }
                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            recyclerViewList?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getSubjectCategory = ArrayList<SubCategoryListModel.Subjectcategory>()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })

    }

    fun classList(){
        ////////////Class List
        academicManagementViewModel.getClassList(0,0)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            recyclerViewList?.visibility = View.VISIBLE
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getClassList= response.classList as ArrayList<ClassListModel.Class>

                            if(getClassList.isNotEmpty()){
                                recyclerViewList?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE

                                if(isAdded){
                                    recyclerViewList?.adapter =  ClassAdapter(
                                        this,
                                        getClassList,
                                        requireActivity(),
                                        TAG
                                    )
                                }
                            }else{
                                recyclerViewList?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text =  resources.getString(R.string.no_results)
                            }
                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            recyclerViewList?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getClassList = ArrayList<ClassListModel.Class>()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })

    }

    fun yearFunction() {
        ////academic year
        academicManagementViewModel.getAcademicList(0,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            recyclerViewList?.visibility = View.VISIBLE
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getAcaddemic = response.accademicDetails as ArrayList<AcademicListModel.AccademicDetail>

                            if(getAcaddemic.isNotEmpty()){
                                recyclerViewList?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE

                                if(isAdded){
                                    recyclerViewList?.adapter =  AcademicYearListAdapter(
                                        this,
                                        getAcaddemic,
                                        requireActivity(),
                                        TAG
                                    )
                                }
                            }else{
                                recyclerViewList?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text =  resources.getString(R.string.no_results)
                            }
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            recyclerViewList?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getAcaddemic = ArrayList<AcademicListModel.AccademicDetail>()
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })

    }



    class ListAdapter(
        var itemClickListener: AcademicClickListener,
        var subjects: Array<String>,
        var context: Context, ) :
        RecyclerView.Adapter<ListAdapter.ViewHolder>() {
        var index = 0

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textSubject: TextView = view.findViewById(R.id.textAssignmentName)
            var cardView: CardView = view.findViewById(R.id.cardView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.academic_type_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textSubject.text = subjects[position]
            holder.cardView.setOnClickListener {
                itemClickListener.onClick(position)
                index = position;
                notifyDataSetChanged()
            }

            if (index == position) {
                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.green_400))
                holder.textSubject.setTextColor(context.resources.getColor(R.color.white))
               // holder.textSubject.setTextColor(context.resources.getColor(R.color.black))
            } else {
                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.white))
                holder.textSubject.setTextColor(context.resources.getColor(R.color.green_400))
            }
        }

        override fun getItemCount(): Int {
            return subjects.size
        }

    }


    class SubjectAdapter(
        var academicClickListener : AcademicClickListener,
        var getSubjectList: ArrayList<SubjectListModel.Subject>,
        var mContext: Context, var TAG: String)
        : RecyclerView.Adapter<SubjectAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var shapeImageView: ShapeableImageView = view.findViewById(R.id.shapeImageView)
            var textSubjectName: TextView = view.findViewById(R.id.textSubjectName)
            var textStatus: TextView = view.findViewById(R.id.textStatus)
            var textCategoryName : TextView = view.findViewById(R.id.textCategoryName)
        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.subject_list_adapter, parent, false)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textSubjectName.text = getSubjectList[position].sUBJECTNAME
            holder.textStatus.text = "Code : ${getSubjectList[position].sUBJECTCODE}"
            holder.textCategoryName.text = "Category : ${getSubjectList[position].sUBJECTCATNAME}"

            when (getSubjectList[position].sUBJECTNAME.toLowerCase()) {
                "English".toLowerCase() -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_english)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl

                    Glide.with(mContext)
                        .load(R.drawable.ic_study_english)
                        .into(holder.shapeImageView)
                }
                "Chemistry".toLowerCase() -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_chemistry)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_chemistry)
                        .into(holder.shapeImageView)
                }
                "Biology".toLowerCase() -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_bio)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_biology)
                        .into(holder.shapeImageView)
                }
                "Maths".toLowerCase() -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_maths)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_maths)
                        .into(holder.shapeImageView)
                }
                "Hindi".toLowerCase() -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_hindi)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_hindi)
                        .into(holder.shapeImageView)
                }
                "Physics".toLowerCase() -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_physics)
                        .into(holder.shapeImageView)
                }
                "Malayalam".toLowerCase() -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_malayalam)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_malayalam)
                        .into(holder.shapeImageView)
                }
                "Arabic".toLowerCase() -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_arabic)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_arabic)
                        .into(holder.shapeImageView)
                }
                "Accountancy".toLowerCase() -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_accounts)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_accountancy)
                        .into(holder.shapeImageView)
                }
                "Social Science".toLowerCase() -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_social)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_social)
                        .into(holder.shapeImageView)
                }
                "Economics".toLowerCase() -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_econonics)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_economics)
                        .into(holder.shapeImageView)
                }
                "Basic Science".toLowerCase() -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_bio)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_biology)
                        .into(holder.shapeImageView)
                }
                "Computer".toLowerCase() -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_computer)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_computer)
                        .into(holder.shapeImageView)
                }
                "General".toLowerCase() -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_computer)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_general)
                        .into(holder.shapeImageView)
                }
                else ->{
                    val colorInt: Int = mContext.resources.getColor(R.color.color100_general)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_general)
                        .into(holder.shapeImageView)
                }
            }

            holder.itemView.setOnClickListener {
                academicClickListener.onUpdateSubject(getSubjectList[position])
            }
        }

        override fun getItemCount(): Int {
            return getSubjectList.size
        }

    }

    class SubCategoryAdapter(
        var academicClickListener : AcademicClickListener,
        var getSubjectCategory: ArrayList<SubCategoryListModel.Subjectcategory>,
        var mContext: Context, var TAG: String)
        : RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textSubCategory: TextView = view.findViewById(R.id.textSubCategory)

        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.sub_category_adapter, parent, false)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textSubCategory.text = "Category : ${getSubjectCategory[position].sUBJECTCATNAME}"

            holder.itemView.setOnClickListener {
                academicClickListener.onUpdateSubCategory(getSubjectCategory[position])
            }

        }

        override fun getItemCount(): Int {
            return getSubjectCategory.size
        }

    }

    class ClassAdapter(
        var academicClickListener : AcademicClickListener,
        var getClassList: ArrayList<ClassListModel.Class>,
        var mContext: Context, var TAG: String)
        : RecyclerView.Adapter<ClassAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textClassName: TextView = view.findViewById(R.id.textClassName)
            var textCategoryName: TextView = view.findViewById(R.id.textCategoryName)
        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.class_list_adapter, parent, false)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textClassName.text = "Class Name : ${getClassList[position].cLASSNAME}"
            holder.textCategoryName.text = "Description : ${getClassList[position].cLASSDESCRIPTION}"

            holder.itemView.setOnClickListener {
                academicClickListener.onUpdateClassDetails(getClassList[position])
            }
        }

        override fun getItemCount(): Int {
            return getClassList.size
        }

    }

    class AcademicYearListAdapter(
        var academicClickListener : AcademicClickListener,
        var getAcaddemic: ArrayList<AcademicListModel.AccademicDetail>,
        var mContext: Context, var TAG: String)
        : RecyclerView.Adapter<AcademicYearListAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textSubCategory: TextView = view.findViewById(R.id.textSubCategory)

        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.sub_category_adapter, parent, false)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textSubCategory.text = "Academic Year : ${getAcaddemic[position].aCCADEMICTIME}"

            holder.itemView.setOnClickListener {
                academicClickListener.onUpdateYearDetails(getAcaddemic[position])
            }
        }

        override fun getItemCount(): Int {
            return getAcaddemic.size
        }

    }

    override fun onSuccessMessage(message: String) {
        Utils.getSnackBarGreen(requireActivity(),message,constraintLayoutContent!!)
        onClick(listIndex)
    }

    override fun onMessageListener(message: String) {
        Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)
    }

    override fun onClick(position: Int) {
        listIndex = position
        when (position) {
            0 -> {
                subjectFunction()
            }
            1 -> {
                subCategoryFun()
            }
            2 -> {
                classList()
            }
            3 -> {
                yearFunction()
            }
        }

    }

    override fun onDeleteFunction(url: String, paramsMap: HashMap<String?, Int>) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure want to delete?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->

                deleteDetailsFun(url,paramsMap)
            }
            .setNegativeButton(
                "No"
            ) { dialog, _ -> //  Action for 'NO' Button
                dialog.cancel()
            }
        //Creating dialog box
        val alert = builder.create()
        //Setting the title manually
        alert.setTitle("Delete")
        alert.show()
        val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonbackground.setTextColor(Color.BLACK)
        val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonbackground1.setTextColor(Color.BLACK)
        true
    }

    override fun onUpdateSubject(subjectList: SubjectListModel.Subject) {
        bottomSheetUpdateSubject = BottomSheetUpdateSubjet(this,subjectList)
        bottomSheetUpdateSubject.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onUpdateSubCategory(subjectCategory: SubCategoryListModel.Subjectcategory) {
        bottomSheetUpdateSubjectCat = BottomSheetUpdateSubjectCategory(this,subjectCategory)
        bottomSheetUpdateSubjectCat.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onUpdateClassDetails(classList: ClassListModel.Class) {
        bottomSheetUpdateClass = BottomSheetUpdateClass(this,classList)
        bottomSheetUpdateClass.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onUpdateYearDetails(academicDetail: AcademicListModel.AccademicDetail) {
        bottomSheetUpdateYear = BottomSheetUpdateYear(this,academicDetail)
        bottomSheetUpdateYear.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onCreateClick(url: String, submitItems: RequestBody?, successMsg: String,
                               failerMsg: String, existMsg :String) {
        academicManagementViewModel.getCommonPostFun(url,submitItems)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "1" -> {
                                    Utils.getSnackBarGreen(requireActivity(), successMsg, constraintLayoutContent!!)
                                    when (listIndex) {
                                        0 -> {
                                            bottomSheetUpdateSubject.dismiss()
                                            subjectFunction()
                                        }
                                        1 -> {
                                            subCategoryFun()
                                            bottomSheetUpdateSubjectCat.dismiss()
                                        }
                                        2 -> {
                                            classList()
                                            bottomSheetUpdateClass.dismiss()
                                        }
                                        3 -> {
                                            yearFunction()
                                            bottomSheetUpdateYear.dismiss()
                                        }
                                    }
                                }
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBar4K(requireActivity(), failerMsg, constraintLayoutContent!!)
                                }
                                Utils.resultFun(response) == "-1" -> {
                                    Utils.getSnackBar4K(requireActivity(), existMsg, constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }



    fun deleteDetailsFun(url : String,paramsMap : HashMap<String?, Int>){
        academicManagementViewModel.getCommonGetFuntion(url,paramsMap)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource ${resource.data}")
                    Log.i(TAG,"message ${resource.message}")
                    Log.i(TAG,"errorBody ${resource.data?.errorBody().toString()}")
                    Log.i(TAG,"message ${resource.data?.errorBody()}")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when {
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Deleted Successfully", constraintLayoutContent!!)
                                    when (listIndex) {
                                        0 -> {
                                            bottomSheetUpdateSubject.dismiss()
                                            subjectFunction()
                                        }
                                        1 -> {
                                            subCategoryFun()
                                            bottomSheetUpdateSubjectCat.dismiss()
                                        }
                                        2 -> {
                                            classList()
                                            bottomSheetUpdateClass.dismiss()
                                        }
                                        3 -> {
                                            yearFunction()
                                            bottomSheetUpdateYear.dismiss()
                                        }
                                    }
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Deletion Failed", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLayoutContent!!)
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"loading")
                        }
                    }
                }
            })
    }

    private fun progressStart() {
        val dialog1 = ProgressBarDialog()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.isCancelable = false
        dialog1.show(transaction, ProgressBarDialog.TAG)
    }

    fun progressStop() {
        val fragment: ProgressBarDialog? =
            requireActivity().supportFragmentManager.findFragmentByTag(ProgressBarDialog.TAG) as ProgressBarDialog?
        if (fragment != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
    }
}
interface AcademicClickListener {
    fun onSuccessMessage(message : String)
    fun onMessageListener(message : String)
    fun onClick(position: Int)
    fun onDeleteFunction(url : String,paramsMap : HashMap<String?, Int>)
    fun onCreateClick(url : String,submitItems: RequestBody?,successMsg : String, failerMsg : String,existMsg :String)
    fun onUpdateSubject(subjectList: SubjectListModel.Subject)
    fun onUpdateSubCategory(subjectCategory: SubCategoryListModel.Subjectcategory)
    fun onUpdateClassDetails(classList: ClassListModel.Class)
    fun onUpdateYearDetails(academicDetail: AcademicListModel.AccademicDetail);


}
