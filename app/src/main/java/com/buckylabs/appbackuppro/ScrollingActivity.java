package com.buckylabs.appbackuppro;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.widget.GridLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;

public class ScrollingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Context context;
    private List<Apk> apks;
    private PackageManager pm;
    private boolean isChecked;
    private MyAdapter adapter;
    private boolean isAllChecked;
    private List<ApplicationInfo> listofApkstoBackup;
    private Handler handler;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        context = this;
        pm = getPackageManager();
        isChecked = false;
        isAllChecked = true;
        apks = new ArrayList<>();
        listofApkstoBackup = new ArrayList<>();
        handler = new Handler(getMainLooper());

        Button backup = findViewById(R.id.backUp);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();
        DividerItemDecoration itemDecor = new DividerItemDecoration(context, HORIZONTAL);
        itemDecor.setOrientation(VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(context, apks);
        recyclerView.setAdapter(adapter);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isSys = preferences.getBoolean("show_sys_apps_pref", false);

        Log.e("Pref", "" + isSys);
        getStoragePermission();
        createDirectory();
        getApks(isSys);

        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                backupApks();

            }
        });


    }

    @Override
    public void onRestart() {
        super.onRestart();
        // SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        final boolean isSys = preferences.getBoolean("show_sys_apps_pref", false);
        handler.post(new Runnable() {
            @Override
            public void run() {

                Log.e("Pref_onRestart", "" + isSys);
                apks.clear();
                getApks(isSys);
                adapter.notifyDataSetChanged();
            }
        });


    }



    public void backupApks() {
        createDirectory();
        adapter.notifyDataSetChanged();
        for (Apk apk : apks) {

            if (apk.isChecked()) {
                listofApkstoBackup.add(apk.getAppInfo());
                Log.e("AppName", " " + apk.getAppName());
            }
        }
        if (listofApkstoBackup.size() == 0) {

            Toast.makeText(context, "No Apps Selected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Backing Up", Toast.LENGTH_SHORT).show();
            Log.e("Apps", (listofApkstoBackup.size()) + "");

            writeData(listofApkstoBackup);
            listofApkstoBackup.clear();
            uncheckAllBoxes();
        }


    }

    public void writeData(List<ApplicationInfo> listapks) {
        for (ApplicationInfo info : listapks) {
            Log.e("Size------  ", listapks.size() + "  ");
            try {
                File f1 = new File(info.sourceDir);
                String rootPath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/App_Backup/";
                String file_name = info.loadLabel(getPackageManager()).toString();
                File f2 = new File(rootPath);
                if (!f2.exists()) {
                    f2.mkdirs();
                }
                Log.e("Backing up ", file_name);
                f2 = new File(f2.getPath() + "/" + file_name + ".apk");
                f2.createNewFile();
                InputStream in = new FileInputStream(f1);
                FileOutputStream out = new FileOutputStream(f2);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                Log.e("BackUp Complete ", file_name);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    public void createDirectory() {
        String rootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/App_Backup_Pro/";
        File f2 = new File(rootPath);
        if (!f2.exists()) {
            f2.mkdirs();
        }
    }


    public void getStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.WRITE_SETTINGS}, 1);
            requestPermissions(new String[]{Manifest.permission.MANAGE_DOCUMENTS}, 1);

        }
    }

    public void getApks(boolean isSys) {

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        Collections.sort(apps, new ApplicationInfo.DisplayNameComparator(pm));
        for (ApplicationInfo app : apps) {

            if (isSys) {
                Apk apk = new Apk((String) app.loadLabel(pm), app.loadIcon(pm), app, isChecked);
                apks.add(apk);

            } else {

                if ((app.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0) {

                } else {
                    Apk apk = new Apk((String) app.loadLabel(pm), app.loadIcon(pm), app, isChecked);
                    apks.add(apk);
                }


            }
        }


    }


    public void uncheckAllBoxes() {

        for (Apk apk : apks) {
            apk.setChecked(false);

        }
        adapter.notifyDataSetChanged();
    }


    public void checkAllBoxes() {
        for (Apk apk : apks) {
            apk.setChecked(true);

        }

        adapter.notifyDataSetChanged();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(context, SettingsActivity.class);
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
            intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);

            startActivity(intent);
            return true;
        }
        if (id == R.id.checkAll) {

            if (isAllChecked) {
                item.setIcon(R.drawable.ic_check_box_black_24dp);
                checkAllBoxes();
                isAllChecked = false;
                Toast.makeText(context, "Check All", Toast.LENGTH_SHORT).show();

            } else {
                item.setIcon(R.drawable.ic_check_box_unchecked_24dp);
                uncheckAllBoxes();
                isAllChecked = true;
                Toast.makeText(context, "Un Check All", Toast.LENGTH_SHORT).show();
            }


            //checkAllBoxes();


            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
