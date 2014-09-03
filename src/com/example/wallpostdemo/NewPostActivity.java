package com.example.wallpostdemo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.format.Time;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

public class NewPostActivity extends Activity implements OnClickListener {

	private String postdatetime,posttitle,postpiclink,userid;
	
	
	private EditText edt_new_post_title;
	private ImageView iv_new_post_pic;
	private Button btn_goto_signup,btn_goto_wall;
	
	
	private String newpostresult = "{\"token\":\"Abcxyy\"}";
	private JSONArray jArray = null;
	private StringBuilder sb = null;
	private String result = null;
	private InputStream is = null;
	private int newpostcode;
	private String uploaded_post_image_name;
	
	private final int SELECT_PHOTO = 1;
	private final int SELECT_PHOTO_FROM_CAMERA = 2;
	private static final String TEMP_PHOTO_FILE = "temporary_holder.jpg";  
	private static final int PICK_FROM_GALLERY = 2;
	private Uri postimageUri;
	private Drawable PostImageDrawable;
	private Bitmap BitmapselectedPostImage;
	private Intent imagereturnedintent;
	
	private ProgressDialog AsyncdialogNewPost;
	
	
	final int CONTEXT_MENU_GALLERY =1;
	 final int CONTEXT_MENU_CAMERA =2;
	 //final int CONTEXT_MENU_ARCHIVE =3;
	 
