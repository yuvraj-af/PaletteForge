package com.example.paletteforge;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import me.jfenn.colorpickerdialog.dialogs.ColorPickerDialog;

public class PaletteActivity extends AppCompatActivity {

    private LinearLayout tempColorLayout;
    private EditText labelEditText;
    private PaletteAdapter paletteAdapter;
    private List<Palette> palettes;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Apply custom animation
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);

        FloatingActionButton floatingAB = findViewById(R.id.floatingAB);
        tempColorLayout = findViewById(R.id.tempColorLayout);
        Button clearListButton = findViewById(R.id.clearListButton);
        RelativeLayout rootLayout = findViewById(R.id.rootLayout);
        Button addPaletteButton = findViewById(R.id.addPaletteButton);
        labelEditText = findViewById(R.id.labelEditText);

        palettes = Actions.loadPalettes(this);
        paletteAdapter = new PaletteAdapter(palettes);
        RecyclerView recyclerView = findViewById(R.id.palettesRecyclerView);
        recyclerView.setAdapter(paletteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        floatingAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Actions.maxCheck(tempColorLayout, view.getContext())) return;
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
                colorPickerDialog
                        .withPresets(Color.RED, Color.GREEN, Color.BLUE, Color.BLACK, Color.WHITE, Color.YELLOW, Color.MAGENTA, Color.DKGRAY, Color.CYAN, Color.LTGRAY)
                        .withAlphaEnabled(false)
                        .withCornerRadius(12.0f)
                        .withColor(Color.parseColor(Actions.generateRandomHexColor()))
                        .withListener((dialog, color) -> {
                            Actions.addTempColor(tempColorLayout, color, view.getContext());
                        })
                        .show(getSupportFragmentManager(), "ColorPicker");
            }
        });

        clearListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Actions.clearList(tempColorLayout, labelEditText, view.getContext());
                Actions.clearLabel(labelEditText);
            }
        });

        addPaletteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Actions.checkColorCountForAddPaletteButton(tempColorLayout, view.getContext())) return;
                if (Actions.checkLabel(labelEditText, view.getContext())) return;

                List<Integer> colors = Actions.extractColorsFromLayout(tempColorLayout);
                final String label = labelEditText.getText().toString();
                Actions.savePalette(view.getContext(), label, colors, paletteAdapter);

                // Update the RecyclerView immediately after adding a new palette
                Actions.updatePaletteList(palettes, getApplicationContext(), paletteAdapter);
                Actions.clearList(tempColorLayout, labelEditText, view.getContext());
                Actions.clearLabel(labelEditText);
            }
        });

        paletteAdapter.setOnItemClickListener(new PaletteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Palette palette) {
                Log.d("TestCase", "PaletteActivity Line 94 ");
                showPaletteDialog(palette);
            }
        });

        paletteAdapter.setOnItemLongClickListener(new PaletteAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(Palette palette) {
                showDeletePaletteDialog(palette);
            }
        });



    }

    private void showDeletePaletteDialog(final Palette palette) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle("Delete Palette")
                .setMessage("Do you want to delete the palette? This can't be undone.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePalette(palette);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePalette(Palette palette) {
        int position = palettes.indexOf(palette);
        if (position >= 0) {
            palettes.remove(position);

            // Update SharedPreferences
            Actions.savePalettes(this, palettes);

            // Notify adapter about the deletion
            paletteAdapter.notifyItemRemoved(position);

            // Log the deleted palette label
            Log.d("PaletteActivity", "Deleted Palette: " + palette.getLabel());
        }
    }



    private void showPaletteDialog(Palette palette) {
        PaletteDialogFragment dialogFragment = new PaletteDialogFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList("colors", (ArrayList<Integer>) palette.getColors());
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "PaletteDialogFragment");
        Log.d("TestCase", "PaletteActivity Line 107");
    }
}
