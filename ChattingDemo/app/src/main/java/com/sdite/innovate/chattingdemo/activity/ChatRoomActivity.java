package com.sdite.innovate.chattingdemo.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sdite.innovate.chattingdemo.R;
import com.sdite.innovate.chattingdemo.adapter.MsgAdapter;
import com.sdite.innovate.chattingdemo.module.Msg;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChatRoomActivity extends AppCompatActivity {

    private List<Msg> msgList;

    private EditText inputText;

    private Button send;

    private Button pic;

    private RecyclerView msgRecyclerView;

    private MsgAdapter adapter;

    private String content;

    private ContentResolver cr;

    public static int who = 0;

    private static final int SOCKET_PORT = 50000;
    private DatagramSocket socket;


    private static final String TAG = "ChatRoomActivity";
    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            Msg reply = new Msg((String)msg.obj, Msg.TYPE_RECEIVED);
            reply.setIsWho(who);
            msgList.add(reply);
            reply.save();
            adapter.notifyItemInserted(msgList.size() - 1);     //当有新消息时，刷新ListView中的显示
            msgRecyclerView.scrollToPosition(msgList.size() - 1);        //将ListView定位到最后一行
        };
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        ActivityCollector.addActivity(this);

        cr = getContentResolver();

        msgList = DataSupport.findAll(Msg.class);
        Iterator<Msg> iterator = msgList.iterator();
        while (iterator.hasNext()) {
            Msg msg =iterator.next();
            if (msg.getIsWho() != who) {
                iterator.remove();
            }
        }

        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        pic = (Button) findViewById(R.id.pic);
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);

        adapter = new MsgAdapter(msgList, cr);
        msgRecyclerView.setAdapter(adapter);
        if (who != 0) pic.setVisibility(View.GONE);

        if (who == 1) {
            SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", SOCKET_PORT);
            try {
                socket = new DatagramSocket(socketAddress);

                new Thread() {
                    private byte buff[] = new byte[1024];
                    @Override
                    public void run() {
                        DatagramPacket p = new DatagramPacket(buff, 1024);
                        while (true) {
                            try {
                                socket.receive(p);
                                Message msg = new Message();
                                msg.obj = new String(p.getData());
                                mHandler.sendMessage(msg);

                                for (int i = 0; i < 1024; ++i)
                                {
                                    buff[i] = 0;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        } else if (who == 2) {
            SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", SOCKET_PORT+1);
            try {
                socket = new DatagramSocket(socketAddress);

                new Thread() {
                    private byte buff[] = new byte[1024];
                    @Override
                    public void run() {
                        DatagramPacket p = new DatagramPacket(buff, 1024);
                        while (true) {
                            try {
                                socket.receive(p);
                                Message msg = new Message();
                                msg.obj = new String(p.getData());
                                mHandler.sendMessage(msg);

                                for (int i = 0; i < 1024; ++i)
                                {
                                    buff[i] = 0;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (who == 0) {
                    content = inputText.getText().toString();
                    if (content != "") {
                        Msg msg = new Msg(content, Msg.TYPE_SENT);
                        msg.setIsWho(who);
                        msgList.add(msg);
                        msg.save();         // 保存到数据库
                        adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                        msgRecyclerView.scrollToPosition(msgList.size() - 1);    // 将ListView定位到最后一行
                        inputText.setText(""); // 清空输入框中的内容

                        // 消息线程
                        new Thread() {
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
                                            .readTimeout(400, TimeUnit.MILLISECONDS)
                                            .connectTimeout(400, TimeUnit.MILLISECONDS)
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
                } else if (who == 1) {
                    content = inputText.getText().toString();
                    if (content != "") {
                        Msg msg = new Msg(content, Msg.TYPE_SENT);
                        msg.setIsWho(who);
                        msgList.add(msg);
                        msg.save();         // 保存到数据库
                        adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                        msgRecyclerView.scrollToPosition(msgList.size() - 1);    // 将ListView定位到最后一行
                        inputText.setText(""); // 清空输入框中的内容

                        final SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", SOCKET_PORT);
                        try {
                            socket = new DatagramSocket(socketAddress);
                            new Thread() {
                                @Override
                                public void run() {
                                    DatagramPacket p = new DatagramPacket(content.getBytes(),
                                            content.getBytes().length, socketAddress);
                                    try {
                                        socket.send(p);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        } catch (SocketException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (who == 2) {
                    content = inputText.getText().toString();
                    if (content != "") {
                        Msg msg = new Msg(content, Msg.TYPE_SENT);
                        msg.setIsWho(who);
                        msgList.add(msg);
                        msg.save();         // 保存到数据库
                        adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                        msgRecyclerView.scrollToPosition(msgList.size() - 1);    // 将ListView定位到最后一行
                        inputText.setText(""); // 清空输入框中的内容

                        final SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", SOCKET_PORT+1);
                        try {
                            socket = new DatagramSocket(socketAddress);
                            new Thread() {
                                @Override
                                public void run() {
                                    DatagramPacket p = new DatagramPacket(content.getBytes(),
                                            content.getBytes().length, socketAddress);
                                    try {
                                        socket.send(p);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        } catch (SocketException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ChatRoomActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatRoomActivity.this,
                            new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    openAlbum();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        msgRecyclerView.scrollToPosition(msgList.size() - 1);    // 将ListView定位到最后一行
    }

    public static final int CHOOSE_PHOTO = 2;
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    String img_path;

                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        img_path = handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        img_path = handleImageBeforeKitKat(data);
                    }

                    Msg msg = new Msg(img_path, Msg.TYPE_SENT_PIC);
                    msg.setIsWho(who);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);    // 将ListView定位到最后一行
                    msg.save();

                    if (who == 0) {
                        Msg reply = new Msg("o(╥﹏╥)o我看不懂图片啦", Msg.TYPE_RECEIVED);
                        reply.setIsWho(who);
                        msgList.add(reply);
                        adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                        msgRecyclerView.scrollToPosition(msgList.size() - 1);    // 将ListView定位到最后一行
                        reply.save();
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private String handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath; // 根据图片路径显示图片
    }

    private String handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        return imagePath;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

}
