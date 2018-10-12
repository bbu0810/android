package com.hitchtransporter.smart.common;

/**
 * Created by ebiztrait321 on 22/6/17.
 */

public class ObjectEditImage {
    String imageType,imageUrl;
    int isStarter;


    public ObjectEditImage(int isStarter,String imageType, String imageUrl) {
        this.imageType = imageType;
        this.imageUrl = imageUrl;
        this.isStarter = isStarter;
    }

    public int getIsStarter() {
        return isStarter;
    }

    public void setIsStarter(int isStarter) {
        this.isStarter = isStarter;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
