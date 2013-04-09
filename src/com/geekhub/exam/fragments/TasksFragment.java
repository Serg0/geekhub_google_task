package com.geekhub.exam.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.geekhub.exam.R;
import com.geekhub.exam.activities.MainActivity;
import com.geekhub.exam.helpers.TaskListArrayAdapter;
import com.geekhub.exam.helpers.asyncTasks.AsyncAddTask;
import com.geekhub.exam.helpers.asyncTasks.AsyncAddTask.AddTaskCallBack;
import com.geekhub.exam.helpers.asyncTasks.AsyncDeleteTask;
import com.geekhub.exam.helpers.asyncTasks.AsyncLoadTasks;
import com.geekhub.exam.helpers.dialogs.NewTaskDialog;
import com.google.api.services.tasks.model.Task;

public class TasksFragment extends SherlockFragment
					implements NewTaskDialog.DialogFinishListener{
	
	private TaskListArrayAdapter adapter;
	private ListView listView;
	private List<Task> tasks = new ArrayList<Task>();
	private View view;
	private TextView lvFootterView;
	
	private MenuItem add, delete, edit, complete;
	
	public static final String TASKLIST_DEFAULT_NAME = "@default";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.task_list_menu, menu);
		
		add = menu.findItem(R.id.add);
		delete = menu.findItem(R.id.delete);
		edit = menu.findItem(R.id.edit);
		complete =	menu.findItem(R.id.complete);
		
		edit.setVisible(false);
		complete.setVisible(false);
		delete.setVisible(false);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:{
			addNewTask();
			return true;
		}
			
		case R.id.delete:{
			deleteTasksAsync();
			return true;
		}
		case R.id.edit:{

				feachureUnderConstruction();
			return true;
		}
		case R.id.complete:{

				feachureUnderConstruction();
			return true;
		}

		default:
			break;
		}
		
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_list, container, false);
		return  view;
	}

	@Override
	public void onActivityCreated(Bundle savedISnstanceState) {
		super.onActivityCreated(savedISnstanceState);
		initViews();
		loadTaskListAsync();
		
/*		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					loadData();
					updateUi();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		}).start();*/
	}

	


	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			updateActionBar();
			
		}

		
	};
	
	private void updateActionBar() {
		
		int checkedPositions = listView.getCheckedItemPositions().size();
		Log.d(MainActivity.TAG, "checkedPositions = " + checkedPositions);
		if(checkedPositions == 0){
			edit.setVisible(false);
			complete.setVisible(false);
			delete.setVisible(false);
		}else if (checkedPositions == 1){
			edit.setVisible(true);
			complete.setVisible(true); 
			delete.setVisible(true);
		}else if(checkedPositions > 1)
		{
			edit.setVisible(false);
			complete.setVisible(true); 
			delete.setVisible(true);
		}else if(checkedPositions < 0){
			edit.setVisible(false);
			complete.setVisible(false);
			delete.setVisible(false);
		};
		
	}
	
	private void initViews() {
		
		
		lvFootterView = new TextView(getActivity());
		lvFootterView.setText(getString(R.string.message_no_tasks));
		
		listView = (ListView) getView().findViewById(R.id.list_tasts);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(onItemClickListener);
		
	}
	private void updateUi(){
		
		
		if(adapter != null){
			adapter.notifyDataSetChanged();
			Log.d(MainActivity.TAG, "adapter != null");
		}else{
			Log.d(MainActivity.TAG, "adapter == null");
			updateListView();
		}
		
	}
	
	private void updateListView() {
		
			adapter = new TaskListArrayAdapter(getSherlockActivity(), tasks);
			listView.setAdapter(adapter);
			/*getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					

				}
			});*/
			
/*		if((tasks != null)&&(tasks.size() >0)){
			
			listView.removeFooterView(lvFootterView);
		}else{
			
			listView.addFooterView(lvFootterView);
		}*/
		adapter.notifyDataSetChanged();
		updateActionBar();
	}

	public void refreshView() {
		//TODO
	}

	

	



	private void feachureUnderConstruction() {
		Toast.makeText(getActivity(), "Feachure is under constraction!" , Toast.LENGTH_SHORT).show();
		
	}

	private void addNewTask() {
		NewTaskDialog newTaskDialog = new NewTaskDialog(this);
		newTaskDialog.show(getFragmentManager(), getTag());
		
	}

	@Override
	public void onFinishDialogAddTask(String taskName) {
		
		addTaskAsync(taskName);
	}

	private String getCurrentTaskList(){
		//TODO add get current tasklist processing
		return TASKLIST_DEFAULT_NAME;
	}
	
	private List<Task> getChoosenItems(){
		
		SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
		int cntChoice = listView.getCount();
		List<Task> tasks = new ArrayList<Task>();
		for(int i = 0; i < cntChoice; i++){
			 
            if(sparseBooleanArray.get(i)) {

               tasks.add(this.tasks.get(i));

            }
		}
		
		return tasks;
	}
	
	private void loadTaskListAsync() {
		
		AsyncLoadTasks.LoadTasksCallBack callBack = new AsyncLoadTasks.LoadTasksCallBack() {
			
			@Override
			public void getTasks(List<Task> loadedTasks) {
				
				if (loadedTasks != null){
					tasks.addAll(loadedTasks);
					Log.d(MainActivity.TAG, "Tasks loaded" + tasks.size());
					updateUi();
				}
				
			}
		};
		AsyncLoadTasks.run(MainActivity.getInstance(), callBack);
		
	}
	
	private void addTaskAsync(String taskName){
		
		Task task = new Task();
		
		task.setTitle(taskName);
		AddTaskCallBack callBack = new AddTaskCallBack() {
			
			@Override
			public void getTask(Task task) {
//				if(adapter!=null){
//					adapter.add(task);
					tasks.add(task);
					updateUi();
					listView.clearChoices();
					
//				}
				
			}
		};
		
		AsyncAddTask.run(MainActivity.getInstance(), callBack , task);
		
	}
	
	private void deleteTasksAsync() {
		
		AsyncDeleteTask.DeleteTaskCallBack callBack = new AsyncDeleteTask.DeleteTaskCallBack() {
			
			@Override
			public void getTask(List<Task> deletedTasks) {
				tasks.removeAll(deletedTasks);
				updateUi();
			}
		};
		
		AsyncDeleteTask.run(MainActivity.getInstance(), callBack, getChoosenItems());
		
	}
}
