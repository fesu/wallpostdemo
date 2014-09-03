package com.example.wallpostdemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

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




import com.example.wallpostdemo.imageloader.ImageLoader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

public class ImageViewerActivity extends Activity implements OnClickListener {

	//Declare All Controls
	TextView tv_temp;
	ImageView iv_full_image;
	Menu menu;
	private ShareActionProvider shareaction;
	
	//Declare All Variable
	String postid,postpiclink;
	ArrayList<String> postidlist;
	
	//Deaclare All Classes
	public ImageLoader imageLoader;
	
	
	private JSONArray jArray = null;
	private String result = null;
	private String insertresult = "{\"token\":\"Abcxyy\"}";
	private StringBuilder sb = null;
	private InputStream is = null;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_viewer);
		
		imageLoader=new ImageLoader(getApplicationContext());
		
		getActionBar().setTitle("ILoveIt");
		getActionBar().setBackgroundDrawable(new ColorDrawable(0x50000000));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		postidlist = new ArrayList<String>();
		Intent intent = getIntent();
		postid = intent.getStringExtra("postid");
		postidlist = intent.getStringArrayListExtra("postidlist");
		//getActionBar().setTitle(postidlist.indexOf(postid));
		//tv_temp = (TextView)findViewById(R.id.tv_temp);
		//tv_temp.setText(postid);
		iv_full_image = (ImageView)findViewById(R.id.iv_full_image);
		
		iv_full_image.setOnClickListener(this);
		
		new LoadPostPicLinkTask().execute();
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.actionbar_imageview, menu);
		MenuItem item = menu.findItem(R.id.menu_item_share);
		
		// Fetch and store ShareActionProvider
	    shareaction = (ShareActionProvider) item.getActionProvider();
		this.menu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
			
		case R.id.action_download:
			new DownloadImageToDeviceTask().execute();
			return true;
			
		case R.id.action_setas_wallpaper:
			
			return true;			
			
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	private void LoadPostPicLink()
	{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("postid",postid));
		//http post
		try{
		     HttpClient httpclient = new DefaultHttpClient();

		     //Why to use 10.0.2.2
		     HttpPost httppost = new HttpPost("http://amberdecorators.in/iloveit-webpage/get-post-piclink-byid.php");
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
		            postpiclink = json_data.getString("post_pic_link");
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

	
	
	
	public void SetShareContents()
	{
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
	    //sharingIntent.setType("text/plain");
		sharingIntent.setType("image/png");
		
		Uri picuri = Uri.parse(postpiclink);
		Bitmap bmp = null;
		//bmp=BitmapFactory.decodeStream(getContentResolver().openInputStream(picuri));
		
		
		
	    String shareBody = "";
	    shareBody = "\n" + "Testing Share : " + postpiclink + "\n" + "\n";
	    
		Uri bmpUri = getLocalBitmapUri(iv_full_image);   
	        
	        
	    
	    
	    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ILoveIt");
	    sharingIntent.putExtra(Intent.EXTRA_STREAM, picuri);
	    //sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
	    setShareIntent(sharingIntent);
	    //startActivity(Intent.createChooser(sharingIntent, "Share Image"));
	}
	
	// Call to update the share intent
		private void setShareIntent(Intent shareIntent) {
		    if (shareaction != null) {
		        shareaction.setShareIntent(shareIntent);
		    }
		}
	
		public Uri getLocalBitmapUri(ImageView imageView) {
		    // Extract Bitmap from ImageView drawable
		    Drawable drawable = imageView.getDrawable();
		    Bitmap bmp = null;
		    if (drawable instanceof BitmapDrawable){
		       bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
		    } else {
		       return null;
		    }
		    // Store image to default external storage directory
		    Uri bmpUri = null;
		    try {
		        File file =  new File(Environment.getExternalStoragePublicDirectory(  
		            Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
		        file.getParentFile().mkdirs();
		        FileOutputStream out = new FileOutputStream(file);
		        bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
		        out.close();
		        bmpUri = Uri.fromFile(file);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		    return bmpUri;
		}
	
		
	private class LoadPostPicLinkTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog Asyncdialog = new ProgressDialog(ImageViewerActivity.this);
		
		@Override
        protected void onPreExecute() {
			
			//CheckNetworkConnection();
            //set message of the dialog
            /*Asyncdialog.setMessage("Loading image..");
            Asyncdialog.setCancelable(false);
            Asyncdialog.show();*/
            super.onPreExecute();
        }
		
	     protected Void doInBackground(Void... args) {
	        // do background work here    	 
	    	
	    	 LoadPostPicLink();   	 
	    	 return null;
	     }

	     
	     protected void onPostExecute(Void result) {
	         // do UI work here
	    	 
	    	 //tv_temp.setText(postpiclink);	    	 
	    	 imageLoader.DisplayImage(postpiclink, iv_full_image);
	    	 SetShareContents();
	    	 
	    	 
	    	 //Asyncdialog.dismiss();
	    	 super.onPostExecute(result);
	     
	     }
	 }

	
	private void DownloadImageToDevice(String url)
	{
		String imgpath;
		File imageFile;
        Bitmap bmpimgdownload = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            bmpimgdownload = BitmapFactory.decodeStream(in);
            
            File direct = new File(Environment.getExternalStorageDirectory()+"/ILoveIt");

    		if(!direct.exists()) {
    		     if(direct.mkdir()); //directory is created;
    		}
    		
    		imgpath = Environment.getExternalStorageDirectory().toString() + File.separator + "iloveit_download" + File.separator + System.currentTimeMillis() + ".jpeg";
    	    imageFile = new File(imgpath);
    	    
    	 // Refresh Screenshot in sdcard
	    	new SingleMediaScanner(this, imageFile);
	    			
			
	        OutputStream fout = new FileOutputStream(imageFile);
	        bmpimgdownload.compress(Bitmap.CompressFormat.JPEG, 90, fout);
	        fout.flush();
	        fout.close();
    		
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        
	}
	
	private void downloadImagesToSdCard(String downloadUrl,String imageName)
    {
        try
        {
            URL url = new URL(downloadUrl); 
            /* making a directory in sdcard */
            String sdCard=Environment.getExternalStorageDirectory().toString();     
            File myDir = new File(sdCard,"ILoveIt");

            /*  if specified not exist create new */
            if(!myDir.exists())
            {
                myDir.mkdir();
                Log.v("", "inside mkdir");
            }
            
            File finalDir = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "ILoveIt","iloveit_downloads");
            if(!finalDir.exists())
            {
                finalDir.mkdir();
                Log.v("", "inside finaldir");
            }
            

            /* checks the file and if it already exist delete */
            String fname = imageName;
            File file = new File (finalDir, fname);
            if (file.exists ()) 
                file.delete (); 

                 /* Open a connection */
            URLConnection ucon = url.openConnection();
            InputStream inputStream = null;
            HttpURLConnection httpConn = (HttpURLConnection)ucon;
            httpConn.setRequestMethod("GET");
            httpConn.connect();

              if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) 
              {
               inputStream = httpConn.getInputStream();
              }

                FileOutputStream fos = new FileOutputStream(file);  
    int totalSize = httpConn.getContentLength();
            int downloadedSize = 0;   
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ( (bufferLength = inputStream.read(buffer)) >0 ) 
            {                 
              fos.write(buffer, 0, bufferLength);                  
              downloadedSize += bufferLength;                 
              Log.i("Progress:","downloadedSize:"+downloadedSize+"totalSize:"+ totalSize) ;
            }   

                fos.close();
                Log.d("test", "Image Saved in sdcard..");    
             // Refresh Screenshot in sdcard
    	    	new SingleMediaScanner(this, file);
        }
        catch(IOException io)
        {                  
             io.printStackTrace();
        }
        catch(Exception e)
        {                     
            e.printStackTrace();
        }
    }
	
	private class DownloadImageToDeviceTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog Asyncdialog = new ProgressDialog(ImageViewerActivity.this);
		
		@Override
        protected void onPreExecute() {
			
			//CheckNetworkConnection();
            //set message of the dialog
            Asyncdialog.setMessage("Downloading image..");
            Asyncdialog.setCancelable(false);
            Asyncdialog.show();
            super.onPreExecute();
        }
		
	     protected Void doInBackground(Void... args) {
	        // do background work here    	 
	    	
	    	 //DownloadImageToDevice(postpiclink);
	    	 downloadImagesToSdCard(postpiclink,"iloveit_" + System.currentTimeMillis() + ".jpg");
	    	 //SetAsWallpaper();
	    	 return null;
	     }

	     
	     protected void onPostExecute(Void result) {
	         // do UI work here
	    	 
	    	 //tv_temp.setText(postpiclink);    	 
	    	 
	    	 //getActionBar().setTitle(postpiclink);
	    	 
	    	 Asyncdialog.dismiss();
	    	 super.onPostExecute(result);
	     
	     }
	 }
	
	private void SetAsWallpaper()
	{
		// get the Image to as Bitmap 
		Bitmap bitmap = BitmapFactory.decodeStream(getResources().openRawResource(R.id.iv_full_image));
		 
		    DisplayMetrics metrics = new DisplayMetrics(); 
		    getWindowManager().getDefaultDisplay().getMetrics(metrics);
		    // get the height and width of screen 
		    int height = metrics.heightPixels; 
		    int width = metrics.widthPixels;
		            
		    WallpaperManager wallpaperManager = WallpaperManager.getInstance(this); 
		      try {
		              wallpaperManager.setBitmap(bitmap);
		                 
		              wallpaperManager.suggestDesiredDimensions(width, height);
		             Toast.makeText(this, "Wallpaper Set", Toast.LENGTH_SHORT).show();
		 
		          } catch (IOException e) {
		                  e.printStackTrace();
		           }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_full_image:
			if(iv_full_image.getTag() == null)
			{
				iv_full_image.setTag("hide");
				getActionBar().hide();
			}
			else
			{
				iv_full_image.setTag(null);
				getActionBar().show();
			}
			break;

		default:
			break;
		}
		
	}

	
	
	
	
	
	
	
	
}
