package cs1302.api;

/**
 * Represents a result in a response from the iTunes Search API. This is
 * used by Gson to create an object from the JSON response body. This class
 * is provided with project's starter code, and the instance variables are
 * intentionally set to package private visibility.
 *
 */
public class CityResults {
    String name;
    int latitude;
    int longitude;
    String country;
    int population;
}
