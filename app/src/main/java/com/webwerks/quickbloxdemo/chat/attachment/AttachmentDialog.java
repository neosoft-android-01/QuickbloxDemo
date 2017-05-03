package com.webwerks.quickbloxdemo.chat.attachment;

import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;

import com.webwerks.quickbloxdemo.R;


/**
 * Created by webwerks on 27/4/17.
 */
public class AttachmentDialog extends BottomSheetDialog implements View.OnClickListener {

    private AttachmentListener listener;

    public AttachmentDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_attachment);

        findViewById(R.id.btnPhotoCamera).setOnClickListener(this);
        findViewById(R.id.btnGallery).setOnClickListener(this);
        findViewById(R.id.btnLocation).setOnClickListener(this);
    }

    public void setAttachmentListener(AttachmentListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            switch (v.getId()) {
                case R.id.btnPhotoCamera:
                    listener.onPhotoCameraClick();
                    break;

                case R.id.btnGallery:
                    listener.onGalleryClick();
                    break;

                case R.id.btnLocation:
                    listener.onLocationClick();
                    break;
            }
        }
        dismiss();
    }

    public interface AttachmentListener {
        void onPhotoCameraClick();
        void onGalleryClick();
        void onLocationClick();
    }
}

