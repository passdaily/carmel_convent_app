package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentObjUnattendedTabBinding
import info.passdaily.camrelconvertapp.model.*


class ObjUnAttendedTAbFragment(var unAttendedListModel: ArrayList<UnAttendedListModel.UnAttended>) : Fragment() {

    var TAG = "ObjDetailsTabFragment"
    private var _binding: FragmentObjUnattendedTabBinding? = null
    private val binding get() = _binding!!

    var searchView : SearchView? = null
    var recyclerView : RecyclerView? = null

    lateinit var mAdapter: UnAttendedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentObjUnattendedTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var constraintEmpty = binding.constraintEmpty
        var imageViewEmpty = binding.imageViewEmpty
        var textEmpty = binding.textEmpty
        var shimmerViewContainer = binding.shimmerViewContainer
        searchView = binding.searchView

        recyclerView = binding.recyclerView
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())
        mAdapter = UnAttendedAdapter(
            unAttendedListModel,
            unAttendedListModel,
            requireActivity(),
            TAG
        )
        recyclerView?.adapter = mAdapter

        searchView!!.setSearchableInfo(
            (requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager).getSearchableInfo(
                requireActivity().componentName
            )
        )
        searchView!!.maxWidth = Int.MAX_VALUE
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(str: String): Boolean {
               mAdapter.getFilter()!!.filter(str)
                Log.i(TAG, "onQueryTextSubmit $str")
                return false
            }
            override fun onQueryTextChange(str: String): Boolean {
                mAdapter.getFilter()!!.filter(str)
                Log.i(TAG, "onQueryTextChange $str")
                return false
            }
        })
        if(unAttendedListModel.isNotEmpty()){
            recyclerView?.visibility = View.VISIBLE
            constraintEmpty.visibility = View.GONE

        }else{
            recyclerView?.visibility = View.GONE
            constraintEmpty.visibility = View.VISIBLE
            Glide.with(this)
                .load(R.drawable.ic_empty_progress_report)
                .into(imageViewEmpty)

            textEmpty.text =  resources.getString(R.string.no_results)
        }
    }

    class UnAttendedAdapter(var unAttendedListModel: ArrayList<UnAttendedListModel.UnAttended>,
                            var unAttendedList: ArrayList<UnAttendedListModel.UnAttended>,
                            var context: Context, var TAG: String)
        : RecyclerView.Adapter<UnAttendedAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textName: TextView = view.findViewById(R.id.textName)
            var textGuardianName: TextView = view.findViewById(R.id.textGuardianName)
            var textGuardianNumber: TextView = view.findViewById(R.id.textGuardianNumber)
            var imageViewCall : ImageView = view.findViewById(R.id.imageCall)
        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): UnAttendedAdapter.ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.unattended_adapter, parent, false)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: UnAttendedAdapter.ViewHolder, position: Int) {
            holder.textGuardianName.text = "Guardian : " + unAttendedListModel[position].sTUDENTGUARDIANNAME
            holder.textName.text = unAttendedListModel[position].sTUDENTNAME
            holder.textGuardianNumber.text = unAttendedListModel[position].sTUDENTGUARDIANNUMBER

            holder.imageViewCall.setOnClickListener {
                try {
                    var phone = unAttendedListModel[position].sTUDENTGUARDIANNUMBER
                    val intent = Intent("android.intent.action.DIAL")
                    intent.data = Uri.parse("tel:$phone")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.i(TAG, "Exception $e")
                }
            }
        }

        override fun getItemCount(): Int {
           return unAttendedListModel.size
        }

        fun getFilter(): Filter? {
            return object : Filter() {
                /* access modifiers changed from: protected */
                override fun performFiltering(charSequence: CharSequence): FilterResults {
                    val charSequence2 = charSequence.toString()
                    Log.i(TAG, "charString $charSequence2")
                    if (charSequence2.isEmpty()) {
                        unAttendedListModel = unAttendedList
                    } else {
                        val arrayList: ArrayList<UnAttendedListModel.UnAttended> = ArrayList()
                        for (unAttended in unAttendedListModel) {
                            Log.i(TAG, "row $unAttended")
                            if (unAttended.sTUDENTNAME.lowercase()
                                    .contains(charSequence2.lowercase())
                                || unAttended.sTUDENTGUARDIANNUMBER
                                    .contains(charSequence) || unAttended.sTUDENTGUARDIANNAME
                                    .lowercase().contains(charSequence2.lowercase())
                            ) {
                                arrayList.add(unAttended)
                            }
                        }
                        unAttendedListModel = arrayList
                    }
                    val filterResults = FilterResults()
                    filterResults.values = unAttendedListModel
                    Log.i(TAG, "filterResults $filterResults")
                    return filterResults
                }

                /* access modifiers changed from: protected */
                @SuppressLint("NotifyDataSetChanged")
                override fun publishResults(
                    charSequence: CharSequence,
                    filterResults: FilterResults
                ) {
                    unAttendedListModel = filterResults.values as ArrayList<UnAttendedListModel.UnAttended>
                    notifyDataSetChanged()
                }
            }
        }
    }

}