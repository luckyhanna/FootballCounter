package com.example.hannabotar.footballcounter;

import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by hanna.botar on 12/15/2017.
 */

public class EvolutionDialogFragment extends DialogFragment {

    /*@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        final View dialogView = inflater.inflate(R.layout.dialog_evolution, container, false);//Inflate Layout
        final View dialogView = inflater.inflate(R.layout.dialog_evolution, null);//Inflate Layout
        TextView evolutionView = (TextView) dialogView.findViewById(R.id.game_evolution);

        // Get Argument that passed from activity in "evolution_string" key value
        String dataString = (String) getArguments().get(String.valueOf(R.string.evolution_string));
        evolutionView.setText(dataString);

        // Set action for cancel button
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EvolutionDialogFragment.this.getDialog().cancel();
            }
        });

        EvolutionDialogFragment.this.getDialog().setTitle(R.string.game_evolution);
        return dialogView;
    }*/

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        String textToDisplay = (String) getArguments().get(String.valueOf(R.string.evolution_string));
        View view = inflater.inflate(R.layout.dialog_evolution, null);
        TextView evolutionView = view.findViewById(R.id.game_evolution);
//        evolutionView.setText(textToDisplay);
        evolutionView.setText(Html.fromHtml(textToDisplay));

        builder
                .setView(view)
                .setTitle(R.string.game_evolution)
                // Add action buttons
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EvolutionDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
