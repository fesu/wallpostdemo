package com.example.wallpostdemo;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//import org.apache.commons.codec.binary.Base64;
import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;

public class HttpFileUploader implements Runnable{

URL connectURL;
String params;
String responseString;
//InterfaceHttpUtil ifPostBack;
String fileName;
byte[] dataToServer;

HttpFileUploader(String urlString, String params, String fileName ){
try{
connectURL = new URL(urlString);
}catch(Exception ex){
Log.i("URL FORMATION","MALFORMATED URL");
}
this.params = params+"=";
this.fileName = fileName;

}


void doStart(FileInputStream stream){ 
fileInputStream = stream;
thirdTry();
} 

FileInputStream fileInputStream = null;
void thirdTry(){
String exsistingFileName = "asdf.png";

String lineEnd = "\r\n";
String twoHyphens = "--";
String boundary = "*****";
String Tag="3rd";
try
{
//------------------ CLIENT REQUEST

Log.e(Tag,"Starting to bad things");
// Open a HTTP connection to the URL

HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();

// Allow Inputs
conn.setDoInput(true);

// Allow Outputs
conn.setDoOutput(true);

// Don't use a cached copy.
conn.setUseCaches(false);

// Use a post method.
conn.setRequestMethod("POST");

conn.setRequestProperty("Connection", "Keep-Alive");

conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

DataOutputStream dos = new DataOutputStream( conn.getOutputStream() );

dos.writeBytes(twoHyphens + boundary + lineEnd);
dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + exsistingFileName +"\"" + lineEnd);
dos.writeBytes(lineEnd);




Log.e(Tag,"Headers are written");

// create a buffer of maximum size

int bytesAvailable = fileInputStream.available();
int maxBufferSize = 1024;
int bufferSize = Math.min(bytesAvailable, maxBufferSize);
byte[] buffer = new byte[bufferSize];

// read file and write it into form...

int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

while (bytesRead > 0)
{
dos.write(buffer, 0, bufferSize);
bytesAvailable = fileInputStream.available();
bufferSize = Math.min(bytesAvailable, maxBufferSize);
bytesRead = fileInputStream.read(buffer, 0, bufferSize);
}

// send multipart form data necesssary after file data...

dos.writeBytes(lineEnd);
dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

// close streams
Log.e(Tag,"File is written");
fileInputStream.close();
dos.flush();

InputStream is = conn.getInputStream();
// retrieve the response from server
int ch;

StringBuffer b =new StringBuffer();
while( ( ch = is.read() ) != -1 ){
b.append( (char)ch );
}
String s=b.toString(); 
Log.i("Response",s);
dos.close();


}
catch (MalformedURLException ex)
{
Log.e(Tag, "error: " + ex.getMessage(), ex);
}

catch (IOException ioe)
{
Log.e(Tag, "error: " + ioe.getMessage(), ioe);
}
}




//Read more

@Override
public void run() {
	// TODO Auto-generated method stub
	
}
//: http://getablogger.blogspot.com/2008/01/android-how-to-post-file-to-php-server.html#ixzz3A4NTDLwX
}