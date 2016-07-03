package albumder.deezer.com.albumder.Tools;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by plbertheau on 02/07/16.
 */
public class RamCache {

    private static RamCache mInstance = null;

    private LruCache<String, Bitmap> mMemoryCache;

    // Singleton instance
    public RamCache() {
        // Initialize memory cache
        this.initCache();
    }

    // Getter to access Singleton instance
    public static RamCache getInstance() {
        if (mInstance == null) {
            mInstance = new RamCache();
        }
        return mInstance ;
    }

    public static void setInstance(RamCache ramCache){
        mInstance = ramCache;
    }

    private void initCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }


    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            if(key != null && bitmap != null) {
                mMemoryCache.put(key, bitmap);
            }
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
