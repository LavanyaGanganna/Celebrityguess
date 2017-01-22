package com.example.lavanya.celebrityguess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    String retresult;
    Pattern p;
    Matcher m;
    ArrayList<String> imagepaths=new ArrayList<>();
    ArrayList<String> names=new ArrayList<>();
    ImageView imageview;
    int chosenvalue=0;
    int locationofanswer;
    int locationofwronganswer;
    String[] answers=new String[4];
    Button button0,button1,button2,button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageview= (ImageView) findViewById(R.id.imageView);
        button0= (Button) findViewById(R.id.button);
        button1= (Button) findViewById(R.id.button2);
        button2=(Button) findViewById(R.id.button3);
        button3=(Button)findViewById(R.id.button4);
        Webjsondata webjsondata=new Webjsondata();
        try {
            retresult=webjsondata.execute("http://www.posh24.com/celebrities").get();
            String[] splitresult=retresult.split("<div class=\"sidebarContainer\">");
           // System.out.println(retresult);
            storevalues(splitresult[0]);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    private void storevalues(String res) {
       p= Pattern.compile("<img src=\"(.*?)\"");
        m=p.matcher(res);
        while(m.find()){
           // System.out.println(m.group(1));
            imagepaths.add(m.group(1));
        }
        p=Pattern.compile("alt=\"(.*?)\"/>");
        m=p.matcher(res);
        while(m.find()){
           // System.out.println(m.group(1));
            names.add(m.group(1));
        }
        Iterator iterator= imagepaths.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
            System.out.println(imagepaths.size());
        }
        Iterator iterates=names.iterator();
        while(iterates.hasNext()){
            System.out.println(iterates.next());
            System.out.println(names.size());
        }

            CreateNewQuestion();


    }

    private void  CreateNewQuestion(){
        Random random= new Random();
        if(imagepaths.size()>0) {
            chosenvalue = random.nextInt(imagepaths.size());
        }
        Imagedownloads imagedownload=new Imagedownloads();
        try {
            Log.i("the url",imagepaths.get(chosenvalue));
            Bitmap bitmapimg= imagedownload.execute(imagepaths.get(chosenvalue)).get();
            imageview.setImageBitmap(bitmapimg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        locationofanswer=random.nextInt(4);
        for(int i=0;i<4;i++){
            if(i==locationofanswer){
                answers[i]=names.get(chosenvalue);

            }
            else{
                locationofwronganswer=random.nextInt(imagepaths.size());
                while(locationofwronganswer==chosenvalue){
                    locationofwronganswer=random.nextInt(imagepaths.size());
                }
                answers[i]=names.get(locationofwronganswer);

            }

        }
        button0.setText(answers[0]);
        button1.setText(answers[1]);
        button2.setText(answers[2]);
        button3.setText(answers[3]);
    }

    public void celebchosen(View view){
        if(view.getTag().toString().equals(Integer.toString(locationofanswer))){
            Toast.makeText(this,"Correct Answer!!",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"Wrong Answer It was: " + names.get(chosenvalue) , Toast.LENGTH_SHORT).show();
        }
        CreateNewQuestion();
    }
    public class Webjsondata extends AsyncTask<String,Void,String>{
        URL url;
        HttpURLConnection urlConnection;
        String result=null;
        @Override
        protected String doInBackground(String... strings) {
            try {
                url=new URL(strings[0]);
                urlConnection= (HttpURLConnection) url.openConnection();
                InputStream inputStream=urlConnection.getInputStream();
                InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
                int data=inputStreamReader.read();
                while(data != -1){
                    char datas=(char)data;
                    result=result+datas;
                    data=inputStreamReader.read();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    public class Imagedownloads extends AsyncTask<String,Void,Bitmap>{
        URL imgurl;
        HttpURLConnection urlconnect;
        Bitmap bitmap;
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                imgurl=new URL(strings[0]);

                urlconnect= (HttpURLConnection) imgurl.openConnection();
                urlconnect.connect();
                InputStream inputstrem=urlconnect.getInputStream();
                 bitmap= BitmapFactory.decodeStream(inputstrem);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }

}
