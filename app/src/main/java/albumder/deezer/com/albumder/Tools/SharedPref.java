package albumder.deezer.com.albumder.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by plbertheau on 01/07/16.
 */
public class SharedPref {

    /** The shared preferences*/
    static SharedPreferences sp;
    /** The editor */
    static SharedPreferences.Editor editor;

    private static final String TAG = "SharedPref";

    /**
     * Init shared pref
     * @param ctx
     */
    @SuppressLint("CommitPrefEdits")
    public static void init(Context ctx){
        if (sp==null) {
            sp=ctx.getSharedPreferences("DEEZER", Context.MODE_PRIVATE);
            editor = sp.edit();
        }
    }

    public static void saveImagePath(Context ctx, String urlKey,String path) {
        init(ctx);

        editor.putString(urlKey, path);
        editor.commit();
    }

    public static String getImagePath(Context ctx,String urlKey){
        init(ctx);
        return sp.getString(urlKey, "");
    }

    public static void resetAllPreferences(Context ctx){
        init(ctx);
        sp.edit().clear().commit();
    }
}
