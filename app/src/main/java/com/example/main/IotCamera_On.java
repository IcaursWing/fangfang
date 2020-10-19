package com.example.main;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.fangfang_gai.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Iterator;
import java.util.List;

import me.jessyan.autosize.internal.CancelAdapt;
import upload.FTPClientFunctions;

public class IotCamera_On extends Activity implements CancelAdapt {

    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;
    Camera mCamera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_on);

        String IP = getIntent().getStringExtra("IP");
        mSurfaceView = findViewById(R.id.surfaceView_iot);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mCamera = Camera.open();//摄像头的初始化
                    Camera.Parameters parameters = mCamera.getParameters();
                    //p.setPictureSize(500, 500); //设置照片分辨率，太大上传不了

                    // 选择合适的预览尺寸
                    int PreviewWidth = 0;
                    int PreviewHeight = 0;
                    parameters.setPreviewFrameRate(3);//每秒3帧  每秒从摄像头里面获得3个画面
                    parameters.setPictureFormat(PixelFormat.JPEG);//设置照片输出的格式
                    parameters.set("jpeg-quality", 85);//设置照片质量

                    List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
                    // 如果sizeList只有一个我们也没有必要做什么了，因为就他一个别无选择
                    if (sizeList.size() > 1) {
                        Iterator<Camera.Size> itor = sizeList.iterator();
                        while (itor.hasNext()) {
                            Camera.Size cur = itor.next();
                            Log.i("test", cur.height + " " + cur.width);
                            if (cur.width == 1080 && cur.height == 1920) {
                                PreviewWidth = cur.width;
                                PreviewHeight = cur.height;
                            }
                        }
                    }
                    parameters.setPreviewSize(PreviewWidth, PreviewHeight); //获得摄像区域的大小
                    parameters.setPictureSize(PreviewWidth, PreviewHeight);//设置拍出来的屏幕大小

                    List<Camera.Size> msizeList1 = parameters.getSupportedPreviewSizes();
                    List<Camera.Size> msizeList2 = parameters.getSupportedPictureSizes();

                    for (int i = 0; i < msizeList2.size(); i++) {
                        Log.i("test", msizeList2.get(i).height + " " + msizeList2.get(i).width);
                    }

                    boolean isgood1 = true;
                    for (int i = 0; i < msizeList1.size(); i++) {

                        //Log.i("test", "1 " + msizeList1.get(i).height + " " + msizeList1.get(i).width);
                        PreviewWidth = msizeList1.get(i).width;
                        PreviewHeight = msizeList1.get(i).height;
                        parameters.setPreviewSize(PreviewWidth, PreviewHeight);
                        try {
                            mCamera.setParameters(parameters);
                            isgood1 = true;
                        } catch (Exception e) {
                            isgood1 = false;
                        }
                        if (isgood1) {
                            Log.i("test", "yes1 " + msizeList1.get(i).height + " " + msizeList1.get(i).width);
                            break;
                        } else {
                            Log.i("test", "no1 " + msizeList1.get(i).height + " " + msizeList1.get(i).width);
                            //break;
                        }
                    }

                    boolean isgood2 = true;
                    for (int i = 0; i < msizeList2.size(); i++) {
                        isgood2 = true;
                        PreviewWidth = msizeList2.get(i).width;
                        PreviewHeight = msizeList2.get(i).height;
                        parameters.setPictureSize(PreviewWidth, PreviewHeight);
                        try {
                            mCamera.setParameters(parameters);
                        } catch (Exception e) {
                            Log.i("test", "no2 " + msizeList2.get(i).height + " " + msizeList2.get(i).width);
                            isgood2 = false;
                           // break;
                        }
                        if (isgood2) {
                            Log.i("test", "yes2 " + msizeList2.get(i).height + " " + msizeList2.get(i).width);
                          break;
                        }
                    }


                    mCamera.setParameters(parameters);
                    mCamera.setPreviewDisplay(mSurfaceHolder);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                mCamera.startPreview();//开始预览
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {

                    }
                });

                // new TakePhotoThread().start();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        Thread check = new Thread() {
            DatagramSocket ds;

            @Override
            public void run() {
                super.run();

                try {
                    ds = new DatagramSocket(11028);

                    while (isAlive()) {
                        byte[] buf = new byte[5];
                        DatagramPacket dp = new DatagramPacket(buf, 5);
                        ds.receive(dp);
                        int result = dp.getData()[0];
                        if (result == 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new TakePhotoThread().start();
                                }
                            });
                        } else if (result == 2) {
                            Upload();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void interrupt() {
                super.interrupt();
                ds.close();
            }
        };
        check.start();


    }

    private void data2file(byte[] w, String fileName) throws Exception {//将二进制数据转换为文件的函数
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            out.write(w);
            out.close();
        } catch (Exception e) {
            if (out != null)
                out.close();
            throw e;
        }
    }

    private class TakePhotoThread extends Thread {
        @Override
        public void run() {
            super.run();

            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {

                    String path = IotCamera_On.this.getExternalFilesDir(null).getPath() + File.separator + "images" + File.separator;
                    File file = new File(path, "test.jpg");
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    if (file.exists()) {
                        file.delete();
                    }
                    try {
                        data2file(data, file.getPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

        }
    }

    private void Upload() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                FTPClientFunctions ftpClient = new FTPClientFunctions();
                boolean connectResult = ftpClient.ftpConnect("fangfangtech.ftp-gz01.bcehost.com", "fangfangtech", "elwohdoa", 8010);
                if (connectResult) {
                    boolean changeDirResult = ftpClient.ftpChangeDir("/webroot/");
                    if (changeDirResult) {
                        boolean uploadResult = ftpClient.ftpUpload(IotCamera_On.this.getExternalFilesDir(null).getPath() + "/images/test.jpg", "test.jpg", "");
                        if (uploadResult) {
                            Log.w("test", "上传成功");
                            boolean disConnectResult = ftpClient.ftpDisconnect();
                            if (disConnectResult) {
                                Log.e("test", "关闭ftp连接成功");
                            } else {
                                Log.e("test", "关闭ftp连接失败");
                            }
                        } else {
                            Log.w("test", "上传失败");
                        }
                    } else {
                        Log.w("test", "切换ftp目录失败");
                    }

                } else {
                    Log.w("test", "连接ftp服务器失败");
                }
            }
        }).start();
    }


}
