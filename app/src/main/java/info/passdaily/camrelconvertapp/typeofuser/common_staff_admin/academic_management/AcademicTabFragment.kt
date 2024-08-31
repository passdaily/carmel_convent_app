package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.academic_management

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentAttendedTabBinding
import info.passdaily.camrelconvertapp.model.AcademicListModel
import info.passdaily.camrelconvertapp.model.ClassListModel
import info.passdaily.camrelconvertapp.model.SubCategoryListModel
import info.passdaily.camrelconvertapp.model.SubjectListModel
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_notification.NotificationSentTabFragment


class AcademicTabFragment(var academicClickListener : AcademicClickListener , var type : Int) : Fragment() {

    var TAG = "AcademicTabFragment"
    private var _binding: FragmentAttendedTabBinding? = null
    private val binding get() = _binding!!

    var recyclerView : RecyclerView? = null
    var adminRole = 0

    lateinit var adapterSubject: SubjectAdapter
    lateinit var adapterCategory: SubCategoryAdapter
    lateinit var adapterClass: ClassAdapter
    lateinit var adapterYear: AcademicYearListAdapter


    private lateinit var localDBHelper : LocalDBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        // Inflate the layout for this fragment
        _binding = FragmentAttendedTabBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var constraintEmpty = binding.constraintEmpty
        var imageViewEmpty = binding.imageViewEmpty
        var textEmpty = binding.textEmpty
        var shimmerViewContainer = binding.shimmerViewContainer


        recyclerView = binding.recyclerView
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())

//        when (type) {
//            1 -> {
//                if(Global.getSubjectList.isNotEmpty()){
//                    recyclerView?.visibility = View.VISIBLE
//                    constraintEmpty.visibility = View.GONE
//                    adapterSubject =  SubjectAdapter(
//                        academicClickListener,
//                        Global.getSubjectList,
//                        requireActivity(),
//                        TAG
//                    )
//                    recyclerView?.adapter = adapterSubject
//
//                }else{
//                    recyclerView?.visibility = View.GONE
//                    constraintEmpty.visibility = View.VISIBLE
//                    Glide.with(this)
//                        .load(R.drawable.ic_empty_progress_report)
//                        .into(imageViewEmpty)
//
//                    textEmpty.text =  resources.getString(R.string.no_results)
//                }
//
//            }
//            2 -> {
//                if(Global.getSubjectCategory.isNotEmpty()){
//                    recyclerView?.visibility = View.VISIBLE
//                    constraintEmpty.visibility = View.GONE
//                    adapterCategory = SubCategoryAdapter(
//                        academicClickListener,
//                        Global.getSubjectCategory,
//                        requireActivity(),
//                        TAG
//                    )
//                    recyclerView?.adapter = adapterCategory
//
//                }else{
//                    recyclerView?.visibility = View.GONE
//                    constraintEmpty.visibility = View.VISIBLE
//                    Glide.with(this)
//                        .load(R.drawable.ic_empty_progress_report)
//                        .into(imageViewEmpty)
//
//                    textEmpty.text =  resources.getString(R.string.no_results)
//                }
//
//            }
//            3 -> {
//                if(Global.getClassList.isNotEmpty()){
//                    recyclerView?.visibility = View.VISIBLE
//                    constraintEmpty.visibility = View.GONE
//                    adapterClass = ClassAdapter(
//                        academicClickListener,
//                        Global.getClassList,
//                        requireActivity(),
//                        TAG
//                    )
//                    recyclerView?.adapter = adapterClass
//
//                }else{
//                    recyclerView?.visibility = View.GONE
//                    constraintEmpty.visibility = View.VISIBLE
//                    Glide.with(this)
//                        .load(R.drawable.ic_empty_progress_report)
//                        .into(imageViewEmpty)
//
//                    textEmpty.text =  resources.getString(R.string.no_results)
//                }
//
//            }
//            4 -> {
//                if(Global.getClassList.isNotEmpty()){
//                    recyclerView?.visibility = View.VISIBLE
//                    constraintEmpty.visibility = View.GONE
//                    adapterYear = AcademicYearListAdapter(
//                        academicClickListener,
//                        Global.getAcaddemic,
//                        requireActivity(),
//                        TAG
//                    )
//                    recyclerView?.adapter = adapterYear
//
//                }else{
//                    recyclerView?.visibility = View.GONE
//                    constraintEmpty.visibility = View.VISIBLE
//                    Glide.with(this)
//                        .load(R.drawable.ic_empty_progress_report)
//                        .into(imageViewEmpty)
//
//                    textEmpty.text =  resources.getString(R.string.no_results)
//                }
//
//            }
//        }
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

            when (getSubjectList[position].sUBJECTNAME) {
                "English" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_english_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl

                    Glide.with(mContext)
                        .load(R.drawable.ic_study_english)
                        .into(holder.shapeImageView)
                }
                "Chemistry" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_chemistry_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_chemistry)
                        .into(holder.shapeImageView)
                }
                "Biology" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_bio_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_biology)
                        .into(holder.shapeImageView)
                }
                "Maths" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_maths_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_maths)
                        .into(holder.shapeImageView)
                }
                "Hindi" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_hindi_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_hindi)
                        .into(holder.shapeImageView)
                }
                "Physics" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_physics_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_physics)
                        .into(holder.shapeImageView)
                }
                "Malayalam" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_malayalam_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_malayalam)
                        .into(holder.shapeImageView)
                }
                "Arabic" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_arabic_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_arabic)
                        .into(holder.shapeImageView)
                }
                "Accountancy" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_accounts_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_accountancy)
                        .into(holder.shapeImageView)
                }
                "Social Science" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_social_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_social)
                        .into(holder.shapeImageView)
                }
                "Economics" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_economics_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_economics)
                        .into(holder.shapeImageView)
                }
                "BasicScience" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_bio_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_biology)
                        .into(holder.shapeImageView)
                }
                "Computer" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_computer_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_computer)
                        .into(holder.shapeImageView)
                }
                "General" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_computer_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_computer)
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
}