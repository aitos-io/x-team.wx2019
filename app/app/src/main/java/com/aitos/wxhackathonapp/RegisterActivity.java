package com.aitos.wxhackathonapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aitos.wxhackathonapp.constants.Constants;
import com.aitos.wxhackathonapp.http.HttpConstant;
import com.aitos.wxhackathonapp.http.OkHttpUtils;
import com.aitos.wxhackathonapp.utils.LogCat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity {

    private EditText mUserNameEdit;
    private EditText mPwdEdit;
    private EditText mConfirmPwdEdit;

    private final Handler.Callback mHandlerCallback = new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.HandlerMSG.REGISTER_FAILURE:
                    dismissProgressDialog();
                    showToast(R.string.toast_account_register_fail);
                    break;
                case Constants.HandlerMSG.REGISTER_RESPONSE:
                    dismissProgressDialog();
                    String regResp = (String) msg.obj;
                    //注册成功
                    //已经注册
                    //注册失败
                    if (regResp.equals("注册成功")) {
                        showToast(R.string.toast_account_register_success);
                        finish();
                    } else {
                        //showToast(R.string.toast_account_register_fail);
                        showToast(regResp);
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    };
    private final Handler mHandler = new Handler(mHandlerCallback);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
    }

    private void initView() {
        findViewById(R.id.nav_btn_back).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
        ((TextView) findViewById(R.id.nav_text_title)).setText(getString(R.string.btn_register));
        mUserNameEdit = findViewById(R.id.et_user_name);
        mPwdEdit = findViewById(R.id.et_pwd);
        mConfirmPwdEdit = findViewById(R.id.et_confirm_pwd);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.nav_btn_back:
                finish();
                break;
            case R.id.btn_register:
                final String userName = mUserNameEdit.getText().toString();
                final String pwd = mPwdEdit.getText().toString();
                final String confirmPwd = mConfirmPwdEdit.getText().toString();
                if(TextUtils.isEmpty(userName)){
                    showToast(getString(R.string.toast_empty_user_name));
                    return;
                }
                if(TextUtils.isEmpty(pwd)){
                    showToast(getString(R.string.toast_empty_pwd));
                    return;
                }
                if(TextUtils.isEmpty(confirmPwd)){
                    showToast(getString(R.string.toast_empty_confirm_pwd));
                    return;
                }
                if(!confirmPwd.equals(pwd)){
                    showToast(getString(R.string.toast_pwd_not_equel));
                    return;
                }


                final String resMsg = "1234567890";
				doEidDetectAndReg(userName, confirmPwd, resMsg);
                break;
        }
    }

    private void doEidDetectAndReg(String userName, String password, String reqId) {
        Map<String, String> params = new HashMap<>();
        params.put("reqID", reqId);
        params.put("appId", userName);
        params.put("password", password);
        LogCat.e("onRequest-REGISTER:" + params.toString());
        OkHttpUtils.getInstance().doPostRequest(HttpConstant.CAR_LEASE_URI.REGISTER, params,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogCat.e("onFailure:" + e.getMessage());
                        mHandler.sendEmptyMessage(Constants.HandlerMSG.REGISTER_FAILURE);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String str = response.body().string();
                        LogCat.e("onResponse:" + str);
                        mHandler.sendMessage(mHandler.obtainMessage(Constants.HandlerMSG.REGISTER_RESPONSE,
                                0, 0, str));
                    }
                });
    }

}
