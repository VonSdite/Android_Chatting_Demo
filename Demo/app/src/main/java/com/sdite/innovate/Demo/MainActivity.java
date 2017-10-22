package com.sdite.innovate.Demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

// 主界面
// 用户登录界面
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.quit:
                ActivityCollector.finishAll(MainActivity.this);
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
        setContentView(R.layout.activity_main);

        setTitle("用户登录");
        // 隐藏actionBar
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null){
//            actionBar.hide();
//        }

        ActivityCollector.addActivity(this);

        LitePal.getDatabase();              // 创建用户数据库

        List<Data> datas = DataSupport.findAll(Data.class);
        for (Data data:datas)
        {
            Log.i(TAG, "username: " + data.getUserName() + " password: " + data.getPassword());
        }

        Button login_button = (Button)findViewById(R.id.login_button);      // 登录按钮
        Button cancel_button = (Button)findViewById(R.id.cancel_butoon);    // 取消按钮
        TextView sign_up_button = (TextView)findViewById(R.id.sign_up);     // 用户注册按钮
        final EditText user_name_edit = (EditText)findViewById(R.id.user_name);     // 用户名输入框
        final EditText password_edit = (EditText)findViewById(R.id.password);       // 密码输入框

        // 登录按钮的监听器
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_name = user_name_edit.getText().toString();
                String password = password_edit.getText().toString();
                if(user_name.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                }
                else if(password.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }
                else {
                    List<Data> judge = DataSupport.where("userName = ?", user_name).find(Data.class);
                    if (judge.isEmpty()) {
                        Toast.makeText(MainActivity.this, "用户名不存在", Toast.LENGTH_SHORT).show();
                    } else {
                        if(password.compareTo(judge.get(0).getPassword()) != 0)
                        {
                            Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // 登录成功
                            Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
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
                Intent intent = new Intent(MainActivity.this, ThirdActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        List<Data> alldatas = DataSupport.findAll(Data.class);
        for (Data data:alldatas)
        {
            Log.i(TAG, "username: " + data.getUserName() + " password: " + data.getPassword());
        }
        ActivityCollector.removeActivity(this);
    }
}
