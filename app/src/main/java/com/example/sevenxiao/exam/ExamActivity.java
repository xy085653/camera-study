package com.example.sevenxiao.exam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
    private static final long ADVANCED_TIME_LIMIT = 15 * 60 * 1000; // 15 minutes

    private AssetDataSource dataSource;
    private List<QuestionModel> questions;
    private ExamResultModel result;
    private int currentIndex = 0;
    private long startTime;
    private String difficulty;
    private CountDownTimer countDownTimer;
    private int questionCount;

    private TextView progressText;
    private TextView timerText;
    private ProgressBar progressBar;
    private ImageView questionImage;
    private TextView questionText;
    private LinearLayout optionsContainer;
    private EditText descriptionInput;
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
            v.setPadding(paddingDp + bars.left, paddingDp + bars.top,
                    paddingDp + bars.right, paddingDp + bars.bottom);
            return insets;
        });

        difficulty = getIntent().getStringExtra(EXTRA_DIFFICULTY);
        if (difficulty == null) difficulty = "basic";

        questionCount = difficulty.equals("advanced") ? 15 : 10;

        dataSource = new AssetDataSource(this);
        List<QuestionModel> all = dataSource.loadQuestions(difficulty);
        questions = dataSource.pickRandomQuestions(all, questionCount);
        result = new ExamResultModel(difficulty, questions);
        startTime = SystemClock.elapsedRealtime();

        progressText = findViewById(R.id.progress_text);
        timerText = findViewById(R.id.timer_text);
        progressBar = findViewById(R.id.progress_bar);
        questionImage = findViewById(R.id.question_image);
        questionText = findViewById(R.id.question_text);
        optionsContainer = findViewById(R.id.options_container);
        descriptionInput = findViewById(R.id.description_input);
        nextBtn = findViewById(R.id.next_btn);

        if (difficulty.equals("advanced")) {
            timerText.setVisibility(View.VISIBLE);
            startTimer();
        }

        nextBtn.setOnClickListener(v -> goToNext());
        showQuestion(0);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(ADVANCED_TIME_LIMIT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                timerText.setText(String.format("⏱ %02d:%02d", minutes, seconds));
                if (millisUntilFinished < 60000) {
                    timerText.setTextColor(0xFFDC2626);
                }
            }

            @Override
            public void onFinish() {
                submitExam();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }

    private void showQuestion(int index) {
        if (index >= questions.size()) return;
        currentIndex = index;
        QuestionModel q = questions.get(index);

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

        questionText.setText(q.getQuestionText());

        // 根据题型显示选项或输入框
        if ("description".equals(q.getType())) {
            optionsContainer.setVisibility(View.GONE);
            descriptionInput.setVisibility(View.VISIBLE);

            String existing = result.getUserAnswers().get(q.getId());
            if (existing != null) {
                descriptionInput.setText(existing);
            } else {
                descriptionInput.setText("");
            }
        } else {
            optionsContainer.setVisibility(View.VISIBLE);
            descriptionInput.setVisibility(View.GONE);
            buildOptions(q);
        }

        if (index == questions.size() - 1) {
            nextBtn.setText("提交");
        } else {
            nextBtn.setText("下一题");
        }
    }

    private void buildOptions(QuestionModel q) {
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
    }

    private void selectOption(int optionIndex) {
        QuestionModel q = questions.get(currentIndex);
        String[] labels = {"A", "B", "C", "D", "E"};
        result.addAnswer(q.getId(), labels[optionIndex]);
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
        QuestionModel q = questions.get(currentIndex);

        // 保存当前题答案
        if ("description".equals(q.getType())) {
            String text = descriptionInput.getText().toString().trim();
            if (text.isEmpty()) return;
            result.addAnswer(q.getId(), text);
        } else {
            if (!result.getUserAnswers().containsKey(q.getId())) return;
        }

        if (currentIndex >= questions.size() - 1) {
            submitExam();
        } else {
            showQuestion(currentIndex + 1);
        }
    }

    private void submitExam() {
        if (countDownTimer != null) countDownTimer.cancel();
        result.setTimeSpent((SystemClock.elapsedRealtime() - startTime) / 1000);
        result.calculateScore();
        startActivity(ExamResultActivity.createIntent(this, result));
        finish();
    }
}
