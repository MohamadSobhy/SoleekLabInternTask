package com.example.soleeklabinterntask.activities;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.soleeklabinterntask.R;
import com.example.soleeklabinterntask.adapters.CountryAdapter;
import com.example.soleeklabinterntask.utils.network.CountryApiClient;
import com.example.soleeklabinterntask.utils.network.NetworkUtils;
import com.example.soleeklabinterntask.utils.network.interfaces.CountryApiService;
import com.example.soleeklabinterntask.utils.network.models.Country;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private ProgressBar loadingIndicator;
    private RecyclerView countriesRecyclerView;
    private CountryAdapter adapter;
    private List<Country> listOfCountries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final TextView errorDisplayTextView = findViewById(R.id.error_display_tv);

        if (!NetworkUtils.isConnected(this)) {
            errorDisplayTextView.setText(getString(R.string.internet_connection_failed));
            errorDisplayTextView.setVisibility(View.VISIBLE);
        }

        loadingIndicator = findViewById(R.id.loading_indicator_pb);


        loadingIndicator.setVisibility(View.VISIBLE);

        Log.v("key", "\n\n\n\n\n\n\nHERE!!!!!!!!!!!!!!!\n\n\n\n\n\n\n");

        CountryApiService service = CountryApiClient.getRetrofitInstance().create(CountryApiService.class);
        Call<List<Country>> apiCall = service.getAllCountries();

        apiCall.enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                listOfCountries = response.body();
                doit(listOfCountries);
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {
                loadingIndicator.setVisibility(View.INVISIBLE);
                errorDisplayTextView.setText(getString(R.string.no_data_retrieved_msg));
                errorDisplayTextView.setVisibility(View.VISIBLE);
                View p = findViewById(android.R.id.content);
                Snackbar.make(p, "Loading Failed", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    void doit(List<Country> listOfCountries) {
        loadingIndicator.setVisibility(View.INVISIBLE);// set up the RecyclerView
        countriesRecyclerView = findViewById(R.id.country_recycler_view);
        countriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CountryAdapter(this, listOfCountries);
        countriesRecyclerView.setAdapter(adapter);
    }
}
