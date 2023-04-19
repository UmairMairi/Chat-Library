package com.xint.example.chat;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xint.chatfooter.AttachmentOption;
import com.xint.chatfooter.AttachmentOptionsListener;
import com.xint.chatfooter.ChatFooterView;
import com.xint.example.R;

import java.util.Objects;


public class ChattingActivity extends AppCompatActivity implements
        ChatFooterView.RecordingListener, View.OnClickListener,
        AttachmentOptionsListener {

    private ChatFooterView audioRecordView;
    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;

    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        audioRecordView = new ChatFooterView();
        audioRecordView.initView((FrameLayout) findViewById(R.id.layoutMain));
        View containerView = audioRecordView.setContainerView(R.layout.layout_chatting);
        audioRecordView.setRecordingListener(this);
        recyclerViewMessages = containerView.findViewById(R.id.recyclerViewMessages);
        messageAdapter = new MessageAdapter();
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewMessages.setHasFixedSize(false);
        recyclerViewMessages.setAdapter(messageAdapter);
        recyclerViewMessages.setItemAnimator(new DefaultItemAnimator());

        setListener();
        Objects.requireNonNull(audioRecordView.getMessageView()).requestFocus();

        containerView.findViewById(R.id.imageViewTitleIcon).setOnClickListener(this);
        containerView.findViewById(R.id.imageViewMenu).setOnClickListener(this);


        audioRecordView.setAttachmentOptions(AttachmentOption.Companion.getDefaultList(), this);

        audioRecordView.removeAttachmentOptionAnimation(false);
    }

    private void setListener() {


        audioRecordView.getCameraView().setOnClickListener(v -> {
            audioRecordView.hideAttachmentOptionView();
            showToast("Camera Icon Clicked");
        });

        audioRecordView.getSendView().setOnClickListener(v -> {
            String msg = audioRecordView.getMessageView().getText().toString().trim();
            audioRecordView.getMessageView().setText("");
            messageAdapter.add(new Message(msg));
        });
    }

    @Override
    public void onRecordingStarted() {
        showToast("started");
        debug("started");

        time = System.currentTimeMillis() / (1000);
    }

    @Override
    public void onRecordingLocked() {
        showToast("locked");
        debug("locked");
    }

    @Override
    public void onRecordingCompleted() {
        showToast("completed");
        debug("completed");

        int recordTime = (int) ((System.currentTimeMillis() / (1000)) - time);

        if (recordTime > 1) {
            messageAdapter.add(new Message(recordTime));
        }
    }

    @Override
    public void onRecordingCanceled() {
        showToast("canceled");
        debug("canceled");
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void debug(String log) {
        Log.d("VarunJohn", log);
    }

    @Override
    public void onClick(View view) {
        showDialog();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Created by:\nVarun John\nvarunjohn1990@gmail.com\n\nCheck code on Github :\nhttps://github.com/varunjohn/Audio-Recording-Animation")
                .setCancelable(false)
                .setPositiveButton("Github", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String url = "https://github.com/varunjohn/Audio-Recording-Animation";
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setPackage("com.android.chrome");
                        try {
                            startActivity(i);
                        } catch (ActivityNotFoundException e) {
                            i.setPackage(null);
                            try {
                                startActivity(i);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    @Override
    public void onClick(AttachmentOption attachmentOption) {
        switch (attachmentOption.getId()) {

            case AttachmentOption.DOCUMENT_ID:
                showToast("Document Clicked");
                break;
            case AttachmentOption.CAMERA_ID:
                showToast("Camera Clicked");
                break;
            case AttachmentOption.GALLERY_ID:
                showToast("Gallery Clicked");
                break;
            case AttachmentOption.AUDIO_ID:
                showToast("Audio Clicked");
                break;
            case AttachmentOption.LOCATION_ID:
                showToast("Location Clicked");
                break;
            case AttachmentOption.CONTACT_ID:
                showToast("Contact Clicked");
                break;
        }
    }
}
