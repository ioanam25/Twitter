package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;
import org.w3c.dom.Text;

public class TweetDetailActivity extends AppCompatActivity {

    Tweet tweet;

    TextView tvBody;
    TextView tvScreenName;
    ImageView ivProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        Log.d("launch tweetdet", "detail");

        tvBody = (TextView) findViewById(R.id.tvDetailBody);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        ivProfileImage = findViewById(R.id.ivProfileImage);

        // unwrap the movie passed in via intent, using its simple name as a key
         tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        //Log.d("TweetDetailActivity", String.format("Showing details for '%s'", tweet.id));

        Log.i("TweetDetailsActivity", tweet.body);
        tvBody.setText(tweet.body);
        tvScreenName.setText(tweet.user.screenName);
        Glide.with(getApplicationContext()).load(tweet.user.profileImageUrl).into(ivProfileImage);
//        if (tweet.tweetUrl != "none") {
//            Glide.with(getApplicationContext()).load(tweet.tweetUrl).into(ivMedia);
//            ivMedia.setVisibility(View.VISIBLE);
//        }
    }
}