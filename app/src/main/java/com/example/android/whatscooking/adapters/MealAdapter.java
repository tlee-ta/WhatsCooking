package com.example.android.whatscooking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.whatscooking.R;
import com.example.android.whatscooking.model.Meal;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealAdapterViewHolder> {

    private List<Meal> mMealData;
    private Context mContext;

    private final MealAdapterOnClickHandler mealAdapterOnClickHandler;

    public interface MealAdapterOnClickHandler {
        void onClick(Meal selectedMeal);
    }

    public MealAdapter(MealAdapterOnClickHandler clickHandler, Context context) {
        mContext = context;
        mealAdapterOnClickHandler = clickHandler;
    }

    public class MealAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mealImage;
        public TextView mealName;

        public MealAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.iv_meal_item);
            mealName = itemView.findViewById(R.id.tv_meal_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Meal selectedMeal = mMealData.get(position);
            mealAdapterOnClickHandler.onClick(selectedMeal);
        }
    }

    @NonNull
    @Override
    public MealAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.meal_list_item;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(layoutId, parent, false);

        return new MealAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealAdapterViewHolder holder, int position) {
        Meal mealInfo = mMealData.get(position);

        holder.mealName.setText(mealInfo.getName());
        Glide.with(mContext)
                .load(mealInfo.getThumbnail())
                .into(holder.mealImage);
    }

    @Override
    public int getItemCount() {
        if (null == mMealData){
            return 0;
        }
        return mMealData.size();
    }

    public void setMealData(List<Meal> mealData) {
        mMealData = mealData;
        if (mealData != null) {
            notifyDataSetChanged();
        }
    }
}
