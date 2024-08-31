package info.passdaily.camrelconvertapp.landingpage.firstpage

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.AssignmentDetailStaffActivity


class FirstScreenFragment : Fragment() {

    var cardview: CardView? = null

    var constraintLogin: ConstraintLayout? = null
    var constraintRegister: ConstraintLayout? = null

    //constraintForgot
    var constraintForgot: ConstraintLayout? = null
    var constraintAboutus: ConstraintLayout? = null
    var constraintFAQ: ConstraintLayout? = null
    var constraintContact: ConstraintLayout? = null

    private lateinit var localDBHelper: LocalDBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        localDBHelper = LocalDBHelper(requireActivity())
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Global.screenState = "homePage"
        cardview = view.findViewById(R.id.cardview) as CardView
        cardview?.setBackgroundResource(R.drawable.cardview_res)

        var constraintLayout = view.findViewById(R.id.constraintLayout) as ConstraintLayout

        constraintRegister = view.findViewById(R.id.constraintRegister)
        constraintLogin = view.findViewById(R.id.constraintLogin)
        constraintForgot = view.findViewById(R.id.constraintForgot)
        constraintAboutus = view.findViewById(R.id.constraintAboutus)
        constraintFAQ = view.findViewById(R.id.constraintFAQ)
        constraintContact = view.findViewById(R.id.constraintContact)


        constraintLogin?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.frameContainer, SelectUserFragment()).commit()
        }

        constraintRegister?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.frameContainer, RegisterFragment("Register Here","Register")).commit()
        }

        constraintForgot?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.frameContainer, RegisterFragment("Forgot Password","Continue")).commit()
        }

        constraintAboutus?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.frameContainer, AboutUsFragment()).commit()
        }

        constraintFAQ?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.frameContainer, FaqFragment()).commit()
        }

        constraintContact?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.frameContainer, ContactUsFragment()).commit()
        }

        var imageViewMore = view.findViewById(R.id.imageViewMore) as ImageView
        imageViewMore.setOnClickListener(View.OnClickListener {
            val popupMenu = PopupMenu(requireActivity(), imageViewMore)
            popupMenu.inflate(R.menu.first_menu)
            //popupMenu.menu.findItem(R.id.action_clear).icon = context.resources.getDrawable(R.drawable.ic_icon_edit)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_clear -> {
                        val builder = AlertDialog.Builder(requireActivity())
                        builder.setMessage("Do you want to clear all the data's stored in this app?")
                            .setCancelable(false)
                            .setPositiveButton("Yes") { _, _ ->
                                localDBHelper.deleteData(requireActivity())
                                //  clearData(this@MainActivityStaff)
//                    val log = Intent(applicationContext, FirstScreenActivity::class.java)
//                    startActivity(log)
//                    finish()
                                Utils.getSnackBarGreen(requireActivity(),"Data Cleared",constraintLayout)
                            }
                            .setNegativeButton(
                                "No"
                            ) { dialog, _ -> //  Action for 'NO' Button
                                dialog.cancel()
                            }


                        //Creating dialog box
                        val alert = builder.create()
                        //Setting the title manually
                        alert.setTitle("Clear Data")
                        alert.show()
                        val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                        buttonbackground.setTextColor(Color.BLACK)

                        val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                        buttonbackground1.setTextColor(Color.BLACK)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        })




        if(!Utils.isOnline(requireActivity())){
            Utils.getSnackBar4K(requireActivity(),requireActivity().resources.getString(R.string.no_internet),constraintLayout)
        }

    }
}