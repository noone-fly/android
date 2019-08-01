package com.example.chenpiyang.contentprovider;

import android.database.Cursor;
import android.provider.Contacts;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //getContentResolver()方法得到应用的ContentResolver实例。
        // query（Phones.CONTENT_URI, null, null, null, null）。它是ContentResolver里的方法，负责查询所有联系人，并返回一个Cursor。这个方法参数比较多，每个参数的具体含义如下。
        //·  第一个参数为Uri，在这个例子里边这个Uri是联系人的Uri。
        //·  第二个参数是一个字符串的数组，数组里边的每一个字符串都是数据表中某一列的名字，它指定返回数据表中那些列的值。
        //·  第三个参数相当于SQL语句的where部分，描述哪些值是我们需要的。
        //·  第四个参数是一个字符串数组，它里边的值依次代替在第三个参数中出现的“?”符号。
        //·  第五个参数指定了排序的方式。
        Cursor c = getContentResolver().query(Contacts.Phones.CONTENT_URI, null, null, null, null);
        startManagingCursor(c); //让系统来管理生成的Cursor。
        ListAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                c,
                new String[] { Contacts.Phones.NAME, Contacts.Phones.NUMBER },
                new int[] { android.R.id.text1, android.R.id.text2 });
        //setListAdapter(adapter); //将ListView和SimpleCursorAdapter进行绑定。
    }
}
