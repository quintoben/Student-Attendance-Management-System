package com.example.quintoben.sams;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.quintoben.sams.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by quintoben on 2016/3/30.
 */
public class ManageFragment extends Fragment {

    private static String TAG = "ManageFragment";
    private ListView listView;
    private Button edit;
    private Button delete;
    private Button show;
    private Bundle bundle;
    private String id;
    private String t = null;
    private String tempCourseName;
    private String tempLocation;
    private String tempTime;
    private String tempClassesName;
    private String tempRecordId;
    private boolean editClick = false;
    private boolean editFlag = false;
    private AlertDialog chooseCourseDialog = null;
    private AlertDialog chooseLocationDialog = null;
    private AlertDialog chooseClassesDialog = null;
    private AlertDialog chooseTimeDialog = null;
    private String loc = null;
    private TextView courseName;
    private TextView courseLocation ;
    private TextView courseTime;
    private TextView courseClassesName;
    private HashMap<String, String> listMap;
    private int pos = 0;
    private String telephone = "";

    private List<Map<String,String>> list = new ArrayList<Map<String,String>>();
    private List<Map<String,String>> tempList = new ArrayList<Map<String, String>>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.manage_fragment, container, false);

        listView = (ListView)rootView.findViewById(R.id.course_list);
        edit = (Button)rootView.findViewById(R.id.edit);
        delete = (Button)rootView.findViewById(R.id.delete);
        show = (Button)rootView.findViewById(R.id.show_detail);
        bundle = getArguments();
        id = bundle.getString("id");

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editClick) {
                    LinearLayout linearLayout = new LinearLayout(rootView.getContext());
                    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    courseName = new TextView(rootView.getContext());
                    courseLocation = new TextView(rootView.getContext());
                    courseTime = new TextView(rootView.getContext());
                    courseClassesName = new TextView(rootView.getContext());
                    courseName.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    courseLocation.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    courseTime.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    courseClassesName.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    courseName.setTextSize(20);
                    courseLocation.setTextSize(20);
                    courseTime.setTextSize(20);
                    courseClassesName.setTextSize(20);
                    courseName.setText(R.string.edit_course_info + tempCourseName);
                    courseLocation.setText(R.string.edit_address_info + tempLocation);
                    courseTime.setText(R.string.edit_time_info + tempTime);
                    courseClassesName.setText(R.string.edit_class_info + tempClassesName);
                    courseName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initChooseCourseDialog();
                            chooseCourseDialog.show();
                        }
                    });
                    courseLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initLocationDialog();
                            chooseLocationDialog.show();
                        }
                    });
                    courseTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initChooseTimeDialog();
                            chooseTimeDialog.show();
                        }
                    });
                    courseClassesName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initChooseClassesDialog();
                            chooseClassesDialog.show();
                        }
                    });
                    linearLayout.addView(courseName);
                    linearLayout.addView(courseLocation);
                    linearLayout.addView(courseTime);
                    linearLayout.addView(courseClassesName);


                    AlertDialog alertDialog = new AlertDialog.Builder(rootView.getContext())
                            .setTitle(R.string.edit_course_information)
                            .setView(linearLayout)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String url = HttpUtil.BASE_URL+"EditTeacherCourseServlet";
                                    Map<String, String> m = new HashMap<String, String>();
                                    m.put("record_id",tempRecordId);
                                    m.put("course_name",tempCourseName);
                                    m.put("course_location",tempLocation);
                                    m.put("course_time",tempTime);
                                    m.put("classes_name",tempClassesName);
                                    try {
                                        JSONObject jsonObject = new JSONObject(HttpUtil.postRequest(url,m));
                                        if(jsonObject.getString("result").equals("success")){
                                            Toast.makeText(getActivity(),R.string.edit_successfully,Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(getActivity(),R.string.edit_fail,Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    for(int i = 0; i < list.size() ; i ++){
                                        if(list.get(i).get("record_id").equals(tempRecordId)){
                                            list.get(i).put("course_name",tempCourseName);
                                            list.get(i).put("course_location", tempLocation);
                                            list.get(i).put("course_time", tempTime);
                                            list.get(i).put("classes_name", tempClassesName);
                                        }
                                    }


                                    listView.setAdapter(new SimpleAdapter(rootView.getContext(),list,R.layout.teacher_course_list,new String[]{"course_name", "course_location", "course_time", "classes_name"},
                                            new int[]{ R.id.course_name, R.id.course_location, R.id.course_time, R.id.classes_name}));
//

                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    tempCourseName =listMap.get("course_name");
                                    tempLocation = listMap.get("course_location");
                                    tempTime = listMap.get("course_time");
                                    tempClassesName = listMap.get("classes_name");
                                    dialog.cancel();
                                }
                            })
                            .create();
                    alertDialog.show();
                    alertDialog.setCanceledOnTouchOutside(false);
                    editClick = false;
                }
                else{
                    Toast.makeText(getActivity(),R.string.pls_select_course_first,Toast.LENGTH_SHORT).show();
                }
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editClick) {
                    AlertDialog alertDialog = new AlertDialog.Builder(rootView.getContext())
                            .setTitle(R.string.sure_to_remove_record)
//                        .setView(linearLayout)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String url = HttpUtil.BASE_URL + "DeleteCourseRecordServlet";
                                    Map<String, String> m = new HashMap<String, String>();
                                    m.put("record_id", tempRecordId);
                                    try {
                                        JSONObject jsonObject = new JSONObject(HttpUtil.postRequest(url, m));
                                        if (jsonObject.getString("result").equals("success")) {
                                            list.remove(pos);
                                            listView.setAdapter(new SimpleAdapter(rootView.getContext(),list,R.layout.teacher_course_list,new String[]{"course_name", "course_location", "course_time", "classes_name"},
                                                    new int[]{ R.id.course_name, R.id.course_location, R.id.course_time, R.id.classes_name}));
                                            Toast.makeText(getActivity(),R.string.remove_successfully,Toast.LENGTH_SHORT).show();
                                            pos = 0;
                                        }
                                        else{
                                            Toast.makeText(getActivity(),R.string.remove_fail,Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    for (int i = 0; i < list.size(); i++) {
                                        if (list.get(i).get("record_id").equals(tempRecordId)) {
                                            list.get(i).put("course_name", tempCourseName);
                                            list.get(i).put("course_location", tempLocation);
                                            list.get(i).put("course_time", tempTime);
                                            list.get(i).put("classes_name", tempClassesName);
                                        }
                                    }


                                    listView.setAdapter(new SimpleAdapter(rootView.getContext(), list, R.layout.teacher_course_list, new String[]{"course_name", "course_location", "course_time", "classes_name"},
                                            new int[]{R.id.course_name, R.id.course_location, R.id.course_time, R.id.classes_name}));

                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();
                                }
                            })
                            .create();
                    alertDialog.show();
                    alertDialog.setCanceledOnTouchOutside(false);
                    editClick = false;
                }
                else{
                    Toast.makeText(getActivity(),R.string.pls_select_course_first,Toast.LENGTH_SHORT).show();
                }
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editClick) {
                    final LinearLayout linearLayout = new LinearLayout(rootView.getContext());
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    final TextView total = new TextView(rootView.getContext());
                    final TextView actual =  new TextView(rootView.getContext());
                    final TextView late = new TextView(rootView.getContext());
                    final TextView leave = new TextView(rootView.getContext());
                    final TextView leaving = new TextView(rootView.getContext());
                    final TextView quit = new TextView(rootView.getContext());

                    total.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    actual.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    late.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    leave.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    leaving.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    quit.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    total.setTextSize(20);
                    actual.setTextSize(20);
                    late.setTextSize(20);
                    leave.setTextSize(20);
                    leaving.setTextSize(20);
                    quit.setTextSize(20);
                    linearLayout.addView(total);
                    linearLayout.addView(actual);
                    linearLayout.addView(late);
                    linearLayout.addView(leave);
                    linearLayout.addView(leaving);
                    linearLayout.addView(quit);

                    String url = HttpUtil.BASE_URL + "AttendDetailServlet";
                    Map<String, String> m = new HashMap<String, String>();
                    m.put("record_id", tempRecordId);

                    try {
                        JSONArray jsonArray = new JSONArray(HttpUtil.postRequest(url,m));
                        JSONArray resultArray = jsonArray.getJSONArray(0);
                        Log.e("resultArray", resultArray.toString());
                        String result = resultArray.getJSONObject(0).getString("result");
                        if(result.equals("true")) {
                            final JSONArray quitStudent = jsonArray.getJSONArray(1);
                            final JSONArray attendStudent = jsonArray.getJSONArray(2);
                            final JSONArray lateStudent = jsonArray.getJSONArray(3);
                            final JSONArray leaveStudent = jsonArray.getJSONArray(4);
                            final JSONArray leavingStudent = jsonArray.getJSONArray(5);

                            String totalNum = quitStudent.length() + "";
                            String actualNum = attendStudent.length() + "";
                            String lateNum = lateStudent.length() + "";
                            String leaveNum = leaveStudent.length() + "";
                            String leavingNum = leavingStudent.length() + "";
                            String quitNum = quitStudent.length() + "";
                            total.setText(R.string.suppose_come_num+ totalNum);
                            actual.setText(R.string.actual_come_num + actualNum);
                            late.setText(R.string.late_num + lateNum +"  "+R.string.click_for_detial);
                            leave.setText(R.string.ask_leave_num + leaveNum );
                            leaving.setText(R.string.not_approve_num + leavingNum + "  "+R.string.click_for_detial);
                            quit.setText(R.string.miss_num + quitNum + "  " +R.string.click_for_detial);

                            late.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LinearLayout layout = new LinearLayout(rootView.getContext());
                                    layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    ListView lv = new ListView(rootView.getContext());

                                    List<Map<String, String>> l = new ArrayList<Map<String, String>>();
                                    SimpleAdapter simpleAdapter;

                                    try {
                                        JSONArray jsonArray = lateStudent;
                                        Log.e("JSON", jsonArray.toString());
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("name", jsonArray.getJSONObject(i).getString("name"));
                                            map.put("phone", jsonArray.getJSONObject(i).getString("phone"));
                                            Log.e("map", map.toString());
                                            l.add(map);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    simpleAdapter = new SimpleAdapter(rootView.getContext(), l, R.layout.late_student_list, new String[]{"name"}, new int[]{R.id.late_student_name});
                                    lv.setAdapter(simpleAdapter);

                                    layout.addView(lv);
                                    AlertDialog alertDialog = new AlertDialog.Builder(rootView.getContext())
                                            .setTitle(R.string.late_list)
                                            .setView(layout)
                                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    ;
                                                }
                                            })
                                            .create();
                                    alertDialog.setCanceledOnTouchOutside(false);
                                    alertDialog.show();

                                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            for(int i = 0 ; i < parent.getCount() ; i ++){
                                                View v = parent.getChildAt(i);
                                                Button button = (Button)v.findViewById(R.id.contact);
                                                button.setVisibility(View.INVISIBLE);
                                            }
                                            Button contact = (Button)view.findViewById(R.id.contact);
                                            contact.setVisibility(View.VISIBLE);
                                            HashMap<String, String> hashMap = (HashMap<String, String>)parent.getItemAtPosition(position);
                                            telephone = hashMap.get("phone");
                                            contact.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    //contact
                                                    String tel = "";
                                                    tel = telephone;
                                                    Uri uri = Uri.parse("tel:" + tel);
                                                    Intent intent = new Intent();
                                                    intent.setAction(Intent.ACTION_DIAL);
                                                    intent.setData(uri);
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                            leaving.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LinearLayout layout = new LinearLayout(rootView.getContext());
                                    layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    ListView listView = new ListView(rootView.getContext());

                                    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                                    SimpleAdapter simpleAdapter;

                                    try {
                                        JSONArray jsonArray = leavingStudent;
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("name", jsonArray.getJSONObject(i).getString("name"));
                                            list.add(map);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    simpleAdapter = new SimpleAdapter(rootView.getContext(), list, R.layout.leaving_student_list, new String[]{"name"}, new int[]{R.id.late_student_name});
                                    listView.setAdapter(simpleAdapter);

                                    layout.addView(listView);
                                    AlertDialog alertDialog = new AlertDialog.Builder(rootView.getContext())
                                            .setTitle(R.string.student_not_approve)
                                            .setView(layout)
                                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    ;
                                                }
                                            })
                                            .create();
                                    alertDialog.setCanceledOnTouchOutside(false);
                                    alertDialog.show();

                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        }
                                    });
                                }
                            });

                            quit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View v) {
                                    LinearLayout layout = new LinearLayout(rootView.getContext());
                                    layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    ListView lv = new ListView(rootView.getContext());
                                    lv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                                    List<Map<String, String>> l = new ArrayList<Map<String, String>>();
                                    SimpleAdapter simpleAdapter;

                                    try {
                                        JSONArray jsonArray = quitStudent;
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("name", jsonArray.getJSONObject(i).getString("name"));
                                            map.put("phone", jsonArray.getJSONObject(i).getString("phone"));
                                            l.add(map);

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    simpleAdapter = new SimpleAdapter(rootView.getContext(), l, R.layout.late_student_list, new String[]{"name"}, new int[]{R.id.late_student_name});
                                    lv.setAdapter(simpleAdapter);


                                    layout.addView(lv);
                                    AlertDialog alertDialog = new AlertDialog.Builder(rootView.getContext())
                                            .setTitle(R.string.miss_student)
                                            .setView(layout)
                                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    ;
                                                }
                                            })
                                            .create();
                                    alertDialog.show();
                                    alertDialog.setCanceledOnTouchOutside(false);

                                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            for(int i = 0 ; i < parent.getCount() ; i ++){
                                                View v = parent.getChildAt(i);
                                                Button button = (Button)v.findViewById(R.id.contact);
                                                button.setVisibility(View.INVISIBLE);
                                            }
                                            Button contact = (Button)view.findViewById(R.id.contact);
                                            contact.setVisibility(View.VISIBLE);
                                            HashMap<String, String> hashMap = (HashMap<String, String>)parent.getItemAtPosition(position);
                                            telephone = hashMap.get("phone");
                                            contact.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    //contact
                                                    String tel = "";
                                                    tel = telephone;
                                                    Uri uri = Uri.parse("tel:" + tel);
                                                    Intent intent = new Intent();
                                                    intent.setAction(Intent.ACTION_DIAL);
                                                    intent.setData(uri);
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                            AlertDialog alertDialog = new AlertDialog.Builder(rootView.getContext())
                                    .setTitle(R.string.attedance_detail)
                                    .setView(linearLayout)
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.cancel();
                                        }
                                    })
                                    .create();
                            alertDialog.show();
                            alertDialog.setCanceledOnTouchOutside(false);
                        }
                        else{
                            Toast.makeText(getActivity(),R.string.not_end_no_detail,Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    editClick = false;
                }
                else{
                    Toast.makeText(getActivity(),R.string.pls_select_course_first,Toast.LENGTH_SHORT).show();
                }
            }

        });

        SimpleAdapter simpleAdapter;

        simpleAdapter = new SimpleAdapter(rootView.getContext(),list,R.layout.teacher_course_list,new String[]{"course_name", "course_location", "course_time", "classes_name"},
                new int[]{ R.id.course_name, R.id.course_location, R.id.course_time, R.id.classes_name});
        listView.setAdapter(simpleAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(pos <=firstVisibleItem ){
                    for(int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount ; i ++) {
                        if (listView.getChildAt(i) != null) {
                            listView.getChildAt(i).setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }
                    }
                }
                else if(pos >= firstVisibleItem + visibleItemCount){
                    for(int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount ; i ++) {
                        if (listView.getChildAt(i) != null) {
                            listView.getChildAt(i).setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }
                    }
                }
                else {
                    int p = pos - firstVisibleItem;
                    if (listView.getChildAt(p) != null) {
                        listView.getChildAt(p).setBackgroundColor(Color.parseColor("#BBFFFF"));
                    }
                }



            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editClick = true;
//                view.setBackgroundResource(R.color.wallet_holo_blue_light);
                for (int i = 0; i < parent.getCount(); i++) {
                    if(parent.getChildAt(i)!=null) {
                        parent.getChildAt(i).setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                }

                view.setBackgroundColor(Color.parseColor("#BBFFFF"));
//                view.setBackgroundResource(R.color.wallet_holo_blue_light);

                listMap = (HashMap)parent.getItemAtPosition(position);
                tempRecordId = listMap.get("record_id");
                tempCourseName =listMap.get("course_name");
                tempLocation = listMap.get("course_location");
                tempTime = listMap.get("course_time");
                tempClassesName = listMap.get("classes_name");
                pos = position;

            }

        });



        return  rootView;
    }

    void initChooseCourseDialog(){
        LinearLayout choose_course_layout = new LinearLayout(this.getActivity());
        choose_course_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ListView course_list_view = new ListView(this.getActivity());

        List<Map<String, String>>  course_list = new ArrayList<Map<String, String>>();
        SimpleAdapter simpleAdapter;
        String url = HttpUtil.BASE_URL+"ChooseCourseServlet";
        try {
            JSONArray jsonArray = new JSONArray(HttpUtil.getRequest(url));
            Log.e("JSON", jsonArray.toString());
            for(int i = 0; i<jsonArray.length(); i++){
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", jsonArray.getJSONObject(i).getString("name"));
                Log.e("map",map.toString());
                course_list.add(map);
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put(jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        simpleAdapter = new SimpleAdapter(this.getActivity(), course_list, R.layout.choose_course, new String[] {"name"}, new int[]{R.id.course_item});
        course_list_view.setAdapter(simpleAdapter);

        choose_course_layout.addView(course_list_view);
        chooseCourseDialog = new AlertDialog.Builder(this.getActivity())
                .setTitle(R.string.choose_course)
                .setView(choose_course_layout)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        chooseCourseDialog.setCanceledOnTouchOutside(false);

        course_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String temp;
                temp = parent.getItemAtPosition(position).toString();
                for(int i = 0 ; i < temp.length() ; i ++){
                    if(temp.charAt(i) == '='){
//                        Log.e("sub",temp.substring(i + 1  , temp.length() - 1));
                        tempCourseName = temp.substring(i + 1 , temp.length() - 1);
                        courseName.setText(R.string.edit_course_info + tempCourseName);
//                        Crouton.makeText(getActivity(), "修改成功", Style.CONFIRM).show();

                        break;
                    }

                }

                chooseCourseDialog.cancel();
            }
        });
    }

    RadioButton intoRadioButton(String text){
        RadioButton radioButton = new RadioButton(this.getActivity());
        radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT));
        radioButton.setText(text);
        radioButton.setTextSize(15);
