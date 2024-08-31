package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.calander

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import info.passdaily.camrelconvertapp.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import android.app.Activity
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.TextView
import com.github.sundeepk.compactcalendarview.domain.Event
import java.util.ArrayList

@Suppress("DEPRECATION")
class BottomSheetFragmentStaff : BottomSheetDialogFragment {
    var getListFeed = ArrayList<Event>()
    var recyclerView: RecyclerView? = null


    constructor(getListFeed: ArrayList<Event>) {
        this.getListFeed = getListFeed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.bottomsheetfor_calender, container, false)
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        hashMapArrayList= new ArrayList <>();
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())
        Log.i(TAG, "getList $getListFeed")
        val mListArrayAdapter = ListFeedAdapter(getListFeed, requireActivity())
        recyclerView?.adapter = mListArrayAdapter
    }

    private inner class ListFeedAdapter(var getListFeed: ArrayList<Event>, var activity: Activity) :
        RecyclerView.Adapter<ListFeedAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.bottomsheet_calender_adapter, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Log.i(TAG, "getListFeed " + getListFeed[position].data)
            val convertedToString =
                getListFeed[position].data.toString().split("~".toRegex()).toTypedArray()
            if (convertedToString[1] == "ACTIVITY_DAY") {
                holder.constraintLayout.setBackgroundColor(activity.resources.getColor(R.color.red_200))
            } else if (convertedToString[1] == "WORKING_DAY") {
                holder.constraintLayout.setBackgroundColor(activity.resources.getColor(R.color.green_300))
            }
            holder.textViewDate.text = convertedToString[0]
            holder.textViewDes.text = convertedToString[2]
        }

        override fun getItemCount(): Int {
            return getListFeed.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraintLayout)
            var textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
            var textViewDes: TextView = itemView.findViewById(R.id.textViewDes)

        }
    }

    companion object {
        var TAG = "BottomSheetFragment"
    }
}