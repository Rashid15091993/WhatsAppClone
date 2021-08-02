package com.chatapp.whatsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chatapp.whatsapp.Activitis.ChatActivity;
import com.chatapp.whatsapp.Models.Contact;
import com.chatapp.whatsapp.Models.User;
import com.chatapp.whatsapp.R;
import com.chatapp.whatsapp.databinding.RowContactphoneBinding;
import com.chatapp.whatsapp.databinding.RowConversationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ContactPhoneAdapter extends  RecyclerView.Adapter<ContactPhoneAdapter.ContactViewHolder>{

    Context context;
    ArrayList<Contact> contacts;
    Contact contactphone;

    public ContactPhoneAdapter(Context context, ArrayList<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;

    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_contactphone, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactPhoneAdapter.ContactViewHolder holder, int position) {
        Contact contactphone = contacts.get(position);


        Cursor cursor=context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if(cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                contactphone = new Contact();

                String id = cursor.getString(
                        cursor.getColumnIndex(
                                ContactsContract.Contacts._ID));
                contactphone.setName(id);

                String name = cursor.getString(
                        cursor.getColumnIndex(
                                ContactsContract.Contacts
                                        .DISPLAY_NAME));
                contactphone.setName(name);

                String has_phone = cursor.getString(
                        cursor.getColumnIndex(
                                ContactsContract.Contacts
                                        .HAS_PHONE_NUMBER));
                if (Integer.parseInt(has_phone) > 0) {
                    // extract phone number
                    Cursor pCur;
                    pCur = context.getContentResolver().query(
                            ContactsContract.CommonDataKinds
                                    .Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds
                                    .Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    while(pCur.moveToNext()) {
                        String phone = pCur.getString(
                                pCur.getColumnIndex(
                                        ContactsContract.
                                                CommonDataKinds.
                                                Phone.NUMBER));
                        contactphone.setPhone(phone);
                    }
                    pCur.close();
                }
                Log.d("TaG", "OKEY");

            }
        }

       // String senderId = FirebaseAuth.getInstance().getUid();

//        String senderRoom = senderId + contact.getUid();
//
//        FirebaseDatabase.getInstance().getReference()
//                .child("chats")
//                .child(senderRoom)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
//                            long time = snapshot.child("lastMsgTime").getValue(Long.class);
//
//                            holder.binding.lastMsgTextView.setText(lastMsg);
//                        } else {
//                            holder.binding.lastMsgTextView.setText("Tab to chat");
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//                    }
//                });
//
        holder.binding.nameNumericTextView.setText(contactphone.getName());
        holder.binding.phoneContactTextView.setText(contactphone.getPhone());
//
//        Glide.with(context).load(user.getProfileImage())
//                .placeholder(R.drawable.avatar)
//                .into(holder.binding.profile);


    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        RowContactphoneBinding binding;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowContactphoneBinding.bind(itemView);
        }
    }

    //функциия определения контактов телефона
}
