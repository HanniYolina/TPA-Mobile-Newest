package edu.bluejack182.defilm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.UUID;

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
    private static final String keyName = "username";
    public boolean isLoggedIn;

    CallbackManager callbackManager;
    AccessToken accessToken;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    private DatabaseReference databaseReference;
    Context context = this;

    LoginButton fbButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

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

        callbackManager = CallbackManager.Factory.create();

        accessToken = AccessToken.getCurrentAccessToken();
        isLoggedIn = accessToken!=null && !accessToken.isExpired();

        if(isLoggedIn){
            Log.d("login facebook", "onCreate: udh sukses login");
        } else {
            Log.d("login fb", "onCreate: ini ga masuk isloggedin tp success");
        }

        fbButton = findViewById(R.id.login_button);
        fbButton.setOnClickListener(this);


        fbButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("Login Activity",response.toString());
                                        try {
                                            String email = object.getString("email");
                                            String name = object.getString("name");

                                            inputUser(email, name);
                                            Toast.makeText(LoginActivity.this,email,Toast.LENGTH_SHORT).show();
//                                            Toast.makeText(LoginActivity.this,name,Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                        );
                        Bundle parameters = new Bundle();
                        parameters.putString("fields","id,name,email,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
    }

    public void redirectRegister(android.view.View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void inputUser(final String email, final String username){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query query =  databaseReference.orderByChild("email");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean check = false;
                for (DataSnapshot sp : dataSnapshot.getChildren()){
                    User user = sp.getValue(User.class);

                    if(user.getEmail().equals(email)){
                        editor.putString(keyUser, sp.getKey() + "");
                        editor.putString(keyName, user.getUsername());
                        editor.apply();

//                        Toast.makeText(LoginActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();
                        check = true;
                    }
                }

                Toast.makeText(context, Boolean.toString(check), Toast.LENGTH_SHORT).show();
                if(!check){
                    String id = databaseReference.push().getKey();
                    databaseReference.child(id).child("email").setValue(email);
                    databaseReference.child(id).child("username").setValue(username);
                    databaseReference.child(id).child("password").setValue(UUID.randomUUID().toString());
                }

                Intent intent = new Intent(context, HomeActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,9001);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                login(this);
                break;
            case R.id.sign_in_button:
                signIn();
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
//                        Toast.makeText(context, user.getEmail(), Toast.LENGTH_SHORT).show();
                        editor.putString(keyUser, sp.getKey() + "");
                        editor.putString(keyName, user.getUsername());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);

        if(requestCode == 9001){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            inputUser(account.getEmail(), account.getDisplayName());

            Toast.makeText(this,"Email: " + account.getEmail(),Toast.LENGTH_LONG).show();
//            Toast.makeText(this, account.getDisplayName(), Toast.LENGTH_SHORT).show();
        } catch (ApiException e) {
            e.printStackTrace();
            Log.v("Google Activity","signInResult : failed code = " + e.getStatusCode());
        }
    }

}
