package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.imageview.ShapeableImageView
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetNumberBinding

class BottomSheetNumbers : BottomSheetDialogFragment,NumberListener{


    private var _binding: BottomSheetNumberBinding? = null
    private val binding get() = _binding!!

    var recyclerViewNumber : RecyclerView? = null

    var buttonClear : AppCompatButton? = null
    var buttonSubmit  : AppCompatButton? = null

    var titleName  = ""
    var numberList = ArrayList<NumberList>()

    var numberListS = ArrayList<NumberList>()

    lateinit var assignmentCreateListener:AssignmentCreateListener
    var index = 0

    constructor()


    constructor(titleName: String,assignmentCreateListener: AssignmentCreateListener,numberListS : ArrayList<NumberList>,index: Int) {
        this.numberListS = numberListS
        this.titleName = titleName
        this.assignmentCreateListener = assignmentCreateListener
        this.index = index
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = BottomSheetNumberBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerViewNumber = binding.recyclerViewNumber
        recyclerViewNumber?.layoutManager = GridLayoutManager(requireActivity(),5)

        recyclerViewNumber?.adapter = NumberAdapter(this,numberListS,requireActivity())
        buttonClear = binding.buttonClear
        buttonSubmit = binding.buttonSubmit

        buttonSubmit!!.setOnClickListener {
            var numbersTxt = ""
            for(i in numberList){
                if(i.isSelect){
                    numbersTxt += i.number.toString()+","
                }
            }

            assignmentCreateListener.onSelectNumberCount(numberList,index,numbersTxt)
        }

        buttonClear!!.setOnClickListener {
            this.numberList = ArrayList<NumberList>()
            assignmentCreateListener.onSelectNumberCount(numberList,index,"")
        }


        binding.textViewSelect.text = titleName

    }




    class NumberAdapter(
        var numberListener: NumberListener,
        var countSize: ArrayList<NumberList>,
        var mContext: Context
    ) :
        RecyclerView.Adapter<NumberAdapter.ViewHolder>() {

        var mylist = ArrayList<Int>()
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewNumber: TextView = view.findViewById(R.id.textViewNumber)
            var shapeImageView: ShapeableImageView = view.findViewById(R.id.shapeImageView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.number_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.textViewNumber.text = countSize[position].number.toString()


            if (countSize[position].isSelect) {
                // viewHolder.checkBox.setChecked(true);
                holder.textViewNumber.setTextColor(mContext.resources.getColor(R.color.white))
                val colorInt: Int = mContext.resources.getColor(R.color.green_600)
                val csl = ColorStateList.valueOf(colorInt)
                holder.shapeImageView.strokeColor = csl
                holder.shapeImageView.setBackgroundColor(mContext.resources.getColor(R.color.green_600))
               // mylist.add(position)

            } else {
                //viewHolder.checkBox.setChecked(false);
                holder.textViewNumber.setTextColor(mContext.resources.getColor(R.color.green_600))
                val colorInt: Int = mContext.resources.getColor(R.color.green_100)
                val csl = ColorStateList.valueOf(colorInt)
                holder.shapeImageView.strokeColor = csl
                holder.shapeImageView.setBackgroundColor(mContext.resources.getColor(R.color.green_100))
                //mylist.remove(position)
            }

            numberListener.onSelectListener(countSize)

            holder.itemView.setOnClickListener {
                countSize[position].isSelect = !countSize[position].isSelect
                notifyItemChanged(position)
            }

        }

        override fun getItemCount(): Int {
            return countSize.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

    }

    companion object {
        var TAG = "BottomSheetFragment"
    }

    override fun onSelectListener(numberList: ArrayList<NumberList>) {
        this.numberList = ArrayList<NumberList>()
        this.numberList = numberList
    }


}
interface NumberListener{
    fun onSelectListener(numberList: ArrayList<NumberList>)
}
class NumberList(var number : String, var isSelect : Boolean)