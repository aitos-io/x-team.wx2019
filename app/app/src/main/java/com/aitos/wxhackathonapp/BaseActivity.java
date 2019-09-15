package com.aitos.wxhackathonapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected ProgressDialog mProgressDialog;
    protected Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }

    protected void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == mToast) {
                    mToast = Toast.makeText(BaseActivity.this, text, Toast.LENGTH_LONG);
                } else {
                    mToast.setText(text);
                }
                mToast.show();
            }
        });
    }

    protected void showToast(final int resId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == mToast) {
                    mToast = Toast.makeText(BaseActivity.this, getString(resId), Toast.LENGTH_LONG);
                } else {
                    mToast.setText(resId);
                }
                mToast.show();
            }
        });
    }

    protected void showMessageDialog(final String title, final String btnText,
                                     final String message,
                                     final DialogInterface.OnClickListener listener) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(BaseActivity.this).setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(btnText, listener).create().show();
            }
        });
    }

    protected void showMessageDialog(final String title, final String okBtnText,
                                     final String cancelBtnText, final String message,
                                     final DialogInterface.OnClickListener okListener,
                                     final DialogInterface.OnClickListener cancelListener) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(BaseActivity.this).setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(okBtnText, okListener).setNeutralButton(cancelBtnText,
                        cancelListener).create().show();
            }
        });
    }

    protected void showProgressDialog(final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == mProgressDialog) {
                    mProgressDialog = new ProgressDialog(BaseActivity.this);
                    mProgressDialog.setTitle(title);
                    mProgressDialog.setMessage(message);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                }
            }
        });
    }

    protected void showProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == mProgressDialog) {
                    mProgressDialog = new ProgressDialog(BaseActivity.this);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                }
            }
        });
    }

    protected void updateProgressTitle(final String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != mProgressDialog) {
                    mProgressDialog.setTitle(title);
                }
            }
        });
    }

    protected void updateProgressMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != mProgressDialog) {
                    mProgressDialog.setMessage(message);
                }
            }
        });
    }

    protected void dismissProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != mProgressDialog) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        });
    }
}
