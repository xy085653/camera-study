package com.example.sevenxiao.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.sevenxiao.R;
import com.example.sevenxiao.data.model.SampleModel;

public class SampleDetailActivity extends AppCompatActivity {

    private static final String EXTRA_SAMPLE_ID = "extra_sample_id";
    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_CATEGORY = "extra_category";
    private static final String EXTRA_DESCRIPTION = "extra_description";

    public static void start(Context context, SampleModel sample) {
        Intent intent = new Intent(context, SampleDetailActivity.class);
        intent.putExtra(EXTRA_SAMPLE_ID, sample.getSampleId());
        intent.putExtra(EXTRA_TITLE, sample.getTitle());
        intent.putExtra(EXTRA_CATEGORY, sample.getCategory());
        intent.putExtra(EXTRA_DESCRIPTION, sample.getDescription());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sample_detail);

        final int contentPadding = (int) (16 * getResources().getDisplayMetrics().density);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    contentPadding + systemBars.left,
                    contentPadding + systemBars.top,
                    contentPadding + systemBars.right,
                    contentPadding + systemBars.bottom
            );
            return insets;
        });

        ImageView imageView = findViewById(R.id.sample_image);
        TextView titleView = findViewById(R.id.sample_title);
        TextView categoryView = findViewById(R.id.category_chip);
        TextView descView = findViewById(R.id.sample_description);

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String category = getIntent().getStringExtra(EXTRA_CATEGORY);
        String description = getIntent().getStringExtra(EXTRA_DESCRIPTION);

        titleView.setText(title);
        categoryView.setText(category);
        descView.setText(description);

        // 使用 Glide 从 assets 加载图片
        Glide.with(this)
                .load("file:///android_asset/" + "samples/overexposure_01.jpg")
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.darker_gray)
                .into(imageView);
    }
}
