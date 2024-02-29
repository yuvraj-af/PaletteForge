package com.example.paletteforge;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PaletteAdapter extends RecyclerView.Adapter<PaletteAdapter.PaletteViewHolder> {



    public interface OnItemClickListener {
        void onItemClick(Palette palette);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Palette palette);
    }

    private static List<Palette> palettes;

    private OnItemClickListener onItemClickListener;
    private static OnItemLongClickListener onItemLongClickListener;


    public PaletteAdapter(List<Palette> palettes) {
        PaletteAdapter.palettes = palettes;
    }

    @NonNull
    @Override
    public PaletteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_palette, parent, false);
        return new PaletteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaletteViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Palette palette = palettes.get(position);
        holder.bind(palette);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null && position < palettes.size()) {
                // Get the clicked palette
                Palette clickedPalette = palettes.get(position);
                // Notify the listener about the click
                onItemClickListener.onItemClick(clickedPalette);

            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onItemLongClickListener != null) {
                // Get the long-pressed palette
                Palette longPressedPalette = palettes.get(position);
                // Notify the listener about the long press
                onItemLongClickListener.onItemLongClick(longPressedPalette);
                return true; // consume the long click event
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return palettes.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }
    static class PaletteViewHolder extends RecyclerView.ViewHolder {

        private final TextView paletteLabel;
        private final ViewGroup colorContainer;

        PaletteViewHolder(@NonNull View itemView) {
            super(itemView);
            paletteLabel = itemView.findViewById(R.id.paletteLabel);
            colorContainer = itemView.findViewById(R.id.colorContainer);

            itemView.setOnLongClickListener(v -> {
                if (onItemLongClickListener != null && palettes != null) {
                    // Get the long-pressed palette
                    Palette longPressedPalette = palettes.get(getAdapterPosition());
                    // Notify the listener about the long press
                    onItemLongClickListener.onItemLongClick(longPressedPalette);
                    return true; // consume the long click event
                }
                return false;
            });


        }

        void bind(Palette palette) {
            Log.d("PaletteActivity", "Binding palette: " + palette.getLabel());
            paletteLabel.setText(palette.getLabel());

            // Clear previous color views
            colorContainer.removeAllViews();

            // Add color views to the colorContainer
            int colorViewWidth = (int) itemView.getResources().getDimension(R.dimen.color_view_width);
            int colorViewHeight = (int) itemView.getResources().getDimension(R.dimen.color_view_height);

            // Adding margin after each color
            int marginBetweenColors = (int) itemView.getResources().getDimension(R.dimen.margin_between_colors);

            for (int color : palette.getColors()) {
                View colorView = new View(itemView.getContext());
                colorView.setBackgroundColor(color);

                // Apply layout parameters with margin and border radius
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(colorViewWidth, colorViewHeight);
                layoutParams.setMargins(0, 0, marginBetweenColors, 0);
                colorView.setLayoutParams(layoutParams);

                // Apply border radius
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                gradientDrawable.setColor(color);
                gradientDrawable.setCornerRadius(itemView.getResources().getDimension(R.dimen.border_radius));
                colorView.setBackground(gradientDrawable);

                colorContainer.addView(colorView);
            }
        }
    }
}
