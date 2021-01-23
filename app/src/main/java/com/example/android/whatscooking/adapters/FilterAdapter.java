package com.example.android.whatscooking.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.whatscooking.R;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter {

    private List<String> dataList;
    private List<String> filterList = new ArrayList<>();
    int counter = 0;

    public interface OnItemCheckListener {
        void onItemCheck(String item);
        void onItemUncheck(String item);
    }

    private OnItemCheckListener onItemClick;

    @NonNull
    private OnItemCheckListener onItemCheckListener;

    public FilterAdapter( @NonNull OnItemCheckListener onItemCheckListener) {
        this.onItemClick = onItemCheckListener;
    }

    static class FilterAdapterViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        View itemView;

        public FilterAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            checkbox = itemView.findViewById(R.id.cb_search_item);
            checkbox.setClickable(false);
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            itemView.setOnClickListener(onClickListener);
        }
    }

    @NonNull
    @Override
    public FilterAdapter.FilterAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.search_list_item;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layoutId, parent, false);

        filterList.addAll(dataList);

        return new FilterAdapter.FilterAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FilterAdapterViewHolder) {
            final String currentItem = dataList.get(position);
            ((FilterAdapterViewHolder) holder).checkbox.setText(currentItem);
            ((FilterAdapterViewHolder) holder).setOnClickListener(view -> {
                ((FilterAdapterViewHolder) holder).checkbox.setChecked( !((FilterAdapterViewHolder) holder).checkbox.isChecked());
                if (((FilterAdapterViewHolder) holder).checkbox.isChecked()) {
                    onItemClick.onItemCheck(currentItem);
                }
                else {
                    onItemClick.onItemUncheck(currentItem);
                }
            });
        }

        ((FilterAdapterViewHolder) holder).checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    counter++;
                }
                else {
                    counter--;
                }
                if (counter > 1) {
                    buttonView.setChecked(false);
                    counter--;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == dataList){
            return 0;
        }
        return dataList.size();
    }

    public void setDataList(List<String> stringList) {
        this.dataList = stringList;
        filterList.addAll(dataList);
        counter = 0;
        notifyDataSetChanged();
    }

    public void filter(String text) {
        dataList = new ArrayList<>();
        if (text.isEmpty()) {
            dataList.addAll(filterList);
        }
        else {
            text = text.toLowerCase();
            for (String item: filterList) {
                if (item.toLowerCase().contains(text)) {
                    dataList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
