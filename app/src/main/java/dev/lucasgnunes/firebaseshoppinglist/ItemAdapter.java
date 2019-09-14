package dev.lucasgnunes.firebaseshoppinglist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ItemAdapter extends FirestoreRecyclerAdapter<Item, ItemAdapter.ItemHolder> {
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progress;
    private Context ctx;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ItemAdapter(@NonNull FirestoreRecyclerOptions<Item> options, Context ctx) {
        super(options);
        this.ctx = ctx;
        // Firebase Auth instance
    }

    @Override
    protected void onBindViewHolder(@NonNull final ItemHolder itemHolder, int i, @NonNull final Item item) {
        final String documentID = getSnapshots().getSnapshot(i).getId();

        itemHolder.tvName.setText(item.getName());
        itemHolder.tvAmount.setText(item.getAmount().toString());

        if(item.getChecked()){
            itemHolder.tvName.setPaintFlags(itemHolder.tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            itemHolder.tvName.setPaintFlags(itemHolder.tvName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        }
        itemHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent yourIntent = new Intent(ctx, NewItemList.class);
                Bundle b = new Bundle();
                b.putSerializable("item", item);
                yourIntent.putExtras(b); //pass bundle to your intent
                ctx.startActivity(yourIntent);

                return true;
            }
        });

        itemHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //region Show ProgressDialog for loading.
                progress = new ProgressDialog(ctx);
                progress.setMessage(ctx.getString(R.string.make_checked));
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
                //endregion

                Map<String,Object> map = new HashMap<>();
                map.put("checked", !item.getChecked());

                db.collection("shopping-list").document(mUser.getUid())
                        .collection("itens").document(documentID).update(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progress.dismiss();
                            }
                        });
            }
        });
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new ItemHolder(v);
    }

    class ItemHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvAmount;
        CardView cardView;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            cardView = itemView.findViewById(R.id.card_item);
        }
    }
}
