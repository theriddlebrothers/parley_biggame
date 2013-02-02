package com.theriddlebrothers.parleybiggame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * List adapter for tweets
 */
public class TweetListAdapter extends ArrayAdapter<Tweet> {
    private ArrayList<Tweet> tweets;
    private Context context;
    public TweetListAdapter(Context context,
                            int textViewResourceId,
                            ArrayList<Tweet> items) {
        super(context, textViewResourceId, items);
        this.tweets = items;
        this.context = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item, null);
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        Tweet o = tweets.get(position);
        TextView tt = (TextView) v.findViewById(R.id.message_text);
        TextView bt = (TextView) v.findViewById(R.id.details_text);
        ImageView iv = (ImageView) v.findViewById(R.id.list_image);
        tt.setText(o.content);
        bt.setText("@" + o.author + " at " + timeFormat.format(o.dateCreated)
                + " on " + dateFormat.format(o.dateCreated));

        // Download the user's profile image
        new DownloadImageTask(iv)
                .execute(o.profileUrl);

        return v;
    }


}