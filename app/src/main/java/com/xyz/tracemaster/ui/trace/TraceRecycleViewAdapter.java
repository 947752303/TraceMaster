package com.xyz.tracemaster.ui.trace;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.xyz.tracemaster.R;
import com.xyz.tracemaster.app.Constant;
import com.xyz.tracemaster.data.bean.Trace;
import com.xyz.tracemaster.data.viewModel.TraceViewModel;
import com.xyz.tracemaster.utils.HistoryUtils;
import com.xyz.tracemaster.utils.PreferencesUtils;

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
    private TraceViewModel traceViewModel;

    public void setTraceArrayList(List<Trace> traceArrayList) {
        this.traceArrayList = traceArrayList;
    }

    public TraceRecycleViewAdapter(TraceViewModel traceViewModel) {
        this.traceViewModel = traceViewModel;
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
    public void onBindViewHolder(@NonNull TraceViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (traceArrayList != null && !traceArrayList.isEmpty()) {
            Trace trace = traceArrayList.get(position);
            holder.textViewLocation.setText(trace.getLocation());
            holder.textViewRecordTime.setText(HistoryUtils.getRecordTime(trace.getStartTime()));
            holder.textViewUseTime.setText(HistoryUtils.getUseTime(trace.getStartTime(), trace.getEndTime()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PreferencesUtils.putString(Constant.LOC, trace.getLatLngList());

                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_nav_gallery_to_nav_history);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(holder.itemView.getContext())
                            .setTitle("是否删除当前记录")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    traceArrayList.remove(position);
                                    traceViewModel.deleteTrace(trace.getTraceId());
                                    notifyDataSetChanged();
                                    notifyItemRangeChanged(0, traceArrayList.size());
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                    return false;
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
