package com.sdite.innovate.Demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChatRoomActivity extends AppCompatActivity {

    private List<Msg> msgList = new ArrayList<Msg>();

    private EditText inputText;

    private Button send;

    private RecyclerView msgRecyclerView;

    private MsgAdapter adapter;

    private String content;

    private static final String TAG = "ChatRoomActivity";
    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {

            Msg reply = new Msg((String)msg.obj, Msg.TYPE_RECEIVED);
            msgList.add(reply);
            adapter.notifyItemInserted(msgList.size() - 1);         //当有新消息时，刷新ListView中的显示
            msgRecyclerView.scrollToPosition(msgList.size() - 1);   //将ListView定位到最后一行
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        setTitle("小黑聊天机器人");
        Msg msg = new Msg("你好！(*^▽^*)", Msg.TYPE_RECEIVED);
        msgList.add(msg);

        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = inputText.getText().toString();
                if (content != "") {
                    Msg msg = new Msg(content, Msg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                    msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                    inputText.setText(""); // 清空输入框中的内容

                    // 消息线程
                    new Thread(){
                        public void run() {
                            // 在这里进行 http request.网络请求相关操作
                            String url =
                                    "http://www.tuling123.com/openapi/api?key=b74895fe92fe426ea5f4839212dcd995&info="
                                            + content;
                            OkHttpClient client = new OkHttpClient();

                            Request request = new Request.Builder()
                                    .url(url)
                                    .build();
                            String reply = null;
                            try {
                                // Copy to customize OkHttp for this request.
                                OkHttpClient copy = client.newBuilder()
                                        .readTimeout(500, TimeUnit.MILLISECONDS)
                                        .connectTimeout(500, TimeUnit.MILLISECONDS)
                                        .build();

                                Response response = copy.newCall(request).execute();
                                reply = response.body().string();
                                try {
                                    JSONObject jsonObject = new JSONObject(reply);
                                    reply = jsonObject.getString("text");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (IOException e) {
                                reply = "网络出错了， 小黑无法回复o(╥﹏╥)o";
                            }

                            Message message = Message.obtain();

                            message.obj = reply;
                            mHandler.sendMessage(message);
                        }
                    }.start();
                }
            }
        });
    }


}