//        radioButton.setId();

        return radioButton;
    }

    void initLocationDialog(){

        LinearLayout linearLayout = (LinearLayout)getLayoutInflater(getArguments()).inflate(R.layout.radio_group_layout, null);

        final RadioGroup radioGroup = (RadioGroup)linearLayout.findViewById(R.id.building);

        final EditText editText = (EditText)linearLayout.findViewById(R.id.room);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.A1){
                    loc = "A1";
                }
                else if(checkedId == R.id.A2){
                    loc = "A2";
                }
                else if(checkedId == R.id.A3){
                    loc = "A3";
                }
                else if(checkedId == R.id.A4){
                    loc = "A4";
                }
                else if(checkedId == R.id.A5){
                    loc = "A5";
                }
                else if(checkedId == R.id.B3){
                    loc = "B3";
                }
                else if(checkedId == R.id.B7){
                    loc = "B7";
                }
            }
        });
        chooseLocationDialog = new AlertDialog.Builder(this.getActivity())
                .setTitle(R.string.choose_location)
                .setView(linearLayout)
                .setPositiveButton(R.string.confirm,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(editText.getText().toString().equals("")){
                            Toast.makeText(getActivity(),R.string.pls_enter_room,Toast.LENGTH_SHORT).show();
                        }
                        else {
                            tempLocation = loc + editText.getText().toString();
                            courseLocation.setText(R.string.edit_address_info + tempLocation);
//                            Crouton.makeText(getActivity(), "修改成功", Style.CONFIRM).show();
                            dialog.cancel();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        radioGroup.clearCheck();
                        editText.setText("");
                        dialog.cancel();
                    }
                })
                .create();
        chooseLocationDialog.setCanceledOnTouchOutside(false);

    }

    void initChooseClassesDialog(){
        LinearLayout linearLayout = new LinearLayout(this.getActivity());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ListView listView = new ListView(this.getActivity());

        List<Map<String, String>>  classes_list = new ArrayList<Map<String, String>>();
        SimpleAdapter simpleAdapter;
        String url = HttpUtil.BASE_URL+"ChooseClassesServlet";
        try {
            JSONArray jsonArray = new JSONArray(HttpUtil.getRequest(url));
            for(int i = 0; i<jsonArray.length(); i++){
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", jsonArray.getJSONObject(i).getString("name"));
                Log.e("map",map.toString());
                classes_list.add(map);
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put(jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("id"));
                Log.e("map",map1.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        simpleAdapter = new SimpleAdapter(this.getActivity(), classes_list, R.layout.choose_classes, new String[] {"name"}, new int[]{R.id.classes_item});
        listView.setAdapter(simpleAdapter);

        linearLayout.addView(listView);
        chooseClassesDialog = new AlertDialog.Builder(this.getActivity())
                .setTitle(R.string.choose_class)
                .setView(linearLayout)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        chooseClassesDialog.setCanceledOnTouchOutside(false);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String temp;
                temp = parent.getItemAtPosition(position).toString();
                for(int i = 0 ; i < temp.length() ; i ++){
                    if(temp.charAt(i) == '='){
//                        Log.e("sub",temp.substring(i + 1  , temp.length() - 1));
                        tempClassesName = temp.substring(i + 1 , temp.length() - 1);

                        courseClassesName.setText(R.string.edit_class_info + tempClassesName);
                        Log.e("tempclassesname",tempClassesName);
//                        Crouton.makeText(getActivity(), "修改成功", Style.CONFIRM).show();
                        break;
                    }

                }

                chooseClassesDialog.cancel();
            }
        });


    }

    void initChooseTimeDialog(){
        LinearLayout linearLayout = new LinearLayout(this.getActivity());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);




        final DatePicker datePicker = new DatePicker(this.getActivity());
        datePicker.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        datePicker.setCalendarViewShown(true);
        datePicker.setSpinnersShown(true);




        linearLayout.addView(datePicker);
        chooseTimeDialog = new AlertDialog.Builder(this.getActivity())
                .setTitle(R.string.set_time)
                .setView(linearLayout)
                .setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        if (datePicker.getMonth() > 10 && datePicker.getDayOfMonth() > 10) {
                        t = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) +  "-" + datePicker.getDayOfMonth() + " ";
