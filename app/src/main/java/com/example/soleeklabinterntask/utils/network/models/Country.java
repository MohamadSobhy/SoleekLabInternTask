package com.example.soleeklabinterntask.utils.network.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Country {
    @SerializedName("name")
    private String name;

    @SerializedName("capital")
    private String capital;

    @SerializedName("region")
    private String region;

    @SerializedName("subregion")
    private String subregion;

    @SerializedName("population")
    private int population;

    @SerializedName("latlng")
    private List<Double> latlng;

    public Country(String name, String capital, String region, String subregion, int population, List<Double> latlng){
        this.name = name;
        this.capital = capital;
        this.region = region;
        this.subregion = region;
        this.population = population;
        this.latlng = latlng;
    }

    public String getName() {
        return name;
    }

    public String getCapital() {
        return capital;
    }

    public String getRegion() {
        return region;
    }

    public String getSubregion() {
        return subregion;
    }

    public int getPopulation() {
        return population;
    }

    public List<Double> getLatlng() {
        return latlng;
    }
}
