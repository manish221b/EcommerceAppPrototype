package com.manishdotdeveloper.amazoncloneapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manishdotdeveloper.amazoncloneapp.Prevalent.Prevalent;
import com.manishdotdeveloper.amazoncloneapp.model.Users;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText InputPhoneNumber, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";
    private TextView AdminLink, NotAdminLink;

    private CheckBox chkboxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_button);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        AdminLink = (TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);
        loadingBar = new ProgressDialog(this);

        chkboxRememberMe = (CheckBox) findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });
    }

    private void LoginUser()
    {
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please write your Phone Number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write your Password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Logging Account");
            loadingBar.setMessage("Please wait, while we check your Credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phone, password);
        }
    }

    private void AllowAccessToAccount(String phone, String password)
    {
        if(chkboxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.UserPhonekey, phone);
            Paper.book().write(Prevalent.UserPasswordkey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(parentDbName).child(phone).exists())
                {
                    Users usersData = snapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if(usersData.getPhone().equals(phone))
                    {
                        if(usersData.getPassword().equals(password))
                        {
                            if(parentDbName.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this, "Welcome Admin, you logged in Successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if(parentDbName.equals("Users"))
                            {
                                Toast.makeText(LoginActivity.this, "logged in Successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Password is Incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Account not found, Please create a new Account", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}