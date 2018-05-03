package com.solusi247.fatkhul.chanthelbeta.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.solusi247.fatkhul.chanthelbeta.R;
import com.solusi247.fatkhul.chanthelbeta.data.ContentData;

import java.util.ArrayList;

/**
 * Created by 247 on 28/03/2018.
 */

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder>{
    private Context context;
    private ArrayList<ContentData> listData;
    private Activity activity;
    private RecyclerView.LayoutManager layoutManager;

    public ContentAdapter(Activity activity, ArrayList<ContentData> listData, RecyclerView.LayoutManager layoutManager){
        this.listData = listData;
        this.activity = activity;
        this.layoutManager = layoutManager;
    }
    private ContentAdapter.onRecyclerViewItemClickListener mItemClickListener;

    public void setOnItemClickListener(ContentAdapter.onRecyclerViewItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onRecyclerViewItemClickListener {
        void onItemClickListener(View view, int position);
    }

    @Override
    public ContentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutManager instanceof GridLayoutManager) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_grid, parent, false);
            return new ContentAdapter.ViewHolder(view);
        } else if (layoutManager instanceof LinearLayoutManager) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_list, parent, false);
            return new ContentAdapter.ViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ContentAdapter.ViewHolder holder, int position) {
        holder.content_id.setText(listData.get(position).getId());
        holder.content_pid.setText(listData.get(position).getPid());
        holder.content_name.setText(listData.get(position).getName());
        holder.ekstensi.setText(listData.get(position).getExt());
        holder.template_id.setText(listData.get(position).getTemplate_id());
        holder.content_image.setImageResource(listData.get(position).getContent_image());
        final ContentAdapter.ViewHolder x = holder;
    }


    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView content_id, content_pid, content_name,ekstensi,template_id;
        private ImageView content_image;
        private ImageButton more_option;


        public ViewHolder(View itemview) {
            super(itemview);
            content_name = (TextView) itemview.findViewById(R.id.content_name);
            content_image = (ImageView) itemview.findViewById(R.id.content_image);
            content_id = (TextView)itemview.findViewById(R.id.content_id);
            content_pid = (TextView)itemview.findViewById(R.id.content_pid);
            template_id =(TextView)itemview.findViewById(R.id.template_id);
            ekstensi = (TextView)itemview.findViewById(R.id.content_extension);
            more_option = (ImageButton)itemview.findViewById(R.id.more_option);
            content_image.setOnClickListener(this);
            content_name.setOnClickListener(this);
            more_option.setOnClickListener(this);
        }

        public void bindView(int position) {
//            id.setText(listData.get(position).getId());
//            pid.setText(listData.get(position).getPid());
//            template_id.setText(listData.get(position).getTemplate_id());
//            content_name.setText(listData.get(position).getName());
//            ekstensi.setText(listData.get(position).getExt());
        }

        @Override
        public void onClick(final View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClickListener(v, getAdapterPosition());
            }
        }
    }
}
