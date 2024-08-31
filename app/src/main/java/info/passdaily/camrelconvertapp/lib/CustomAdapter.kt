package info.passdaily.camrelconvertapp.lib

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global


class CustomAdapter(
    var context: Context,
    var studentModel: List<ChildrensModel.Children>

) : BaseAdapter() {
    var TAG="CustomAdapter"
    val mInflater: LayoutInflater = LayoutInflater.from(context)
     override fun getItem(position: Int): Any? {

        return null

    }

    override fun getItemId(position: Int): Long {

        return 0

    }

    override fun getCount(): Int {
        return studentModel.size
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = mInflater.inflate(R.layout.layout_drop_title, parent, false)
        val vh = ItemRowHolder(view)
        Glide.with(context)
            .load(Global.student_image_url+studentModel[position].sTUDENTIMAGE)
            .into(vh.imageViewChild)
        vh.label.text = studentModel[position].sTUDENTFNAME
        Log.i(TAG,"sTUDENTFNAME "+ studentModel[position].sTUDENTFNAME)
        Log.i(TAG,"sTUDENTIMAGE "+ studentModel[position].sTUDENTIMAGE)
        Log.i(TAG,"sTUDENTID "+ studentModel[position].sTUDENTID)
        //  cardViewColor.setCardBackgroundColor(Color.parseColor(mPois[position].colorCode.toString()))
        return view
    }

    private class ItemRowHolder(row: View?) {

        val label: TextView = row?.findViewById(R.id.textCitiesName) as TextView
        var imageViewChild: ImageView = row?.findViewById(R.id.imageViewChild) as ImageView

    }


}
