package com.example.group2_final_project.onboarding;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group2_final_project.R;

import java.util.List;

public class OnboardingViewPagerAdapter extends RecyclerView.Adapter<OnboardingViewPagerAdapter.ViewHolder> {
    Context context;
    List<OnboardingScreenItem> screenItems;

    public OnboardingViewPagerAdapter(List<OnboardingScreenItem> screenItems) {
        this.screenItems = screenItems;
        Log.i("SIZE", screenItems.size()+"");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.onboarding_screen_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OnboardingScreenItem onboardingScreenItem = screenItems.get(position);
        holder.itemView.setBackgroundResource(onboardingScreenItem.getScreenImg());
//        holder.imageView.setImageResource(onboardingScreenItem.getScreenImg());
    }

    @Override
    public int getItemCount() {
        return screenItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            imageView = itemView.findViewById(R.id.intro_image);
        }
    }
}
