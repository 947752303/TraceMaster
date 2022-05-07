package com.xyz.tracemaster.ui.about;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.xyz.tracemaster.R;
import com.xyz.tracemaster.utils.VersionUtils;

public class AboutFragment extends Fragment {
    private TextView version, introduce, feedbackButton;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_about, container, false);
        findId(root);
        setOnClickListener();
        introduce.setText(R.string.about_introduce);
        version.setText("当前版本:" + VersionUtils.getVersionCode(requireActivity()));

        return root;
    }

    private void setOnClickListener() {
        feedbackButton.setOnClickListener(v -> JoinEmail());

    }

    @SuppressLint({"IntentReset", "UseCompatLoadingForDrawables"})
    private void JoinEmail() {
        Uri uri = Uri.parse("mailto:947752303@qq.com");
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "“对于<轨迹追踪>的意见反馈"); // 正文
        intent.putExtra(Intent.EXTRA_SUBJECT, "我的建议");

        Toast.makeText(requireContext().getApplicationContext(), "没有找到邮箱软件哦！", Toast.LENGTH_SHORT).show();
    }


    private void findId(View view) {
        version = view.findViewById(R.id.version);
        introduce = view.findViewById(R.id.introduce);
        feedbackButton = view.findViewById(R.id.feedbackButton);

    }

}