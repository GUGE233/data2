package com.example.guge.data2;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private static final String TABLE_NAME = "Contacts";

    MyDateBase myDateBase = new MyDateBase(MainActivity.this);

    //根据姓名查找联系人的电话号码
    public String number(String name) {
        String Num = "";
        //先用ContentResolver把所有联系人的数据都拿到
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        //通过moveToNext()吧这些数据遍历一次
        while (cursor.moveToNext()) {
            //获取联系人的ID
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //获取联系人的名字
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            if (name.equals(contactName)) {
                //使用ContentResolver查找联系人的电话号码
                Cursor phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                //一个联系人可能有多个电话号码，遍历一次
                while(phone.moveToNext()) {
                    //这里由于有些手机上可能会把号码用空格分开成三部分，所以去掉号码字符串中的空格
                    Num += phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ","") + "   ";
                }
            }
        }
        //释放内存
        cursor.close();

        return Num;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //动态权限申请(android 6.0以上版本)
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 0);
        }

        //更新界面的操作
        final List<Map<String, String>> listitems = new ArrayList<Map<String, String>>();
        //找出可读取的数据库
        final MyDateBase myDateBase = new MyDateBase(getBaseContext());
        final SQLiteDatabase sqLiteDatabase = myDateBase.getWritableDatabase();
        //通过cursor读取数据库中的表
        final Cursor cursor = sqLiteDatabase.query(TABLE_NAME, new String[]{"name", "bir", "gift"},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            Map<String, String> listem = new HashMap<String, String>();
            listem.put("name", cursor.getString(cursor.getColumnIndex("name")));
            listem.put("bir", cursor.getString(cursor.getColumnIndex("bir")));
            listem.put("gift", cursor.getString(cursor.getColumnIndex("gift")));
            listitems.add(listem);
        }
        cursor.close();
        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, listitems, R.layout.item,
                new String[]{"name", "bir", "gift"}, new int[]{R.id.iname, R.id.ibir, R.id.igift});
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(simpleAdapter);


        //增加条目按钮的操作
        Button to_add = (Button) findViewById(R.id.add1);
        to_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });

        //长按列表内容删除的操作
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            //注意和之前一样吧参数中的int i改成final int position，不然会和内部的onClick函数中的int i冲突造成闪退！
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, long l) {
                //弹出提示框
                AlertDialog.Builder message = new AlertDialog.Builder(MainActivity.this);
                message.setTitle("删除生日记录");
                message.setMessage("确认要删除吗？");
                message.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //点击确定，就执行删除操作
                        MyDateBase myDateBase1 = new MyDateBase(getBaseContext());
                        //在列表中找出第i行中列名为"name"的值，删除数据库中对应的行
                        //Toast.makeText(MainActivity.this,listitems.get(position).get("name"),Toast.LENGTH_SHORT).show();
                        myDateBase1.delete(listitems.get(position).get("name"));
                        //然后把列表中对应的数据删掉
                        listitems.remove(position);
                        simpleAdapter.notifyDataSetChanged();
                    }
                });
                message.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //什么都不用做
                    }
                });
                message.create().show();

                return true;
            }
        });


        //点击列表项弹出修改窗口
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {

                //自定义提示框
                LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                View view1 = layoutInflater.inflate(R.layout.dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(view1);
                builder.setTitle("大吉大利，今晚吃鸡");
                //获取提示框里面的控件
                final TextView name = (TextView) view1.findViewById(R.id.dname2);
                final TextView phone1 = (TextView) view1.findViewById(R.id.dphone2);
                final EditText bir = (EditText) view1.findViewById(R.id.Edit_dbir);
                final EditText gift = (EditText) view1.findViewById(R.id.Edit_dgift);
                //把提示框里控件的初始值设置为列表中对应项的值
                name.setText(listitems.get(position).get("name"));
                bir.setText(listitems.get(position).get("bir"));
                gift.setText(listitems.get(position).get("gift"));



                //电话号码
                String Number = "";

                //调用查找号码函数找到该人的号码，没有找到Number就是空的

                Number = number(listitems.get(position).get("name"));


                if (Number.equals("")) {
                    Number = "电话本中没有TA的号码";
                }
                //然后吧显示号码的TextView设置一下内容
                phone1.setText(Number);





                builder.setPositiveButton("保存修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MyDateBase myDateBase1 = new MyDateBase(getBaseContext());
                        SQLiteDatabase sqLiteDatabase1 = myDateBase1.getWritableDatabase();
                        sqLiteDatabase1.execSQL("update " + TABLE_NAME
                                + " set bir = ? where name = ?", new Object[]{
                                bir.getText().toString(), name.getText().toString()});
                        sqLiteDatabase1.execSQL("update " + TABLE_NAME
                                + " set gift = ? where name = ?", new Object[]{
                                gift.getText().toString(), name.getText().toString()});
                        sqLiteDatabase1.close();


                        //把界面再次刷新
                        listitems.clear();
                        final MyDateBase myDateBase = new MyDateBase(getBaseContext());
                        final SQLiteDatabase sqLiteDatabase = myDateBase.getWritableDatabase();
                        //通过cursor读取数据库中的表
                        final Cursor cursor = sqLiteDatabase.query(TABLE_NAME, new String[]{"name", "bir", "gift"},
                                null, null, null, null, null);
                        while (cursor.moveToNext()) {
                            Map<String, String> listem = new HashMap<String, String>();
                            listem.put("name", cursor.getString(cursor.getColumnIndex("name")));
                            listem.put("bir", cursor.getString(cursor.getColumnIndex("bir")));
                            listem.put("gift", cursor.getString(cursor.getColumnIndex("gift")));
                            listitems.add(listem);
                        }
                        cursor.close();
                        final SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this, listitems, R.layout.item,
                                new String[]{"name", "bir", "gift"}, new int[]{R.id.iname, R.id.ibir, R.id.igift});
                        listView = (ListView) findViewById(R.id.listView);
                        listView.setAdapter(simpleAdapter);


                    }
                });

                builder.setNegativeButton("放弃修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //什么都不做
                    }
                });

                builder.show();

            }
        });


    }
}
