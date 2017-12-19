package com.example.guge.data2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    private static final String TABLE_NAME = "Contacts";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        final EditText edit_name = (EditText)findViewById(R.id.Edit_name);
        final EditText edit_bir = (EditText)findViewById(R.id.Edit_bir);
        final EditText edit_gift = (EditText)findViewById(R.id.Edit_gift);
        final Button btn_add = (Button)findViewById(R.id.add2);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edit_name.length() == 0){
                    Toast.makeText(Main2Activity.this,"名字不能为空！",Toast.LENGTH_SHORT).show();
                }
                else{
                    String name = edit_name.getText().toString();
                    String bir = edit_bir.getText().toString();
                    String gift = edit_gift.getText().toString();
                    MyDateBase myDateBase = new MyDateBase(getBaseContext());
                    SQLiteDatabase sqLiteDatabase = myDateBase.getWritableDatabase();
                    Cursor cursor = sqLiteDatabase.rawQuery("select * from "+TABLE_NAME+" where name = '" + name + "'",null);
                    if(cursor.moveToFirst()){
                        Toast.makeText(Main2Activity.this,"名字已存在！",Toast.LENGTH_SHORT).show();
                        cursor.close();
                    }
                    //如果没有重名，添加到数据库中
                    else{
                        myDateBase.insert(name,bir,gift);
                        cursor.close();
                        Toast.makeText(Main2Activity.this,"添加成功",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Main2Activity.this,MainActivity.class);
                        startActivity(intent);
                    }



                }
            }
        });
    }
}
