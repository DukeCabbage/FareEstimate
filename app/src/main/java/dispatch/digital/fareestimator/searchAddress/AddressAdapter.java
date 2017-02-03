package dispatch.digital.fareestimator.searchAddress;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dispatch.digital.fareestimator.R;
import dispatch.digital.fareestimator.googleApi.place.placeautocompletebean.Prediction;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    final private ItemSelectionCallback mCallback;
    private List<Prediction> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @BindView(R.id.tv_address) TextView tvAddress;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, itemView);
        }

        public void setData(Prediction data) {
            tvAddress.setText(data.getDescription());
        }
    }

    interface ItemSelectionCallback {
        void onAddressSelected(Prediction prediction);
    }

    public AddressAdapter(@NonNull ItemSelectionCallback selectionCallback) {
        this.mCallback = selectionCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                                            .inflate(R.layout.item_search_address, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Prediction data = mDataset.get(position);
        holder.setData(data);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onAddressSelected(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size();
    }

    public void setDataset(List<Prediction> dataset) {
        if (dataset == null) return;
        mDataset = dataset;
        notifyDataSetChanged();
    }
}
