package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.admin_staff_punch_attendance

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentStaffAttendanceSuccessBinding

class AdminStaffAttendanceSuccessFragment(var text: String) : Fragment() {

    var TAG = "StaffAttendanceSuccessFragment"

    private var _binding: FragmentStaffAttendanceSuccessBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStaffAttendanceSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewStatus.text = text


        Handler(Looper.getMainLooper()).postDelayed({
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_staff_host_fragment, AdminStaffAttendanceFragment(0,""))
//            .addToBackStack("home").commit()
                .commit()
        }, 1000)

    }


}