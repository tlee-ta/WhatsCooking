package com.example.android.whatscooking.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.whatscooking.R;
import com.example.android.whatscooking.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter {

    private List<Ingredient> ingredientDataList;
    private List<Ingredient> ingredientFilterList = new ArrayList<>();
    int counter = 0;

    public interface OnItemCheckListener {
        void onItemCheck(Ingredient item);
        void onItemUncheck(Ingredient item);
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

        ingredientFilterList.addAll(ingredientDataList);

        return new FilterAdapter.FilterAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FilterAdapterViewHolder) {
            final Ingredient currentIngredient = ingredientDataList.get(position);
            ((FilterAdapterViewHolder) holder).checkbox.setText(currentIngredient.getName());
            ((FilterAdapterViewHolder) holder).setOnClickListener(view -> {
                ((FilterAdapterViewHolder) holder).checkbox.setChecked( !((FilterAdapterViewHolder) holder).checkbox.isChecked());
                if (((FilterAdapterViewHolder) holder).checkbox.isChecked()) {
                    onItemClick.onItemCheck(currentIngredient);
                }
                else {
                    onItemClick.onItemUncheck(currentIngredient);
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
        if (null == ingredientDataList){
            return 0;
        }
        return ingredientDataList.size();
    }

    public void setIngredientDataList(List<Ingredient> ingredientData) {
        this.ingredientDataList = ingredientData;
        ingredientFilterList.addAll(ingredientDataList);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        ingredientDataList.clear();
        if (text.isEmpty()) {
            ingredientDataList.addAll(ingredientFilterList);
        }
        else {
            text = text.toLowerCase();
            for (Ingredient item: ingredientFilterList) {
                if (item.getName().toLowerCase().contains(text)) {
                    ingredientDataList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
