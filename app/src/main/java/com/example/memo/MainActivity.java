package com.example.memo;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;    //https://github.com/ParkSangGwon/TedPermission
import com.gun0912.tedpermission.TedPermission;         //https://github.com/ParkSangGwon/TedPermission

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    TextFileManager mTextFileManager = new TextFileManager(this);
    ListViewAdapter adapter;
    ListView listview;
    long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tedPermission();

        listview = (ListView) findViewById(R.id.memoList);
        loadList();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);
                String titleStr = item.getTitle();

                Intent intent=new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("flag", 1);
                intent.putExtra("title", titleStr);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            // 새 메모 작성하기
            case R.id.create: {
                Intent intent=new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }
    //메모 리스트 업데이트
    public void loadList() {
        String[] titles = mTextFileManager.viewMemo();
        // Adapter 생성
        ArrayList<ListViewItem> itemList = new ArrayList<>() ;

        adapter = new ListViewAdapter(itemList) ;

        for(int i=0; i<titles.length; i++) {
            File imgFile = new File("/data/data/com.example.memo/files/Images/"+titles[i]);

            if(imgFile.exists()){
                String[] images = mTextFileManager.loadImg(imgFile).split(" ");
                imgFile = new File(images[0]);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                adapter.addItem(titles[i].replace(".txt", ""), mTextFileManager.load(titles[i]), mTextFileManager.getDate(titles[i]), myBitmap);
            }

            else
                adapter.addItem(titles[i].replace(".txt", ""), mTextFileManager.load(titles[i]), mTextFileManager.getDate(titles[i]), null);
        }

        Comparator<ListViewItem> textDate = new Comparator<ListViewItem>() {
            @Override
            public int compare(ListViewItem item1, ListViewItem item2) {
                return item2.getDate().compareTo(item1.getDate()) ;         //날짜순으로 내림차순 정렬

            }
        } ;

        Collections.sort(itemList, textDate) ;
        listview.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadList();
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() > backPressedTime + 2000) {
            backPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return ;
        }
        else {
            this.finish();
        }
    }

    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
                finish();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }
}