package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_voice

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetVoiceMessageAdminBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_voice.send_to_voice.*
import java.io.IOException
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class BottomSheetVoiceMessageDetailsAdmin : BottomSheetDialogFragment {

    private var _binding: BottomSheetVoiceMessageAdminBinding? = null
    private val binding get() = _binding!!

    var aCCADEMICID = 0

    lateinit var voiceMessageTabClicker : VoiceMessageTabClicker

    private lateinit var quickVoiceMessageViewModel: QuickVoiceMessageViewModel

    lateinit var voiceMessageListModel: VoiceMessageListModel.Voice

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0

    var textViewTitle : TextView? = null
    var textViewDesc : TextView? = null

    var buttonParent : CardView? = null
    var buttonStaff  : CardView? = null
    var buttonGroup   : CardView? = null
    var buttonPublicGroup   : CardView? = null
    var buttonPTA   : CardView? = null
    var buttonConveyor  : CardView? = null
    var buttonClassDivision  : CardView? = null
    var buttonPublicGroupWise : CardView? = null
    var buttonGroupWise  : CardView? = null
    var buttonClassWise  : CardView? = null
    var buttonAllParents  : CardView? = null
    var buttonAllStaff  : CardView? = null
    var buttonAllConveyor : CardView? = null
    var buttonAllPta  : CardView? = null

    var textViewDate : TextView? = null
    var mediaPlayer = MediaPlayer()
    var seekHandler = Handler()
    var run: Runnable? = null
    var mSeekBar: SeekBar? = null
    var imageViewPlay : ImageView? = null
    var textSongDuration : TextView? = null

    var textDeleteIcon : TextView? = null

    var position = 0

    var constraintLayoutSeek : ConstraintLayout? = null

    constructor()

    constructor(voiceMessageTabClicker: VoiceMessageTabClicker, voiceMessageListModel: VoiceMessageListModel.Voice,
                position :Int){
        this.voiceMessageTabClicker = voiceMessageTabClicker
        this.voiceMessageListModel = voiceMessageListModel
        this.position = position

//        this.gMEMBERID = gMEMBERID
//        this.gROUPNAME = gROUPNAME
//        this.gMEMBERNUMBER = gMEMBERNUMBER
//        this.aCCADEMICID = aCCADEMICID
//        this.gROUPID = gROUPID
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

        quickVoiceMessageViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[QuickVoiceMessageViewModel::class.java]

        _binding = BottomSheetVoiceMessageAdminBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        textViewTitle  = binding.textViewTitle
        textViewDesc  = binding.textViewDesc
        textViewDate = binding.textViewDate
        textDeleteIcon = binding.textDeleteIcon
        constraintLayoutSeek = binding.constraintLayoutSeek

        buttonParent = binding.buttonParent
        buttonStaff = binding.buttonStaff
        buttonGroup  = binding.buttonGroup
        buttonPublicGroup = binding.buttonPublicGroup
        buttonPTA = binding.buttonPTA
        buttonConveyor = binding.buttonConveyor
        buttonClassDivision = binding.buttonClassDivision
        buttonPublicGroupWise = binding.buttonPublicGroupWise
        buttonGroupWise = binding.buttonGroupWise
        buttonClassWise = binding.buttonClassWise
        buttonAllParents = binding.buttonAllParents
        buttonAllStaff = binding.buttonAllStaff
        buttonAllConveyor = binding.buttonAllConveyor
        buttonAllPta = binding.buttonAllPta

        textViewTitle?.text = voiceMessageListModel.vOICEMAILTITLE
        if(!voiceMessageListModel.vOICEMAILDATE.isNullOrBlank()) {
            val date1: Array<String> = voiceMessageListModel.vOICEMAILDATE.split("T".toRegex()).toTypedArray()
            val sendStr = Utils.longconversion(date1[0] + " " + date1[1])
            textViewDate?.text = Utils.formattedDateTime(sendStr)
        }

        mSeekBar = binding.mSeekBar
        imageViewPlay  = binding.imageViewPlay
        textSongDuration  = binding.textSongDuration

        Log.i(TAG,"voice_url ${Global.voice_url + voiceMessageListModel.vOICEMAILFILE}")
        if(voiceMessageListModel.tEMPLATEID.isNotEmpty()) {
            mediaPlayer = MediaPlayer()
            constraintLayoutSeek?.visibility = View.VISIBLE
            textViewDesc?.text = "Approved on ${textViewDate?.text.toString()}"
            textViewDesc?.setTextColor(resources.getColor(R.color.green_600))
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            Log.i(TAG,"voice_url ${Global.voice_url + voiceMessageListModel.vOICEMAILFILE}")
            try {
                mediaPlayer.setDataSource(
                    Global.voice_url + voiceMessageListModel.vOICEMAILFILE
                )

                mediaPlayer.prepare() // might take long for buffering.
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (ex: IllegalStateException) {
                ex.printStackTrace()
            }
            mSeekBar!!.max = mediaPlayer.duration
            mSeekBar!!.tag = 0

            imageViewPlay?.setOnClickListener {
                if (!mediaPlayer.isPlaying) {
                    mediaPlayer.start()
                    imageViewPlay?.setImageResource(R.drawable.ic_exam_pause)
                    run = Runnable {
                        // Updateing SeekBar every 100 miliseconds
                        mSeekBar!!.progress = mediaPlayer.currentPosition
                        seekHandler.postDelayed(run!!, 100)
                        //For Showing time of audio(inside runnable)
                        val miliSeconds = mediaPlayer.currentPosition
                        if (miliSeconds != 0) {
                            //if audio is playing, showing current time;
                            val minutes = TimeUnit.MILLISECONDS.toMinutes(miliSeconds.toLong())
                            val seconds = TimeUnit.MILLISECONDS.toSeconds(miliSeconds.toLong())
                            if (minutes == 0L) {
                                textSongDuration?.text =
                                    "0 : $seconds | " + Utils.calculateDuration(
                                        mediaPlayer.duration
                                    )
                            } else {
                                if (seconds >= 60) {
                                    val sec = seconds - minutes * 60
                                    textSongDuration?.text =
                                        "$minutes : $sec | " + Utils.calculateDuration(
                                            mediaPlayer.duration
                                        )
                                }
                            }
                        } else {
                            //Displaying total time if audio not playing
                            val totalTime = mediaPlayer.duration
                            val minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime.toLong())
                            val seconds = TimeUnit.MILLISECONDS.toSeconds(totalTime.toLong())
                            if (minutes == 0L) {
                                textSongDuration?.text = "0 : $seconds"
                            } else {
                                if (seconds >= 60) {
                                    val sec = seconds - minutes * 60
                                    textSongDuration?.text = "$minutes : $sec"
                                }
                            }
                        }
                    }
                    run?.run()
                } else {
                    mediaPlayer.pause()
                    imageViewPlay?.setImageResource(R.drawable.ic_exam_play)
                }
            }

            mediaPlayer.setOnCompletionListener {
                imageViewPlay?.setImageResource(R.drawable.ic_exam_play)
                mediaPlayer.pause()
            }


        }
        else{
            constraintLayoutSeek?.visibility = View.GONE
            buttonParent?.isEnabled = false
            buttonStaff?.isEnabled = false
            buttonGroup?.isEnabled = false
            buttonPublicGroup?.isEnabled = false
            buttonPTA?.isEnabled = false
            buttonConveyor?.isEnabled = false
            buttonClassDivision?.isEnabled = false
            buttonPublicGroup?.isEnabled = false
            buttonGroupWise?.isEnabled = false
            buttonClassWise?.isEnabled = false
            buttonAllParents?.isEnabled = false
            buttonAllStaff?.isEnabled = false
            buttonAllConveyor?.isEnabled = false
            buttonAllPta?.isEnabled = false


            textViewDesc?.text = "Yet to Approve"
            textViewDesc?.setTextColor(resources.getColor(R.color.red_300))

        }

//        textViewDesc?.text = messageList.mESSAGECONTENT
//        textViewDesc?.apply {
//            setShowingLine(8)
//            setShowMoreTextColor(resources.getColor(R.color.green_300))
//            setShowLessTextColor(resources.getColor(R.color.green_300))
//        }


        buttonParent?.setOnClickListener {
            val dialog1 = SendToParentDialog(voiceMessageListModel,voiceMessageTabClicker,"SendVoice/SendMessageToParents")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToParentDialog.TAG)
        }
//
        buttonStaff?.setOnClickListener {
            val dialog1 = SendToStaffDialog(voiceMessageListModel,voiceMessageTabClicker,"StaffVoice/SendMessageToStaffs")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToStaffDialog.TAG)
        }
