package com.maks.seatimewear;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.view.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maks.seatimewear.model.Spot;

import java.util.ArrayList;
import java.util.Collection;

public class SpotListAdapter extends WearableRecyclerView.Adapter<SpotListAdapter.ViewHolder> {
    private final Object mLock = new Object();
    private ArrayList<Spot> data;
    private Context context;
    private ItemSelectedListener itemSelectedListener;

    /**
     * Indicates whether or not {@link #notifyDataSetChanged()} must be called whenever
     * {@link #data} is modified.
     */
    private boolean mNotifyOnChange = true;

    public SpotListAdapter(Context context, ArrayList<Spot> data) {
        this.context = context;
        this.data = data;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        // private ImageView imageView;

        ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.text_item);
            // imageView = (ImageView) view.findViewById(R.id.item_image);
        }

        void bind(final int position, final ItemSelectedListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onItemSelected(position);
                    }
                }
            });
        }
    }

    public void setListener(ItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    @Override
    public SpotListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_curved_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(SpotListAdapter.ViewHolder holder, final int position) {
        if (data != null && !data.isEmpty()) {
            holder.textView.setText(data.get(position).getValue());
            // holder.imageView.setImageResource(data.get(position).getImage());
            holder.bind(position, itemSelectedListener);
        }
    }

    @Override
    public int getItemCount() {
        if (data != null && !data.isEmpty()) {
            return data.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        super.getItemId(position);

        if (data != null && !data.isEmpty()) {
            return data.get(position).getId();
        }
        return 0;
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        synchronized (mLock) {
            if (data != null) {
                data.clear();
            } else {
                data.clear();
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void addAll(@NonNull Collection<? extends Spot> collection) {
        synchronized (mLock) {
            if (data != null) {
                data.addAll(collection);
            } else {
                data.addAll(collection);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public interface ItemSelectedListener {
        void onItemSelected(int position);
    }
}
