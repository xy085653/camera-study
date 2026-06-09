package com.example.sevenxiao.knowledge.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sevenxiao.R;
import com.example.sevenxiao.data.model.KnowledgeEntry;

import java.util.List;

public class KnowledgeAdapter extends RecyclerView.Adapter<KnowledgeAdapter.ViewHolder> {

    private List<KnowledgeEntry> entries;
    private int expandedPosition = -1;

    public KnowledgeAdapter(List<KnowledgeEntry> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_knowledge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        KnowledgeEntry entry = entries.get(position);
        holder.icon.setText(entry.getIcon());
        holder.title.setText(entry.getTitle());
        holder.summary.setText(entry.getSummary());

        boolean isExpanded = position == expandedPosition;
        holder.detailContainer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        if (isExpanded) {
            holder.descText.setText(entry.getDescription());
            holder.causesText.setText(entry.getCauses());
            holder.identText.setText(entry.getIdentification());
            holder.solutionText.setText(entry.getSolution());
        }

        holder.itemView.setOnClickListener(v -> {
            if (expandedPosition == position) {
                expandedPosition = -1;
            } else {
                expandedPosition = position;
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView icon, title, summary;
        View detailContainer;
        TextView descText, causesText, identText, solutionText;

        ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.knowledge_icon);
            title = itemView.findViewById(R.id.knowledge_title);
            summary = itemView.findViewById(R.id.knowledge_summary);
            detailContainer = itemView.findViewById(R.id.knowledge_detail);
            descText = itemView.findViewById(R.id.knowledge_desc);
            causesText = itemView.findViewById(R.id.knowledge_causes);
            identText = itemView.findViewById(R.id.knowledge_ident);
            solutionText = itemView.findViewById(R.id.knowledge_solution);
        }
    }
}
