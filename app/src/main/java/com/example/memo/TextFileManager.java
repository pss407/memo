package com.example.memo;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TextFileManager {
    Context mContext = null;

    public TextFileManager(Context context) {
        mContext = context;
    }

    public void save(String strData, String FILE_NAME) {
        FileOutputStream fosMemo = null;

        try {
            // 파일에 데이터를 쓰기 위해서 output 스트림 생성
            fosMemo = mContext.openFileOutput(FILE_NAME,Context.MODE_PRIVATE); //Context.MODE_PRIVATE: 덮어쓰기 Context.MODE_APPEND: 이어쓰기

            // 파일에 메모 적기
            fosMemo.write(strData.getBytes());
            fosMemo.close();

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    // 파일에 메모를 저장하는 함수
    public void saveWithImg(String strData, String FILE_NAME, ArrayList imageList) {
        FileOutputStream fosMemo = null;
        FileOutputStream fosImg = null;
        String strImg = "";

        try {
            // 파일에 데이터를 쓰기 위해서 output 스트림 생성
            fosMemo = mContext.openFileOutput(FILE_NAME,Context.MODE_PRIVATE); //Context.MODE_PRIVATE: 덮어쓰기 Context.MODE_APPEND: 이어쓰기

            // 파일에 메모 적기
            fosMemo.write(strData.getBytes());
            fosMemo.close();

            File imgDir = new File("/data/data/com.example.memo/files/Images/");
            if (! imgDir.exists()){
                if (! imgDir.mkdirs()){
                    Log.d("MyCameraApp", "failed to create director y");
                }
            }
            fosImg = new FileOutputStream(new File("/data/data/com.example.memo/files/Images/"+FILE_NAME));

            //파일에 이미지 경로 저장
            for(int i=0; i<imageList.size(); i++) {
                if(i == imageList.size()-1)
                    strImg = strImg+imageList.get(i);
                else
                    strImg = strImg+imageList.get(i)+" ";
            }
            fosImg.write(strImg.getBytes());
            fosImg.close();

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    // 저장된 메모를 불러오는 함수
    public String load(String FILE_NAME){
        try{
            // 파일에서 데이터를 읽기 위해서 input 스트림 생성
            FileInputStream fisMemo = mContext.openFileInput(FILE_NAME);

            // 데이터를 읽어 온 뒤 , String 타입 객체로 반환
            byte[] memoData = new byte[fisMemo.available()];
            while(fisMemo.read(memoData)!= -1) {}

            return new String(memoData);
        }catch (IOException e){}

        return "";
    }

    // 저장된 메모의 이미지를 불러오는 함수
    public String loadImg(File imgFile){
        try{
            // 파일에서 데이터를 읽기 위해서 input 스트림 생성
            FileInputStream fisImg = new FileInputStream(imgFile);

            // 데이터를 읽어 온 뒤 , String 타입 객체로 반환
            byte[] imgData = new byte[fisImg.available()];
            while(fisImg.read(imgData)!= -1) {}

            return new String(imgData);
        }catch (IOException e){}

        return "";
    }

    public String getDate(String FILE_NAME) {

        File file = new File("/data/data/com.example.memo/files/"+FILE_NAME);

        long lastModified = file.lastModified();

        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        Date lastModifiedDate = new Date( lastModified );

        return simpleDateFormat.format( lastModifiedDate );
    }

    // 저장된 메모를 삭제하는 함수
    public void delete(String FILE_NAME) {
        File imgDir = new File("/data/data/com.example.memo/files/Images/"+FILE_NAME);
        if(imgDir.exists())
            imgDir.delete();
        mContext.deleteFile(FILE_NAME);
    }

    public String[] viewMemo() {
        File f = new File("/data/data/com.example.memo/files");
        File[] files = f.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase(Locale.US).endsWith(".txt"); //확장자
            }
        });
        String[] list= new String[files.length];
        for(int i=0; i<files.length; i++) {
            list[i] = files[i].getName();
        }
        return list;
    }
}
