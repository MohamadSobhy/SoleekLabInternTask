package com.example.soleeklabinterntask.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.soleeklabinterntask.R;
import com.example.soleeklabinterntask.adapters.CountryAdapter;
import com.example.soleeklabinterntask.utils.network.CountryApiClient;
import com.example.soleeklabinterntask.utils.network.NetworkUtils;
import com.example.soleeklabinterntask.utils.network.interfaces.CountryApiService;
import com.example.soleeklabinterntask.utils.network.models.Country;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private ProgressBar loadingIndicator;
    private RecyclerView countriesRecyclerView;
    private TextView errorDisplayTextView;
    private CountryAdapter adapter;
    private List<Country> listOfCountries;
    private SwipeRefreshLayout pullToRefresh;
    private boolean connectionFailed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent currentIntent = getIntent();
        String userName = currentIntent.getStringExtra(RegistrationActivity.USER_NAME_KEY);
        String userEmail = currentIntent.getStringExtra(RegistrationActivity.EMAIL_KEY);

        View parentView = findViewById(android.R.id.content);
        Snackbar.make(parentView, getString(R.string.logged_as, userName, userEmail), Snackbar.LENGTH_LONG).show();

        errorDisplayTextView = findViewById(R.id.error_display_tv);

        if (!NetworkUtils.isConnected(this)) {
            errorDisplayTextView.setText(getString(R.string.internet_connection_failed));
            errorDisplayTextView.setVisibility(View.VISIBLE);
        }

        pullToRefresh = findViewById(R.id.swipe_refresh_indicator);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListOfCountriesFromServerAndSetupRecycler();
            }
        });

        loadingIndicator = findViewById(R.id.loading_indicator_pb);

        getListOfCountriesFromServerAndSetupRecycler();

    }

    private void getListOfCountriesFromServerAndSetupRecycler(){
        loadingIndicator.setVisibility(View.VISIBLE);
        pullToRefresh.setRefreshing(false);

        CountryApiService service = CountryApiClient.getRetrofitInstance().create(CountryApiService.class);
        Call<List<Country>> apiCall = service.getAllCountries();

        apiCall.enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                connectionFailed = false;
                listOfCountries = response.body();
                setupRecyclerView(listOfCountries);
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {
                connectionFailed = true;
            }
        });
    }

    void setupRecyclerView(List<Country> listOfCountries) {

        loadingIndicator.setVisibility(View.INVISIBLE);

        if (!isConnectionFailed()) {
            if (listOfCountries.size() != 0) {
                // set up the RecyclerView
                countriesRecyclerView = findViewById(R.id.country_recycler_view);
                countriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                adapter = new CountryAdapter(this, listOfCountries);
                countriesRecyclerView.setAdapter(adapter);
            } else {
                errorDisplayTextView.setText(getString(R.string.no_data_retrieved_msg));
                errorDisplayTextView.setVisibility(View.VISIBLE);
            }
        } else {
            errorDisplayTextView.setText(getString(R.string.no_data_retrieved_msg));
            errorDisplayTextView.setVisibility(View.VISIBLE);
            View p = findViewById(android.R.id.content);
            Snackbar.make(p, "Loading Failed", Snackbar.LENGTH_LONG).show();
        }
    }

    public boolean isConnectionFailed() {
        return connectionFailed;
    }
}
