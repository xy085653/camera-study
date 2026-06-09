package com.example.sevenxiao.gallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.bumptech.glide.Glide;
import com.example.sevenxiao.R;
import com.example.sevenxiao.data.model.SampleModel;

public class SampleDetailActivity extends AppCompatActivity {

    private static final String EXTRA_SAMPLE_ID = "extra_sample_id";
    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_CATEGORY = "extra_category";
    private static final String EXTRA_DESCRIPTION = "extra_description";
    private static final String EXTRA_TYPE = "extra_type";
    private static final String EXTRA_FILE = "extra_file";

    private ExoPlayer player;
    private PlayerView playerView;

    public static void start(Context context, SampleModel sample) {
        Intent intent = new Intent(context, SampleDetailActivity.class);
        intent.putExtra(EXTRA_SAMPLE_ID, sample.getSampleId());
        intent.putExtra(EXTRA_TITLE, sample.getTitle());
        intent.putExtra(EXTRA_CATEGORY, sample.getCategory());
        intent.putExtra(EXTRA_DESCRIPTION, sample.getDescription());
        intent.putExtra(EXTRA_TYPE, sample.getType());
        intent.putExtra(EXTRA_FILE, sample.getLocalAsset());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sample_detail);

        final int padding = (int) (16 * getResources().getDisplayMetrics().density);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(padding + bars.left, padding + bars.top,
                    padding + bars.right, padding + bars.bottom);
            return insets;
        });

        ImageView imageView = findViewById(R.id.sample_image);
        playerView = findViewById(R.id.player_view);
        TextView titleView = findViewById(R.id.sample_title);
        TextView categoryView = findViewById(R.id.category_chip);
        TextView descView = findViewById(R.id.sample_description);
        TextView formatBadge = findViewById(R.id.format_badge);
        TextView fileNameView = findViewById(R.id.file_name);

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String category = getIntent().getStringExtra(EXTRA_CATEGORY);
        String description = getIntent().getStringExtra(EXTRA_DESCRIPTION);
        String type = getIntent().getStringExtra(EXTRA_TYPE);
        String file = getIntent().getStringExtra(EXTRA_FILE);

        titleView.setText(title);
        categoryView.setText(category);
        descView.setText(description);
        if (file != null) {
            fileNameView.setText(file.replace("samples/", ""));
        }

        if ("video".equals(type) && file != null) {
            // 视频：使用 ExoPlayer
            imageView.setVisibility(View.GONE);
            playerView.setVisibility(View.VISIBLE);
            formatBadge.setVisibility(View.VISIBLE);
            formatBadge.setText("MP4");

            player = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(player);

            Uri videoUri = Uri.parse("file:///android_asset/" + file);
            MediaItem mediaItem = MediaItem.fromUri(videoUri);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        } else {
            // 图片：使用 Glide
            imageView.setVisibility(View.VISIBLE);
            playerView.setVisibility(View.GONE);
            formatBadge.setVisibility(View.VISIBLE);
            formatBadge.setText("JPG");

            String assetPath = file != null ? file : "samples/overexposure_01.jpg";
            Glide.with(this)
                    .load("file:///android_asset/" + assetPath)
                    .placeholder(android.R.color.darker_gray)
                    .error(android.R.color.darker_gray)
                    .into(imageView);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
