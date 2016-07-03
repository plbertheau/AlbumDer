package albumder.deezer.com.albumder.WebServices;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import albumder.deezer.com.albumder.R;
import albumder.deezer.com.albumder.Tools.RamCache;
import albumder.deezer.com.albumder.Tools.SharedPref;

/**
 * Created by plbertheau on 30/06/16.
 */
public class ImageDownloader extends AsyncTask<String,Void,String> {

    private String TAG = ImageDownloader.class.getName();
    private int code;
    private ImageView myImage;
    private Context context;
    private String url;
    private boolean isAlbum;
    private int FADE_IN_TIME =1000;

    public ImageDownloader(ImageView _ivAdapter, Context _ctx, boolean _isAlbum) {
        this.myImage = _ivAdapter;
        this.context = _ctx;
        this.isAlbum = _isAlbum;
    }

    @Override
    protected String doInBackground(String... param) {
        Log.d(TAG, "doInBackground params : "+param[0]);
        url = param[0].replaceAll("/","");

        String path = SharedPref.getImagePath(context,url);
        //if path exist in sharedpref show the image from dir or download it.
        if (path.equals("")) {
            return getDataFromUrl(param[0]);
        } else {
            return path;
        }
    }

    @Override
    protected void onPreExecute() {
        Log.i(TAG, "onPreExecute Called");
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute Called result : "+result);

        //Display image.
        Bitmap myBitmap = BitmapFactory.decodeFile(result);
        //Save in cache RAM
        RamCache.getInstance().addBitmapToMemoryCache(url,myBitmap);

        if (isAlbum) {

            // Transition drawable with a transparent drwabale and the final bitmap
            final TransitionDrawable td =
                    new TransitionDrawable(new Drawable[]{
                            new ColorDrawable(Color.TRANSPARENT),
                            new BitmapDrawable(context.getResources(), myBitmap)
                    });

            // Set background to loading bitmap
            myImage.setImageResource(R.drawable.cd_vierge);
            myImage.setImageDrawable(td);
            td.startTransition(FADE_IN_TIME);
        } else {
            myImage.setImageBitmap(myBitmap);
        }
    }

    /**
     * Download image from url
     *
     * @param url
     * @return
     */
    public String getDataFromUrl(String url) {

        Log.d(TAG, "getDataFromUrl");
        String json_response = null;
        try {
            URL myurl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) myurl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            code = urlConnection.getResponseCode();

            Log.d(TAG, " status code : " + code);
            if (code != 200) {
                Log.w(TAG, "Error " + code +
                        " while retrieving bitmap from " + url);
                return null;

            }

            InputStream is = urlConnection.getInputStream();
            if (is != null) {
                try {


                    // decoding stream data back into image Bitmap that android understands
                    final Bitmap bitmap = BitmapFactory.decodeStream(is);

                    return SaveBitmapToDir(bitmap, url);
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "#### ERRREUR ####", e);
            json_response = null;
        }

        return null;
    }

    /**
     * Save Bitmap to dir
     *
     * @param bmp
     * @param url
     * @return
     */
    private String SaveBitmapToDir(Bitmap bmp, String url)
    {
        Log.d(TAG, "SaveBitmapToDir bmp : "+bmp);
        Log.d(TAG, "SaveBitmapToDir url : "+url);
        url = url.replaceAll("/","");
        FileOutputStream out = null;
        File file = null;
        try {

            String path = Environment.getExternalStorageDirectory().toString();
            file = new File(path, url);
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            SharedPref.saveImagePath(context, url,file.getAbsolutePath());

        } catch (Exception e) {
            Log.e(TAG,"EXCEPTION ",e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                return file.getAbsolutePath();
            } catch (IOException e) {
                Log.e(TAG,"EXCEPTION 2 ",e);
            }
        }
        return null;
    }

}

