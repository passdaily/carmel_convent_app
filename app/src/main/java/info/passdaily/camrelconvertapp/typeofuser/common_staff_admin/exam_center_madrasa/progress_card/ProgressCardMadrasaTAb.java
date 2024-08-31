package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center_madrasa.progress_card;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.passdaily.camrelconvertapp.R;
import info.passdaily.camrelconvertapp.services.CustomRequest;
import info.passdaily.camrelconvertapp.services.FileUtils;
import info.passdaily.camrelconvertapp.services.Global;
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.progress_card.BottomSheetProgressCard;
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.progress_card.SampleObject;
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.progress_card.SampleObject1;


@SuppressLint("ValidFragment")
public class ProgressCardMadrasaTAb extends Fragment{

    String year,exam,sclass,s_cname,s_ename;

    int aCCADEMICID,cLASSID,eXAMID,adminId;
    String cLASSNAME;

    public ProgressCardMadrasaTAb(){}


    public ProgressCardMadrasaTAb(int aCCADEMICID, int cLASSID, int eXAMID, int adminId, String cLASSNAME){
        this.aCCADEMICID = aCCADEMICID;
        this.cLASSID = cLASSID;
        this.eXAMID = eXAMID;
        this.adminId = adminId;
        this.cLASSNAME = cLASSNAME;

    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater,ViewGroup viewGroup,Bundle
            saveInstanceState){

        this.aCCADEMICID = Global.Companion.getACCADEMICID();
        this.cLASSID =  Global.Companion.getCLASSID();
        this.eXAMID =  Global.Companion.getEXAMID();
        this.adminId = Global.Companion.getAdminId();
        this.cLASSNAME = Global.Companion.getCLASSNAME();

        ProgressCardTableRow view=new ProgressCardTableRow(getActivity(),year,exam,sclass);
        view.setBackgroundColor(Color.WHITE);
        return view;
    }


    public class ProgressCardTableRow extends RelativeLayout{

        String TAG = "Tablelayout";
        public RequestQueue mRequestQueue;
//        String GET_year_subject_class = Global.url+"Mark/MarkEntryPageLoad?AdminId="+Global.Admin_id;
// http://demostaff.passdaily.in/ElixirApi/Mark/MarkEntryPageLoad?AdminId=1

        String GET_MarkList = FileUtils.m_url+"Mark/ProgressReportHs?AccademicId=";
///http://demostaff.passdaily.in/ElixirApi/Mark/ProgressReportHs?AccademicId=6&ClassId=5&ExamId=1&AdminId=1&Dummy=0


        ArrayList <HashMap <String, String>> studentname, subjectlist, markList, stafflist;

        String year, exam, sclass;

        String header[] = {};
        String maxMark[] = {" MAX MARKS ","","","","","","","",""};
        String passMark[] = {" PASS MARKS ","","","","","","","",""};
        String subWisePass[] = {" SUBJECT WISE PASS ","","","","","","","",""};
        String subPassPercentage[] = {" Subject Wise % ","","","","","","","",""};
        String dummayrow[] = {" STUDENTS"};


        TableLayout tableA;
        TableLayout tableB;
        TableLayout tableC;
        TableLayout tableD;

        TableLayout tableMG;

        HorizontalScrollView horizontalScrollViewB;
        HorizontalScrollView horizontalScrollViewD;

        HorizontalScrollView horizontalScrollViewMG;

        ScrollView scrollViewC;
        ScrollView scrollViewD;

        Context context;
        List <SampleObject> sampleObjects;
        List <SampleObject1> sampleObjects1 = new ArrayList <SampleObject1>();
        // int HeaderCellWidth[] = new int[header.length];
        int[] HeaderCellWidth;


