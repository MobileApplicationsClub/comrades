package com.macbitsgoa.comrades.csa;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.macbitsgoa.comrades.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AttachmentsAdapter extends RecyclerView.Adapter<AttachmentsAdapter.AttachmentsVH> {

    private List<Attachment> attachments;
    private Context context;

    public AttachmentsAdapter(final List<Attachment> attachments, Context context) {
        this.attachments = attachments;
        this.context = context;
    }

    @Override
    public AttachmentsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AttachmentsVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vh_attachment, parent, false));
    }

    @Override
    public void onBindViewHolder(AttachmentsVH holder, int position) {
        holder.populateFile(attachments.get(position));
    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }

    public class AttachmentsVH extends RecyclerView.ViewHolder {
        private TextView filenameTv;
        private SimpleDraweeView fileThumbSdv;



        public AttachmentsVH(View itemView) {
            super(itemView);
            filenameTv = itemView.findViewById(R.id.tv_filename);
            fileThumbSdv = itemView.findViewById(R.id.sdv_file_thumb);

        }

        void populateFile(Attachment attachment)
        {
            filenameTv.setText(attachment.name);

            if(attachment.type.equalsIgnoreCase("image"))
            {
                fileThumbSdv.setImageURI(attachment.url);
            }
            else if(attachment.type.equalsIgnoreCase("pdf"))
            {
                fileThumbSdv.setImageResource(R.drawable.ic_pdf_black_24dp);
                fileThumbSdv.setColorFilter(Color.parseColor("#F44336"));
            }
            else if(attachment.type.equalsIgnoreCase("doc"))
            {
                fileThumbSdv.setImageResource(R.drawable.icon_doc);
                fileThumbSdv.setColorFilter(Color.parseColor("#56ABE4"));
            }
            else if (attachment.type.equalsIgnoreCase("ppt"))
            {
                fileThumbSdv.setImageResource(R.drawable.icon_ppt);
                fileThumbSdv.setColorFilter(Color.parseColor("#f39c12"));
            }
            else if(attachment.type.equalsIgnoreCase("sheet"))
            {
                fileThumbSdv.setImageResource(R.drawable.icon_xls);
                fileThumbSdv.setColorFilter(Color.parseColor("#008000"));
            }

            fileThumbSdv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DownloadManager downloadManager =(DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(attachment.url);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setVisibleInDownloadsUi(true);
                    request.setTitle("Attachment");
                    request.setDescription(""+attachment.name);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setMimeType(getMimeType(uri.toString()));
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                            "/Comrades CSA Media/"+attachment.name+"."+MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
                    Long ref = downloadManager.enqueue(request);
                }
            });
        }

        private String getMimeType(String url) {
            String type = null;
            String extension = MimeTypeMap.getFileExtensionFromUrl(url);
            if (extension != null) {
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                type = mime.getMimeTypeFromExtension(extension);
            }
            return type;
        }



    }
}
