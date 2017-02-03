package dispatch.digital.fareestimator.googleApi.place;

import dispatch.digital.fareestimator.googleApi.place.placeautocompletebean.PlaceResult;
import dispatch.digital.fareestimator.googleApi.place.placedetailbean.PlaceDetailResult;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * See Google Places API Web Service doc:
 * https://developers.google.com/places/web-service/intro
 */

public interface PlaceApi {

    String AUTO_COMPLETE_URL = "maps/api/place/autocomplete/json?language=en&radius=100000";//radius is 100KM
    String PLACE_DETAIL_URL = "maps/api/place/details/json?language=en";

    @GET(AUTO_COMPLETE_URL)
    Observable<PlaceResult> autoComplete(@Query("input") String input, @Query("location") String location);

    @GET(AUTO_COMPLETE_URL)
    Observable<PlaceResult> autoComplete(@Query("input") String input);

    @GET(PLACE_DETAIL_URL)
    Observable<PlaceDetailResult> getPlaceDetail(@Query("placeid") String placeid);
}
