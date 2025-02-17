package com.example.numad25sp_juranhuang;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Adapter for displaying contacts in a RecyclerView
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {
    private List<Contact> contacts;
    private ContactsActivity activity;

    public ContactsAdapter(List<Contact> contacts, ContactsActivity activity) {
        this.contacts = contacts;
        this.activity = activity;
    }

    // ViewHolder class to hold UI elements for each contact time
    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, phoneTextView;
        Button editButton;
        public ContactViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contact_name);
            phoneTextView = itemView.findViewById(R.id.contact_phone);
            editButton = itemView.findViewById(R.id.btn_edit);
        }
    }

    // Inflates the contact item layout and creates a ViewHolder
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    // Binds contact data to UI elements in each list item
    @Override
    public void onBindViewHolder (@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.nameTextView.setText(contact.getName());
        holder.phoneTextView.setText(contact.getPhoneNumber());

        // Handle click event to start phone call
        holder.itemView.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel: " + contact.getPhoneNumber()));
            v.getContext().startActivity(callIntent);
        });

        // Long-press to delete
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Contact")
                    .setMessage("Are you sure you want to delete " + contact.getName() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        contacts.remove(position);  // Remove from list
                        notifyItemRemoved(position); // Notify RecyclerView
                        activity.saveContacts();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });
        // "Click" Edit button to modify contact details
        holder.editButton.setOnClickListener(v -> showEditDialog(v, position));
    }

    // Return the total size of contacts
    @Override
    public int getItemCount() {
        return contacts.size();
    }

    // Displays a dialog to edit contact name and phone number
    private void showEditDialog(View view, int position) {
        Contact contact = contacts.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Edit Contact");

        // Input fields for name and phone
        LinearLayout layout = new LinearLayout(view.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText nameInput = new EditText(view.getContext());
        nameInput.setText(contact.getName());
        layout.addView(nameInput);

        EditText phoneInput = new EditText(view.getContext());
        phoneInput.setText(contact.getPhoneNumber());
        phoneInput.setInputType(InputType.TYPE_CLASS_PHONE);
        layout.addView(phoneInput);

        builder.setView(layout);

        // Set changes
        builder.setPositiveButton("Save", (dialog, which) -> {
            contact.setName(nameInput.getText().toString().trim());
            contact.setPhoneNumber(phoneInput.getText().toString().trim());
            notifyItemChanged(position); // Update RecyclerView
            Toast.makeText(view.getContext(), "Contact Updated", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
