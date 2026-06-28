package com.cleaner.appuninstaller;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AppAdapter.OnAppActionListener {

    private RecyclerView rvApps;
    private AppAdapter appAdapter;
    private List<AppInfo> appList;
    private WhitelistManager whitelistManager;
    
    private Button btnSelectAll, btnDeselectAll, btnSaveWhitelist, btnUninstallSelected;
    private TextView tvStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        whitelistManager = new WhitelistManager(this);
        
        // 扫描已安装应用
        scanApps();
    }

    private void initViews() {
        tvStats = findViewById(R.id.tvStats);
        rvApps = findViewById(R.id.rvApps);
        btnSelectAll = findViewById(R.id.btnSelectAll);
        btnDeselectAll = findViewById(R.id.btnDeselectAll);
        btnSaveWhitelist = findViewById(R.id.btnSaveWhitelist);
        btnUninstallSelected = findViewById(R.id.btnUninstallSelected);

        rvApps.setLayoutManager(new LinearLayoutManager(this));
        
        btnSelectAll.setOnClickListener(v -> selectAll(true));
        btnDeselectAll.setOnClickListener(v -> selectAll(false));
        btnSaveWhitelist.setOnClickListener(v -> saveCurrentWhitelist());
        btnUninstallSelected.setOnClickListener(v -> confirmAndUninstall());
    }

    private void scanApps() {
        AppScanner.scanInstalledApps(this, new AppScanner.ScanCallback() {
            @Override
            public void onScanComplete(List<AppInfo> apps) {
                appList = apps;
                
                // 恢复白名单状态
                for (AppInfo app : appList) {
                    if (whitelistManager.isInWhitelist(app.getPackageName())) {
                        app.setInWhitelist(true);
                    }
                }
                
                setupAdapter();
                updateStats();
            }

            @Override
            public void onScanError(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupAdapter() {
        appAdapter = new AppAdapter(this, appList, this);
        rvApps.setAdapter(appAdapter);
    }

    private void updateStats() {
        int total = appList != null ? appList.size() : 0;
        int whitelisted = whitelistManager.getWhitelistCount();
        String statsText = getString(R.string.total_apps, total) + " | " + 
                          getString(R.string.apps_in_whitelist, whitelisted);
        tvStats.setText(statsText);
    }

    @Override
    public void onSelectionChanged(AppInfo app, boolean isSelected) {
        // 可以在这里添加选中状态变化的逻辑
    }

    @Override
    public void onWhitelistToggle(AppInfo app) {
        if (app.isInWhitelist()) {
            whitelistManager.removeFromWhitelist(app.getPackageName());
            app.setInWhitelist(false);
            Toast.makeText(this, "已从白名单移除: " + app.getAppName(), Toast.LENGTH_SHORT).show();
        } else {
            whitelistManager.addToWhitelist(app.getPackageName());
            app.setInWhitelist(true);
            app.setSelected(false); // 加入白名单后自动取消选中（避免误删）
            Toast.makeText(this, "已添加到白名单: " + app.getAppName(), Toast.LENGTH_SHORT).show();
        }
        updateStats();
        if (appAdapter != null) {
            appAdapter.notifyDataSetChanged();
        }
    }

    private void selectAll(boolean select) {
        if (appList == null) return;
        
        for (AppInfo app : appList) {
            // 白名单中的应用不参与批量选择
            if (!app.isInWhitelist()) {
                app.setSelected(select);
            }
        }
        if (appAdapter != null) {
            appAdapter.notifyDataSetChanged();
        }
    }

    private void saveCurrentWhitelist() {
        whitelistManager.saveWhitelist();
        Toast.makeText(this, R.string.whitelist_saved, Toast.LENGTH_SHORT).show();
    }

    private void confirmAndUninstall() {
        List<AppInfo> selectedApps = getSelectedApps();
        
        if (selectedApps.isEmpty()) {
            Toast.makeText(this, R.string.no_apps_selected, Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查是否有白名单应用被意外选中
        List<AppInfo> toRemove = new ArrayList<>();
        for (AppInfo app : selectedApps) {
            if (!whitelistManager.isInWhitelist(app.getPackageName())) {
                toRemove.add(app);
            }
        }

        if (toRemove.isEmpty()) {
            Toast.makeText(this, "选中的应用都在白名单中，不会被卸载", Toast.LENGTH_SHORT).show();
            return;
        }

        String message = String.format(getString(R.string.confirm_uninstall_message), toRemove.size());
        
        new AlertDialog.Builder(this)
            .setTitle(R.string.confirm_uninstall_title)
            .setMessage(message)
            .setPositiveButton("确认卸载", (dialog, which) -> startUninstall(toRemove))
            .setNegativeButton("取消", null)
            .show();
    }

    private List<AppInfo> getSelectedApps() {
        List<AppInfo> selected = new ArrayList<>();
        if (appList == null) return selected;
        
        for (AppInfo app : appList) {
            if (app.isSelected() && !app.isInWhitelist()) {
                selected.add(app);
            }
        }
        return selected;
    }

    /**
     * 一键卸载：卸载所有不在白名单中的已选中应用
     */
    private void startUninstall(List<AppInfo> appsToUninstall) {
        if (appsToUninstall.isEmpty()) return;

        // 逐个发起卸载请求
        for (int i = 0; i < appsToUninstall.size(); i++) {
            AppInfo app = appsToUninstall.get(i);
            
            try {
                Intent intent = new Intent(Intent.ACTION_DELETE, 
                    Uri.parse("package:" + app.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                
                if (i == appsToUninstall.size() - 1) {
                    // 最后一个应用，等待结果
                    startActivityForResult(intent, 1001);
                } else {
                    startActivity(intent);
                }
                
                // 延迟一下，避免同时弹出多个卸载对话框
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
            } catch (Exception e) {
                Toast.makeText(this, "无法卸载 " + app.getAppName() + ": " + e.getMessage(), 
                              Toast.LENGTH_SHORT).show();
            }
        }
        
        Toast.makeText(this, R.string.uninstalling, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            // 卸载完成后刷新列表
            scanApps();
            Toast.makeText(this, R.string.uninstall_complete, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 高级功能：一键清除所有非白名单应用（危险操作）
     * 需要用户二次确认
     */
    public void uninstallAllNonWhitelisted(View view) {
        if (appList == null) return;
        
        List<AppInfo> nonWhitelisted = whitelistManager.getAppsNotInWhitelist(appList);
        
        if (nonWhitelisted.isEmpty()) {
            Toast.makeText(this, "所有应用都已在白名单中", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("即将卸载以下 ").append(nonWhitelisted.size()).append(" 个应用：\n\n");
        for (int i = 0; i < Math.min(10, nonWhitelisted.size()); i++) {
            sb.append("• ").append(nonWhitelisted.get(i).getAppName()).append("\n");
        }
        if (nonWhitelisted.size() > 10) {
            sb.append("... 等共 ").append(nonWhitelisted.size()).append(" 个应用\n\n");
        }
        sb.append("\n⚠️ 此操作不可撤销！确定继续吗？");

        new AlertDialog.Builder(this)
            .setTitle("⚠️ 危险操作确认")
            .setMessage(sb.toString())
            .setPositiveButton("我已了解风险，继续卸载", (dialog, which) -> {
                startUninstall(nonWhitelisted);
            })
            .setNegativeButton("取消", null)
            .show();
    }
}
