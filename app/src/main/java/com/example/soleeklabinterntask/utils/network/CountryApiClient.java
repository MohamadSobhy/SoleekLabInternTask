package com.example.soleeklabinterntask.utils.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CountryApiClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://restcountries.eu/";

    public static Retrofit getRetrofitInstance() {

        retrofit = new Retrofit.
                Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}