//                        } else if (datePicker.getMonth() < 10 && datePicker.getDayOfMonth() > 10) {
//                            t = datePicker.getYear() + "-0" + datePicker.getMonth() + "-" + datePicker.getDayOfMonth() + " ";
//                        } else if (datePicker.getMonth() > 10 && datePicker.getDayOfMonth() < 10) {
//                            t = datePicker.getYear() + "-" + datePicker.getMonth() + "-0" + datePicker.getDayOfMonth() + " ";
//                        } else {
//                            t = datePicker.getYear() + "-0" + datePicker.getMonth() + "-0" + datePicker.getDayOfMonth() + " ";
//                        }
                        LinearLayout layout = new LinearLayout(getActivity());
                        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        layout.setOrientation(LinearLayout.VERTICAL);
                        final TimePicker timePicker = new TimePicker(getActivity());
                        timePicker.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


                        layout.addView(timePicker);
                        AlertDialog alertDialog;
                        alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.set_time)
                                .setView(layout)
                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        t += timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute() + ":" + "00";

                                        tempTime = t;
                                        courseTime.setText(R.string.edit_time_info + tempTime);
//                                        Crouton.makeText(getActivity(), "修改成功", Style.CONFIRM).show();
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton(R.string.previous, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        chooseTimeDialog.show();

                                    }
                                })
                                .create();
                        alertDialog.show();

                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        t = null;
                        dialog.cancel();
                    }
                })
                .create();
        chooseTimeDialog.setCanceledOnTouchOutside(false);

    }

