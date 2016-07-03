package albumder.deezer.com.albumder.WebServices;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * User this abstract for future Webservice
 *
 *
 * Created by plbertheau on 29/06/16.
 */
public abstract class WSAbstractBufferTask<E> {

    public static final Long DELAY_ALWAYS_ALIVE = null;
    public static final Long DELAY_NEVER_ALIVE = -1L;
    public static final Long DELAY_1_SEC = 1000L;
    public static final Long DELAY_5_SEC = 1000L * 5;
    public static final Long DELAY_10_SEC = 1000L * 10;
    public static final Long DELAY_1_MINUTE = 1000L * 60;

    private final Long mCacheDelay;

    private static String TAG = WSAbstractBufferTask.class.getName();

    private static Map<String, Object> mBufferedData = new HashMap<String, Object>();
    private static Map<String, Long> mBufferedCache = new HashMap<String, Long>();

    public WSAbstractBufferTask(Long _cacheDelay) {
        this.mCacheDelay = _cacheDelay;
    }

    public void start() {
        new Thread() {
            @Override
            public void run() {
                final String key = key();
                E result = null;
                result = loadData(key);
                if (result == null) {
                    try {
                        result = execute();
                    } catch (Exception e) {
                        onFailed(e);
                    }
                    saveData(key, result);
                } else {
                    Log.d(TAG, "Data available, DO load from CACHE, key = " + innerKey(key));
                }
                onResult(result);
            }
        }.start();
    }

    public abstract String key();
    public abstract E execute() throws Exception;
    public abstract void onResult(E result);
    public abstract void onFailed(Exception exc);

    public boolean hasBufferedData(String _key) {
        if (isCacheAlive(_key)) {
            return mBufferedData.get(innerKey(_key)) != null;
        }
        return false;
    }

    public boolean isCacheAlwaysAlive() {
        return mCacheDelay == null;
    }
    public boolean isCacheDisabled() {
        return mCacheDelay != null && mCacheDelay < 0;
    }

    public boolean isCacheAlive(String _key) {
        if (isCacheAlwaysAlive()) {
            return true;
        }
        if (isCacheDisabled()) {
            return false;
        }
        if (mBufferedCache.get(innerKey(_key)) != null) {
            return mBufferedCache.get(innerKey(_key)) + mCacheDelay > System.currentTimeMillis();
        }
        return false;
    }

    public synchronized void saveData(String _key, E _data) {
        mBufferedData.put(innerKey(_key), _data);
        mBufferedCache.put(innerKey(_key), System.currentTimeMillis());
    }

    public synchronized  E loadData(String _key) {
        if (hasBufferedData(_key)) {
            E dataFound = (E) mBufferedData.get(innerKey(_key));
            return dataFound;
        }
        return null;
    }

    public static synchronized void clearData(Class<?> T, String _key) {
        mBufferedData.put(genInnerKey(T, _key), null);
        mBufferedCache.put(genInnerKey(T, _key), null);
    }

    public static String genInnerKey(Class<?> T, String _key) {
        return T.getSimpleName() + "_" + _key;
    }
    private String innerKey(String _key) {
        return genInnerKey(this.getClass(), _key);
    }
}

