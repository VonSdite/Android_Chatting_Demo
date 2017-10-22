package com.sdite.innovate.Demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;


// 用户注册界面
public class ThirdActivity extends AppCompatActivity {

    private static final String TAG = "ThirdActivity";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.quit:
                ActivityCollector.finishAll(ThirdActivity.this);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        ActivityCollector.addActivity(this);

        Button sign_up_button = (Button)findViewById(R.id.sign_up_button);    // 注册按钮
        final Button back_button = (Button)findViewById(R.id.back_button);          // 返回按钮

        final EditText user_name_edit = (EditText)findViewById(R.id.user_name);     // 用户名输入框
        final EditText password_edit = (EditText)findViewById(R.id.password);       // 密码输入框

        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_name = user_name_edit.getText().toString();
                String password = password_edit.getText().toString();
                if (user_name.isEmpty())
                {
                    Toast.makeText(ThirdActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                }
                else if (password.isEmpty())
                {
                    Toast.makeText(ThirdActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }
                else if (password.length() < 6 || password.length() > 16)
                {
                    Toast.makeText(ThirdActivity.this, "请设置密码长度在6-16位", Toast.LENGTH_SHORT).show();
                }
                else {
                    List<Data> judge = DataSupport.where("userName = ?", user_name).find(Data.class);
                    if (judge.isEmpty()) {
                        Data data = new Data();
                        data.setUserName(user_name);
                        data.setPassword(password);
                        Toast.makeText(ThirdActivity.this, "用户注册成功", Toast.LENGTH_SHORT).show();
                        data.save();
                        Intent intent = new Intent(ThirdActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ThirdActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ThirdActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
