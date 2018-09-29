package com.hitchtransporter.smart.common;

import android.net.Uri;

/**
 * Created by ebiztrait321 on 11/7/17.
 */

public class Object_Image {
    Uri imageUri;
    String imageName;

    public Object_Image(Uri imageUri, String imageName) {
        this.imageUri = imageUri;
        this.imageName = imageName;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
