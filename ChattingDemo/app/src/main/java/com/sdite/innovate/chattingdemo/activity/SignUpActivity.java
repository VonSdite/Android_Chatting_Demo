package com.sdite.innovate.chattingdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sdite.innovate.chattingdemo.R;
import com.sdite.innovate.chattingdemo.module.UserData;

import org.litepal.crud.DataSupport;

import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    private Button sign_up_button;
    private Button back_button;
    private EditText user_name_edit;
    private EditText password_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActivityCollector.addActivity(this);

        sign_up_button = (Button)findViewById(R.id.sign_up_button);         // 注册按钮
        back_button = (Button)findViewById(R.id.back_button);               // 返回按钮

        user_name_edit = (EditText)findViewById(R.id.user_name);            // 用户名输入框
        password_edit = (EditText)findViewById(R.id.password);              // 密码输入框

        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_name = user_name_edit.getText().toString();
                String password = password_edit.getText().toString();
                if (user_name.isEmpty())
                {
                    Toast.makeText(SignUpActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                }
                else if (password.isEmpty())
                {
                    Toast.makeText(SignUpActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }
                else if (password.length() < 6 || password.length() > 16)
                {
                    Toast.makeText(SignUpActivity.this, "请设置密码长度在6-16位", Toast.LENGTH_SHORT).show();
                }
                else {
                    List<UserData> judge = DataSupport.where("userName = ?", user_name).find(UserData.class);
                    if (judge.isEmpty()) {
                        UserData data = new UserData();
                        data.setUserName(user_name);
                        data.setPassword(password);
                        Toast.makeText(SignUpActivity.this, "用户注册成功", Toast.LENGTH_SHORT).show();
                        data.save();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignUpActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                ActivityCollector.finishAll(SignUpActivity.this);
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
