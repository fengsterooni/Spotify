package com.udacity.android.spotify.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class ImageUtils {

    public static final int IMAGE_HEIGHT = 120;
    public static final int IMAGE_WIDTH = 120;

    private static LruCache<String, Bitmap> imageCache;

    public static void setupMemoryCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        imageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String url, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static void addToImageCache(String url, Bitmap bitmap) {
        if (getFromImageCache(url) == null) {
            imageCache.put(url, bitmap);
        }
    }

    public static Bitmap downloadBitmap(String uri)
            throws IOException {
        URL url = new URL(uri);
        Bitmap bitmap = null;
        BufferedInputStream inputStream = null;
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap = BitmapScaler.scaleToFill(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return bitmap;
    }

    public static Bitmap getFromImageCache(final String url) {
        Bitmap bitmap = imageCache.get(url);

        if (bitmap != null)
            return bitmap;

        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void[] objects) {
                Bitmap bitmap;
                try {
                    bitmap = downloadBitmap(url);
                    imageCache.put(url, bitmap);
                } catch (IOException e) {
                    return null;
                }
                return bitmap;
            }
        }.execute();

        return bitmap;
    }

    public static void removeFromimageCache(String url) {
        if (getFromImageCache(url) != null) {
            imageCache.remove(url);

            Log.i("INFO", "Cache size: " + imageCache.size());
        }
    }

    public static void removeimageCache(List<String> list) {
        if (list != null) {
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                removeFromimageCache(it.next());
            }
        }
    }
}
