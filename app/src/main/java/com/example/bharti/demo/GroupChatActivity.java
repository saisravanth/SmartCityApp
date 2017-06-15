package com.example.bharti.demo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import com.google.firebase.auth.FirebaseAuth;


public class GroupChatActivity extends AppCompatActivity implements View.OnClickListener,
        MessageDataSource.MessagesCallbacks{

    public static final String USER_EXTRA = "USER";

    public static final String TAG = "ChatActivity";

    private ArrayList<Message> mMessages;
    private MessagesAdapter mAdapter;
    private String mRecipient;
    private ListView mListView;
    private Date mLastMessageDate = new Date();
    private String mConvoId;
    private MessageDataSource.MessagesListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);



        if (null == FirebaseAuth.getInstance().getCurrentUser()) {
            smartCityChat context = (smartCityChat)getApplicationContext();
            mRecipient = context.getUser().getFullName();
        } else {
            mRecipient = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }

        mListView = (ListView)findViewById(R.id.messages_list);
        mMessages = new ArrayList<>();
        mAdapter = new MessagesAdapter(mMessages);
        mListView.setAdapter(mAdapter);
        setTitle(getIntent().getStringExtra("topicname"));

        Button sendMessage = (Button)findViewById(R.id.send_message);
        sendMessage.setOnClickListener(this);

        mConvoId =  getIntent().getStringExtra("topicname") + "/messages/";
        mListener = MessageDataSource.addMessagesListener(mConvoId , this);
    }

    public void onClick(View v) {
        String senderName = "";
        if (null == FirebaseAuth.getInstance().getCurrentUser()) {
            smartCityChat context = (smartCityChat)getApplicationContext();
            senderName = context.getUser().getFullName();
        } else {
            senderName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }
        EditText newMessageView = (EditText)findViewById(R.id.new_message);
        String newMessage = newMessageView.getText().toString();
        newMessageView.setText("");
        Message msg = new Message();
        msg.setDate(new Date());
        msg.setText(senderName + System.getProperty("line.separator") + newMessage);
        msg.setSender(senderName);
        MessageDataSource.saveMessage(msg, mConvoId);
    }

    @Override
    public void onMessageAdded(Message message) {
        mMessages.add(message);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageDataSource.stop(mListener);
    }


    private class MessagesAdapter extends ArrayAdapter<Message> {
        MessagesAdapter(ArrayList<Message> messages){
            super(GroupChatActivity.this, R.layout.message_item, R.id.message, messages);
        }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            final Message message = getItem(position);

            TextView nameView = (TextView)convertView.findViewById(R.id.message);
            nameView.setText(message.getText());


            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)nameView.getLayoutParams();

            int sdk = Build.VERSION.SDK_INT;
            if (message.getSender() != null){
                if (sdk >= Build.VERSION_CODES.JELLY_BEAN) {
                    nameView.setBackground(getDrawable(R.drawable.bubble_right_green));
                } else{
                    nameView.setBackgroundDrawable(getDrawable(R.drawable.bubble_right_green));
                }
                layoutParams.gravity = Gravity.RIGHT;
            }else{
                if (sdk >= Build.VERSION_CODES.JELLY_BEAN) {
                    nameView.setBackground(getDrawable(R.drawable.bubble_left_gray));
                } else{
                    nameView.setBackgroundDrawable(getDrawable(R.drawable.bubble_left_gray));
                }
                layoutParams.gravity = Gravity.LEFT;
            }

            nameView.setLayoutParams(layoutParams);
            return convertView;
        }
    }
}
