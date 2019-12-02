package ru.unfortunately.school.audiorecorder;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.unfortunately.school.audiorecorder.RecordListAdapter.ViewHolder;

public class RecordListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<File> mFiles;

    public RecordListAdapter(List<File> files){
        mFiles = new ArrayList<>(files);
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_record_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mView.setText(mFiles.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    private void setPaths(List<File> files){
        mFiles = new ArrayList<>(files);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView.findViewById(R.id.text_record_item);
        }
    }
}
