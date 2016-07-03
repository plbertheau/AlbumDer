package albumder.deezer.com.albumder.Tools;

import android.os.Environment;

import java.io.File;

/**
 * Created by plbertheau on 29/06/16.
 */
public class Tools {

    /**
     * Clear external storage files.
     */
    public static void clear() {
        File[] directory = Environment.getExternalStorageDirectory().listFiles();
        if(directory != null){
            for (File file : directory ){
                file.delete();
            }
        }
    }

}
