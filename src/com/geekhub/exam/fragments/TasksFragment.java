package com.geekhub.exam.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.ericharlow.DragNDrop.DragListener;
import com.ericharlow.DragNDrop.DragNDropListView;
import com.ericharlow.DragNDrop.DropListener;
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
import com.geekhub.exam.helpers.asyncTasks.AsyncMoveTask;
import com.geekhub.exam.helpers.asyncTasks.AsyncMoveTask.MoveTaskCallBack;
import com.geekhub.exam.helpers.asyncTasks.AsyncUpdateTask;
import com.geekhub.exam.helpers.asyncTasks.CommonAsyncTask.ProgressBar;
import com.geekhub.exam.helpers.dialogs.TaskDialog;
import com.geekhub.exam.services.UpdateService;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

public class TasksFragment extends SherlockFragment
implements TaskDialog.DialogFinishListener, MainActivity.RefreshCallBack, 
	ProgressBar, ListViewCheckedListener, OnNavigationListener, DropListener, DragListener{

	private TasksFragment fragment;
	private TaskListArrayAdapter adapter;
	private List<Task> 	tasks			= new ArrayList<Task>(),
						completedTasks 	= new ArrayList<Task>();
	private View view;
	private LinearLayout lvFootterView;

	private MenuItem delete, refresh, showAllOrCompleded;
	private ActionBar actionBar;
	private Boolean showAll = true;
	private BroadcastReceiver mReceiver;
	private DragNDropListView listView;

	static String ID = Constants.DEFAULT_KEY;
	String PARAM_STATUS;

	private TaskList taskList;

	public  int currentTaskListNumber = 0;
	private List<TaskList> taskLists;
	private List<String> taskListsTitles = new ArrayList<String>();
	private ArrayAdapter<String> menuAdapter;

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
		
		fragment = this;
		

		initViews();
		initActionBar();
		
		if(MainActivity.getInstance() !=null)
			MainActivity.getInstance().setRefreshCallBack(this);
		
		if(savedISnstanceState == null)
			refresh();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.task_list_menu, menu);

		delete 				= menu.findItem(R.id.delete);
		refresh 			= menu.findItem(R.id.refresh);
		showAllOrCompleded 	= menu.findItem(R.id.showAllOrCompleded);

		delete.setVisible(false);
	}
	
	@Override
	public void onResume() {
			runUpdateService();
		super.onResume();
	}
	
	@Override
	public void onPause() {
			getSherlockActivity().unregisterReceiver(mReceiver);
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		
		if(MainActivity.getInstance() !=null)
			MainActivity.getInstance().removeRefreshCallBack();
		
		super.onDestroy();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.add: {
			addNewTaskDialog();
			return true;
		}

		case R.id.delete: {
			deleteTasksAsync();
			return true;
		}

		case R.id.share: {
			performShareAction();
			return true;
		}

		case R.id.clear_completed: {
			clearCompletedTasks();
			return true;
		}

		case R.id.showAllOrCompleded: {
			showAllOrCompleded();
			return true;
		}

		case R.id.refresh: {
			refresh();
			return true;
		}

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}




	private void showAllOrCompleded() {

		if((showAll)&&(completedTasks.size() == 0)){
			Toast.makeText(getActivity(), getString(R.string.no_completed_tasks), Toast.LENGTH_LONG).show();
			return;
		}

		showAll  = !showAll;

		updateShowAllOrCompletedButton();

		updateUi();

	}

	private void updateShowAllOrCompletedButton(){

		if (showAllOrCompleded != null)
			if (!showAll)
				showAllOrCompleded.setTitle(getString(R.string.show_all));
			else
				showAllOrCompleded.setTitle(getString(R.string.show_completed));
	}

	private void clearCompletedTasks() {

		deleteTasksAsync(completedTasks);
	}

	



	private void initActionBar() {
		
		actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setTitle(getString(R.string.app_name));
		
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
	

	private void updateActionBar() {

		int checkedPositions = getChoosenItemsCount();
		Log.d(MainActivity.TAG, "checkedPositions = " + checkedPositions);
		if(checkedPositions > 0){
			delete.setVisible(true);
		}else{
			delete.setVisible(false);
		};

	}

	private void initViews() {


		lvFootterView = (LinearLayout) LinearLayout.inflate(getActivity(), R.layout.footter,null);

		listView = (DragNDropListView) getView().findViewById(R.id.list_tasts);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(onItemClickListener);
		listView.addFooterView(lvFootterView);
		listView.setDropListener(this);
		listView.setDragListener(this);
		listView.setFooterDividersEnabled(false);
		lvFootterView.setVisibility(View.GONE);

		updateFooterState();
		listView.setOnItemLongClickListener(onItemLongClickListener);
	}
	
	private void updateUi(){

		setUpListViewAdapter();
		
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

		if(showAll){
			adapter = new TaskListArrayAdapter(getSherlockActivity(), tasks, this, true);
			
		}else
		{
			adapter = new TaskListArrayAdapter(getSherlockActivity(), completedTasks, this, false);
		}

		listView.setDragModeEnabled(showAll);
		listView.setAdapter(adapter);

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
				
				/*if(loadedTasks == null)
						return;*/
				unchekListView();
				tasks.clear();
				completedTasks.clear();
				if (loadedTasks != null){
					tasks.addAll(loadedTasks);
					for(Task task:tasks){
						if(task.getStatus().equals(Constants.TASK_COMPLETED_KEY))
							completedTasks.add(task);
					}

				}else{}

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
				
				if(task == null)
					return;
				
				tasks.add(0, task);
				updateUi();
				listView.clearChoices();


			}
		};

		if(MainActivity.getInstance() !=null)
			AsyncAddTask.run(MainActivity.getInstance(), this, callBack, getCurrentTaskList(), task);

	}

	private void deleteTasksAsync(){

		deleteTasksAsync(getChoosenItems());

	}
	private void deleteTasksAsync(List<Task> tasksToDelete) {

		AsyncDeleteTask.DeleteTaskCallBack callBack = new AsyncDeleteTask.DeleteTaskCallBack() {

			@Override
			public void getTask(List<Task> deletedTasks) {
				
				if(deletedTasks == null)
					return;
				
				tasks.removeAll(deletedTasks);
				completedTasks.removeAll(deletedTasks);

				if(!showAll){
					showAllOrCompleded();
				}else
				{
					updateUi();
					updateFooterState();
					unchekListView();
				}

			}
		};

		if(MainActivity.getInstance() !=null)
			AsyncDeleteTask.run(MainActivity.getInstance(), this, callBack, getCurrentTaskList(), tasksToDelete);

	}

	private void editTaskUpdate(Task task, final int taskPos) {

		AsyncUpdateTask.UpdateTaskCallBack callBack = new AsyncUpdateTask.UpdateTaskCallBack() {

			@Override
			public void getTask(Task task) {
				
				if(task == null)
					return;
				
				tasks.set(taskPos, task);
				if(task.getStatus().equals(Constants.TASK_COMPLETED_KEY))
					completedTasks.add(task);
				else
					completedTasks.remove(task);
				
				updateUi();
				unchekListView();
			}
		};

		if(MainActivity.getInstance() !=null)
			AsyncUpdateTask.run(MainActivity.getInstance(),this, callBack, getCurrentTaskList(), task);


	}

	@Override
	public void refresh() {
		addDropdownMenu();
//		loadTaskListAsync();

	}

	@Override
	public void accountChanged() {
		refresh();
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
		if(taskList != null)
			shareString +=taskList.getTitle();

		shareString +="\n\n";

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
			public void loadTaskLists(TaskLists localTaskLists) {

				if((localTaskLists	!= null)&&(localTaskLists.getItems() != null))
					taskLists = localTaskLists.getItems();
				

				if ((taskLists == null)||(taskLists.size() == 0)){
					taskLists = new ArrayList<TaskList>();
					TaskList defaultTask = new TaskList();
					defaultTask.setTitle(Constants.DEFAULT_KEY);
					defaultTask.setId(Constants.DEFAULT_KEY);
					taskLists.add(defaultTask);
				}
				
					taskListsTitles.clear();
				
				for (TaskList taskList : taskLists) {
					taskListsTitles.add(taskList.getTitle().toString());
				}
					menuAdapter = new ArrayAdapter<String>(MainActivity.getInstance(),	android.R.layout.simple_list_item_1, taskListsTitles);
					MainActivity.getInstance().getSupportActionBar().setListNavigationCallbacks(menuAdapter, fragment);
					/*MainActivity.getInstance().getSupportActionBar().setSelectedNavigationItem(currentTaskListNumber);	
					for (TaskList taskList : taskLists) {
						if(taskListsTitles.get(currentTaskListNumber) == taskList.getTitle()) {
							TasksFragment.ID = taskList.getId();
							break;
						}
					}*/
				}
		};

		if(MainActivity.getInstance() !=null)
			AsyncLoadTaskLists.run(MainActivity.getInstance(), this, callBack);

	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {

		currentTaskListNumber = itemPosition;

		for (TaskList taskList : taskLists) {
			if(taskListsTitles.get(currentTaskListNumber) == taskList.getTitle()) {
				ID = taskList.getId();
				this.taskList = taskList;
				break;
			}		
		}

		loadTaskListAsync();

		return false;

	}
	
	private void runUpdateService() {
		IntentFilter intentFilter = new IntentFilter(Constants.BROADCAST_ACTION);
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String message = intent.getStringExtra(Constants.BROADCAST_TYPE);
				if(message.equals(Constants.BROADCAST_MESSAGE)) {
					
					refresh();
				}
			}
		};
		
		if(getSherlockActivity() != null){
			getSherlockActivity().registerReceiver(mReceiver, intentFilter);
			Intent intent = new Intent(getSherlockActivity(), UpdateService.class);
			getSherlockActivity().startService(intent);
		}
	}

	@Override
	public void onDrop(int from, int to) {
		
		Task task, taskPrevious = null;
		Log.d(MainActivity.TAG, "from " + from +" to "+ to);
		if((tasks == null)||(tasks.size() == 0))
			return;
		
		task = tasks.get(from);

		tasks.remove(from);
		tasks.add(to,task);
		
		if(to>0&&to<=tasks.size())
			taskPrevious = tasks.get(to-1);
		
		moveAsyncTask(task, taskPrevious);
		
		updateUi();
		
	}

	@Override
	public void onStartDrag(View itemView) {
		itemView.setBackgroundColor(getResources().getColor(R.color.selected));
		
	}

	@Override
	public void onDrag(int x, int y, ListView listView) {
		
	}

	@Override
	public void onStopDrag(View itemView) {
		
		itemView.setBackgroundColor(getResources().getColor(R.color.unselected));
	}

	private void moveAsyncTask(Task task, Task taskPrevious){

		MoveTaskCallBack callBack = new MoveTaskCallBack() {

			@Override
			public void getTask(Task task) {
				
				listView.clearChoices();

			}
		};

		if(MainActivity.getInstance() !=null)
			AsyncMoveTask.run(MainActivity.getInstance(), this, callBack, getCurrentTaskList(), task, taskPrevious);

	}
}

