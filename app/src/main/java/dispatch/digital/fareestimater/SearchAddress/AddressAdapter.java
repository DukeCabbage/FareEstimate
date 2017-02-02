package dispatch.digital.fareestimater.SearchAddress;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dispatch.digital.fareestimater.R;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    final private ItemSelectionCallback mCallback;
    private String[] mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @BindView(R.id.tv_address) TextView tvAddress;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, itemView);
        }

        public void setData(String data) {
            tvAddress.setText(data);
        }
    }

    interface ItemSelectionCallback {
        void onAddressSelected(String addressName);
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
        holder.setData(mDataset[position]);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onAddressSelected(mDataset[holder.getAdapterPosition()]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.length;
    }

    public void setDataset(String[] dataset) {
        if (dataset == null) return;
        mDataset = dataset;
        notifyDataSetChanged();
    }
}
