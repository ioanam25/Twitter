package com.codepath.apps.restclienttemplate;

import static android.R.drawable.btn_star_big_on;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;

    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    // For each row, inflate a layout
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get data
        Tweet tweet = tweets.get(position);
        // bind the tweet at the viewholder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }


    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder implements com.codepath.apps.restclienttemplate.ViewHolder {

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        ImageView ivMedia;
        TextView tvRelativeTimestamp;
        ConstraintLayout constraintLayout;
        TextView tvFavoriteCount;
        ImageButton ibFavorite;
        ImageButton ibReply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            tvRelativeTimestamp = itemView.findViewById(R.id.tvRelativeTimestamp);
            constraintLayout = itemView.findViewById(R.id.cL);
            tvFavoriteCount = itemView.findViewById(R.id.tvFavoriteCount);
            ibFavorite = itemView.findViewById(R.id.ibFavorite);
            ibReply = itemView.findViewById(R.id.ibReply);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvRelativeTimestamp.setText(tweet.createdAt);
            tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
            if (tweet.tweetUrl != "none") {
                Glide.with(context).load(tweet.tweetUrl).into(ivMedia);
                ivMedia.setVisibility(View.VISIBLE);
            }
            else ivMedia.setVisibility(View.GONE);

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // create intent for the new activity
                    Intent intent = new Intent(context, TweetDetailActivity.class);
                    // serialize the movie using parceler, use its short name as a key
                    intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    // show the activity
                    context.startActivity(intent);
                }
            });

            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // if not already favorited
                    if (!tweet.isFavorited) {
                        // tell twitter i want to favorite this
                        TwitterApp.getRestClient(context).favorite(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("adapter", "this message should be favorited");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("adapter", "this should be unfavorited");
                            }
                        });
                        // change the drawable to btn_star_big_on
                        tweet.isFavorited = true;
                        Drawable newImage = context.getDrawable(android.R.drawable.btn_star_big_on);
                        ibFavorite.setImageDrawable(newImage);
                        // increment the text inside tvFavoriteCount
                        tweet.favoriteCount++;
                        tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
                    }
                    // else if already favorited
                    else {
                        // tell twitter to unfavorite this
                        TwitterApp.getRestClient(context).unfavorite(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("adapter", "this message should be unfavorited");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("adapter", "this should be favorited");
                            }
                        });
                        // change drawable back to btn_star_off
                        tweet.isFavorited = false;
                        Drawable newImage = context.getDrawable(android.R.drawable.btn_star_big_off);
                        ibFavorite.setImageDrawable(newImage);
                        // decrement the text inside tvFavoriteCount
                        tweet.favoriteCount--;
                        tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
                    }
                }
            });

            ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // pop up a compose tweet but it's gonna have an extra attribute
                        // extra attribute "in_reply_to_status_id"
                    Intent i = new Intent(context, ComposeActivity.class);
                    i.putExtra("should_reply_to_tweet", true);
                    i.putExtra("id_of_tweet_to_reply_to", tweet.id);
                    i.putExtra("screenname_of_tweet_to_reply_to", tweet.user.screenName);
                    // context.startActivity(i);
                    ((Activity) context).startActivityForResult(i, TimelineActivity.REQUEST_CODE);
                }
            });
        }
    }
    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> tweetsList) {
        tweets.addAll(tweetsList);
        notifyDataSetChanged();
    }
}
