package adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fileops.fileops.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by haseeb on 26/4/17.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.MyViewHolder> {
    public int VIEW_ITEM = 0;
    public int VIEW_LOAD = 1;
    int HolderId = 0;

    private List<List<Map.Entry<String, Integer>>> mainData = new ArrayList<>();
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView heading;
        public LinearLayout dataholder;

        public MyViewHolder(View view, int ViewType) {
            super(view);
            HolderId = 0;
            heading = (TextView) view.findViewById(R.id.heading);
            dataholder = (LinearLayout) view.findViewById(R.id.dataholder);

        }
    }


    public FileAdapter(List<List<Map.Entry<String, Integer>>> data, Context mContext) {
        this.mainData = data;
        this.mContext = mContext;
    }

    @Override
    public FileAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_list_row, parent, false);
        return new FileAdapter.MyViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(FileAdapter.MyViewHolder holder, final int position) {
        List<Map.Entry<String, Integer>> item = mainData.get(position);
        String heading = String.valueOf(position * 10 + " - "+ (position+1)*10);
        holder.heading.setText(heading);

        for (Map.Entry<String, Integer> entry : item){
            TextView textView = new TextView(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(40,10,10,10);
            textView.setLayoutParams(lp);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(16);
            String text = entry.getKey() +  " " + entry.getValue();
            textView.setText(text);
            holder.dataholder.addView(textView);
        }

    }

    @Override
    public int getItemCount() {
        return mainData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}

