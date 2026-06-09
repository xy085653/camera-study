package com.example.sevenxiao.exam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sevenxiao.R;

public class ExamHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exam_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.basic_card).setOnClickListener(v ->
                startActivity(ExamActivity.createIntent(requireContext(), "basic")));

        view.findViewById(R.id.advanced_card).setOnClickListener(v ->
                startActivity(ExamActivity.createIntent(requireContext(), "advanced")));
    }
}
