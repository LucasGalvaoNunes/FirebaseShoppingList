package dev.lucasgnunes.firebaseshoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ProgressDialog progress;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private Button button_register;
    private EditText edit_email, edit_password, edit_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //region Bind input from view
        button_register = findViewById(R.id.button_register);
        edit_name = findViewById(R.id.edit_name);
        edit_email = findViewById(R.id.edit_email);
        edit_password = findViewById(R.id.edit_password);
        //endregion

        // Firebase Auth instance
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        //region Setup onClick listeners
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = edit_name.getText().toString();
                final String email = edit_email.getText().toString();
                String password = edit_password.getText().toString();

                //region Validations
                if(name.isEmpty()){
                    edit_name.setError(getString(R.string.error_name_required));
                    return ;
                }if(email.isEmpty()){
                    edit_email.setError(getString(R.string.error_email_required));
                    return ;
                }if(password.isEmpty()){
                    edit_password.setError(getString(R.string.error_password_required));
                    return ;
                }
                //endregion

                //region Show ProgressDialog for loading.
                progress = new ProgressDialog(RegisterActivity.this);
                progress.setMessage(getString(R.string.make_login));
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
                //endregion

                //region Create user using Firebase
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    final FirebaseUser user = mAuth.getCurrentUser();

                                    Map<String,Object> map = new HashMap<>();
                                    map.put("email", email);
                                    map.put("name", name);

                                    mFirestore.collection("users").document(user.getUid()).set(map)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    goToMain(user);
                                                    progress.dismiss();
                                                }
                                            });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterActivity.this, task.getException().toString(),
                                            Toast.LENGTH_SHORT).show();
                                    progress.dismiss();
                                }

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
