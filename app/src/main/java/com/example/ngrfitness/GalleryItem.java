package com.example.ngrfitness;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.graphics.Bitmap;
import android.graphics.Picture;
import android.net.Uri;

public class GalleryItem {
    Bitmap image;

    public GalleryItem(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }


}
