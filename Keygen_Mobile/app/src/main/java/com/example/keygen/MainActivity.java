package com.example.keygen;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keygen.entity.Key;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private ListView listView ;
    protected static ProgressDialog progressDialog ;
    private ArrayList<Key> keys ;
    protected static KeyListAdapter keyListAdapter ;

    private Button generate ;
    private Button save ;
    private TextView key_generate ;

    public static final String local_domain = "http://192.168.43.107:8000" ;
    public static final String domain = "https://keygenapp.herokuapp.com" ;

    // les url
    public static final String GET_ALL_URL = local_domain+"/keygen/api/get_all" ;
    public static final String GENERATE_API_URL = local_domain+"/keygen/api/generate" ;
    public static final String SAVE_API_URL = local_domain+"/keygen/api/save" ;
    public static final String DELETE_API_URL = local_domain+"/keygen/api/delete" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        key_generate = (TextView)findViewById(R.id.key_gen) ;

        // générer une clé
        generate = (Button)findViewById(R.id.generate) ;
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // générer une clé
                String s = null;
                try {
                    s = new GenerateTask().execute(GENERATE_API_URL).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                key_generate.setText(s) ;

            }
        });

        // sauvegarder la clé
        save = (Button)findViewById(R.id.save) ;
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!((String) key_generate.getText()).equals("appuyer sur générer pour avoir une nouvelle clé"))
                {
                    new SaveOrDeleteTask(MainActivity.this,keys,keyListAdapter,progressDialog).execute(SAVE_API_URL, (String) key_generate.getText(),"post") ;
                }
                else
                {
                    Toast.makeText(MainActivity.this,"vous devez dabord générer une clé", Toast.LENGTH_LONG).show() ;
                }
            }
        });

        // list view
        listView = (ListView)findViewById(R.id.list) ;

        // header
        View headerView = ((LayoutInflater)this.getSystemService(this.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_header, null, false);
        listView.addHeaderView(headerView); ;

        // attacher l'adapter à la listview
        keys = new ArrayList<>() ;
        keyListAdapter = new KeyListAdapter(MainActivity.this,keys) ;
        listView.setAdapter(keyListAdapter) ;

        // récupérer les données et les ajouter à la liste
        try {
            keys = new JsonTask().execute(GET_ALL_URL).get() ;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    // tâche pour récuperer toutes les clés
    public class JsonTask extends AsyncTask<String, String, ArrayList<Key>>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this) ;
            progressDialog.setMessage("wait...") ;
            progressDialog.setCancelable(false) ;
            progressDialog.show() ;
        }

        protected ArrayList<Key> doInBackground(String ... params)
        {

            HttpURLConnection connection = null ;
            BufferedReader reader = null ;

            ArrayList<Key> keys = new ArrayList<>() ;

            try
            {
                // connection
                URL url = new URL(params[0]) ;
                connection = (HttpURLConnection) url.openConnection() ;
                connection.connect() ;

                // recevoir les données
                InputStream inputStream = connection.getInputStream() ;

                reader = new BufferedReader(new InputStreamReader(inputStream)) ;

                StringBuffer buffer = new StringBuffer() ;
                String s = "" ;

                while((s=reader.readLine()) != null )
                {
                    buffer.append(s+"\n") ;
                    Log.d("resp : ",s) ;
                }

                // pour gérer les dates
                DateFormat format = new SimpleDateFormat("dd/mm/yyyy") ;

                // creation de la liste des clés depuis le jsonarray reçu

                // convertir la chaine reçu en jsonarray
                JSONArray jsonArray = new JSONArray(buffer.toString()) ;

                // pour chaque objet dans le json array remplire la liste des clés avec
                keys = new Key().convertJsonToKeys(jsonArray) ;

                return keys ;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                if(connection != null)
                {
                    connection.disconnect() ;
                }
                try {
                    if(reader!=null)
                    {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null ;
        }

        @Override
        protected void onPostExecute(ArrayList<Key> s) {
            super.onPostExecute(s) ;

            if(progressDialog.isShowing())
            {
                progressDialog.dismiss() ;
            }

            //après avoir récupérer les données les ajouter à la liste
            keyListAdapter.setList(s) ;
            keyListAdapter.notifyDataSetChanged() ;

        }
    }

    // tâche pour generer une clé
    public class GenerateTask extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

            @Override
        protected String doInBackground(String... strings) {
                HttpURLConnection connection = null ;
                BufferedReader reader = null ;

                ArrayList<Key> keys = new ArrayList<>() ;

                try
                {
                    // connection
                    URL url = new URL(strings[0]) ;
                    connection = (HttpURLConnection) url.openConnection() ;
                    connection.connect() ;

                    // recevoir les données
                    InputStream inputStream = connection.getInputStream() ;

                    reader = new BufferedReader(new InputStreamReader(inputStream)) ;

                    StringBuffer buffer = new StringBuffer() ;
                    String s = "" ;

                    while((s=reader.readLine()) != null )
                    {
                        buffer.append(s+"\n") ;
                        Log.d("resp : ",s) ;
                    }



                    return buffer.toString() ;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();

                } finally {
                    if(connection != null)
                    {
                        connection.disconnect() ;
                    }
                    try {
                        if(reader!=null)
                        {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return null ;
            }

    }





}

