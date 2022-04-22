package com.xyz.tracemaster.ui.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xyz.tracemaster.R;
import com.xyz.tracemaster.data.bean.Trace;
import com.xyz.tracemaster.utils.HistoryUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : xyz
 *     time   : 2022/4/22 9:23
 * </pre>
 */
public class TraceRecycleViewAdapter extends RecyclerView.Adapter<TraceRecycleViewAdapter.TraceViewHolder> {
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private List<Trace> traceArrayList = new ArrayList<>();

    public void setTraceArrayList(List<Trace> traceArrayList) {
        this.traceArrayList = traceArrayList;
    }

    @NonNull
    @Override
    public TraceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY) {
            View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_recyclerview, parent, false);
            return new TraceViewHolder(emptyView);
        }
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.cell_trace, parent, false);

        return new TraceViewHolder(itemView);
    }

    static class TraceViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUseTime, textViewRecordTime, textViewLocation;

        public TraceViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUseTime = itemView.findViewById(R.id.textViewUseTime);
            textViewRecordTime = itemView.findViewById(R.id.textViewRecordTime);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (traceArrayList.size() == 0) {
            return VIEW_TYPE_EMPTY;
        }
        return VIEW_TYPE_ITEM;

    }

    @Override
    public void onBindViewHolder(@NonNull TraceViewHolder holder, int position) {
        if (traceArrayList != null && !traceArrayList.isEmpty()) {
            Trace trace = traceArrayList.get(position);
            holder.textViewLocation.setText(trace.getLocation());
            holder.textViewRecordTime.setText(HistoryUtils.getRecordTime(trace.getStartTime()));
            holder.textViewUseTime.setText(HistoryUtils.getUseTime(trace.getStartTime(), trace.getEndTime()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    System.out.println("点击了");
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (traceArrayList.size() == 0) {
            return 1;
        }
        return traceArrayList.size();

    }

}
