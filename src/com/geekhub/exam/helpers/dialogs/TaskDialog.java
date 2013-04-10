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
public class TaskDialog  extends DialogFragment{

	 public interface DialogFinishListener {
	        void onFinishDialogAddTask(Task task);
	    }
	    private DialogFinishListener mParent;
	    private EditText 
	    			mTaskTitle,
	    			mTaskDesc;
	    private Task task;

	    public TaskDialog(DialogFinishListener parent, Task task) {
	        mParent = parent;
	        this.task = task;
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
	        mTaskDesc  = (EditText) layout.findViewById(R.id.etTaskDesc);
	        
	        String dialogTitle;
	        if(task != null){
	        	String title = task.getTitle();
	        	String desc = task.getNotes();
	        	if(title!=null)
	        		mTaskTitle.setText(title);
	        	if(desc!=null)
	        		mTaskDesc.setText(desc);
	        	
	        	dialogTitle = getString(R.string.title_task_edit);
	        }else{
	        	task = new Task();
	        	dialogTitle = getString(R.string.title_task_create);
	        }
	        
	        builder
	                .setView(layout)
	                .setTitle(dialogTitle)
	                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialogInterface, int i) {
	                    	task.setTitle(mTaskTitle.getText().toString());
	                    	task.setNotes(mTaskDesc.getText().toString());
	                    	if(mParent!=null)
	                    		mParent.onFinishDialogAddTask(task);
	                    }
	                })
	                .setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialogInterface, int i) {
	                    }
	                });
	    }
}
