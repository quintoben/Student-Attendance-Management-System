package com.example.quintoben.sams;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.quintoben.sams.utils.HttpUtil;
import com.example.quintoben.sams.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by quintoben on 2016/3/4.
 */
public class AttendFragment extends Fragment {

    private static String TAG = "AttendFragment";

    private List<Map<String,String>> list = new ArrayList<Map<String,String>>();

    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private Locate locate=null;
    private LocationService locationService;
    private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>(); // 存放历史定位结果的链表，最大存放当前结果的前5次定位结果
    //use for store location entity
    class LocationEntity {
        BDLocation location;
        long time;
    }

    private ImageButton refresh;
    private Button attendence;
    private Button ask4leave;
    private String temp = null;
    private String id ;
    private int pos = 0;
    String record_id = null;
    String course_time = null;

    private ListView listView;

    //building location example
    private LatLng A5Data = new LatLng(23.056371, 113.413343);
    private LatLng A4Data = new LatLng(23.056288, 113.412238);
    private LatLng A3Date = new LatLng(23.055673, 113.411942);
    private LatLng A2Data = new LatLng(23.054783, 113.411942);
    private LatLng A1Data = new LatLng(23.05406, 113.411897);
    private LatLng B3Data = new LatLng(23.051458, 113.415256);
    private LatLng B7Data = new LatLng(23.052364, 113.413729);
    private LatLng GZUCM = new LatLng(23.065731, 113.412975);
    private LatLng tempLng;
    private LatLng currentPoint;


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState){
        //initialize
        SDKInitializer.initialize(this.getActivity().getApplicationContext());
        final View rootView = layoutInflater.inflate(R.layout.attend_fragment, viewGroup, false);

        //baidumap
        mMapView = (MapView)rootView.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){
            child.setVisibility(View.INVISIBLE);
        }
        //map scale control
        mMapView.showScaleControl(false);
        //hide scale control
        mMapView.showZoomControls(false);
        //        mMapView.addView(locate);
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(19));
        locationService =new LocationService(this.getActivity().getApplicationContext());
        LocationClientOption mOption = locationService.getDefaultLocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mOption.setCoorType("bd09ll");
        locationService.setLocationOption(mOption);
        locationService.registerListener(listener);
        locationService.start();


        Bundle bundle = getArguments();
        if(bundle != null){
            id = bundle.getString("id");
        }

        attendence = (Button)rootView.findViewById(R.id.attendence);
        ask4leave = (Button)rootView.findViewById(R.id.ask4leave);
        refresh = (ImageButton)rootView.findViewById(R.id.refresh);

        //refresh the list
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),R.string.refresh_success,Toast.LENGTH_SHORT).show();
                //clean the list
                list.clear();
                SimpleAdapter simpleAdapter;
                listView = (ListView)rootView.findViewById(R.id.course_list);
                String url = HttpUtil.BASE_URL+"CourseInfoServlet";
                Map<String, String> m = new HashMap<String, String>();
                m.put("id",id);
                try {
                    JSONArray jsonArray = new JSONArray(HttpUtil.postRequest(url,m));
                    Log.e("JSON", jsonArray.toString());
                    for(int i = 0; i<jsonArray.length(); i++){

                        Map<String, String> map = new HashMap<String, String>();
                        map.put("record_id", jsonArray.getJSONObject(i).getString("record_id"));i++;
                        map.put("course_name", jsonArray.getJSONObject(i).getString("course_name"));i++;
                        map.put("course_location", jsonArray.getJSONObject(i).optString("course_location"));i++;
                        map.put("course_time", jsonArray.getJSONObject(i).getString("course_time"));i++;
                        map.put("course_teacher", jsonArray.getJSONObject(i).getString("course_teacher"));
                        Log.e(TAG,map.toString());
                        list.add(map);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e(TAG,list.toString());
                simpleAdapter = new SimpleAdapter(rootView.getContext(),list,R.layout.course_list,new String[]{"course_name", "course_location", "course_time", "course_teacher"},
                        new int[]{ R.id.course_name, R.id.course_location, R.id.course_time, R.id.course_teacher});
                listView.setAdapter(simpleAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        for(int i=0;i<parent.getCount();i++) {
                            parent.getChildAt(i).setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }

                        view.setBackgroundColor(Color.parseColor("#BBFFFF"));
                        temp = parent.getItemAtPosition(position).toString();
                        Log.e(TAG,parent.getItemAtPosition(position).toString());
                        pos = position;
                        for(int i = 0 ; i < temp.length() ; i ++ ){
                            if(temp.substring(i,i+10).equals("record_id")){
                                int j;
                                for(j = i + 10;j<temp.length();j++){
                                    if(temp.charAt(j) == ',') {
                                        record_id = temp.substring(i + 10, j );
                                        break;
                                    }
                                }

                            }
                            if(temp.substring(i,i+12).equals("course_time")){
                                int j;
                                for(j = i + 12;j<temp.length();j++){
                                    if(temp.charAt(j) == ',') {
                                        course_time = temp.substring(i + 12, j );
                                        break;
                                    }
                                }
                            }
                        }

                        HashMap<String, String> hashMap = (HashMap<String, String>)parent.getItemAtPosition(position);
                        String location = hashMap.get("course_location");
                        if(location.substring(0,2).equals("A1")){
                            tempLng = A1Data;
                        }
                        else if(location.substring(0, 2).equals("A2")){
                            tempLng = A2Data;
                        }
                        else if(location.substring(0, 2).equals("A3")){
                            tempLng = A3Date;
                        }
                        else if(location.substring(0, 2).equals("A4")){
                            tempLng = A4Data;
                        }
                        else if(location.substring(0, 2).equals("A5")){
                            tempLng = A5Data;
                        }
                        else if(location.substring(0, 2).equals("B3")){
                            tempLng = B3Data;
                        }
                        else if(location.substring(0, 2).equals("B7")){
                            tempLng = GZUCM;
                        }
                    }
                });
            }
        });

