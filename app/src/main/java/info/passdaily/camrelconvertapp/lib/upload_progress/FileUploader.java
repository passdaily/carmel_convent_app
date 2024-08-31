package info.passdaily.camrelconvertapp.lib.upload_progress;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import info.passdaily.camrelconvertapp.model.FileResultModel;
import info.passdaily.camrelconvertapp.services.retrofit.ApiInterface;
import kotlinx.serialization.json.JsonElement;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

public class FileUploader {

    public FileUploaderCallback fileUploaderCallback;
    private File[] files;
    public int uploadIndex = -1;
    private String uploadURL = "";
    private long totalFileLength = 0;
    private long totalFileUploaded = 0;
    private String filekey="";
    private UploadInterface uploadInterface;
    private String auth_token = "";
    private String[] responses;

    private String typeReturn;

    String TAG = "FileUploader";


    private interface UploadInterface {

//        @Multipart
//        // POST request to upload an image from storage
//        @POST("{path}")
//        Call<ResponseBody> uploadImage(
//                @Path("path") String path,
//                @Part MultipartBody.Part image);

        @Multipart
        @POST
        Call<ResponseBody> uploadFile(@Url String url, @Part MultipartBody.Part file, @Header("Authorization") String authorization);

        @Multipart
        @POST
        Call<ResponseBody> uploadFile(@Url String url, @Part MultipartBody.Part file);
    }

    public interface FileUploaderCallback{
        void onError();
        void onFinish(String[] responses);
        void onProgressUpdate(int currentpercent, int totalpercent, int filenumber);
    }

    public class PRRequestBody extends RequestBody {
        private File mFile;

        private static final int DEFAULT_BUFFER_SIZE = 2048;

        public PRRequestBody(final File file) {
            mFile = file;

        }

        @Override
        public MediaType contentType() {
            // i want to upload only images
            return MediaType.parse("multipart/form-file");
        }

        @Override
        public long contentLength() throws IOException {
            return mFile.length();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            long fileLength = mFile.length();
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            FileInputStream in = new FileInputStream(mFile);
            long uploaded = 0;

            try {
                int read;
                Handler handler = new Handler(Looper.getMainLooper());
                while ((read = in.read(buffer)) != -1) {

                    // update progress on UI thread
                    handler.post(new ProgressUpdater(uploaded, fileLength));
                    uploaded += read;
                    sink.write(buffer, 0, read);
                }
            } finally {
                in.close();
            }
        }
    }

    public FileUploader(int adminId){
        if(adminId == 1 || adminId == 3 || adminId == 5) {
            uploadInterface = ApiClient.getClient().create(UploadInterface.class);
        }else if(adminId == 4){
            uploadInterface = ApiClient.getClientParent().create(UploadInterface.class);
        }
    }

    public void uploadFiles(String url,String filekey,File[] files , String typeReturn, FileUploaderCallback fileUploaderCallback){
        uploadFiles(url,filekey,files,typeReturn,fileUploaderCallback,"");
    }

    public void uploadFiles(String url,String filekey,File[] files, String typeReturn, FileUploaderCallback fileUploaderCallback,String auth_token){
        this.fileUploaderCallback = fileUploaderCallback;
        this.files = files;
        this.typeReturn = typeReturn;
        this.uploadIndex = -1;
        this.uploadURL = url;
        this.filekey = filekey;
        this.auth_token = auth_token;
        totalFileUploaded = 0;
        totalFileLength = 0;
        uploadIndex = -1;
        responses = new String[files.length];
        for(int i=0; i<files.length; i++){
            totalFileLength = totalFileLength + files[i].length();
        }
        uploadNext();
    }

    private void uploadNext(){
        if(files.length>0){
            if(uploadIndex!= -1)
                totalFileUploaded = totalFileUploaded + files[uploadIndex].length();
            uploadIndex++;
            if(uploadIndex < files.length){
                uploadSingleFile(uploadIndex);
            }else{
                fileUploaderCallback.onFinish(responses);
            }
        }else{
            fileUploaderCallback.onFinish(responses);
        }
    }

    private void uploadSingleFile(final int index){
        PRRequestBody fileBody = new PRRequestBody(files[index]);
        // RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-file"), files[index]);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(filekey, files[index].getName(), fileBody);
        Call<ResponseBody>  call  = uploadInterface.uploadFile(uploadURL, filePart);
//        if(auth_token.isEmpty()){
//            call  = uploadInterface.uploadFile(uploadURL, filePart);
//        }else{
//            call  = uploadInterface.uploadFile(uploadURL, filePart, auth_token);
//        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.i(TAG, "onResponse " + response);

                if(typeReturn.equals("voice")){
                    try {
                        if (response.body() != null) {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            Log.i(TAG, "jsonObject " + jsonObject);
                            String result = jsonObject.get("RESULT").toString();
                            String filename = jsonObject.get("VOICE_MAIL_FILE").toString();
                            String templateId = jsonObject.get("TEMPLATE_ID").toString();

                            responses[index] = filename+"~"+templateId;
                            // responses[index] = templateId;
                        } else {
                            responses[index] = "";
                        }
                        uploadNext();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        if (response.body() != null) {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            Log.i(TAG, "jsonObject " + jsonObject);
                            String result = jsonObject.get("RESULT").toString();
                            String filename = jsonObject.get("DETAILS").toString();

                            responses[index] = filename;
                        } else {
                            responses[index] = "";
                        }
                        uploadNext();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                fileUploaderCallback.onError();
                Log.i(TAG,"onFailure "+t);
            }
        });
    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;
        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            int current_percent = (int)(100 * mUploaded / mTotal);
            int total_percent = (int)(100 * (totalFileUploaded+mUploaded) / totalFileLength);
            fileUploaderCallback.onProgressUpdate(current_percent, total_percent,uploadIndex+1 );
        }
    }
}

class ApiClient {
    // var BASE_URL = "http://parent.teachdaily.in/PassDailyParentsApi/"
    //        var BASE_URLS = "http://staff.teachdaily.in/ElixirApi/"
    public static final String BASE_URLS = ApiInterface.Companion.getBASE_URLS();
    public static final String BASE_URL = ApiInterface.Companion.getBASE_URL();
    private static Retrofit retrofit = null;



    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URLS)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClientParent() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
