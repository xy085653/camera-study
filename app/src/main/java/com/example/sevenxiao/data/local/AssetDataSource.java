package com.example.sevenxiao.data.local;

import android.content.Context;

import com.example.sevenxiao.data.model.QuestionModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssetDataSource {

    private final Context context;

    public AssetDataSource(Context context) {
        this.context = context.getApplicationContext();
    }

    /** 从 assets 读取 JSON 文件 */
    private String readAssetFile(String path) {
        try (InputStream is = context.getAssets().open(path)) {
            byte[] buffer = new byte[is.available()];
            int read = is.read(buffer);
            if (read > 0) {
                return new String(buffer, 0, read, "UTF-8");
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** 根据难度加载题目 */
    public List<QuestionModel> loadQuestions(String difficulty) {
        String fileName = "questions/" + difficulty + ".json";
        String json = readAssetFile(fileName);
        return parseQuestions(json);
    }

    /** 解析 JSON 为题目列表 */
    private List<QuestionModel> parseQuestions(String json) {
        List<QuestionModel> questions = new ArrayList<>();
        if (json == null) return questions;

        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                QuestionModel q = new QuestionModel();
                q.setId(obj.getString("id"));
                q.setType(obj.getString("type"));
                q.setCategory(obj.optString("category", ""));
                q.setDifficulty(obj.optString("difficulty", "basic"));
                q.setImageAsset(obj.optString("imageAsset", ""));
                q.setQuestionText(obj.getString("questionText"));

                JSONArray optArray = obj.getJSONArray("options");
                String[] options = new String[optArray.length()];
                for (int j = 0; j < optArray.length(); j++) {
                    options[j] = optArray.getString(j);
                }
                q.setOptions(options);
                q.setCorrectAnswer(obj.getString("correctAnswer"));
                q.setExplanation(obj.optString("explanation", ""));
                questions.add(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }

    /** 从题库随机抽取指定数量的题目 */
    public List<QuestionModel> pickRandomQuestions(List<QuestionModel> all, int count) {
        List<QuestionModel> shuffled = new ArrayList<>(all);
        Collections.shuffle(shuffled);
        int n = Math.min(count, shuffled.size());
        return shuffled.subList(0, n);
    }
}
