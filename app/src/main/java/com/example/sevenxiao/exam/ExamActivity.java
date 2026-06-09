package com.example.sevenxiao.exam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.sevenxiao.R;
import com.example.sevenxiao.data.local.AssetDataSource;
import com.example.sevenxiao.data.model.ExamResultModel;
import com.example.sevenxiao.data.model.QuestionModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ExamActivity extends AppCompatActivity {

    private static final String EXTRA_DIFFICULTY = "extra_difficulty";

    private AssetDataSource dataSource;
    private List<QuestionModel> questions;
    private ExamResultModel result;
    private int currentIndex = 0;
    private long startTime;

    private TextView progressText;
    private ProgressBar progressBar;
    private ImageView questionImage;
    private TextView questionText;
    private LinearLayout optionsContainer;
    private MaterialButton nextBtn;
    private View[] optionViews;

    public static Intent createIntent(Context context, String difficulty) {
        Intent intent = new Intent(context, ExamActivity.class);
        intent.putExtra(EXTRA_DIFFICULTY, difficulty);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exam);

        final int paddingDp = (int) (16 * getResources().getDisplayMetrics().density);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.exam_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    paddingDp + bars.left,
                    paddingDp + bars.top,
                    paddingDp + bars.right,
                    paddingDp + bars.bottom
            );
            return insets;
        });

        String difficulty = getIntent().getStringExtra(EXTRA_DIFFICULTY);
        if (difficulty == null) difficulty = "basic";

        dataSource = new AssetDataSource(this);
        List<QuestionModel> all = dataSource.loadQuestions(difficulty);
        questions = dataSource.pickRandomQuestions(all, 10);
        result = new ExamResultModel(difficulty, questions);
        startTime = SystemClock.elapsedRealtime();

        progressText = findViewById(R.id.progress_text);
        progressBar = findViewById(R.id.progress_bar);
        questionImage = findViewById(R.id.question_image);
        questionText = findViewById(R.id.question_text);
        optionsContainer = findViewById(R.id.options_container);
        nextBtn = findViewById(R.id.next_btn);

        nextBtn.setOnClickListener(v -> goToNext());
        showQuestion(0);
    }

    private void showQuestion(int index) {
        if (index >= questions.size()) return;
        currentIndex = index;
        QuestionModel q = questions.get(index);

        // 更新进度
        progressText.setText("第 " + (index + 1) + "/" + questions.size() + " 题");
        progressBar.setProgress((index + 1) * 100 / questions.size());

        // 加载图片
        String assetPath = q.getImageAsset();
        if (assetPath != null && !assetPath.isEmpty()) {
            Glide.with(this)
                    .load("file:///android_asset/" + assetPath)
                    .placeholder(android.R.color.darker_gray)
                    .error(android.R.color.darker_gray)
                    .into(questionImage);
            questionImage.setVisibility(View.VISIBLE);
        } else {
            questionImage.setVisibility(View.GONE);
        }

        // 显示题干
        questionText.setText(q.getQuestionText());

        // 动态创建选项布局（使用统一模板，ABCD 始终对齐）
        optionsContainer.removeAllViews();
        String[] options = q.getOptions();
        optionViews = new View[options.length];
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < options.length; i++) {
            final int optionIndex = i;
            View optionView = inflater.inflate(R.layout.item_exam_option, optionsContainer, false);
            TextView labelView = optionView.findViewById(R.id.option_label);
            TextView textView = optionView.findViewById(R.id.option_text);
            labelView.setText(String.valueOf((char) ('A' + i)));
            textView.setText(options[i]);
            optionView.setOnClickListener(v -> selectOption(optionIndex));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = 12;
            optionsContainer.addView(optionView, lp);
            optionViews[i] = optionView;
        }

        // 检查是否已有答案（旋转恢复等场景）
        String existingAnswer = result.getUserAnswers().get(q.getId());
        if (existingAnswer != null) {
            String[] labels = {"A", "B", "C", "D", "E"};
            for (int i = 0; i < options.length; i++) {
                if (labels[i].equals(existingAnswer)) {
                    highlightOption(i);
                    break;
                }
            }
        }

        // 最后一题切换按钮文字
        if (index == questions.size() - 1) {
            nextBtn.setText("提交");
        } else {
            nextBtn.setText("下一题");
        }
    }

    private void selectOption(int optionIndex) {
        QuestionModel q = questions.get(currentIndex);
        String[] labels = {"A", "B", "C", "D", "E"};
        String answer = labels[optionIndex];
        result.addAnswer(q.getId(), answer);
        highlightOption(optionIndex);
    }

    private void highlightOption(int optionIndex) {
        for (int i = 0; i < optionViews.length; i++) {
            View root = optionViews[i];
            TextView label = root.findViewById(R.id.option_label);
            TextView text = root.findViewById(R.id.option_text);
            if (i == optionIndex) {
                root.setBackgroundResource(R.drawable.chip_selected);
                label.setTextColor(0xFFFFFFFF);
                text.setTextColor(0xFFFFFFFF);
            } else {
                root.setBackgroundResource(R.drawable.chip_unselected);
                label.setTextColor(0xFF374151);
                text.setTextColor(0xFF374151);
            }
        }
    }

    private void goToNext() {
        // 检查当前题是否已作答
        QuestionModel q = questions.get(currentIndex);
        if (!result.getUserAnswers().containsKey(q.getId())) {
            return;
        }

        if (currentIndex >= questions.size() - 1) {
            // 最后一道题，提交
            result.setTimeSpent((SystemClock.elapsedRealtime() - startTime) / 1000);
            result.calculateScore();
            startActivity(ExamResultActivity.createIntent(this, result));
            finish();
        } else {
            showQuestion(currentIndex + 1);
        }
    }
}
