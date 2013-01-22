package com.OsMoDroid;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class mesActivity extends Activity implements ResultsListener {
	private ArrayAdapter<String> adapter;
	ArrayList<String> list;
	 EditText toAppText;
	 EditText toUserText;
	 EditText sendText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.meslist);
		  toAppText = (EditText) findViewById(R.id.toAppText);
		  toUserText = (EditText) findViewById(R.id.toUserText);
		  sendText = (EditText) findViewById(R.id.sendText);
		
		Button sendButton = (Button) findViewById(R.id.sendButton);
		sendButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				netutil.newapicommand((ResultsListener)mesActivity.this, "im_send:"+toUserText.getText().toString()+","+toAppText.getText().toString(),sendText.getText().toString());
				
			}
		});
		
		
		 final ListView lv1 = (ListView) findViewById(R.id.meslistView);
	        list = new ArrayList<String>();
	        list.clear();
	      
	       adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, list);
	       lv1.setAdapter(adapter);
	       adapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		 Log.d("mesActivity", "OnDestroy");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		 Log.d("mesActivity", "OnPause");
		 OsMoDroid.activityVisible=false;
		 
	}
	@Override
	protected void onNewIntent (Intent intent){
	super.onNewIntent(intent);
	setIntent(intent);
	 Log.d(this.getClass().getName(), "OnNewIntentExit");
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		OsMoDroid.activityVisible=true;
			Log.d("mesActivity", "OnResume");
			Bundle b=this.getIntent().getExtras();
	if (b!=null){
			list.clear();
	        list.addAll(b.getStringArrayList("meslist")); 
	        Log.d(this.getClass().getName(), "mesList:"+list);
			adapter.notifyDataSetChanged();
	}
	}

	public void onResultsSucceeded(APIComResult result) {
		 Log.d(this.getClass().getName(), "ResultListenr recieve");
		
	}

	

}
