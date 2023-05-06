package cs1302.api;

import com.google.gson.annotations.SerializedName;

/**
 * Current class is used for Gson to create an object of current
 * for the weather response.
 */
public class Current {
    @SerializedName("last_updated") String lastUpdated;
    @SerializedName("temp_f") double tempF;
    Condition condition;
}
