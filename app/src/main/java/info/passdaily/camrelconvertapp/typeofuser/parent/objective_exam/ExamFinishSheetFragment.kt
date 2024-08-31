package info.passdaily.camrelconvertapp.typeofuser.parent.objective_exam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import info.passdaily.camrelconvertapp.R

class ExamFinishSheetFragment : BottomSheetDialogFragment {

    var tOTALQUESTIONS = 0
    var aNSWEREDQUESTIONS = 0
    var TO_BE_ANSWERED: Int = 0
    var finish = 0

    var textViewAnswered : TextView? = null
    var textViewNotAnswered : TextView? = null
    var textViewTotQuestion : TextView? = null
    var textAreYouSure : TextView? = null
    lateinit var finishClickListener: FinishClickListener

    constructor(finishClickListener: FinishClickListener,tOTALQUESTIONS: Int,aNSWEREDQUESTIONS: Int,TO_BE_ANSWERED : Int,finish : Int ) {
        this.finishClickListener = finishClickListener
        this.tOTALQUESTIONS = tOTALQUESTIONS
        this.aNSWEREDQUESTIONS = aNSWEREDQUESTIONS
        this.TO_BE_ANSWERED = TO_BE_ANSWERED
        this.finish = finish
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
        return inflater.inflate(R.layout.bottomsheetfor_objective_exam, container, false)
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        hashMapArrayList= new ArrayList <>();
        textViewAnswered = view.findViewById(R.id.textViewAnswered)
        textViewNotAnswered = view.findViewById(R.id.textViewNotAnswered)
        textViewTotQuestion = view.findViewById(R.id.textViewTotQuestion)
        textAreYouSure = view.findViewById(R.id.textAreYouSure)

        textViewAnswered?.text = aNSWEREDQUESTIONS.toString()
        textViewNotAnswered?.text = TO_BE_ANSWERED.toString()
        textViewTotQuestion?.text = tOTALQUESTIONS.toString()

        val cancelButton  = view.findViewById(R.id.cancelButton) as AppCompatButton
        val finishButton  = view.findViewById(R.id.finishButton) as AppCompatButton
        val viewResultButton  = view.findViewById(R.id.viewResultButton) as AppCompatButton


        if(finish == 2){
            cancelButton.visibility = View.GONE
            finishButton.visibility = View.GONE
            textAreYouSure?.visibility = View.GONE
            viewResultButton.visibility = View.VISIBLE
        }else{
            cancelButton.visibility = View.VISIBLE
            finishButton.visibility = View.VISIBLE
            textAreYouSure?.visibility = View.VISIBLE
            viewResultButton.visibility = View.GONE
        }


        cancelButton.setOnClickListener {
            dialog?.dismiss()
            finishClickListener.onCancelClick()
        }
        finishButton.setOnClickListener {
            finishClickListener.onFinishClick()
        }
        viewResultButton.setOnClickListener {
            finishClickListener.onViewResult()
        }

    }

    companion object {
        var TAG = "ExamFinishSheetFragment"
    }

}



