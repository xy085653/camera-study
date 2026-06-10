package com.example.sevenxiao.data.remote;

import android.content.Context;

import com.example.sevenxiao.data.model.SampleModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ApiDataSource {

    // 改成你的极空间地址
    private static final String BASE_URL = "https://remote-access-18080.zconnect.cn";

    private final Context context;

    public ApiDataSource(Context context) {
        this.context = context.getApplicationContext();
    }

    /** 检查后端是否可达 */
    public boolean isReachable() {
        try {
            URL url = new URL(BASE_URL + "/api/version/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    /** 从后端加载样图列表 */
    public List<SampleModel> fetchSamples() {
        List<SampleModel> samples = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL + "/api/samples/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) return samples;

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray results = json.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject obj = results.getJSONObject(i);
                SampleModel sample = new SampleModel(
                        obj.getString("sid"),
                        obj.getString("title"),
                        obj.getString("type"),
                        obj.optString("category", ""),
                        obj.optString("file", ""),
                        obj.optString("description", ""),
                        "" // localAsset 为空，使用远端 URL
                );
                sample.setStorageUrl(obj.optString("file", ""));
                samples.add(sample);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return samples;
    }

    /** 获取完整的图片/视频 URL */
    public static String getMediaUrl(String path) {
        if (path.startsWith("http")) return path;
        return BASE_URL + path;
    }
}
