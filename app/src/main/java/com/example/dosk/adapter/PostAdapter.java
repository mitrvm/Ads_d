package com.example.dosk.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dosk.DBManager;
import com.example.dosk.EditActivity;
import com.example.dosk.MainActivity;
import com.example.dosk.NewPost;
import com.example.dosk.R;
import com.example.dosk.utils.MyConstants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolderData> {
    private List<NewPost> arrayPost;
    private Context context;
    private OnItemClickCustom onItemClickCustom;
    private DBManager dbManager;

    public PostAdapter(List<NewPost> arrayPost, Context context, OnItemClickCustom onItemClickCustom) {
        this.arrayPost = arrayPost;
        this.context = context;
        this.onItemClickCustom = onItemClickCustom;
        this.dbManager = dbManager;
    }

    @NonNull
    @Override
    public ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.item_ads, parent, false);
        return new ViewHolderData(view, onItemClickCustom);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderData holder, int position) {
        holder.setData(arrayPost.get(position));

    }

    @Override
    public int getItemCount() {
        return arrayPost.size();
    }

    public class ViewHolderData extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView tvPriceTel, tvDesc, tvTitle;
        private LinearLayout edit_layout;
        private ImageButton deleteButton, editButton;
        private ImageView imAds;
        private OnItemClickCustom onItemClickCustom;

        public ViewHolderData(@NonNull View itemView, OnItemClickCustom onItemClickCustom) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPriceTel = itemView.findViewById(R.id.tvPriceTel);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            imAds = itemView.findViewById(R.id.imAds);
            edit_layout = itemView.findViewById(R.id.edit_layout);
            deleteButton = itemView.findViewById(R.id.imDeleteItem);
            editButton = itemView.findViewById(R.id.imEditItem);
            itemView.setOnClickListener(this);
            this.onItemClickCustom = onItemClickCustom;




        }
        public void setData(NewPost newPost){
            if (newPost.getUid().equals(MainActivity.MAUTH)){
                edit_layout.setVisibility(View.VISIBLE);
            }
            else{
                edit_layout.setVisibility(View.GONE);
            }
            Picasso.get().load(newPost.getImageId()).into(imAds);
            tvTitle.setText(newPost.getTitle());
            String price_tel = "Цена: "+ newPost.getPrice() + " Тел: "+ newPost.getTel();
            tvPriceTel.setText(price_tel);
            String textDesc = null;
            if(newPost.getDesc().length()>50){
                textDesc = newPost.getDesc().substring(0,50)+"...";
            }
            else{
                textDesc = newPost.getDesc();
            }
            tvDesc.setText(textDesc);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    deleteDialog(newPost, getAdapterPosition());
                }
            });
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(context, EditActivity.class);
                    i.putExtra(MyConstants.IMAGE_ID, newPost.getImageId());
                    i.putExtra(MyConstants.TITLE, newPost.getTitle());
                    i.putExtra(MyConstants.PRICE, newPost.getPrice());
                    i.putExtra(MyConstants.TEL, newPost.getTel());
                    i.putExtra(MyConstants.DESC, newPost.getDesc());
                    i.putExtra(MyConstants.KEY, newPost.getKey());
                    i.putExtra(MyConstants.UID, newPost.getUid());
                    i.putExtra(MyConstants.TIME, newPost.getTime());
                    i.putExtra(MyConstants.CAT, newPost.getCat());
                    i.putExtra(MyConstants.EDIT_STATE, true);
                    context.startActivity(i);
                }
            });
        }

        @Override
        public void onClick(View view) {
            onItemClickCustom.onItemSelected(getAdapterPosition());
        }
    }

    private void deleteDialog(final NewPost newPost, int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_title);
        builder.setMessage(R.string.delete_message);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbManager.deleteItem(newPost);
                arrayPost.remove(position);
                notifyItemRemoved(position);
            }
        });
        builder.show();
    }
    public interface OnItemClickCustom{
        public void onItemSelected(int position);
    }
    public void updateAdapter(List<NewPost> listData){
        arrayPost.clear();
        arrayPost.addAll(listData);
        notifyDataSetChanged();
    }
    public void setDbManager(DBManager dbManager){
        this.dbManager = dbManager;
     }
}
