package com.aitos.wxhackathonapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aitos.wxhackathonapp.constants.Constants;
import com.aitos.wxhackathonapp.http.HttpConstant;
import com.aitos.wxhackathonapp.http.OkHttpUtils;
import com.aitos.wxhackathonapp.http.request.EidRequest;
import com.aitos.wxhackathonapp.http.request.ReqContent;
import com.aitos.wxhackathonapp.http.request.ReqHeader;
import com.aitos.wxhackathonapp.http.response.BookCarResponse;
import com.aitos.wxhackathonapp.http.response.EidRespContent;
import com.aitos.wxhackathonapp.http.response.EidResponse;
import com.aitos.wxhackathonapp.utils.LogCat;
import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    private static final int OPERATOR_BOOK_CAR = 1;
    private static final int OPERATOR_START_CAR = 2;
    private static final int OPERATOR_FINISH_CAR = 3;

    private int mNextOperatorId = OPERATOR_BOOK_CAR;

    private String mUserAppId = "";
    private String mBookContractId = "";
    private String mBookContractIdStatus = "";

    private TextView mCarStatusText;
    private Button mOperatorBtn;

    private final Handler.Callback mHandlerCallback = new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.HandlerMSG.CAR_BOOK_FAILURE:
                    dismissProgressDialog();
                    showToast(R.string.toast_book_car_fail);
                    break;
                case Constants.HandlerMSG.CAR_BOOK_RESPONSE:
                    dismissProgressDialog();
                    String bookCarResp = (String) msg.obj;
                    try {
                        BookCarResponse bookCarResponse = JSON.parseObject(bookCarResp,
                                BookCarResponse.class);
                        if (bookCarResponse != null) {
                            showToast(R.string.toast_book_car_success);
                            mBookContractId = bookCarResponse.getxId();
//                            bookCarResponse.getVinNumber();
//                            bookCarResponse.getNumberPlate();
//                            bookCarResponse.getVehiclePosition();
//                            bookCarResponse.getVehicleStatus();

                            // TODO
                            // if book return success
                            // update status
                            mNextOperatorId = OPERATOR_START_CAR;
                            updateView();
                        } else {
                            showToast(R.string.toast_book_car_fail);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast(R.string.toast_book_car_fail);
                    }
                    break;
                case Constants.HandlerMSG.START_CAR_FAILURE:
                    dismissProgressDialog();
                    showToast(R.string.toast_start_use_car_fail);
                    break;
                case Constants.HandlerMSG.START_CAR_RESPONSE:
                    dismissProgressDialog();
                    String startCarResp = (String) msg.obj;
                    if (startCarResp.equals(Constants.Contract.STATUS_INUSE)) {
                        showToast(R.string.toast_start_use_car_success);

                        // TODO
                        // if start return success
                        // update status
                        mNextOperatorId = OPERATOR_FINISH_CAR;
                        updateView();
                    } else {
                        showToast(R.string.toast_start_use_car_fail);
                    }
                    break;
                case Constants.HandlerMSG.STOP_CAR_FAILURE:
                    dismissProgressDialog();
                    showToast(R.string.toast_start_use_car_fail);
                    break;
                case Constants.HandlerMSG.STOP_CAR_RESPONSE:
                    dismissProgressDialog();
                    String stopCarResp = (String) msg.obj;
                    if (stopCarResp.equals(Constants.Contract.STATUS_RETURNED)) {
                        showToast(R.string.toast_stop_use_car_success);

                        // TODO
                        // if finish return success
                        // update status
                        mNextOperatorId = OPERATOR_BOOK_CAR;
                        updateView();
                    } else {
                        showToast(R.string.toast_stop_use_car_fail);
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
        setContentView(R.layout.activity_main);

        initView();

        // init status
        mUserAppId = getIntent().getStringExtra(Constants.Key.USER_APP_ID);
        mBookContractId = getIntent().getStringExtra(Constants.Key.XID);
        mBookContractIdStatus = getIntent().getStringExtra(Constants.Key.XID_STATUS);
        if (mBookContractIdStatus.equals(Constants.Contract.STATUS_BOOKED)) {
            mNextOperatorId = OPERATOR_START_CAR;
        } else if (mBookContractIdStatus.equals(Constants.Contract.STATUS_INUSE)) {
            mNextOperatorId = OPERATOR_FINISH_CAR;
        } else {
            mNextOperatorId = OPERATOR_BOOK_CAR;
        }
        updateView();
    }

    private void initView(){
        mCarStatusText = findViewById(R.id.text_car_status);
        mOperatorBtn = findViewById(R.id.btn_operator);
        mOperatorBtn.setOnClickListener(this);
    }

    private void updateText(String text){
//        mCarStatusText.setText(getString(R.string.label_car_status_init));
//        mCarStatusText.setText(getString(R.string.label_car_status_booked));
//        mCarStatusText.setText(getString(R.string.label_car_status_using));
        mCarStatusText.setText(text);
    }

    private void updateView(){
        switch (mNextOperatorId) {
            case OPERATOR_BOOK_CAR:
                updateText(getString(R.string.label_car_status_init));
                mOperatorBtn.setText(getString(R.string.btn_book_car));
                break;
            case OPERATOR_START_CAR:
                updateText(getString(R.string.label_car_status_booked));
                mOperatorBtn.setText(getString(R.string.btn_start_car));
                break;
            case OPERATOR_FINISH_CAR:
                updateText(getString(R.string.label_car_status_using));
                mOperatorBtn.setText(getString(R.string.btn_finish_car));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_operator:
                switch (mNextOperatorId) {
                    case OPERATOR_BOOK_CAR:
					final String resMsg = "1234567890";
					doEidDetectAndBookCar(resMsg);
                        break;
                    case OPERATOR_START_CAR:
                        if(TextUtils.isEmpty(mBookContractId)){
                            showToast(getString(R.string.toast_empty_book_contract));
                            return;
                        }
						doStartUseCar(mBookContractId);
                        break;
                    case OPERATOR_FINISH_CAR:
                        if(TextUtils.isEmpty(mBookContractId)){
                            showToast(getString(R.string.toast_empty_book_contract));
                            return;
                        }
						doStopUseCar(mBookContractId);
                        break;
                }
                break;
        }
    }
	

    private void doEidDetectAndBookCar(String reqId){
        // TODO request book car
        Map<String, String> params = new HashMap<>();
        params.put("reqID", reqId);
        params.put("appId", mUserAppId);
        params.put("vinNumber", TestConfig.VIN_NUM);
        LogCat.e("onRequest-BOOK_CAR:" + params.toString());
        OkHttpUtils.getInstance().doPostRequest(HttpConstant.CAR_LEASE_URI.BOOK_CAR, params,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogCat.e("onFailure:" + e.getMessage());
                        mHandler.sendEmptyMessage(Constants.HandlerMSG.CAR_BOOK_FAILURE);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String str = response.body().string();
                        LogCat.e("onResponse:" + str);
                        mHandler.sendMessage(mHandler.obtainMessage(Constants.HandlerMSG.CAR_BOOK_RESPONSE,
                                0, 0, str));
                    }
                });
    }

    private void doStartUseCar(String contractId){
        // TODO request start car
        Map<String, String> params = new HashMap<>();
        params.put("xId", contractId);
        params.put("tBox", "01");//00 使用模拟; 01 使用tBox
        LogCat.e("onRequest-START_USER_CAR:" + params.toString());
        OkHttpUtils.getInstance().doPostRequest(HttpConstant.CAR_LEASE_URI.START_USER_CAR, params,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogCat.e("onFailure:" + e.getMessage());
                        mHandler.sendEmptyMessage(Constants.HandlerMSG.START_CAR_FAILURE);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String str = response.body().string();
                        LogCat.e("onResponse:" + str);
                        mHandler.sendMessage(mHandler.obtainMessage(Constants.HandlerMSG.START_CAR_RESPONSE,
                                0, 0, str));
                    }
                });
    }

    private void doStopUseCar(String contractId){
        // TODO request finish car
        Map<String, String> params = new HashMap<>();
        params.put("xId", contractId);
        LogCat.e("onRequest-STOP_USER_CAR:" + params.toString());
        OkHttpUtils.getInstance().doPostRequest(HttpConstant.CAR_LEASE_URI.STOP_USER_CAR, params,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogCat.e("onFailure:" + e.getMessage());
                        mHandler.sendEmptyMessage(Constants.HandlerMSG.STOP_CAR_FAILURE);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String str = response.body().string();
                        LogCat.e("onResponse:" + str);
                        mHandler.sendMessage(mHandler.obtainMessage(Constants.HandlerMSG.STOP_CAR_RESPONSE,
                                0, 0, str));
                    }
                });
    }

}
