package com.example.paletteforge;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.List;

public class PaletteDialogFragment extends DialogFragment {

    private List<Integer> colors;
    public PaletteDialogFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set dialog style to fullscreen
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_palette_dialog, container, false);

        // Find the LinearLayout container for colors
        LinearLayout colorContainer = view.findViewById(R.id.colorContainer);
        assert getArguments() != null;
        List<Integer> colors = getArguments().getIntegerArrayList("colors");

        // Retrieve colors from arguments
        Bundle args = getArguments();
        if (args != null && args.containsKey("colors")) {
            colors = args.getIntegerArrayList("colors");
        }

        // Display colors in equal height bars
        if (colors != null) {
            for (int color : colors) {
                View colorEntry = inflater.inflate(R.layout.item_color_entry_horizontal, colorContainer, false);
                colorEntry.startAnimation(getScaleAndFadeInAnimation());
                View colorView = colorEntry.findViewById(R.id.colorView);
                TextView hexTextView = colorEntry.findViewById(R.id.hexTextView);

                colorView.setBackgroundColor(color);
                hexTextView.setText(String.format("#%06X", (0xFFFFFF & color)));
                hexTextView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        // Copy hex code to clipboard
                        copyToClipboard(hexTextView.getText().toString());
                        return true; // Consume the long click event
                    }
                });
                colorContainer.addView(colorEntry, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1.0f
                ));
            }
        }

        return view;
    }

    private Animation getScaleAndFadeInAnimation() {
        // Scale and fade in animation
        AnimationSet scaleAndFadeInAnimation = new AnimationSet(true);

        // Scale animation
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(175); // Set the duration of the scale animation

        // Fade in animation
        AlphaAnimation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
        fadeInAnimation.setDuration(200); // Set the duration of the fade in animation

        // Add both animations to the animation set
        scaleAndFadeInAnimation.addAnimation(scaleAnimation);
        scaleAndFadeInAnimation.addAnimation(fadeInAnimation);

        return scaleAndFadeInAnimation;
    }

    private Animation getScaleAndFadeOutAnimation() {
        // Scale and fade out animation
        AnimationSet scaleAndFadeOutAnimation = new AnimationSet(true);

        // Scale animation
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.5f, 1.0f, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500); // Set the duration of the scale animation

        // Fade out animation
        AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        fadeOutAnimation.setDuration(500); // Set the duration of the fade out animation

        // Add both animations to the animation set
        scaleAndFadeOutAnimation.addAnimation(scaleAnimation);
        scaleAndFadeOutAnimation.addAnimation(fadeOutAnimation);

        return scaleAndFadeOutAnimation;
    }
    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Hex Code", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(requireContext(), "Hex code copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}
