//package com.example.parkx.tests;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.parkx.R;
//import com.google.android.material.snackbar.Snackbar;
//
//import java.util.List;
//
//public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
//
//    private final List<String> titles;
//    private final List<String> details;
//    private final List<Integer> images;
//
//    public RecyclerAdapter(List<String> titles, List<String> details, List<Integer> images) {
//        this.titles = titles;
//        this.details = details;
//        this.images = images;
//    }
//    //Variables storing data to display for this example
//
//    //Methods that must be implemented for a RecyclerView.Adapter
//    @NonNull
//    @Override
//    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.card_layout, parent, false);
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
//        holder.itemTitle.setText(titles.get(position));
//        holder.itemDetail.setText(details.get(position));
//        holder.itemImage.setImageResource(images.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return titles.size();
//    }
//
//    //Class that holds the items to be displayed (Views in card_layout)
//    static class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView itemImage;
//        TextView itemTitle;
//        TextView itemDetail;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            itemImage = itemView.findViewById(R.id.item_image);
//            itemTitle = itemView.findViewById(R.id.item_title);
//            itemDetail = itemView.findViewById(R.id.item_detail);
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getAdapterPosition();
//
//                    Snackbar.make(v, "Click detected on item " + position,
//                            Snackbar.LENGTH_LONG).show();
//                }
//            });
//        }
//    }
//}
