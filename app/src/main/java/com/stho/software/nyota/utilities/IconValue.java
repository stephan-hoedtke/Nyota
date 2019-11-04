package com.stho.software.nyota.utilities;

/**
 * Created by shoedtke on 28.09.2016.
 */
public class IconValue implements IIconValue {

    private final int imageId;
    private final String value;

    @Override
    public int getImageId() { return imageId; }

    @Override
    public String getValue() { return value; }


    public IconValue(int imageId, String value) {
        this.imageId = imageId;
        this.value = value;
    }
}

