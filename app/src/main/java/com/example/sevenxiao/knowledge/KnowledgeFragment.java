package com.example.sevenxiao.knowledge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sevenxiao.R;
import com.example.sevenxiao.data.model.KnowledgeEntry;
import com.example.sevenxiao.knowledge.adapter.KnowledgeAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class KnowledgeFragment extends Fragment {

    private RecyclerView recyclerView;
    private KnowledgeAdapter adapter;
    private List<KnowledgeEntry> entries = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_knowledge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.knowledge_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new KnowledgeAdapter(entries);
        recyclerView.setAdapter(adapter);

        loadKnowledgeBase();
    }

    private void loadKnowledgeBase() {
        try (InputStream is = requireContext().getAssets().open("knowledge/knowledge_base.json")) {
            byte[] buffer = new byte[is.available()];
            int read = is.read(buffer);
            if (read <= 0) return;

            String json = new String(buffer, 0, read, "UTF-8");
            JSONArray array = new JSONArray(json);
            entries.clear();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                KnowledgeEntry entry = new KnowledgeEntry(
                        obj.getString("id"),
                        obj.getString("title"),
                        obj.getString("icon"),
                        obj.getString("summary")
                );
                entry.setDescription(obj.optString("description", ""));
                entry.setCauses(obj.optString("causes", ""));
                entry.setIdentification(obj.optString("identification", ""));
                entry.setSolution(obj.optString("solution", ""));
                entries.add(entry);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
