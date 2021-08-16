package com.chatapp.whatsapp.Activitis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chatapp.whatsapp.Adapter.MessageAdapter;
import com.chatapp.whatsapp.Models.Message;
import com.chatapp.whatsapp.R;
import com.chatapp.whatsapp.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    MessageAdapter adapter;
    ArrayList<Message> messages;

    String senderRoom, receiverRoom;

    FirebaseDatabase database;
    FirebaseStorage storage;

    String receiverUid;
    String senderUid;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        messages = new ArrayList<>();


        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        String name = getIntent().getStringExtra("name");
        String profile = getIntent().getStringExtra("image");

        binding.editName.setText(name);
        Glide.with(ChatActivity.this).load(profile)
                    .placeholder(R.drawable.avatar)
                    .into(binding.profileImage);
        //кнопка стрелка назад
        binding.imageView2.setOnClickListener(v -> finish());

        receiverUid = getIntent().getStringExtra("uid");
        senderUid = FirebaseAuth.getInstance().getUid();

        database.getReference().child("presence").child(receiverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    assert status != null;
                    if (!status.isEmpty()) {
                        if (!status.equals("Offline")) {
                            binding.status.setText(status);
                        }
                        binding.status.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        adapter = new MessageAdapter(this, messages, senderRoom, receiverRoom);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);


        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot1: snapshot.getChildren()) {
                            Message message = snapshot1.getValue(Message.class);
                            assert message != null;
                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //отправка сообщения
        binding.sendBtn.setOnClickListener(v -> {
            String messageTxt = binding.messageEditText.getText().toString();
            Date date = new Date();
            Message message = new Message(messageTxt, senderUid, date.getTime());
            binding.messageEditText.setText("");

            String randomKey = database.getReference().push().getKey();

            HashMap<String, Object> lastMsgObjc = new HashMap<>();
            lastMsgObjc.put("lastMsg", message.getMessage());
            lastMsgObjc.put("lastMsgTime", date.getTime());

            database.getReference().child("chats").child(senderRoom)
                    .updateChildren(lastMsgObjc);

            database.getReference().child("chats").child(receiverRoom)
                    .updateChildren(lastMsgObjc);

            assert randomKey != null;
            database.getReference().child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(randomKey)
                    .setValue(message).addOnSuccessListener(unused -> {
                Task<Void> voidTask = database.getReference().child("chats")
                        .child(receiverRoom)
                        .child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener(unused1 -> {

                        });

                HashMap<String, Object> lastMsgObjc1 = new HashMap<>();
                        lastMsgObjc1.put("lastMsg", message.getMessage());
                        lastMsgObjc1.put("lastMsgTime", date.getTime());

                        database.getReference().child("chats").child(senderRoom)
                                .updateChildren(lastMsgObjc1);

                        database.getReference().child("chats").child(receiverRoom)
                                .updateChildren(lastMsgObjc1);


                    });
        });
        //Открывает с иконкой скрепка галерея и отправка собщением фото\видео
        binding.attachment.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 25);
        });

        final Handler handler = new Handler();
        binding.messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                database.getReference().child("presence").child(senderUid).setValue("typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedTyping, 1000);
            }

            final Runnable userStoppedTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderUid).setValue("Online");
                }
            };
        });

            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
//        binding.editName.setText(name);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 25) {
            if (data != null) {
                if (data.getData() != null) {
                    Uri selectedImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference()
                            .child("chats")
                            .child(calendar.getTimeInMillis() + "");

                    reference.putFile(selectedImage).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                String filePath = uri.toString();

                                String messageTxt = binding.messageEditText.getText().toString();
                                Date date = new Date();
                                Message message = new Message(messageTxt, senderUid, date.getTime());
                                message.setMessage("photo");
                                message.setImageUrl(filePath);
                                binding.messageEditText.setText("");

                                String randomKey = database.getReference().push().getKey();

                                HashMap<String, Object> lastMsgObjc = new HashMap<>();
                                lastMsgObjc.put("lastMsg", message.getMessage());
                                lastMsgObjc.put("lastMsgTime", date.getTime());

                                database.getReference().child("chats").child(senderRoom)
                                        .updateChildren(lastMsgObjc);

                                database.getReference().child("chats").child(receiverRoom)
                                        .updateChildren(lastMsgObjc);

                                assert randomKey != null;
                                database.getReference().child("chats")
                                        .child(senderRoom)
                                        .child("messages")
                                        .child(randomKey)
                                        .setValue(message).addOnSuccessListener(unused -> {
                                            database.getReference().child("chats")
                                                    .child(receiverRoom)
                                                    .child("messages")
                                                    .child(randomKey)
                                                    .setValue(message).addOnSuccessListener(unused1 -> {

                                                    });

                                            HashMap<String, Object> lastMsgObjc1 = new HashMap<>();
                                            lastMsgObjc1.put("lastMsg", message.getMessage());
                                            lastMsgObjc1.put("lastMsgTime", date.getTime());

                                            database.getReference().child("chats").child(senderRoom)
                                                    .updateChildren(lastMsgObjc1);

                                            database.getReference().child("chats").child(receiverRoom)
                                                    .updateChildren(lastMsgObjc1);


                                        });
                            });
                        }
                    });
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    //Показывает кто онлайн оффлайн
    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        assert currentId != null;
        database.getReference().child("presence").child(currentId).setValue("Online");

    }
    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        assert currentId != null;
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }

//    @SuppressLint("NonConstantResourceId")
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.groups:
//                break;
//            case R.id.settings:
//                Toast.makeText(this, "Setting Clicked", Toast.LENGTH_SHORT).show();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}