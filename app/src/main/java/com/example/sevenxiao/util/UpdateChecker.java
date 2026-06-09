package com.example.sevenxiao.util;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    // 改成你的仓库地址
    private static final String GITHUB_API = "https://api.github.com/repos/sevenxiao/study-project/releases/latest";
    private static final String GITEE_API = "https://gitee.com/api/v5/repos/sevenxiao/study-project/releases/latest";

    public interface UpdateCallback {
        void onResult(boolean hasUpdate, String latestVersion, String downloadUrl, String error);
    }

    /** 检查 GitHub 最新 release */
    public static void checkGithub(Context context, String currentVersion, UpdateCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(GITHUB_API);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int code = conn.getResponseCode();
                if (code == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject json = new JSONObject(response.toString());
                    String latestVersion = json.getString("tag_name").replace("v", "");
                    String downloadUrl = json.optString("html_url", "");

                    boolean hasUpdate = !currentVersion.equals(latestVersion);
                    callback.onResult(hasUpdate, latestVersion, downloadUrl, null);
                } else {
                    callback.onResult(false, null, null, "检查失败: " + code);
                }
            } catch (Exception e) {
                callback.onResult(false, null, null, e.getLocalizedMessage());
            }
        }).start();
    }

    /** 检查 Gitee 最新 release（备选） */
    public static void checkGitee(Context context, String currentVersion, UpdateCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(GITEE_API);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int code = conn.getResponseCode();
                if (code == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject json = new JSONObject(response.toString());
                    String latestVersion = json.getString("tag_name").replace("v", "");
                    String downloadUrl = json.optString("html_url", "");

                    boolean hasUpdate = !currentVersion.equals(latestVersion);
                    callback.onResult(hasUpdate, latestVersion, downloadUrl, null);
                } else {
                    callback.onResult(false, null, null, "检查失败: " + code);
                }
            } catch (Exception e) {
                callback.onResult(false, null, null, e.getLocalizedMessage());
            }
        }).start();
    }
}
