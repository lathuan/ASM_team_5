package com.example.asm_ad;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Set up toolbar


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tìm Kiếm");
        }

        // Add search functionality here (e.g., EditText for search input, RecyclerView for results)
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Handle back button in toolbar
        return true;
    }
}