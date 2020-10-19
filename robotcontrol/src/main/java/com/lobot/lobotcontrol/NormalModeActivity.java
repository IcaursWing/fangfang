package com.lobot.lobotcontrol;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.example.robotcontrol.R;
import com.lobot.lobotcontrol.Fragments.FragmentFight;
import com.lobot.lobotcontrol.Fragments.FragmentFootBall;
import com.lobot.lobotcontrol.Fragments.FragmentNormal;
import com.lobot.lobotcontrol.Fragments.FragmentNormal.ModeSelect;
import com.lobot.lobotcontrol.component.popdialog.NormalDialog;
import com.lobot.lobotcontrol.connect.BLEManager;
import com.lobot.lobotcontrol.connect.BLEService;
import com.lobot.lobotcontrol.connect.BLEService.BLEBinder;
import com.lobot.lobotcontrol.uitls.BluetoothUtils;
import com.lobot.lobotcontrol.uitls.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class NormalModeActivity
        extends AppCompatActivity
        implements ModeSelect {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static boolean controlMode = false;
    public static int languageType = 0;
    public static ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder) {
            LogUtil.i(NormalModeActivity.TAG, "BLE Service connected");
            paramAnonymousComponentName = ((BLEBinder) paramAnonymousIBinder).getService();
            BLEManager.getInstance().init(paramAnonymousComponentName);
        }

        public void onServiceDisconnected(ComponentName paramAnonymousComponentName) {
            LogUtil.w(NormalModeActivity.TAG, "BLE Service disconnected");
            BLEManager.getInstance().destroy();
        }
    };
    public static long m_Bat = 65535L;
    public static boolean noShowConnect = false;
    public static int screenHigh;
    public static int screenWidth;
    public static int workMode;
    private boolean confirm;

    private void ReadWorkMode() {
        Object localObject = new File(getFilesDir(), "workmode.dat");
        if (((File) localObject).exists()) {
            try {
                FileInputStream localFileInputStream = new FileInputStream((File) localObject);
                InputStreamReader localInputStreamReader = new InputStreamReader(localFileInputStream);
                localObject = new BufferedReader(new BufferedReader(localInputStreamReader));
                localObject = ((BufferedReader) localObject).readLine();
                if (((String) localObject).contains("workmode:0")) {
                    workMode = 0;
                } else if (((String) localObject).contains("workmode:1")) {
                    workMode = 1;
                } else if (((String) localObject).contains("workmode:2")) {
                    workMode = 2;
                } else {
                    workMode = 0;
                }
                localFileInputStream.close();
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }
    }

    private void saveWorkMode() {
        Object localObject = new File(getFilesDir(), "workmode.dat");
        try {
            FileOutputStream localFileOutputStream = new FileOutputStream((File) localObject);
            localObject = new StringBuilder();
            ((StringBuilder) localObject).append("workmode:");
            ((StringBuilder) localObject).append(String.valueOf(workMode));
            localFileOutputStream.write(((StringBuilder) localObject).toString().getBytes());
            localFileOutputStream.close();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public void onBackPressed() {
        if (BLEManager.getInstance().isConnected()) {
            NormalDialog.createDialog(this, screenWidth / 2, screenHigh / 2, getString(R.string.exit_tips_title), getString(R.string.exit_tips_content), false,
                    new NormalDialog.OnNormalDialogtClickListener() {
                        public void onNormalDialogClick(boolean paramAnonymousBoolean) {
                            if (paramAnonymousBoolean) {
                                BLEManager.getInstance().stop();
                                NormalModeActivity.this.onBackPressed();
                                SysApplication.getInstance().exit();
                            }
                        }
                    }).showDialog();
        } else if (!this.confirm) {
            this.confirm = true;
            Toast.makeText(this, R.string.exit_remind, Toast.LENGTH_SHORT).show();
            new Timer().schedule(new TimerTask() {
                public void run() {
                    finish();
                    //NormalModeActivity.access$202(NormalModeActivity.this, false);
                }
            }, 2000L);
        } else {
            stopService(new Intent(this, BLEService.class));
            BLEManager.getInstance().destroy();
            super.onBackPressed();
            SysApplication.getInstance().exit();
        }
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_normal_mode);
        SysApplication.getInstance().addActivity(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentNormal fragmentNormal = (FragmentNormal) fragmentManager.findFragmentByTag(TAG);


        if (!BluetoothUtils.isSupport(BluetoothAdapter.getDefaultAdapter())) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }
        Locale locale;
        if (Build.VERSION.SDK_INT >= 24) {
            locale = getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = getResources().getConfiguration().locale;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(locale.getLanguage());
        stringBuilder.append("-");
        stringBuilder.append(locale.getCountry());
        String string = stringBuilder.toString();
        if (string.contains("zh-CN")) {
            languageType = 0;
        } else if ((!string.contains("zh-TW")) && (!string.contains("zh-HK"))) {
            if (string.contains("en")) {
                languageType = 2;
            } else {
                languageType = 2;
            }
        } else {
            languageType = 1;
        }
        Intent intent = new Intent(this, BLEService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        BLEManager.getInstance().register(this);
        ReadWorkMode();
        Fragment fragment;
        switch (workMode) {
            default:
                fragment = new FragmentNormal();
                break;
            case 2:
                fragment = new FragmentFight();
                break;
            case 1:
                fragment = new FragmentFootBall();
                break;
            case 0:
                fragment = new FragmentNormal();
        }

        fragmentTransaction.add(R.id.id_container, fragment);
        fragmentTransaction.commit();
    }

    protected void onDestroy() {
        LogUtil.i(TAG, "MainAcivityClose");
        unbindService(mConnection);
        BLEManager.getInstance().unregister(this);
        super.onDestroy();
    }

    public void onModeSelected(int paramInt) {
        FragmentTransaction localFragmentTransaction = getSupportFragmentManager().beginTransaction();
        workMode = paramInt;
        switch (paramInt) {
            default:
                break;
            case 2:
                localFragmentTransaction.replace(R.id.id_container, new FragmentFight());
                localFragmentTransaction.commit();
                break;
            case 1:
                localFragmentTransaction.replace(R.id.id_container, new FragmentFootBall());
                localFragmentTransaction.commit();
                break;
            case 0:
                localFragmentTransaction.replace(R.id.id_container, new FragmentNormal());
                localFragmentTransaction.commit();
        }
        saveWorkMode();
    }

    public void onResume() {
        super.onResume();
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        screenWidth = localDisplayMetrics.widthPixels;
        screenHigh = localDisplayMetrics.heightPixels;
    }
}
