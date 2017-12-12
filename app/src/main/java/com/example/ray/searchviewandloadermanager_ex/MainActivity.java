package com.example.ray.searchviewandloadermanager_ex;

/*
此範例展示使用Loader去讀取資料, 使用Cursor讀取搭配Loader的好處是避免UI被block或自己須要另外去處理讀取的thread.
使用Loader會協助自行建立另外的Thread去讀資料, 當讀取完畢時, 會有callback function返回, 再去處裡顯示即可, 相當方便.

此外, 此範例還使用SearchView去更新資料顯示的內容, 利用Uri提供的filter去所小顯示的範圍.

Loader大部分都使與Cursor合併使用, 本例易是如此.
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
