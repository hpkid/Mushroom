package hardison.hp.mushroom;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.imgrec.ImageRecognitionPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

public class MainActivity extends AppCompatActivity {
    ImageView img;
    Button btTake;
    Integer REQUEST_CODE = 5398;
    Integer PIC_CROP = 2;
    public Uri picUri, mCapturedImageURI;
    ArrayBlockingQueue<NeuralNetwork> networkQueue = new ArrayBlockingQueue<NeuralNetwork>(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.imageView);
        btTake = (Button) findViewById(R.id.btTake);
        btTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Images.Media.TITLE, "Image File name");
//                mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                startActivityForResult(intentPicture, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                img.setImageBitmap(photo);

                boolean deleted = false;
                try {//You can delete here
                    File file = new File("/sdcard/image.jpg");
                    deleted = file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                OutputStream stream = null;
                try {
                    stream = new FileOutputStream("/sdcard/image.jpg");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                startNeuralNetwork();
            }
//            else if (requestCode == PIC_CROP){
//                Bundle extras = data.getExtras();
//
//                Bitmap thePic = extras.getParcelable("data");
//                OutputStream output;
//
//                img.setImageBitmap(thePic);
//                String path = getRealPathFromURI(mCapturedImageURI);
//                Toast.makeText(this,path, Toast.LENGTH_SHORT).show();
//            }
        }
    }

    private void startNeuralNetwork() {
        Thread t = new Thread(null, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "loading nnet", Toast.LENGTH_LONG).show();
                final NeuralNetwork network = NeuralNetwork.createFromFile("/sdcard/net10-1.nnet"); // load trained neural network saved with Neuroph Studio
                Toast.makeText(getApplicationContext(), "done nnet", Toast.LENGTH_LONG).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // get the image recognition plugin from neural network
                            final ImageRecognitionPlugin imageRecognition = (ImageRecognitionPlugin) network.getPlugin(ImageRecognitionPlugin.class); // get the image recognition plugin from neural network
                            //Toast.makeText(getApplicationContext(), "this", Toast.LENGTH_LONG).show();

                            // image recognition is done here (specify some existing image file)
                            Toast.makeText(getApplicationContext(), "loading image", Toast.LENGTH_LONG).show();
                            imageRecognition.setInput(new File("/sdcard/image.jpg"));
                            Toast.makeText(getApplicationContext(), "done image", Toast.LENGTH_LONG).show();
                            Thread t2 = new Thread(null, new Runnable() {
                                @Override
                                public void run() {
                                    imageRecognition.processInput();
                                    final HashMap<String, Double> result = imageRecognition.getOutput();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }, "nnet2", 256000);
                            t2.start();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        }, "nnet", 256000);
        t.start();
    }




    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return contentUri.getPath();
        }
    }


    //    private void performCrop() {
//        try {
//            //call the standard crop action intent (the user device may not support it)
//            Intent cropIntent = new Intent("com.android.camera.action.CROP");
//            cropIntent.setDataAndType(picUri, "image/*");
//            cropIntent.putExtra("crop", "true");
//            cropIntent.putExtra("aspectX", 1);
//            cropIntent.putExtra("aspectY", 1);
//            cropIntent.putExtra("outputX", 200);
//            cropIntent.putExtra("outputY", 200);
//            cropIntent.putExtra("return-data", true);
//
//            startActivityForResult(cropIntent, PIC_CROP);
//            Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
//        } catch (ActivityNotFoundException anfe) {
//            String errorMessage = "Your device doesn't support the crop action!";
//            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
//        }
//    }

}
