package albumder.deezer.com.albumder.Activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import albumder.deezer.com.albumder.Adapters.ImageAdapter;
import albumder.deezer.com.albumder.Interfaces.OnGetAlbumListener;
import albumder.deezer.com.albumder.Models.Album;
import albumder.deezer.com.albumder.R;
import albumder.deezer.com.albumder.Tools.RamCache;
import albumder.deezer.com.albumder.Tools.SharedPref;
import albumder.deezer.com.albumder.Tools.Tools;
import albumder.deezer.com.albumder.WebServices.WSGetAlbumFromDeezer;

public class MainActivity extends AppCompatActivity implements OnGetAlbumListener {

    private ArrayList<Album> mAlbumList = new ArrayList<>();

    private LinearLayout mEmptyScreen;
    private GridView mGridview;
    private ImageAdapter mAdapter;

    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //For API>21 hide notification bar
        this.updateNotificationColor();

        //init UI
        this.setContentView(R.layout.activity_main);

        mGridview = (GridView) findViewById(R.id.gridview);
        mEmptyScreen = (LinearLayout) findViewById(R.id.ll_empty_screen);
        mAdapter = new ImageAdapter(this, mAlbumList);
        mGridview.setAdapter(mAdapter);

        //launch ws to get list of album
        WSGetAlbumFromDeezer getAlbum = new WSGetAlbumFromDeezer(this,this);
        getAlbum.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu in action bar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_clear:

                //Clear cache system
                Tools.clear();
                SharedPref.resetAllPreferences(this);

                //Clear cache RAM
                RamCache.setInstance(null);

                //Reinit adapter
                mAlbumList.clear();
                mAdapter.updateData(mAlbumList);
                mAdapter.notifyDataSetChanged();
                Snackbar.make(findViewById(R.id.gridview),
                        getResources().getString(R.string.clear_cache),
                        Snackbar.LENGTH_LONG)
                        .show();
                return true;

            default:

                return super.onOptionsItemSelected(item);
        }
    }


    /*
        Reponse to WebService
     */

    @Override
    public void onAlbumReceive(final ArrayList<Album> list) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //update data in adapter
                mGridview.setVisibility(View.VISIBLE);
                mEmptyScreen.setVisibility(View.GONE);
                mAlbumList = list;
                mAdapter.updateData(mAlbumList);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onAlbumReveiveFailed(String result) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGridview.setVisibility(View.GONE);
                mEmptyScreen.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onAlbumReceiveException(Exception exception) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGridview.setVisibility(View.GONE);
                mEmptyScreen.setVisibility(View.VISIBLE);
            }
        });
    }

    @SuppressLint("NewApi")
    private void updateNotificationColor() {
        if (Build.VERSION.SDK_INT > 21){
            Window window = this.getWindow();
            window.addFlags(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }
}
