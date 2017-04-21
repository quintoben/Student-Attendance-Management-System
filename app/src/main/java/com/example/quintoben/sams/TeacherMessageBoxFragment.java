package com.example.quintoben.sams;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.quintoben.sams.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by quintoben on 2016/3/27.
 */
public class TeacherMessageBoxFragment extends Fragment {
    private ListView listView;
    private List<Map<String,String>> list = new ArrayList<Map<String,String>>();
    private List<Map<String, String>> revertList = new ArrayList<Map<String, String>>();
    private ImageButton refresh;
    String leaveId;
    String id = null;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.teacher_msg_fragment, container, false);//关联布局文件

        Bundle bundle = new Bundle();
        bundle = getArguments();
        id = bundle.getString("id");
        listView = (ListView)rootView.findViewById(R.id.msgbox);
        refresh = (ImageButton)rootView.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(),R.string.refresh_success,Toast.LENGTH_SHORT).show();
                revertList.clear();
                list.clear();
                SimpleAdapter simpleAdapter;
                String url = HttpUtil.BASE_URL + "TeacherMessageServlet";
                Map<String, String> m = new HashMap<String, String>();
                m.put("id",id);
                try {
                    JSONArray jsonArray = new JSONArray(HttpUtil.postRequest(url,m));
                    Log.e("JSON", jsonArray.toString());
                    for(int i = 0; i<jsonArray.length(); i++){

                        Map<String, String> map = new HashMap<String, String>();
                        map.put("msg", jsonArray.getJSONObject(i).getString("msg"));
                        map.put("leaveId",jsonArray.getJSONObject(i).getString("id"));
                        list.add(map);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for(int i = list.size() - 1 ; i >= 0 ; i --){
                    revertList.add(list.get(i));
                }
                Log.e("revertList",revertList.toString());

                simpleAdapter = new SimpleAdapter(rootView.getContext(),revertList,R.layout.teacher_msg_list,new String[]{"msg"},
                        new int[]{ R.id.msg});
                listView.setAdapter(simpleAdapter);
//        listView.setAdapter(simpleAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        for(int i = 0 ; i < parent.getCount() ; i ++){
                            View v = parent.getChildAt(i);
                            Button b1 = (Button)v.findViewById(R.id.approve);
                            Button b2 = (Button)v.findViewById(R.id.reject);
                            b1.setVisibility(View.INVISIBLE);
                            b2.setVisibility(View.INVISIBLE);
                        }

                        Button approve;
                        Button reject;
                        approve = (Button)view.findViewById(R.id.approve);
                        reject = (Button)view.findViewById(R.id.reject);
                        approve.setVisibility(View.VISIBLE);
                        reject.setVisibility(View.VISIBLE);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map = (HashMap<String,String>)parent.getItemAtPosition(position);
                        Log.e("map",map.toString());
                        leaveId = map.get("leaveId");
                        approve.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url = HttpUtil.BASE_URL + "ApproveLeaveServlet";
                                Map<String, String> m = new HashMap<String, String>();
                                m.put("id",leaveId);
//                        Log.e("leaveid",leaveId);
                                try {
                                    JSONObject jsonObject = new JSONObject(HttpUtil.postRequest(url,m));
                                    if(jsonObject.getString("result").equals("success")){
                                        Log.e("result",jsonObject.getString("result"));
                                        revertList.remove(position);
                                        listView.setAdapter(new SimpleAdapter(rootView.getContext(),revertList,R.layout.teacher_msg_list,new String[]{"msg"},
                                                new int[]{ R.id.msg}));

                                    }
                                    else{
                                        //fail
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        reject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url = HttpUtil.BASE_URL + "RejectLeaveServlet";
                                Map<String, String> m = new HashMap<String, String>();
                                m.put("id",leaveId);
                                try {
                                    JSONObject jsonObject = new JSONObject(HttpUtil.postRequest(url,m));
                                    if(jsonObject.getString("result").equals("success")){
                                        revertList.remove(position);
                                        listView.setAdapter(new SimpleAdapter(rootView.getContext(),revertList,R.layout.teacher_msg_list,new String[]{"msg"},
                                                new int[]{ R.id.msg}));
                                    }
                                    else{
                                        //fail
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        });
        SimpleAdapter simpleAdapter;
        String url = HttpUtil.BASE_URL + "TeacherMessageServlet";
        Map<String, String> m = new HashMap<String, String>();
        m.put("id",id);
        try {
            JSONArray jsonArray = new JSONArray(HttpUtil.postRequest(url,m));
            Log.e("JSON", jsonArray.toString());
            for(int i = 0; i<jsonArray.length(); i++){

                Map<String, String> map = new HashMap<String, String>();
                map.put("msg", jsonArray.getJSONObject(i).getString("msg"));
                map.put("leaveId",jsonArray.getJSONObject(i).getString("id"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int i = list.size() - 1 ; i >= 0 ; i --){
            revertList.add(list.get(i));
        }
        Log.e("revertList",revertList.toString());

        simpleAdapter = new SimpleAdapter(rootView.getContext(),revertList,R.layout.teacher_msg_list,new String[]{"msg"},
                new int[]{ R.id.msg});
        listView.setAdapter(simpleAdapter);
//        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                for(int i = 0 ; i < parent.getCount() ; i ++){
                    View v = parent.getChildAt(i);
                    Button b1 = (Button)v.findViewById(R.id.approve);
                    Button b2 = (Button)v.findViewById(R.id.reject);
                    b1.setVisibility(View.INVISIBLE);
                    b2.setVisibility(View.INVISIBLE);
                }

                Button approve;
                Button reject;
                approve = (Button)view.findViewById(R.id.approve);
                reject = (Button)view.findViewById(R.id.reject);
                approve.setVisibility(View.VISIBLE);
                reject.setVisibility(View.VISIBLE);
                HashMap<String, String> map = new HashMap<String, String>();
                map = (HashMap<String,String>)parent.getItemAtPosition(position);
                Log.e("map",map.toString());
                leaveId = map.get("leaveId");
                approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = HttpUtil.BASE_URL + "ApproveLeaveServlet";
                        Map<String, String> m = new HashMap<String, String>();
                        m.put("id",leaveId);
//                        Log.e("leaveid",leaveId);
                        try {
                            JSONObject jsonObject = new JSONObject(HttpUtil.postRequest(url,m));
                            if(jsonObject.getString("result").equals("success")){
                                Log.e("result",jsonObject.getString("result"));
                                revertList.remove(position);
                                listView.setAdapter(new SimpleAdapter(rootView.getContext(),revertList,R.layout.teacher_msg_list,new String[]{"msg"},
                                        new int[]{ R.id.msg}));
                                
                            }
                            else{
                                //fail
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = HttpUtil.BASE_URL + "RejectLeaveServlet";
                        Map<String, String> m = new HashMap<String, String>();
                        m.put("id",leaveId);
                        try {
                            JSONObject jsonObject = new JSONObject(HttpUtil.postRequest(url,m));
                            if(jsonObject.getString("result").equals("success")){
                                revertList.remove(position);
                                listView.setAdapter(new SimpleAdapter(rootView.getContext(),revertList,R.layout.teacher_msg_list,new String[]{"msg"},
                                        new int[]{ R.id.msg}));
                            }
                            else{
                                //fail
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


        return  rootView;
    }
    public void onPause(){
        super.onPause();
        revertList.clear();
        list.clear();
    }
}
