package dispatch.digital.fareestimator.googleApi.place.addressbean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AddressComponent {

    @SerializedName("long_name")
    @Expose
    public String longName;
    @SerializedName("short_name")
    @Expose
    public String shortName;
    @SerializedName("types")
    @Expose
    public List<String> types = new ArrayList<String>();

    @Override
    public String toString() {
        return "AddressComponent{" +
                "longName='" + longName + '\'' +
                ", shortName='" + shortName + '\'' +
                ", types=" + types +
                '}';
    }
}