        private List <SampleObject> sampleObjects(){

            List <SampleObject> sampleObjects = new ArrayList <SampleObject>();

            int flcnt = 0;
            int passcnt = 0;
            String status = "FP";
            int tot = 0;
            int outoffmark=0;
            int nosub = 0;
            int a1 = 0;
            int a2 = 0;
            int i = 0;

            String subj="";
            for (int y = 0; y<subjectlist.size(); y++) {
                subj+=subjectlist.get(y).get("SUBJECT_NAME")+"~";
            }

            String[] subjt=subj.split("~");

            for (int x = 0; x<studentname.size(); x++) {
                StringBuilder var= new StringBuilder();
                String row1 = "-", row2 = "-", row3 = "-", row4 = "-", row5 = "-", row6 = "-", row7 = "-";
                flcnt = 0;
                passcnt = 0;
                a1 = 0;
                a2 = 0;
                nosub = 0;
                tot = 0;
                outoffmark=0;
                // var=studentname.get(x).get("STUDENT_FNAME")+"~";
                for (int y = 0; y<subjectlist.size(); y++) {

                    String temp="-~";
                    for (int z = 0; z<markList.size(); z++) {

                        if (markList.get(z).get("TOTAL_MARK").equals("-1")){
                            markList.get(z).put("TOTAL_MARK","AB");
                        }
                        if (studentname.get(x).get("STUDENT_ROLL_NUMBER").equals(markList.get(z).get("STUDENT_ROLL_NUMBER"))){

                            if (subjectlist.get(y).get("SUBJECT_ID").equals(markList.get(z).get("SUBJECT_ID"))){

                                String n = subjectlist.get(y).get("SUBJECT_NAME");

                                for(int c=0;c<subjt.length;c++){
                                    if(subjt[c].equals(n)){

                                        int d=c+1;

                                        if (markList.get(z).get("TOTAL_MARK").contains("AB")){
                                            temp= markList.get(z).get("TOTAL_MARK")+"~";
                                            flcnt++;
                                            int outmark = Integer.parseInt(markList.get(z).get("OUTOFF_MARK"));
                                            outoffmark+=outmark;
                                        } else{
                                            temp= markList.get(z).get("TOTAL_MARK")+" | "+markList.get(z).get("MARK_GRADE")+"~";
                                            int passmrk = Integer.parseInt(subjectlist.get(y).get("PASS_MARK"));
                                            int mark = Integer.parseInt(markList.get(z).get("TOTAL_MARK"));
                                            int outmark = Integer.parseInt(markList.get(z).get("OUTOFF_MARK"));
                                            outoffmark+=outmark;
                                            tot += mark;
                                            if (mark >= passmrk){ // old code
                                                //    if (mark > passmrk){
                                                passcnt++;
                                            } else{
                                                flcnt++;
                                            }

                                            if (markList.get(z).get("MARK_GRADE").contains("A")){
                                                a1++;
                                            } else if (markList.get(z).get("MARK_GRADE").contains("B")){
                                                a2++;
                                            }
                                        }



                                    }

                                }

                                //   Log.i(TAG,"Print--"+temp);
                            }

                        }
                    }
                    var.append(temp);
                    temp="";


                }

                if (flcnt>0){
                    status = "FL";
                } else{
                    status = "FP";
                }
                ////old code
                //  int totper=tot/nosub;

                nosub = subjectlist.size();

//////////////////new corrected
                float outc11 = Float.parseFloat(String.valueOf(tot))/ Float.parseFloat(String.valueOf(outoffmark));
                DecimalFormat df = new DecimalFormat("#.#");
                //   df.setRoundingMode(RoundingMode.DOWN);
                // Log.i(TAG,"salary : " + df.format(outc1));

                float v= (Float.parseFloat(String.valueOf(outc11))*100);
                int rr_final = (int)Math.round(v);
                //  String per = String.valueOf((int)v);


                Log.i(TAG,"rr_final : " + rr_final);
//// old code
                float outc1 = Float.parseFloat(String.valueOf(tot)) / Float.parseFloat(String.valueOf(nosub));
                @SuppressLint("DefaultLocale") String s = String.format("%.1f",outc1);
                int result1 = (int)Math.round(Float.parseFloat(s));

                //    Log.i(TAG,"outoffmark "+outoffmark);

                //        Log.i(TAG,"tot "+ Float.parseFloat(String.valueOf(tot)) +" nosub "+nosub +" total "+result1);

                SampleObject sampleObject = new SampleObject(
                        studentname.get(x).get("STUDENT_FNAME"),
                        studentname.get(x).get("STUDENT_GUARDIAN_NAME"),
                        studentname.get(x).get("STUDENT_GUARDIAN_NUMBER"),
                        row2,
                        row3,
                        row4,
                        row5,
                        row6

                );


                sampleObjects.add(sampleObject);

                SampleObject1 sampleObject1 = new SampleObject1(
                        String.valueOf(flcnt),
                        String.valueOf(passcnt),
                        String.valueOf(status),
                        String.valueOf(rr_final),
                        String.valueOf(a1),
                        String.valueOf(a2),
                        String.valueOf(tot)

                );
                //     Log.i(TAG,"totper "+totper);
                sampleObjects1.add(sampleObject1);



                String[] addExtraString = {
                        String.valueOf(flcnt),
                        String.valueOf(passcnt),
                        status,
                        String.valueOf(tot),
                        df.format(v)+"%",
                        String.valueOf(a1),
                        String.valueOf(a2)

                };

                String[] addExtra_String = {
                        String.valueOf(flcnt),
                        String.valueOf(passcnt),
                        status,
                        String.valueOf(tot),
                        df.format(v)+"%",
                        "No.of A : "+a1 +", No.of B : "+a2+",No.of Pass : "+passcnt+", No.of Fail : "+flcnt,
//                        String.valueOf(a1),
//                        String.valueOf(a2),
                        String.valueOf(outoffmark)

                };

                TableRow taleRowForTableD = new TableRow(this.context);
                String[] info = var.toString().split("~");

                List <String> array3 = new ArrayList <>();
                Collections.addAll(array3,info);

                //    Collections.addAll(array3,header);
                Collections.addAll(array3,addExtraString);

                //for(int x=0 ; x<loopCount; x++){
                for (int l = 0; l<(array3.size()); l++) {
                    //    TableRow.LayoutParams params = new TableRow.LayoutParams( HeaderCellWidth[x+1],LayoutParams.MATCH_PARENT);
                    TableRow.LayoutParams params = new TableRow.LayoutParams(220, LayoutParams.MATCH_PARENT);
                    params.setMargins(2,2,2,0);

                    // TextView textViewB = this.bodyTextView(info[x]);
                    TextView textViewB = this.bodyTextView(array3.get(l));
                    Typeface typeface = ResourcesCompat.getFont(context, R.font.poppins_medium);
                    textViewB.setTypeface(typeface);
                    textViewB.setTextSize(COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_02));
                    textViewB.setTextColor(getResources().getColor(R.color.gray_600));
                    textViewB.setBackgroundResource(R.drawable.cellshapes_two);
                    taleRowForTableD.addView(textViewB,params);
                }
                taleRowForTableD.setOnClickListener(new OnClickListener(){
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View v){
                        List<String> array4 = new ArrayList<>();
//                        Collections.addAll(array4, info);
                        List<String> array5 = new ArrayList<>();
                        for (int i = 0; i < subjectlist.size(); i++) {
                            Collections.addAll(array5, subjectlist.get(i).get("SUBJECT_NAME"));
                        }
                        //Log.i(TAG, "array4 " + array4);
                        Log.i(TAG, "array5 " + array5);

                        String staffFname = "";

                        if(!stafflist.isEmpty()){
                            staffFname = stafflist.get(0).get("STAFF_FNAME");
                        }

                        BottomSheetProgressCard bottomSheet =
                                new BottomSheetProgressCard(addExtra_String,sampleObject,
                                        cLASSNAME,
                                        staffFname, info,subjectlist,"other");
                        bottomSheet.show(requireActivity().getSupportFragmentManager(), "TAG");
                    }
                });
                this.tableD.addView(taleRowForTableD);

            }
            return sampleObjects;

        }

        public ProgressCardTableRow(Context context,String year,String exam,String sclass){
            super(context);
            this.context = context;
            this.year = year;
            this.exam = exam;
            this.sclass = sclass;
            GET_MarkList = FileUtils.m_url+"Mark/ProgressReportHs?AccademicId="+aCCADEMICID+
                    "&ClassId="+cLASSID+"&ExamId="+eXAMID+"&AdminId="+adminId+"&Dummy=0";
            this.RunGetFunMarkList(GET_MarkList,context);

            //  this.setBackgroundColor(Color.RED);

        }

        private void RunGetFunMarkList(String url,Context context){


            Log.i(TAG,"GET_URL "+url);

            studentname = new ArrayList <HashMap <String, String>>();
            subjectlist = new ArrayList <HashMap <String, String>>();
            markList = new ArrayList <HashMap <String, String>>();
            stafflist = new ArrayList <HashMap <String, String>>();

            CustomRequest customerRequest = new CustomRequest(Request.Method.GET,url,null,
                    new Response.Listener <JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response){
///"StudentList":[{"STUDENT_ID":67,"ADMISSION_NUMBER":"839","STUDENT_ROLL_NUMBER":1,
// "STUDENT_FNAME":"AADINATH P","CLASS_ID":5,"ACCADEMIC_ID":6}
                            try {
                                JSONArray jsonArray1 = response.getJSONArray("StudentList");
                                for (int i1 = 0; i1<jsonArray1.length(); i1++) {
                                    JSONObject STUDENT_ID = jsonArray1.getJSONObject(i1);

                                    HashMap <String, String> map1 = new HashMap <String, String>();
                                    map1.put("STUDENT_ID",STUDENT_ID.getString("STUDENT_ID"));
                                    map1.put("ADMISSION_NUMBER",STUDENT_ID.getString("ADMISSION_NUMBER"));
                                    map1.put("STUDENT_ROLL_NUMBER",STUDENT_ID.getString("STUDENT_ROLL_NUMBER"));
                                    map1.put("STUDENT_FNAME",STUDENT_ID.getString("STUDENT_FNAME"));
                                    map1.put("CLASS_ID",STUDENT_ID.getString("CLASS_ID"));
                                    map1.put("ACCADEMIC_ID",STUDENT_ID.getString("ACCADEMIC_ID"));
                                    map1.put("STUDENT_GUARDIAN_NAME",STUDENT_ID.getString("STUDENT_GUARDIAN_NAME"));
                                    map1.put("STUDENT_GUARDIAN_NUMBER",STUDENT_ID.getString("STUDENT_GUARDIAN_NUMBER"));
                                    studentname.add(map1);
                                }
///"SubjectList":[{"SUBJECT_ID":1,"SUBJECT_NAME":"ENGLISH","PASS_MARK":30,
// "OUTOFF_MARK":100,"SUBJECTWISE_PASS":23,"TOTAL_ATTEND":25}
                                String head = " SUBJECTS~";
                                String maxM = " MAX MARKS~";
                                String passM = " PASS MARKS~";
                                String subWsP = " Subjects Pass~";
                                String subWsPassPer = "";
                                JSONArray jsonArray2 = response.getJSONArray("SubjectList");
                                for (int i2 = 0; i2<jsonArray2.length(); i2++) {
                                    JSONObject SUBJECT_ID = jsonArray2.getJSONObject(i2);
                                    HashMap <String, String> map2 = new HashMap <String, String>();
                                    map2.put("SUBJECT_ID",SUBJECT_ID.getString("SUBJECT_ID"));
                                    map2.put("SUBJECT_NAME",SUBJECT_ID.getString("SUBJECT_NAME"));
                                    head += SUBJECT_ID.getString("SUBJECT_NAME")+"~";
                                    map2.put("PASS_MARK",SUBJECT_ID.getString("PASS_MARK"));
                                    passM += SUBJECT_ID.getString("PASS_MARK")+"~";
                                    map2.put("OUTOFF_MARK",SUBJECT_ID.getString("OUTOFF_MARK"));
                                    maxM += SUBJECT_ID.getString("OUTOFF_MARK")+"~";
                                    map2.put("SUBJECTWISE_PASS",SUBJECT_ID.getString("SUBJECTWISE_PASS"));
                                    subWsP += SUBJECT_ID.getString("SUBJECTWISE_PASS")+"~";
                                    map2.put("TOTAL_ATTEND",SUBJECT_ID.getString("TOTAL_ATTEND"));
                                    subWsPassPer += SUBJECT_ID.getString("TOTAL_ATTEND")+"~";
                                    subjectlist.add(map2);
                                }
                                header = head.split("~");
                                maxMark = maxM.split("~");
                                passMark = passM.split("~");
                                subWisePass = subWsP.split("~");
                                subPassPercentage = subWsPassPer.split("~");
                                HeaderCellWidth = new int[header.length];
                                //     Log.i(TAG,"inside_subjectlist "+ subjectlist.size());

///"MarkList":[{"MARK_ID":12,"ACCADEMIC_ID":6,"CLASS_ID":5,"EXAM_ID":1,"SUBJECT_ID":13,"STUDENT_ID":67,
// "STUDENT_ROLL_NUMBER":1,"PASS_MARK":30,"OUTOFF_MARK":100,"TOTAL_MARK":66,"MARK_DATE":"2019-08-22T12:00:55.483",
// "MARKED_BY":1,"MARK_GRADE":"B","PASS_MARK_CE":null,"OUTOFF_MARK_CE":null,"TOTAL_MARK_CE":null}
                                JSONArray jsonArray3 = response.getJSONArray("MarkList");
                                for (int i3 = 0; i3<jsonArray3.length(); i3++) {
                                    JSONObject MARK_ID = jsonArray3.getJSONObject(i3);
                                    HashMap <String, String> map3 = new HashMap <String, String>();
                                    map3.put("MARK_ID",MARK_ID.getString("MARK_ID"));
                                    map3.put("ACCADEMIC_ID",MARK_ID.getString("ACCADEMIC_ID"));
                                    map3.put("CLASS_ID",MARK_ID.getString("CLASS_ID"));
                                    map3.put("SUBJECT_ID",MARK_ID.getString("SUBJECT_ID"));
                                    map3.put("EXAM_ID",MARK_ID.getString("EXAM_ID"));
                                    map3.put("STUDENT_ID",MARK_ID.getString("STUDENT_ID"));
                                    map3.put("STUDENT_ROLL_NUMBER",MARK_ID.getString("STUDENT_ROLL_NUMBER"));
                                    map3.put("PASS_MARK",MARK_ID.getString("PASS_MARK"));
                                    map3.put("OUTOFF_MARK",MARK_ID.getString("OUTOFF_MARK"));
                                    map3.put("TOTAL_MARK",MARK_ID.getString("TOTAL_MARK"));
                                    map3.put("MARK_DATE",MARK_ID.getString("MARK_DATE"));
                                    map3.put("MARKED_BY",MARK_ID.getString("MARKED_BY"));
                                    map3.put("MARK_GRADE",MARK_ID.getString("MARK_GRADE"));
                                    map3.put("PASS_MARK_CE",MARK_ID.getString("PASS_MARK_CE"));
                                    map3.put("OUTOFF_MARK_CE",MARK_ID.getString("OUTOFF_MARK_CE"));
                                    map3.put("TOTAL_MARK_CE",MARK_ID.getString("TOTAL_MARK_CE"));
                                    markList.add(map3);
                                }
                                //"StaffList":[{"STAFF_ID":9,"STAFF_FNAME":"PRAVITHA ANEEESH","STAFF_MNAME":"","STAFF_LNAME":"","STAFF_PHONE_NUMBER":"9"}]}
                                JSONArray jsonArray4 = response.getJSONArray("StaffList");
                                for (int i4 = 0; i4<jsonArray4.length(); i4++) {
                                    JSONObject STAFF_ID = jsonArray4.getJSONObject(i4);
                                    HashMap <String, String> map4 = new HashMap <String, String>();
                                    map4.put("STAFF_ID",STAFF_ID.getString("STAFF_ID"));
                                    map4.put("STAFF_FNAME",STAFF_ID.getString("STAFF_FNAME"));
                                    map4.put("STAFF_MNAME",STAFF_ID.getString("STAFF_MNAME"));
                                    map4.put("STAFF_LNAME",STAFF_ID.getString("STAFF_LNAME"));
                                    map4.put("STAFF_PHONE_NUMBER",STAFF_ID.getString("STAFF_PHONE_NUMBER"));
                                    stafflist.add(map4);
                                }

                                newTable_init();
                            } catch (JSONException e) {
                                Toast.makeText(context,"Something went wrong try after sometime.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error){
                    Toast.makeText(context,"Something went wrong try after sometime.",Toast.LENGTH_SHORT).show();
                }
            }){
                protected Map <String, String> getParams(){

                    Map <String, String> parames = new HashMap <>();
                    return parames;

                }

            };
            addtoRequestQueue(customerRequest);
        }

        public <T> void addtoRequestQueue(Request <T> Req){
            Req.setTag(TAG);
            Req.setRetryPolicy(new DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            getRequestQueue().add(Req);
        }

        public RequestQueue getRequestQueue(){

            if (mRequestQueue == null){
                mRequestQueue = Volley.newRequestQueue(this.context);
            }
            return mRequestQueue;
        }


        //Table Data Feed
        public void newTable_init(){

            this.initComponents();
            sampleObjects = this.sampleObjects();

            this.setComponentsId();
            this.setScrollViewAndHorizontalScrollViewTag();

            this.horizontalScrollViewB.addView(this.tableB);
            this.scrollViewC.addView(this.tableC);
            this.scrollViewD.addView(this.horizontalScrollViewD);
            this.horizontalScrollViewD.addView(this.tableD);

            this.addComponentToMainLayout();

            this.addTableRowToTableA();
            this.addTableRowToTableB();


            this.resizeHeaderHeight();
            this.getTableRowHeaderCellWidth();

            this.generateTableC_AndTable_B();

            this.resizeBodyTableRowHeight();

        }

        /////initial Components list
        private void initComponents(){

            this.tableA = new TableLayout(this.context);
            this.tableB = new TableLayout(this.context);
            this.tableC = new TableLayout(this.context);
            this.tableD = new TableLayout(this.context);
            this.horizontalScrollViewB = new MyHorizontalScrollView(this.context);
            this.horizontalScrollViewD = new MyHorizontalScrollView(this.context);
            this.scrollViewC = new MyScrollView(this.context);
            this.scrollViewD = new MyScrollView(this.context);

            //  this.tableA.setBackgroundColor(Color.GREEN);
            //       this.horizontalScrollViewB.setBackgroundColor(Color.LTGRAY);

        }


        ///named scrollview and Horizontal Tags
        private void setScrollViewAndHorizontalScrollViewTag(){

            this.horizontalScrollViewB.setTag("horizontal scroll view b");
            this.horizontalScrollViewD.setTag("horizontal scroll view d");

            this.scrollViewC.setTag("scroll view c");
            this.scrollViewD.setTag("scroll view d");

        }

        ////set id for tables
        @SuppressLint("ResourceType")
        private void setComponentsId(){
            this.tableA.setId(1);
            this.horizontalScrollViewB.setId(2);
            this.scrollViewC.setId(3);
            this.scrollViewD.setId(4);

        }

        private void addComponentToMainLayout(){


            LayoutParams componentB_Params = new LayoutParams
                    (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            componentB_Params.addRule(RelativeLayout.RIGHT_OF,this.tableA.getId());

            LayoutParams componentC_Params = new LayoutParams
                    (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            componentC_Params.addRule(RelativeLayout.BELOW,this.tableA.getId());

            LayoutParams componentD_Params = new LayoutParams
                    (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            componentD_Params.addRule(RelativeLayout.RIGHT_OF,this.scrollViewC.getId());
            componentD_Params.addRule(RelativeLayout.BELOW,this.horizontalScrollViewB.getId());

            this.addView(this.tableA);
            this.addView(this.horizontalScrollViewB,componentB_Params);
            this.addView(this.scrollViewC,componentC_Params);
            this.addView(this.scrollViewD,componentD_Params);

        }


        private void addTableRowToTableA(){
            //   this.tableA.addView(this.componentA_Title_TableRow());
            this.tableA.addView(this.componentATableRow());
            //  this.tableA.addView(this.componentDividerA());
            this.tableA.addView(this.componentA_MaxMark_TableRow());
            this.tableA.addView(this.componentA_PassMark_TableRow());
            this.tableA.addView(this.componentA_SubWisePass_TableRow());
            this.tableA.addView(this.componentA_SubWisePassPercentage_TableRow());
            this.tableA.addView(this.componentA_DummyRow_TableRow());


        }


        private void addTableRowToTableB(){
            this.tableB.addView(this.componentBTableRow());
            this.tableB.addView(this.componentB_MaxMark_TableRow());
            this.tableB.addView(this.componentB_PassMark_TableRow());

            this.tableB.addView(this.componentB_SubWisePass_TableRow());
            this.tableB.addView(this.componentB_SubWisePassPercentage_TableRow());
            this.tableB.addView(this.componentB_DummyRow_TableRow());
        }




        ///// row 1 header
        TableRow componentATableRow(){

            TableRow componentATabele = new TableRow(this.context);
            TableRow.LayoutParams params = new TableRow.LayoutParams(330,LayoutParams.MATCH_PARENT);
            params.setMargins(2,0,0,0);
            TextView textView = this.headerTextView(this.header[0]);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.poppins_bold);
            textView.setTypeface(typeface);
            textView.setTextSize(COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_02));
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setBackgroundResource(R.drawable.cellshapes);
            textView.setLayoutParams(params);
            componentATabele.setBackgroundColor(getResources().getColor(R.color.gray_600));
            componentATabele.addView(textView);

//        //New Cell
//        LinearLayout cell = new LinearLayout(this.context);
//        cell.setBackgroundColor(Color.DKGRAY);
//        cell.setLayoutParams(params);

            return componentATabele;
        }

        /////row 2 Header
        TableRow componentA_MaxMark_TableRow(){
            TableRow componentATabele = new TableRow(this.context);
            TableRow.LayoutParams params = new TableRow.LayoutParams(330,LayoutParams.MATCH_PARENT);
            params.setMargins(2,0,0,0);
            TextView textView = this.headerTextView(this.maxMark[0]);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.poppins_bold);
            textView.setTypeface(typeface);
            textView.setTextSize(COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_02));
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setBackgroundResource(R.drawable.cellshapes);
            textView.setLayoutParams(params);
            //   componentATabele.setBackgroundColor(getResources().getColor(R.color.color_light));
            componentATabele.addView(textView);

            return componentATabele;
        }

        //// row 3 Header
        TableRow componentA_PassMark_TableRow(){
            TableRow componentATabele = new TableRow(this.context);
            TableRow.LayoutParams params = new TableRow.LayoutParams(330,LayoutParams.MATCH_PARENT);
            params.setMargins(2,0,0,0);
            TextView textView = this.headerTextView(this.passMark[0]);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.poppins_bold);
            textView.setTypeface(typeface);
            textView.setTextSize(COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_02));
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setBackgroundResource(R.drawable.cellshapes);
            textView.setLayoutParams(params);
            //   componentATabele.setBackgroundColor(getResources().getColor(R.color.color_light));
            componentATabele.addView(textView);

            return componentATabele;
        }

        //// row 4 Header
        TableRow componentA_SubWisePass_TableRow(){
            TableRow componentATabele = new TableRow(this.context);
            TableRow.LayoutParams params = new TableRow.LayoutParams(330,LayoutParams.MATCH_PARENT);
            params.setMargins(2,0,0,0);
            TextView textView = this.headerTextView("SUB PASS");
            Typeface typeface = ResourcesCompat.getFont(context, R.font.poppins_bold);
            textView.setTypeface(typeface);
            textView.setTextSize(COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_02));
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setBackgroundResource(R.drawable.cellshapes);
            textView.setLayoutParams(params);
            componentATabele.addView(textView);
            //   componentATabele.setBackgroundColor(getResources().getColor(R.color.color_light));
            return componentATabele;
        }

        //// row 5 Header
        TableRow componentA_SubWisePassPercentage_TableRow(){
            TableRow componentATabele = new TableRow(this.context);
            TableRow.LayoutParams params = new TableRow.LayoutParams(330,LayoutParams.MATCH_PARENT);
            params.setMargins(2,0,0,0);
            TextView textView = this.headerTextView(" S.P.P");
            Typeface typeface = ResourcesCompat.getFont(context, R.font.poppins_bold);
            textView.setTypeface(typeface);
            textView.setTextSize(COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_02));
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setBackgroundResource(R.drawable.cellshapes);
            textView.setLayoutParams(params);
            //  componentATabele.setBackgroundColor(getResources().getColor(R.color.color_light));
            componentATabele.addView(textView);
            return componentATabele;
        }

        //// row 6 Header
        TableRow componentA_DummyRow_TableRow(){
            TableRow componentATabele = new TableRow(this.context);
            TableRow.LayoutParams params = new TableRow.LayoutParams(330,LayoutParams.MATCH_PARENT);
            params.setMargins(2,0,0,0);
            TextView textView = this.headerTextView(this.dummayrow[0]);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.poppins_bold);
            textView.setTypeface(typeface);
            textView.setTextSize(COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_02));
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setLayoutParams(params);
            textView.setBackgroundResource(R.drawable.cellshapes);
            //  componentATabele.setBackgroundColor(getResources().getColor(R.color.color_light));
            componentATabele.addView(textView);
            return componentATabele;
        }

        /////row 1 fields
        TableRow componentBTableRow(){

            TableRow componentBTableRow = new TableRow(this.context);
            int headerFieldCount = this.header.length;
            String[] addExtraString = {" "," "," "," "," "," "," "};
            TableRow.LayoutParams params = new TableRow.LayoutParams(220,LayoutParams.MATCH_PARENT);
            params.setMargins(2,2,2,0);

//        Log.i(TAG,"subjectlist.size() "+subjectlist.size() );
//        for(int i=0;i<subjectlist.size();i++){
//            Collections.addAll(array3,subjectlist.get(i).get("SUBJECT_NAME").substring(0,3));
//            //this.header[i+1].substring(0,3)
//        //    Collections.addAll(array3,this.header[i+1].substring(0,3));
//         //   Log.i(TAG,"header "+subjectlist.get(i).get("SUBJECT_NAME").substring(0,3));
//        }
//        Collections.addAll(array3,addExtraString);
//
//        Log.i(TAG,"size "+array3.size());
//        for(int i=0 ;i<array3.size();i++) {
//          //  Log.i(TAG,"new "+array3.get(i));
//            TextView textView =this.headerTextView(array3.get(i));
//            textView.setTypeface(textView.getTypeface(),Typeface.BOLD);
//            textView.setLayoutParams(params);
//            componentBTableRow.addView(textView);
//        }
//            List <String> array = new ArrayList <>();
//
//            Collections.addAll(array,header);
//
//            Collections.addAll(array,addExtraString);

            //may be want to changes here
            for (int i = 0; i<(headerFieldCount-1); i++) {
                //  TextView textView =this.headerTextView(array3.get(i+1).substring(0,3));
                TextView textView = this.headerTextView(this.header[i+1].substring(0,3));//.substring(0,3)
                Typeface typeface = ResourcesCompat.getFont(context, R.font.poppins_medium);
                textView.setTypeface(typeface);
                textView.setTextSize(COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_02));
                textView.setTextColor(getResources().getColor(R.color.gray_600));
                textView.setLayoutParams(params);
                textView.setBackgroundResource(R.drawable.cellshapes);
                textView.setTextColor(Color.BLACK);

                componentBTableRow.addView(textView);
            }