//    public void onPause(){
//        super.onPause();
//        list.clear();
//        tempList = list;
//    }

    public void onStop(){
        super.onStop();
        list.clear();
    }

//    public void onResume(){
//        super.onResume();
////        list = tempList;
////        listView.setAdapter(new SimpleAdapter(this.getActivity(),list,R.layout.teacher_course_list,new String[]{"course_name", "course_location", "course_time", "classes_name"},
////                new int[]{ R.id.course_name, R.id.course_location, R.id.course_time, R.id.classes_name}));
//        onStart();
//    }

//    public void onRestart(){
//        super.

//    }


    public void onStart() {
        super.onStart();
        String url = HttpUtil.BASE_URL + "TeacherCourseServlet";
        Map<String, String> m = new HashMap<String, String>();
        m.put("id", id);
        try {
            JSONArray jsonArray = new JSONArray(HttpUtil.postRequest(url, m));
            Log.e("JSON", jsonArray.toString());
            for (int i = 0; i < jsonArray.length(); i++) {

                Map<String, String> map = new HashMap<String, String>();
                map.put("record_id", jsonArray.getJSONObject(i).getString("record_id"));
//                i++;
                map.put("course_name", jsonArray.getJSONObject(i).getString("course_name"));
//                i++;
                map.put("course_location", jsonArray.getJSONObject(i).optString("course_location"));
//                i++;
                map.put("course_time", jsonArray.getJSONObject(i).getString("course_time"));
//                i++;
                map.put("classes_name", jsonArray.getJSONObject(i).getString("classes_name"));

                Log.e("map", map.toString());
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("list", list.toString());
    }

}
