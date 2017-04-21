package com.example.quintoben.sams;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quintoben.sams.utils.HttpUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by quintoben on 2016/3/26.
 */
public class TeacherInfoFragment extends Fragment {

    private static String TAG = "TeacherInfoFragment";
    private TextView name;
    private TextView person_id;
    private TextView person_phone;
    private TextView person_password;
    private Button edit_phone;
    private Button edit_password;
    String id;
    String phone;
    String password;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.teacher_info_fragment, container, false);//关联布局文件

        name = (TextView)rootView.findViewById(R.id.name);
        person_id = (TextView)rootView.findViewById(R.id.person_id);
        person_phone = (TextView)rootView.findViewById(R.id.person_phone);

        person_password = (TextView)rootView.findViewById(R.id.person_password);
        edit_phone = (Button)rootView.findViewById(R.id.edit_phone);

        edit_password = (Button)rootView.findViewById(R.id.edit_password);

        Bundle bundle = this.getArguments();
        id = bundle.getString("id");

        init();

        edit_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout = new LinearLayout(rootView.getContext());
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                final EditText editText = new EditText(rootView.getContext());
                editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                editText.setHint(R.string.tel);
                editText.setTextSize(14);
                linearLayout.addView(editText);

                AlertDialog alertDialog = new AlertDialog.Builder(rootView.getContext())
                        .setTitle(R.string.edit_tel)
                        .setView(linearLayout)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                phone = editText.getText().toString();
                                if (phone.equals("")) {
                                    Toast.makeText(getActivity(),R.string.tel_empty,Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        JSONObject jsonObject;
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("phone", phone);
                                        map.put("id", id);
                                        String url = HttpUtil.BASE_URL + "EditTeacherPhoneServlet";
                                        jsonObject = new JSONObject(HttpUtil.postRequest(url, map));
                                        if (jsonObject.getString("result").equals("success")) {
                                            Log.e(TAG, "success");
                                            person_phone.setText(phone);
                                        } else {
                                            Log.e(TAG, "fail");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    dialog.cancel();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editText.setText("");
                                dialog.cancel();
                            }
                        })
                        .create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

            }
        });

        edit_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout = new LinearLayout(rootView.getContext());
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                final EditText editText = new EditText(rootView.getContext());
                editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                editText.setHint(R.string.password);
                editText.setTextSize(14);
                linearLayout.addView(editText);

                AlertDialog alertDialog = new AlertDialog.Builder(rootView.getContext())
                        .setTitle(R.string.edit_password)
                        .setView(linearLayout)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                password = editText.getText().toString();
                                if (password.equals("")) {
                                    Toast.makeText(getActivity(),R.string.psw_empty,Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        JSONObject jsonObject;
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("password", password);
                                        map.put("id", id);
                                        String url = HttpUtil.BASE_URL + "EditTeacherPasswordServlet";
                                        jsonObject = new JSONObject(HttpUtil.postRequest(url, map));
                                        if (jsonObject.getString("result").equals("success")) {
                                            Log.e("edit_password", "success");
                                            person_password.setText(password);
                                        } else {
                                            Log.e("edit_password", "fail");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    dialog.cancel();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editText.setText("");
                                dialog.cancel();
                            }
                        })
                        .create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

            }
        });

        return rootView;
    }

    private void init(){
        try {
            JSONObject jsonObject;
            jsonObject = getInfo();
            Log.e("jsoninfo", jsonObject.toString());
            name.setText(jsonObject.getString("name"));
            person_id.setText(id);
            person_phone.setText(jsonObject.getString("person_phone"));

            person_password.setText("******");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private JSONObject getInfo()throws Exception{
        String url = HttpUtil.BASE_URL + "TeacherInfoServlet";
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        return new JSONObject(HttpUtil.postRequest(url, map));
    }
}