//
        buttonGroup?.setOnClickListener {
            val dialog1 = SendToGroupDialog(voiceMessageListModel,voiceMessageTabClicker,"GroupVoice/SendMessageToGroup")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToGroupDialog.TAG)
        }
//
        buttonPublicGroup?.setOnClickListener {
            val dialog1 = SendToPublicGroupDialog(voiceMessageListModel,voiceMessageTabClicker,"PublicGroupVoice/SendMessageToPublicGroup")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToPublicGroupDialog.TAG)
        }

        buttonPTA?.setOnClickListener {
            val dialog1 = SendToPTADialog(voiceMessageListModel,voiceMessageTabClicker,"PtaVoice/SendMessageToPta")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToPTADialog.TAG)
        }
//
        buttonConveyor?.setOnClickListener {
            val dialog1 = SendToConveyorDialog(voiceMessageListModel,voiceMessageTabClicker,"ConveyorVoice/SendMessageToConveyor")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToConveyorDialog.TAG)
        }
//
//
        buttonClassDivision?.setOnClickListener {
            val dialog1 = SendToClassDivisionDialog(voiceMessageListModel,voiceMessageTabClicker,"BulkMessageVoice/SendMessageClassWise")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToClassDivisionDialog.TAG)
        }
//
//
        buttonClassWise?.setOnClickListener {
            val dialog1 = SendToClassWiseDialog(voiceMessageListModel,voiceMessageTabClicker,"BulkMessageVoice/SendClassSectionWise")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToClassWiseDialog.TAG)
        }
