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

        allSamples.clear();
        allSamples.addAll(getBuiltinSamples());
        filteredSamples.clear();
        filteredSamples.addAll(allSamples);

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

    private List<SampleModel> getBuiltinSamples() {
        List<SampleModel> samples = new ArrayList<>();
        samples.add(new SampleModel("1", "过曝_日景", "image", "过曝",
                "overexposure_01", "高光区域细节丢失，天空过曝成白色", "samples/overexposure_01.jpg"));
        samples.add(new SampleModel("2", "过曝_夜景", "image", "过曝",
                "overexposure_02", "灯光过曝导致高光溢出", "samples/overexposure_02.jpg"));
        samples.add(new SampleModel("3", "鬼影_逆光", "image", "鬼影",
                "ghost_01", "逆光拍摄产生镜头鬼影", "samples/ghost_01.jpg"));
        samples.add(new SampleModel("4", "炫光_夜景", "image", "炫光",
                "flare_01", "强光源引起炫光条纹", "samples/flare_01.jpg"));
        samples.add(new SampleModel("5", "噪点_暗光", "image", "噪点",
                "noise_01", "低光环境ISO过高导致噪点", "samples/noise_01.jpg"));
        samples.add(new SampleModel("6", "模糊_运动", "image", "模糊",
                "blur_01", "运动模糊导致细节丢失", "samples/blur_01.jpg"));
        samples.add(new SampleModel("7", "偏色_室内", "image", "偏色",
                "colorcast_01", "白平衡不准导致偏黄", "samples/colorcast_01.jpg"));
        samples.add(new SampleModel("8", "欠曝_逆光", "image", "欠曝",
                "underexposure_01", "逆光主体欠曝，暗部细节丢失", "samples/underexposure_01.jpg"));
        // 视频样张（将 .mp4 文件放入 assets/samples/ 即可生效）
        samples.add(new SampleModel("9", "夜景_噪点", "video", "视频",
                "night_noise.mp4", "夜景视频中高ISO导致的动态噪点", "samples/night_noise.mp4"));
        samples.add(new SampleModel("10", "运动_模糊", "video", "视频",
                "motion_blur.mp4", "快速运动物体在视频中的拖影", "samples/motion_blur.mp4"));
        return samples;
    }
}
