package com.example.quintoben.sams;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;


public class TeacherMainActivity extends FragmentActivity {

    private static String TAG = "TeacherMainActivity";

    //viewpager
    private ViewPager viewPager;
    private ArrayList<Fragment> fragmentList;
    private ImageButton[] imageButtons;
    HttpClient httpClient;
    public Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);
        Intent intent = getIntent();
        bundle = intent.getExtras();
        Log.e("MainBundle", bundle.toString());


        imageButtons = new ImageButton[4];
        imageButtons[0] = (ImageButton)findViewById(R.id.person);
        imageButtons[1] = (ImageButton)findViewById(R.id.attend);
        imageButtons[2] = (ImageButton)findViewById(R.id.manage);
        imageButtons[3] = (ImageButton)findViewById(R.id.message);
        for(int i = 0;i < 4;i++){
            imageButtons[i].setOnClickListener(new ButtonListener(i));
        }
        imageButtons[0].setBackgroundColor(Color.parseColor("#CCCCCC"));
        imageButtons[1].setBackgroundColor(Color.parseColor("#FFF68F"));
        imageButtons[2].setBackgroundColor(Color.parseColor("#CCCCCC"));
        imageButtons[3].setBackgroundColor(Color.parseColor("#CCCCCC"));

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
            for(int i = 0; i < 4; i++){
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
        final Fragment setCourseFragment = new SetCoursesFragment();
        setCourseFragment.setArguments(bundle);
        Fragment teacherInfoFragment = new TeacherInfoFragment();
        teacherInfoFragment.setArguments(bundle);
        Fragment manageFragment = new ManageFragment();
        manageFragment.setArguments(bundle);
        Fragment teacherMessageBoxFragment = new TeacherMessageBoxFragment();
        teacherMessageBoxFragment.setArguments(bundle);
        fragmentList.add(teacherInfoFragment);
        fragmentList.add(setCourseFragment);
        fragmentList.add(manageFragment);
        fragmentList.add(teacherMessageBoxFragment);


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

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }
        });

        viewPager.setCurrentItem(1);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i < 4; i++){
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

}
