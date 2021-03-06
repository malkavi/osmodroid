package com.OsMoDroid;

import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ChannelsFragment extends SherlockFragment implements ResultsListener {
	private GPSLocalServiceClient globalActivity;
	//private ListView lv1;
	protected String canalid;
	private String u;
	 int channelpos=-1;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		globalActivity=(GPSLocalServiceClient)getSherlockActivity();
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		globalActivity.actionBar.setTitle(R.string.chanals);
		if(channelpos!=-1){
			openChannelChat(channelpos);
			
		}
		super.onResume();
	}

	@Override
	public void onDetach() {
		globalActivity=null;
		
		super.onDetach();
	}

	@Override
	public void onPause() {
		channelpos=-1;
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		  final AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
		  if (item.getItemId() == 1) {
			  LinearLayout layout = new LinearLayout(getSherlockActivity());
			  layout.setOrientation(LinearLayout.VERTICAL);
			  final TextView txv = new TextView(getSherlockActivity());
			  txv.setText(R.string.yourmessage);
			  layout.addView(txv);
			  final EditText input = new EditText(getSherlockActivity());
			  layout.addView(input);
			  AlertDialog alertdialog3 = new AlertDialog.Builder(getSherlockActivity())
						.setTitle(R.string.sendingmessage)
						.setView(layout)
						.setPositiveButton(R.string.send,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,	int whichButton) {
										if (!(input.getText().toString().equals(""))) {
											JSONObject postjson = new JSONObject();
											try {
											postjson.put("text", input.getText().toString());
											postjson.put("channel", LocalService.channelList.get((int) acmi.id).u);
											postjson.put("device", OsMoDroid.settings.getString("device", ""));
											Netutil.newapicommand(ChannelsFragment.this, "om_channel_chat_post","json="+postjson.toString());
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
									}
								})
						.setNegativeButton(R.string.cancel,	new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int whichButton) 
							{
										}
								}).create();
				alertdialog3.show();
				return true;
		    }
		  if (item.getItemId() == 2) {
			  openChannelChat(LocalService.channelList.get((int) acmi.id).u);
			    return true;

		  }
		  if (item.getItemId() == 3) {
			  ClipboardManager clipboard = (ClipboardManager) getSherlockActivity().getSystemService(Context.CLIPBOARD_SERVICE);
			  if (LocalService.channelList.get((int) acmi.id).url != null){
				  clipboard.setText(LocalService.channelList.get((int) acmi.id).url);
			  }
			  return true;
		  }
		  
		  if (item.getItemId() == 4) {
		  	Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, LocalService.channelList.get((int) acmi.id).url != null);
            startActivity(Intent.createChooser(sendIntent, getSherlockActivity().getString(R.string.sharelink)));
            return true;
		  }
		  
		  if (item.getItemId() == 5) {
			  Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(LocalService.channelList.get((int) acmi.id).url));
			  startActivity(browseIntent);
			  return true;
		  }
		  if (item.getItemId() == 6) {
			  Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(LocalService.channelList.get((int) acmi.id).url));
			  Netutil.newapicommand(ChannelsFragment.this, "om_channel_out:"+OsMoDroid.settings.getString("device", "")+","+LocalService.channelList.get((int) acmi.id).u);
			  u=Integer.toString(LocalService.channelList.get((int) acmi.id).u);
			  return true;
		  }
		  return super.onContextItemSelected(item);
		}

	void openChannelChat(int i) {
		
			  globalActivity.drawClickListener.chandev=new ChannelDevicesFragment();
		  
		  
		    Bundle bundle = new Bundle();
		    bundle.putInt("channelpos",i);
		    globalActivity.drawClickListener.chandev.setArguments(bundle);
		    globalActivity.showFragment(globalActivity.drawClickListener.chandev,true);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
			menu.add(0, 2, 1, R.string.deviceslis).setIcon(android.R.drawable.ic_menu_delete);
		    menu.add(0, 1, 2, R.string.messagetochanal).setIcon(android.R.drawable.ic_menu_share);
		    menu.add(0, 3, 3, R.string.copylink).setIcon(android.R.drawable.ic_menu_edit);
		    menu.add(0, 4, 4, R.string.sharelink).setIcon(android.R.drawable.ic_menu_edit);
		    menu.add(0, 5, 5, R.string.openinbrowser).setIcon(android.R.drawable.ic_menu_edit);
		    menu.add(0, 6, 6, R.string.exitfromchanal).setIcon(android.R.drawable.ic_menu_edit);
		super.onCreateContextMenu(menu, v, menuInfo);
	}


	

	
	 @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setHasOptionsMenu(true);
         //setRetainInstance(true);
         super.onCreate(savedInstanceState);
     }
	
	@Override
	public void onAttach(Activity activity) {
		globalActivity = (GPSLocalServiceClient)activity;// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com.actionbarsherlock.view.Menu, com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		 MenuItem refresh = menu.add(0, 3, 0, R.string.refresh);
		 refresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		 refresh.setIcon(android.R.drawable.ic_menu_rotate); 
		 MenuItem createchannel = menu.add(0, 1, 0, R.string.createchanel);
		 createchannel.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		 createchannel.setIcon(android.R.drawable.ic_menu_add);
		 MenuItem enterchannel = menu.add(0, 2, 0, R.string.enterchanal);
		 enterchannel.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		 enterchannel.setIcon(android.R.drawable.ic_menu_agenda);
		 super.onCreateOptionsMenu(menu, inflater);
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onPrepareOptionsMenu(com.actionbarsherlock.view.Menu)
	 */
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(com.actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			LinearLayout layout = new LinearLayout(globalActivity);
			layout.setOrientation(LinearLayout.VERTICAL);
			final TextView txv3 = new TextView(globalActivity);
			txv3.setText(R.string.chanalname);
			layout.addView(txv3);
			final EditText input2 = new EditText(globalActivity);
			layout.addView(input2);
			final TextView txv1 = new TextView(globalActivity);
			txv1.setText(R.string.chanalcode);
			layout.addView(txv1);
			final EditText input = new EditText(globalActivity);
			layout.addView(input);
			final CheckBox chb1 = new CheckBox(globalActivity);
			chb1.setText(R.string.privatechanal);
			layout.addView(chb1);
			final TextView txv2 = new TextView(globalActivity);
			txv2.setText(R.string.chanalkey);
			txv2.setEnabled(false);
			layout.addView(txv2);
			final EditText input1 = new EditText(globalActivity);
			input1.setText(R.string.chanalkey);
			input1.setEnabled(false);
			layout.addView(input1);

			chb1.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
				if (isChecked){
					input1.setEnabled(true);}
				if (!isChecked){
					input1.setEnabled(false);}
				}
				});
			AlertDialog alertdialog4 = new AlertDialog.Builder(globalActivity)
					.setTitle(R.string.createchanal)
					.setView(layout)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									canalid = input.getText().toString();
									String canalkey = input1.getText().toString();
									String canalname = input2.getText().toString();
									if ( !(canalid.equals("")) && !(canalname.equals("")) && ( chb1.isChecked()&&(!(canalkey.equals(""))) || !chb1.isChecked() )  )
									{
										try {
										JSONObject postjson = new JSONObject();
										postjson.put("name", Uri.encode(input2.getText().toString()));
										postjson.put("code", Uri.encode(input.getText().toString()));
										postjson.put("private", chb1.isChecked() ? "1":"0");
										Netutil.newapicommand(ChannelsFragment.this, "om_channel_add","json="+postjson.toString());
										}
										catch (Exception e) {
											e.printStackTrace();
										}
									} else {
										Toast.makeText(
												globalActivity,
												R.string.noallenter, 5).show();
									}
									}
									})
					.setNegativeButton(R.string.No,
							new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog,int whichButton)
							{}
							}).create();
				alertdialog4.show();
			}
        
        if (item.getItemId() == 2) {
			if (!(OsMoDroid.settings.getString("device", "").equals(""))) {
			LinearLayout layout = new LinearLayout(globalActivity);
			layout.setOrientation(LinearLayout.VERTICAL);
			final TextView txv1 = new TextView(globalActivity);
			txv1.setText(R.string.chanalcode);
			layout.addView(txv1);
			final EditText input = new EditText(globalActivity);
			layout.addView(input);
			AlertDialog alertdialog4 = new AlertDialog.Builder(
					globalActivity)
					.setTitle(R.string.bindtochanal)
					.setView(layout)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									 canalid = Uri.encode(input.getText().toString());
									if (!(canalid.equals("")))
									{
										
										Netutil.newapicommand((ResultsListener)ChannelsFragment.this,(Context)getSherlockActivity(), "om_channel_enter:"+OsMoDroid.settings.getString("device", "")+","+canalid);
									} else {
											Toast.makeText(
											globalActivity,
											R.string.noallenter, 5).show();
									}
								}
							})
					.setNegativeButton(R.string.No,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create();
			alertdialog4.show();
			}else 
				{
				Toast.makeText(globalActivity,R.string.requestdevicebefore, 5).show();
				}
        	}
        if (item.getItemId() == 3) 
        	{
        		Netutil.newapicommand((ResultsListener)LocalService.serContext, (Context)getSherlockActivity(), "om_device_channel_adaptive:"+OsMoDroid.settings.getString("device", ""));
        	}
        return super.onOptionsItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		   if(bundle != null){
		      channelpos = bundle.getInt("channelpos", -1);
		   
		   }
		LocalService.channelsAdapter.context=getSherlockActivity();
		View view=inflater.inflate(R.layout.mychannels, container, false);
		ListView lv1 = (ListView) view.findViewById(R.id.mychannelslistView);
		lv1.setAdapter(LocalService.channelsAdapter);
		registerForContextMenu(lv1);
	    if (LocalService.channelsAdapter!=null)
	    	{
	    		LocalService.channelsAdapter.notifyDataSetChanged();
	    	}
	    registerForContextMenu(lv1);
	    lv1.setOnItemClickListener(new OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
	    	{
	    		arg0.showContextMenuForChild(arg1);
	    	}
	    	});
	    	if(LocalService.channelList.size()==0)
	    	{
	    		Netutil.newapicommand((ResultsListener)LocalService.serContext,(Context)getSherlockActivity(), "om_device_channel_adaptive:"+OsMoDroid.settings.getString("device", ""));
	    	}
	    	return view;
		}
	@Override
	public void onResultsSucceeded(APIComResult result) {
		Log.d(getClass().getSimpleName(),"OnResultListener Command:"+result.Command+",Jo="+result.Jo);
		if (!(result.Jo==null)  ) {
			Toast.makeText(getSherlockActivity(),result.Jo.optString("state")+" "+ result.Jo.optString("error_description"),5).show();
		if (result.Jo.has("om_channel_add"))
			{	
			try {
					JSONObject js = new JSONObject(result.post.replace("json=", ""));
					Netutil.newapicommand((ResultsListener)ChannelsFragment.this,(Context)getSherlockActivity(),"om_channel_enter:"+OsMoDroid.settings.getString("device", "")+","+js.getString("code"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		if (result.Jo.has("om_channel_enter:"+OsMoDroid.settings.getString("device", "")+","+canalid))
		{
			Netutil.newapicommand((ResultsListener)LocalService.serContext,(Context)getSherlockActivity(), "om_device_channel_adaptive:"+OsMoDroid.settings.getString("device", ""));
		}
		if (result.Jo.has("om_channel_out:"+OsMoDroid.settings.getString("device", "")+","+u))
		{
			Netutil.newapicommand((ResultsListener)LocalService.serContext,(Context)getSherlockActivity(), "om_device_channel_adaptive:"+OsMoDroid.settings.getString("device", ""));
		}
		}
	}
}
