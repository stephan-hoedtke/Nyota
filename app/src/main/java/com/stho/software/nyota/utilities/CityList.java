package com.stho.software.nyota.utilities;

import android.location.LocationManager;

import java.util.ArrayList;

public class CityList extends ArrayList<City> {

    public boolean isAutomaticOnly() {
        return (size() == 1 && get(0).isAutomatic());
    }

    public City findCityById(int id) {
        for (City city : this) {
            if (city.getId() == id)
                return city;
        }
        return null;
    }

    City findCityByName(String cityName) {
        for (City city : this) {
            if (city.getName().equals(cityName))
                return city;
        }
        return null;
    }

    public City findMatchingCity(City cityToFind) {
        for (City city : this) {
            if (city.matches(cityToFind))
                return city;
        }
        return null;
    }

    public City getSelectedCityOrDefault() {
        return getSelectedCityOrDefault(null);
    }

    City getSelectedCityOrDefault(LocationManager locationManager) {
        City city = getSelectedCity();
        if (city == null) {
            city = getAutomatic();
        }
        if (city == null) {
            city = createDefault(locationManager);
        }
        if (city != null) {
            city.select();
        }
        return city;
    }

    private City createDefault(LocationManager locationManager) {
        City city = City.createDefault(locationManager);
        this.add(city);
        return city;
    }

    private City getSelectedCity() {
        for (City city : this) {
            if (city.isSelected())
                return city;
        }
        return null;
    }

    private City getAutomatic() {
        for (City city : this) {
            if (city.isAutomatic())
                return city;
        }
        return null;
    }

    public void selectById(int id) {
        for (City city : this) {
            if (city.getId() == id)
                city.select();
            else
                city.unselect();
        }
    }

    void selectByName(String cityName) {
        for (City city : this) {
            if (city.getName().equals(cityName))
                city.select();
            else
                city.unselect();
        }
    }

    public void select(City cityToSelect) {
        for (City city : this) {
            if (city.matches(cityToSelect))
                city.select();
            else
                city.unselect();
        }
    }

    public void unselect() {
        for (City city : this) {
            city.unselect();
        }
    }

    public void removeById(int id) {
        City city = findCityById(id);
        if (city != null) {
            this.remove(city);
        }
    }

    /**
     * Serialize the list into a string ready to be stored in settings
     * @return serialized
     */
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        boolean empty = true;
        for (City city : this) {
            if (empty) {
                empty = false;
            }
            else {
                sb.append(City.CITY_DELIMITER);
            }
            sb.append(city.serialize());
        }
        return sb.toString();
    }

    /**
     * Reading the list of cities from a settings string. In case of an error an empty list might be returned.
     * @param str serialized string with list of cities
     * @return list of cities
     */
    public static CityList deserialize(String str) {
        CityList cities = new CityList();
        try {
            if (str != null) {
                String[] parts = str.split(City.CITY_DELIMITER);
                if (parts.length > 0) {
                    for (String part : parts) {
                        City city = City.deserialize(part);
                        if (city != null) {
                            cities.add(city);
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            // ignore;
        }
        return cities;
    }

    public int getSelectedItemPosition() {
        for (int position=0; position < size(); position++) {
            if (get(position).isSelected())
                return position;
        }
        return -1;
    }
}
