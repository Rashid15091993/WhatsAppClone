package com.chatapp.whatsapp.Activitis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import com.chatapp.whatsapp.Adapter.ContactPhoneAdapter;
import com.chatapp.whatsapp.Adapter.UsersAdapter;
import com.chatapp.whatsapp.Models.Contact;
import com.chatapp.whatsapp.Models.User;
import com.chatapp.whatsapp.databinding.ActivityContactPhoneListBinding;
import com.chatapp.whatsapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class ContactPhoneListActivity extends AppCompatActivity {
    ActivityContactPhoneListBinding binding;
    ArrayList<Contact> contact;
    ContactPhoneAdapter contactAdapter;
    FirebaseDatabase database;
    Contact contactModule;
    List<String> listElementPhone;
    ArrayList<User> users;
    Hashtable<String, String> dictName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactPhoneListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        contact = new ArrayList<>();
        List<String> listElementPhone = new ArrayList<>();
        Hashtable<String, String> dictName = new Hashtable<String, String>();

        contactAdapter = new ContactPhoneAdapter(this, contact);
        binding.recycleViewContact.setAdapter(contactAdapter);
        Contact contacts = new Contact();

        database = FirebaseDatabase.getInstance();
        //Определения номера телефона и имени  контактов телефона
        Cursor cursor = this.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                contactModule = new Contact();

                String id = cursor.getString(
                        cursor.getColumnIndex(
                                ContactsContract.Contacts._ID));
                contactModule.setName(id);

                String name = cursor.getString(
                        cursor.getColumnIndex(
                                ContactsContract.Contacts
                                        .DISPLAY_NAME));
                contactModule.setName(name);

                String has_phone = cursor.getString(
                        cursor.getColumnIndex(
                                ContactsContract.Contacts
                                        .HAS_PHONE_NUMBER));
                if (Integer.parseInt(has_phone) > 0) {
                    // extract phone number
                    Cursor pCur;
                    pCur = this.getContentResolver().query(
                            ContactsContract.CommonDataKinds
                                    .Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds
                                    .Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(
                                pCur.getColumnIndex(
                                        ContactsContract.
                                                CommonDataKinds.
                                                Phone.NUMBER));
                        contactModule.setPhone(phone);
                    }
                    pCur.close();
                }


                //Добавляем в словарь имя и номер
                dictName.put(contactModule.getPhone(), contactModule.getName());
                //Добавляем в список все номера которые есть на телефоне
                listElementPhone.add(contactModule.getPhone());
            }

            readContactPhone(listElementPhone, dictName);

        }
    }

    private void readContactPhone(List<String> listElementPhone, Hashtable<String, String> dictName) {
        //Поиск вывод в приложения тех контактов которые зарегистрированы в приложении
        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Contact user = new Contact();

                    String phoneDataBase = snapshot1.child("phoneNumber").getValue().toString();

                    for (String elementPhone : listElementPhone) {
                        String replaceContact1 = elementPhone.replace("-", "");
                        String replaceContact12 = replaceContact1.replace(" ", "");

                        if (phoneDataBase.equals(replaceContact12)) {
                            user.setPhone(replaceContact12);
                            user.setName(String.valueOf(dictName.get(elementPhone)));

                            contact.add(user);
                        } else {
                            continue;
                        }
                    }
                }
                contactAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}