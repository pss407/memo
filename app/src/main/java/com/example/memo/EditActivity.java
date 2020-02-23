package com.example.memo;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditActivity extends AppCompatActivity {
    EditText mMemoEdit = null;
    EditText mMemoTitle = null;
    Gallery gallery = null;
    ArrayList<String> imageList = new ArrayList<>();
    TextFileManager mTextFileManager = new TextFileManager(this);
    InputMethodManager imm;
    GalleryViewAdapter galleryViewAdapter;
    final int REQUEST_TAKE_ALBUM = 1;
    final int REQUEST_FROM_CAMERA = 2;
    String mCurrentPhotoPath;
    private File tempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mMemoTitle=(EditText)findViewById(R.id.editTitle);
        mMemoEdit=(EditText)findViewById(R.id.editText);
        gallery = (Gallery) findViewById(R.id.gallery);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Intent intent = new Intent(this.getIntent());
        //flag가 1 이면 저장된 메모 불러오기
        if(intent.getIntExtra("flag", 0)==1) {
            String title=intent.getStringExtra("title");
            String memoTitle = title+".txt";
            String memoData = mTextFileManager.load(memoTitle);
            File imgFile = new File("/data/data/com.example.memo/files/Images/"+memoTitle);

            if(imgFile.exists()) {
                String[] images = mTextFileManager.loadImg(imgFile).split(" ");
                for(int i=0; i<images.length; i++) {
                    imageList.add(images[i]);
                }
                galleryViewAdapter = new GalleryViewAdapter(getApplicationContext(), R.layout.gallery_row, imageList);
                gallery.setAdapter(galleryViewAdapter);
            }

            mMemoEdit.setText(memoData);
            mMemoTitle.setText(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            // 1. editText에 입력된 메모를 텍스트 파일(제목.text)에 저장하기
            case R.id.save: {
                if(mMemoTitle.getText().toString().equals("")) {
                    if(mMemoEdit.getText().toString().equals("")) {
                        finish();
                        Toast.makeText(this,"내용이 없어서 저장하지 않았습니다.", Toast.LENGTH_LONG).show();
                        break;
                    }

                    else {
                        clear();
                        Toast.makeText(this,"제목을 입력하세요.", Toast.LENGTH_LONG).show();
                        break;
                    }
                }

                else {
                    String memoData = mMemoEdit.getText().toString();
                    String memoTitle = mMemoTitle.getText().toString()+".txt";

                    if(imageList.size()>0)
                        mTextFileManager.saveWithImg(memoData, memoTitle, imageList);
                    else
                        mTextFileManager.save(memoData, memoTitle);

                    clear();

                    Toast.makeText(this,"저장 완료", Toast.LENGTH_LONG).show();
                    break;
                }
            }
            // 2. 저장된 메모 파일 삭제하기
            case R.id.delete: {
                String memoTitle = mMemoTitle.getText().toString()+".txt";
                mTextFileManager.delete(memoTitle);

                finish();
                Toast.makeText(this,"삭제 완료", Toast.LENGTH_LONG).show();
                break;
            }
            // 3. 저장된 이미지 삭제하기 : gallery를 뷰를 이용해서 메뉴를 누르면 gallery 뷰의 selectedItem이 삭제되도록 구현함
            case R.id.delete_image: {
                if(imageList.size()>0) {
                    imageList.remove(gallery.getSelectedItemPosition());
                    galleryViewAdapter.notifyDataSetChanged();
                }
                break;
            }
            // 4. 편집 메뉴
            case R.id.edit: {
                mMemoEdit.requestFocus();
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                break;
            }
            // 5. 카메라로 찍어서 이미지 첨부 -> 구현하지 못함
            case R.id.add_image_camera: {

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_FROM_CAMERA);

                /*try {
                    tempFile = createImageFile();
                }
                catch (IOException e) {
                    Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    finish();
                    e.printStackTrace();
                }
                if (tempFile != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        Uri photoUri = FileProvider.getUriForFile(this, "com.example.memo.provider", tempFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(intent, REQUEST_FROM_CAMERA);
                    }
                    else {
                        Uri photoUri = Uri.fromFile(tempFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(intent, REQUEST_FROM_CAMERA);

                    }
                }*/
                break;
            }
            //6. URL로 이미지 첨부 -> 구현하지 못함
            /*case R.id.add_image_url: {
                imageList.add("http://developer.android.com/assets/images/android_logo.png");
                galleryViewAdapter.notifyDataSetChanged();
                break;
            }*/
            //7. 갤러리의 사진 첨부
            case R.id.add_image_gallery: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(Intent.createChooser(intent,"다중 선택은 '포토'를 선택하세요."), REQUEST_TAKE_ALBUM);

                    }catch(Exception e){
                        Log.e("error", e.toString());
                    }
                }
                else{
                    Log.e("kitkat under", "..");
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void clear() {
        mMemoEdit.clearFocus();
        mMemoTitle.clearFocus();
        imm.hideSoftInputFromWindow(mMemoEdit.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(mMemoTitle.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int index = imageList.size();

        switch (requestCode) {
            //앨범에서 첨부할때
            case REQUEST_TAKE_ALBUM: {
                if (resultCode == Activity.RESULT_OK) {
                    // 멀티 선택을 지원하지 않는 기기에서는 getClipdata()가 없음 => getData()로 접근해야 함
                    if (data.getClipData() == null) {
                        imageList.add(String.valueOf(data.getData()));
                    } else {
                        ClipData clipData = data.getClipData();

                        if (clipData.getItemCount() > 10) {         //너무 많은 선택을 막기위해 10개 이하로 선택하도록 함
                            Toast.makeText(this, "사진은 10개까지 선택가능 합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // 멀티 선택에서 하나만 선택했을 경우
                        else if (clipData.getItemCount() == 1) {
                            String dataStr = String.valueOf(clipData.getItemAt(0).getUri());
                            imageList.add(dataStr);
                        } else if (clipData.getItemCount() > 1 && clipData.getItemCount() < 10) {
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                imageList.add(String.valueOf(clipData.getItemAt(i).getUri()));
                            }
                        }
                    }

                    for (int i = index; i < imageList.size(); i++) {
                        String flag = imageList.get(i).substring(0, 7);
                        if (flag.equals("content")) {
                            // content:// -> /storage... (포토)
                            String path = getRealPathFromURI(Uri.parse(imageList.get(i)));
                            imageList.remove(i);
                            imageList.add(i, path);
                        } else {
                            // 갤러리 : 파일 절대 경로 리턴함(변환은 필요없고, file://를 빼줘야 업로드시 new File에서 이용)
                            String path = imageList.get(i).replace("file://", "");
                            imageList.remove(i);
                            imageList.add(i, path);
                        }
                    }
                    galleryViewAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "사진 선택을 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            //카메라로 찍어서 첨부할떄
            case REQUEST_FROM_CAMERA: {

                String dataStr = String.valueOf(data.getData());
                imageList.add(dataStr);
                galleryViewAdapter.notifyDataSetChanged();
               /* if (resultCode == Activity.RESULT_OK && data.hasExtra("data")) {
                    //String dataStr = String.valueOf(data.getExtras().get("data"));
                    imageList.add(String.valueOf(data.getData()));
                    galleryViewAdapter.notifyDataSetChanged();
                }
                if (resultCode == Activity.RESULT_OK) {
                    imageList.add(tempFile.getAbsolutePath());
                }

                else if (resultCode != Activity.RESULT_OK) {
                    Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

                    if (tempFile != null) {
                        if (tempFile.exists()) {
                            if (tempFile.delete())
                                tempFile = null;
                        }
                    }
                }*/
            }
        }
    }
    //content://로 시작하는 경로명을 바꿔주는 함수
    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
