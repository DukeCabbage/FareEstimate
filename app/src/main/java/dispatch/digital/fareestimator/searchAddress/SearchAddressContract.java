package dispatch.digital.fareestimator.searchAddress;


import java.util.List;

import dispatch.digital.fareestimator.googleApi.place.addressbean.Result;
import dispatch.digital.fareestimator.googleApi.place.placeautocompletebean.Prediction;

public interface SearchAddressContract {

    interface View {
        void showAutoCompleteResults(List<Prediction> results);

        void showPlaceDetailResult(Result result);
    }

    interface Presenter {
        void autoCompletePlace(String input);

        void searchPlaceDetail(String placeId);

        void start();

        void stop();
    }
}
