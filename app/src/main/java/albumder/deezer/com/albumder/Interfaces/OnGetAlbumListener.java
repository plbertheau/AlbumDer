package albumder.deezer.com.albumder.Interfaces;

import java.util.ArrayList;

import albumder.deezer.com.albumder.Models.Album;

/**
 * Created by plbertheau on 30/06/16.
 */
public interface OnGetAlbumListener {

    void onAlbumReceive(ArrayList<Album> _list);
    void onAlbumReveiveFailed(String _result);
    void onAlbumReceiveException(Exception _exception);

}
