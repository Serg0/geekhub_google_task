package com.geekhub.exam.helpers.dialogs;

import com.geekhub.exam.R;
import com.google.api.services.tasks.model.Task;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

@SuppressLint("ValidFragment")
public class NewTaskDialog  extends DialogFragment{

	 public interface DialogFinishListener {
	        void onFinishDialogAddTask(String inputText);
	    }
	    private DialogFinishListener mParent;
	    private EditText mTaskTitle;

	    public NewTaskDialog(DialogFinishListener parent) {
	        mParent = parent;
//	        setDialogType(DialogType.AlertDialog);
	    }

	    @Override
	    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

	        prepareBuilder(builder);

	        return builder.create();
	    }

	    protected void prepareBuilder(AlertDialog.Builder builder) {
	        View layout = getActivity().getLayoutInflater().inflate(R.layout.new_task_edit, null);

	        mTaskTitle = (EditText) layout.findViewById(R.id.etTaskTitle);

	        builder
	                .setView(layout)
	                .setTitle(getString(R.string.title_task_create))
	                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialogInterface, int i) {
	                        mParent.onFinishDialogAddTask(mTaskTitle.getText().toString());
	                    }
	                })
	                .setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialogInterface, int i) {
	                    }
	                });
	    }
}
