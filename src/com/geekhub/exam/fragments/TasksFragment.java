package com.geekhub.exam.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.geekhub.exam.R;
import com.geekhub.exam.activities.MainActivity;
import com.geekhub.exam.constants.Constants;
import com.geekhub.exam.constants.OperationCodes;
import com.geekhub.exam.helpers.TaskListArrayAdapter;
import com.geekhub.exam.helpers.TaskListArrayAdapter.ListViewCheckedListener;
import com.geekhub.exam.helpers.asyncTasks.AsyncAddTask;
import com.geekhub.exam.helpers.asyncTasks.AsyncAddTask.AddTaskCallBack;
import com.geekhub.exam.helpers.asyncTasks.AsyncDeleteTask;
import com.geekhub.exam.helpers.asyncTasks.AsyncLoadTaskLists;
import com.geekhub.exam.helpers.asyncTasks.AsyncLoadTaskLists.LoadTaskListsCallBack;
import com.geekhub.exam.helpers.asyncTasks.AsyncLoadTasks;
import com.geekhub.exam.helpers.asyncTasks.AsyncUpdateTask;
import com.geekhub.exam.helpers.asyncTasks.CommonAsyncTask.ProgressBar;
import com.geekhub.exam.helpers.dialogs.TaskDialog;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

public class TasksFragment extends SherlockFragment
implements TaskDialog.DialogFinishListener, MainActivity.RefreshCallBack, ProgressBar, ListViewCheckedListener, OnNavigationListener{

	private TaskListArrayAdapter adapter;
	private ListView listView;
	private List<Task> tasks = new ArrayList<Task>();
	private View view;
	private LinearLayout lvFootterView;

	private MenuItem add, delete, refresh;
	private ActionBar actionBar;

	static String ID = Constants.DEFAULT_KEY;
	static TaskList taskList;

	public static Integer currentTaskListNumber = 0;
	public static TaskLists taskLists;
	public static List<String> taskListsTitles = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if(MainActivity.getInstance() !=null)
			MainActivity.getInstance().setRefreshCallBack(this);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.task_list_menu, menu);

		add 		= menu.findItem(R.id.add);
		delete 		= menu.findItem(R.id.delete);
		refresh 	= menu.findItem(R.id.refresh);

		delete.setVisible(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:{
			addNewTaskDialog();
			return true;
		}

		case R.id.delete:{
			deleteTasksAsync();
			return true;
		}

		case R.id.share:{
			performShareAction();
			return true;
		}

		/*case R.id.edit:{

//			editTaskDialog(getChoosenSingleItemPos());
			//				feachureUnderConstruction();
			return true;
		}*/
		/*case R.id.complete:{
			listView.clearChoices();
			adapter.notifyDataSetChanged();
			showToast("listView.clearChoices()");
			return true;
		}*/
		case R.id.refresh:{
			loadTaskListAsync();

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
		getSherlockActivity().getSupportActionBar().setNavigationMode(getSherlockActivity().getSupportActionBar().NAVIGATION_MODE_LIST);

		addDropdownMenu();
		initViews();

		loadTaskListAsync();
		initActionBar();
	}




	private void initActionBar() {
		actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(getCurrentTaskList());


	}


	OnItemLongClickListener onItemLongClickListener =	new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int pos, long arg3) {
			if(tasks.size() >= pos){
				editTaskDialog(pos);
				return true;
			}
			return false;
		}
	};

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			updateActionBar();


		}

	};

	/*private OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			updateActionBar();

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			updateActionBar();

		}
	};*/

	private void updateActionBar() {

		//TODO implement correct check
		int checkedPositions = getChoosenItemsCount();
		Log.d(MainActivity.TAG, "checkedPositions = " + checkedPositions);
		if(checkedPositions > 0){
			delete.setVisible(true);
		}else{
			delete.setVisible(false);
		};

	}

	private void initViews() {


		lvFootterView = (LinearLayout) getView().inflate(getActivity(), R.layout.footter,null);

		listView = (ListView) getView().findViewById(R.id.list_tasts);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(onItemClickListener);
		listView.addFooterView(lvFootterView);
		lvFootterView.setVisibility(View.GONE);

		updateFooterState();
		listView.setOnItemLongClickListener(onItemLongClickListener);
	}
	private void updateUi(){

		//TODO temporary disabled due completed\incompleted representation bug on new task add

		/*if(adapter != null){
			adapter.notifyDataSetChanged();
			Log.d(MainActivity.TAG, "adapter != null");
		}else{
			Log.d(MainActivity.TAG, "adapter == null");*/
		setUpListViewAdapter();
		//		}
		updateFooterState();
		listView.clearChoices();
		updateActionBar();

		adapter.notifyDataSetChanged();

	}

	private void updateFooterState(){

		int footterVisibility = View.GONE;

		if((tasks == null)||(tasks.size() == 0))
			footterVisibility = View.VISIBLE;

		lvFootterView.setVisibility(footterVisibility);
	}
	private void setUpListViewAdapter() {

		adapter = new TaskListArrayAdapter(getSherlockActivity(), tasks, this);
		listView.setAdapter(adapter);

	}





	private void showToast(String toastText) {
		Toast.makeText(getActivity(), toastText , Toast.LENGTH_SHORT).show();

	}

	private void feachureUnderConstruction() {
		Toast.makeText(getActivity(), "Feachure is under constraction!" , Toast.LENGTH_SHORT).show();

	}

	private void addNewTaskDialog() {
		TaskDialog newTaskDialog = new TaskDialog(this, OperationCodes.ADD_TASK);
		newTaskDialog.show(getFragmentManager(), getTag());

	}

	private void editTaskDialog(int taskPos) {

		TaskDialog newTaskDialog = new TaskDialog(this, tasks.get(taskPos), taskPos, OperationCodes.UPDATE_TASK);
		newTaskDialog.show(getFragmentManager(), getTag());

	}

	@Override
	public void onFinishTaskDialog(Task task, int taskPos, int operationCode) {

		switch (operationCode) {
		case OperationCodes.ADD_TASK:
			addTaskAsync(task);
			break;

		case OperationCodes.UPDATE_TASK:
			editTaskUpdate(task, taskPos);
			break;

		default:
			break;
		}


	}

	private String getCurrentTaskList(){
		//TODO add get current tasklist processing
		return ID;
	}

	private List<Task> getChoosenItems(){

		SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
		int cntChoice = listView.getCount();
		List<Task> tasks = new ArrayList<Task>();
		for(int i = 0; i < cntChoice; ++i){

			if(sparseBooleanArray.get(i)) {

				tasks.add(this.tasks.get(i));

			}
		}

		return tasks;
	}

	/*private int  getChoosenSingleItemPos(){

		SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
		int cntChoice = listView.getCount();
		for(int i = 0; i < cntChoice; ++i){

			if(sparseBooleanArray.get(i)) {

				return i;
			}
		}

		return -1;
	}*/
	private int  getChoosenItemsCount(){
		int conut = 0;
		SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
		int cntChoice = listView.getCount();

		for(int i = 0; i < cntChoice; ++i){

			if(sparseBooleanArray.get(i)) {

				conut++;
			}
		}

		return conut;
	}

	private void unchekListView(){

		SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
		int cntChoice = listView.getCount();
		for(int i = 0; i < cntChoice; ++i){

			if(sparseBooleanArray.get(i)) {

				listView.setItemChecked(i, false);

			}
		}

	}


	private void loadTaskListAsync() {

		AsyncLoadTasks.LoadTasksCallBack callBack = new AsyncLoadTasks.LoadTasksCallBack() {

			@Override
			public void getTasks(List<Task> loadedTasks) {

				unchekListView();
				tasks.clear();
				if (loadedTasks != null)
					tasks.addAll(loadedTasks);
				Log.d(MainActivity.TAG, "Tasks loaded" + tasks.size());
				updateUi();

			}
		};

		if(MainActivity.getInstance() !=null)
			AsyncLoadTasks.run(MainActivity.getInstance(), this, callBack, getCurrentTaskList());

	}

	private void addTaskAsync(Task task){

		AddTaskCallBack callBack = new AddTaskCallBack() {

			@Override
			public void getTask(Task task) {
				tasks.add(0, task);
				updateUi();
				listView.clearChoices();


				//				}

			}
		};

		if(MainActivity.getInstance() !=null)
			AsyncAddTask.run(MainActivity.getInstance(), this, callBack, getCurrentTaskList(), task);

	}

	private void deleteTasksAsync() {

		AsyncDeleteTask.DeleteTaskCallBack callBack = new AsyncDeleteTask.DeleteTaskCallBack() {

			@Override
			public void getTask(List<Task> deletedTasks) {
				tasks.removeAll(deletedTasks);
				updateUi();
				updateFooterState();
				unchekListView();
			}
		};

		if(MainActivity.getInstance() !=null)
			AsyncDeleteTask.run(MainActivity.getInstance(), this, callBack, getCurrentTaskList(), getChoosenItems());

	}

	private void editTaskUpdate(Task task, final int taskPos) {

		AsyncUpdateTask.UpdateTaskCallBack callBack = new AsyncUpdateTask.UpdateTaskCallBack() {

			@Override
			public void getTask(Task task) {
				tasks.set(taskPos, task);
				updateUi();
				unchekListView();
			}
		};

		if(MainActivity.getInstance() !=null)
			AsyncUpdateTask.run(MainActivity.getInstance(),this, callBack, getCurrentTaskList(), task);


	}

	@Override
	public void refresh() {

		loadTaskListAsync();

	}

	@Override
	public void accountChanged() {
		//		tasks.clear();
		loadTaskListAsync();
	}

	@Override
	public void onDestroy() {
		if(MainActivity.getInstance() !=null)
			MainActivity.getInstance().removeRefreshCallBack();
		super.onDestroy();
	}

	public void showProgressDialog(boolean show){

		if(show){
			refresh.setActionView(R.layout.progress_bar);
			refresh.expandActionView();
		}else{
			refresh.setActionView(null);
			refresh.collapseActionView();
		}


	}

	@Override
	public void checkStateChanged(Task task, int position, boolean isChecked) {
		if(isChecked){
			task.setStatus(Constants.TASK_COMPLETED_KEY);
		}else{
			task.setStatus(Constants.TASK_NEEDSACTION_KEY);
			task.setCompleted(null);
		}

		editTaskUpdate(task, position);

	}

	private void performShareAction() {

		String shareString = generateShareString();

		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareString);
		startActivity(Intent.createChooser(sharingIntent, "Share TaskList via"));


	}

	private String generateShareString() {
		//TODO move Strings to string.xml
		String completed, incompeted;
		completed = "[v]";
		incompeted = "[ ]";
		String shareString = "";
		shareString +="Task list:";
		shareString +=taskList.getTitle()+"\n\n";

		for(Task task:tasks){
			if(task.getStatus().equals(Constants.TASK_COMPLETED_KEY))
				shareString += completed;
			else
				shareString += incompeted;
			shareString+=" "+task.getTitle() + "\n";
		}
		shareString += "\n"+"published with GoogleTask\n"+"https://github.com/Serg0/geekhub_google_task";
		return shareString;
	}

	private void addDropdownMenu() {
		LoadTaskListsCallBack callBack = new LoadTaskListsCallBack() {

			@Override
			public void getTasks(TasksFragment fragment,TaskLists taskLists, final ArrayAdapter<String> adapter,List<String> taskListsTitles) {

				TasksFragment.taskLists = taskLists;
				TasksFragment.taskListsTitles = taskListsTitles;

				MainActivity.getInstance().getSupportActionBar().setListNavigationCallbacks(adapter, fragment);
				MainActivity.getInstance().getSupportActionBar().setSelectedNavigationItem(currentTaskListNumber);	
				for (TaskList taskList : taskLists.getItems()) {
					if(taskListsTitles.get(currentTaskListNumber) == taskList.getTitle()) {
						TasksFragment.ID = taskList.getId();
						break;
					}
				}
			}
		};

		if(MainActivity.getInstance() !=null)
			AsyncLoadTaskLists.run(MainActivity.getInstance(), this, callBack, this);
		
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		
		currentTaskListNumber = itemPosition;
		
		for (TaskList taskList : taskLists.getItems()) {
			if(taskListsTitles.get(currentTaskListNumber) == taskList.getTitle()) {
				ID = taskList.getId();
				break;
			}		
		}
		
		updateUi();
		refresh();
		
		return false;
		
	}

}
