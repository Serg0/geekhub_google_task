package com.geekhub.exam.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.geekhub.exam.R;
import com.geekhub.exam.activities.MainActivity;
import com.geekhub.exam.helpers.asyncTasks.AsyncAddTask;
import com.geekhub.exam.helpers.asyncTasks.AsyncAddTask.AddTaskCallBack;
import com.geekhub.exam.helpers.asyncTasks.AsyncDeleteTask;
import com.geekhub.exam.helpers.dialogs.NewTaskDialog;
import com.google.api.services.tasks.model.Task;

public class TasksFragment extends SherlockFragment
					implements NewTaskDialog.DialogFinishListener{
	View view;
	ArrayAdapter<Task> adapter;
	private ListView listView;
	List<String> result = null;
	private List<Task> tasks;
	private ArrayAdapter<String> resultAdapter;
	
	public static final String TASKLIST_DEFAULT_NAME = "@default";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
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
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					loadData();
					updateUi();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		}).start();
	}

	private void initViews() {
		listView = (ListView) view.findViewById(R.id.list_tasts);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
	}
	void loadData() throws IOException {
		result = new ArrayList<String>();
		tasks = MainActivity.service.tasks().list("@default")
				/*.setFields("items")*/.execute().getItems();
		if (tasks != null) {
			for (Task task : tasks) {
				result.add(task.getTitle());
			}
		} else {
			result.add("No tasks.");
		}

		//		try {
		//			com.google.api.services.tasks.Tasks.Tasklists.List list =	MainActivity.service.tasklists().list();
		//			TaskLists taskLists = list.execute();
		//			GoogleTaskLists tasks1 = new GoogleTaskLists(MainActivity.service);
		//			Log.e("title name",((Integer)tasks1.getTitles().size()).toString());
		//			Log.e("title name",tasks1.getTitles().get(0).toString());
		//			Iterator<String> it = tasks1.getTitles().iterator();
		//			MainActivity.service.tasks().list("@default")
		//					.setFields("items/title").execute().
		//			while(it.hasNext())				
		//				Log.e("title name", it.next());
		//			tasks1.delete(tasks1.getTitles().get(2));
		//			if(tasks1.getGoogleTaskList("Klon")!=null)
		//				Log.e("ID",tasks1.getGoogleTaskList("Klon").getTaskList().getId());
		//		} catch (IOException e) {
		// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}//list("@default")

	}

	private void updateUi() {
		adapter = new ArrayAdapter<Task>(getSherlockActivity(), R.layout.list_menu_item_checkbox, tasks);
//		resultAdapter = new ArrayAdapter<String>(getSherlockActivity(), R.layout.list_menu_item_checkbox, result);
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				listView.setAdapter(adapter);

			}
		});
		
	}

	public void refreshView() {}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.task_list_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:{
			addNewTask();
			return true;
		}
			
		case R.id.delete:{
			deleteTasks();
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

	private void deleteTasks() {
		// TODO Auto-generated method stub
		
		AsyncDeleteTask.DeleteTaskCallBack callBack = new AsyncDeleteTask.DeleteTaskCallBack() {
			
			@Override
			public void getTask(List<Task> deletedTasks) {
				tasks.removeAll(deletedTasks);
				adapter.notifyDataSetChanged();
			}
		};
		
		AsyncDeleteTask.run(MainActivity.getInstance(), callBack, getChoosenItems());
		
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
	
		Task task = new Task();
		
		task.setTitle(taskName);
		AddTaskCallBack callBack = new AddTaskCallBack() {
			
			@Override
			public void getTask(Task task) {
				if(adapter!=null){
					adapter.add(task);
					adapter.notifyDataSetChanged();
					tasks.add(task);
				}
				
			}
		};
		
		AsyncAddTask.run(MainActivity.getInstance(), callBack , task);
		
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
	
}
