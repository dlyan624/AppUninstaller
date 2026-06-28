package com.cleaner.appuninstaller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {

    private List<AppInfo> appList;
    private Context context;
    private OnAppActionListener listener;

    public interface OnAppActionListener {
        void onSelectionChanged(AppInfo app, boolean isSelected);
        void onWhitelistToggle(AppInfo app);
    }

    public AppAdapter(Context context, List<AppInfo> appList, OnAppActionListener listener) {
        this.context = context;
        this.appList = appList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppInfo app = appList.get(position);

        holder.tvAppName.setText(app.getAppName());
        holder.tvPackageName.setText(app.getPackageName());
        holder.ivAppIcon.setImageDrawable(app.getIcon());
        
        holder.checkBoxSelect.setChecked(app.isSelected());
        holder.checkBoxSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            app.setSelected(isChecked);
            if (listener != null) {
                listener.onSelectionChanged(app, isChecked);
            }
        });

        // 白名单状态显示
        updateWhitelistButton(holder, app);

        holder.btnToggleWhitelist.setOnClickListener(v -> {
            if (listener != null) {
                listener.onWhitelistToggle(app);
                updateWhitelistButton(holder, app);
            }
        });
    }

    private void updateWhitelistButton(AppViewHolder holder, AppInfo app) {
        if (app.isInWhitelist()) {
            holder.btnToggleWhitelist.setImageResource(android.R.drawable.ic_lock_idle_lock);
            holder.btnToggleWhitelist.setContentDescription("从白名单移除");
        } else {
            holder.btnToggleWhitelist.setImageResource(android.R.drawable.ic_menu_manage);
            holder.btnToggleWhitelist.setContentDescription("添加到白名单");
        }
    }

    @Override
    public int getItemCount() { return appList.size(); }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxSelect;
        ImageView ivAppIcon;
        TextView tvAppName;
        TextView tvPackageName;
        ImageButton btnToggleWhitelist;

        AppViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxSelect = itemView.findViewById(R.id.checkBoxSelect);
            ivAppIcon = itemView.findViewById(R.id.ivAppIcon);
            tvAppName = itemView.findViewById(R.id.tvAppName);
            tvPackageName = itemView.findViewById(R.id.tvPackageName);
            btnToggleWhitelist = itemView.findViewById(R.id.btnToggleWhitelist);
        }
    }
}
