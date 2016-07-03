package albumder.deezer.com.albumder.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import albumder.deezer.com.albumder.Models.Album;
import albumder.deezer.com.albumder.R;
import albumder.deezer.com.albumder.Tools.RamCache;
import albumder.deezer.com.albumder.WebServices.ImageDownloader;

/**
 * Created by plbertheau on 30/06/16.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Album> mAlbum = new ArrayList<>();

    // Constructor
    public ImageAdapter(Context _ctx, ArrayList<Album> _album) {
        this.mAlbum = _album;
        this.mContext = _ctx;
    }

    public int getCount() {
        return mAlbum.size();
    }

    public Object getItem(int position) {
        return mAlbum.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        final Album current = (Album) getItem(position);

        Holder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_image_layout, parent, false);

            holder = new Holder();
            holder.ivAlbum = (ImageView) convertView.findViewById(R.id.iv_album);
            holder.ivArtist = (ImageView) convertView.findViewById(R.id.iv_artist);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.ivAlbum.setImageResource(R.drawable.cd_vierge);
        holder.ivArtist.setImageResource(R.drawable.cd_vierge);
        holder.ivAlbum.setVisibility(View.VISIBLE);
        holder.ivArtist.setVisibility(View.INVISIBLE);

        final Bitmap bitmapAlbum = RamCache.getInstance().getBitmapFromMemCache((current.getCoverFileName()));
        final Bitmap bitmapArtist = RamCache.getInstance().getBitmapFromMemCache((current.getArtist().getPictureFileName()));

        if (bitmapAlbum != null) {
            holder.ivAlbum.setImageBitmap(bitmapAlbum);
        } else {
            //launch download of image album
            ImageDownloader id = new ImageDownloader(holder.ivAlbum, mContext, true);
            id.execute(current.getCover_medium());
        }
        if (bitmapArtist != null) {
            holder.ivArtist.setImageBitmap(bitmapArtist);
        } else {
            //launch download of image artist
            ImageDownloader id = new ImageDownloader(holder.ivArtist, mContext, false);
            id.execute(current.getArtist().getPicture_medium());
        }

        //Flip animation
        final Holder finalHolder = holder;
        final Animation animation1 = AnimationUtils.loadAnimation(mContext, R.anim.to_middle);
        final Animation animation2 = AnimationUtils.loadAnimation(mContext, R.anim.from_middle);
        final Animation animation3 = AnimationUtils.loadAnimation(mContext, R.anim.to_middle);
        final Animation animation4 = AnimationUtils.loadAnimation(mContext, R.anim.from_middle);

        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finalHolder.ivAlbum.clearAnimation();
                finalHolder.ivArtist.clearAnimation();
                finalHolder.ivAlbum.setVisibility(View.INVISIBLE);

                finalHolder.ivArtist.setAnimation(animation2);
                finalHolder.ivArtist.startAnimation(animation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO nothing
            }
        });

        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finalHolder.ivArtist.setVisibility(View.VISIBLE);
                finalHolder.ivArtist.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO nothing
            }
        });

        animation3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finalHolder.ivArtist.clearAnimation();
                finalHolder.ivAlbum.clearAnimation();

                finalHolder.ivArtist.setVisibility(View.INVISIBLE);
                finalHolder.ivAlbum.setAnimation(animation4);
                finalHolder.ivAlbum.startAnimation(animation4);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO nothing
            }
        });

        animation4.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finalHolder.ivAlbum.setVisibility(View.VISIBLE);
                finalHolder.ivAlbum.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO nothing
            }
        });

        holder.ivAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalHolder.ivArtist.setVisibility(View.INVISIBLE);
                finalHolder.ivAlbum.setVisibility(View.VISIBLE);
                finalHolder.ivAlbum.clearAnimation();
                finalHolder.ivArtist.clearAnimation();
                finalHolder.ivAlbum.setAnimation(animation1);
                finalHolder.ivAlbum.startAnimation(animation1);
            }
        });

        holder.ivArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalHolder.ivAlbum.setVisibility(View.INVISIBLE);
                finalHolder.ivArtist.setVisibility(View.VISIBLE);
                finalHolder.ivArtist.clearAnimation();
                finalHolder.ivAlbum.clearAnimation();
                finalHolder.ivArtist.setAnimation(animation3);
                finalHolder.ivArtist.startAnimation(animation3);
            }
        });

        return convertView;
    }

    public void updateData(ArrayList<Album> album) {
        this.mAlbum.addAll(album);
    }

    class Holder {
        private ImageView ivAlbum;
        private ImageView ivArtist;
    }

}