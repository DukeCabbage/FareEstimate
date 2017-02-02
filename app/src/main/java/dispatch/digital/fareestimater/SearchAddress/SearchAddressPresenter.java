package dispatch.digital.fareestimater.SearchAddress;

import android.content.Context;
import android.support.annotation.NonNull;

import dispatch.digital.fareestimater.Cheeses;

public class SearchAddressPresenter implements SearchAddressContract.Presenter {

    final private Context mContext;
    final private SearchAddressContract.View mView;

    public SearchAddressPresenter(@NonNull Context context, @NonNull final SearchAddressContract.View destinationView) {
        this.mContext = context;
        this.mView = destinationView;
    }

    @Override
    public void searchAddress(String input) {
        mView.showSearchResults(Cheeses.CHEESES);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
