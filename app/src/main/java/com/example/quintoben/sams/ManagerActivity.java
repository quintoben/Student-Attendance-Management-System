package com.example.quintoben.sams;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quintoben.sams.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ManagerActivity extends Activity {

    private static String TAG = "ManagerActivity";
    private EditText id;
    private TextView info ;
    private Button search ;
    private RadioGroup radioGroup ;
    private String type = "1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        id = (EditText)findViewById(R.id.id);

        info = (TextView)findViewById(R.id.info);
        search = (Button)findViewById(R.id.search);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!id.getText().toString().equals("")) {
                    JSONObject jsonObject = new JSONObject();
//                Log.e("jsonObject",jsonObject.toString());

                    try {
                        String url = HttpUtil.BASE_URL + "ManagerServlet";
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("id", id.getText().toString());
                        map.put("type", type);

//                Log.e("jsonObject",jsonObject.toString());
                        jsonObject = new JSONObject(HttpUtil.postRequest(url, map));
                        if (jsonObject.toString().equals("{}")) {
                            info.setText(R.string.no_result_pls_check_id);

                        } else {
//                Log.e("jsonObject",jsonObject.toString());
                            info.setText(R.string.name+": " + jsonObject.getString("name")
                                    + "\n" + R.string.password+": " + jsonObject.getString("person_password")
                                    + "\n" + R.string.tel+": " + jsonObject.getString("person_phone"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(ManagerActivity.this,R.string.pls_enter_id,Toast.LENGTH_SHORT).show();
                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.student){
                    type = "1";
                }
                if(checkedId == R.id.teacher){
                    type = "2";
                }
            }
        });

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_manager, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
