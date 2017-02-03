package dispatch.digital.fareestimator.searchAddress;

import android.content.Context;
import android.support.annotation.NonNull;

import dispatch.digital.fareestimator.googleApi.place.PlaceApi;
import dispatch.digital.fareestimator.googleApi.place.addressbean.Result;
import dispatch.digital.fareestimator.googleApi.place.placeautocompletebean.PlaceResult;
import dispatch.digital.fareestimator.googleApi.place.placedetailbean.PlaceDetailResult;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SearchAddressPresenter implements SearchAddressContract.Presenter {

    final private Context mContext;
    final private SearchAddressContract.View mView;
    final private PlaceApi placeApi;

    private Subscription autoCompleteSubscription;

    public SearchAddressPresenter(@NonNull Context context,
                                  @NonNull SearchAddressContract.View destinationView,
                                  @NonNull PlaceApi placeApi) {
        this.mContext = context;
        this.mView = destinationView;
        this.placeApi = placeApi;
    }

    @Override
    public void autoCompletePlace(String input) {
        Timber.v("Search for %s", input);

        autoCompleteSubscription = placeApi.autoComplete(input)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PlaceResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(PlaceResult placeResult) {
                        // TODO: Better handling for unsuccessful status codes
                        mView.showAutoCompleteResults(placeResult.getPredictions());
                    }
                });
    }

    @Override
    public void searchPlaceDetail(String placeId) {
        placeApi.getPlaceDetail(placeId)
                .flatMap(new Func1<PlaceDetailResult, Observable<Result>>() {
                    @Override
                    public Observable<Result> call(PlaceDetailResult placeDetailResult) {
                        return Observable.just(placeDetailResult.getResult());
                    }
                })
                .take(1)
                .single()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Result>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Result result) {
                        // TODO: Better handling for unsuccessful status codes
                        mView.showPlaceDetailResult(result);
                    }
                });
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        if (autoCompleteSubscription != null && !autoCompleteSubscription.isUnsubscribed())
            autoCompleteSubscription.unsubscribe();
    }
}
