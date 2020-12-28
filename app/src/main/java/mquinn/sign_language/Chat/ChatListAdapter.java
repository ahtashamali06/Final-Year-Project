package mquinn.sign_language.Chat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import mquinn.sign_language.Activities.ChatActivity;
import mquinn.sign_language.R;

import static android.content.Context.MODE_PRIVATE;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    ArrayList<ChatObject> chatList;

    public ChatListAdapter(ArrayList<ChatObject> ChatList) {
        this.chatList = ChatList;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutview.setLayoutParams(lp);

        ChatListViewHolder rcv = new ChatListViewHolder(layoutview);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListViewHolder holder, final int position) {
        holder.title.setText(chatList.get(position).getChatId());
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences;
                String Converted_Message="";
                sharedPreferences = view.getContext().getSharedPreferences("MessageSharedPreference",MODE_PRIVATE);
                Converted_Message  = sharedPreferences.getString("Converted_Message","");
                if (Converted_Message!=null){
                    Intent intent = new Intent(view.getContext(), ChatActivity.class);
                    intent.putExtra("chatObject",chatList.get(holder.getAdapterPosition()));
                    intent.putExtra("Message_Text",Converted_Message);
                    view.getContext().startActivity(intent);
                    sharedPreferences.edit().clear().commit();
                }else {
                    Intent intent = new Intent(view.getContext(), ChatActivity.class);
                    intent.putExtra("chatObject",chatList.get(holder.getAdapterPosition()));
                    view.getContext().startActivity(intent);
                }


            }
        });

        holder.mLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Delete Chat").setMessage("Are your sure you want to Delete this Chat").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chat").child(key);
                        reference.removeValue();


                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;

                    }
                });
                builder.create().show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public static class ChatListViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public RelativeLayout mLayout;
        public ChatListViewHolder(View view){
            super(view);
            title = view.findViewById(R.id.users_single_title);
            mLayout = view.findViewById(R.id.layout);

        }
    }
}
