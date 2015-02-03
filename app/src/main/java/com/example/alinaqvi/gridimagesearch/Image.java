package com.example.alinaqvi.gridimagesearch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alinaqvi on 2/2/15.
 */
public class Image implements Parcelable {
    public String tbUrl;
    public String url;
    public String title;
    public int width;
    public int height;

    public Image() {

    }

    public Image(Parcel in) {
        Object[] data = in.readArray(this.getClass().getClassLoader());
        tbUrl = (String) data[0];
        url = (String) data[1];
        title = (String) data[2];
        width = (Integer) data[3];
        height = (Integer) data[4];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArray(new Object[]{tbUrl, url, title, width, height});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
