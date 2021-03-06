package makemachine.android.examples;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

public class PhotoCaptureExample extends Activity 
{
	protected Button _button;
	protected ImageView _image;
	protected TextView _field;
	protected String _path;
	protected boolean _taken;
	TessBaseAPI baseApi;
	ExifInterface exif;

	protected static final String PHOTO_TAKEN	= "photo_taken";
		
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        

       
        _image = ( ImageView ) findViewById( R.id.image );
        _field = ( TextView ) findViewById( R.id.field );
        _button = ( Button ) findViewById( R.id.button );
        _button.setOnClickListener( new ButtonClickHandler() );
        
        _path = Environment.getExternalStorageDirectory() + "/tesseract/tessdata/make_machine_example.jpg";
    
      
    }    
    
    public class ButtonClickHandler implements View.OnClickListener 
    {
    	public void onClick( View view ){
    		Log.i("MakeMachine", "ButtonClickHandler.onClick()" );
    		
    		startCameraActivity();
    	}
    }
    
    public void startCameraActivity()
    {
    	Log.i("MakeMachine", "startCameraActivity()" );
    	File file = new File( _path );
    	Uri outputFileUri = Uri.fromFile( file );
    	
    	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
    	intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
    	
    	startActivityForResult( intent, 0 );
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {	
    	Log.i( "MakeMachine", "resultCode: " + resultCode );
    	switch( resultCode )
    	{
    	
    			
    		case  RESULT_OK:
    			Log.i( "MakeMachine", "resultCode2: " + resultCode );
    		  onPhotoTaken();
    		
    			
    		break;
    	}
    }
    
    protected void onPhotoTaken()
    {
    	Log.i( "MakeMachine", "onPhotoTaken" );
    	
    	_taken = true;
    	
    	BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
    	
    	Bitmap bitmap = BitmapFactory.decodeFile( _path, options );
    	
    	_image.setImageBitmap(bitmap);
    	
    	_field.setVisibility( View.GONE );
    	
    	
    	
		try {
			exif = new ExifInterface(_path);
		 	int exifOrientation = exif.getAttributeInt(
	    	        ExifInterface.TAG_ORIENTATION,
	    	        ExifInterface.ORIENTATION_NORMAL);
		 	
			int rotate = 0;

			switch (exifOrientation) {
	    	case ExifInterface.ORIENTATION_ROTATE_90:
	    	    rotate = 90;
	    	    break;
	    	case ExifInterface.ORIENTATION_ROTATE_180:
	    	    rotate = 180;
	    	    break;
	    	case ExifInterface.ORIENTATION_ROTATE_270:
	    	    rotate = 270;
	    	    break;
	    	}

	    	if (rotate != 0) {
	    	    int w = bitmap.getWidth();
	    	    int h = bitmap.getHeight();

	    	    // Setting pre rotate
	    	    Matrix mtx = new Matrix();
	    	    mtx.preRotate(rotate);

	    	    // Rotating Bitmap & convert to ARGB_8888, required by tess
	    	    bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
	    	}
	    	bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
	    	
	    	baseApi = new TessBaseAPI();
	    	// DATA_PATH = Path to the storage
	    	 String lang =  "eng";
	    	baseApi.init(_path, lang);
	    	//  baseApi.init("/mnt/sdcard/tesseract/tessdata/eng.traineddata", "eng");
	    	baseApi.setImage(bitmap);
	    	String recognizedText = baseApi.getUTF8Text();
	    	baseApi.end();
	    	Log.i("Swaaagui",recognizedText);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   

    
    }
    
    @Override 
    protected void onRestoreInstanceState( Bundle savedInstanceState){
    	Log.i( "MakeMachine", "onRestoreInstanceState()");
    	if( savedInstanceState.getBoolean( PhotoCaptureExample.PHOTO_TAKEN ) ) {
    		onPhotoTaken();
    	}
    }
    
    @Override
    protected void onSaveInstanceState( Bundle outState ) {
    	outState.putBoolean( PhotoCaptureExample.PHOTO_TAKEN, _taken );
    }
}