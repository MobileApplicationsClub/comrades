package com.macbitsgoa.comrades.coursematerial;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.2
 * <p>
 */
public class DownloadService extends IntentService {

    private static final String KEY_FILE_PATH = "filepath";
    private static final String KEY_DOWNLOAD_URL = "downloadUrl";
    private static final String KEY_FILE_NAME = "filename";
    private static final String KEY_FILE_EXTENSION = "extension";
    private static final String TAG = TAG_PREFIX + DownloadService.class.getSimpleName();
    private static final String KEY_ITEM_ID = "itemId";
    private static final String KEY_ITEM_POSITION = "itemPosition";
    private static final String KEY_FILE_SIZE = "fileSize";
    public static final String ACTION = "com.macbitsgoa.comrades.action.ACTION_TAG";
    private static final String RESULT_CODE = "resultCode";

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            String id = intent.getStringExtra(KEY_ITEM_ID);
            String downloadUrl = intent.getStringExtra(KEY_DOWNLOAD_URL);
            String path = intent.getStringExtra(KEY_FILE_PATH);
            String fName = intent.getStringExtra(KEY_FILE_NAME);
            String extension = intent.getStringExtra(KEY_FILE_EXTENSION);
            Long fileLength = intent.getLongExtra(KEY_FILE_SIZE, 5454544);
            int position = intent.getIntExtra(KEY_ITEM_POSITION, 0);

            Bundle sBundle = new Bundle();
            sBundle.putString("id", id);
            sBundle.putInt("position", position);
            sBundle.putInt(RESULT_CODE, 0);
            Intent sIntent = new Intent(ACTION);
            sIntent.putExtras(sBundle);
            LocalBroadcastManager.getInstance(this).sendBroadcast(sIntent);

            int count;
            try {

                final URL url = new URL(downloadUrl);
                final URLConnection connection = url.openConnection();
                connection.setRequestProperty("Accept-Encoding", "identity");
                connection.connect();

                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file

                final OutputStream output = new FileOutputStream(path + fName + extension);
                byte data[] = new byte[1024];
                long total = 0;
                int progress;
                while ((count = input.read(data)) != -1) {
                    Log.e("count", count + "");
                    total += count;

                    progress = (int) (total * 100 / fileLength);
                    if (fileLength > 0) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id", id);
                        bundle.putInt("position", position);
                        bundle.putInt("progress", progress);
                        bundle.putInt(RESULT_CODE, 1);
                        Intent messageIntent = new Intent(ACTION);
                        messageIntent.putExtras(bundle);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

                        // writing data to file
                        output.write(data, 0, count);

                    }
                }
                // flushing output
                output.flush();
                // closing streams
                output.close();
                input.close();

            } catch (final Exception e) {
                Log.e(TAG + ":Error: ", e.getMessage());
            }

            Bundle mBundle = new Bundle();
            mBundle.putString("id", id);
            mBundle.putInt("position", position);
            mBundle.putInt(RESULT_CODE, 2);
            Intent mIntent = new Intent(ACTION);
            mIntent.putExtras(sBundle);
            LocalBroadcastManager.getInstance(this).sendBroadcast(mIntent);

        }


    }


    public static Intent makeIntent(final Context context, final String downloadUrl,
                                    final String fName, final String extension,
                                    final String filePath,
                                    String itemId, Long fileSize) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(KEY_ITEM_ID, itemId);
        intent.putExtra(KEY_DOWNLOAD_URL, downloadUrl);
        intent.putExtra(KEY_FILE_NAME, fName);
        intent.putExtra(KEY_FILE_SIZE, fileSize);
        intent.putExtra(KEY_FILE_EXTENSION, extension);
        intent.putExtra(KEY_FILE_PATH, filePath);
        return intent;
    }

}
