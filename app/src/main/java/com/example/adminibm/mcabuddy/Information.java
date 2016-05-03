package com.example.adminibm.mcabuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adminibm.mcabuddy.bean.Message;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Information.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Information#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Information extends Fragment {

    View rootView;
    ExpandableListView lv;
    private String[] groups;
    private String[][] children;
    private String[] id;

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


    public Information() {
        // Required empty public constructor
    }

    public Information(String[] groups, String[][] children,List<Message> messageList) {
        this.groups = groups;
        this.children = children;
        this.messageList=messageList;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getRole = getActivity().getIntent().getExtras().getString("loginRole");
        strAuther = getActivity().getIntent().getExtras().getString("Auther");
        strDate = getActivity().getIntent().getExtras().getString("Date");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_information, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lv = (ExpandableListView) view.findViewById(R.id.listInformation);
        lv.setAdapter(new ExpandableListAdapter(groups, children));
        lv.setGroupIndicator(null);
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private final LayoutInflater inf;
        private String[] groups;
        private String[][] children;


        public ExpandableListAdapter(String[] groups, String[][] children) {
            this.groups = groups;
            this.children = children;
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
            return groupPosition;
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
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

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
            Toast.makeText(getActivity(), strheader, Toast.LENGTH_LONG).show();

            listItem = (TextView)convertView.findViewById(R.id.lblListItem);

            listItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent listItemDetails = new Intent(getActivity(), MCABuddy_ThreadDetails.class);

                    itemDetails = new Bundle();
                    itemDetails.putString("strDetails", strDetailText);
                    itemDetails.putString("strRole", getRole);
                    itemDetails.putString("strHeader", strheader);
                    itemDetails.putString("strAuther", strAuther);
                    itemDetails.putString("strDate", strDate);
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
