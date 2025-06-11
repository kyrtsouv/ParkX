package com.example.parkx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkx.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private final List<String> titles;
    private int selectedTab;


    private final List<String> details;

    private final List<Integer> images;

    public RecyclerAdapter(List<String> titles, List<String> details, List<Integer> images) {
        this.titles = titles;
        this.details = details;
        this.images = images;
        this.selectedTab=0;
    }
    public void SelectedTab(int number){
        this.selectedTab=number;


    }
    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
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

        if(this.selectedTab==0){

            holder.btn_accept.setVisibility(View.GONE);
            holder.btn_reject.setVisibility(View.GONE); }
            if(this.selectedTab==1) {
                if (images != null) {
                    if (images.get(position) == R.drawable.crossmark_svgrepo_com || images.get(position) == R.drawable.checkmark_svgrepo_com) {
                        holder.btn_accept.setVisibility(View.GONE);
                        holder.btn_reject.setVisibility(View.GONE);

                    }
                    else if (images.get(position)==R.drawable.pending_svgrepo_com)
                        holder.itemImage.setVisibility(View.GONE);
                }


        }





    }

    @Override
    public int getItemCount() {
        return titles == null ? 0 :titles.size();
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
            btn_accept=itemView.findViewById(R.id.btn_accept);
            btn_reject=itemView.findViewById(R.id.btn_reject);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    Snackbar.make(v, "Click detected on item " + position,
                            Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
