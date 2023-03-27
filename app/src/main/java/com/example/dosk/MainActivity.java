package com.example.dosk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dosk.adapter.DataSender;
import com.example.dosk.adapter.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 5.50
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private NavigationView nav_view;
    private FirebaseAuth mAuth;
    private TextView userEmail;

    private AlertDialog dialog;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private PostAdapter.OnItemClickCustom onItemClickCustom;
    private RecyclerView rcView;
    private PostAdapter postAdapter;
    private DataSender dataSender;
    private DBManager dbManager;
    public static String MAUTH = "";
    private String current_cat = "машины";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MyLog", "On create");
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MyLog", "On resume");
        if(current_cat.equals("my_ads")){
            dbManager.getMyDataFromDB(mAuth.getUid());
        }
        else {
            dbManager.getDataFromDB(current_cat);
        }
    }

    private void init()
    {
        setOnItemClickCustom();
        rcView = findViewById(R.id.rcView);
        rcView.setLayoutManager(new LinearLayoutManager(this));
        List<NewPost> arrayPost = new ArrayList<>();
        postAdapter = new PostAdapter(arrayPost, this, onItemClickCustom);

        rcView.setAdapter(postAdapter);
        nav_view = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.toggle_open, R.string.toggle_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(this);
        userEmail = nav_view.getHeaderView(0).findViewById(R.id.tvEmail);
        mAuth = FirebaseAuth.getInstance();

        getDataDB();
        dbManager = new DBManager(dataSender, this);
        dbManager.getDataFromDB("Машины");
        postAdapter.setDbManager(dbManager);

    }
    private void getDataDB()
    {
        dataSender = new DataSender() {
            @Override
            public void onDataReceived(List<NewPost> listData) {
                Collections.reverse(listData);
                postAdapter.updateAdapter(listData);
            }
        };
    }
    private void setOnItemClickCustom(){
        onItemClickCustom = new PostAdapter.OnItemClickCustom() {
            @Override
            public void onItemSelected(int position) {
                Log.d("MyLog", "Position: "+position);

            }
        };
    }
    protected void onStart(){
        super.onStart();
        getUserData();
    }
    public void onClickEdit(View view){
        Intent i = new Intent(MainActivity.this, EditActivity.class);
        startActivity(i);
    }
    private void getUserData(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            userEmail.setText(currentUser.getEmail());
            MAUTH = mAuth.getUid();
        }
        else{
            userEmail.setText(R.string.signinorout);
            MAUTH = "";
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.id_my_ads:
                current_cat = "my_ads";
                Toast.makeText(this, "Pressed your ads", Toast.LENGTH_SHORT).show();
                dbManager.getMyDataFromDB(mAuth.getUid());
                break;
            case R.id.id_cars_ads:
                current_cat = "Машины";
                Toast.makeText(this, "Pressed cars ads", Toast.LENGTH_SHORT).show();
                dbManager.getDataFromDB("Машины");
                break;
            case R.id.id_pc_ads:
                current_cat = "Компьютеры";
                Toast.makeText(this, "Pressed pc ads", Toast.LENGTH_SHORT).show();
                dbManager.getDataFromDB("Компьютеры");
                break;
            case R.id.id_smartphone_ads:
                current_cat = "Смартфоны";
                Toast.makeText(this, "Pressed smartphone ads", Toast.LENGTH_SHORT).show();
                dbManager.getDataFromDB("Смартфоны");
                break;
            case R.id.id_dm_ads:
                current_cat = "Бытовая техника";
                Toast.makeText(this, "Pressed dm ads", Toast.LENGTH_SHORT).show();
                dbManager.getDataFromDB("Бытовая техника");
                break;
            case R.id.id_sign_in:
                signUpDialog(R.string.sign_in, R.string.sign_in_button,1);
                Toast.makeText(this, "Pressed sign in", Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_sign_out:
                signOut();
                Toast.makeText(this, "Pressed sign out", Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_sign_up:
                signUpDialog(R.string.sign_up, R.string.sign_up_button, 0);
                Toast.makeText(this, "Pressed sign up", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
    private void signUpDialog(int title, int buttonTitle, int index)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sign_up_layout, null);
        dialogBuilder.setView(dialogView);
        TextView titleTextView = dialogView.findViewById(R.id.tvAlertTitle);
        titleTextView.setText(title);
        Button b = dialogView.findViewById(R.id.button_signup);
        EditText edEmail = dialogView.findViewById(R.id.edit_mail);
        EditText edPassword = dialogView.findViewById(R.id.edit_password);
        b.setText(buttonTitle);
        b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (index==0){
                    signUp(edEmail.getText().toString(), edPassword.getText().toString());
                }
                else{
                    signIn(edEmail.getText().toString(), edPassword.getText().toString());
                }
                dialog.dismiss();
            }
        });
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void signUp(String email, String password){
        if (!email.equals("") && !password.equals("")) {
        mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task ->  {
                            if (task.isSuccessful()) {

                                getUserData();
                            }
                            else {
                                Log.w("MyLogMainActivity", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();

                    });
        }
        else {
            Toast.makeText(getApplicationContext(), "Заполните все поля.", Toast.LENGTH_SHORT).show();
        }
    }
    private void signIn(String email, String password) {
        if (!email.equals("") && !password.equals("")) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            getUserData();
                        } else {
                            Log.w("MyLogMainActivity", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    });
        } else {
            Toast.makeText(getApplicationContext(), "Заполните все поля.", Toast.LENGTH_SHORT).show();
        }
    }
    private void signOut(){
        mAuth.signOut();
        getUserData();
    }
}