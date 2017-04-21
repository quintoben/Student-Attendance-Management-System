package com.example.quintoben.sams;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.quintoben.sams.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by quintoben on 2016/3/26.
 */
public class SetCoursesFragment extends Fragment {
    private static String TAG = "SetCoursesFragment";

    private Button choose_course;
    private Button choose_location;
    private Button choose_classes;
    private Button choose_time;
    private Button set;
    private TextView course;
    private TextView location;
    private TextView classes;
    private TextView time;
    private String tempCourse = "";
    private String tempLocation = "";
    private String tempClasses = "";
    private String tempTime = "";
    String id;
    AlertDialog chooseCourseDialog = null;
    AlertDialog chooseLocationDialog = null;
    AlertDialog chooseClassesDialog = null;
    AlertDialog chooseTimeDialog = null;
    List<Map<String, String>>  courseIdMap = new ArrayList<Map<String, String>>();
    List<Map<String, String>>  classesIdMap = new ArrayList<Map<String, String>>();
    String loc = "";
    String t = "";
    int start = 0;
    boolean checked = false;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.set_courses_fragment, container, false);

        Bundle bundle = new Bundle();
        bundle = getArguments();
        id = bundle.getString("id");

        choose_course = (Button)rootView.findViewById(R.id.choose_course);
        choose_location = (Button)rootView.findViewById(R.id.choose_location);
        choose_classes = (Button)rootView.findViewById(R.id.choose_classes);
        choose_time = (Button)rootView.findViewById(R.id.choose_time);
        set = (Button)rootView.findViewById(R.id.set);
        course = (TextView)rootView.findViewById(R.id.course);
        location = (TextView)rootView.findViewById(R.id.location);
        classes = (TextView)rootView.findViewById(R.id.classes);
        time = (TextView)rootView.findViewById(R.id.time);

        initChooseCourseDialog();
        initLocationDialog();
        initChooseClassesDialog();
        initChooseTimeDialog();


        choose_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCourseDialog.show();
            }
        });

        choose_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseLocationDialog.show();
            }
        });

        choose_classes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseClassesDialog.show();
            }
        });

        choose_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTimeDialog.show();
            }
        });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (course.getText().toString().equals("")) {
                    Toast.makeText(getActivity(),R.string.pls_set_course,Toast.LENGTH_SHORT).show();
                } else if (location.getText().toString().equals("")) {
                    Toast.makeText(getActivity(),R.string.pls_set_location,Toast.LENGTH_SHORT).show();
                } else if (classes.getText().toString().equals("")) {
                    Toast.makeText(getActivity(),R.string.pls_set_class,Toast.LENGTH_SHORT).show();
                } else if (time.getText().toString().equals("")) {
                    Toast.makeText(getActivity(),R.string.pls_set_time,Toast.LENGTH_SHORT).show();
                } else {
                    String teacher_id = id;
                    String course_id = "";
                    for (int i = 0; i < courseIdMap.size(); i++) {
                        if (courseIdMap.get(i).containsKey(course.getText().toString())) {
                            course_id = courseIdMap.get(i).get(course.getText().toString());
                        }
                    }
                    String locate;
                    locate = location.getText().toString();
                    String classes_id = "";
                    for (int i = 0; i < classesIdMap.size(); i++) {
                        if (classesIdMap.get(i).containsKey(classes.getText().toString())) {
                            classes_id = classesIdMap.get(i).get(classes.getText().toString());
                        }
                    }
                    String ti = time.getText().toString();
                    Log.e(TAG, teacher_id + course_id + locate + classes_id + ti);


                    Map<String, String> map = new HashMap<String, String>();
                    map.put("teacher_id", teacher_id);
                    map.put("course_id", course_id);
                    map.put("location", locate);
                    map.put("classes_id", classes_id);
                    map.put("time", ti);

                    try {
                        String url = HttpUtil.BASE_URL + "SetCoursesServlet";
                        JSONObject jsonObject = new JSONObject(HttpUtil.postRequest(url, map));
                        if (jsonObject.getString("result").equals("success")) {
                            //success
                            Toast.makeText(getActivity(),R.string.set_successfully,Toast.LENGTH_SHORT).show();

//                            ManageFragment.onStart();
                        } else {
                            //fail
                            Log.e("tag", "fail");
                            Toast.makeText(getActivity(),R.string.set_fail,Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    course.setText("");
                    location.setText("");
                    time.setText("");
                    classes.setText("");
                    TeacherMainActivity mainActivity = (TeacherMainActivity)getActivity();
                    mainActivity.initViewPager();
                }
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
                course_list.add(map);
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put(jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("id"));
                courseIdMap.add(map1);
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
                        course.setText(temp.substring(i + 1 , temp.length() - 1));
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
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(loc.equals("")){
                            Toast.makeText(getActivity(),R.string.pls_select_building,Toast.LENGTH_SHORT).show();
                        }
                        else if(editText.getText().toString().equals("")){
                            Toast.makeText(getActivity(),R.string.pls_enter_room,Toast.LENGTH_SHORT).show();
                        }
                        else {
                            location.setText(loc + editText.getText().toString());
                            dialog.cancel();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checked) {
                        radioGroup.clearCheck();
                        }
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
            Log.e("JSON", jsonArray.toString());
            for(int i = 0; i<jsonArray.length(); i++){
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", jsonArray.getJSONObject(i).getString("name"));
                classes_list.add(map);
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put(jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("id"));
                classesIdMap.add(map1);
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
                Log.e("temp",temp);
                for(int i = 0 ; i < temp.length() ; i ++){
                    if(temp.charAt(i) == '='){
                        classes.setText(temp.substring(i + 1 , temp.length() - 1));
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
        Date date = new Date();
        long minTime = date.getTime();
        datePicker.setMinDate(System.currentTimeMillis()-1000);

        linearLayout.addView(datePicker);
        chooseTimeDialog = new AlertDialog.Builder(this.getActivity())
                .setTitle(R.string.set_time)
                .setView(linearLayout)
                .setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        t = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth() + " ";
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
                                        time.setText(t);
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

    public void onStart(){
        super.onStart();
        course.setText(tempCourse);
        classes.setText(tempClasses);
        location.setText(tempLocation);
        time.setText(tempTime);
    }

    public void onPause(){
        super.onPause();
        tempCourse = course.getText().toString();
        tempClasses = classes.getText().toString();
        tempLocation = location.getText().toString();
        tempTime = time.getText().toString();
    }
}
