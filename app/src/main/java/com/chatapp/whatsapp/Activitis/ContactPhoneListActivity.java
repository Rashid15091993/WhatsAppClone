package com.chatapp.whatsapp.Activitis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.chatapp.whatsapp.Adapter.ContactPhoneAdapter;
import com.chatapp.whatsapp.Adapter.UsersAdapter;
import com.chatapp.whatsapp.Models.Contact;
import com.chatapp.whatsapp.Models.User;
import com.chatapp.whatsapp.databinding.ActivityContactPhoneListBinding;
import com.chatapp.whatsapp.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class ContactPhoneListActivity extends AppCompatActivity {
    ActivityContactPhoneListBinding binding;
    ArrayList<Contact> contact;
    ContactPhoneAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactPhoneListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        contact = new ArrayList<>();

        contactAdapter = new ContactPhoneAdapter(this, contact);
        Intent intent = getIntent();
        String phone = intent.getStringExtra("phoneContact");
        binding.recycleViewContact.setAdapter(contactAdapter);
        Contact contacts = new Contact();
        contact.add(contacts);
        Log.d("Phone", "Phone " + phone);

    }
}