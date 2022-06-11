package com.android.sampleapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> implements RecyclerView.OnItemTouchListener {
    private ArrayList<Model> dataList;
    private OnItemClickListener onItemClickListener;
    Context mContext;

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        return true;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ListAdapter(Context context, ArrayList<Model> modelList) {
        this.dataList = modelList;
        mContext = context;
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Model item = dataList.get(position);
        holder.setDetails(item, position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTvDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvDesc = itemView.findViewById(R.id.tv_text);
            itemView.setOnClickListener(this);
        }

        public void setDetails(Model item, int pos) {
            mTvDesc.setText((pos + 1) + "- " + item.getText());
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(getAdapterPosition());
        }
    }


}


