package download;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by lws on 2017/3/14.
 */

public abstract class DownloadObserver implements Observer<DownloadInfo> {

    protected Disposable d;
    protected DownloadInfo mDownloadInfo;

    @Override
    public void onSubscribe(Disposable d) {
        this.d = d;
    }

    @Override
    public void onNext(DownloadInfo downloadInfo) {
        this.mDownloadInfo = downloadInfo;
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }
}
