package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 10;
    EditText etCompose;
    Button btnTweet;
    TextView tvLetterCount;
    TwitterClient client;
    Drawable bkrColor = btnTweet.getBackground();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tvLetterCount = findViewById(R.id.tvLetterCount);
        String concat = "0  / "+MAX_TWEET_LENGTH;
        tvLetterCount.setText(concat);


        //Set click listener on button for submitting a new tweet
        btnTweet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty())
                {
                    Toast.makeText(ComposeActivity.this,"Sorry, your tweet cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH)
                {
                    Toast.makeText(ComposeActivity.this,"Sorry, your tweet cannot be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this,tweetContent,Toast.LENGTH_SHORT).show();

                // Make an API call to Twitter to publish the tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG,"onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG,"Published tweet says: "+tweet.body);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            // set result code and bundle data for response
                            setResult(RESULT_OK,intent);
                            // closes the activity, pass data to parent

                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG,"onFailure to publish tweet",throwable);
                    }
                });
            }
        });

        //Add text listener to the text box for the word count
        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)
            }



            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Fires right before text is changing
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void afterTextChanged(Editable s) {
                // Fires right after the text has changed
                String lengthCharsTextBox=String.valueOf(s.toString().length());


                if(Integer.parseInt(lengthCharsTextBox) >= MAX_TWEET_LENGTH)
                {
                    btnTweet.setBackgroundColor(android.R.color.darker_gray);

                    //Block the button too
                    btnTweet.setEnabled(false);
                }
                else
                {
                    btnTweet.setBackground(bkrColor);

                    //Block the button too
                    btnTweet.setEnabled(true);
                }
                String concat = lengthCharsTextBox + " / "+MAX_TWEET_LENGTH;
                tvLetterCount.setText(concat);

            }

        });


    }
}