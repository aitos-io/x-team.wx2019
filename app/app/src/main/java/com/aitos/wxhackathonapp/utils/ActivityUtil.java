package com.aitos.wxhackathonapp.utils;

import android.content.Context;
import android.content.Intent;

import com.aitos.wxhackathonapp.MainActivity;
import com.aitos.wxhackathonapp.RegisterActivity;
import com.aitos.wxhackathonapp.constants.Constants;


public class ActivityUtil {

    public static void showRegisterActivity(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    public static void showMainActivity(Context context, String userAppId, String xId,
                                        String xIdStatus) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.Key.USER_APP_ID, userAppId);
        intent.putExtra(Constants.Key.XID, xId);
        intent.putExtra(Constants.Key.XID_STATUS, xIdStatus);
        context.startActivity(intent);
    }


}

