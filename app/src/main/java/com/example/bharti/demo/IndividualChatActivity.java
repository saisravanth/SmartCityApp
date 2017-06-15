package com.example.bharti.demo;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDDocumentCatalog;
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission;
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDAcroForm;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDCheckbox;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDComboBox;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDField;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDFieldTreeNode;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDNonTerminalField;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDTextField;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;


import org.spongycastle.jce.provider.BouncyCastleProvider;



public class IndividualChatActivity extends AppCompatActivity implements View.OnClickListener,
        MessageDataSource.MessagesCallbacks{

    public static final String USER_EXTRA = "USER";
    public static final String TAG = "IndividualChatActivity";

    private ArrayList<Message> mMessages;
    private MessagesAdapter mAdapter;
    private String mRecipient;
    private ListView mListView;
    private Date mLastMessageDate = new Date();
    private String mConvoId;
    private MessageDataSource.MessagesListener mListener;
    private static PDDocument _pdfDocument;
    File root;
    boolean entry;
    AssetManager assetManager;
    Queue<String> formQualifiedNames = new LinkedList<String>();
    Queue<String> formPartialNames = new LinkedList<String>();
    ArrayList<String> availableForms = new ArrayList<String>();
    ArrayList<String> formFields = new ArrayList<String>();
    ArrayList<String> formValues = new ArrayList<String>();

    String mode = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat);

        // Enable Android-style asset loading (highly recommended)
        PDFBoxResourceLoader.init(getApplicationContext());
        // Find the root of the external storage.
        root = android.os.Environment.getExternalStorageDirectory();
        assetManager = getAssets();

        MessageDataSource individualReference = new MessageDataSource();
        individualReference.setsRef("https://smartcitychat.firebaseio.com/usernamelist");
        mode = "none";
        entry=false;
        formValues.clear();
        formFields.clear();
        availableForms.add("REQUEST FOR YOUR OWN DRIVER LICENSE/IDENTIFICATION CARD (DL/ID)");
        if (null == FirebaseAuth.getInstance().getCurrentUser()) {
            smartCityChat context = (smartCityChat)getApplicationContext();
            mRecipient = context.getUser().getFullName();
        } else {
            mRecipient = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }
        Log.v(TAG, "current user name is" + mRecipient);
        mListView = (ListView) findViewById(R.id.messages_list);
        mMessages = new ArrayList<>();
        mAdapter = new MessagesAdapter(mMessages);
        mListView.setAdapter(mAdapter);
        setTitle("Representative");
        mConvoId =  "/chats/" + mRecipient + "-" + "representative";
        mListener = MessageDataSource.addMessagesListener(mConvoId , this);

        Button sendMessage = (Button) findViewById(R.id.send_message);
        sendMessage.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu, menu);
        return true;
    }

    public void onClick(View v) {
        if(v.getId() == R.id.send_message) {
            entry=true;
            String senderName = "";
            if (null == FirebaseAuth.getInstance().getCurrentUser()) {
                smartCityChat context = (smartCityChat) getApplicationContext();
                senderName = context.getUser().getFullName();
            } else {
                senderName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            }
            EditText newMessageView = (EditText) findViewById(R.id.new_message);
            String newMessage = newMessageView.getText().toString();
            if (mode.equals("form")) {
                formValues.add(newMessage);
            }
            newMessageView.setText("");
            Message msg = new Message();
            msg.setDate(new Date());
            msg.setText(senderName + System.getProperty("line.separator") + newMessage);
            msg.setSender(senderName);
            MessageDataSource.saveMessage(msg, mConvoId);
        }
    }

    @Override
    public void onMessageAdded(Message message) {
        mMessages.add(message);
        mAdapter.notifyDataSetChanged();
        if(!(message.getSender().equals("Your assistant")) && entry) {
            Log.v(TAG, "I got a message, waiting for process");
            processMessage(message.getText());
        }
    }

    private void processMessage(String message) {
        if(message.contains("assistant"))
                return;

        Message msg = new Message();
        msg.setDate(new Date());
        if(message.toLowerCase().contains("dashboard") || message.toLowerCase().contains("statistics")) {
            msg.setText("Check our live San Jose Dashboard");
            Intent intent1 = new Intent(this, DashboardActivity.class);
            intent1.putExtra("emailId", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            intent1.putExtra("from", "IndividualChat");
            startActivity(intent1);
        }
        else if(message.toLowerCase().contains("issue")) {
            msg.setText("Here you go!");
            Log.v(TAG, "Create an Issue \n");
            Intent intent1 = new Intent(this, IssueActivity.class);
            intent1.putExtra("emailId", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            intent1.putExtra("from", "IndividualChat");
            startActivity(intent1);
        } else if (message.contains("form") || (!formQualifiedNames.isEmpty())) {
            String formMessage = "List of forms available with us are 1.Request for DL/ID \n " +
                                " 2. Bill of Sale";
            if (formQualifiedNames.isEmpty()) {
                msg.setText("Your assistant" + System.getProperty("line.separator") + formMessage);
                formQualifiedNames.add(formMessage);
            } else if(formQualifiedNames.peek().equals(formMessage)) {
                //addToQueue(Integer.parseInt(message));
                if(formQualifiedNames.size() == 1)
                {
                    formQualifiedNames.poll();
                }
                addToQueue(1);
                msg.setText("You have entered 1 \n please confirm with Yes if it is correct ");
            } else
            {
                    mode = "form";
                    String question = formPartialNames.peek().replaceAll("[0-9]","") + " ?";
                    msg.setText("Your assistant" + System.getProperty("line.separator") + question);
                    Log.v(TAG,question);
                    formQualifiedNames.poll();
                    formPartialNames.poll();
            }
        } else if (message.length() > 0 )
        {
            Log.v(TAG, "Im here and message length is " + message);
            if(mode.equals("form") ) {
                mode = "";
                msg.setText("You have completed filling your form. Form saved in Downloads. How can I assist you now?");
                try {
                    saveForm();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
                msg.setText("Your assistant" + System.getProperty("line.separator") + "How can I assist you?");
        }
        msg.setSender("Your assistant");
        MessageDataSource.saveMessage(msg, mConvoId);
    }

    private void addToQueue(int number) {
        //String documentName = availableForms.get(number);
        int keyLength = 128; // 128 bit is the highest currently supported

        // Limit permissions of those without the password
        AccessPermission ap = new AccessPermission();
        ap.setCanPrint(false);

        // Sets the owner password and user password
        StandardProtectionPolicy spp = new StandardProtectionPolicy("12345", "hi", ap);

        // Setups up the encryption parameters
        spp.setEncryptionKeyLength(keyLength);
        spp.setPermissions(ap);
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);

        PDDocument document = null;
        try {
            //document = PDDocument.load(assetManager.load(documentName));
            String[] f = getAssets().list("");
            for(String f1 : f){
                Log.v("names",f1);
            }
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference pdfRef = storageRef.child("usernamelist/forms/dmv_12.pdf");
            File rootPath = new File(Environment.getExternalStorageDirectory(), "dmv");
            if(!rootPath.exists()) {
                rootPath.mkdirs();
            }
            final File localFile = new File(rootPath,"dmv.pdf");

            pdfRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Log.v(TAG,";local tem file created  created " +localFile.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.v(TAG,";local tem file was not created  created " +localFile.toString());
                }
            });
            Log.v(TAG, localFile.toString());
            document = PDDocument.load(localFile);
//            document = PDDocument.load(getAssets().open("dmv_12.pdf"));
            Log.v(TAG, "loaded document");
            String path = root.getAbsolutePath() + "/Download/dmv_12.pdf";
            PDDocumentCatalog docCatalog = document.getDocumentCatalog();
            PDAcroForm acroForm = docCatalog.getAcroForm();

            for (PDFieldTreeNode field : acroForm.getFields()) {
                if (field.getClass().getSimpleName().equals("PDPushButton")) {
                    Log.v(TAG, "continue for " + field.getClass().getSimpleName());
                    continue;
                }
                Log.v(TAG,field.getClass().getSimpleName());
                formQualifiedNames.add(field.getFullyQualifiedName());
                formFields.add(field.getFullyQualifiedName());
                formPartialNames.add(field.getPartialName());
            }
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveForm( ) throws IOException {
        //String documentName = availableForms.get(number);
        int keyLength = 128; // 128 bit is the highest currently supported

        // Limit permissions of those without the password
        AccessPermission ap = new AccessPermission();
        ap.setCanPrint(false);

        // Sets the owner password and user password
        StandardProtectionPolicy spp = new StandardProtectionPolicy("12345", "hi", ap);

        // Setups up the encryption parameters
        spp.setEncryptionKeyLength(keyLength);
        spp.setPermissions(ap);
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);

        PDDocument document = PDDocument.load(getAssets().open("dmv_12.pdf"));
        Log.v(TAG, "loaded document");
        String path = root.getAbsolutePath() + "/Download/dmv_12.pdf";
        PDDocumentCatalog docCatalog = document.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();

        try {
            for(int i = 0; i < formValues.size();i++) {
                PDFieldTreeNode field = acroForm.getField(formFields.get(i));
                if (field.getFullyQualifiedName().contains("Box")) {
                    Log.v(TAG,"Checkbox for " + field.getFullyQualifiedName());
                }
                field.setValue(formValues.get(i));
            }
            document.protect(spp);
            document.save(path);
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        formPartialNames.clear();
        formQualifiedNames.clear();
        formValues.clear();
        formFields.clear();
        MessageDataSource.stop(mListener);
    }

    private class MessagesAdapter extends ArrayAdapter<Message> {
        MessagesAdapter(ArrayList<Message> messages){
            super(IndividualChatActivity.this, R.layout.message_item, R.id.message, messages);
        }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);

            final Message message = getItem(position);

            TextView nameView = (TextView)convertView.findViewById(R.id.message);
            nameView.setText(message.getText());

            nameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "User clicked on " + message.getSender());
                }
            });
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)nameView.getLayoutParams();

            if (message.getSender().toLowerCase().equals("your assistant")) {
                nameView.setBackground(getDrawable(R.drawable.bubble_left_gray));
                layoutParams.gravity = Gravity.LEFT;
            } else {
                nameView.setBackground(getDrawable(R.drawable.bubble_right_green));
                layoutParams.gravity = Gravity.RIGHT;
            }
            nameView.setLayoutParams(layoutParams);
            return convertView;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent listUsersIntent = new Intent(IndividualChatActivity.this, ListForums.class);
                startActivity(listUsersIntent);
                return true;
            case R.id.issue:
                startActivity(new Intent(this, IssueActivity.class));
                Toast.makeText(this, "issue clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.forms:
                Intent formIntent = new Intent(IndividualChatActivity.this, ListFormsActivity.class);
                startActivity(formIntent);
                Toast.makeText(this, "forms clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

/*
            // Fill the text field
            List<PDFieldTreeNode> fields = acroForm.getFields();
            for (int i = 0;i<fields.size();i++) {
                PDFieldTreeNode field = fields.get(i);
                String new_tag = "some_crap";
               // Log.v(new_tag, "cos object = " + field.getCOSObject());
                Log.v(new_tag, "field = " + field.getFullyQualifiedName());
                Log.v(new_tag, "field type = " + field.getFieldType());
            }
            */