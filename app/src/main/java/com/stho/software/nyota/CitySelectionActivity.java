package com.stho.software.nyota;

import android.app.ListActivity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.stho.software.nyota.utilities.City;

import java.util.Locale;

public class CitySelectionActivity extends ListActivity {

    private static final int RQ_CITY = 123458;

    private ArrayAdapter<City> adapter = null;
    private int selectedListItemPosition = 0;
    final CitySettings settings = new CitySettings();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);

        loadCitiesFromSettings();
        loadCityFromIntend();

        adapter = new ArrayAdapter<City>(CitySelectionActivity.this, R.layout.city_list_item, settings.cities) {

            class ViewHolder {
                RadioButton radioButton;
                TextView id;
                TextView name;
                TextView location;
                ImageView deleteButton;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                ViewHolder viewHolder = null;

                View rowView = convertView;
                if (rowView == null) {
                    LayoutInflater inflater = CitySelectionActivity.this.getLayoutInflater();
                    rowView = inflater.inflate(R.layout.city_list_item, parent, false);
                    viewHolder = new ViewHolder();
                    viewHolder.radioButton = rowView.findViewById(R.id.radioButton);
                    viewHolder.id = rowView.findViewById(R.id.listItemId);
                    viewHolder.name = rowView.findViewById(R.id.listItemName);
                    viewHolder.location = rowView.findViewById(R.id.listItemLocation);
                    viewHolder.deleteButton = rowView.findViewById(R.id.buttonDelete);
                    updateRowView(position, viewHolder);
                    setListener(viewHolder);
                    rowView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) rowView.getTag();
                    updateRowView(position, viewHolder);
                }

                return rowView;
            }

            private void setListener(final ViewHolder viewHolder) {
                viewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // its called after the checkbox was updated!

                        int id = Integer.parseInt(viewHolder.id.getText().toString());
                        City city = settings.cities.findCityById(id);
                        if (city != null) {
                            if (city.isSelected()) {
                                settings.cities.unselect();
                            } else {
                                settings.cities.selectById(id);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = Integer.parseInt(viewHolder.id.getText().toString());
                        settings.cities.removeById(id);
                        adapter.notifyDataSetChanged();
                    }
                });
                viewHolder.name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = Integer.parseInt(viewHolder.id.getText().toString());
                        City city = settings.cities.findCityById(id);
                        if (city != null) {
                            Framework.openCityActivity(CitySelectionActivity.this, city);
                        }
                    }
                });
            }

            private void updateRowView(final int position, final ViewHolder viewHolder) {
                final City city = settings.cities.get(position);
                viewHolder.id.setText(String.format(Locale.ENGLISH, "%d", city.getId()));
                viewHolder.radioButton.setChecked(city.isSelected());
                viewHolder.name.setText(city.getNameEx());
                viewHolder.location.setText(city.getLocation().toString());
           }
        };

        setListAdapter(adapter);

        findViewById(R.id.buttonAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Framework.openCityActivity(CitySelectionActivity.this);
            }
        });

        findViewById(R.id.buttonAccept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCities();
                City city = settings.cities.getSelectedCityOrDefault();
                Framework.finishActivity(CitySelectionActivity.this, city);
            }
        });
    }

    private void loadCitiesFromSettings() {
        settings.load(this);

        if (settings.cities.isEmpty()) {
            settings.cities.add(City.createDefault((LocationManager) this.getSystemService(LOCATION_SERVICE)));
        }
        if (settings.cities.isAutomaticOnly()) {
            settings.cities.add(new City("Berlin", 52.6425,   13.4925, 43, "CET"));
            settings.cities.add(new City("Munich", 48.1374283, 11.57549, 524, "CET"));          // https://www.geodatos.net/en/coordinates/germany/bavaria/munich
            settings.cities.add(new City("London",  51.5085297, -0.12574, 25, "GMT"));          // https://www.geodatos.net/en/coordinates/united-kingdom/england/london
            settings.cities.add(new City("Lusaka",  -15.4066896, 28.2871304, 1277, "CAT"));     // https://www.geodatos.net/en/coordinates/zambia/lusaka
            settings.cities.add(new City("Monrovia", 6.30054, -10.7968998, 32, "Africa/Monrovia"));         // https://www.geodatos.net/en/coordinates/liberia
            settings.cities.add(new City("Dar es Salaam", -6.8234901, 39.2695084, 24, "EAT"));  // https://www.geodatos.net/en/coordinates/tanzania/dar-es-salaam
            settings.cities.add(new City("Lagos", 6.4540701,  3.39467, 11, "Africa/Lagos"));             // https://www.geodatos.net/en/coordinates/nigeria/lagos
            settings.cities.add(new City("Kigali", -1.94995,  30.0588493, 32, "CAT"));          // https://www.geodatos.net/en/coordinates/rwanda/kigali
            settings.cities.add(new City("Antananarivo", -18.9136791, 47.536129, 1542, "GMT+3")); // https://www.geodatos.net/en/coordinates/madagascar/analamanga/antananarivo
            settings.cities.add(new City("Borneo", -1.24204, 116.8941879, 10, "GMT+8")); // https://www.geodatos.net/en/coordinates/indonesia
            settings.cities.add(new City("New York", 40.7142715, -74.0059662, 10, "GMT-5")); // https://www.geodatos.net/en/coordinates/united-states/new-york/new-york-city
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        int position = settings.cities.getSelectedItemPosition();
        getListView().smoothScrollToPosition(position); // doesn't work, why not?
    }

    private void loadCityFromIntend() {
        City city = Framework.getCity(getIntent());
        if (city != null) {
            City matchingCity = settings.cities.findMatchingCity(city);
            if (matchingCity == null) {
                settings.cities.add(city);
                settings.cities.select(city);
            } else {
                settings.cities.select(matchingCity);
            }
        }
    }

    private void saveCities() {
        settings.save(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RQ_CITY:
                    City newCity = Framework.getCity(data);
                    if (newCity != null) {
                        updateCity(newCity);
                    }
                    break;
            }
        }
    }

    private void updateCity(City newCity) {
        City cityToUpdate = settings.cities.findCityById(newCity.getId());
        if (cityToUpdate != null) {
            cityToUpdate.copyFrom(newCity);
        } else {
            settings.cities.add(newCity);
        }
        adapter.notifyDataSetChanged();
    }
}
