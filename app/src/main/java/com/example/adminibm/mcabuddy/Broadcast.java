package com.example.adminibm.mcabuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adminibm.mcabuddy.bean.Message;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Broadcast.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Broadcast#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Broadcast extends Fragment {

    View rootView;
    ExpandableListView lv;
    private String[] groups;
    private String[][] children;
    private Integer[] id;

    private String[] parentItem;

    Bundle itemDetails;
    String getRole;
    private TextView listItem;
    private RecyclerView.ViewHolder holder;

    public String strheader;
    public String strDetailText;
    public String strAuther;
    public String strDate;
    public List<Message> messageList;


    public Broadcast(String[] groups, String[][] children,List<Message> messageList) {
        this.groups = groups;
        this.children = children;
        this.messageList= messageList;

    }

    public Broadcast() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fetchMessagesForChannel();

        //groups = new String[]{" "," ", " "," "," "};
        id = new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};

        //children = new String [10][10];

        getRole = getActivity().getIntent().getExtras().getString("loginRole");
        strAuther = getActivity().getIntent().getExtras().getString("Auther");
        strDate = getActivity().getIntent().getExtras().getString("Date");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_broadcast, container, false);
        return rootView;
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_broadcast, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lv = (ExpandableListView) view.findViewById(R.id.listBroadcast);
        if(groups!=null && children!=null ){
            if(groups.length>0 &&  children.length>0){
                lv.setAdapter(new ExpandableListAdapter(groups, children,messageList));
                lv.setGroupIndicator(null);
            }

        }

    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private final LayoutInflater inf;
        private String[] groups;
        private String[][] children;
        private List<Message> messageList;


        public ExpandableListAdapter(String[] groups, String[][] children,List<Message> messageList) {
            this.groups = groups;
            this.children = children;
            this.messageList=messageList;

            inf = LayoutInflater.from(getActivity());
        }

        @Override
        public int getGroupCount() {
            return groups.length;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return children[groupPosition].length;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return children[groupPosition][childPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return id[groupPosition];
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                convertView = inf.inflate(R.layout.list_item, parent, false);
                holder = new ViewHolder();

                holder.text = (TextView) convertView.findViewById(R.id.lblListItem);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if(getChild(groupPosition, childPosition).toString() != null) {
                holder.text.setText(getChild(groupPosition, childPosition).toString());
            }

            strDetailText = getChild(groupPosition, childPosition).toString();
            strheader = getGroup(groupPosition).toString();
            //Toast.makeText(getActivity(),strheader, Toast.LENGTH_LONG).show();

            listItem = (TextView)convertView.findViewById(R.id.lblListItem);

            listItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent listItemDetails = new Intent(getActivity(), MCABuddy_ThreadDetails.class);

                    itemDetails = new Bundle();
                    itemDetails.putString("strDetails", strDetailText);
                    itemDetails.putStringArrayList("tags", (ArrayList<String>) messageList.get(groupPosition).getTags());
                    itemDetails.putString("strRole", getRole);
                    itemDetails.putString("strHeader", strheader);
                    itemDetails.putString("strAuther", messageList.get(groupPosition).getAuthor());
                    itemDetails.putString("strDate", messageList.get(groupPosition).getDate());
                    //// TODO: 5/1/2016 change this to thread id 
                    itemDetails.putString("threadId",messageList.get(groupPosition).getUuid());
                    itemDetails.putString("uuid", messageList.get(groupPosition).getUuid());
                    itemDetails.putInt("likes",messageList.get(groupPosition).getLikes());
                    itemDetails.putString("channelName",messageList.get(groupPosition).getChannel());
                    listItemDetails.putExtras(itemDetails);
                    getActivity().startActivity(listItemDetails);
                }
            });

            return convertView;
        }


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inf.inflate(R.layout.list_group, parent, false);

                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.lblListHeader);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(getGroup(groupPosition).toString());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private class ViewHolder {
            TextView text;
        }
    }
}
