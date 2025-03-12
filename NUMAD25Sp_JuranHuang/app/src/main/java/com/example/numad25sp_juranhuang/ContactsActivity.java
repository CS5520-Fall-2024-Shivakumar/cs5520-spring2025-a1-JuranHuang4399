package com.example.numad25sp_juranhuang;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ContactsAdapter adapter;
    private List<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadContacts(); // Load saved contacts
        adapter = new ContactsAdapter(contactList, this);
        recyclerView.setAdapter(adapter);

        // FAB button
        FloatingActionButton fab = findViewById(R.id.fab_add_contact);
        fab.setOnClickListener(view -> showAddContactDialog());
    }

    private void showAddContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Contact");

        // Set up a layout to hold text fields
        LinearLayout layout = new LinearLayout(this);
        // Stack vertically
        layout.setOrientation(LinearLayout.VERTICAL);

        // Create name text field and add to the layout
        EditText nameInput = new EditText(this);
        nameInput.setHint("Name");
        layout.addView(nameInput);

        // Create phone number text field and add to the layout
        EditText phoneInput = new EditText(this);
        phoneInput.setHint("Phone Number");
        // Set input style to phone number specific
        phoneInput.setInputType(InputType.TYPE_CLASS_PHONE);
        layout.addView(phoneInput);

        builder.setView(layout);

        // Add & Cancel Buttons
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            if (!name.isEmpty() && !phone.isEmpty()) {
                contactList.add(new Contact(name, phone));
                // notify Recycler View about the newly added contact
                // notifyItemInserted(position) is built in method from RecyclerView.Adapter
                adapter.notifyItemInserted(contactList.size() - 1);
                saveContacts();
                Snackbar.make(recyclerView, "Contact Added", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> {
                            contactList.remove(contactList.size() - 1);
                            // notifyDataSetChanged is built in method from RecyclerView.Adapter
                            adapter.notifyDataSetChanged();
                            Toast.makeText(this, "Contact Removed", Toast.LENGTH_SHORT).show();
                        }).show();
            } else {
                Snackbar.make(recyclerView, "Please enter both name and phone number", Snackbar.LENGTH_SHORT).show();
            }
        });
        // Button to cancel
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Saves the contacts in SharedPreference
    protected void saveContacts() {
        SharedPreferences prefs = getSharedPreferences("ContactsCollectorList", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        // convert contactList into a JSON string to store in SharedPreferences
        String json = gson.toJson(contactList);

        editor.putString("contacts", json);
        editor.apply(); // Save data
    }

    // Loads the contacts in SharedPreference
    protected void loadContacts() {
        SharedPreferences prefs = getSharedPreferences("ContactsCollectorList", MODE_PRIVATE);
        String json = prefs.getString("contacts", null);

        if (json != null) {
            Gson gson = new Gson();
            // declare the type we will be using
            Type type = new TypeToken<ArrayList<Contact>>() {}.getType();
            // convert JSON back to ArrayList of contacts
            contactList = gson.fromJson(json, type);
        } else {
            contactList = new ArrayList<>(); // Start blank if no data has found
        }
    }
}
