package edu.bluejack182.defilm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText txtEmail;
    EditText txtName;
    EditText txtPassword;
    EditText txtConfPassword;
    TextView txtError;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        txtName = findViewById(R.id.edt_name);
        txtEmail = findViewById(R.id.edt_email);
        txtPassword = findViewById(R.id.edt_password);
        txtConfPassword = findViewById(R.id.edt_repassword);
        txtError = findViewById(R.id.txt_error);

        final Button btn = findViewById(R.id.btn_register);
        btn.setOnClickListener(this);

    }

    public void redirectLogin(android.view.View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                addUser();
                break;
        }
    }

    private void addUser() {

        String username = txtName.getText().toString();
        String email = txtEmail.getText().toString();

        isUsernameExists(username,email, this);
    }


    public void isUsernameExists(final String enteredUsername, final String enteredEmail, final Context context) {
        Query query =
                FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").equalTo(enteredUsername);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    txtError.setText("Username must be unique");
                else{
                    txtError.setText("");
                    isEmailExists(enteredUsername, enteredEmail, context);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void isEmailExists(final String enteredUsername, final String enteredEmail, final Context context) {

        Query query =
                FirebaseDatabase.getInstance().getReference("Users").orderByChild("email").equalTo(enteredEmail);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    txtError.setText("Email must be unique");
                else{
                    txtError.setText("");
                    String password = txtPassword.getText().toString();
                    String confPassword = txtConfPassword.getText().toString();
                    String id = databaseReference.push().getKey();

                    if(password.matches("[a-zA-Z0-9]+")){
                        if(password.length() > 8){
                            if (password.equals(confPassword)) {
                                txtError.setText("");
                                User user = new User(id, enteredUsername, enteredEmail, password);
                                databaseReference.child("Users").push().setValue(user);

                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                txtError.setText("Password and Confrim password must be the same");
                            }
                        }
                        else{
                            txtError.setText("Password length must be more than 8 characters");
                        }
                    }else{
                        txtError.setText("Password must be alphanumeric");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
