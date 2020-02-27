package com.ycalm.amandeep.hackernews2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase databaseArticles;
    SQLiteStatement statement;
    ArrayList<String> titlesArrayList,urlsArrayList;
    RecyclerView recyclerView;
    RecyclerView.Adapter myAdaptor;
    String articleDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titlesArrayList = new ArrayList<>();
        urlsArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(layoutManager);
        myAdaptor = new CustromAdaptor(getApplicationContext(),titlesArrayList);
        recyclerView.setAdapter(myAdaptor);
        titlesArrayList.clear();
        urlsArrayList.clear();
        Toast.makeText(this,"Updating",Toast.LENGTH_LONG).show();

        databaseArticles = this.openOrCreateDatabase("Articles",MODE_PRIVATE,null);
        databaseArticles.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY,articleId INTEGER,url VARCHAR,title VARCHAR,content VARCHAR)");

        DownloadTask task = new DownloadTask();

        try{

            task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
        }catch (Exception e){
        e.printStackTrace();
        }

    }

    private void updateListView() {
        try {
            Cursor c = databaseArticles.rawQuery("SELECT * FROM articles ORDER BY articleId DESC", null);
            int urlIndex = c.getColumnIndex("url");
            int titleIndex = c.getColumnIndex("title");
            c.moveToFirst();

            while (c != null) {
                titlesArrayList.add(c.getString(titleIndex));
                urlsArrayList.add(c.getString(urlIndex));
//                Log.i("Article id ", c.getString(idIndex));
//                Log.i("Article url ", c.getString(urlIndex));
//                Log.i("Article title ", c.getString(titleIndex));
                c.moveToNext();

            }
            myAdaptor.notifyDataSetChanged();
           // arrayAdapter.notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public class DownloadTask extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                JSONArray jsonArray = new JSONArray(result);
                databaseArticles.execSQL("DELETE FROM articles");

                for(int i = 0;i < 20; i++){
                    String articleId = jsonArray.getString(i);

                    url = new URL("https://hacker-news.firebaseio.com/v0/item/"+articleId+".json?print=pretty");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    reader = new InputStreamReader(httpURLConnection.getInputStream());
                    data = reader.read();
                    articleDetails = "";
                    while (data != -1){
                        char current = (char) data;
                        articleDetails += current;
                        data = reader.read();
                    }

                    JSONObject jsonObject = new JSONObject(articleDetails);
                    String title = jsonObject.getString("title");
                    String url2 = jsonObject.getString("url");
                    titlesArrayList.add(title);
                    urlsArrayList.add(title);
                    String sql = ("INSERT INTO articles (articleId,url,title) VALUES (?,?,?)");
                    statement = databaseArticles.compileStatement(sql);
                    statement.bindString(1,articleId);
                    statement.bindString(2,url2);
                    statement.bindString(3,title);
                    statement.execute();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //updateListView();
            myAdaptor.notifyDataSetChanged();
        }
    }
}
