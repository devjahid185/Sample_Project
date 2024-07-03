package com.sohoj.sampleproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {
    EditText purchaseKey;
    Button submit;
    LinearLayout main;
    ImageView productImage, profileImage;
    TextView productName, price, licenseType;
    LottieAnimationView lottieLoading;

    public static final String API_KEY = "4ea7f9a4a4875d6cba9785772c8acbeb74e8a6b4277850119812676cd39eb761"; // Replace with your actual API key
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        purchaseKey = findViewById(R.id.purchase_key);
        submit = findViewById(R.id.submit_button);
        main = findViewById(R.id.mother_layout);
        productImage = findViewById(R.id.product_image);
        profileImage = findViewById(R.id.profile_image);
        productName = findViewById(R.id.product_name);
        price = findViewById(R.id.price);
        licenseType = findViewById(R.id.license_type);
        lottieLoading = findViewById(R.id.lottieLoading);

        // Hide the main layout initially
        main.setVisibility(View.GONE);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Load Profile_Page after it fully loads
                loadProfilePage();
            }
        });

        // Set up button click listener
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = purchaseKey.getText().toString().trim();
                if (!key.isEmpty()) {
                    validatePurchase(API_KEY, key);
                    lottieLoading.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a purchase key", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void validatePurchase(String apiKey, String purchaseKey) {
        String url = "https://codecollection.shop/api/purchases/validation";

        // Create a new request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create JSON object with the required parameters
        JSONObject params = new JSONObject();
        try {
            params.put("api_key", apiKey);
            params.put("purchase_code", purchaseKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a new JSON object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log the full response
                        Log.d(TAG, "Response: " + response.toString());

                        try {
                            // Check if the status is success
                            String status = response.getString("status");
                            Log.d(TAG, "Status: " + status);
                            if (status.equals("success")) {
                                // Parse the item data
                                JSONObject item = response.getJSONObject("item");
                                Log.d(TAG, "Item: " + item.toString());

                                String purchaseCode = item.getString("purchase_code");
                                String licenseTypeText = item.getString("license_type");
                                double priceValue = item.getDouble("price");
                                String currency = item.getString("currency");
                                JSONObject itemDetails = item.getJSONObject("item");
                                Log.d(TAG, "Item Details: " + itemDetails.toString());

                                String itemName = itemDetails.getString("name");
                                String itemUrl = itemDetails.getString("url");
                                String previewImage = itemDetails.getJSONObject("media").getString("preview_image");

                                // Update UI with the data
                                productName.setText(itemName);
                                price.setText(String.format("%s %.2f", currency, priceValue));
                                licenseType.setText(licenseTypeText);

                                // Load image using Picasso
                                Picasso.get()
                                        .load(previewImage)
                                        .into(productImage);

                                lottieLoading.setVisibility(View.GONE);
                                // Show the main layout
                                main.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(MainActivity.this, "Invalid purchase key", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log the error
                        Log.e(TAG, "Error: " + error.toString());
                        // Handle the error response
                        error.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error validating purchase key", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the request queue
        requestQueue.add(jsonObjectRequest);
    }

    private void loadProfilePage() {
        // Intent to start Profile_Page activity
        Intent intent = new Intent(MainActivity.this, Profile_Page.class);
        startActivity(intent);
    }
}