	// Weblink path for webservices
	    private String webservicePath = "http://amberdecorators.in/iloveit-webpage/";
	    private String webserviceName = "";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_post);
		InitializeAllControls();
		//SetSwipeToRotate();
		getActionBar().setTitle("ILoveIt");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.actionbar_new_post, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		switch (item.getItemId()) {
		case R.id.action_post:
			//String resName = getResourceNameFromClassByID(R.drawable.class, R.drawable.default_pic);
			
			new LoadImageUploaderTask().execute();			
			
			return true;			

		case R.id.action_gotowall:
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
			
		}
		
	}
	
	
	@Override
	 public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
	  //Context menu
	  menu.setHeaderTitle("Select Method..");          
	  menu.add(Menu.NONE, CONTEXT_MENU_GALLERY, Menu.NONE, "From Gallery");
	  menu.add(Menu.NONE, CONTEXT_MENU_CAMERA, Menu.NONE, "Use Camera");
	  //menu.add(Menu.NONE, CONTEXT_MENU_ARCHIVE, Menu.NONE, "Delete");
	 }

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    // TODO Auto-generated method stub
	    switch(item.getItemId())
	    {
	    case CONTEXT_MENU_GALLERY:
	    {
	    	Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");			
			startActivityForResult(photoPickerIntent, SELECT_PHOTO);
	    }
	    break;
	    case CONTEXT_MENU_CAMERA:
	    {
	    	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	        startActivityForResult(intent, SELECT_PHOTO_FROM_CAMERA);

	    }
	    break;
	    
	    }

	    return super.onContextItemSelected(item);
	}
	private void InitializeAllControls()
	{
		iv_new_post_pic = (ImageView)findViewById(R.id.iv_new_post_pic);
		edt_new_post_title = (EditText)findViewById(R.id.edt_new_post_title);
		btn_goto_signup = (Button)findViewById(R.id.btn_goto_signup);
		btn_goto_wall = (Button)findViewById(R.id.btn_goto_wall);
		//rl_main_new_post = (RelativeLayout)findViewById(R.id.rl_main_new_post);
		
		btn_goto_signup.setOnClickListener(this);
		btn_goto_wall.setOnClickListener(this);
		iv_new_post_pic.setOnClickListener(this);
		//rl_main_new_post.setOnClickListener(this);
		
		//edt_new_post_title.setText(currentDateandTime);
	}
	
	
	private void SetNewPostData()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
		String currentDateandTime = sdf.format(new Date());
		
		SimpleDateFormat sdf_year_for_path = new SimpleDateFormat("yyyy");
		sdf_year_for_path.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
		String year_for_path = sdf_year_for_path.format(new Date());
		
		SimpleDateFormat sdf_date_for_path = new SimpleDateFormat("dd-MM-yyyy");
		sdf_date_for_path.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
		String date_for_path = sdf_date_for_path.format(new Date());
		
		String final_image_path = "http://amberdecorators.in/iloveit-webpage/iloveit_post_images/" + year_for_path + "/" + date_for_path + "/";
		
		postdatetime = currentDateandTime;
		posttitle = edt_new_post_title.getText().toString();
		postpiclink = final_image_path + uploaded_post_image_name;
		userid = "1";	
		
	}
	
	private void NewPost()
	{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		 
	   	nameValuePairs.add(new BasicNameValuePair("userid",userid));
	   	nameValuePairs.add(new BasicNameValuePair("postdatetime",postdatetime));
	   	nameValuePairs.add(new BasicNameValuePair("posttitle",posttitle));
	   	nameValuePairs.add(new BasicNameValuePair("postpiclink",postpiclink));
	   	   	
		
	    	try
	    	{
			HttpClient httpclient = new DefaultHttpClient();
			webserviceName = "newpost.php";
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
	            newpostresult = sb.toString();
		    Log.e("pass 2", "connection success ");
		}
	        catch(Exception e)
		{
	            Log.e("Fail 2", e.toString());
		}     
	       
		try
		{
			//Toast.makeText(getApplicationContext(), insertresult, Toast.LENGTH_LONG).show();
	            JSONObject json_data = new JSONObject(newpostresult);	
	            newpostcode=(json_data.getInt("flag"));
						
	            
	            if(newpostcode==1)
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
	
	
	private class LoadNewPostTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog Asyncdialog = new ProgressDialog(NewPostActivity.this);
		
		@Override
        protected void onPreExecute() {
			
			//CheckNetworkConnection();
            //set message of the dialog
            Asyncdialog.setMessage("Publishing your post....");
            Asyncdialog.setCancelable(false);
            Asyncdialog.show();
            
            super.onPreExecute();
        }
		
	     protected Void doInBackground(Void... args) {
	        // do background work here    	 
	    	
	    	 NewPost();
	    	 //CheckInsertData();
	    	 return null;
	     }

	     
	     protected void onPostExecute(Void result) {
	         // do UI work here
	    	 
	    	 //edt_signup_email.setText(ct_name);	    	 
	    	 //btn_signup.setText(insertresult.toString());
	    	 AlertDialog.Builder builder1 = new AlertDialog.Builder(NewPostActivity.this);
	            builder1.setMessage("Post Published on wall...");
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
	            
	            //btn_new_post.setText(newpostresult);
	    	 
	    	 Asyncdialog.dismiss();
	    	 iv_new_post_pic.setImageResource(R.drawable.default_pic_black);
	    	 iv_new_post_pic.setTag("default_pic");
	    	 edt_new_post_title.setText("");
	    	 super.onPostExecute(result);
	     
	     }
	 }

	
	
	private class LoadImageUploaderTask extends AsyncTask<Void, Void, Void> {
		//ProgressDialog Asyncdialog = new ProgressDialog(NewPostActivity.this);
		
		@Override
        protected void onPreExecute() {
			
			//CheckNetworkConnection();
            //set message of the dialog
			AsyncdialogNewPost = new ProgressDialog(NewPostActivity.this);
			AsyncdialogNewPost.setMessage("Uploading image...");
            AsyncdialogNewPost.setCancelable(false);
            AsyncdialogNewPost.show();
            
            super.onPreExecute();
        }
		
	     protected Void doInBackground(Void... args) {
	        // do background work here    	 
	    	
	    	 UploadImage();
	    	 //TestMkDir();
	    	 //CheckInsertData();
	    	 return null;
	     }

	     
	     protected void onPostExecute(Void result) {
	         // do UI work here
	    	 
	    	 //edt_signup_email.setText(ct_name);	    	 
	    	 //btn_signup.setText(insertresult.toString());
	    	 /*AlertDialog.Builder builder1 = new AlertDialog.Builder(NewPostActivity.this);
	            builder1.setMessage("Image Uploaded...");
	            builder1.setCancelable(true);
	            builder1.setPositiveButton("Ok",
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	                }
	            });
	           builder1.setNegativeButton("No",
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	                }
	            });

	            AlertDialog alert11 = builder1.create();
	            alert11.show();
	            
	            //btn_new_post.setText(newpostresult);
	    	 
	    	 Asyncdialog.dismiss();*/
	    	 
	    	 SetNewPostData();
	    	 new LoadNewPostTask().execute();
	    	 AsyncdialogNewPost.dismiss();
	    	 super.onPostExecute(result);
	     
	     }
	 }

	
	

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btn_goto_signup:					
			
			Intent intsignup = new Intent(NewPostActivity.this, UserRegisterActivity.class);
			startActivity(intsignup);
			
			/*RotateBitmap(90);
			iv_new_post_pic.setImageBitmap(BitmapselectedPostImage);*/
			
			break;
			
		case R.id.iv_new_post_pic:			
			
			//To register the button with context menu.
            registerForContextMenu(iv_new_post_pic);
            openContextMenu(iv_new_post_pic);
			
			/*try {

				intent.putExtra("return-data", true);
				startActivityForResult(Intent.createChooser(intent,
				"Complete action using"), PICK_FROM_GALLERY);

				} catch (ActivityNotFoundException e) {
				// Do nothing for now
				}*/


				//Read more: http://www.androidhub4you.com/2012/07/how-to-crop-image-from-camera-and.html#ixzz3A4CJdmDN

			//Read more: http://www.androidhub4you.com/2012/07/how-to-crop-image-from-camera-and.html#ixzz3A4C6U1kZ
			break;
			
		case R.id.btn_goto_wall:
			Intent intwall = new Intent(NewPostActivity.this, WallActivity.class);
			startActivity(intwall);
			break;

		default:
			break;
		}
		
	}
	
		
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 
 
        
        	//Read more: http://www.androidhub4you.com/2012/07/how-to-crop-image-from-camera-and.html#ixzz3A4CjUgMv
        
        switch(requestCode) { 
        case SELECT_PHOTO:
            if(resultCode == Activity.RESULT_OK)
            {
            	/*imagereturnedintent = imageReturnedIntent;
            	new LoadImagePickerGalleryTask().execute();*/
            	
            	try {
        			postimageUri = imageReturnedIntent.getData();				
        			//OutputStream outStream = new ByteArrayOutputStream();
        			OutputStream outStream = getContentResolver().openOutputStream(postimageUri);
        			//final InputStream imageStream = getContentResolver().openInputStream(postimageUri);
        			InputStream inputStream = getContentResolver().openInputStream(postimageUri);
        			BitmapselectedPostImage = BitmapFactory.decodeStream(inputStream);
        			BitmapselectedPostImage.compress(Bitmap.CompressFormat.JPEG, 50, outStream);			
        		    PostImageDrawable = Drawable.createFromStream(inputStream, postimageUri.toString() );
        		       			
        			 //iv_new_post_pic.setImageDrawable(PostImageDrawable);
        	    	 iv_new_post_pic.setImageBitmap(BitmapselectedPostImage);
        	    	 iv_new_post_pic.setTag("selected");			
        	    	 edt_new_post_title.setText(getSelectedImageName(postimageUri));
        			
        			
        		} catch (Exception e) {
        			e.printStackTrace();
        		}

            	//SetImageFromGallery(imageReturnedIntent);
            }
            
        case SELECT_PHOTO_FROM_CAMERA:
        	if(resultCode == Activity.RESULT_OK)
        	{
        		imagereturnedintent = imageReturnedIntent;
        		new LoadImagePickerCameraTask().execute();
        	}
        	break;
            
        }
    }
	
	private void SetImageFromGallery(Intent imageReturnedIntent)
	{
		try {
			postimageUri = imageReturnedIntent.getData();				
			//OutputStream outStream = new ByteArrayOutputStream();
			OutputStream outStream = getContentResolver().openOutputStream(postimageUri);
			//final InputStream imageStream = getContentResolver().openInputStream(postimageUri);
			InputStream inputStream = getContentResolver().openInputStream(postimageUri);
			BitmapselectedPostImage = BitmapFactory.decodeStream(inputStream);
			BitmapselectedPostImage.compress(Bitmap.CompressFormat.JPEG, 100, outStream);			
		    PostImageDrawable = Drawable.createFromStream(inputStream, postimageUri.toString() );
		       			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void SetImageFromCamera(Intent imageReturnedIntent)
	{
		try {
			postimageUri = imageReturnedIntent.getData();
			OutputStream outStream = new ByteArrayOutputStream();
			//final InputStream imageStream = getContentResolver().openInputStream(postimageUri);
			InputStream inputStream = getContentResolver().openInputStream(postimageUri);
			BitmapselectedPostImage = BitmapFactory.decodeStream(inputStream);
			BitmapselectedPostImage.compress(Bitmap.CompressFormat.JPEG, 40, outStream);
			
		    PostImageDrawable = Drawable.createFromStream(inputStream, postimageUri.toString() );
		    
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private class LoadImagePickerCameraTask extends AsyncTask<Void, Void, Void> {
		//ProgressDialog Asyncdialog = new ProgressDialog(NewPostActivity.this);
		
		@Override
        protected void onPreExecute() {
			
			//CheckNetworkConnection();
            //set message of the dialog
			AsyncdialogNewPost = new ProgressDialog(NewPostActivity.this);
			AsyncdialogNewPost.setMessage("Loading image...");
            AsyncdialogNewPost.setCancelable(false);
            AsyncdialogNewPost.show();
            
            super.onPreExecute();
        }
		
	     protected Void doInBackground(Void... args) {
	        // do background work here    	 
	    	
	    	 SetImageFromCamera(imagereturnedintent);
	    	 //TestMkDir();
	    	 //CheckInsertData();
	    	 return null;
	     }

	     
	     protected void onPostExecute(Void result) {
	         // do UI work here
	    	 	    	
	    	 /*AlertDialog.Builder builder1 = new AlertDialog.Builder(NewPostActivity.this);
	            builder1.setMessage("Image Uploaded...");
	            builder1.setCancelable(true);
	            builder1.setPositiveButton("Ok",
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	                }
	            });
	           builder1.setNegativeButton("No",
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	                }
	            });

	            AlertDialog alert11 = builder1.create();
	            alert11.show();           
	            
	    	 
	    	 Asyncdialog.dismiss();*/
	    	 iv_new_post_pic.setImageBitmap(BitmapselectedPostImage);
	    	 //iv_new_post_pic.setImageDrawable(PostImageDrawable);
	    	 iv_new_post_pic.setTag("selected");
				
				//iv_new_post_pic.setImageDrawable(yourDrawable);
//				getSelectedImageName(imageUri);
				edt_new_post_title.setText(getSelectedImageName(postimageUri));
	    	 
	    	
	    	 AsyncdialogNewPost.dismiss();
	    	 super.onPostExecute(result);
	     
	     }
	 }

	
	
	public String getSelectedImageName(Uri uri) {
		  String result = null;
		  if (uri.getScheme().equals("content")) {
		    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		    try {
		      if (cursor != null && cursor.moveToFirst()) {
		        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
		      }
		    } finally {
		      cursor.close();
		    }
		  }
		  if (result == null) {
		    result = uri.getPath();
		    int cut = result.lastIndexOf('/');
		    if (cut != -1) {
		      result = result.substring(cut + 1);
		    }
		  }
		  return result;
		}
	
		
	public void RotateBitmap(float angle)
	{
	      Matrix matrix = new Matrix();
	      matrix.postRotate(angle);
	      BitmapselectedPostImage = Bitmap.createBitmap(BitmapselectedPostImage, 0, 0, BitmapselectedPostImage.getWidth(), BitmapselectedPostImage.getHeight(), matrix, true);	      
	}
		
	
	public void UploadImage()
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		BitmapselectedPostImage.compress(Bitmap.CompressFormat.JPEG, 50, stream); //compress to which format you want.
        byte [] byte_arr = stream.toByteArray();
        final String image_str = Base64.encodeBytes(byte_arr);        
				
				try{					
			        
                    HttpClient httpclient = new DefaultHttpClient();
                    
                    HttpPost httppost = new HttpPost("http://amberdecorators.in/iloveit-webpage/uploadpostimage2.php");
                    ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("image",image_str));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    final String the_string_response = convertResponseToString(response);                
                            
                           
                               //Toast.makeText(NewPostActivity.this, "Response " + the_string_response, Toast.LENGTH_LONG).show();
                               //btn_new_post.setText(the_string_response);
                               uploaded_post_image_name = the_string_response;                             
                          
                     
                }catch(final Exception e){
                    
                            //Toast.makeText(NewPostActivity.this, "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();                              
                      
                       System.out.println("Error in http connection "+e.toString());
                 }
				
		
        
	}

	
	public void TestMkDir()
	{
		/*
				
				try{					
			        
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://amberdecorators.in/iloveit-webpage/test-mkdir2.php");
                    ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();
                    //nameValuePairs.add(new BasicNameValuePair("image",image_str));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEn	tity();
                    is = entity.getContent();
                    //final String the_string_response = convertResponseToString(response);                
                            
                           
                               //Toast.makeText(NewPostActivity.this, "Response " + the_string_response, Toast.LENGTH_LONG).show();
                               //btn_new_post.setText(the_string_response);
                               //uploaded_post_image_name = the_string_response;                             
                          
                     
                }catch(final Exception e){
                    
                            //Toast.makeText(NewPostActivity.this, "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();                              
                      
                       System.out.println("Error in http connection "+e.toString());
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
*/				
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//http post
		try{
		     HttpClient httpclient = new DefaultHttpClient();

		     //Why to use 10.0.2.2
		     HttpPost httppost = new HttpPost("http://amberdecorators.in/iloveit-webpage/test-mkdir2.php");
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
		             //ct_name=json_data.getString("username");//here "username" is the column name in database
		         }
		      }
		      catch(JSONException e1){
		       Toast.makeText(getBaseContext(), "No Data Found" ,Toast.LENGTH_LONG).show();
		      } catch (ParseException e1) {
		   e1.printStackTrace();
		 }

        
	}
	
	
	public String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException{
		 
        String res = "";
        StringBuffer buffer = new StringBuffer();
        InputStream inputStream = response.getEntity().getContent();
        final int contentLength = (int) response.getEntity().getContentLength(); //getting content length…..
         
           //Toast.makeText(NewPostActivity.this, "contentLength : " + contentLength, Toast.LENGTH_LONG).show();                     
      
     
        if (contentLength < 0){
        }
        else{
               byte[] data = new byte[512];
               int len = 0;
               try
               {
                   while (-1 != (len = inputStream.read(data)) )
                   {
                       buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer…..
                   }
               }
               catch (IOException e)
               {
                   e.printStackTrace();
               }
               try
               {
                   inputStream.close(); // closing the stream…..
               }
               catch (IOException e)
               {
                   e.printStackTrace();
               }
               res = buffer.toString();     // converting stringbuffer to string…..

               
               //Toast.makeText(NewPostActivity.this, "Result : " + "res", Toast.LENGTH_LONG).show();
              
               //System.out.println("Response => " +  EntityUtils.toString(response.getEntity()));
        }
        return res;
   }
	
	
	private void SetSwipeToRotate()
	{
		iv_new_post_pic.setOnTouchListener(new OnSwipeTouchListener(this){
			public void onSwipeTop() {
		        //Toast.makeText(FreeListingActivity.this, "top", Toast.LENGTH_SHORT).show();
		    }
		    public void onSwipeRight() {
		        //Toast.makeText(FreeListingActivity.this, "right", Toast.LENGTH_SHORT).show();
		    	
		    }
		    public void onSwipeLeft() {
		       // Toast.makeText(FreeListingActivity.this, "left", Toast.LENGTH_SHORT).show();
		    	RotateBitmap(90);
				iv_new_post_pic.setImageBitmap(BitmapselectedPostImage);		    	
		    	
		    }
		    public void onSwipeBottom() {
		        //Toast.makeText(FreeListingActivity.this, "bottom", Toast.LENGTH_SHORT).show();
		    }

		public boolean onTouch(View v, MotionEvent event) {
			

		    return gestureDetector.onTouchEvent(event);
		}
		});

		
	}
	
	
	
	
	
}
