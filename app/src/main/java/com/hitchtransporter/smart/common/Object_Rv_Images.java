package com.hitchtransporter.smart.common;

/**
 * Created by ebiztrait321 on 12/7/17.
 */

public class Object_Rv_Images {
    int isStarter;
    String imgPath;

    public Object_Rv_Images(int isStarter, String imgPath) {
        this.isStarter = isStarter;
        this.imgPath = imgPath;
    }

    public int getIsStarter() {
        return isStarter;
    }

    public void setIsStarter(int isStarter) {
        this.isStarter = isStarter;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
