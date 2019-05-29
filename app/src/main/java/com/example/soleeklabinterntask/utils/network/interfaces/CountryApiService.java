package com.example.soleeklabinterntask.utils.network.interfaces;

import com.example.soleeklabinterntask.utils.network.models.Country;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CountryApiService {
    @GET("rest/v2/all")
    Call<List<Country>> getAllCountries();
}
