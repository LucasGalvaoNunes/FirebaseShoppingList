package dev.lucasgnunes.firebaseshoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewItemList extends AppCompatActivity {

    private Item item;

    private EditText edit_name, edit_amount;
    private Button button_save, button_remove;


    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item_list);

        edit_name = findViewById(R.id.edit_name);
        edit_amount = findViewById(R.id.edit_amount);

        button_save = findViewById(R.id.button_save);
        button_remove = findViewById(R.id.button_remove);

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        if(bundle != null){
            item = (Item) bundle.getSerializable("item");
        }

        if(item != null){
            edit_name.setText(item.getName());
            edit_amount.setText(item.getAmount().toString());
            button_remove.setVisibility(View.VISIBLE);
        }

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edit_name.getText().toString();
                String price = edit_amount.getText().toString();

                Map<String,Object> map = new HashMap<>();
                map.put("name", name);
                map.put("amount", price);

                //region Show ProgressDialog for loading.
                progress = new ProgressDialog(NewItemList.this);
                progress.setMessage(getString(R.string.make_save));
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
                //endregion

                if(item != null) {
                    map.put("checked", item.getChecked());
                    db.collection("shopping-list")
                            .document(mUser.getUid()).collection("itens").document(item.getDocumentID()).update(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progress.dismiss();
                                    finish();
                                }
                            });
                } else {
                    map.put("checked", false);
                    db.collection("shopping-list")
                            .document(mUser.getUid()).collection("itens").document().set(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progress.dismiss();
                                    finish();
                                }
                            });
                }

            }
        });
        button_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //region Show ProgressDialog for loading.
                progress = new ProgressDialog(NewItemList.this);
                progress.setMessage(getString(R.string.make_save));
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
                //endregion


                db.collection("shopping-list")
                        .document(mUser.getUid()).collection("itens").document(item.getDocumentID()).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progress.dismiss();
                                finish();
                            }
                        });
            }
        });

    }
}
