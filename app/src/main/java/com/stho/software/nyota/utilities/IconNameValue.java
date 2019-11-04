package com.stho.software.nyota.utilities;

/**
 * Created by shoedtke on 28.09.2016.
 */
public class IconNameValue implements IIconNameValue {

    private final int imageId;
    private final String name;
    private final String value;

    @Override
    public int getImageId() { return imageId; }

    @Override
    public String getName() { return name; }

    @Override
    public String getValue() { return value; }


    public IconNameValue(int imageId, String name, String value) {
        this.imageId = imageId;
        this.name = name;
        this.value = value;
    }
}

