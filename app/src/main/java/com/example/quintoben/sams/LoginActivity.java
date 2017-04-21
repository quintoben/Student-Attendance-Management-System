package com.example.quintoben.sams;

import android.app.Activity;
import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.os.Handler;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.quintoben.sams.utils.HttpUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class LoginActivity extends Activity {

    private static String TAG = "LoginActivity";
    private HttpClient httpClient;
    private Button login;
    private Button forget_password;
    private RadioButton student;
    private RadioButton teacher;
    private RadioButton manager;
    private EditText id;
    private EditText password;
    private RadioGroup radioGroup;
    private String type = "student";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        httpClient = new DefaultHttpClient();

        login = (Button)findViewById(R.id.login);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        forget_password = (Button)findViewById(R.id.forget_password);
        student = (RadioButton)findViewById(R.id.student);
        teacher = (RadioButton)findViewById(R.id.teacher);
        manager = (RadioButton)findViewById(R.id.manager);
        id = (EditText)findViewById(R.id.id);
        password = (EditText)findViewById(R.id.password);

        //login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject;
                try {
                    if(id.getText().toString().equals("")){
                        Toast.makeText(LoginActivity.this,R.string.id_empty,Toast.LENGTH_SHORT).show();
                    }
                    else if(password.getText().toString().equals("")){
                        Toast.makeText(LoginActivity.this,R.string.psw_empty,Toast.LENGTH_SHORT).show();
                    }
                    else{
                        jsonObject = query(id.getText().toString(), password.getText().toString(), type);
                        Log.e("json", jsonObject.toString());
                        if (jsonObject.getString("LOGIN_FLAG").equals("success")) {

                            Intent intent = new Intent();
                            if (type == "student") {
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                            } else if (type == "teacher") {
                                intent = new Intent(LoginActivity.this, TeacherMainActivity.class);
                            } else if(type == "manager"){
                                intent = new Intent(LoginActivity.this, ManagerActivity.class);
                            }
                            Bundle data = new Bundle();
                            data.putString("id", id.getText().toString());
                            intent.putExtras(data);
                            Log.e("data", data.toString());
                            id.setText("");
                            password.setText("");
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this,R.string.check_id_psd,Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //forget password
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);

//                intent.putExtra("id",id.getText().toString());
                startActivity(intent);

            }
        });

        //use radio group to choose type
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.student){
                    type = "student";
                }
                else if(checkedId == R.id.teacher){
                    type = "teacher";
                }
                else if(checkedId == R.id.manager){
                    type = "manager";
                }
            }
        });
    }

    //validate
    private JSONObject query(String id, String password, String type)throws Exception{
        Map<String, String> map = new HashMap<String, String>();
        map.put("id",id);
        map.put("password",password);
        map.put("type", type);
        String url = HttpUtil.BASE_URL + "LoginServlet";
        return new JSONObject(HttpUtil.postRequest(url, map));
    }


}
