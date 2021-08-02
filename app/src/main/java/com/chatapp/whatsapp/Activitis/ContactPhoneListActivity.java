package com.chatapp.whatsapp.Activitis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.chatapp.whatsapp.databinding.ActivityContactPhoneListBinding;
import com.chatapp.whatsapp.databinding.ActivityMainBinding;

public class ContactPhoneListActivity extends AppCompatActivity {
    ActivityContactPhoneListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactPhoneListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        String fName = intent.getStringExtra("phoneContact");
        Log.d("TAG", "Contact id=" + fName);
    }
}