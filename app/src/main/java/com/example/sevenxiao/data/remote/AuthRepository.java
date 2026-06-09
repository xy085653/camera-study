package com.example.sevenxiao.data.remote;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.sevenxiao.data.model.UserModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.UUID;

public class AuthRepository {
    private static final String PREFS_NAME = "sevenxiao_auth";
    private static final String KEY_USERS = "users_json";
    private static final String KEY_CURRENT_UID = "current_uid";

    private final SharedPreferences prefs;

    public AuthRepository(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** 注册新用户，返回 null 表示成功，否则返回错误信息 */
    public String register(String email, String password, String displayName) {
        try {
            String usersJson = prefs.getString(KEY_USERS, "[]");
            JSONArray users = new JSONArray(usersJson);

            // 检查邮箱是否已存在
            for (int i = 0; i < users.length(); i++) {
                if (users.getJSONObject(i).getString("email").equals(email)) {
                    return "该邮箱已注册";
                }
            }

            // 创建新用户
            JSONObject newUser = new JSONObject();
            newUser.put("uid", UUID.randomUUID().toString());
            newUser.put("email", email);
            newUser.put("passwordHash", hashPassword(password));
            newUser.put("displayName", displayName);
            newUser.put("avatarUrl", "");
            newUser.put("createdAt", System.currentTimeMillis());
            newUser.put("totalScore", 0);
            newUser.put("totalExams", 0);
            newUser.put("totalQuestions", 0);
            newUser.put("avgAccuracy", 0);
            newUser.put("rank", 0);

            users.put(newUser);
            prefs.edit().putString(KEY_USERS, users.toString()).apply();
            return null; // 成功
        } catch (Exception e) {
            return "注册失败: " + e.getLocalizedMessage();
        }
    }

    /** 登录，返回 null 表示成功，否则返回错误信息 */
    public String login(String email, String password) {
        try {
            String usersJson = prefs.getString(KEY_USERS, "[]");
            JSONArray users = new JSONArray(usersJson);

            String hash = hashPassword(password);
            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                if (user.getString("email").equals(email)) {
                    if (user.getString("passwordHash").equals(hash)) {
                        // 保存当前登录用户
                        prefs.edit().putString(KEY_CURRENT_UID, user.getString("uid")).apply();
                        return null; // 成功
                    } else {
                        return "密码错误";
                    }
                }
            }
            return "该邮箱未注册";
        } catch (Exception e) {
            return "登录失败: " + e.getLocalizedMessage();
        }
    }

    /** 退出登录 */
    public void logout() {
        prefs.edit().remove(KEY_CURRENT_UID).apply();
    }

    /** 获取当前用户数据 */
    public UserModel getCurrentUser() {
        String currentUid = prefs.getString(KEY_CURRENT_UID, null);
        if (currentUid == null) return null;

        try {
            String usersJson = prefs.getString(KEY_USERS, "[]");
            JSONArray users = new JSONArray(usersJson);
            for (int i = 0; i < users.length(); i++) {
                JSONObject obj = users.getJSONObject(i);
                if (obj.getString("uid").equals(currentUid)) {
                    UserModel user = new UserModel(
                            obj.getString("uid"),
                            obj.getString("email"),
                            obj.getString("displayName")
                    );
                    user.setAvatarUrl(obj.optString("avatarUrl", ""));
                    user.setCreatedAt(obj.optLong("createdAt", 0));
                    user.setTotalScore(obj.optDouble("totalScore", 0));
                    user.setTotalExams(obj.optInt("totalExams", 0));
                    user.setTotalQuestions(obj.optInt("totalQuestions", 0));
                    user.setAvgAccuracy(obj.optDouble("avgAccuracy", 0));
                    user.setRank(obj.optInt("rank", 0));
                    return user;
                }
            }
        } catch (Exception ignored) { }
        return null;
    }

    /** 检查是否已登录 */
    public boolean isLoggedIn() {
        return prefs.contains(KEY_CURRENT_UID);
    }

    /** 获取当前用户 UID */
    public String getCurrentUid() {
        return prefs.getString(KEY_CURRENT_UID, null);
    }

    /** 密码哈希（SHA-256） */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            return password; // fallback（不会发生）
        }
    }
}
