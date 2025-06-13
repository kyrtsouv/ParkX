package com.example.parkx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkx.supabase.SupabaseManager;
import com.example.parkx.utils.JavaResultCallback;
import com.example.parkx.utils.RequestStatus;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private final List<String> titles;
    private final List<String> details;
    private final List<Integer> images;
    private final List<Integer> ids;
    private final List<RequestStatus> statuses;
    private final int selectedTab;

    public RecyclerAdapter(List<String> titles, List<String> details, List<Integer> images, List<Integer> ids, List<RequestStatus> statuses, int selectedTab) {
        this.titles = titles;
        this.details = details;
        this.images = images;
        this.ids = ids;
        this.statuses = statuses;

        this.selectedTab = selectedTab;
    }

    /**
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     */
    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(v);
    }

    /**
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        // if (titles != null && !titles.isEmpty() && position < titles.size()) {
        //     holder.itemTitle.setText(titles.get(position));
        // } else {
        //     holder.itemTitle.setText("No data available"); // Prevent crash
        // }

        if (titles != null && details != null && images != null &&
                position < titles.size() && position < details.size() && position < images.size()) {


            if (titles.size() == details.size() && details.size() == images.size() && !images.isEmpty()) {
                holder.itemTitle.setText(titles.get(position));
                holder.itemDetail.setText(details.get(position));
                holder.itemImage.setImageResource(images.get(position));
            } else {
                holder.itemTitle.setText("Error: Mismatched data");
            }
        } else {
            holder.itemTitle.setText("Error: No data available");
            holder.itemDetail.setText("");
            holder.itemImage.setImageResource(R.drawable.crossmark_svgrepo_com); // Provide a fallback image
        }

        if (this.selectedTab == 0) {
            holder.btn_accept.setVisibility(View.GONE);
            holder.btn_reject.setVisibility(View.GONE);
        }
        if (this.selectedTab == 1) {
            if (images != null) {
                if (statuses.get(position) == RequestStatus.PENDING) {
                    holder.itemImage.setVisibility(View.GONE);
                    holder.btn_accept.setOnClickListener(v -> acceptRequest(holder, position));
                    holder.btn_reject.setOnClickListener(v -> rejectRequest(holder, position));
                } else {
                    holder.btn_accept.setVisibility(View.GONE);
                    holder.btn_reject.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     *
     * @param holder
     * @param position
     */
    public void acceptRequest(RecyclerAdapter.ViewHolder holder, int position) {
        SupabaseManager.acceptRequest(ids.get(position), new JavaResultCallback<>() {
            @Override
            public void onSuccess(String value) {
                holder.btn_accept.setVisibility(View.GONE);
                holder.btn_reject.setVisibility(View.GONE);
                holder.itemImage.setImageResource(R.drawable.checkmark_svgrepo_com);
                holder.itemImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(@NonNull Throwable exception) {
                System.out.println("Error accepting request: " + exception.getMessage());
            }
        });
    }

    /**
     *
     * @param holder
     * @param position
     */
    public void rejectRequest(RecyclerAdapter.ViewHolder holder, int position) {
        SupabaseManager.rejectRequest(ids.get(position), new JavaResultCallback<>() {
            @Override
            public void onSuccess(String value) {
                holder.btn_accept.setVisibility(View.GONE);
                holder.btn_reject.setVisibility(View.GONE);
                holder.itemImage.setImageResource(R.drawable.crossmark_svgrepo_com);
                holder.itemImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(@NonNull Throwable exception) {
                System.out.println("Error rejecting request: " + exception.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles == null ? 0 : titles.size();
    }

    //Class that holds the items to be displayed (Views in card_layout)
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView itemTitle;
        TextView itemDetail;
        Button btn_accept;

        Button btn_reject;

        public ViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemDetail = itemView.findViewById(R.id.item_detail);
            btn_accept = itemView.findViewById(R.id.btn_accept);
            btn_reject = itemView.findViewById(R.id.btn_reject);
        }
    }
}
