package com.example.adminibm.mcabuddy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.adminibm.mcabuddy.R;
import com.example.adminibm.mcabuddy.bean.Subject;

import java.security.Policy;
import java.util.List;

/**
 * Created by nampreet on 3/15/2015.
 */
public class UserDetailsAdapter extends ArrayAdapter<Subject> {

    private Context context;
    private int resource;
    private List<Subject> subjectArray;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater= LayoutInflater.from(context);
        View row=inflater.inflate(R.layout.user_details, parent, false);
        TextView userName=(TextView)row.findViewById(R.id.userName);
        TextView emailId=(TextView)row.findViewById(R.id.emailIdUserDetails);
        TextView phoneNumber=(TextView)row.findViewById(R.id.phoneNumberUserDetails);
        TextView aoe =(TextView)row.findViewById(R.id.aoeUserDetails);
        TextView role =(TextView)row.findViewById(R.id.roleUserDetails);

        userName.setText(subjectArray.get(position).getFname()+" "+subjectArray.get(position).getLname().trim());
        emailId.setText(subjectArray.get(position).getEmail().trim());
        phoneNumber.setText(subjectArray.get(position).getPhone().trim());
        if(subjectArray.get(position).getAoe()!=null) {
            for (int i = 0; i < subjectArray.get(position).getAoe().size(); i++) {
                if (i == subjectArray.get(position).getAoe().size() - 1) {
                    aoe.append(subjectArray.get(position).getAoe().get(i));
                } else {
                    aoe.append(subjectArray.get(position).getAoe().get(i) + ", ");
                }

            }
        }

        if(subjectArray.get(position).getRoles()!=null) {
            for (int i = 0; i < subjectArray.get(position).getRoles().size(); i++) {
                if (i == subjectArray.get(position).getRoles().size() - 1) {
                    role.append(subjectArray.get(position).getRoles().get(i));
                } else {
                    role.append(subjectArray.get(position).getRoles().get(i) + ", ");
                }
            }
        }
        return row;
    }

    public UserDetailsAdapter(Context context, int resource, List<Subject> objects) {
        super(context, resource, objects);
        this.context= context;
        this.resource=resource;
        this.subjectArray=objects;
    }
}
