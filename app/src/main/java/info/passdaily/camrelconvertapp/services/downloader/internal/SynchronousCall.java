package info.passdaily.camrelconvertapp.services.downloader.internal;

import info.passdaily.camrelconvertapp.services.downloader.Response;
import info.passdaily.camrelconvertapp.services.downloader.request.DownloadRequest;

public class SynchronousCall {

    public final DownloadRequest request;

    public SynchronousCall(DownloadRequest request) {
        this.request = request;
    }

    public Response execute() {
        DownloadTask downloadTask = DownloadTask.create(request);
        return downloadTask.run();
    }

}
