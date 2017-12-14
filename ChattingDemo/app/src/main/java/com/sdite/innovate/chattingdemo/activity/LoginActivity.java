package com.sdite.innovate.chattingdemo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sdite.innovate.chattingdemo.R;
import com.sdite.innovate.chattingdemo.module.UserData;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private Button login_button;
    private Button cancel_button;
    private TextView sign_up_button;

    private EditText user_name_edit;
    private EditText password_edit;

    private CheckBox save_password;
    private CheckBox auto_login;

    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;

    public static boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("登录界面");
        ActivityCollector.addActivity(this);

        LitePal.getDatabase();              // 创建用户数据库
        preferences = getSharedPreferences("data", MODE_PRIVATE);
//        Log.i(TAG, "onCreate: "+preferences.getString("username", ""));
//        Log.i(TAG, "onCreate: " + preferences.getString("password", ""));
//        Log.i(TAG, "onCreate: "+preferences.getBoolean("remember_password", false));
//        Log.i(TAG, "onCreate: "+preferences.getBoolean("auto_login", false));

        List<UserData> datas = DataSupport.findAll(UserData.class);

        List<UserData> judge = DataSupport.where("userName = ?", "admin").find(UserData.class);
        if (judge.isEmpty()) {
            // 没有管理员账户则创建
            UserData admin = new UserData();
            admin.setUserName("admin");
            admin.setPassword("123456");
            admin.save();
        }

        // 输出数据库中已有的用户名和密码
        for (UserData data:datas)
        {
            Log.i(TAG, "username: " + data.getUserName() + " password: " + data.getPassword());
        }

        login_button = (Button)findViewById(R.id.login_button);             // 登录按钮
        cancel_button = (Button)findViewById(R.id.cancel_butoon);           // 取消按钮
        sign_up_button = (TextView)findViewById(R.id.sign_up);              // 用户注册按钮
        user_name_edit = (EditText)findViewById(R.id.user_name);            // 用户名输入框
        password_edit = (EditText)findViewById(R.id.password);              // 密码输入框
        save_password = (CheckBox)findViewById(R.id.save_password);      // 自动保存密码按钮
        auto_login = (CheckBox)findViewById(R.id.auto_login);            // 自动登录按钮

        boolean isRemember = preferences.getBoolean("remember_password", false);
        boolean isAutoLogin = preferences.getBoolean("auto_login", false);

        if (isRemember) {
            // 如果上次已记住密码， 则从上次中获取结果
            user_name_edit.setText(preferences.getString("username", ""));
            password_edit.setText(preferences.getString("password", ""));
            save_password.setChecked(true);
            if (isAutoLogin) {
                // 有自动登录必然是会有勾选记住密码的
                auto_login.setChecked(true);
            }
        }

        // 登录按钮的监听器
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_name = user_name_edit.getText().toString();
                String password = password_edit.getText().toString();
                if(user_name.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                }
                else if(password.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }
                else {
                    List<UserData> judge = DataSupport.where("userName = ?", user_name).find(UserData.class);
                    if (judge.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "用户名不存在", Toast.LENGTH_SHORT).show();
                    } else {
                        if(password.compareTo(judge.get(0).getPassword()) != 0)
                        {
                            Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // 登录成功
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            isLogin = true;

                            editor = preferences.edit();
                            // 判断是否是勾选了保存密码或者自动登录
                            if (save_password.isChecked()) {
                                // 勾了保存密码则保存密码
                                editor.putString("username", user_name);
                                editor.putString("password", password);
                                editor.putBoolean("remember_password", true);
                                if (auto_login.isChecked()) {
                                    // 有自动登录 必然是有勾选保存密码的
                                    editor.putBoolean("auto_login", true);
                                } else {
                                    editor.putBoolean("auto_login", false);
                                }
                            } else {
                                // 否则，清空保存的记录
                                editor.clear();
                            }
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, ChattingListActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            }
        });

        // 取消按钮的监听器
        // 清空EditText的内容
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password_edit.setText("");
                user_name_edit.setText("");
            }
        });

        // 用户注册按钮监听器
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        save_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = save_password.isChecked();
                if (!isChecked) {
                    // 取消保存密码的话， 自动登录也被取消勾选
                    auto_login.setChecked(false);
                    save_password.setChecked(false);
                } else {
                    save_password.setChecked(true);
                }
            }
        });

        auto_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = auto_login.isChecked();
                if (isChecked) {
                    // 自动登录被勾选了， 自动保存密码也被勾选了
                    save_password.setChecked(true);
                    auto_login.setChecked(true);
                } else {
                    auto_login.setChecked(false);
                }
            }
        });

        if (isAutoLogin) {
            isLogin = true;
            Intent intent = new Intent(LoginActivity.this, ChattingListActivity.class);
            startActivity(intent);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLogin) {
            // 已登录不需要再登录
            Intent intent = new Intent(LoginActivity.this, ChattingListActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.quit:
                ActivityCollector.finishAll(LoginActivity.this);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
