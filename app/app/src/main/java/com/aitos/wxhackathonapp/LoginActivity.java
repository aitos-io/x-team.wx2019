package com.aitos.wxhackathonapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;


import com.aitos.wxhackathonapp.constants.Constants;
import com.aitos.wxhackathonapp.http.HttpConstant;
import com.aitos.wxhackathonapp.http.OkHttpUtils;
import com.aitos.wxhackathonapp.http.request.EidRequest;
import com.aitos.wxhackathonapp.http.request.ReqContent;
import com.aitos.wxhackathonapp.http.request.ReqHeader;
import com.aitos.wxhackathonapp.http.response.EidRespContent;
import com.aitos.wxhackathonapp.http.response.EidResponse;
import com.aitos.wxhackathonapp.http.response.LoginResponse;
import com.aitos.wxhackathonapp.utils.ActivityUtil;
import com.aitos.wxhackathonapp.utils.LogCat;
import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {

    private EditText mUserNameEdit;
    private EditText mUserPwdEdit;

    private String mUserAppId = "";


    private final Handler.Callback mHandlerCallback = new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.HandlerMSG.LOGIN_RESPONSE:
                    dismissProgressDialog();
                    String loginResp = (String) msg.obj;
                    LoginResponse loginResponse = JSON.parseObject(loginResp,
                            LoginResponse.class);
                    if (loginResponse != null) {
                        String respStatus = loginResponse.getStatus();
                        if (respStatus.equals(Constants.RespStatus.SUCCESS)) {
                            String xId = loginResponse.getxId();
                            String xIdStatus = loginResponse.getXidStatus();
                            ActivityUtil.showMainActivity(LoginActivity.this, mUserAppId,
                                    TextUtils.isEmpty(xId) ? "" : xId,
                                    TextUtils.isEmpty(xIdStatus) ? "" : xIdStatus);
                        } else {
                            showToast(R.string.toast_account_login_fail);
                        }
                    } else {
                        showToast(R.string.toast_account_login_fail);
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
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_eid_login).setOnClickListener(this);
        findViewById(R.id.text_register).setOnClickListener(this);
        mUserNameEdit = findViewById(R.id.et_user_name);
        mUserPwdEdit = findViewById(R.id.et_password);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_login:
                final String userName = mUserNameEdit.getText().toString();
                final String userPwd = mUserPwdEdit.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    showToast(getString(R.string.toast_empty_user_name));
                    return;
                }
                if (TextUtils.isEmpty(userPwd)) {
                    showToast(getString(R.string.toast_empty_pwd));
                    return;
                }

                ActivityUtils.readIDCard(this, new FastEidSDK.FastEidCallback() {
                    @Override
                    public void onResult(String resCode, String resMsg) {
                        if (resCode.equals(FastEidSDK.ResCode.SUCCESS)) {
                            showProgressDialog(getString(R.string.notice), getString(R.string.msg_eid_login));
                            mUserAppId = userName;
                            doEidDetectAndLogin(userName, userPwd, resMsg);
                        } else if (resCode.equals(FastEidSDK.ResCode.CANCELED)) {
                            showToast(R.string.toast_canceled);
                        }
                    }
                });
                break;
            case R.id.text_register:
                ActivityUtil.showRegisterActivity(this);
                break;
        }
    }

    private void doEidDetectAndLogin(String userName, String password, String reqId) {
        Map<String, String> params = new HashMap<>();
        params.put("reqID", reqId);
        params.put("appId", userName);
        params.put("password", password);
        LogCat.e("onRequest-LOGIN:" + params.toString());
        OkHttpUtils.getInstance().doPostRequest(HttpConstant.CAR_LEASE_URI.LOGIN, params,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogCat.e("onFailure:" + e.getMessage());
                        mHandler.sendEmptyMessage(Constants.HandlerMSG.LOGIN_FAILURE);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String str = response.body().string();
                        LogCat.e("onResponse:" + str);
                        mHandler.sendMessage(mHandler.obtainMessage(Constants.HandlerMSG.LOGIN_RESPONSE,
                                0, 0, str));
                    }
                });
    }

}
