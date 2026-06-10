package com.example.sevenxiao.gallery;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sevenxiao.R;
import com.example.sevenxiao.data.model.SampleModel;
import com.example.sevenxiao.data.remote.ApiDataSource;
import com.example.sevenxiao.gallery.adapter.CategoryChipAdapter;
import com.example.sevenxiao.gallery.adapter.SampleAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SampleGalleryFragment extends Fragment {

    private SampleAdapter adapter;
    private final List<SampleModel> allSamples = new ArrayList<>();
    private final List<SampleModel> filteredSamples = new ArrayList<>();
    private String currentCategory = "全部";
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 尝试从后端 API 加载样图，失败则用本地数据
        loadSamples();

        // 搜索框
        EditText searchBar = view.findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                searchQuery = s.toString().trim().toLowerCase();
                applyFilters();
            }
        });

        // 分类标签
        List<String> categories = new ArrayList<>(Arrays.asList(
                "全部", "过曝", "欠曝", "鬼影", "炫光", "噪点", "模糊", "偏色", "视频"));
        RecyclerView chipRecycler = view.findViewById(R.id.category_chips);
        chipRecycler.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        CategoryChipAdapter chipAdapter = new CategoryChipAdapter(categories, (category, position) -> {
            currentCategory = category;
            applyFilters();
        });
        chipRecycler.setAdapter(chipAdapter);

        // 样图网格
        RecyclerView sampleGrid = view.findViewById(R.id.sample_grid);
        sampleGrid.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        adapter = new SampleAdapter(filteredSamples, sample ->
                SampleDetailActivity.start(requireContext(), sample));
        sampleGrid.setAdapter(adapter);
    }

    /** 加载样图：优先从后端 API，失败则回退到本地数据 */
    private void loadSamples() {
        new Thread(() -> {
            ApiDataSource api = new ApiDataSource(requireContext());
            List<SampleModel> remote = api.fetchSamples();
            if (!remote.isEmpty()) {
                requireActivity().runOnUiThread(() -> {
                    allSamples.clear();
                    allSamples.addAll(remote);
                    filteredSamples.clear();
                    filteredSamples.addAll(remote);
                    adapter.notifyDataSetChanged();
                });
            } else {
                requireActivity().runOnUiThread(() -> {
                    allSamples.clear();
                    filteredSamples.clear();
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }

    private void applyFilters() {
        filteredSamples.clear();

        // 按分类筛选
        List<SampleModel> categoryFiltered;
        if ("全部".equals(currentCategory)) {
            categoryFiltered = new ArrayList<>(allSamples);
        } else {
            categoryFiltered = allSamples.stream()
                    .filter(s -> s.getCategory().equals(currentCategory))
                    .collect(Collectors.toList());
        }

        // 按搜索关键词筛选
        if (searchQuery.isEmpty()) {
            filteredSamples.addAll(categoryFiltered);
        } else {
            for (SampleModel s : categoryFiltered) {
                if (s.getTitle().toLowerCase().contains(searchQuery)
                        || s.getFileName().toLowerCase().contains(searchQuery)
                        || s.getDescription().toLowerCase().contains(searchQuery)) {
                    filteredSamples.add(s);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

}
