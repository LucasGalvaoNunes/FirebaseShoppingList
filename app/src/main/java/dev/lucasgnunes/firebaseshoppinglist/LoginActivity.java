package dev.lucasgnunes.firebaseshoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog progress;


    private EditText edit_email, edit_password;
    private Button button_login;
    private TextView text_create_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //region Bind input from view
        edit_email = findViewById(R.id.edit_email);
        edit_password = findViewById(R.id.edit_password);
        button_login = findViewById(R.id.button_login);
        text_create_account = findViewById(R.id.text_create_account);
        //endregion

        //Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        // Get User Logged, and make verify if is logged to send MainActivity.class
        FirebaseUser userLogged = mAuth.getCurrentUser();
        goToMain(userLogged);


        //region Setup Clicks Listeners
        text_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });


        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edit_email.getText().toString();
                String password = edit_password.getText().toString();

                //region Validate input and show error.
                if(email.isEmpty()){
                    edit_email.setError(getString(R.string.error_email_required));
                    return;
                }
                if(password.isEmpty()){
                    edit_password.setError(getString(R.string.error_password_required));
                    return;
                }
                //endregion

                //region Show ProgressDialog for loading.
                progress = new ProgressDialog(LoginActivity.this);
                progress.setMessage(getString(R.string.make_login));
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
                //endregion

                //region Make call to login callback for firebase auth.
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    goToMain(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                progress.dismiss();
                            }
                        });
                //endregion
            }
        });
        //endregion
    }


    public void goToMain(FirebaseUser user){
        if(user != null){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}
