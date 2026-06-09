package com.example.sevenxiao.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sevenxiao.R;
import com.example.sevenxiao.auth.LoginActivity;
import com.example.sevenxiao.data.model.UserModel;
import com.example.sevenxiao.data.remote.AuthRepository;
import com.example.sevenxiao.util.UpdateChecker;

public class ProfileFragment extends Fragment {

    private AuthRepository authRepository;
    private View loginPrompt;
    private View userInfoContainer;
    private TextView userName, userEmail, statScore, statExams, statAccuracy;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authRepository = new AuthRepository(requireContext());

        loginPrompt = view.findViewById(R.id.login_prompt);
        userInfoContainer = view.findViewById(R.id.user_info_container);
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);
        statScore = view.findViewById(R.id.stat_score);
        statExams = view.findViewById(R.id.stat_exams);
        statAccuracy = view.findViewById(R.id.stat_accuracy);

        loginPrompt.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), LoginActivity.class)));

        view.findViewById(R.id.check_update_btn).setOnClickListener(v -> {
            String currentVer = getAppVersion();

            AlertDialog loadingDialog = new AlertDialog.Builder(requireContext())
                    .setTitle("检查更新")
                    .setMessage("正在检查...")
                    .setCancelable(false)
                    .show();

            UpdateChecker.checkGithub(requireContext(), currentVer, (hasUpdate, latestVer, downloadUrl, error) -> {
                requireActivity().runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    if (hasUpdate) {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("发现新版本 v" + latestVer)
                                .setMessage("当前版本: v" + currentVer + "\n是否前往下载？")
                                .setPositiveButton("去下载", (d, w) -> {
                                    Intent intent = new Intent(Intent.ACTION_VIEW,
                                            android.net.Uri.parse(downloadUrl));
                                    startActivity(intent);
                                })
                                .setNegativeButton("稍后", null)
                                .show();
                    } else if (error != null) {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("检查失败")
                                .setMessage(error)
                                .setPositiveButton("确定", null)
                                .show();
                    } else {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("已是最新版本")
                                .setMessage("当前版本: v" + currentVer)
                                .setPositiveButton("确定", null)
                                .show();
                    }
                });
            });
        });

        view.findViewById(R.id.about_btn).setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("关于")
                    .setMessage("影像效果工程师看图训练\n\n版本: v" + getAppVersion() + "\n\n用于培养影像效果工程师的看图能力，涵盖过曝、欠曝、鬼影、噪点等多种影像缺陷的识别与分析。")
                    .setPositiveButton("确定", null)
                    .show();
        });

        view.findViewById(R.id.logout_btn).setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("退出登录")
                    .setMessage("确定要退出登录吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        authRepository.logout();
                        startActivity(new Intent(requireContext(), LoginActivity.class));
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private String getAppVersion() {
        try {
            return requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "1.0";
        }
    }

    private void updateUI() {
        if (authRepository == null) return;

        if (authRepository.isLoggedIn()) {
            UserModel user = authRepository.getCurrentUser();
            if (user != null) {
                loginPrompt.setVisibility(View.GONE);
                userInfoContainer.setVisibility(View.VISIBLE);
                userName.setText(user.getDisplayName());
                userEmail.setText(user.getEmail());
                statScore.setText(String.valueOf((int) user.getTotalScore()));
                statExams.setText(String.valueOf(user.getTotalExams()));
                statAccuracy.setText(String.format("%d%%", (int) (user.getAvgAccuracy() * 100)));
            }
        } else {
            loginPrompt.setVisibility(View.VISIBLE);
            userInfoContainer.setVisibility(View.GONE);
        }
    }
}
