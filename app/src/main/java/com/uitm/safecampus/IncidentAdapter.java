package com.uitm.safecampus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class IncidentAdapter extends RecyclerView.Adapter<IncidentAdapter.ViewHolder> {
    private List<Incident> incidentList;

    public IncidentAdapter(List<Incident> incidentList) {
        this.incidentList = incidentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_incident, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Incident incident = incidentList.get(position);
        holder.type.setText(incident.type);
        holder.desc.setText(incident.description);
        holder.details.setText(incident.details);
    }

    @Override
    public int getItemCount() { return incidentList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView type, desc, details;
        public ViewHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.tvListType);
            desc = itemView.findViewById(R.id.tvListDesc);
            details = itemView.findViewById(R.id.tvListDetails);
        }
    }
}