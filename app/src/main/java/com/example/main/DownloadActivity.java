package com.example.main;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.List;

import com.example.download.Channel;
import com.example.download.ChannelService;
import com.example.download.Download;
import com.example.download.DownloadUtil;
import com.example.fangfang_gai.R;
import com.hb.dialog.dialog.LoadingDialog;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.cache.DiskCacheStore;
import com.yanzhenjie.nohttp.cookie.DBCookieStore;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import androidx.core.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ENABLE_GPS = 2;

    public static Context sContext;

    private ListView mListView;
    private List<Channel> channels;
    private ProgressDialog progressDialog;

    private BluetoothAdapter mBluetoothAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        this.setTitle(R.string.app_name);
        sContext = this;

        NoHttp.initialize(InitializationConfig.newBuilder(this).networkExecutor(new OkHttpNetworkExecutor()).cacheStore(new DiskCacheStore(this)).cookieStore(new DBCookieStore(this)).build());

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // mChatService = new BluetoothChatService(this, mHandler);
        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }

        mListView = findViewById(R.id.downloaditem);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Channel tempchannel =
//                        (Channel) mListView.getItemAtPosition(position);
//                downItem("http://mikelee.space/ffykq/" + tempchannel.getName() + "_" + tempchannel.getTime() + ".af", tempchannel.getName() + "_" + tempchannel.getTime() + ".af");
//                view.setBackgroundColor(Color.parseColor("#00FF00"));
            }
        });

        File mFile = new File(this.getExternalFilesDir(null).getPath() + "/actionlist.xml");
        if (mFile.exists()) {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.download_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.download_refesh) {
            downFile("http://mikelee.space/ffykq/actionlist.xml");
            return true;
        } else if (id == R.id.download_connect) {

            AndPermission.with(this).runtime().permission(Permission.ACCESS_FINE_LOCATION).onGranted(new Action<List<String>>() {
                @Override
                public void onAction(List<String> data) {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    } else if (!isGpsEnable(DownloadActivity.this)) {

                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, REQUEST_ENABLE_GPS);
                    } else {
                        Intent intent = new Intent(DownloadActivity.this, DeviceScanDownloadActivity.class);
                        startActivity(intent);
                    }
                }
            }).onDenied(new Action<List<String>>() {
                @Override
                public void onAction(List<String> data) {
                    Uri packageURI = Uri.parse("package:" + getPackageName());
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);

                    Toast.makeText(DownloadActivity.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                }
            }).start();

        }
        return super.onOptionsItemSelected(item);
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return channels.size();
        }

        @Override
        public Object getItem(int position) {
            return channels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewHolder holder = null;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_downloaditem, null);
                holder = new ViewHolder();
                holder.version = convertView.findViewById(R.id.version);
                holder.time = convertView.findViewById(R.id.versiontime);
                Channel channel = channels.get(position);
                convertView.setTag(holder);
                holder.ID = position;
                holder.version.setText(channel.getName());
                holder.time.setText(channel.getTime());

                holder.text1 = convertView.findViewById(R.id.download_text1);
                holder.text2 = convertView.findViewById(R.id.download_text2);
                holder.progress = convertView.findViewById(R.id.download_progress);
                holder.progressBar = convertView.findViewById(R.id.download_progressBar);

                holder.start = convertView.findViewById(R.id.download_download);
                holder.stop = convertView.findViewById(R.id.download_stop);
                holder.delete = convertView.findViewById(R.id.download_delete);
                holder.undone = convertView.findViewById(R.id.download_false);
                holder.done = convertView.findViewById(R.id.download_true);

                itemListener mitemListener = new itemListener(holder, convertView);
                holder.start.setOnClickListener(mitemListener);
                holder.stop.setOnClickListener(mitemListener);
                holder.delete.setOnClickListener(mitemListener);


                if (new File(getExternalFilesDir(null) + "/" + channel.getName() + "_" + channel.getTime() + ".af").exists()) {
                    holder.start.setVisibility(View.GONE);
                    holder.undone.setVisibility(View.GONE);
                    holder.delete.setVisibility(View.VISIBLE);
                    holder.done.setVisibility(View.VISIBLE);
                    holder.text1.setText("删除");
                    holder.text2.setText("已下载");
                    convertView.setBackgroundColor(Color.parseColor("#5000FF00"));
                } else {
                    holder.delete.setVisibility(View.GONE);
                    holder.done.setVisibility(View.GONE);
                    holder.start.setVisibility(View.VISIBLE);
                    holder.undone.setVisibility(View.VISIBLE);
                    holder.text1.setText("下载");
                    holder.text2.setText("未下载");
                    convertView.setBackgroundColor(Color.parseColor("#20FF0000"));
                }
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            return convertView;
        }

    }

    static class ViewHolder {
        // 声明引用
        public TextView version, time, text1, text2, progress;
        public ImageButton start, stop, delete, done, undone;
        public ProgressBar progressBar;
        public int ID;
        public int bug = 0;
    }

    private class itemListener implements OnClickListener {
        ViewHolder mholder = null;
        View mconverView = null;

        public itemListener(ViewHolder tempHolder, View tempconvertView) {
            mholder = tempHolder;
            mconverView = tempconvertView;
        }

        @Override
        public void onClick(View v) {
            DownloadRequest mRequest;
            switch (v.getId()) {
                case R.id.download_download:
                    mholder.start.setVisibility(View.GONE);
                    mholder.undone.setVisibility(View.GONE);
                    mholder.stop.setVisibility(View.VISIBLE);
                    mholder.progress.setVisibility(View.VISIBLE);
                    mholder.text1.setText("下载中");
                    mholder.text2.setText("停止");
                    String murl = "http://mikelee.space/ffykq/" + mholder.version.getText().toString() + "_" + mholder.time.getText().toString() + ".af";
                    Log.i("test", murl);

                    mRequest = new DownloadRequest(murl, RequestMethod.GET, DownloadActivity.this.getExternalFilesDir(null).getPath(),
                            mholder.version.getText().toString() + "_" + mholder.time.getText().toString() + ".af", true, true);
                    mRequest.setCancelSign(mholder.ID);


                    Download.getInstance().download(mholder.ID, mRequest, new DownloadListener() {
                        @Override
                        public void onDownloadError(int what, Exception exception) {
                            mholder.progress.setVisibility(View.GONE);
                            mholder.stop.setVisibility(View.GONE);
                            mholder.start.setVisibility(View.VISIBLE);
                            mholder.undone.setVisibility(View.VISIBLE);
                            mholder.text1.setText("下载");
                            mholder.text2.setText("未下载");
                            Toast.makeText(DownloadActivity.this, "下载失败！", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
                            mholder.start.setVisibility(View.GONE);
                            mholder.undone.setVisibility(View.GONE);
                            mholder.stop.setVisibility(View.VISIBLE);
                            mholder.progress.setVisibility(View.VISIBLE);
                            mholder.text1.setText("下载中");
                            mholder.text2.setText("停止");

                        }

                        @Override
                        public void onProgress(int what, int progress, long fileCount, long speed) {
                            BigDecimal bg = new BigDecimal(speed / 1024D);
                            String speedText = bg.setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString();
                            speedText = getString(R.string.download_speed, speedText);
                            mholder.progressBar.setMax(100);
                            mholder.progressBar.setProgress(progress);
                            mholder.progress.setText(progress + "%");
                            mholder.text1.setText(speedText);
                        }

                        @Override
                        public void onFinish(int what, String filePath) {
                            mholder.progress.setVisibility(View.GONE);
                            mholder.stop.setVisibility(View.GONE);
                            mholder.delete.setVisibility(View.VISIBLE);
                            mholder.done.setVisibility(View.VISIBLE);
                            mholder.text1.setText("删除");
                            mholder.text2.setText("已下载");
                            mconverView.setBackgroundColor(Color.parseColor("#5000FF00"));
                        }

                        @Override
                        public void onCancel(int what) {
                            mholder.progress.setVisibility(View.GONE);
                            mholder.stop.setVisibility(View.GONE);
                            mholder.start.setVisibility(View.VISIBLE);
                            mholder.undone.setVisibility(View.VISIBLE);
                            mholder.text1.setText("下载");
                            mholder.text2.setText("未下载");
                        }
                    });

//                        if (mholder.bug == 0) {
//                            Download.getInstance().cancelBySign(mholder.ID);
//                            mholder.bug = 1;
//
//                        } else {
//                            break;
//                        }

//                    DownloadManager.getInstance().download(murl,
//                            new DownloadObserver() {
//                                @Override
//                                public void onNext(DownloadInfo value) {
//                                    super.onNext(value);
//                                    mholder.progressBar.setMax((int) value.getTotal());
//                                    mholder.progressBar.setProgress((int) value.getProgress());
//                                    mholder.progress.setText(String.valueOf((int) value.getProgress()) + "%");
//                                }
//
//                                @Override
//                                public void onComplete() {
//                                    mholder.progress.setVisibility(View.GONE);
//                                    mholder.stop.setVisibility(View.GONE);
//                                    mholder.delete.setVisibility(View.VISIBLE);
//                                    mholder.done.setVisibility(View.VISIBLE);
//                                    mholder.text1.setText("删除");
//                                    mholder.text2.setText("已下载");
//                                    mconverView.setBackgroundColor(Color.parseColor("#5000FF00"));
//                                }
//
//                                @Override
//                                public void onError(Throwable e) {
//                                    super.onError(e);
//                                    mholder.progress.setVisibility(View.GONE);
//                                    mholder.stop.setVisibility(View.GONE);
//                                    mholder.start.setVisibility(View.VISIBLE);
//                                    mholder.undone.setVisibility(View.VISIBLE);
//                                    mholder.text1.setText("下载");
//                                    mholder.text2.setText("未下载");
//                                    Toast.makeText(DownloadActivity.this, "下载失败！", Toast.LENGTH_SHORT).show();
//                                }
//                            });

                    break;

                case R.id.download_stop:
                    Download.getInstance().cancelBySign(mholder.ID);

//                    DownloadManager.getInstance().cancel("http://mikelee.space/ffykq/" + mholder.version.getText().toString() + "_" + mholder.time.getText().toString() + ".af");
//                    mholder.progress.setVisibility(View.GONE);
//                    mholder.stop.setVisibility(View.GONE);
//                    mholder.start.setVisibility(View.VISIBLE);
//                    mholder.undone.setVisibility(View.VISIBLE);
//                    mholder.text1.setText("下载");
//                    mholder.text2.setText("未下载");
                    break;

                case R.id.download_delete:
                    File file = new File(getExternalFilesDir(null).getPath() + "/" + mholder.version.getText().toString() + "_" + mholder.time.getText().toString() + ".af");
                    if (file.exists()) {
                        file.delete();
                    }
                    mholder.delete.setVisibility(View.GONE);
                    mholder.done.setVisibility(View.GONE);
                    mholder.start.setVisibility(View.VISIBLE);
                    mholder.undone.setVisibility(View.VISIBLE);
                    mholder.text1.setText("下载");
                    mholder.text2.setText("未下载");
                    mconverView.setBackgroundColor(Color.parseColor("#20FF0000"));
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 文件下载
     *
     * @param url
     */
    @SuppressWarnings("deprecation")
    public void downFile(String url) {
        final LoadingDialog loadingDialog = new LoadingDialog(DownloadActivity.this);
        loadingDialog.setMessage("正在同步...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();

//        progressDialog = new ProgressDialog(DownloadActivity.this);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setTitle("正在同步");
//        progressDialog.setMessage("请稍后...");
//        progressDialog.setProgress(0);
//        progressDialog.setMax(100);
//        progressDialog.show();
//        progressDialog.setCancelable(false);
        DownloadUtil.get().download(url, this.getExternalFilesDir(null).getPath(), "actionlist.xml", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                Looper.prepare();
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                // 下载完成进行相关逻辑操作
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);

                Toast.makeText(DownloadActivity.this, "同步成功！", Toast.LENGTH_SHORT).show();
                Looper.loop();

            }

            @Override
            public void onDownloading(int progress) {
                //progressDialog.setProgress(progress);
            }

            @Override
            public void onDownloadFailed(Exception e) {
                Looper.prepare();
                Toast.makeText(DownloadActivity.this, "同步失败！", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                Looper.loop();

                // 下载异常进行相关提示操作
            }
        });
    }

    public void downItem(String url, String filename) {
        progressDialog = new ProgressDialog(DownloadActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在下载动作文件");
        progressDialog.setMessage("请稍后...");
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.show();
        progressDialog.setCancelable(false);
        DownloadUtil.get().download(url, this.getExternalFilesDir(null).getPath(), filename, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();

                }
                // 下载完成进行相关逻辑操作

                Looper.prepare();
                Toast.makeText(DownloadActivity.this, "下载成功！", Toast.LENGTH_SHORT).show();
                Looper.loop();


            }

            @Override
            public void onDownloading(int progress) {
                progressDialog.setProgress(progress);
            }

            @Override
            public void onDownloadFailed(Exception e) {
                Looper.prepare();
                Toast.makeText(DownloadActivity.this, "下载失败！", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                Looper.loop();

                // 下载异常进行相关提示操作
            }
        });
    }

    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    try {
                        FileInputStream is = new FileInputStream(DownloadActivity.this.getExternalFilesDir(null).getPath() + "/actionlist.xml");
                        channels = ChannelService.getChannels(is);
                        Log.i("test", channels.get(0).getName());
                        mListView.setAdapter(new MyAdapter());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        }
    };

    public static final boolean isGpsEnable(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

}
