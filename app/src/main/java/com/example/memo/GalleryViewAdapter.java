package com.example.memo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class GalleryViewAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<String> img;
    LayoutInflater inf;
    Handler handler = new Handler();

    public GalleryViewAdapter(Context context, int layout, ArrayList<String> img) {
        this.context = context;
        this.layout = layout;
        this.img = img;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return img.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inf.inflate(layout, null);
        }

        final ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView1);
        /*String flag = img.get(position).substring(0, 7);

        if(flag.equals("http://")) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        URL url = new URL(img.get(position));
                        InputStream is = url.openStream();
                        final Bitmap myBitmap = BitmapFactory.decodeStream(is);
                        handler.post(new Runnable() {

                            @Override
                            public void run() {  // 화면에 그려줄 작업
                                imageView.setImageBitmap(myBitmap);
                            }
                        });
                        imageView.setImageBitmap(myBitmap);

                    } catch(Exception e){

                    }
                }
            });
            t.start();
        }

        else{
            File imgFile = new  File(img.get(position));

            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
            }
        }*/
        File imgFile = new  File(img.get(position));

        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }

        return convertView;
    }
}
