package com.buckylabs.appbackuppro;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Apk> apks;
    private Context context;

    public MyAdapter(Context context, List<Apk> apks) {
        this.apks = apks;
        this.context = context;

    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyAdapter.ViewHolder viewHolder, int i) {

        final Apk apk = apks.get(i);
        viewHolder.appName.setText(apk.getAppName());
        viewHolder.appIcon.setImageDrawable(apk.getAppIcon());
        viewHolder.checkBox.setChecked(apk.isChecked());


    }

    @Override
    public int getItemCount() {
        if (apks == null) {
            return 0;
        }
        return apks.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView appName;
        ImageView appIcon;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.textView);
            appIcon = itemView.findViewById(R.id.imageView);
            checkBox = itemView.findViewById(R.id.checkBox);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (apks.get(getAdapterPosition()).isChecked()) {

                checkBox.setChecked(false);
                apks.get(getAdapterPosition()).setChecked(false);

            } else {
                checkBox.setChecked(true);
                apks.get(getAdapterPosition()).setChecked(true);

            }
        }


    }
}