//        LinearLayout cell = new LinearLayout(this.context);
//        cell.setBackgroundColor(Color.BLACK);
//        cell.setLayoutParams(params);
            componentBTableRow.setBackgroundColor(getResources().getColor(R.color.green_100));
            return componentBTableRow;
        }

        /////row 2 fields
        TableRow componentB_MaxMark_TableRow(){
            TableRow componentBTableRow = new TableRow(this.context);
            // int headerFieldCount =this.maxMark.length;

            String[] addExtraString = {" "," "," "," "," "," "," "};
            TableRow.LayoutParams params = new TableRow.LayoutParams(220,LayoutParams.MATCH_PARENT);
            params.setMargins(2,2,2,0);

            List <String> array3 = new ArrayList<>();

            Collections.addAll(array3,maxMark);

            Collections.addAll(array3,addExtraString);

            //may be want to changes here
            //  for(int i=0 ;i<(headerFieldCount-1);i++){
            for (int i = 0; i<(array3.size()-1); i++) {
                //TextView textView =this.headerTextView(this.maxMark[i+1]);
                TextView textView = this.headerTextView(array3.get(i+1));
                Typeface typeface = ResourcesCompat.getFont(context, R.font.poppins_bold);
                textView.setTypeface(typeface);
                textView.setTextSize(COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_02));
                textView.setTextColor(getResources().getColor(R.color.gray_600));
                textView.setLayoutParams(params);
                textView.setBackgroundResource(R.drawable.cellshapes);
                componentBTableRow.addView(textView);
            }
            //     componentBTableRow.setBackgroundColor(getResources().getColor(R.color.color_light));
            return componentBTableRow;
        }

        /////row 3 fields
        TableRow componentB_PassMark_TableRow(){
            TableRow componentBTableRow = new TableRow(this.context);
            //   int headerFieldCount =this.passMark.length;
            String[] addExtraString = {" "," "," "," "," "," "," "};
            TableRow.LayoutParams params = new TableRow.LayoutParams(220,LayoutParams.MATCH_PARENT);
            params.setMargins(2,2,2,0);

            List <String> array3 = new ArrayList <>();

            Collections.addAll(array3,passMark);

            Collections.addAll(array3,addExtraString);

//        TableRow.LayoutParams params = new TableRow.LayoutParams(180,LayoutParams.MATCH_PARENT);
//        params.setMargins(2,0,2,0);


            //may be want to changes here
            for (int i = 0; i<(array3.size()-1); i++) {
                // TextView textView =this.headerTextView(this.passMark[i+1]);
                TextView textView = this.headerTextView(array3.get(i+1));
                Typeface typeface = ResourcesCompat.getFont(context, R.font.poppins_bold);
                textView.setTypeface(typeface);
                textView.setTextSize(COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_02));
                textView.setTextColor(getResources().getColor(R.color.gray_600));
                textView.setLayoutParams(params);
                textView.setBackgroundResource(R.drawable.cellshapes);
                componentBTableRow.addView(textView);
            }
            //  componentBTableRow.setBackgroundColor(getResources().getColor(R.color.color_light));
            return componentBTableRow;
        }

        /////row 4 fields
        TableRow componentB_SubWisePass_TableRow(){
            TableRow componentBTableRow = new TableRow(this.context);
            //      int headerFieldCount =this.subWisePass.length;
            String[] addExtraString = {" "," "," "," "," "," "," "};
            TableRow.LayoutParams params = new TableRow.LayoutParams(220,LayoutParams.MATCH_PARENT);
            params.setMargins(2,2,2,0);

            List <String> array3 = new ArrayList <>();

            Collections.addAll(array3,subWisePass);

            Collections.addAll(array3,addExtraString);

            //may be want to changes here
            for (int i = 0; i<(array3.size()-1); i++) {

                // TextView textView =this.headerTextView(this.subWisePass[i+1]);
                TextView textView = this.headerTextView(array3.get(i+1));
                Typeface typeface = ResourcesCompat.getFont(context, R.font.poppins_bold);
                textView.setTypeface(typeface);
                textView.setTextSize(COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_02));
                textView.setTextColor(getResources().getColor(R.color.gray_600));
                textView.setLayoutParams(params);
                textView.setBackgroundResource(R.drawable.cellshapes);
                componentBTableRow.addView(textView);
            }
            //    componentBTableRow.setBackgroundColor(getResources().getColor(R.color.color_light));
            return componentBTableRow;
        }

        /////row 5 fields
        TableRow componentB_SubWisePassPercentage_TableRow(){
            TableRow componentBTableRow = new TableRow(this.context);
            int headerFieldCount = this.subPassPercentage.length;
            TableRow.LayoutParams params = new TableRow.LayoutParams(220,LayoutParams.MATCH_PARENT);
            params.setMargins(2,2,2,0);

            List <String> array3 = new ArrayList <>();
            String[] addExtraString = {" "," "," "," "," "," "," "};

            //may be want to changes here
            //     Log.i(TAG," size - "+subjectlist.size());
            for (int i = 0; i<subjectlist.size(); i++) {
                DecimalFormat df = new DecimalFormat("#.#");

                float j = Float.parseFloat(subjectlist.get(i).get("SUBJECTWISE_PASS"));
                float k = Float.parseFloat(subjectlist.get(i).get("TOTAL_ATTEND"));
                float x = j / k;

                float v= (Float.parseFloat(String.valueOf(x))*100);
                int rr_final = (int)Math.round(v);

                Log.i(TAG,"swp "+rr_final);

                //old code
                //   x = x * 100;
                //   String per = String.valueOf((int)x);


                //          Log.i(TAG," X- "+x);
                //       Log.i(TAG," xx "+Integer.valueOf(subPassPercentage[i])+" "+Integer.valueOf(subWisePass[i+1]));

             //   Collections.addAll(array3,(rr_final+"%"));
                Collections.addAll(array3,(df.format(v)+"%"));
            }
            Collections.addAll(array3,addExtraString);
            for (int i = 0; i<(array3.size()); i++) {
                TextView textView = this.headerTextView(array3.get(i));
                //        TextView textView =this.headerTextView(String.valueOf(per)+"%");
                Typeface typeface = ResourcesCompat.getFont(context, R.font.poppins_bold);
                textView.setTypeface(typeface);
                textView.setTextSize(COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_02));
                textView.setTextColor(getResources().getColor(R.color.gray_600));
                textView.setLayoutParams(params);
                textView.setBackgroundResource(R.drawable.cellshapes);
                componentBTableRow.addView(textView);
            }


            //    componentBTableRow.setBackgroundColor(getResources().getColor(R.color.color_light));
            return componentBTableRow;
        }

        /////row 5 fields
        TableRow componentB_DummyRow_TableRow(){

//        TableRow componentBTableRow= new TableRow(this.context);
//        LayoutParams params = new LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//
//        String [] addExtraString={" "," "," "," ","A+ | A"};
//        for(int i=0;i<header.length;i++){
//            if(i % 7==0)
//            this.addView(componentBTableRow, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//            TextView textView = new TextView(this.context);
//            textView.setLayoutParams(params);
//            textView.setText(i);
//            componentBTableRow.addView(textView);
//        }
//
//        TextView textView = new TextView(this.context);
//        textView.setText("M");
//        componentBTableRow.addView(textView);
//        TableRow.LayoutParams bLp = (TableRow.LayoutParams) textView.getLayoutParams();
//        bLp.span = 1;
//        textView.setLayoutParams(bLp);
//        TableRow componentBTableRow= new TableRow(this.context);
//        TableRow.LayoutParams params = new TableRow.LayoutParams(90,LayoutParams.MATCH_PARENT);
//        params.setMargins(2,0,2,0);
//        int size= subjectlist.size();
//        Log.i(TAG,"size "+size);
//        for(int i=0 ;i<size;i++){
//            //   TextView textView =this.headerTextView("M | G");
//            TextView textView =this.headerTextView("M | G");
//            textView.setLayoutParams(params);
//            componentBTableRow.addView(textView);
//        }


//////new Original code
            TableRow componentBTableRow = new TableRow(this.context);
            int headerFieldCount = this.header.length;
            TableRow.LayoutParams params = new TableRow.LayoutParams(220,LayoutParams.MATCH_PARENT);
            params.setMargins(2,2,2,0);
            String[] addExtraString = {"FL","PAS","STA","TOT","PER","No.of A","No.of B"};
            List <String> array3 = new ArrayList <>();
            for (int i = 0; i<header.length; i++) {
                Collections.addAll(array3,"M  |  G");
            }
            Collections.addAll(array3,addExtraString);
            //may be want to changes here
            // for(int i=0 ;i<(headerFieldCount-1);i++){
            for (int i = 0; i<(array3.size()-1); i++) {
                //   TextView textView =this.headerTextView("M | G");
                TextView textView = this.headerTextView(array3.get(i+1));
                Typeface typeface = ResourcesCompat.getFont(context, R.font.poppins_bold);
                textView.setTypeface(typeface);
                textView.setTextSize(COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_02));
                textView.setTextColor(getResources().getColor(R.color.gray_600));
                textView.setLayoutParams(params);
                textView.setBackgroundResource(R.drawable.cellshapes);
                componentBTableRow.addView(textView);
            }
            //     componentBTableRow.setBackgroundColor(getResources().getColor(R.color.color_light));
            return componentBTableRow;
        }

        private void resizeHeaderHeight(){

            TableRow productNameHeaderTableRow = (TableRow)this.tableA.getChildAt(0);
            TableRow produceInfoTableRow = (TableRow)this.tableB.getChildAt(0);

            int rowAheight = this.viewHeight(productNameHeaderTableRow);
            int rowBheight = this.viewHeight(produceInfoTableRow);

            TableRow tableRow = rowAheight<rowBheight ? productNameHeaderTableRow : produceInfoTableRow;
            int finalHeight = rowAheight>rowBheight ? rowAheight : rowBheight;

            this.matchLayoutHeight(tableRow,finalHeight);

        }


        void getTableRowHeaderCellWidth(){

            int tableAChildCount = ((TableRow)this.tableA.getChildAt(0)).getChildCount();
            int tableBChildCount = ((TableRow)this.tableB.getChildAt(0)).getChildCount();


            for (int i = 0; i<(tableAChildCount+tableBChildCount); i++) {

                if (i == 0){
                    this.HeaderCellWidth[i] = this.viewWidth(((TableRow)this.tableA.getChildAt(0)).getChildAt(i));
                } else{
                    this.HeaderCellWidth[i] = this.viewWidth(((TableRow)this.tableB.getChildAt(0)).getChildAt(i-1));
                }
            }
        }

        private void generateTableC_AndTable_B(){


            for (int i = 0; i<HeaderCellWidth.length; i++) {
                Log.i(TAG,this.HeaderCellWidth[i]+"");
            }
            int j = 0;
            for (SampleObject sampleObject : this.sampleObjects) {

                TableRow tableRowForTableC = this.tableRowForTableC(sampleObject);
                // TableRow tableRowForTableD = this.tableRowForTableD(sampleObject,j);
                j++;

//            tableRowForTableC.setBackgroundColor(Color.LTGRAY);
//            tableRowForTableD.setBackgroundColor(Color.LTGRAY);

                this.tableC.addView(tableRowForTableC);
                //      this.tableD.addView(tableRowForTableD);
            }


        }

        TableRow tableRowForTableC(SampleObject sampleObject){
            // TableRow.LayoutParams params = new TableRow.LayoutParams( HeaderCellWidth[0],LayoutParams.MATCH_PARENT);
            TableRow.LayoutParams params = new TableRow.LayoutParams(330,LayoutParams.MATCH_PARENT);
            params.setMargins(2,2,2,0);

            TableRow tableRowForTableC = new TableRow(this.context);
            TextView textView = this.bodyTextView(sampleObject.header1);
            //     TextView textView = this.bodyTextView(header[0]);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.poppins_bold);
            textView.setTypeface(typeface);
            textView.setTextSize(COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_02));
            textView.setTextColor(getResources().getColor(R.color.gray_600));
            tableRowForTableC.addView(textView,params);
            textView.setBackgroundResource(R.drawable.cellshapes);
            //  tableRowForTableC.setBackgroundColor(getResources().getColor(R.color.color_light));


            tableRowForTableC.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v){
                    // Log.i(TAG,"sampleObject.header1 "+sampleObject.header1);
                }
            });


            return tableRowForTableC;

        }



        // resize body table row height
        void resizeBodyTableRowHeight(){

            int tableC_ChildCount = this.tableC.getChildCount();

            for (int x = 0; x<tableC_ChildCount; x++) {

                TableRow productNameHeaderTableRow = (TableRow)this.tableC.getChildAt(x);
                TableRow productInfoTableRow = (TableRow)this.tableD.getChildAt(x);

                int rowAHeight = this.viewHeight(productNameHeaderTableRow);
                int rowBHeight = this.viewHeight(productInfoTableRow);

                TableRow tableRow = rowAHeight<rowBHeight ? productNameHeaderTableRow : productInfoTableRow;
                int finalHeight = rowAHeight>rowBHeight ? rowAHeight : rowBHeight;

                this.matchLayoutHeight(tableRow,finalHeight);
            }

        }

        private void matchLayoutHeight(TableRow tableRow,int finalHeight){
            int tableRowChildCount = tableRow.getChildCount();

            if (tableRow.getChildCount() == 1){
                View view = tableRow.getChildAt(0);
                TableRow.LayoutParams params = (TableRow.LayoutParams)view.getLayoutParams();
                params.height = finalHeight-(params.bottomMargin+params.topMargin);
            }

            for (int i = 0; i<tableRowChildCount; i++) {
                View view = tableRow.getChildAt(i);
                TableRow.LayoutParams params = (TableRow.LayoutParams)view.getLayoutParams();

                if (!isTheHeighestLayout(tableRow,i)){
                    params.height = finalHeight-(params.bottomMargin+params.topMargin);
                    return;
                }
            }
        }

        private boolean isTheHeighestLayout(TableRow tableRow,int layoutposition){
            int tableRowChildCount = tableRow.getChildCount();
            int heightViewPosition = -1;
            int viewHeight = 0;

            for (int i = 0; i<tableRowChildCount; i++) {
                View view = tableRow.getChildAt(i);
                int height = this.viewHeight(view);

                if (viewHeight<height){
                    heightViewPosition = i;
                    viewHeight = height;
                }
            }

            return heightViewPosition == layoutposition;
        }

        private int viewWidth(View view){
            view.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
            return view.getMeasuredWidth();
        }

        private int viewHeight(View view){
            view.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
            return view.getMeasuredHeight();
        }


        // table cell standard TextView
        TextView bodyTextView(String label){

            TextView bodyTextView = new TextView(this.context);
            bodyTextView.setBackgroundColor(Color.WHITE);
            bodyTextView.setText(label);
            bodyTextView.setGravity(Gravity.CENTER);
            bodyTextView.setPadding(10,10,10,10);

            return bodyTextView;
        }

        // header standard TextView
        TextView headerTextView(String label){

            TextView headerTextView = new TextView(this.context);
            headerTextView.setBackgroundColor(Color.WHITE);
            headerTextView.setText(label);
            headerTextView.setGravity(Gravity.CENTER);
            headerTextView.setPadding(15,15,15,15);

            return headerTextView;
        }


        class MyHorizontalScrollView extends HorizontalScrollView{

            public MyHorizontalScrollView(Context context){
                super(context);
            }

            @Override
            protected void onScrollChanged(int l,int t,int oldl,int oldt){
                String tag = (String)this.getTag();

                if (tag.equalsIgnoreCase("horizontal scroll view b")){
                    horizontalScrollViewD.scrollTo(l,0);
                } else{
                    horizontalScrollViewB.scrollTo(l,0);
                }
            }

        }

        // scroll view custom class
        class MyScrollView extends ScrollView{

            public MyScrollView(Context context){
                super(context);
            }

            @Override
            protected void onScrollChanged(int l,int t,int oldl,int oldt){

                String tag = (String)this.getTag();

                if (tag.equalsIgnoreCase("scroll view c")){
                    scrollViewD.scrollTo(0,t);
                } else{
                    scrollViewC.scrollTo(0,t);
                }
            }
        }


        /////////////////////////////////////////////
//        public class SampleObject{
//
//            String header1;
//            String header2;
//            String header3;
//            String header4;
//            String header5;
//            String header6;
//            String header7;
//            String header8;
//            String header9;
//
//
//            public SampleObject(String header1,String header2,String header3,
//                                String header4,String header5,String header6,
//                                String header7,String header8){
//                //
//                this.header1 = header1;
//                this.header2 = header2;
//                this.header3 = header3;
//                this.header4 = header4;
//                this.header5 = header5;
//                this.header6 = header6;
//                this.header7 = header7;
//                this.header8 = header8;
//
//            }
//        }
//
//
//        public class SampleObject1{
//
//            String header1;
//            String header2;
//            String header3;
//            String header4;
//            String header5;
//            String header6;
//            String header7;
//
//
//            public SampleObject1(String header1,String header2,String header3,
//                                 String header4,String header5,String header6,
//                                 String header7){
//                //
//                this.header1 = header1;
//                this.header2 = header2;
//                this.header3 = header3;
//                this.header4 = header4;
//                this.header5 = header5;
//                this.header6 = header6;
//                this.header7 = header7;
//
//
//            }
//        }

    }

}
