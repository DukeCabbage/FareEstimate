package dispatch.digital.fareestimator.searchAddress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import dispatch.digital.fareestimator.MyApplication;
import dispatch.digital.fareestimator.R;
import dispatch.digital.fareestimator.googleApi.place.PlaceApi;
import dispatch.digital.fareestimator.googleApi.place.addressbean.Result;
import dispatch.digital.fareestimator.googleApi.place.placeautocompletebean.Prediction;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class SearchAddressActivity extends AppCompatActivity
        implements SearchAddressContract.View, AddressAdapter.ItemSelectionCallback {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.et_destination) EditText etDestination;
    @BindView(R.id.rv_address_results) RecyclerView rvAddressResults;

    private SearchAddressContract.Presenter mPresenter;
    private AddressAdapter mAdapter;
    private Subscription textChangeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);
        ButterKnife.bind(this);
        setUpAppBar();

        PlaceApi placeApi = MyApplication.getInstance(SearchAddressActivity.this).component().placeApi();
        mPresenter = new SearchAddressPresenter(this, this, placeApi);

        rvAddressResults.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AddressAdapter(this);
        rvAddressResults.setAdapter(mAdapter);
    }

    private void setUpAppBar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            throw new RuntimeException("Can not find toolbar");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        textChangeSubscription = RxTextView.textChanges(etDestination)
                .debounce(400, TimeUnit.MILLISECONDS)
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        return charSequence != null && !charSequence.toString().isEmpty();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        String searchInput = charSequence.toString();
                        mPresenter.autoCompletePlace(searchInput);
                    }
                });

        mPresenter.start();
    }

    @Override
    public void onStop() {
        super.onStart();
        textChangeSubscription.unsubscribe();
        mPresenter.stop();
    }

    @Override
    public void showAutoCompleteResults(List<Prediction> results) {
        mAdapter.setDataset(results);
    }

    @Override
    public void showPlaceDetailResult(Result result) {
        Timber.i("Chosen destination: %f, %f", result.geometry.location.lat, result.geometry.location.lng);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();

        setResult(RESULT_CANCELED);
    }

    @Override
    public void onAddressSelected(Prediction prediction) {
        Toast.makeText(this, prediction.getDescription(), Toast.LENGTH_SHORT).show();
        mPresenter.searchPlaceDetail(prediction.getPlaceId());
    }
}
