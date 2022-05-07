package com.xyz.tracemaster.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.xyz.tracemaster.R;
import com.xyz.tracemaster.app.Constant;
import com.xyz.tracemaster.utils.PreferencesUtils;


public class LoginFragment extends Fragment {
    private DrawerLayout mDrawerLayout;
    private Button bt_logo;
    private Button userItem;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        findID(root);
        // 禁止侧滑打开抽屉
        mDrawerLayout = requireActivity().findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        onclick();

        return root;


    }

    private void findID(View view) {
        bt_logo = view.findViewById(R.id.bt_logo);
        userItem = view.findViewById(R.id.userItem);

    }

    /**
     * 强制隐藏软键盘
     *
     * @param activity
     */
    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {

        }

    }

    private void onclick() {
        bt_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 键盘隐藏
                hideSoftKeyboard(requireActivity());

                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_nav_login_to_nav_home);

                PreferencesUtils.putBoolean(Constant.FIRST_LOGO, true);

                // 允许侧滑打开抽屉
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                // 显示Toolbar
                ((AppCompatActivity) requireActivity()).getSupportActionBar().show();
            }
        });
        //点击用户协议，弹出对话框
        userItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("用户条款")
                        .setMessage(R.string.user_item)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
//                        dialog.dismiss();
                            }
                        }).create().show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // 隐藏toolbar 实现全屏
        @SuppressWarnings("ConstantConditions")
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }
    }

}