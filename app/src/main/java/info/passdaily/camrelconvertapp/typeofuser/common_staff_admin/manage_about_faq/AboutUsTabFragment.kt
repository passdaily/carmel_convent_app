package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_about_faq

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentAttendedTabBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_album.BottomSheetUpdateAlbum
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_notification.CreateNotificationDialog
import okhttp3.RequestBody
import java.util.ArrayList

class AboutUsTabFragment(
    var manageAboutUsListener: ManageAboutUsListener,
    var type: Int
) : Fragment(),AboutUsTabListener {


    var TAG = "AboutUsTabFragment"
    private var _binding: FragmentAttendedTabBinding? = null
    private val binding get() = _binding!!

    var aboutUsFaqList = ArrayList<AboutusFaqListModel.Aboutus>()

    private lateinit var manageAboutFaqViewModel: ManageAboutFaqViewModel

    var recyclerView : RecyclerView? = null

    lateinit var mAdapter: AboutUsAdapter

    lateinit var bottomSheetUpdate : BottomSheetUpdate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        manageAboutFaqViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ManageAboutFaqViewModel::class.java]

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

        if(type == 1){
            this.aboutUsFaqList = Global.aboutUsFaqList
        }else if(type == 2){
            this.aboutUsFaqList = Global.faqAboutUsList
        }

        if(aboutUsFaqList.isNotEmpty()){
            recyclerView?.visibility = View.VISIBLE
            constraintEmpty.visibility = View.GONE

            if(isAdded){
                mAdapter = AboutUsAdapter(
                    this,
                    aboutUsFaqList,
                    requireActivity(),
                    TAG
                )
            }

            recyclerView?.adapter = mAdapter

        }else{
            recyclerView?.visibility = View.GONE
            constraintEmpty.visibility = View.VISIBLE
            Glide.with(this)
                .load(R.drawable.ic_empty_progress_report)
                .into(imageViewEmpty)

            textEmpty.text =  resources.getString(R.string.no_results)
        }

        bottomSheetUpdate = BottomSheetUpdate()

        binding.fab.visibility = View.VISIBLE
        binding.fab.setOnClickListener {

            val dialog1 = CreateAboutUsFaqDialog(this,type)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, CreateAboutUsFaqDialog.TAG)
        }
    }
    class AboutUsAdapter(var aboutUsTabListener: AboutUsTabListener,
                         var aboutUsFaqList: ArrayList<AboutusFaqListModel.Aboutus>,
                         var context: Context, var TAG: String)
        : RecyclerView.Adapter<AboutUsAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewTitle: TextView = view.findViewById(R.id.textViewClass)
            var textViewDesc: TextView = view.findViewById(R.id.textViewTitle)

        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.aboutus_tab_adapter, parent, false))
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewTitle.text = aboutUsFaqList[position].aBTFAQTITLE

            holder.textViewDesc.text = aboutUsFaqList[position].aBTFAQDESCRIPTION

            holder.itemView.setOnClickListener {
                aboutUsTabListener.onDetailsClick(aboutUsFaqList[position])
            }
        }

        override fun getItemCount(): Int {
            return aboutUsFaqList.size
        }

    }

    override fun onShowMessage(message: String) {
        Log.i(TAG,"onCreateClick ")
        manageAboutUsListener.onFailedMessage(message)

    }

    override fun onDetailsClick(message: AboutusFaqListModel.Aboutus) {
        bottomSheetUpdate = BottomSheetUpdate(this,message,type)
        bottomSheetUpdate.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onUpdateClick(
        url: String,
        submitItems: RequestBody,
        successMessage: String,
        failedMessage: String,
        existingMessage: String
    ) {
        manageAboutFaqViewModel.getCommonPostFun(url,submitItems)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "1" -> {
                                    manageAboutUsListener.onSuccessMessage(successMessage)
                                    bottomSheetUpdate.dismiss()
                                }
                                Utils.resultFun(response) == "0" -> {
                                    manageAboutUsListener.onFailedMessage(failedMessage)
                                }
                                Utils.resultFun(response) == "-1" -> {
                                    manageAboutUsListener.onFailedMessage(existingMessage)
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

    override fun onDeleteItem(aBTFAQID: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure want to delete?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                deleteFunction(aBTFAQID)
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
    }


    fun deleteFunction(aBTFAQID: Int) {
        manageAboutFaqViewModel.getAboutUsFaqDelete("AboutFaq/DeleteAboutFaq",aBTFAQID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "0" -> {
                                    manageAboutUsListener.onSuccessMessage("Deleted Successfully")
                                    bottomSheetUpdate.dismiss()
                                }
                                else -> {
                                    manageAboutUsListener.onFailedMessage("Deletion Failed")
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

interface AboutUsTabListener{
    fun onShowMessage(message: String)
    fun onDetailsClick(message: AboutusFaqListModel.Aboutus)
    fun onUpdateClick(url: String, submitItems: RequestBody, successMessage: String, failedMessage: String, existingMessage: String)
    fun onDeleteItem(aBTFAQID: Int)


}