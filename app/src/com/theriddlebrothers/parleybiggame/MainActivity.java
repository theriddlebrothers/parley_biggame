package com.theriddlebrothers.parleybiggame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    private final String TAG = "MainActivity";

    /**
     * Tags used by this app to search twitter.
     */
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

                // Search Twitter's public feed
                String url = "http://search.twitter.com/search.json?q=" + query
                        + "&lang=en&result_type=recent&rpp=20";
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
                Log.e(TAG, "Error loading JSON", e);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit_menu_item:
                finish();
                System.exit(0);
                return true;
            case R.id.about_menu_item:
                AlertDialog dialog = createAboutDialog();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private AlertDialog createAboutDialog() {
        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = inflater.inflate(R.layout.dialog_about, null);
        builder.setView(dialogView)
                .setNegativeButton(R.string.okay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });

        Button visitWebsite = (Button)dialogView.findViewById(R.id.visitWebsite);
        visitWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(getString(R.string.url)) );
                startActivity( browse );
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
