package com.hitenter.chataround;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {



    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String mDisplayName;
    private ArrayList<DataSnapshot> mSnapshotList;


    public ChatListAdapter(Activity activity, DatabaseReference ref, String name){

        mActivity = activity;
        mDatabaseReference = ref;
        mDisplayName = name;
        mSnapshotList = new ArrayList<>();

    }


    //Inner Class
    static class ViewHolder{

        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;

    }



    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public InstantMessage getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    //convertview is listitem
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null ) {

            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_msg_row,parent,false);

            final ViewHolder holder = new ViewHolder();
            holder.authorName = convertView.findViewById(R.id.author);
            holder.body = convertView .findViewById(R.id.message);
            holder.params = (LinearLayout.LayoutParams) holder.authorName.getLayoutParams();


            //Store our view Holder to the convertView
            convertView.setTag(holder);


        }




        final InstantMessage message = getItem(position);
        final ViewHolder holder = (ViewHolder) convertView.getTag();


        String author = message.getAuthor();
        holder.authorName.setText(author);
        String msg = message.getMessage();
        holder.body.setText(msg);



        return convertView;
    }
}
