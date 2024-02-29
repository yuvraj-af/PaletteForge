package com.example.paletteforge;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Actions {

    public static String generateRandomHexColor() {
        Random random = new Random();

        // Generate random hue value in the range [0, 360)
        float hue = random.nextFloat() * 360;

        // Fixed saturation and value
        float saturation = 0.7f;
        float value = 1.0f;

        // Convert HSV to RGB
        int colorInt = Color.HSVToColor(new float[]{hue, saturation, value});

        // Convert RGB to hex

        return String.format("#%06X", 0xFFFFFF & colorInt);
    }

    private static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public static boolean maxCheck(final LinearLayout tempColorLayout, Context context) {
        if (tempColorLayout.getChildCount() == 4) {
            Toast.makeText(context, "Maximum Palette Size Reached", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public static void clearList(final LinearLayout tempColorLayout, final EditText labelEditText, Context context) {
        if (tempColorLayout.getChildCount() == 0 && labelEditText.getText().toString().isEmpty()) {
            Toast.makeText(context, "List Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        tempColorLayout.removeAllViews();
    }

    public static void clearLabel(final EditText labelEditText) {
        labelEditText.setText("");
    }

    public static boolean checkColorCountForAddPaletteButton(final LinearLayout tempColorLayout, Context context) {
        if (tempColorLayout.getChildCount() < 2) {
            Toast.makeText(context, "Please Add at least 2 Colors", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public static boolean checkLabel(final EditText label, Context context) {
        String labelText = label.getText().toString();
        labelText = labelText.trim();
        if (labelText.isEmpty()) {
            Toast.makeText(context, "Please Enter Label", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public static void addTempColor(final LinearLayout tempColorLayout, int color, Context context) {
        final CardView tempColor = new CardView(context);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                dpToPx(60, context),  // Width in pixels
                dpToPx(36, context)   // Height in pixels
        );

        layoutParams.setMargins(dpToPx(0, context), 0, dpToPx(14, context), 0);
        tempColor.setRadius(dpToPx(8, context));
        tempColor.setCardElevation(dpToPx(2, context));

        tempColor.setLayoutParams(layoutParams);
        tempColor.setCardBackgroundColor(color);
        tempColorLayout.addView(tempColor);
    }

    public static void savePalette(Context context, String label, List<Integer> colors, PaletteAdapter paletteAdapter) {
        List<Palette> palettes = loadPalettes(context);
        if (palettes == null) {
            palettes = new ArrayList<>();
        }
        Palette palette = new Palette();
        palette.setLabel(label);
        palette.setColors(colors);

        palettes.add(0, palette);

        SharedPreferences preferences = context.getSharedPreferences("Palettes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(palettes);
        editor.putString("palettes", json);
        editor.apply();

        // Notify the adapter about the insertion
        paletteAdapter.notifyItemInserted(0);
        Log.d("TestCase", "Actions Line 156");
    }




    public static void savePalettes(Context context, List<Palette> palettes) {
        SharedPreferences preferences = context.getSharedPreferences("Palettes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(palettes);
        editor.putString("palettes", json);
        editor.apply();
    }


    public static List<Palette> loadPalettes(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Palettes", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("palettes", null);

        if (json != null) {
            Type type = new TypeToken<List<Palette>>() {}.getType();
            List<Palette> loadedPalettes = gson.fromJson(json, type);
            Log.d("Actions", "Palettes loaded: " + loadedPalettes.size());
            return gson.fromJson(json, type);
        } else {
            Log.d("Actions", "No palettes found");
            return new ArrayList<>(); // Return an empty list if the saved data is null
        }
    }

    public static List<Integer> extractColorsFromLayout(final LinearLayout tempColorLayout) {
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < tempColorLayout.getChildCount(); i++) {
            View colorView = tempColorLayout.getChildAt(i);
            if (colorView instanceof CardView) {
                CardView cardView = (CardView) colorView;
                colors.add(cardView.getCardBackgroundColor().getDefaultColor());
            }
        }
        return colors;
    }

    public static void updatePaletteList(List<Palette> palettes, Context context, PaletteAdapter paletteAdapter) {
        List<Palette> loadedPalettes = Actions.loadPalettes(context);
        palettes.clear();
        palettes.addAll(loadedPalettes);
        paletteAdapter.notifyDataSetChanged();
        Log.d("TestCase", "Actions Line 150");
    }
}