//
        buttonGroupWise?.setOnClickListener {
            val dialog1 = SendToGroupWiseDialog(voiceMessageListModel,voiceMessageTabClicker,"GroupWiseVoice/MessageToGroupWise")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToGroupWiseDialog.TAG)
        }
//
        buttonPublicGroupWise?.setOnClickListener {
            val dialog1 = SendToPublicGroupWiseDialog(voiceMessageListModel,voiceMessageTabClicker,"PublicGroupVoice/MessageToPublicGroupWise")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToPublicGroupWiseDialog.TAG)
        }
//
        buttonAllParents?.setOnClickListener {
            val paramsMap: HashMap<String?, Int> = LinkedHashMap()
            paramsMap["MessageId"] = voiceMessageListModel.vOICEMAILID
            paramsMap["AdminId"] = adminId
            sendFunction("Are you Sure want to send All Parents?","AllParentsUnicode/MessageToAllParents",paramsMap)
        }

        buttonAllStaff?.setOnClickListener {
            val paramsMap: HashMap<String?, Int> = LinkedHashMap()
            paramsMap["MessageId"] = voiceMessageListModel.vOICEMAILID
            paramsMap["AdminId"] = adminId
            sendFunction("Are you Sure want to send All Staff?","AllStaffsUnicode/MessageToAllStaffs",paramsMap)
        }

        buttonAllConveyor?.setOnClickListener {
            val paramsMap: HashMap<String?, Int> = LinkedHashMap()
            paramsMap["MessageId"] = voiceMessageListModel.vOICEMAILID
            paramsMap["AdminId"] = adminId
            sendFunction("Are you Sure want to send All Conveyor?","ConveyorUnicode/MessageToAllConveyors",paramsMap)
        }

        buttonAllPta?.setOnClickListener {
            val paramsMap: HashMap<String?, Int> = LinkedHashMap()
            paramsMap["MessageId"] = voiceMessageListModel.vOICEMAILID
            paramsMap["AdminId"] = adminId
            sendFunction("Are you Sure want to send All PTA?","PtaUnicode/MessageToAllPta",paramsMap)
        }



        binding.imageViewClose.setOnClickListener {
            if (mediaPlayer != null) {
                mediaPlayer.pause()
            }
            dialog?.dismiss()
        }

        binding.textDeleteIcon.setOnClickListener {
            //Voice/VoiceDropById?VoiceMailId=
            // + feedlist.get(position).get("CLASS_ID");
            voiceMessageTabClicker.onDeleteClickListener("Voice/VoiceDropById",voiceMessageListModel.vOICEMAILID)
        }

//
//        binding.buttonSubmit.setOnClickListener {
//            if(binding.editTextTitle.text.toString().isNotEmpty() &&
//                binding.editDescription.text.toString().isNotEmpty()){
//                val url = "Teacher/AlbumCategoryEdit"
//                    val jsonObject = JSONObject()
//                    try {
//                        jsonObject.put("ALBUM_CATEGORY_NAME",binding.editTextTitle.text.toString())
//                        jsonObject.put("ALBUM_CATEGORY_DISCRIPTION", binding.editDescription.text.toString())
//                        jsonObject.put("ALBUM_CATEGORY_TYPE", albumCategory.aLBUMCATEGORYTYPE)
//                        jsonObject.put("ACCADEMIC_ID", albumCategory.aCCADEMICID)
//                        jsonObject.put("ALBUM_CATEGORY_CREATED", albumCategory.aLBUMCATEGORYCREATED)
//                        jsonObject.put("ALBUM_CATEGORY_ID", albumCategory.aLBUMCATEGORYID)
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                    Log.i(TAG,"jsonObject $jsonObject")
//
//                    val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
//                    albumListener.onUpdateClick(url,submitItems,
//                        "Album Updated Successfully","Album Updation Failed")
//            }else{
//                albumListener.onShowMessage("Don't leave fields empty")
//            }
//        }
//

    }

    fun sendFunction(message : String,url : String,paramsMap : HashMap<String?, Int>){
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                //AllStaffs/MessageToAllStaffs?MessageId=
                voiceMessageTabClicker.sendToAllParentStaffPTAConveyor(url,paramsMap)
            }
            .setNegativeButton(
                "No"
            ) { dialog, _ -> //  Action for 'NO' Button
                dialog.cancel()
            }
        //Creating dialog box
        val alert = builder.create()
        //Setting the title manually
        alert.setTitle("Send Message")
        alert.show()
        val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonbackground.setTextColor(Color.BLACK)
        val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonbackground1.setTextColor(Color.BLACK)
    }





    companion object {
        var TAG = "BottomSheetFragment"
    }
}