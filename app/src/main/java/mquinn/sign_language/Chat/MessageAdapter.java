package mquinn.sign_language.Chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.Locale;

import mquinn.sign_language.R;

import static android.content.Context.MODE_PRIVATE;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    ArrayList<MessageObject> messageList;
    TextToSpeech textToSpeech;

    public MessageAdapter(ArrayList<MessageObject> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutview.setLayoutParams(lp);

        MessageViewHolder rcv = new MessageViewHolder(layoutview);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {
        String message = messageList.get(position).getMessage();
        String sender = messageList.get(position).getSenderId();
        holder.mMessage.setText(message);
        holder.mSender.setText(sender);
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech = new TextToSpeech(view.getContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if (i != TextToSpeech.ERROR) {
                            textToSpeech.setLanguage(Locale.getDefault());
                            textToSpeech.speak(sender,TextToSpeech.QUEUE_FLUSH,null);
                        }
                    }
                });
            }
        });
//        holder.mLayout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        break;
//                }
//
//                return false;
//            }
//        });

        if (messageList.get(holder.getAdapterPosition()).getMediaUrlList().isEmpty())
            holder.mViewMedia.setVisibility(View.GONE);
        holder.mViewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ImageViewer.Builder(view.getContext(),messageList.get(holder.getAdapterPosition()).getMediaUrlList()).setStartPosition(0).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


     static class MessageViewHolder extends RecyclerView.ViewHolder{
         LinearLayout mLayout;
         TextView mMessage,mSender;
         Button mViewMedia;
         MessageViewHolder(View view){
            super(view);
            mLayout = view.findViewById(R.id.layout_message);
            mMessage = view.findViewById(R.id.message_Text_View);
            mSender = view.findViewById(R.id.sender_Text_View);
            mViewMedia = view.findViewById(R.id.viewMedia);


        }
    }
}
