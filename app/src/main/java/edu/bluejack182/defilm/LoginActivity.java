package edu.bluejack182.defilm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener {

    EditText txtEmail;
    EditText txtPassword;
    CheckBox chkRemember;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private static final String prefName = "prefs";
    private static final String keyRemember = "remember";
    private static final String keyEmail = "email";
    private static final String keyPass = "password";
    private static final String keyUser = "user";


    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button btn = findViewById(R.id.btn_login);
        btn.setOnClickListener(this);

        txtEmail = findViewById(R.id.edt_email);
        txtPassword = findViewById(R.id.edt_password);
        chkRemember = findViewById(R.id.chk_remember);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences.getBoolean(keyRemember, false)){
            chkRemember.setChecked(true);
        }else chkRemember.setChecked(true);

        txtEmail.setText(sharedPreferences.getString(keyEmail, ""));
        txtPassword.setText(sharedPreferences.getString(keyPass, ""));

        txtEmail.addTextChangedListener(this);
        txtPassword.addTextChangedListener(this);
        chkRemember.setOnCheckedChangeListener(this);
    }

    public void redirectRegister(android.view.View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                login(this);
                break;
        }
    }

    public void login(final Context context){
        String enteredEmail = txtEmail.getText().toString();
        final String enteredPass = txtPassword.getText().toString();

        Query query =  FirebaseDatabase.getInstance().getReference("Users").orderByChild("email").equalTo(enteredEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot sp : dataSnapshot.getChildren()){
                    User user = sp.getValue(User.class);

                    if(user.getPassword().equals(enteredPass)){
                        editor.putString(keyUser, sp.getKey() + "");
                        editor.apply();

                        Intent intent = new Intent(context, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(context, "Wrong username or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        managePrefs();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        managePrefs();
    }

    private void managePrefs(){
        if(chkRemember.isChecked()){
            editor.putString(keyEmail, txtEmail.getText().toString());
            editor.putString(keyPass, txtPassword.getText().toString());
            editor.putBoolean(keyRemember, true);
            editor.apply();
        }else{
            editor.putBoolean(keyRemember, false);
            editor.remove(keyEmail);
            editor.remove(keyPass);
            editor.apply();
        }
    }
}
