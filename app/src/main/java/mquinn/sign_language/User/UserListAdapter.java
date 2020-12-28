package mquinn.sign_language.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import mquinn.sign_language.R;

import static android.content.Context.MODE_PRIVATE;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    ArrayList<UserObject> userList;

    public UserListAdapter(ArrayList<UserObject> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutview = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutview.setLayoutParams(lp);

        UserListViewHolder rcv = new UserListViewHolder(layoutview);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserListViewHolder holder, int position) {
        holder.name.setText(userList.get(position).getName());
        holder.phone.setText(userList.get(position).getPhone());

        holder.mAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                userList.get(holder.getAdapterPosition()).setSelected(b);
            }
        });

    }



    @Override
    public int getItemCount() {
        return userList.size();
    }


     static class UserListViewHolder extends RecyclerView.ViewHolder{
         TextView name,phone;
         RelativeLayout mLayout;
         CheckBox mAdd;
         UserListViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.users_single_name);
            phone = view.findViewById(R.id.users_single_phone);
            mAdd= view.findViewById(R.id.add);
            mLayout = view.findViewById(R.id.layout);

        }
    }
}
