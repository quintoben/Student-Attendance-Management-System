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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.quintoben.sams.utils.HttpUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by quintoben on 2016/3/5.
 */
public class MessageBoxFragment extends Fragment {

    private static String TAG = "MessageBoxFragment";
    private ListView listView;
    private List<Map<String,String>> list = new ArrayList<Map<String,String>>();
    private List<Map<String, String>> revertList = new ArrayList<Map<String, String>>();
    private ImageButton refresh;
    private String id;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.message_box_fragment, container, false);

        Bundle bundle = new Bundle();
        bundle = getArguments();
        id = bundle.getString("id");
        listView = (ListView)rootView.findViewById(R.id.msgbox);
        refresh = (ImageButton)rootView.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),R.string.refresh_success,Toast.LENGTH_SHORT).show();
                list.clear();
                revertList.clear();SimpleAdapter simpleAdapter;
                String url = HttpUtil.BASE_URL + "MessageServlet";
                Map<String, String> m = new HashMap<String, String>();
                m.put("id",id);
                try {
                    JSONArray jsonArray = new JSONArray(HttpUtil.postRequest(url,m));
                    for(int i = 1; i<jsonArray.length(); i++){
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("msg", jsonArray.getJSONObject(i).getString("msg"));
                        list.add(map);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //show the latest msg first
                for(int i = list.size() - 1 ;i >= 0 ; i --){
                    revertList.add(list.get(i));
                }
                simpleAdapter = new SimpleAdapter(rootView.getContext(),list,R.layout.msg_list,new String[]{"msg"},
                        new int[]{ R.id.msg});
                listView.setAdapter(simpleAdapter);
            }
        });
        SimpleAdapter simpleAdapter;
        String url = HttpUtil.BASE_URL + "MessageServlet";
        Map<String, String> m = new HashMap<String, String>();
        m.put("id",id);
        try {
            JSONArray jsonArray = new JSONArray(HttpUtil.postRequest(url,m));
            Log.e("JSON", jsonArray.toString());
            Log.e("length",jsonArray.length()+"");
            for(int i = 1; i<jsonArray.length(); i++){
                Map<String, String> map = new HashMap<String, String>();
                map.put("msg", jsonArray.getJSONObject(i).getString("msg"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int i = list.size() - 1 ;i >= 0 ; i --){
            revertList.add(list.get(i));
        }
        simpleAdapter = new SimpleAdapter(rootView.getContext(),list,R.layout.msg_list,new String[]{"msg"},
                new int[]{ R.id.msg});
        listView.setAdapter(simpleAdapter);


        return rootView;
    }

    public void onPause(){
        super.onPause();
        list.clear();
        revertList.clear();
    }

}