//        button attendence
        attendence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(temp == null){
                    Toast.makeText(getActivity(),R.string.not_choose_class,Toast.LENGTH_LONG).show();
                }
                else{
                    try {
                        JSONObject jsonObject;
                        jsonObject = query(id, record_id,course_time);
                        if(jsonObject.getString("result").equals("true")){
                            Toast.makeText(getActivity(),R.string.already_sign,Toast.LENGTH_SHORT).show();
                        }
                        else if(jsonObject.getString("result").equals("quit")){
                            //miss the whole class, can not sign in
                            Toast.makeText(getActivity(),R.string.miss_cant_sign,Toast.LENGTH_SHORT).show();
                        }
                        else if(jsonObject.getString("result").equals("false")){
                            //get the distance between user and target building location
                            double distance = getDistance(tempLng,currentPoint);
                            Log.e("distance",distance + "");
                            //error margin
                            if(distance < 50){
                                Log.e("50","<");
                            }
                            else{
                                Log.e("50",">");
                            }
                            Toast.makeText(getActivity(),R.string.sign_in_successfully,Toast.LENGTH_SHORT).show();
                            list.remove(pos);
                            listView.setAdapter(new SimpleAdapter(rootView.getContext(),list,R.layout.course_list,new String[]{"course_name", "course_location", "course_time", "course_teacher"},
                                    new int[]{ R.id.course_name, R.id.course_location, R.id.course_time, R.id.course_teacher}));
//                            for(int i = 0; i < listView.getCount(); i++){
//                                listView.getChildAt(i).setBackgroundColor(Color.parseColor("#FFFFFF"));
//                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //button ask4leave
        ask4leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(temp == null){
                    //no select a course
                }
                else {
                    try {
                        JSONObject jsonObject;
                        jsonObject = ask(id, record_id);
                        if(jsonObject.getString("result").equals("success")){
                            //success
                            Toast.makeText(getActivity(),R.string.ask4leave_successfully,Toast.LENGTH_SHORT).show();
                            list.remove(pos);
                            listView.setAdapter(new SimpleAdapter(rootView.getContext(),list,R.layout.course_list,new String[]{"course_name", "course_location", "course_time", "course_teacher"},
                                    new int[]{ R.id.course_name, R.id.course_location, R.id.course_time, R.id.course_teacher}));
//                            for(int i = 0; i < listView.getCount(); i++){
//                                listView.getChildAt(i).setBackgroundColor(Color.parseColor("#FFFFFF"));
//                            }
                        }
                        else{
                            //not permit to ask for leave 10 mins before class begins
                            Toast.makeText(getActivity(),R.string.cant_ask4leave_before_10min,Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        //courses list
        SimpleAdapter simpleAdapter;
        listView = (ListView)rootView.findViewById(R.id.course_list);
        String url = HttpUtil.BASE_URL+"CourseInfoServlet";
        Map<String, String> m = new HashMap<String, String>();
        m.put("id",id);
        try {
            JSONArray jsonArray = new JSONArray(HttpUtil.postRequest(url,m));
            Log.e("JSON", jsonArray.toString());
            for(int i = 0; i<jsonArray.length(); i++){

                Map<String, String> map = new HashMap<String, String>();
                map.put("record_id", jsonArray.getJSONObject(i).getString("record_id"));i++;
                map.put("course_name", jsonArray.getJSONObject(i).getString("course_name"));i++;
                map.put("course_location", jsonArray.getJSONObject(i).optString("course_location"));i++;
                map.put("course_time", jsonArray.getJSONObject(i).getString("course_time"));i++;
                map.put("course_teacher", jsonArray.getJSONObject(i).getString("course_teacher"));
                Log.e("map",map.toString());
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("list",list.toString());
        simpleAdapter = new SimpleAdapter(rootView.getContext(),list,R.layout.course_list,new String[]{"course_name", "course_location", "course_time", "course_teacher"},
                new int[]{ R.id.course_name, R.id.course_location, R.id.course_time, R.id.course_teacher});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for(int i=0;i<parent.getCount();i++) {
                    parent.getChildAt(i).setBackgroundColor(Color.parseColor("#FFFFFF"));
                }

                view.setBackgroundColor(Color.parseColor("#BBFFFF"));
                temp = parent.getItemAtPosition(position).toString();
                Log.e(TAG,parent.getItemAtPosition(position).toString());
                pos = position;

                for(int i = 0 ; i < temp.length() ; i ++ ){
                    if(temp.substring(i,i+10).equals("record_id")){
                        int j;
                        for(j = i + 10;j<temp.length();j++){
                            if(temp.charAt(j) == ',') {
                                record_id = temp.substring(i + 10, j );
                                Log.e("record_id", record_id);

                                break;
                            }
                        }

                    }
                        if(temp.substring(i,i+12).equals("course_time")){
                                int j;
                                for(j = i + 12;j<temp.length();j++){
                                if(temp.charAt(j) == ',') {
                                    course_time = temp.substring(i + 12, j );
                                    Log.e("course_time", course_time);
                                    break;
                                }
                         }

                   }
                }

                HashMap<String, String> hashMap = (HashMap<String, String>)parent.getItemAtPosition(position);
                String location = hashMap.get("course_location");
                Log.e("subString", location.substring(0,1));
                if(location.substring(0,2).equals("A1")){
                    tempLng = A1Data;
                }
                else if(location.substring(0, 2).equals("A2")){
                    tempLng = A2Data;
                }
                else if(location.substring(0, 2).equals("A3")){
                    tempLng = A3Date;
                }
                else if(location.substring(0, 2).equals("A4")){
                    tempLng = A4Data;
                }
                else if(location.substring(0, 2).equals("A5")){
                    tempLng = A5Data;
                }
                else if(location.substring(0, 2).equals("B3")){
                    tempLng = B3Data;
                }
                else if(location.substring(0, 2).equals("B7")){
                    tempLng = GZUCM;
                }
            }
        });


        return rootView;
    }

    private JSONObject query(String id, String record_id, String course_time)throws Exception{
        Map<String, String> map = new HashMap<String, String>();
        map.put("id",id);
        map.put("record_id",record_id);
        Log.e("id",record_id);
        map.put("course_time", course_time);
        String url = HttpUtil.BASE_URL + "CourseAttendServlet";
        return new JSONObject(HttpUtil.postRequest(url, map));
    }

    private JSONObject ask(String id, String record_id)throws Exception{
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        map.put("record_id", record_id);
        String url = HttpUtil.BASE_URL + "LeaveServlet";
        return new JSONObject(HttpUtil.postRequest(url, map));
    }

    BDLocationListener listener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub

            if (location != null && (location.getLocType() == 161 || location.getLocType() == 66)) {
                Message locMsg = locHander.obtainMessage();
                Bundle locData;
                locData = Algorithm(location);
                if (locData != null) {
                    locData.putParcelable("loc", location);
                    locMsg.setData(locData);
                    locHander.sendMessage(locMsg);
                }
            }
        }
    };

    private Bundle Algorithm(BDLocation location) {
        Bundle locData = new Bundle();
        double curSpeed = 0;
        if (locationList.isEmpty() || locationList.size() < 2) {
            LocationEntity temp = new LocationEntity();
            temp.location = location;
            temp.time = System.currentTimeMillis();
            locData.putInt("iscalculate", 0);
            locationList.add(temp);
        } else {
            if (locationList.size() > 5)
                locationList.removeFirst();
            double score = 0;
            for (int i = 0; i < locationList.size(); ++i) {
                LatLng lastPoint = new LatLng(locationList.get(i).location.getLatitude(),
                        locationList.get(i).location.getLongitude());
                LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
                double distance = DistanceUtil.getDistance(lastPoint, curPoint);
                curSpeed = distance / (System.currentTimeMillis() - locationList.get(i).time) / 1000;
                score += curSpeed * Utils.EARTH_WEIGHT[i];
            }
            if (score > 0.00000999 && score < 0.00005) { // 经验值,开发者可根据业务自行调整，也可以不使用这种算法
                location.setLongitude(
                        (locationList.get(locationList.size() - 1).location.getLongitude() + location.getLongitude())
                                / 2);
                location.setLatitude(
                        (locationList.get(locationList.size() - 1).location.getLatitude() + location.getLatitude())
                                / 2);
                locData.putInt("iscalculate", 1);
            } else {
                locData.putInt("iscalculate", 0);
            }
            LocationEntity newLocation = new LocationEntity();
            newLocation.location = location;
            newLocation.time = System.currentTimeMillis();
            locationList.add(newLocation);

        }
        return locData;
    }

    private Handler locHander = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            try {
                BDLocation location = msg.getData().getParcelable("loc");
                int iscal = msg.getData().getInt("iscalculate");
                if (location != null) {
                    LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                    currentPoint = point;
                    Log.e(TAG,"lat: " + location.getLatitude() + " Lon: " + location.getLongitude());
                    // Marker icon
                    BitmapDescriptor bitmap = null;

                    if (mBaiduMap != null) {
                        mBaiduMap.clear();
                    }
                    CircleOptions circle_option = new CircleOptions();
                    circle_option.center(point);
                    circle_option.fillColor(0xAAFFFF00);
                    circle_option.radius(50);
                    mBaiduMap.addOverlay(circle_option);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(point));
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

    };

    //calculate the distance
    public double getDistance(LatLng start,LatLng end){
        double lat1 = (Math.PI/180)*start.latitude;
        double lat2 = (Math.PI/180)*end.latitude;

        double lon1 = (Math.PI/180)*start.longitude;
        double lon2 = (Math.PI/180)*end.longitude;

//      double Lat1r = (Math.PI/180)*(gp1.getLatitudeE6()/1E6);
//      double Lat2r = (Math.PI/180)*(gp2.getLatitudeE6()/1E6);
//      double Lon1r = (Math.PI/180)*(gp1.getLongitudeE6()/1E6);
//      double Lon2r = (Math.PI/180)*(gp2.getLongitudeE6()/1E6);

        //earth radius
        double R = 6371;

        //unit(km)
        double d =  Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;
        //return meter
        return d*1000;
    }

    @Override
    public void onResume(){
        super.onResume();
//        mMapView.setVisibility(View.INVISIBLE);
//        在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
//        mMapView.setVisibility(View.INVISIBLE);
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        locationService.unregisterListener(listener);
        locationService.stop();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();

    }
}
