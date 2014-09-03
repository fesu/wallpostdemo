package com.example.wallpostdemo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.example.wallpostdemo.imageloader.UserDBHandler;
import com.example.wallpostdemo.imageloader.UserDbAdapter;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class WallActivity extends Activity implements OnItemClickListener, OnScrollListener, TabListener, OnRefreshListener {
	
	Activity context;	
	Integer[] profile_pic;
	String[] username;
	String[] post_time;
	String[] post_title;
	Integer[] post_pic;
	String[] total_love;
	int temp;
	private WallPostItem item;
	private ListView lv_wall;
	private int limitstart=0;
	private int limitend=5;
	private boolean isfirst = true;
	private String uname,pass;
	private int previousLastPosition;
	private int rowcount=20;
	
	
	public WallCustomListViewAdapter adptrwallpost;
	public ArrayList<WallPostItem> wallpostitemlist;
	
	private JSONArray jArray = null;
	private String result = null;
	private String insertresult = "{\"token\":\"Abcxyy\"}";
	private StringBuilder sb = null;
	private InputStream is = null;
	private ArrayList<String> postidlist;
	
	private ImageView iv_post_image_from_wall_items;
	private ProgressBar prog_post_data;
	
	// For swipe to refresh
	private SwipeRefreshLayout mSwipeRefreshLayout;
    private static final int LIST_ITEM_COUNT = 5;
    private int mOffset = 0;
    private ArrayAdapter<String> mListAdapter;
    
    
    // To mantain ListView Position
    private static final String LIST_STATE = "listState";
    private Parcelable mListState = null;
    
    //For Authentication
    private String unameparam="Faisal",passparam="faisal";
    
    //For LocalDB
    private String userid_local,username_local,password_local,email_local,birthdate_local,gender_local,contact_local,profile_pic_local,aboutme_local,country_local,love_rank_local,pic_count_local,total_love_count_local;
    

    // Weblink path for webservices
    private String webservicePath = "http://amberdecorators.in/iloveit-webpage/";
    private String webserviceName = "";
    
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_wall);
		
				
		/*if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
		{
			requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}*/
	    
		getActionBar().setTitle("ILoveIt");
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffdd191d")));
		//getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff3f51b5")));
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		//getActionBar().setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffdd191d")));	// Set Tab Bg
		
		Tab tabpost = getActionBar().newTab();
		//tabpost.setText("Post");
		tabpost.setIcon(R.drawable.ic_action_tab_new_post);		
		tabpost.setTabListener(this);
		getActionBar().addTab(tabpost, 0, false);
		
		Tab tabwall = getActionBar().newTab();
		//tabwall.setText("Wall");
		tabwall.setIcon(R.drawable.ic_action_tab_wall);
		tabwall.setTabListener(this);
		getActionBar().addTab(tabwall);

		Tab tabprofile = getActionBar().newTab();
		//tabprofile.setText("Profile");
		tabprofile.setIcon(R.drawable.ic_action_tab_profile);
		tabprofile.setTabListener(this);
		getActionBar().addTab(tabprofile, 2, false);
		
		Tab tabsetting = getActionBar().newTab();
		//tabprofile.setText("Profile");
		tabsetting.setIcon(R.drawable.ic_action_tab_settings);
		tabsetting.setTabListener(this);
		getActionBar().addTab(tabsetting, 3, false);		
		
		getActionBar().selectTab(tabwall);
		
		
		//SetWallContent();
		//prog_post_data = (ProgressBar)findViewById(R.id.progress_post_data);
		
		
		View v = getLayoutInflater().inflate(R.layout.wall_post_items, null);
		iv_post_image_from_wall_items = (ImageView)v.findViewById(R.id.iv_post_pic);
		
		/*iv_post_image_from_wall_items.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(WallActivity.this, "ImageView Clicked", Toast.LENGTH_LONG).show();
				
			}
		});*/
		
		lv_wall = (ListView)findViewById(R.id.lv_wallpost);
		
		//new LoadWallDataTask().execute();		
		
		lv_wall.setOnItemClickListener(this);
		lv_wall.setOnScrollListener(this);		
		
		
		// Configure the swipe refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.container);
        //mSwipeRefreshLayout.setOnRefreshListener(WallActivity.this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(
                android.R.color.holo_blue_bright, android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark, android.R.color.holo_red_dark);

     // Start showing the refresh animation
        mSwipeRefreshLayout.setRefreshing(true);
        //new LoadWallDataTask().execute();
        
     // Simulate a long running activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               new LoadWallDataTask().execute();
            }
        }, 4000);   		  

        /*unameauth = "Faisal";
        passauth = "faisal";*/
        
        new LoadAuthenticationTask().execute();
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.actionbar_wall_post,menu);
		
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		/*int id = item.getItemId();
		if (id == R.id.action_refresh_wall) {
			//new LoadWallDataTask().execute();
			limitstart = 0;
			limitend = 5;
			return true;
		}*/
		
		//new LoadWallDataTask().execute();
		//new LoadPostCountTask().execute();
		return super.onOptionsItemSelected(item);
	}
	
	
	/*private void SetPostImages()
	{
		wallpostitemlist = new ArrayList<WallPostItem>();
		item = new WallPostItem(R.drawable.picnew,"Faisal","31-July-14 01:10 PM", "Title Test","http://premium.imagesocket.com/images/2014/04/07/2682009-fvrq.jpg","100 Love");
	    wallpostitemlist.add(item);
	    
	    
	    item = new WallPostItem(R.drawable.picnew,"Faisal","31-July-14 01:10 PM", "Title Test","http://premium.imagesocket.com/images/2014/04/07/2682049-hgmg.jpg","100 Love");
	    wallpostitemlist.add(item);
	    
	    item = new WallPostItem(R.drawable.picnew,"Faisal","31-July-14 01:10 PM", "Title Test","http://premium.imagesocket.com/images/2014/04/07/2682004-keb1.jpg","100 Love");
	    wallpostitemlist.add(item);
	    
	    item = new WallPostItem(R.drawable.picnew,"Faisal","31-July-14 01:10 PM", "Title Test","http://premium.imagesocket.com/images/2014/04/07/2681983-t5em.jpg","100 Love");
	    wallpostitemlist.add(item);
	    
	    item = new WallPostItem(R.drawable.picnew,"Faisal","31-July-14 01:10 PM", "Title Test","http://premium.imagesocket.com/images/2014/04/07/2681979-bgxy.jpg","100 Love");
	    wallpostitemlist.add(item);
	    
	    
	}*/
	
	private void LoadWallData()
	{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("limitstart",Integer.toString(limitstart)));
		nameValuePairs.add(new BasicNameValuePair("limitend",Integer.toString(limitend)));
		/*nameValuePairs.add(new BasicNameValuePair("limitstart",Integer.toString(0)));
		nameValuePairs.add(new BasicNameValuePair("limitend",Integer.toString(5)));*/
		try{
		     HttpClient httpclient = new DefaultHttpClient();
		     
		     webserviceName = "get_post_data.php";
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
		      if(isfirst)
		      {
		    	  wallpostitemlist = new ArrayList<WallPostItem>();
		      }
		      
		      postidlist = new ArrayList<String>();
		      for(int i=0;i<jArray.length();i++){
		             json_data = jArray.getJSONObject(i);
		             //ct_name+=json_data.getString("post_title"); //here "username" is the column name in database             
		            postidlist.add(json_data.getString("user_post_pic_id"));
		            //rowcount = json_data.getInt("rcount");
		            //GetUsernameByID(json_data.getString("user_id"));
		     		item = new WallPostItem(R.drawable.picnew,"Faisal",json_data.getString("user_post_pic_id"),json_data.getString("post_date_time"),json_data.getString("post_title"),json_data.getString("post_pic_link"),json_data.getString("love_count"));
		     	    wallpostitemlist.add(item);
		         }
		      }
		      catch(JSONException e1){
		       //Toast.makeText(getBaseContext(), "No Data Found" ,Toast.LENGTH_LONG).show();
		      } catch (ParseException e1) {
		   e1.printStackTrace();
		 }
		}

	private void SetPostID(EditText edt, String postid)
	{
		edt.setTag(postid);
	}
	
	private class LoadWallDataTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog Asyncdialog = new ProgressDialog(WallActivity.this);
		
		@Override
        protected void onPreExecute() {
			
			//CheckNetworkConnection();
            //set message of the dialog
            Asyncdialog.setMessage("Loading Love Posts...");
            Asyncdialog.setCancelable(false);
            //Asyncdialog.show();
            super.onPreExecute();
        }
		
	     protected Void doInBackground(Void... args) {
	        // do background work here    	 
	    	
	    	 LoadWallData();    	 
	    	 return null;
	     }

	     
	     protected void onPostExecute(Void result) {
	         // do UI work here
	    	 
	    	 	    	 
	    	 lv_wall.setVisibility(View.VISIBLE);
	    	 //prog_post_data.setVisibility(View.GONE);
	    	 	
	    	 
	    	 if(isfirst == true)
	    	 {
	    		 adptrwallpost = new WallCustomListViewAdapter(WallActivity.this,R.layout.wall_post_items, wallpostitemlist);
	    		 lv_wall.setAdapter(adptrwallpost);
	    		 isfirst = false;
	    		 limitstart += 5;
		    	 //limitend += 5;
	    		 
	    	 }
	    	 else
	    	 {
	    		 //adptrwallpost = new WallCustomListViewAdapter(WallActivity.this,R.layout.wall_post_items, wallpostitemlist);
	    		 adptrwallpost.notifyDataSetChanged();
	    		 limitstart += 5;
		    	 //limitend += 5;
	    		 //lv_wall.setAdapter(adptrwallpost);
	    	 }
	    	 
	    	 
	    	 //getActionBar().setTitle(""+rowcount);
	    	 /*if(adptrwallpost == null)
	    	 {
	    		 lv_wall.setAdapter(adptrwallpost);	
	    	 }
	    	 else
	    	 {
	    		 adptrwallpost.notifyDataSetChanged();
	    	 }*/
	    	 
	    	 //scaleImage(iv_post_image_from_wall_items, 250);
	    	 //Asyncdialog.dismiss();
	    	 
	    	// Signify that we are done refreshing
	         mSwipeRefreshLayout.setRefreshing(false);
	         
	    	 super.onPostExecute(result);
	     
	     }
	 }	
		
	
	@SuppressWarnings("deprecation")
	private void scaleImage(ImageView view, int boundBoxInDp)
	{
	    // Get the ImageView and its bitmap
	    Drawable drawing = view.getDrawable();
	    Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

	    // Get current dimensions
	    int width = bitmap.getWidth();
	    int height = bitmap.getHeight();

	    // Determine how much to scale: the dimension requiring less scaling is
	    // closer to the its side. This way the image always stays inside your
	    // bounding box AND either x/y axis touches it.
	    float xScale = ((float) boundBoxInDp) / width;
	    float yScale = ((float) boundBoxInDp) / height;
	    float scale = (xScale <= yScale) ? xScale : yScale;

	    // Create a matrix for the scaling and add the scaling data
	    Matrix matrix = new Matrix();
	    matrix.postScale(scale, scale);

	    // Create a new bitmap and convert it to a format understood by the ImageView
	    Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	    BitmapDrawable result = new BitmapDrawable(scaledBitmap);
	    width = scaledBitmap.getWidth();
	    height = scaledBitmap.getHeight();

	    // Apply the scaled bitmap
	    view.setImageDrawable(result);

	    // Now change ImageView's dimensions to match the scaled image
	    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
	    params.width = width;
	    params.height = height;
	    view.setLayoutParams(params);
	}

	private void GetUsernameByID(String userid)
	{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userid",userid));
		//http post
		try{
		     HttpClient httpclient = new DefaultHttpClient();

		     //Why to use 10.0.2.2
		     webserviceName = "get-username-byid.php";
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
		            uname = json_data.getString("username");
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

	private void GetPostCount()
	{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		try{
		     HttpClient httpclient = new DefaultHttpClient();

		     webserviceName = "get-total-post.php";
		     HttpPost httppost = new HttpPost(webservicePath + webserviceName);
		     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		     HttpResponse response = httpclient.execute(httppost);
		     HttpEntity entity = response.getEntity();
		     is = entity.getContent();
		     
		     ResponseHandler<String> responseHandler = new BasicResponseHandler();
		     //rowcount = httpclient.execute(httppost, responseHandler);
		     
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
		      if(isfirst)
		      {
		    	  wallpostitemlist = new ArrayList<WallPostItem>();
		      }
		      
		      postidlist = new ArrayList<String>();
		      for(int i=0;i<jArray.length();i++){
		             json_data = jArray.getJSONObject(i);
		             //ct_name+=json_data.getString("post_title"); //here "username" is the column name in database             
		            postidlist.add(json_data.getString("user_post_pic_id"));
		            //GetUsernameByID(json_data.getString("user_id"));
		     		item = new WallPostItem(R.drawable.picnew,"Faisal",json_data.getString("user_post_pic_id"),json_data.getString("post_date_time"),json_data.getString("post_title"),json_data.getString("post_pic_link"),"100 Love");
		     	    wallpostitemlist.add(item);
		         }
			
			/*JSONObject json_data = new JSONObject(result);	            
			rowcount=(json_data.getInt("rowcount"));*/
		      }
		      catch(JSONException e1){
		       //Toast.makeText(getBaseContext(), "No Data Found" ,Toast.LENGTH_LONG).show();
		      } catch (ParseException e1) {
		   e1.printStackTrace();
		 }
				         /*try {
					
				        	 String url = "http://amberdecorators.in/iloveit-webpage/get-total-post.php";
				     		List<NameValuePair> parmeters = new ArrayList<NameValuePair>();
				     		            

				     		  DefaultHttpClient httpClient = new DefaultHttpClient();

				     		  HttpPost httpPost = new HttpPost(url); // You can use get method too here if you use get at the php scripting side to receive any values.

				     		  httpPost.setEntity(new UrlEncodedFormEntity(parmeters));

				     		  HttpResponse httpResponse = httpClient.execute(httpPost);

				     		  HttpEntity httpEntity = httpResponse.getEntity();

				     		  is = httpEntity.getContent();

				     		   BufferedReader reader = new BufferedReader(new InputStreamReader(
				     		                    is, "iso-8859-1"), 8); // From here you can extract the data that you get from your php file..

				     		        StringBuilder builder = new StringBuilder();

				     		        String line = null;

				     		        while ((line = reader.readLine()) != null) {
				     		            builder.append(line + "\n");
				     		        }

				     		        is.close();
				     		        result = builder.toString(); // Here you are converting again the string into json object. So that you can get values with the help of keys that you send from the php side.
				     		        JSONObject json = new JSONObject(result);
				     		        rowcount = json.getInt("rowcount");
				     		         

				} catch (Exception e) {
					// TODO: handle exception
				}
*/
		}

	
	private class LoadPostCountTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog Asyncdialog = new ProgressDialog(WallActivity.this);
		
		@Override
        protected void onPreExecute() {
			
			//CheckNetworkConnection();
            //set message of the dialog
            Asyncdialog.setMessage("Loading Love Posts...");
            Asyncdialog.setCancelable(false);
            //Asyncdialog.show();
            super.onPreExecute();
        }
		
	     protected Void doInBackground(Void... args) {
	        // do background work here    	 
	    	
	    	 GetPostCount();    	 
	    	 return null;
	     }

	     
	     protected void onPostExecute(Void result) {
	         // do UI work here
	    	 
	    	 //getActionBar().setTitle(ct_name);
	    	 
	    	 
	    	  //Asyncdialog.dismiss();
	    	 getActionBar().setTitle(""+rowcount);
	    	 super.onPostExecute(result);
	     
	     }
	 }	

	
	private void GetAuthentication()
	{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("username",unameparam));
		nameValuePairs.add(new BasicNameValuePair("password",passparam));
		//http post
		try{
		     HttpClient httpclient = new DefaultHttpClient();

		     webserviceName = "get_authentication.php";
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
		            username_local = json_data.getString("username");
		            password_local = json_data.getString("password");
		            
		            userid_local = json_data.getString("user_id");
		            email_local = json_data.getString("email");
		            birthdate_local = json_data.getString("birthdate");
		            gender_local = json_data.getString("gender");
		            contact_local = json_data.getString("contact_number");
		            profile_pic_local = json_data.getString("profile_pic");
		            aboutme_local = json_data.getString("about_me");
		            country_local = json_data.getString("country");
		            love_rank_local = json_data.getString("love_rank");
		            pic_count_local = json_data.getString("pic_count");
		            total_love_count_local = json_data.getString("total_love_count");            
		            
		            
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
	
	private class LoadAuthenticationTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog Asyncdialog = new ProgressDialog(WallActivity.this);
		
		@Override
        protected void onPreExecute() {
			
			//CheckNetworkConnection();
            //set message of the dialog
            Asyncdialog.setMessage("Loging in...");
            Asyncdialog.setCancelable(false);
            //Asyncdialog.show();
            super.onPreExecute();
        }
		
	     protected Void doInBackground(Void... args) {
	        // do background work here    	 
	    	
	    	 GetAuthentication();    	 
	    	 return null;
	     }

	     
	     protected void onPostExecute(Void result) {
	         // do UI work here
	    	 
	    	 //getActionBar().setTitle(ct_name);
	    	 
	    	 
	    	  //Asyncdialog.dismiss();
	    	 if(username_local.equals(unameparam) && password_local.equals(passparam))
	    	 {
	    		 getActionBar().setTitle("Success : " + password_local);
	    		 /*String sdcardroot = Environment.getExternalStorageDirectory().toString();
	    		 boolean success = (new File(sdcardroot + "/ILoveIt")).mkdir(); 
                 if (!success)
                 {
                     Log.w("directory not created", "directory not created");
                 }*/
                 
	    	 }
	    	 super.onPostExecute(result);
	     
	     }
	 }	
	
	private void DownloadImageToSDCard(String imageurl)
	{
		
	}
	
	//Set Userdata After LogedIn
	private void SetLogedInUserData()
	{
		UserDbAdapter userdetails= new UserDbAdapter(userid_local,username_local,password_local,email_local,birthdate_local,gender_local,contact_local,profile_pic_local,aboutme_local,country_local,love_rank_local,pic_count_local,1,total_love_count_local);
		userdetails.setUser_id(userid_local);
		userdetails.setUsername(username_local);
		userdetails.setPassword(password_local);
		userdetails.setEmail(email_local);
		userdetails.setBirthdate(birthdate_local);
		userdetails.setGender(gender_local);
		userdetails.setContact_number(contact_local);
		userdetails.setProfile_pic(profile_pic_local);
		userdetails.setAbout_me(aboutme_local);
		userdetails.setCountry(country_local);
		userdetails.setLove_rank(love_rank_local);
		userdetails.setPic_count(pic_count_local);
		userdetails.setIslogedin(1);
		userdetails.setTotal_love_count(total_love_count_local);
		
		UserDBHandler udb = new UserDBHandler(this);		
		udb.AddUserData(userdetails);
		Toast.makeText(this, "User Session has created..", Toast.LENGTH_LONG).show();
	}

	

	

	public View getView(int position, View convertView, ViewGroup parent) {
		
		
		//Load your view, populate it, etc...
	    /*View view = adptrwallpost.getView(position, convertView, parent);    

	    Animation animation = AnimationUtils.loadAnimation(view.getContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
	    view.startAnimation(animation);
	    lastPosition = position;*/
		
		return adptrwallpost.getView(position, convertView, parent);
	    //return wall_view;
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, final View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
		//Toast.makeText(getApplicationContext(), ""+position, Toast.LENGTH_LONG).show();
		
		// Open Image Viewer
		/*Intent intimageview = new Intent(WallActivity.this, ImageViewerActivity.class);
		intimageview.putExtra("postid", ((TextView)view.findViewById(R.id.tv_username)).getTag().toString());
		intimageview.putExtra("postidlist", postidlist);
		startActivity(intimageview);*/
		
		//if(view == (TextView)view.findViewById(R.id.tv_username))
		
			//String strimgtag = ((TextView)view.findViewById(R.id.tv_username)).getText().toString();
			//long strimgtag = lv_wall.getItemIdAtPosition(position);
			//Toast.makeText(WallActivity.this, ((TextView)view.findViewById(R.id.tv_username)).getTag().toString(), Toast.LENGTH_LONG).show();
			//Toast.makeText(WallActivity.this,""+ strimgtag, Toast.LENGTH_LONG).show();		
		
		
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		switch (view.getId()) {
		case R.id.lv_wallpost:
			int lastPosition = firstVisibleItem + visibleItemCount;
			// Save ListView state
			Parcelable state = lv_wall.onSaveInstanceState();

			 if (lastPosition == totalItemCount) {
			        if (previousLastPosition != lastPosition) { 

			        	new LoadWallDataTask().execute();
			        }
			        previousLastPosition = lastPosition;
			    }
			    else if(lastPosition < previousLastPosition - 5){
			        resetLastIndex();
			    }

			// Restore previous state (including selected item index and scroll position)
			 //lv_wall.onRestoreInstanceState(state);
			
			
		   
			
			
			
			/*if(visibleItemCount == firstVisibleItem + visibleItemCount)
			{
				new LoadWallDataTask().execute();
			}*/
			break;

		default:
			break;
		}
		
	}
	
	public void resetLastIndex(){
	    previousLastPosition = 0;
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
		// Start showing the refresh animation
        mSwipeRefreshLayout.setRefreshing(true);
        //resetLastIndex();
        //limitstart = 0;
        //limitend += 5;
        //isfirst = true;
     // Simulate a long running activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               new LoadWallDataTask().execute();
            }
        }, 5000);
	}
	
	
	@Override
	protected void onRestoreInstanceState(Bundle state) {
	    super.onRestoreInstanceState(state);
	    mListState = state.getParcelable(LIST_STATE);
	}

	@Override
	protected void onResume() {
	    super.onResume();
	    new LoadWallDataTask().execute();
	    if (mListState != null)
	        lv_wall.onRestoreInstanceState(mListState);
	    mListState = null;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle state) {
	    super.onSaveInstanceState(state);
	    mListState = lv_wall.onSaveInstanceState();
	    state.putParcelable(LIST_STATE, mListState);
	}
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
        super.onActivityResult(requestCode, resultCode, data); 
 
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        	
    }

	
	
	
	
	
	
	
	
}
