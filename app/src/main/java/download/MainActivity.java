package download;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fangfang_gai.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_progress1)
    ProgressBar mMainProgress1;
    @BindView(R.id.main_btn_down1)
    Button mMainBtnDown1;
    @BindView(R.id.main_btn_cancel1)
    Button mMainBtnCancel1;
    @BindView(R.id.main_progress2)
    ProgressBar mMainProgress2;
    @BindView(R.id.main_btn_down2)
    Button mMainBtnDown2;
    @BindView(R.id.main_btn_cancel2)
    Button mMainBtnCancel2;
    @BindView(R.id.main_progress3)
    ProgressBar mMainProgress3;
    @BindView(R.id.main_btn_down3)
    Button mMainBtnDown3;
    @BindView(R.id.main_btn_cancel3)
    Button mMainBtnCancel3;

    private String url1 = "http://192.168.31.169:8080/out/dream.flac";
    private String url2 = "http://192.168.31.169:8080/out/music.mp3";
    private String url3 = "http://192.168.31.169:8080/out/code.zip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_demo);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.main_btn_down1, R.id.main_btn_cancel1, R.id.main_btn_down2, R.id.main_btn_cancel2, R.id.main_btn_down3, R.id.main_btn_cancel3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_btn_down1:
                DownloadManager.getInstance().download(url1, new DownloadObserver() {
                    @Override
                    public void onNext(DownloadInfo value) {
                        super.onNext(value);
                        mMainProgress1.setMax((int) value.getTotal());
                        mMainProgress1.setProgress((int) value.getProgress());
                    }

                    @Override
                    public void onComplete() {
                        if (mDownloadInfo != null) {
                            Toast.makeText(MainActivity.this,
                                    mDownloadInfo.getFileName() + "-DownloadComplete",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.main_btn_down2:
                DownloadManager.getInstance().download(url2, new DownloadObserver() {
                    @Override
                    public void onNext(DownloadInfo value) {
                        super.onNext(value);
                        mMainProgress2.setMax((int) value.getTotal());
                        mMainProgress2.setProgress((int) value.getProgress());
                    }

                    @Override
                    public void onComplete() {
                        if (mDownloadInfo != null) {
                            Toast.makeText(MainActivity.this,
                                    mDownloadInfo.getFileName() + Uri.encode("下载完成"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.main_btn_down3:
                DownloadManager.getInstance().download(url3, new DownloadObserver() {
                    @Override
                    public void onNext(DownloadInfo value) {
                        super.onNext(value);
                        mMainProgress3.setMax((int) value.getTotal());
                        mMainProgress3.setProgress((int) value.getProgress());
                    }

                    @Override
                    public void onComplete() {
                        if (mDownloadInfo != null) {
                            Toast.makeText(MainActivity.this,
                                    mDownloadInfo.getFileName() + "下载完成",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.main_btn_cancel1:
                DownloadManager.getInstance().cancel(url1);
                break;
            case R.id.main_btn_cancel2:
                DownloadManager.getInstance(). cancel(url2);
                break;
            case R.id.main_btn_cancel3:
                DownloadManager.getInstance().cancel(url3);
                break;
        }
    }
}
