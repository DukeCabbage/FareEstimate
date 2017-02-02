package dispatch.digital.fareestimater.SearchAddress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import dispatch.digital.fareestimater.R;

public class SearchAddressActivity extends AppCompatActivity
        implements SearchAddressContract.View, AddressAdapter.ItemSelectionCallback {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.et_destination) EditText etDestination;
    @BindView(R.id.rv_address_results) RecyclerView rvAddressResults;

    private SearchAddressContract.Presenter mPresenter;
    private AddressAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);
        ButterKnife.bind(this);
        setUpAppBar();

        mPresenter = new SearchAddressPresenter(this, this);

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
        mPresenter.start();
        mPresenter.searchAddress("Mock");
    }

    @Override
    public void onStop() {
        super.onStart();
        mPresenter.stop();
    }

    @Override
    public void showSearchResults(String[] results) {
        mAdapter.setDataset(results);
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
    public void onAddressSelected(String addressName) {
        Toast.makeText(this, addressName, Toast.LENGTH_SHORT).show();
    }
}
