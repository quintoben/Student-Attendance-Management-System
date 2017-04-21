package com.example.quintoben.sams;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.quintoben.sams.utils.HttpUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ForgetPasswordActivity extends Activity {

    private ListView listView;
    private List<Map<String,String>> list = new ArrayList<Map<String,String>>();

    private SimpleAdapter simpleAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        listView = (ListView)findViewById(R.id.managerlist);
        String url = HttpUtil.BASE_URL+"ManagerInfoServlet";
        try {
            JSONArray jsonArray = new JSONArray(HttpUtil.getRequest(url));
            Log.e("JSON", jsonArray.toString());
            for(int i = 0; i<jsonArray.length(); i++){

                Map<String, String> map = new HashMap<String, String>();
                map.put("name", jsonArray.getJSONObject(i).getString("name"));
                i++;
                map.put("phone", jsonArray.getJSONObject(i).optString("phone"));
                i++;
                map.put("address", jsonArray.getJSONObject(i).getString("address"));
                Log.e("map",map.toString());
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("list",list.toString());
        simpleAdapter = new SimpleAdapter(this,list,R.layout.data_list,new String[]{"name", "phone", "address"}, new int[]{ R.id.name, R.id.phone, R.id.address});
        listView.setAdapter(simpleAdapter);


    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_forget_password, menu);
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
