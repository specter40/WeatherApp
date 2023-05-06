package cs1302.api;

import com.google.gson.Gson;

/**
 * Represents a response from the iTunes Search API. This is used by Gson to
 * create an object from the JSON response body. This class is provided with
 * project's starter code, and the instance variables are intentionally set
 * to package private visibility.
 */
public class CityResponse {
    String name;
    double latitude;
    double longitude;
    String country;
    int population;

    /* public static CityResponse[] fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, CityResponse[].class);
        }*/
} // ItunesResponse
