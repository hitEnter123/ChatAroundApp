package com.hitenter.chataround;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

//TODO 4-8 Create Adapter Class
public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageViewHolder> {

    private List<InstantMessage> messageList = null;


    public ChatMessageAdapter(List<InstantMessage> messageList) {
        this.messageList = messageList;
    }

    public ChatMessageAdapter() {
    }

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.chat_msg_row,parent,false);


        return new ChatMessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChatMessageViewHolder holder, int position) {

        InstantMessage msg = this.messageList.get(position);


            if (msg.getMessageType().equals("MSG_RECEIVED")) {

                holder.receivedMsg.setText(msg.getMessage());
                holder.authorReceived.setText(msg.getAuthor());
                holder.sentLayout.setVisibility(View.GONE);

            } else if (msg.getMessageType().equals("MSG_SENT")) {

                holder.sentMsg.setText(msg.getMessage());
                holder.authorSent.setText(msg.getAuthor());
                holder.receivedLayout.setVisibility(View.GONE);

            }

    }

    @Override
    public int getItemCount() {

        if (messageList == null ){
            messageList = new ArrayList<InstantMessage>();

        }
        return messageList.size();
    }
}
