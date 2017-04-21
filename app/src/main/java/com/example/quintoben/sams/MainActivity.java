package com.example.quintoben.sams;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.quintoben.sams.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieStore;
import java.util.ArrayList;
import java.util.LinkedList;


public class MainActivity extends FragmentActivity {

    //viewpager
    private ViewPager viewPager;
    private ArrayList<Fragment> fragmentList;
    private ImageButton[] imageButtons;

    public Bundle bundle;

    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private Locate locate=null;
    private LocationService locationService;
    private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>(); // 存放历史定位结果的链表，最大存放当前结果的前5次定位结果
    HttpClient httpClient;
    class LocationEntity {
        BDLocation location;
        long time;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);


        Intent intent = getIntent();
        bundle = intent.getExtras();
        Log.e("MainBundle",bundle.toString());


        imageButtons = new ImageButton[3];
        imageButtons[0] = (ImageButton)findViewById(R.id.person);
        imageButtons[1] = (ImageButton)findViewById(R.id.attend);
        imageButtons[2] = (ImageButton)findViewById(R.id.message);
        for(int i = 0;i < 3;i++){
            imageButtons[i].setOnClickListener(new ButtonListener(i));
        }
        imageButtons[0].setBackgroundColor(Color.parseColor("#CCCCCC"));
        imageButtons[1].setBackgroundColor(Color.parseColor("#FFF68F"));
        imageButtons[2].setBackgroundColor(Color.parseColor("#CCCCCC"));


        httpClient = new DefaultHttpClient();
        initViewPager();


    }

    public class ButtonListener implements View.OnClickListener{
        private int index=0;

        public ButtonListener(int i) {
            index =i;
        }
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Log.e("click","click");
            viewPager.setCurrentItem(index);
            for(int i = 0; i < 3; i++){
                if(i == index){
                    imageButtons[i].setBackgroundColor(Color.parseColor("#FFF68F"));
                }
                else{
                    imageButtons[i].setBackgroundColor(Color.parseColor("#CCCCCC"));
                }
            }
        }
    }

    public void initViewPager(){
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        fragmentList = new ArrayList<Fragment>();
        Fragment attendFragment = new AttendFragment();
        attendFragment.setArguments(bundle);
        Fragment personInfoFragment = new PersonInfoFragment();
        personInfoFragment.setArguments(bundle);
        Fragment messageBoxFragment = new MessageBoxFragment();
        messageBoxFragment.setArguments(bundle);
        fragmentList.add(personInfoFragment);
        fragmentList.add(attendFragment);
        fragmentList.add(messageBoxFragment);

        //set adapter
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                 return fragmentList.size();
            }


        });

        viewPager.setCurrentItem(1);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i < 3; i++){
                    if(i == position){
                        imageButtons[i].setBackgroundColor(Color.parseColor("#FFF68F"));
                    }
                    else{
                        imageButtons[i].setBackgroundColor(Color.parseColor("#CCCCCC"));
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == 2){

                }

            }
        });

    }


    protected void onDestroy() {
        super.onDestroy();
//        locationService.unregisterListener(listener);
//        locationService.stop();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
//        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
//        mMapView.onResume();
//        reset.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                if (mBaiduMap != null)
//                    mBaiduMap.clear();
//            }
//        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
//        mMapView.onPause();
    }

}
