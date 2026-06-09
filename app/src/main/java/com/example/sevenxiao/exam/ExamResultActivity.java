package com.example.sevenxiao.exam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sevenxiao.R;
import com.example.sevenxiao.data.model.ExamResultModel;
import com.example.sevenxiao.data.model.QuestionModel;

public class ExamResultActivity extends AppCompatActivity {

    public static Intent createIntent(Context context, ExamResultModel result) {
        Intent intent = new Intent(context, ExamResultActivity.class);
        intent.putExtra("score", result.getScore());
        intent.putExtra("correctCount", result.getCorrectCount());
        intent.putExtra("totalCount", result.getTotalCount());
        intent.putExtra("difficulty", result.getDifficulty());
        intent.putExtra("timeSpent", result.getTimeSpent());

        String[] questionTexts = new String[result.getQuestions().size()];
        String[] correctAnswers = new String[result.getQuestions().size()];
        String[] explanations = new String[result.getQuestions().size()];
        String[] userAnswers = new String[result.getQuestions().size()];

        for (int i = 0; i < result.getQuestions().size(); i++) {
            QuestionModel q = result.getQuestions().get(i);
            questionTexts[i] = q.getQuestionText();
            correctAnswers[i] = q.getCorrectAnswer();
            explanations[i] = q.getExplanation();
            String ua = result.getUserAnswers().get(q.getId());
            userAnswers[i] = ua != null ? ua : "";
        }

        intent.putExtra("questionTexts", questionTexts);
        intent.putExtra("correctAnswers", correctAnswers);
        intent.putExtra("explanations", explanations);
        intent.putExtra("userAnswers", userAnswers);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exam_result);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.result_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        int score = getIntent().getIntExtra("score", 0);
        int correctCount = getIntent().getIntExtra("correctCount", 0);
        int totalCount = getIntent().getIntExtra("totalCount", 0);
        long timeSpent = getIntent().getLongExtra("timeSpent", 0);
        String difficulty = getIntent().getStringExtra("difficulty");

        String[] questionTexts = getIntent().getStringArrayExtra("questionTexts");
        String[] correctAnswers = getIntent().getStringArrayExtra("correctAnswers");
        String[] explanations = getIntent().getStringArrayExtra("explanations");
        String[] userAnswers = getIntent().getStringArrayExtra("userAnswers");

        // 显示分数
        TextView scoreText = findViewById(R.id.score_text);
        scoreText.setText(String.valueOf(score));

        // 显示统计
        TextView statsText = findViewById(R.id.stats_text);
        String timeStr = String.format("%d分%d秒", timeSpent / 60, timeSpent % 60);
        statsText.setText("✅ 正确 " + correctCount + "/" + totalCount + "    ⏱ " + timeStr);

        // 显示解析
        LinearLayout reviewContainer = findViewById(R.id.review_container);
        if (questionTexts != null && correctAnswers != null) {
            for (int i = 0; i < questionTexts.length; i++) {
                String userAns = userAnswers != null && i < userAnswers.length ? userAnswers[i] : "";
                boolean isCorrect = userAns.equals(correctAnswers[i]);

                String resultIcon = isCorrect ? "✅" : "❌";
                String reviewText = resultIcon + "  " + questionTexts[i] + "\n"
                        + "你的答案: " + userAns + "    正确答案: " + correctAnswers[i] + "\n"
                        + (explanations != null && i < explanations.length ? "解析: " + explanations[i] : "");

                TextView tv = new TextView(this);
                tv.setText(reviewText);
                tv.setTextSize(14);
                tv.setLineSpacing(4, 1);
                tv.setPadding(16, 16, 16, 16);

                if (isCorrect) {
                    tv.setBackgroundResource(R.drawable.chip_unselected);
                    tv.setTextColor(0xFF374151);
                } else {
                    tv.setBackgroundResource(R.drawable.chip_selected);
                    tv.setTextColor(0xFFFFFFFF);
                }

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.bottomMargin = 12;
                reviewContainer.addView(tv, lp);
            }
        }

        // 按钮
        findViewById(R.id.retry_btn).setOnClickListener(v -> {
            startActivity(ExamActivity.createIntent(this, difficulty));
            finish();
        });

        findViewById(R.id.home_btn).setOnClickListener(v -> finish());
    }
}
