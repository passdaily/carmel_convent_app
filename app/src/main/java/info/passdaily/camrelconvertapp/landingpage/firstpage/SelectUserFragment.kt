package info.passdaily.camrelconvertapp.landingpage.firstpage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.retrofit.ApiInterface

class SelectUserFragment : Fragment() {

    var imageBackPress : ImageView?= null
    var imageParent : ImageView?= null
    var imageTeacher : ImageView?= null

    var boarderStudent : ImageView?=null
    var boarderTeacher : ImageView?=null

    var constraintLayout : ConstraintLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Global.screenState = "landingpage"

        constraintLayout = view.findViewById(R.id.constraintLayout) as ConstraintLayout
        imageBackPress = view.findViewById(R.id.imageBackPress) as ImageView
        imageBackPress?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.frameContainer, FirstScreenFragment()
            ).commit()
        }

        boarderStudent = view.findViewById(R.id.boarderStudent) as ImageView
        boarderTeacher = view.findViewById(R.id.boarderTeacher) as ImageView
        boarderStudent?.visibility = View.GONE
        boarderTeacher?.visibility = View.GONE

        imageParent = view.findViewById(R.id.imageParent) as ImageView
        imageParent?.setOnClickListener {

            boarderStudent?.visibility = View.VISIBLE
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.frameContainer, LoginParentFragment("","")
            ).commit()

        }

        imageTeacher = view.findViewById(R.id.imageTeacher) as ImageView
        imageTeacher?.setOnClickListener {

            boarderTeacher?.visibility = View.VISIBLE
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.frameContainer, LoginStaffFragment("","")
            ).commit()

        }

        if(!Utils.isOnline(requireActivity())){
            Utils.getSnackBar4K(requireActivity(),requireActivity().resources.getString(R.string.no_internet),constraintLayout)
        }
    }

}