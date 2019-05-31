package com.example.soleeklabinterntask.utils.network;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.example.soleeklabinterntask.utils.network.models.Country;

import java.util.ArrayList;

public class NetworkUtils {
    private static ArrayList<Country> listOfCountries;

    public static boolean isConnected(Activity currentActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) currentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

}
