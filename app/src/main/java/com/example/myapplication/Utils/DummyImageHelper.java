package com.example.myapplication.Utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.example.myapplication.R;

/**
 * Utility class to handle dummy images instead of using Firebase Storage
 */
public class DummyImageHelper {

    private static final String DUMMY_IMAGE_URL = "https://lh3.googleusercontent.com/yjDoBdvT5hee7GpGXk5fSi43sU0E_4_f2YeopUW99NJODjcMWAHbDWhkLO6KvjwTXvjQwlyRR0gQx2w2CnGfyohY8ETkGVzVwo-O5ti6uk8gaHecDEMA4w4yyiCAHepf29ZGXE8M";

    /**
     * Get a dummy image URI that can be used instead of gallery selection
     * @param context Application context
     * @return Uri of the dummy image
     */
    public static Uri getDummyImageUri(Context context) {
        // Create a Uri from the dummy image resource
        return Uri.parse("android.resource://" + context.getPackageName() + "/" + R.drawable.dummy_image);
    }

    /**
     * Set the dummy image to an ImageView
     * @param imageView The ImageView to set the dummy image to
     */
    public static void setDummyImage(ImageView imageView) {
        // Set the dummy image resource to the ImageView
        imageView.setImageResource(R.drawable.dummy_image);
    }

    /**
     * Get a dummy download URL that can be used instead of Firebase Storage URL
     * @return String representing a dummy URL
     */
    public static String getDummyDownloadUrl() {
        // Return a dummy URL that can be used in place of Firebase Storage URLs
        return DUMMY_IMAGE_URL;
    }
}
