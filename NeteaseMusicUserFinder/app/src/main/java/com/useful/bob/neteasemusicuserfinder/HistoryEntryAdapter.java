package com.useful.bob.neteasemusicuserfinder;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by bob on 2018/7/30.
 */

public class HistoryEntryAdapter extends RecyclerView.Adapter<HistoryEntryAdapter.HistoryEntryViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    public final HistoryAndRecommendationSQLiteOpenHelper mDB;
    class HistoryEntryViewHolder extends RecyclerView.ViewHolder{
        public TextView historyTitle;
        public Button deleteButton;
        public Button copyButton;

        public HistoryEntryViewHolder(View view){
            super(view);
            historyTitle = (TextView) view.findViewById(R.id.historyEntryTitle);
            deleteButton = (Button) view.findViewById(R.id.deleteButton);
            copyButton = (Button) view.findViewById(R.id.copyButton);
        }
    }

    public HistoryEntryAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
        mDB = new HistoryAndRecommendationSQLiteOpenHelper(context);
    }
    @Override
    public void onBindViewHolder(final HistoryEntryViewHolder holder, int position) {
        final String[] name = new String[1];
        final int[] id = new int[1];
        Cursor cursor = mDB.query(position);
        if (cursor != null){
            if (cursor.moveToFirst()){
                name[0] = cursor.getString(cursor.getColumnIndex(HistoryAndRecommendationSQLiteOpenHelper.NICK_NAME));
                int indexId = cursor.getColumnIndex(HistoryAndRecommendationSQLiteOpenHelper.NICK_NAME_ID);
                id[0] = cursor.getInt(indexId);
                Log.d("NAME",name[0]);
                holder.historyTitle.setText(name[0]);
            }else {

            }
            cursor.close();
        }else {
            Log.d("CURSOR","Cursor is Null");
//            holder.historyTitle.setText("无搜索历史");
        }
        holder.copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager =(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("NICKNAME",name[0]);
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(context,"已复制到剪贴板",Toast.LENGTH_SHORT).show();
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int deleted = mDB.delete(id[0]);
                if (deleted >0 ){
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(),getItemCount());
                }
            }
        });
    }


    @Override
    public HistoryEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.history_entry,parent,false);
        return new HistoryEntryViewHolder(view);
    }

    @Override
    public int getItemCount() {
        Cursor cursor = mDB.count();
        try {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            Log.d("ITEMCOUNT",String.valueOf(count));
            cursor.close();
            return count;
        }catch (Exception e){
            throw e;
        }
    }
}
