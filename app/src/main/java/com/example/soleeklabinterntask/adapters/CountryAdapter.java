package com.example.soleeklabinterntask.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.soleeklabinterntask.R;
import com.example.soleeklabinterntask.utils.network.models.Country;

import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {
    private final int MILLION = 1000000;
    private List<Country> mCountries;
    private Context mContext;

    public CountryAdapter(Context context, List<Country> countries) {
        mContext = context;
        mCountries = countries;
    }

    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parentView, int i) {
        LayoutInflater inflater = LayoutInflater.from(parentView.getContext());
        View itemContainer = inflater.inflate(R.layout.country_list_item, parentView, false);

        return new CountryViewHolder(itemContainer);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, final int position) {
        Country currentCountry = mCountries.get(position);
        holder.nameTextView.setText(currentCountry.getName());
        holder.capitalTextView.setText(currentCountry.getCapital());
        holder.populationTextView.setText(
                mContext.getString(R.string.default_country_population, (float) (currentCountry.getPopulation() / MILLION))
        );
        holder.regionTextView.setText(currentCountry.getRegion());
        holder.subregionTextView.setText(currentCountry.getSubregion());

        Typeface fontTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/montserrat_smibold.otf");

        holder.nameTextView.setTypeface(fontTypeface);
        holder.capitalTextView.setTypeface(fontTypeface);
        holder.populationTextView.setTypeface(fontTypeface);
        holder.regionTextView.setTypeface(fontTypeface);
        holder.subregionTextView.setTypeface(fontTypeface);

        holder.showOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Double> latlng = mCountries.get(position).getLatlng();
                String countryLocation = "geo:" + latlng.get(0) + "," + latlng.get(1);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(countryLocation));
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCountries.size();
    }

    public class CountryViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView capitalTextView;
        public TextView regionTextView;
        public TextView subregionTextView;
        public TextView populationTextView;
        public ImageButton showOnMap;

        public CountryViewHolder(@NonNull View parentView) {
            super(parentView);
            nameTextView = parentView.findViewById(R.id.country_name_tv);
            capitalTextView = parentView.findViewById(R.id.country_capital_tv);
            regionTextView = parentView.findViewById(R.id.country_region_tv);
            subregionTextView = parentView.findViewById(R.id.country_sub_region_tv);
            populationTextView = parentView.findViewById(R.id.country_population_tv);
            showOnMap = parentView.findViewById(R.id.show_on_map_btn);

        }
    }
}
