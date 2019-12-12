package com.hitenter.chataround;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//TODO PT4 -7 ViewHolder
public class ChatMessageViewHolder extends RecyclerView.ViewHolder {



    LinearLayout receivedLayout , sentLayout;
    TextView receivedMsg, sentMsg, authorReceived, authorSent;




    public ChatMessageViewHolder(@NonNull View itemView) {
        super(itemView);



        receivedLayout = itemView.findViewById(R.id.received_layout);
        sentLayout = itemView.findViewById(R.id.sent_layout);
        sentMsg = itemView.findViewById(R.id.message_sent);
        receivedMsg = itemView.findViewById(R.id.message_received);
        authorReceived = itemView.findViewById(R.id.author_received);
        authorSent = itemView.findViewById( R.id.author_sent);




    }
}
