package albumder.deezer.com.albumder.WebServices;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import albumder.deezer.com.albumder.Interfaces.OnGetAlbumListener;
import albumder.deezer.com.albumder.Models.Album;
import albumder.deezer.com.albumder.Models.Artist;

/**
 * Created by plbertheau on 29/06/16.
 */
public class WSGetAlbumFromDeezer extends WSAbstractBufferTask<ArrayList<Album>> {


    private static String TAG = WSGetAlbumFromDeezer.class.getName();
    private Context ctx;
    private int code;
    private OnGetAlbumListener listener;

    private String key = "http://api.deezer.com/2.0/user/2529/albums";

    private Exception excep = null;

    public WSGetAlbumFromDeezer(final Context ctx, OnGetAlbumListener listener) {
        super(DELAY_NEVER_ALIVE);
        this.ctx = ctx;
        this.listener = listener;
    }

    @Override
    public String key() {
//        return ctx.getResources().getString(R.string.server_path);
        return key;
    }

    @Override
    public ArrayList<Album> execute() throws Exception {
        Log.e(TAG, "execute");
        String result = getDataFromUrl(key());
        ArrayList<Album> albumList = parseResult(new JSONObject(result));
        return albumList;

    }

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

            InputStream is = urlConnection.getInputStream();
            if (is != null) {
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is));
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();
                } finally {
                    is.close();
                }
                json_response = sb.toString();
            }
        } catch (Exception e) {
            Log.e(TAG, "#### ERRREUR ####", e);
            json_response = null;
        }
        Log.d(TAG, "getDataFromUrl json_response :"+json_response);
        return json_response;
    }

    @Override
    public void onResult(ArrayList<Album> result) {
        Log.e(TAG, " onResult : " + result);
        if(code ==200){
            listener.onAlbumReceive(result);
        }else{
            listener.onAlbumReveiveFailed(String.valueOf(code));
        }
    }

    @Override
    public void onFailed(Exception exc) {
        Log.e(TAG, " End error : ", exc);
        listener.onAlbumReceiveException(exc);
    }

    /**
     * Parsing result from WS to fill Album and artist models.
     *
     * @param result
     * @return
     */
    private ArrayList<Album> parseResult(JSONObject result){
        ArrayList<Album> listAlbum = new ArrayList<>();
        JSONArray albumJson = new JSONArray();
        try {
            if (result.has("data") && !result.isNull("data")) {
                albumJson = result.getJSONArray("data");
            }

            for (int k = 0; k < albumJson.length(); k++) {
                JSONObject albumObj = (JSONObject) albumJson.get(k);
                Album album = new Album();
                int albumId = 0;
                String albumTitle = "";
                String albumLink ="";
                String albumCover ="";
                String albumCoverSmall ="";
                String albumCoverMedium ="";
                String albumCoverBig ="";
                int albumNbTracks = 0;
                String albumReleaseDate="";
                String albumRecordType ="";
                boolean albumAvailable = false;
                String albumTracklist ="";
                boolean albumExplicitLyrics = false;
                int albumTimeAdd=0;
                Artist albumArtist = new Artist();
                String albumType ="";
                if (albumObj.has("id") && !albumObj.isNull("id")) {
                    albumId = albumObj.getInt("id");
                }
                if (albumObj.has("title") && !albumObj.isNull("title")) {
                    albumTitle = albumObj.getString("title");
                }
                if (albumObj.has("link") && !albumObj.isNull("link")) {
                    albumLink = albumObj.getString("link");
                }
                if (albumObj.has("cover") && !albumObj.isNull("cover")) {
                    albumCover = albumObj.getString("cover");
                }
                if (albumObj.has("cover_small") && !albumObj.isNull("cover_small")) {
                    albumCoverSmall = albumObj.getString("cover_small");
                }
                if (albumObj.has("cover_medium") && !albumObj.isNull("cover_medium")) {
                    albumCoverMedium = albumObj.getString("cover_medium");
                }
                if (albumObj.has("cover_big") && !albumObj.isNull("cover_big")) {
                    albumCoverBig = albumObj.getString("cover_big");
                }
                if (albumObj.has("nb_tracks") && !albumObj.isNull("nb_tracks")) {
                    albumNbTracks = albumObj.getInt("nb_tracks");
                }
                if (albumObj.has("release_date") && !albumObj.isNull("release_date")) {
                    albumReleaseDate = albumObj.getString("release_date");
                }
                if (albumObj.has("record_type") && !albumObj.isNull("record_type")) {
                    albumRecordType = albumObj.getString("record_type");
                }
                if (albumObj.has("available") && !albumObj.isNull("available")) {
                    albumAvailable = albumObj.getBoolean("available");
                }
                if (albumObj.has("tracklist") && !albumObj.isNull("tracklist")) {
                    albumTracklist = albumObj.getString("tracklist");
                }
                if (albumObj.has("explicit_lyrics") && !albumObj.isNull("explicit_lyrics")) {
                    albumExplicitLyrics = albumObj.getBoolean("explicit_lyrics");
                }
                if (albumObj.has("time_add") && !albumObj.isNull("time_add")) {
                    albumTimeAdd = albumObj.getInt("time_add");
                }
                if (albumObj.has("type") && !albumObj.isNull("type")) {
                    albumType = albumObj.getString("type");
                }
                if (albumObj.has("artist") && !albumObj.isNull("artist")) {
                    JSONObject artistObj = new JSONObject();
                    artistObj = albumObj.getJSONObject("artist");
                    int artistId =0;
                    String artistName="";
                    String artistPicture="";
                    String artistPictureSmall="";
                    String artistPictureMedium="";
                    String artistPictureBig="";
                    String artistTracklist="";
                    String artistType="";
                    if (artistObj.has("id") && !artistObj.isNull("id")) {
                        artistId = artistObj.getInt("id");
                    }
                    if (artistObj.has("name") && !artistObj.isNull("name")) {
                        artistName = artistObj.getString("name");
                    }
                    if (artistObj.has("picture") && !artistObj.isNull("picture")) {
                        artistPicture = artistObj.getString("picture");
                    }
                    if (artistObj.has("picture_small") && !artistObj.isNull("picture_small")) {
                        artistPictureSmall = artistObj.getString("picture_small");
                    }
                    if (artistObj.has("picture_medium") && !artistObj.isNull("picture_medium")) {
                        artistPictureMedium = artistObj.getString("picture_medium");
                    }
                    if (artistObj.has("picture_big") && !artistObj.isNull("picture_big")) {
                        artistPictureBig = artistObj.getString("picture_big");
                    }
                    if (artistObj.has("tracklist") && !artistObj.isNull("tracklist")) {
                        artistTracklist = artistObj.getString("tracklist");
                    }
                    if (artistObj.has("type") && !artistObj.isNull("type")) {
                        artistType = artistObj.getString("type");
                    }
                    albumArtist.setId(artistId);
                    albumArtist.setName(artistName);
                    albumArtist.setPicture(artistPicture);
                    albumArtist.setPicture_small(artistPictureSmall);
                    albumArtist.setPicture_medium(artistPictureMedium);
                    albumArtist.setPicture_big(artistPictureBig);
                    albumArtist.setTracklist(artistTracklist);
                    albumArtist.setType(artistType);
                }

                album.setId(albumId);
                album.setTitle(albumTitle);
                album.setLink(albumLink);
                album.setCover(albumCover);
                album.setCover_small(albumCoverSmall);
                album.setCover_medium(albumCoverMedium);
                album.setCover_big(albumCoverBig);
                album.setNb_tracks(albumNbTracks);
                album.setRelease_date(albumReleaseDate);
                album.setRecord_type(albumRecordType);
                album.setAvailable(albumAvailable);
                album.setTracklist(albumTracklist);
                album.setExplicit_lyrics(albumExplicitLyrics);
                album.setTime_add(albumTimeAdd);
                album.setArtist(albumArtist);
                album.setType(albumType);
                listAlbum.add(album);
            }


        }catch (Exception e){
            Log.e(TAG,"Exception parse ", e );
        }


        return listAlbum;
    }

}
