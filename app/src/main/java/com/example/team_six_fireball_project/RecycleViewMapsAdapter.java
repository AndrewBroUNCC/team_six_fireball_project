package com.example.team_six_fireball_project;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecycleViewMapsAdapter extends RecyclerView.Adapter<RecycleViewMapsAdapter.UserViewHolder> {

    final String TAG = "demo";
    FirebaseAuth mAuth;
    RecycleViewMapsAdapter.IRecycleViewMapsAdapter mRecycleViewMapsAdapter;
    ArrayList<FireBall> fireBallsList;
    //RecycleViewExpensesFragAdapter.IRecycleViewExpensesFragAdapter mRecycleViewExpensesFragAdapter;

    public RecycleViewMapsAdapter(ArrayList<FireBall> data, RecycleViewMapsAdapter.IRecycleViewMapsAdapter adapter) {
        //this.mRecycleViewExpensesFragAdapter = adapter;
        this.fireBallsList = data;
        mRecycleViewMapsAdapter = adapter;
        //mRecycleViewExpensesFragAdapter = adapter;

    }

    @NonNull
    @Override
    public RecycleViewMapsAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_maps_item, parent, false);
        RecycleViewMapsAdapter.UserViewHolder userViewHolder = new RecycleViewMapsAdapter.UserViewHolder(view);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewMapsAdapter.UserViewHolder holder, @SuppressLint("RecyclerView") int position) {
        FireBall fireBall = fireBallsList.get(position);
        holder.meteorDate.setText("Date: "+fireBall.getDate());
        holder.meteorLat.setText("Lat: "+fireBall.getLat());
        holder.meteorLon.setText("Lon: "+fireBall.getLon());
        holder.meteorLatDir.setText("LatDir: "+fireBall.getLatDir());
        holder.meteorEnergy.setText("Energy: "+fireBall.getEnergy());
        holder.meteorImpactE.setText("Impact-e: "+fireBall.getImpactE());
        holder.meteorLonDir.setText("LonDir: "+ fireBall.getLonDir());
        holder.meteorAlt.setText("Alt: "+fireBall.getAlt());
        holder.meteorVel.setText("Vel: "+fireBall.getVel());

    }

    @Override
    public int getItemCount() {
        return this.fireBallsList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        //TextView textView = itemView.findViewById(R.id.textViewSortText);
        // View sortViewContainer = itemView.findViewById(R.id.viewSortContainer);
        TextView meteorDate = itemView.findViewById(R.id.textViewMapsFragRecDate);
        TextView meteorLat = itemView.findViewById(R.id.textViewMapsFragRecLat);
        TextView meteorLon = itemView.findViewById(R.id.textViewMapsFragRecLon);
        TextView meteorLatDir = itemView.findViewById(R.id.textViewMapsFragRecLatDir);
        TextView meteorEnergy = itemView.findViewById(R.id.textViewMapsFragRecEnergy);
        TextView meteorImpactE = itemView.findViewById(R.id.textViewMapsFragRecImpactE);
        TextView meteorLonDir = itemView.findViewById(R.id.textViewMapsFragRecLonDir);
        TextView meteorAlt = itemView.findViewById(R.id.textViewMapsFragRecAlt);
        TextView meteorVel = itemView.findViewById(R.id.textViewMapsFragRecVel);

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    interface IRecycleViewMapsAdapter{
    }
}
