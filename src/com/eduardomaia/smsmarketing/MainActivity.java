package com.eduardomaia.smsmarketing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	static private final int FILE_DIALOG_REQUEST_CODE = 1;
	static private final int SETTINGS_REQUEST_CODE = 2;
	
	private static int mPauseFor=1;
	private static int mAfterSending=1;
	private static String mRandomType="none";
	private static boolean cansend=false;
	private static String mobiles_path="";
	private static String mStatusWait;
	private static String mStatusDeliver;
	// TODO: use a single variable to store how many sms's were sent
	private static int total_messages_sent=0; 
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			
			// LOAD SETTINGS ACTIVITY
			Intent in = new Intent(MainActivity.this, Settings.class);
			
			// LOAD PREFERENCES
			SharedPreferences prefs = getPreferences(MODE_PRIVATE);
			mPauseFor = prefs.getInt("mPauseFor",mPauseFor);
			mAfterSending = prefs.getInt("mAfterSending",mAfterSending);
			mRandomType = prefs.getString("mRandomType",mRandomType);
			
			in.putExtra("pause_for", mPauseFor);
			in.putExtra("after_sending", mAfterSending);
			in.putExtra("random", mRandomType);

			startActivityForResult(in,SETTINGS_REQUEST_CODE);

			return true;
		default:
			return false;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		// OPEN FILE DIALOG
		Button explicitActivationButton = (Button) findViewById(R.id.button1);
		explicitActivationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// CREATE A NEW INTENT TO OPEN FILE DIALOG
				Intent newIntExplicity = new Intent(MainActivity.this, AndroidExplorer.class);
				startActivityForResult(newIntExplicity,FILE_DIALOG_REQUEST_CODE);
			}
		});


		
		// CANCEL DELIVERY
		Button CANCEL = (Button) findViewById(R.id.buttonCancel);
		CANCEL.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			    if (cansend==false)
			    	{
			    	Toast.makeText(getApplicationContext(), getString(R.string.not_delivering), Toast.LENGTH_SHORT).show();
			    	}
			    else
				    {
				    Toast.makeText(getApplicationContext(), getString(R.string.delivery_cancelled), Toast.LENGTH_SHORT).show();
				    
				    mStatusDeliver="";
				    mStatusWait="Canceled!";

				    TextView answerView = (TextView) findViewById(R.id.textViewStatusDeliver);
					answerView.setText(mStatusDeliver);
					
					answerView = (TextView) findViewById(R.id.textViewStatusWait);
					answerView.setText(mStatusWait);
				    }
				cansend=false;
			}
		});
		
		
		// SEND SMS
		Button SEND = (Button) findViewById(R.id.buttonSend);
		SEND.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// check if we have message and telephone list
				TextView telephonesPath = (TextView) findViewById(R.id.textView2);
				TextView message = (TextView) findViewById(R.id.editText1);
				if ( telephonesPath.getText().toString().isEmpty()  )
				{
			        Toast.makeText(getApplicationContext(), getString(R.string.select_telephone_list), Toast.LENGTH_SHORT).show();
					return;
				}
				if ( message.getText().toString().isEmpty()  )
				{
			        Toast.makeText(getApplicationContext(), getString(R.string.type_sms_message), Toast.LENGTH_SHORT).show();
					return;
				}
				
				cansend=true;
				total_messages_sent=0;
				sendBulkSMS();
		        
			}
		});
		
		
	}


	private void sendBulkSMS()
	{
	new Thread(new Runnable() {
			@Override
			public void run() {
				
				TextView telephonesPath = (TextView) findViewById(R.id.textView2);
				TextView message = (TextView) findViewById(R.id.editText1);

				FileInputStream fstream = null;
				try
				{
					fstream = new FileInputStream(telephonesPath.getText().toString());
				} catch (FileNotFoundException e)
				{
					//Could not open telephones file
					runOnUiThread(new Runnable() {
						  public void run() {
						    Toast.makeText(getApplicationContext(), getString(R.string.fail_loading_telephones), Toast.LENGTH_SHORT).show();
						  }
						});
					return;
				}
				
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
				String strLine;

				//Read File Line By Line
				int i=0;
		        
				try 
				{
					while ((strLine = br.readLine()) != null)
					{
						i++;
				        String smsmsg = message.getText().toString();
				        if (mRandomType.equals("phone") )
				        {
				        	smsmsg = smsmsg + strLine;
				        }
				        else if ( mRandomType.equals("random") )
				        {
				        	smsmsg = smsmsg + i;
				        }

				        
						// sending new sms
				        final String pnumber = strLine;
				        final int sms_number=i;
				        
				        runOnUiThread(new Runnable() {
							  public void run() {
							    //Toast.makeText(getApplicationContext(), getString(R.string.sending_sms) + " #" + sms_number + " " + getString(R.string.to) + " " + pnumber + "...", Toast.LENGTH_SHORT).show();
								
								mStatusDeliver=getString(R.string.sending_sms) + " #" + sms_number + " " + getString(R.string.to) + " " + pnumber + "...";
								TextView answerView = (TextView) findViewById(R.id.textViewStatusDeliver);
								answerView.setText(mStatusDeliver);
								
								mStatusWait="";
								answerView = (TextView) findViewById(R.id.textViewStatusWait);
								answerView.setText(mStatusWait);
							  }
							});

				        SendSMS.sendSMS(strLine, smsmsg );
				        total_messages_sent++;
				        
				        
				        // pause... or not
				        if (i % mAfterSending == 0)
				        {
							runOnUiThread(new Runnable() {
								  public void run() {
								    //Toast.makeText(getApplicationContext(), getString(R.string.waiting) + " " + mPauseFor + " " + getString(R.string.secs) + "...", Toast.LENGTH_LONG).show();
									TextView answerView = (TextView) findViewById(R.id.textViewStatusWait);
									mStatusWait=getString(R.string.waiting) + " " + mPauseFor + " " + getString(R.string.secs) + "...";
									answerView.setText(mStatusWait);

								  }
								});
							
				        	try {
								Thread.sleep(mPauseFor * 1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
				        	
				        	// end sleeping
							runOnUiThread(new Runnable() {
								  public void run() {
									mStatusWait="";
									TextView answerView = (TextView) findViewById(R.id.textViewStatusWait);
									answerView.setText(mStatusWait);

								  }
								});

				        }
				        
				        
				        if (!cansend)
				        {
							br.close();
							return;
				        }
				        
					}
					//Close the input stream
					br.close();

					// all messages were sent
					runOnUiThread(new Runnable() {
						  public void run() {
							mStatusWait=getString(R.string.done) + " " + total_messages_sent + " " + getString(R.string.messages_sent);
							TextView answerView = (TextView) findViewById(R.id.textViewStatusWait);
							answerView.setText(mStatusWait);
							
							mStatusDeliver="";
							answerView = (TextView) findViewById(R.id.textViewStatusDeliver);
							answerView.setText(mStatusDeliver);

							cansend=false;
						  }
						});

				}
				catch (IOException e)
				{
					//e.printStackTrace();
			        Toast.makeText(getApplicationContext(), getString(R.string.error_reading_telephone_list), Toast.LENGTH_SHORT).show();
					return;
				}				
			
			

			}
	}).start();
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// RESULT FROM OPEN FILE DIALOG
		if ( requestCode==FILE_DIALOG_REQUEST_CODE && resultCode == RESULT_OK) 
			{
			Bundle extras = data.getExtras();
			String filePath = extras.getString("filePath");
			
			TextView answerView = (TextView) findViewById(R.id.textView2);
			answerView.setText(filePath);
			mobiles_path = filePath;
			}

		// RESULT FOR SETTINGS
		if ( requestCode==SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) 
			{
			Bundle extras = data.getExtras();
			mPauseFor = extras.getInt("pause_for");
			mAfterSending = extras.getInt("after_sending");
			mRandomType = extras.getString("random");
			
			// SET PREFERENCES
			final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt("mPauseFor", mPauseFor);
			editor.putInt("mAfterSending", mAfterSending);
			editor.putString("mRandomType", mRandomType);
			editor.commit();
			}

	}	
	
	

	public void onResume()
	{
		super.onResume();

		TextView answerView = (TextView) findViewById(R.id.textView2);
		answerView.setText(mobiles_path);
		
		answerView = (TextView) findViewById(R.id.textViewStatusWait);
		answerView.setText(mStatusWait);
		
		answerView = (TextView) findViewById(R.id.textViewStatusDeliver);
		answerView.setText(mStatusDeliver);
	}
	
}
