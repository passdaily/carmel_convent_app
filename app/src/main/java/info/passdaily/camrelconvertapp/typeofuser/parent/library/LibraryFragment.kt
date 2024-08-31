package info.passdaily.camrelconvertapp.typeofuser.parent.library

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentLibraryBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper


@Suppress("DEPRECATION")
class LibraryFragment : Fragment() {

    var TAG = "LibraryFragment"

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0

    var recyclerViewLibraryList: RecyclerView? = null

    private lateinit var libraryViewModel: LibraryViewModel

    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null

    var mContext: Context? = null

    var shimmerViewContainer : ShimmerFrameLayout? =null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext;
        }
        Log.i(TAG, "onAttach ")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.currentPage = 10
        Global.screenState = "landingpage"
        // Inflate the layout for this fragment

        libraryViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[LibraryViewModel::class.java]

        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
      ///  libraryViewModel = ViewModelProvider(this).get(LibraryViewModel::class.java)
    //    return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var studentDBHelper = StudentDBHelper(requireActivity())
        var student = studentDBHelper.getStudentById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID

        var textViewTitle = binding.textView32
        textViewTitle.text = "Library"
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        if (isAdded) {
            Glide.with(mContext!!)
                .load(R.drawable.ic_empty_state_library)
                .into(imageViewEmpty!!)
        }
        textEmpty?.text = "No Library Books"

       // libraryViewModel.getLibraryList(STUDENTID)
        recyclerViewLibraryList = binding.recyclerView
        recyclerViewLibraryList?.layoutManager = LinearLayoutManager(requireActivity())

        shimmerViewContainer = binding.shimmerViewContainer

        intiFunction()
    }

    private fun intiFunction() {
//        libraryViewModel.getLibraryListObservable().observe(requireActivity(), {
//
//            if (it != null) {
//                if (it.bookIssueDetails.isNotEmpty()) {
//                    constraintEmpty?.visibility = View.GONE
//                    recyclerViewLibraryList?.visibility = View.VISIBLE
//                    if (isAdded) {
//                        recyclerViewLibraryList?.adapter =
//                            LibraryAdapter(it.bookIssueDetails, mContext!!)
//                    }
//                } else {
//                    constraintEmpty?.visibility = View.VISIBLE
//                    recyclerViewLibraryList?.visibility = View.GONE
//                }
//            }
//        })
        libraryViewModel.getLibraryList(STUDENTID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if(response.bookIssueDetails.isNotEmpty()){
                                constraintEmpty?.visibility = View.GONE
                                recyclerViewLibraryList?.visibility = View.VISIBLE
                                shimmerViewContainer?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewLibraryList?.adapter =
                                        LibraryAdapter(response.bookIssueDetails, mContext!!)
                                }
                            }else{
                                constraintEmpty?.visibility = View.VISIBLE
                                recyclerViewLibraryList?.visibility = View.GONE
                                shimmerViewContainer?.visibility = View.GONE
                                if (isAdded) {
                                    Glide.with(mContext!!)
                                        .load(R.drawable.ic_empty_state_absent)
                                        .into(imageViewEmpty!!)
                                }
                                textEmpty?.text =  requireActivity().resources.getString(R.string.no_results)
                            }
                        }
                        Status.ERROR -> {
                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_no_internet)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text =  requireActivity().resources.getString(R.string.no_internet)
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewLibraryList?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                        }
                        Status.LOADING -> {
                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_empty_state_absent)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text =  requireActivity().resources.getString(R.string.loading)
                            shimmerViewContainer?.visibility = View.VISIBLE
                            recyclerViewLibraryList?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                        }
                    }
                }
            })
    }

    class LibraryAdapter(var libraryList: ArrayList<LibraryModel.BookIssueDetail>,
                         var context: Context) : RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {

        var issuedDate = ""
        var returnDate = ""
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewBookName: TextView = view.findViewById(R.id.textViewBookName)
            var textViewAuthor: TextView = view.findViewById(R.id.textViewAuthor)
            var textViewIssuedDate: TextView = view.findViewById(R.id.textViewIssuedDate)
            val textViewReturnDate : TextView = view.findViewById(R.id.textViewReturnDate)

            val textViewBookId : TextView = view.findViewById(R.id.textViewBookId)
            val textViewRate : TextView = view.findViewById(R.id.textViewRate)
            var cardViewStatus  : CardView = view.findViewById(R.id.cardViewStatus)
            val textViewStatus : TextView = view.findViewById(R.id.textViewStatus)

            val cardViewBook  : CardView = view.findViewById(R.id.cardViewBook)
            val imageViewBook  : ImageView = view.findViewById(R.id.imageViewBook)

            val textViewFine : TextView = view.findViewById(R.id.textViewFine)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.library_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Glide.with(context)
                .load(R.drawable.book_sample)
                .into(holder.imageViewBook)
            createPaletteAsync((ContextCompat.getDrawable(context,R.drawable.book_sample) as BitmapDrawable).bitmap,holder.cardViewBook)
            holder.textViewBookName.text = libraryList[position].bOOKNAME
            holder.textViewAuthor.text = "Author Name : "+libraryList[position].bOOKAUTHOR

            if (!libraryList[position].iSSUEDATE.isNullOrBlank()) {
                val date: Array<String> =
                    libraryList[position].iSSUEDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
                issuedDate = Utils.formattedDateWords(dddd)
            }
            holder.textViewIssuedDate.text = "Issued Date : $issuedDate"

            if (!libraryList[position].rETURNEDDATE.isNullOrBlank()) {
                val date: Array<String> =
                    libraryList[position].rETURNEDDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
                returnDate = Utils.formattedDateWords(dddd)
            }
            holder.textViewReturnDate.text = "Return Date : $returnDate"
            holder.textViewBookId.text = "Book Id : "+libraryList[position].bOOKCODE


            if(libraryList[position].rETURNSTATUS ==2 ){
                holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.green_100))
                holder.textViewStatus.text = "Returned"
                holder.textViewStatus.setTextColor(context.resources.getColor(R.color.green_400))
                holder.textViewFine.text = ""
            }else{
                holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_physics))
                holder.textViewStatus.text = "Not Returned"
                holder.textViewStatus.setTextColor(context.resources.getColor(R.color.color_physics))

                if (libraryList[position].fINEAMOUNT == null) {
                    holder.textViewFine.text = "Fine Amount ₹ 0"
                    holder.textViewFine.setTextColor(Color.parseColor("#1e88e5"))
                } else {
                    holder.textViewFine.text = "Fine Amount ₹ " + libraryList[position].fINEAMOUNT
                    holder.textViewFine.setTextColor(Color.parseColor("#1e88e5"))
                }
            }

        }

        override fun getItemCount(): Int {
           return libraryList.size
        }

        private fun createPaletteAsync(bitmap: Bitmap, cardViewBook: CardView) {
                Palette.from(bitmap).generate(){ palette ->
                    // Change toolbar background color
//                    if(palette?.lightMutedSwatch!!.rgb == null) {
                        cardViewBook.setCardBackgroundColor(palette!!.lightMutedSwatch!!.rgb)
                }
//            }catch(e :NullPointerException){
//                cardViewBook.setCardBackgroundColor(context.resources.getColor(R.color.gray_200))
//            }

        }

    }



}