package com.sohoj.sampleproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class Profile_Page extends AppCompatActivity {
    ImageView profileImage;
    TextView userName, userEmail, userBalance, fullname;
    LottieAnimationView lottieLoading;
    LinearLayout profileLayout;

    private static final String API_KEY = MainActivity.API_KEY; // Replace with your actual API key
    private static final String TAG = "ProfilePage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        profileImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        userBalance = findViewById(R.id.user_balance);
        fullname = findViewById(R.id.full_name);
        lottieLoading = findViewById(R.id.lottieLoading);
        profileLayout = findViewById(R.id.profile_layout);

        profileLayout.setVisibility(View.GONE);
        lottieLoading.setVisibility(View.VISIBLE);


        // Make API request to fetch profile details
        fetchProfileDetails();
    }

    private void fetchProfileDetails() {
        String url = "https://codecollection.shop/api/account/details";

        // Create a new request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create JSON object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url + "?api_key=" + API_KEY,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Check if the status is success
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                // Parse profile data
                                JSONObject data = response.getJSONObject("data");

                                // Extract user details
                                String firstName = data.getJSONObject("name").getString("firstname");
                                String lastName = data.getJSONObject("name").getString("lastname");
                                String fullName = data.getJSONObject("name").getString("full_name");
                                String username = data.getString("username");
                                String email = data.getString("email");
                                String balance = data.getString("balance");


                                // Update UI
                                userName.setText(username);
                                userEmail.setText(email);
                                userBalance.setText(balance);
                                fullname.setText(fullName);

                                // Load profile image using Picasso
                                String avatarUrl = data.getJSONObject("profile").getJSONObject("media").getString("avatar");
                                Picasso.get()
                                        .load(avatarUrl)
                                        .into(profileImage);
                                lottieLoading.setVisibility(View.GONE);
                                profileLayout.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(Profile_Page.this, "Failed to fetch profile details", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Profile_Page.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        Log.e(TAG, "Error fetching profile details: " + error.toString());
                        Toast.makeText(Profile_Page.this, "Failed to fetch profile details", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the request queue
        requestQueue.add(jsonObjectRequest);
    }
}
