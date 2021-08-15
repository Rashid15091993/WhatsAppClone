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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ContactPhoneAdapter extends  RecyclerView.Adapter<ContactPhoneAdapter.ContactViewHolder>{

    Context context;
    ArrayList<Contact> contacts;
    Contact contactphone;
    FirebaseDatabase database;
    String receiverUid;

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

        database = FirebaseDatabase.getInstance();

        holder.binding.nameNumericTextView.setText(contactphone.getName());
        holder.binding.phoneContactTextView.setText(contactphone.getPhone());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", contactphone.getName());
                intent.putExtra("image", contactphone.getProfileImage());
                intent.putExtra("uid", contactphone.getUid());
                Log.d("TEST", contactphone.getUid());
                context.startActivity(intent);

                database.getReference()
                        .child("chats_my_list")
                        .child(contactphone.getUid())
                        .setValue(contactphone.getPhone())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
            }
        });


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
