package com.example.quintoben.sams.utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by quintoben on 2016/3/3.
 */
public class HttpUtil {
    public static HttpClient httpClient = new DefaultHttpClient();
//    public static final String BASE_URL = "http://192.168.43.238:8080/Server/";
    public static final String BASE_URL = "http://192.168.10.105:8080/Server/";
//    public static final String BASE_URL = "http://192.168.1.50:8080/Server/";

    public static String getRequest(final String url) throws Exception{
        FutureTask<String> task = new FutureTask<String>(
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        HttpGet httpGet = new HttpGet(url);
                        HttpResponse httpResponse = httpClient.execute(httpGet);
                        if(httpResponse.getStatusLine().getStatusCode() == 200){
                            String result = EntityUtils.toString(httpResponse.getEntity());
                            return result;
                        }
                        return null;
                    }
                }
        );
        new Thread(task).start();
        return task.get();
    }

    public static String postRequest(final String url, final Map<String, String> rawParams)throws Exception{
        FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                HttpPost httpPost = new HttpPost(url);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                for(String key : rawParams.keySet()){
                    params.add(new BasicNameValuePair(key, rawParams.get(key)));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(params, "gbk"));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                if(httpResponse.getStatusLine().getStatusCode() == 200){
                    String result  = EntityUtils.toString(httpResponse.getEntity());
                    return result;
                }
                return null;
            }
        });
        new Thread(task).start();
        return task.get();
    }
}
