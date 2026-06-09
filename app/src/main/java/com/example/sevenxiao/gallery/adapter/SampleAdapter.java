package com.example.sevenxiao.gallery.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sevenxiao.R;
import com.example.sevenxiao.data.model.SampleModel;

import java.util.List;

public class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.ViewHolder> {

    private List<SampleModel> samples;
    private OnSampleClickListener listener;

    public interface OnSampleClickListener {
        void onSampleClick(SampleModel sample);
    }

    public SampleAdapter(List<SampleModel> samples, OnSampleClickListener listener) {
        this.samples = samples;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sample, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SampleModel sample = samples.get(position);
        holder.title.setText(sample.getFileName());
        holder.category.setText(sample.getCategory());
        holder.category.setVisibility(View.VISIBLE);

        // 使用 Glide 从 assets 加载图片
        Glide.with(holder.thumbnail.getContext())
                .load("file:///android_asset/" + sample.getLocalAsset())
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.darker_gray)
                .centerCrop()
                .into(holder.thumbnail);

        holder.card.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSampleClick(sample);
            }
        });
    }

    @Override
    public int getItemCount() {
        return samples.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        ImageView thumbnail;
        TextView title;
        TextView category;

        ViewHolder(View itemView) {
            super(itemView);
            card = (CardView) itemView;
            thumbnail = itemView.findViewById(R.id.sample_thumbnail);
            title = itemView.findViewById(R.id.sample_title);
            category = itemView.findViewById(R.id.sample_category);
        }
    }
}
