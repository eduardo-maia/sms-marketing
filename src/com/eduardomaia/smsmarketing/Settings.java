package com.eduardomaia.smsmarketing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends Activity {
	
	private boolean settings_validate()
	{
		TextView TextViewPauseFor = (TextView) findViewById(R.id.editTextPauseFor);
		if (TextViewPauseFor.getText().toString().equals("") || Integer.parseInt(TextViewPauseFor.getText().toString()) <= 0)
			{
			Toast.makeText(getApplicationContext(), getString(R.string.greater_than_zero), Toast.LENGTH_LONG).show();
			return false;
			}

		TextView TextViewAfterSending = (TextView) findViewById(R.id.editTextAfterSending);
		if (TextViewAfterSending.getText().toString().equals("") || Integer.parseInt(TextViewAfterSending.getText().toString()) <= 0)
			{
			Toast.makeText(getApplicationContext(), getString(R.string.greater_than_zero), Toast.LENGTH_LONG).show();
			return false;
			}

		return true;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);

		
		TextView TextViewPauseFor = (TextView) findViewById(R.id.editTextPauseFor);
		TextViewPauseFor.setText(getIntent().getIntExtra("pause_for",10)+"");

		TextView TextViewAfter = (TextView) findViewById(R.id.editTextAfterSending);
		TextViewAfter.setText(getIntent().getIntExtra("after_sending",10)+"");
		
		String checkme = getIntent().getStringExtra("random");
		RadioButton rb= (RadioButton) findViewById(R.id.radio0);
		if (checkme.equals("none"))
		{
			rb = (RadioButton) findViewById(R.id.radio0);
		}
		else if (checkme.equals("phone"))
		{
			rb = (RadioButton) findViewById(R.id.radio1);
		}
		else if (checkme.equals("random"))
		{
			rb = (RadioButton) findViewById(R.id.radio2);
		}
		rb.setChecked(true);
		
		
		Button SAVE = (Button) findViewById(R.id.buttonSave);
		SAVE.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { // finalize activity

				Intent i = new Intent(getApplicationContext(),Settings.class);

				if (!settings_validate())
				{
					return;
				}
				
				// pause for
				TextView TextViewPauseFor = (TextView) findViewById(R.id.editTextPauseFor);
				i.putExtra("pause_for",Integer.parseInt(TextViewPauseFor.getText().toString()) );
				
				// after sending
				TextView TextViewAfterSending = (TextView) findViewById(R.id.editTextAfterSending);
				i.putExtra("after_sending",Integer.parseInt(TextViewAfterSending.getText().toString()) );

				// random type
				RadioGroup RadioRandomType = (RadioGroup) findViewById(R.id.radioGroup1);
				RadioButton radioButtonResult = (RadioButton) findViewById(RadioRandomType.getCheckedRadioButtonId());
				String randomResult="";
				if (radioButtonResult.getId() == R.id.radio0)
				{
					randomResult="none";
				}
				else if (radioButtonResult.getId() == R.id.radio1)
				{
					randomResult="phone";
				}
				else if (radioButtonResult.getId() == R.id.radio2)
				{
					randomResult="random";
				}
				
				i.putExtra("random", randomResult );
				
				setResult(RESULT_OK, i);
				finish(); 

			}
		});
		

	}
}
