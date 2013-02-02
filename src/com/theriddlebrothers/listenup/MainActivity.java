package com.theriddlebrothers.listenup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.ads.AdActivity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private final String TWITTER_HASHTAGS = "SuperBowl,SuperBowl2013,SuperBowlXLVII,XLVII,49ers,Niners," +
            "SanFrancisco,SF,Ravens,QuestforSix,SBRavens,RavenNation,Baltimore,Bmore";

    private PullToRefreshListView listView;
    private TweetListAdapter adapter;
    private String lastId = "";
    public ArrayList<Tweet> tweets;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweets = new ArrayList<Tweet>();
        new MyTask().execute();
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                String[] hashtags = TWITTER_HASHTAGS.split(",");
                String query = "";
                for(int i = 0; i < hashtags.length; i++) {
                    if (i != 0) query += "+OR+";
                    query += hashtags[i];
                }
                if (lastId != null && lastId.length() != 0) query += "&since_id=" + lastId;

                String url = "http://search.twitter.com/search.json?q=" + query
                        + "&lang=en&result_type=recent&rpp=20";
                Log.d("MAINVIEW", url);
                HttpClient hc = new DefaultHttpClient();
                HttpGet get = new
                        HttpGet(url);
                HttpResponse rp = hc.execute(get);
                if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                {
                    String result = EntityUtils.toString(rp.getEntity());
                    JSONObject root = new JSONObject(result);
                    JSONArray sessions = root.getJSONArray("results");
                    lastId = root.getString("max_id");
                    SimpleDateFormat format =
                            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                    format.setLenient(true);

                    for (int i = 0; i < sessions.length(); i++) {
                        JSONObject session = sessions.getJSONObject(i);
                        Tweet tweet = new Tweet();
                        tweet.id = session.getString("id");
                        tweet.content = session.getString("text");
                        tweet.author = session.getString("from_user");
                        tweet.dateCreated = format.parse(session.getString("created_at"));
                        tweet.profileUrl = session.getString("profile_image_url");
                        tweets.add(0, tweet);
                    }
                }
            } catch (Exception e) {
                Log.e("TwitterFeedActivity", "Error loading JSON", e);
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {

            setContentView(R.layout.main);

            listView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(MainActivity.this, adapter.getItem(i).content, 2000);
                }
            });

            // OPTIONAL: Disable scrolling when list is refreshing
            // listView.setLockScrollWhileRefreshing(false);

            // OPTIONAL: Uncomment this if you want the Pull to Refresh header to show the 'last updated' time
            // listView.setShowLastUpdatedText(true);

            // OPTIONAL: Uncomment this if you want to override the date/time format of the 'last updated' field
            // listView.setLastUpdatedDateFormat(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));

            // OPTIONAL: Uncomment this if you want to override the default strings
            // listView.setTextPullToRefresh("Pull to Refresh");
            // listView.setTextReleaseToRefresh("Release to Refresh");
            // listView.setTextRefreshing("Refreshing");

            // MANDATORY: Set the onRefreshListener on the list. You could also use
            // listView.setOnRefreshListener(this); and let this Activity
            // implement OnRefreshListener.
            listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    new MyTask().execute();
                }
            });

            adapter = new TweetListAdapter(
                    MainActivity.this, R.layout.list_item, tweets);

            listView.setAdapter(adapter);
            listView.onRefreshComplete();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private class Tweet {
        public String id;
        public Date dateCreated;
        public String content;
        public String author;
        public String profileUrl;
    }

    private class TweetListAdapter extends ArrayAdapter<Tweet> {
        private ArrayList<Tweet> tweets;
        public TweetListAdapter(Context context,
                                int textViewResourceId,
                                ArrayList<Tweet> items) {
            super(context, textViewResourceId, items);
            this.tweets = items;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }

            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

            Tweet o = tweets.get(position);
            TextView tt = (TextView) v.findViewById(R.id.top_text);
            TextView bt = (TextView) v.findViewById(R.id.details_text);
            ImageView iv = (ImageView) v.findViewById(R.id.list_image);
            tt.setText(o.content);
            bt.setText("@" + o.author + " at " + timeFormat.format(o.dateCreated)
                            + " on " + dateFormat.format(o.dateCreated));

            // download the user's profile image
            new DownloadImageTask(iv)
                    .execute(o.profileUrl);

            return v;
        }
    }

}
