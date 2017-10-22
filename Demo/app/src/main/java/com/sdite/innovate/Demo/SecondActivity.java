package com.sdite.innovate.Demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

// 用户登录成功后的界面
public class SecondActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.quit:
                ActivityCollector.finishAll(SecondActivity.this);
                break;
            case 1:
                Toast.makeText(getApplicationContext(), "你点了添加好友按钮", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        menu.add(Menu.NONE, 1, Menu.NONE, "添加好友");

        return true;
    }

    private ExpandableListView expandableListView;

    private List<String> groupList;         // 存放每个分组

    private List<List<String>> friendsName;    // 存放每个组的元素的名字

    private List<List<String>> friendsNote;    // 存放每个组的元素的个性签名

    private List<List<Integer>> friendsIcon;  // 存放每个组的元素的图标



    private MyExpandableListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ActivityCollector.addActivity(this);

        setTitle("好友列表");

        // 第一层组
        groupList = new ArrayList<String>();
        groupList.add("我的好友");
        groupList.add("我的家人");
        groupList.add("兄弟姐妹");
        groupList.add("我的同学");

        // 每一组人的名字
        List<String> item_1t;
        item_1t = new ArrayList<String>();
        item_1t.add("robot");
        item_1t.add("al_bassam");
        item_1t.add("amalraj");
        item_1t.add("anash");

        List<String> item_2t;
        item_2t = new ArrayList<String>();
        item_2t.add("attaras");
        item_2t.add("bailey");
        item_2t.add("bair");

        List<String> item_3t;
        item_3t = new ArrayList<String>();
        item_3t.add("barrett");
        item_3t.add("bawany");
        item_3t.add("bezard");

        List<String> item_4t;
        item_4t = new ArrayList<String>();
        item_4t.add("blonchek");
        item_4t.add("boonstra");
        item_4t.add("bourbier");

        friendsName = new ArrayList<List<String>>();
        friendsName.add(item_1t);
        friendsName.add(item_2t);
        friendsName.add(item_3t);
        friendsName.add(item_4t);

        friendsNote = new ArrayList<List<String>>();
        List<String> tmp_note1 = new ArrayList<String>();
        tmp_note1.add("聊天机器人");
        tmp_note1.add("Software Engineer");
        tmp_note1.add("Founderand CEO");
        tmp_note1.add("SoftwareArchitect");
        friendsNote.add(tmp_note1);

        List<String> tmp_note2 = new ArrayList<String>();
        tmp_note2.add("Developer Technical Services Lead Manager");
        tmp_note2.add("PlatformArchitect");
        tmp_note2.add("Developer");
        friendsNote.add(tmp_note2);

        List<String> tmp_note3 = new ArrayList<String>();
        tmp_note3.add("PlatformApplication Review Lead Manager");
        tmp_note3.add("Head of Integration");
        tmp_note3.add("ProductMarketing Manager");
        friendsNote.add(tmp_note3);

        List<String> tmp_note4 = new ArrayList<String>();
        tmp_note4.add("SeniorSoftware Engineer");
        tmp_note4.add("Integration Engineer");
        tmp_note4.add("Head of Emerging Markets");
        friendsNote.add(tmp_note4);

        // 每一组人的头像
        friendsIcon = new ArrayList<List<Integer>>();
        for (int i = 0; i < friendsName.size(); ++i)
        {
            List<Integer> tmp_list = new ArrayList<Integer>();
            for (int j = 0; j < friendsName.get(i).size(); ++j)
            {
                int tmp = getResources().getIdentifier(friendsName.get(i).get(j), "drawable",
                        getApplicationInfo().packageName);
                if(tmp == 0) {
                    tmp_list.add(R.mipmap.ic_launcher);
                }
                else{
                    tmp_list.add(tmp);
                }

            }
            friendsIcon.add(tmp_list);
        }

        expandableListView = (ExpandableListView)findViewById(R.id.expendlist);
        expandableListView.setGroupIndicator(null);

        // 监听组点击
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {
            @SuppressLint("NewApi")
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
            {
                if (friendsName.get(groupPosition).isEmpty())
                {
                    return true;
                }
                return false;
            }
        });

        // 监听每个分组里子控件的点击事件
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
        {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                if (friendsName.get(groupPosition).get(childPosition) == "robot"){
                    Intent intent = new Intent(SecondActivity.this, ChatRoomActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(SecondActivity.this, "你点击的是第" + (1+groupPosition)
                            + "组 第" + (1+childPosition) + "个元素 "
                            + friendsName.get(groupPosition).get(childPosition), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        adapter = new MyExpandableListViewAdapter(this);

        expandableListView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }


    // 用过ListView的人一定很熟悉，只不过这里是BaseExpandableListAdapter
    class MyExpandableListViewAdapter extends BaseExpandableListAdapter
    {

        private Context context;

        public MyExpandableListViewAdapter(Context context)
        {
            this.context = context;
        }

        /**
         *
         * 获取组的个数
         *
         * @return
         * @see android.widget.ExpandableListAdapter#getGroupCount()
         */
        @Override
        public int getGroupCount()
        {
            return groupList.size();
        }

        /**
         *
         * 获取指定组中的子元素个数
         *
         * @param groupPosition
         * @return
         * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
         */
        @Override
        public int getChildrenCount(int groupPosition)
        {
            return friendsName.get(groupPosition).size();
        }

        /**
         *
         * 获取指定组中的数据
         *
         * @param groupPosition
         * @return
         * @see android.widget.ExpandableListAdapter#getGroup(int)
         */
        @Override
        public Object getGroup(int groupPosition)
        {
            return groupList.get(groupPosition);
        }

        /**
         *
         * 获取指定组中的指定子元素数据。
         *
         * @param groupPosition
         * @param childPosition
         * @return
         * @see android.widget.ExpandableListAdapter#getChild(int, int)
         */
        @Override
        public Object getChild(int groupPosition, int childPosition)
        {
            return friendsName.get(groupPosition).get(childPosition);
        }

        /**
         *
         * 获取指定组的ID，这个组ID必须是唯一的
         *
         * @param groupPosition
         * @return
         * @see android.widget.ExpandableListAdapter#getGroupId(int)
         */
        @Override
        public long getGroupId(int groupPosition)
        {
            return groupPosition;
        }

        /**
         *
         * 获取指定组中的指定子元素ID
         *
         * @param groupPosition
         * @param childPosition
         * @return
         * @see android.widget.ExpandableListAdapter#getChildId(int, int)
         */
        @Override
        public long getChildId(int groupPosition, int childPosition)
        {
            return childPosition;
        }

        /**
         *
         * 组和子元素是否持有稳定的ID,也就是底层数据的改变不会影响到它们。
         *
         * @return
         * @see android.widget.ExpandableListAdapter#hasStableIds()
         */
        @Override
        public boolean hasStableIds()
        {
            return true;
        }

        /**
         *
         * 获取显示指定组的视图对象
         *
         * @param groupPosition 组位置
         * @param isExpanded 该组是展开状态还是伸缩状态
         * @param convertView 重用已有的视图对象
         * @param parent 返回的视图对象始终依附于的视图组
         * @return
         * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, android.view.View,
         *      android.view.ViewGroup)
         */
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
        {
            GroupHolder groupHolder = null;
            if (convertView == null)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.expendlist_group, null);
                groupHolder = new GroupHolder();
                groupHolder.txt = (TextView)convertView.findViewById(R.id.txt);
                groupHolder.txt2 = (TextView)convertView.findViewById(R.id.txt2);
                groupHolder.img = (ImageView)convertView.findViewById(R.id.img);
                convertView.setTag(groupHolder);
            }
            else
            {
                groupHolder = (GroupHolder)convertView.getTag();
            }

            if (!isExpanded)
            {
                groupHolder.img.setBackgroundResource(R.drawable.ic_expand_more);
            }
            else
            {
                groupHolder.img.setBackgroundResource(R.drawable.ic_expand_less);
            }

            groupHolder.txt.setText(groupList.get(groupPosition));
            groupHolder.txt2.setText(""+friendsName.get(groupPosition).size()+"/"+friendsName.get(groupPosition).size());
            return convertView;
        }

        /**
         *
         * 获取一个视图对象，显示指定组中的指定子元素数据。
         *
         * @param groupPosition 组位置
         * @param childPosition 子元素位置
         * @param isLastChild 子元素是否处于组中的最后一个
         * @param convertView 重用已有的视图(View)对象
         * @param parent 返回的视图(View)对象始终依附于的视图组
         * @return
         * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, android.view.View,
         *      android.view.ViewGroup)
         */
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
        {
            ItemHolder itemHolder = null;
            if (convertView == null)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.expendlist_item, null);
                itemHolder = new ItemHolder();
                itemHolder.txt = (TextView)convertView.findViewById(R.id.txt);
                itemHolder.img = (ImageView)convertView.findViewById(R.id.img);
                itemHolder.txt2 = (TextView)convertView.findViewById(R.id.txt2);
                convertView.setTag(itemHolder);
            }
            else
            {
                itemHolder = (ItemHolder)convertView.getTag();
            }
            itemHolder.txt.setText(friendsName.get(groupPosition).get(childPosition));
            itemHolder.txt2.setText(friendsNote.get(groupPosition).get(childPosition));
            itemHolder.img.setBackgroundResource(friendsIcon.get(groupPosition).get(childPosition));
            return convertView;
        }

        /**
         *
         * 是否选中指定位置上的子元素。
         *
         * @param groupPosition
         * @param childPosition
         * @return
         * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
         */
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition)
        {
            return true;
        }

    }

    class GroupHolder
    {
        public TextView txt;
        public TextView txt2;

        public ImageView img;
    }

    class ItemHolder
    {
        public ImageView img;

        public TextView txt;

        public TextView txt2;
    }

}

