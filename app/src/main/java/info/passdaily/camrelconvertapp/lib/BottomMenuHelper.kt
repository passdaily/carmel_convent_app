package info.passdaily.camrelconvertapp.lib

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.annotation.IdRes
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import info.passdaily.camrelconvertapp.lib.BottomMenuHelper
import android.view.LayoutInflater
import android.view.View
import info.passdaily.camrelconvertapp.R
import android.widget.TextView

class BottomMenuHelper {
    companion object {
        private const val TAG = "BottomMenuHelper"
        fun showBadge(
            context: Context?,
            bottomNavigationView: BottomNavigationView,
            @IdRes itemId: Int,
            value: String?
        ) {
            val itemView: BottomNavigationItemView = bottomNavigationView.findViewById(itemId)
            val badge = LayoutInflater.from(context).inflate(R.layout.badge_layout, bottomNavigationView, false)
            val text = badge!!.findViewById<TextView>(R.id.notificationsBadgeTextView)
            text.text = value
            itemView.addView(badge)
        }

        fun removeBadge(bottomNavigationView: BottomNavigationView, @IdRes itemId: Int) {
            val itemView: BottomNavigationItemView = bottomNavigationView.findViewById(itemId)
            Log.i(TAG, "Item Count-" + itemView.childCount)
            if (itemView.childCount == 3) {
                itemView.removeViewAt(2)
            }
        }
    }
}