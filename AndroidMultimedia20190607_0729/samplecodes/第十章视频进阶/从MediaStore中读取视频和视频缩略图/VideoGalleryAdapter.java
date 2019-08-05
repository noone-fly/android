package com.example.chenpiyang.videogallery;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenpiyang on 2019/7/27.
 */

class VideoGalleryAdapter extends BaseAdapter {
    private Context context;
    private List<VideoViewInfo> videoItems;
    LayoutInflater inflater;
    public VideoGalleryAdapter(Context _context, ArrayList<VideoViewInfo> _items){
        context = _context;
        videoItems = _items;
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return videoItems.size();
    }

    @Override
    public Object getItem(int i) {
        return videoItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View videoRow = inflater.inflate(R.layout.list_item, null);
        ImageView videoThumb = (ImageView)videoRow.findViewById(R.id.ImageView);
        if (videoItems.get(i).thumbPath != null){
            videoThumb.setImageURI(Uri.parse(videoItems.get(i).thumbPath));
        }
        TextView videoTitle = (TextView)videoRow.findViewById(R.id.TextView01);
        videoTitle.setText(videoItems.get(i).title);
        return videoRow;
    }
}
