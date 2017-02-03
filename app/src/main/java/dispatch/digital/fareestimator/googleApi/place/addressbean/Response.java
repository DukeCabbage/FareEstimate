package dispatch.digital.fareestimator.googleApi.place.addressbean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Response {

    @SerializedName("results")
    @Expose
    public List<Result> results = new ArrayList<Result>();
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("error_message")
    @Expose
    public String errorMessage;
}
