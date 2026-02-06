package com.uitm.safecampus;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsItem> newsList;

    public NewsAdapter(List<NewsItem> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Load your existing card design
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_card, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem news = newsList.get(position);

        holder.tvTitle.setText(news.title);
        holder.tvDate.setText(news.date);
        holder.tvDesc.setText(news.description);
        holder.tvType.setText(news.type);
        holder.tvLocation.setText(news.location);

        // --- COLOR CODING LOGIC ---
        if (news.type.equals("URGENT")) {
            holder.strip.setBackgroundColor(Color.parseColor("#D32F2F")); // Red
            holder.tvType.setTextColor(Color.parseColor("#D32F2F"));
            holder.tvType.setBackgroundColor(Color.parseColor("#FFEBEE"));
        } else if (news.type.equals("REPORT")) {
            holder.strip.setBackgroundColor(Color.parseColor("#FF9800")); // Orange
            holder.tvType.setTextColor(Color.parseColor("#E65100"));
            holder.tvType.setBackgroundColor(Color.parseColor("#FFF3E0"));
        } else {
            holder.strip.setBackgroundColor(Color.parseColor("#1976D2")); // Blue
            holder.tvType.setTextColor(Color.parseColor("#1976D2"));
            holder.tvType.setBackgroundColor(Color.parseColor("#E3F2FD"));
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvDesc, tvType, tvLocation;
        View strip;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvType = itemView.findViewById(R.id.tvType);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            strip = itemView.findViewById(R.id.viewStrip);
        }
    }
}