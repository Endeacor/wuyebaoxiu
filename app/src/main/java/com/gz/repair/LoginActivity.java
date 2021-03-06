package com.gz.repair;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.google.gson.Gson;
import com.gz.repair.bean.CidBean;
import com.gz.repair.bean.Login;
import com.gz.repair.utils.T;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    @Bind(R.id.username)
    EditText mUser;
    @Bind(R.id.password)
    EditText pwd;
    @Bind(R.id.sign_in_button)
    Button sign_in;
    @Bind(R.id.progressBar)
    CircleProgressBar progressBar;
    SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        config = getSharedPreferences("config", MODE_PRIVATE);

    }


    @OnClick(R.id.sign_in_button)
    public void onClick() {
        attemptLogin();
    }

    private void attemptLogin() {
        // Store values at the time of the login attempt.
        String user = mUser.getText().toString().trim();
        String password = pwd.getText().toString().trim();

        if (TextUtils.isEmpty(user)) {
            mUser.setError("用户名不能为空");

        }

        if (TextUtils.isEmpty(password)) {
            pwd.setError("密码不能为空");
            return;
        }

        login(user, password);

//        showDialog();

    }

    private void login(final String user, final String password) {
        Log.e("my", "开始请求");
        progressBar.setVisibility(View.VISIBLE);
        String url = MyAppcation.baseUrl + "/repair_login";
        RequestParams params = new RequestParams(url);

        params.addBodyParameter("login", user);
        params.addBodyParameter("password", password);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e("my", "onSuccess" + result);
                try {
                    JSONObject jo = new JSONObject(result);
                    int ret = jo.getInt("ret");
//                    T.showShort(LoginActivity.this, "jo:ret=="+ret);
                    String msg = jo.getString("msg");
//                    T.showShort(LoginActivity.this, "jo:msg=="+msg);
                    if (ret == 1) {
                        showDialog();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                Login login = gson.fromJson(result, Login.class);
//                Log.e("my", "login.toString==" + login.toString());
                if (login.ret == 0) {

                    config.edit().putString("user", user).commit();
                    config.edit().putString("password", password).commit();


                    T.showShort(LoginActivity.this, login.msg);
                    ArrayList<Login.Result.Privileges> privileges = login.result.privileges;
                    MyAppcation.pro.clear();
                    MyAppcation.pro.addAll(privileges);

                    MyAppcation.userId = login.result.user_id;
                    MyAppcation.rootId = login.result.root_id;
                    MyAppcation.userName = login.result.user_name;
                    config.edit().putInt("userId", login.result.user_id).commit();
                    config.edit().putInt("rootId", login.result.root_id).commit();
                    config.edit().putString("userName", login.result.user_name).commit();
//                    Log.e("is==", LoginActivity.this.getSharedPreferences("config", MODE_PRIVATE).getBoolean("isSuccess", true) + "");
                    if (MyAppcation.userId != -1 && MyAppcation.clientid != null) {

//                        Log.e("is==", LoginActivity.this.getSharedPreferences("config", MODE_PRIVATE).getBoolean("isSuccess", true) + "");
                        subCid();
                    }

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("my", "onError" + ex.toString());
                Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e("my", "onCancelled" + cex.toString());
            }

            @Override
            public void onFinished() {
                Log.e("my", "onFinished");
                progressBar.setVisibility(View.GONE);
            }

        });

    }

    private void subCid() {
        Log.e("my", "开始请求");

        String url = MyAppcation.baseUrl + "/generator_clientid";
        RequestParams params = new RequestParams(url);
        params.addParameter("user_id", MyAppcation.userId);
        params.addParameter("clientid", MyAppcation.clientid);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e("my", "onSuccess" + result);
                Gson gson = new Gson();
                CidBean c = gson.fromJson(result, CidBean.class);

//                if (c.ret == 0) {
//                    LoginActivity.this.getSharedPreferences("config", MODE_PRIVATE).edit().putBoolean("isSuccess", false).commit();
////                    Log.e("is==", LoginActivity.this.getSharedPreferences("config", MODE_PRIVATE).getBoolean("isSuccess", true) + "");
//                }
                Toast.makeText(LoginActivity.this, "cid" + c.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("my", "onError" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e("my", "onCancelled" + cex.toString());
            }

            @Override
            public void onFinished() {
                Log.e("my", "onFinished");
            }

        });
    }


    private void showDialog() {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.content(
                "用户名或密码错误，请重新输入。")
                .btnNum(1)
                .btnText("确定")//
                .title("提示")
//                .contentTextColor(Color.parseColor("#0097A7"))
                .titleTextColor(Color.parseColor("#006064"))
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {//left btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                }
        );
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            // 返回键最小化程序
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            startActivity(intent);
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }

}

