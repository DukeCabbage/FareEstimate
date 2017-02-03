package dispatch.digital.fareestimator.googleApi.place.placeautocompletebean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PlaceResult {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("predictions")
    @Expose
    private List<Prediction> predictions = new ArrayList<>();

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The predictions
     */
    public List<Prediction> getPredictions() {
        return predictions;
    }

    /**
     * @param predictions The predictions
     */
    public void setPredictions(List<Prediction> predictions) {
        this.predictions = predictions;
    }


}
