package com.example.endproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BugsAdminActivity extends AppCompatActivity {
//Initialize parameters
    private ApiService apiService;
    private ListView bugListView;
    private ArrayAdapter<String> adapter;
    private List<String> bugList = new ArrayList<>();
    private List<Bug> bugs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bugs_admin);

        // Set up the window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize ListView and Adapter
        bugListView = findViewById(R.id.bugsListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bugList);
        bugListView.setAdapter(adapter);

        // Initialize Retrofit API Service
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Fetch unfixed bugs
        fetchUnfixedBugs();

        setupImageButtons();

        // Handle item clicks to show popup dialog
        bugListView.setOnItemClickListener((parent, view, position, id) -> showBugPopup(position));
    }

    //Nav - bar

    private void setupImageButtons() {
        ImageButton home = findViewById(R.id.homeButton);
        home.setOnClickListener(v -> startActivity(new Intent(BugsAdminActivity.this, HomeActivity.class)));

        ImageButton search = findViewById(R.id.searchButton);
        search.setOnClickListener(v -> startActivity(new Intent(BugsAdminActivity.this, SearchActivity.class)));

        ImageButton favorite = findViewById(R.id.favoriteButton);
        favorite.setOnClickListener(v -> startActivity(new Intent(BugsAdminActivity.this, FavoriteActivity.class)));

        ImageButton profile = findViewById(R.id.profileButton);
        profile.setOnClickListener(v -> startActivity(new Intent(BugsAdminActivity.this, ProfileActivity.class)));

        ImageButton admin = findViewById(R.id.adminButton);

        String userName = User.getInstance().getUsername();
        admin.setOnClickListener(v -> startActivity(new Intent(BugsAdminActivity.this, AdminActivity.class)));
        if (userName.equals("admin")) {
            admin.setVisibility(View.VISIBLE);
        }
    }
//pop up for the admin to handle the bugs list
    private void showFixBugDialog(Bug bug) {
        new AlertDialog.Builder(this)
                .setTitle("Fix Bug")
                .setMessage("Do you want to mark this bug as fixed?")
                .setPositiveButton("Fixed", (dialog, which) -> {
                    markBugAsFixed(bug);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
//populate the current bugs list
    private void fetchUnfixedBugs() {
        Call<List<Bug>> call = apiService.getUnfixedBugs();
        call.enqueue(new Callback<List<Bug>>() {
            @Override
            public void onResponse(Call<List<Bug>> call, Response<List<Bug>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Bug> bugs = response.body();
                    bugList.clear();
                    for (Bug bug : bugs) {
                        String bugInfo = "User: " + bug.getUserName() + "\nDescription: " + bug.getDescription() +
                                "\nDate: " + bug.getDate();
                        bugList.add(bugInfo);
                    }
                    adapter.notifyDataSetChanged();

                    bugListView.setOnItemClickListener((parent, view, position, id) -> {
                        Bug selectedBug = bugs.get(position);
                        showFixBugDialog(selectedBug);
                    });
                } else {
                    Toast.makeText(BugsAdminActivity.this, "No bugs found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Bug>> call, Throwable t) {
                Toast.makeText(BugsAdminActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//Display dialog window of the bug
    private void showBugPopup(int position) {
        Bug selectedBug = bugs.get(position);

        // Create a custom dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.bug_popup_dialog);

        // Set the bug details in the dialog
        TextView bugDetailsText = dialog.findViewById(R.id.bugDetailsText);
        bugDetailsText.setText("User Name: " + selectedBug.getUserName() + "\nDescription: " + selectedBug.getDescription() +
                "\nDate: " + selectedBug.getDate());

        // Handle the "Fixed" button click
        Button markFixedButton = dialog.findViewById(R.id.markFixedButton);
        markFixedButton.setOnClickListener(v -> {
            markBugAsFixed(selectedBug);
            dialog.dismiss();
        });

        // Handle the "Cancel" button click
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
//Function to mark bug as fixed
    private void markBugAsFixed(Bug bug) {
        Call<Void> call = apiService.markBugAsFixed(bug);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BugsAdminActivity.this, "Bug marked as fixed", Toast.LENGTH_SHORT).show();
                    fetchUnfixedBugs(); // Refresh the list
                } else {
                    Toast.makeText(BugsAdminActivity.this, "Failed to mark as fixed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(BugsAdminActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
