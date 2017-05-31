package com.cermati.yoris;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by YORIS on 5/29/17.
 */

public class ListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<User> userList;

    public ListAdapter(Activity activity, List<User> userList) {
        this.activity = activity;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int location) {
        return userList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.row, null);


        ImageView thumbNail = (ImageView)convertView.findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);

        User u = userList.get(position);

        Glide.with(activity).load(u.getAvatar_url()).into(thumbNail);

        title.setText(u.getLogin());

        return convertView;
    }

}
