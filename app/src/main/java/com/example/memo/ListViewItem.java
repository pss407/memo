package com.example.memo;

import android.graphics.Bitmap;

public class ListViewItem {

    private String titleStr;
    private String descStr;
    private String dateStr;
    private Bitmap thumbBitmap;

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setDesc(String desc) {
        descStr = desc;
    }

    public void setDate(String date) { dateStr = date; }

    public void setThumb(Bitmap thumb) { thumbBitmap = thumb;}

    public String getTitle() {
        return this.titleStr;
    }

    public String getDesc() {
        return this.descStr;
    }

    public String getDate() {return this.dateStr;}

    public Bitmap getThumb() {return this.thumbBitmap;}
}
