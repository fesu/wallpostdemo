package com.example.wallpostdemo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class UserRegisterActivity extends Activity implements OnClickListener {

	private int year;
	private int month;
	private int day;
	public ArrayAdapter<CharSequence> adptrcontries;
	static final int DATE_DIALOG_ID = 999;
	
	private EditText edt_signup_birthdate,edt_signup_email,edt_signup_username,edt_signup_password,edt_signup_conpassword,edt_signup_contact;	
	private Spinner spnr_countries;
	private RadioGroup rbtng_gender;
	private Button btn_signup;
	
	private String email,username,password,conpassword,gender,birthdate,country,contact;
	
	private JSONArray jArray = null;
	private String result = null;
	private String insertresult = "{\"token\":\"Abcxyy\"}";
	private StringBuilder sb = null;
	private InputStream is = null;
	private String ct_name;
	
	// Server return Codes
	private int insertcode;
	
	//To Check USer Exist
	String unamecheckwith,unamefromdb;
	
	
	// Weblink path for webservices
    private String webservicePath = "http://amberdecorators.in/iloveit-webpage/";
    private String webserviceName = "";
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* Progress bar on ActionBar*/
        getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
		setContentView(R.layout.activity_user_register);
				
		getActionBar().setTitle("Signup");
		
		InitializeAllControls();
		
		
		adptrcontries = ArrayAdapter.createFromResource(
	            this, R.array.countries_array, android.R.layout.simple_spinner_item);
	    adptrcontries.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spnr_countries.setAdapter(adptrcontries);
	    		
		spnr_countries.setSelection(adptrcontries.getPosition("India"));
		
		String country = null;
		//country = getApplicationContext().getResources().getConfiguration().locale.getDisplayCountry();
		//country = TelephonyManager.getNetworkCountryIso().toString();
		TelephonyManager teleMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		 if (teleMgr != null){
		     country = teleMgr.getNetworkCountryIso();
		 }		 
		 
		//edt_signup_email.setText(country);
		SetBirthDatePicker();
		GetGoogleEmail();
		//new LoadUsernameTask().execute();
		
		btn_signup.setOnClickListener(this);	
		SetOnFocusChangedEvent();
	}
	
	
	
	
	private DatePickerDialog.OnDateSetListener datePickerListener 
			= new DatePickerDialog.OnDateSetListener() {

			// 		when dialog box is closed, below method will be called.
			public void onDateSet(DatePicker view, int selectedYear,
					int selectedMonth, int selectedDay) {
					year = selectedYear;
					month = selectedMonth;
					day = selectedDay;

					// 		set selected date into textview
					edt_signup_birthdate.setText(new StringBuilder().append(month + 1)
						.append("-").append(day).append("-").append(year)
							.append(" "));

					// 	set selected date into datepicker also
					//dpResult.init(year, month, day, null);

			}

			
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_register, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	private void SetBirthDatePicker()
	{
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		// set current date into textview
		/*edt_signup_birthdate.setText(new StringBuilder()
					// Month is 0 based, just add 1
					.append(month + 1).append("-").append(day).append("-")
					.append(year).append(" "));*/
		
		edt_signup_birthdate.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus)
				{	
					DatePickerDialog datePickerDialog = new DatePickerDialog(UserRegisterActivity.this, datePickerListener, year, month, day);
					datePickerDialog.show();
				    // Get the current date
				    //datePickerDialog.updateDate(year, month, day);
				}
			}
		});
	
	}
	
	
	private void GetGoogleEmail()
	{
		
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(this).getAccounts();
		
		
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                UserRegisterActivity.this);
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select Email..");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
        		UserRegisterActivity.this,
                android.R.layout.select_dialog_singlechoice);
        
        String possibleEmail = "";
        String possibleEmail2 = "";
        for (Account account : accounts) {
		    if (emailPattern.matcher(account.name).matches()) {
		        possibleEmail = account.name;	        
		        
		        if(possibleEmail != possibleEmail2)
		        {
		        	arrayAdapter.add(possibleEmail);
		        }
			    		        

		        possibleEmail2 = possibleEmail;
		        //emailstr[i] = account.name;
		        //i++;
		        //edt_signup_email.setText(possibleEmail);
		    }
		}	
      
        builderSingle.setNegativeButton("Add Other Account",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                 
        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        edt_signup_email.setText(strName);
                    }
                });
        builderSingle.show();
        
		/*ArrayList<String> emaillist = new ArrayList<String>();
		ArrayAdapter<String> emailadptr;
		
		String[] emailstr = new String[]{"asasasas","asdsadasd"};		
			
		AlertDialog.Builder builder = new AlertDialog.Builder(UserRegisterActivity.this);
		builder.setTitle("Select Account");
		
		//builder.setMessage("Image has been saved in \n \"/Dial24hrs_Inquiry\" folder. Click on notification to open Image..");

		View mView = View.inflate(this, R.layout.email_box_layout, null);
		ListView lvemail = (ListView) mView.findViewById(R.id.lv_email_list);
		//lvemail.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(this).getAccounts();
		
		for (Account account : accounts) {
		    if (emailPattern.matcher(account.name).matches()) {
		        String possibleEmail = account.name;
		        emaillist.add(account.name);
		        //emailstr[i] = account.name;
		        //i++;
		        edt_signup_email.setText(possibleEmail);
		    }
		}	
		emaillist.add("Select other Account");
		emailadptr = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_checked, emailstr);
		//setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked,cars));
		lvemail.setAdapter(emailadptr);
		builder.setView(mView);
		
		builder.show();
	*/	
		
	}
	
	private void TestDB()
	{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//http post
		try{
		     HttpClient httpclient = new DefaultHttpClient();

		     //Why to use 10.0.2.2
		     HttpPost httppost = new HttpPost("http://amberdecorators.in/iloveit-webpage/testdb.php");
		     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		     HttpResponse response = httpclient.execute(httppost);
		     HttpEntity entity = response.getEntity();
		     is = entity.getContent();
		     }catch(Exception e){
		         Log.e("log_tag", "Error in http connection"+e.toString());
		    }
		//convert response to string
		try{
		      BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		       sb = new StringBuilder();
		       sb.append(reader.readLine() + "\n");

		       String line="0";
		       while ((line = reader.readLine()) != null) {
		                      sb.append(line + "\n");
		        }
		        is.close();
		        result=sb.toString();
		        }catch(Exception e){
		              Log.e("log_tag", "Error converting result "+e.toString());
		        }

		String name;
		try{
		      jArray = new JSONArray(result);
		      JSONObject json_data=null;
		      for(int i=0;i<jArray.length();i++){
		             json_data = jArray.getJSONObject(i);
		             ct_name=json_data.getString("username");//here "username" is the column name in database
		         }
		      }
		      catch(JSONException e1){
		       Toast.makeText(getBaseContext(), "No Data Found" ,Toast.LENGTH_LONG).show();
		      } catch (ParseException e1) {
		   e1.printStackTrace();
		 }
		}
	
	private void InitializeAllControls()
	{
		edt_signup_birthdate = (EditText)findViewById(R.id.edt_signup_birthdate);
		edt_signup_email = (EditText)findViewById(R.id.edt_signup_email);
		edt_signup_username = (EditText)findViewById(R.id.edt_signup_username);
		edt_signup_contact = (EditText)findViewById(R.id.edt_signup_contact);
		edt_signup_password = (EditText)findViewById(R.id.edt_signup_password);
		edt_signup_conpassword = (EditText)findViewById(R.id.edt_signup_confirm_password);
		
		spnr_countries = (Spinner)findViewById(R.id.spnr_signup_country);
		
		rbtng_gender = (RadioGroup)findViewById(R.id.rbtng_gender);
		
		btn_signup = (Button)findViewById(R.id.btn_signup);
		
		((RadioButton)this.findViewById(R.id.rbtn_signup_male)).setChecked(true);
		
		edt_signup_username.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
		
		
		 
	}
	
	private void SetRegisterUserManualyData()
	{		
		email = edt_signup_email.getText().toString();
		username = edt_signup_username.getText().toString().trim().toLowerCase();
		password = edt_signup_password.getText().toString();
		
		//int rbtncheckedid = rbtng_gender.getCheckedRadioButtonId();
		//View rbtn = rbtng_gender.findViewById(rbtng_gender.getCheckedRadioButtonId());
		int idx = rbtng_gender.indexOfChild(rbtng_gender.findViewById(rbtng_gender.getCheckedRadioButtonId()));
		if(idx == 0)
		{
			gender = "Male";
		}
		else
		{
			gender = "Female";
		}				
		
		//gender = ((RadioButton)this.findViewById(rbtng_gender.getCheckedRadioButtonId())).getText().toString();
		//gender = "Male";
		birthdate = edt_signup_birthdate.getText().toString();
		country = spnr_countries.getSelectedItem().toString();
		contact = edt_signup_contact.getText().toString();
	}
	
	private void RegisterUserManualy()
	{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		 
	   	nameValuePairs.add(new BasicNameValuePair("email",email));
	   	nameValuePairs.add(new BasicNameValuePair("username",username));
	   	nameValuePairs.add(new BasicNameValuePair("password",password));
	   	nameValuePairs.add(new BasicNameValuePair("gender",gender));
	   	nameValuePairs.add(new BasicNameValuePair("birthdate",birthdate));
	   	nameValuePairs.add(new BasicNameValuePair("country",country));
	   	nameValuePairs.add(new BasicNameValuePair("contact",contact));   	
	   	
	   	
		
	    	try
	    	{
			HttpClient httpclient = new DefaultHttpClient();
			webserviceName = "registeruser.php";
		        HttpPost httppost = new HttpPost(webservicePath + webserviceName);
		        //HttpPost httppost = new HttpPost("http://blackfoxweb.com/iloveit/registeruser.php");
		        
		       	//HttpPost httppost = new HttpPost("http://megashop.us/iloveit/registeruser.php");
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        HttpResponse response = httpclient.execute(httppost); 
		        HttpEntity entity = response.getEntity();
		        is = entity.getContent();
		        Log.e("pass 1", "connection success ");
		}
	        catch(Exception e)
		{
	        	Log.e("Fail 1", e.toString());
		    	Toast.makeText(getApplicationContext(), "Invalid IP Address",
				Toast.LENGTH_LONG).show();
		}     
	        
	        try
	        {
	            BufferedReader reader = new BufferedReader
				(new InputStreamReader(is,"iso-8859-1"),8);
	            StringBuilder sb = new StringBuilder();
	            String line="0";
	            while ((line = reader.readLine()) != null)
		    {
	                sb.append(line + "\n");
	            }
	            is.close();
	            insertresult = sb.toString();
		    Log.e("pass 2", "connection success ");
		}
	        catch(Exception e)
		{
	            Log.e("Fail 2", e.toString());
		}     
	       
		try
		{
			//Toast.makeText(getApplicationContext(), insertresult, Toast.LENGTH_LONG).show();
	            JSONObject json_data = new JSONObject(insertresult);	            
	            insertcode=(json_data.getInt("flag"));
						
	            
	            if(insertcode==1)
	            {
	            	Toast.makeText(getApplicationContext(), "Inserted Successfully",
	            	Toast.LENGTH_SHORT).show();
	            }
	            else
	            {
	            	Toast.makeText(getApplicationContext(), "Sorry, Try Again",
	            	Toast.LENGTH_LONG).show();
	            }
		}
		catch(Exception e)
		{
	            Log.e("Fail 3", e.toString());
		}
	 
	}
	
	private void CheckUserExist()
	{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("username",unamecheckwith));
		//http post
		try{
		     HttpClient httpclient = new DefaultHttpClient();

		     webserviceName="check-user-exist.php";
		     HttpPost httppost = new HttpPost(webservicePath + webserviceName);
		     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		     HttpResponse response = httpclient.execute(httppost);
		     HttpEntity entity = response.getEntity();
		     is = entity.getContent();
		     }catch(Exception e){
		         Log.e("log_tag", "Error in http connection"+e.toString());
		    }
		//convert response to string
		try{
		      BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		       sb = new StringBuilder();
		       sb.append(reader.readLine() + "\n");

		       String line="0";
		       while ((line = reader.readLine()) != null) {
		                      sb.append(line + "\n");
		        }
		        	is.close();
		        	result=sb.toString();
		        }catch(Exception e){
		              Log.e("log_tag", "Error converting result "+e.toString());
		        }

		String name;
		try{
		      jArray = new JSONArray(result);
		      JSONObject json_data=null;
		      //wallpostitemlist = new ArrayList<WallPostItem>();
		      for(int i=0;i<jArray.length();i++){
		             json_data = jArray.getJSONObject(i);
		             //ct_name+=json_data.getString("post_title"); //here "username" is the column name in database             
		            unamefromdb = json_data.getString("username");
		     		//item = new WallPostItem(R.drawable.picnew,"Faisal",json_data.getString("user_post_pic_id"),json_data.getString("post_date_time"),json_data.getString("post_title"),json_data.getString("post_pic_link"),"100 Love");
		     	    //wallpostitemlist.add(item);
		         }
		      }
		      catch(JSONException e1){
		       //Toast.makeText(getBaseContext(), "No Data Found" ,Toast.LENGTH_LONG).show();
		      } catch (ParseException e1) {
		   e1.printStackTrace();
		 }
		
		}

	
	private void CheckInsertData()
	{
		// http post
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();		nameValuePairs.add(new BasicNameValuePair("c_name","KL"));
				nameValuePairs.add(new BasicNameValuePair("email",email));
			   	nameValuePairs.add(new BasicNameValuePair("username",username));
			   	nameValuePairs.add(new BasicNameValuePair("password",password));
			   	nameValuePairs.add(new BasicNameValuePair("gender",gender));
			   	nameValuePairs.add(new BasicNameValuePair("birthdate",birthdate));
			   	nameValuePairs.add(new BasicNameValuePair("country",country));
			   	nameValuePairs.add(new BasicNameValuePair("contact",contact));
				try{
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost("http://amberdecorators.in/iloveit-webpage/registeruser.php");
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					is = entity.getContent();
					}catch(Exception e){
					Log.e("log_tag", "Error in http connection"+e.toString());
					}
	}
	
	private class LoadUsernameTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog Asyncdialog = new ProgressDialog(UserRegisterActivity.this);
		
		@Override
        protected void onPreExecute() {
			
			//CheckNetworkConnection();
            //set message of the dialog
            Asyncdialog.setMessage("Loading Contents for Free Listing..");
            Asyncdialog.setCancelable(false);
            Asyncdialog.show();
            super.onPreExecute();
        }
		
	     protected Void doInBackground(Void... args) {
	        // do background work here    	 
	    	
	    	 TestDB();    	 
	    	 return null;
	     }

	     
	     protected void onPostExecute(Void result) {
	         // do UI work here
	    	 
	    	 edt_signup_email.setText(ct_name);	    	 
	    	 
	    	 Asyncdialog.dismiss();
	    	 super.onPostExecute(result);
	     
	     }
	 }


	private class LoadRegisterUserTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog Asyncdialog = new ProgressDialog(UserRegisterActivity.this);
		
		@Override
        protected void onPreExecute() {
			
			//CheckNetworkConnection();
            //set message of the dialog
            Asyncdialog.setMessage("Creating Account..");
            Asyncdialog.setCancelable(false);
            Asyncdialog.show();
            super.onPreExecute();
        }
		
	     protected Void doInBackground(Void... args) {
	        // do background work here    	 
	    	
	    	 RegisterUserManualy();
	    	 //CheckInsertData();
	    	 return null;
	     }

	     
	     protected void onPostExecute(Void result) {
	         // do UI work here
	    	 
	    	 //edt_signup_email.setText(ct_name);	    	 
	    	 //btn_signup.setText(insertresult.toString());
	    	 AlertDialog.Builder builder1 = new AlertDialog.Builder(UserRegisterActivity.this);
	            builder1.setMessage("Account created successfully..");
	            builder1.setCancelable(true);
	            builder1.setPositiveButton("Ok",
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	                }
	            });
	            /*builder1.setNegativeButton("No",
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	                }
	            });*/

	            AlertDialog alert11 = builder1.create();
	            alert11.show();
	    	 
	    	 Asyncdialog.dismiss();
	    	 super.onPostExecute(result);
	     
	     }
	 }
	
	
	private class LoadUserExistCheckTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog Asyncdialog = new ProgressDialog(UserRegisterActivity.this);
		
		@Override
        protected void onPreExecute() {
			
			//CheckNetworkConnection();
            //set message of the dialog

			//enable Progress On ActionBar			
			setProgressBarIndeterminateVisibility(true);
	        setProgressBarVisibility(true);

            super.onPreExecute();
        }
		
	     protected Void doInBackground(Void... args) {
	        // do background work here    	 
	    	
	    	 CheckUserExist();   	 
	    	 return null;
	     }

	     
	     protected void onPostExecute(Void result) {
	         // do UI work here
	    	 
	    	 getActionBar().setTitle(unamefromdb);	   
	    	 
	    	 if(unamecheckwith.equals(unamefromdb))
	    	 {
	    		Toast.makeText(getApplicationContext(), "Username not available.", Toast.LENGTH_LONG).show();
	    		edt_signup_username.setTextColor(Color.RED);
	 			edt_signup_username.setTag("error");
	    	 }
	    	 else
	    	 {
	    		Toast.makeText(getApplicationContext(), "Username available.", Toast.LENGTH_LONG).show();
 	    		edt_signup_username.setTextColor(Color.BLACK);
		    	edt_signup_username.setTag("done");
	    	 }
	    	 

	    	 //Disable Progress On ActionBar
	    	 setProgressBarIndeterminateVisibility(false);
	         setProgressBarVisibility(false);
	         
	    	 super.onPostExecute(result);
	     
	     }
	 }


	private void SetOnFocusChangedEvent()
	{
		edt_signup_email.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
				{
					edt_signup_email.setTextColor(Color.BLACK);					
				}
				
			}
		});
		
		edt_signup_contact.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
				{
					edt_signup_contact.setTextColor(Color.BLACK);					
				}
				
			}
		});
		
		edt_signup_conpassword.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
				{
					edt_signup_conpassword.setTextColor(Color.BLACK);					
				}
				
			}
		});
		
		edt_signup_username.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				
				if(!hasFocus)
				{
					if(!edt_signup_username.getText().toString().equals(""))
					{
						unamecheckwith = edt_signup_username.getText().toString().trim();
						new LoadUserExistCheckTask().execute();
					}					
				}
				else
				{
					edt_signup_username.setTextColor(Color.BLACK);
				}
				
			}
		});		
		
	}
	
	private void ValidateData()
	{
		if(emailValidator(edt_signup_email.getText().toString().trim()))
		{
			edt_signup_email.setTextColor(Color.BLACK);
			edt_signup_email.setTag("done");
		}
		else
		{
			edt_signup_email.setTextColor(Color.RED);
			edt_signup_email.setTag("error");
		}
		
		if(IsMobileValid(edt_signup_contact.getText().toString().trim()))
		{
			edt_signup_contact.setTextColor(Color.BLACK);
			edt_signup_contact.setTag("done");
		}
		else
		{
			edt_signup_contact.setTextColor(Color.RED);
			edt_signup_contact.setTag("error");
		}
		
		if(!edt_signup_password.getText().equals(null) && !edt_signup_conpassword.getText().equals(null))
		{
			if(edt_signup_password.getText().toString().equals(edt_signup_conpassword.getText().toString()))
			{
				edt_signup_conpassword.setTag("done");
				edt_signup_conpassword.setTextColor(Color.BLACK);
			}
			else {
				edt_signup_conpassword.setTag("error");
				edt_signup_conpassword.setTextColor(Color.RED);
			}
		}
		
	}
	
	public boolean emailValidator(String email) 
	{
	    Pattern pattern;
	    Matcher matcher;
	    final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	    pattern = Pattern.compile(EMAIL_PATTERN);
	    matcher = pattern.matcher(email);
	    return matcher.matches();
	}
	
	public boolean IsMobileValid(String mobile) 
	{
	    Pattern pattern;
	    Matcher matcher;
	    final String EMAIL_PATTERN = "(\\+91(-|\\s)?|91(-|\\s)?|0(-|\\s)?)?(7|8|9)[0-9]{9}(/(((\\+91(-|\\s)?|91(-|\\s)?|0(-|\\s)?)?(7|8|9)[0-9]{9})))*$";
	    pattern = Pattern.compile(EMAIL_PATTERN);
	    matcher = pattern.matcher(mobile);
	    return matcher.matches();
	}
	
	


	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_signup:
			SetRegisterUserManualyData();
			ValidateData();
			
			if(edt_signup_username.getTag() == "error" || edt_signup_email.getTag() == "error" || edt_signup_contact.getTag() == "error" || edt_signup_conpassword.getTag() == "error")
			{
				AlertDialog.Builder builder1 = new AlertDialog.Builder(UserRegisterActivity.this);
	            builder1.setMessage("Invalid data..");
	            builder1.setCancelable(true);
	            builder1.setPositiveButton("Ok",
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	                }
	            });
	            /*builder1.setNegativeButton("No",
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	                }
	            });*/

	            AlertDialog alert11 = builder1.create();
	            alert11.show();
			}
			else
			{
				new LoadRegisterUserTask().execute();
			}
			
			break;

		default:
			break;
		}
		
	}

	
	
	

	
	
	
}
