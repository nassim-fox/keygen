package com.example.keygen;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.keygen.entity.Key;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

// tâche pour ajouter une clé ou la supprimer
public  class SaveOrDeleteTask extends AsyncTask<String, String, SaveOrDeleteTask.Keys> {

    String method ;
    private ArrayList<Key> keys ;
    private KeyListAdapter keyListAdapter ;
    private ProgressDialog progressDialog ;
    private Context context ;

    public SaveOrDeleteTask(Context context,ArrayList<Key> keys, KeyListAdapter keyListAdapter, ProgressDialog progressDialog)
    {
        this.context = context ;
        this.keys = keys ;
        this.keyListAdapter = keyListAdapter ;
        this.progressDialog = progressDialog ;

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Keys doInBackground(String... strings) {


        //  request

        HttpURLConnection connection = null ;
        BufferedWriter bufferedWriter = null ;
        OutputStream outputStream = null ;
        Key k = null ;

        // la clé à ajouter ou supprimer
        String key = strings[1] ;
        // post or delete
        method = strings[2] ;

        try
        {
            // connection
            URL url = new URL(strings[0]) ;
            connection = (HttpURLConnection) url.openConnection() ;
            connection.setRequestMethod("POST") ;
            connection.setDoInput(true) ;
            connection.setDoOutput(true) ;

            outputStream = new BufferedOutputStream(connection.getOutputStream()) ;

            // envoyer la clé
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTf-8")) ;
            bufferedWriter.write("key="+key) ;
            bufferedWriter.flush() ;
            outputStream.close() ;

            // en cas de réponse de delete on peu juste l'ignorer
            int response = connection.getResponseCode() ;

            if(response==HttpURLConnection.HTTP_OK)
            {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream())) ;
                StringBuffer buffer = new StringBuffer() ;

                String s = "" ;

                while((s=bufferedReader.readLine()) != null )
                {
                    buffer.append(s+"\n") ;
                    Log.d("resp : ",s) ;
                }


                // si la fonction est ajout (post) alors le retour devrait être jsonobject
                // si la fonction est suppression alors le retour c'est tout les éléments apart celui supprimé

                // pour gérer les dates
                DateFormat format = new SimpleDateFormat("dd/mm/yyyy") ;

                if(method.equals("post")) {

                    JSONObject jsonObject = new JSONObject(buffer.toString()) ;

                    if (jsonObject.has("key")) {
                        k = new Key();
                        k.setKey(jsonObject.getString("key"));
                        //convertir les dates
                        k.setCreate_date(format.parse(jsonObject.getString("create_date")));
                        k.setExpire_date(format.parse(jsonObject.getString("expire_date")));
                        k.setUsed(jsonObject.getBoolean("used"));
                    } else if (jsonObject.has("for")) {
                        k = new Key();
                        k.setKey(jsonObject.getString("message"));

                    }

                    return new Keys(k,null) ;
                }
                else if(method.equals("delete"))
                {
                    Object json = new JSONTokener(buffer.toString()).nextValue();
                    ArrayList<Key> rkeys = new ArrayList<>() ;

                    if (json instanceof JSONArray)
                    {
                        // convertir la chaine reçu en jsonarray
                        JSONArray jsonArray = new JSONArray(buffer.toString()) ;

                        // pour chaque objet dans le json array remplire la liste des clés avec
                        rkeys = new Key().convertJsonToKeys(jsonArray) ;

                    }
                    else if (json instanceof JSONObject)
                    {
                        JSONObject jsonObject = new JSONObject(buffer.toString()) ;

                        if(jsonObject.has("for"))
                        {
                            k = new Key();
                            k.setKey(jsonObject.getString("message"));
                        }
                    }

                    return new Keys(k,rkeys) ;
                }

            }
            connection.connect() ;

            return null ;


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
                if(bufferedWriter!=null)
                {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null ;
    }

    @Override
    protected void onPostExecute(Keys s) {
        super.onPostExecute(s) ;

        if(progressDialog.isShowing())
        {
            progressDialog.dismiss() ;
        }

        if(method.equals("post")) // en cas de post
        {
            //après avoir récupérer les données les ajouter à la liste
            if(s.getKey().getCreate_date()!=null) {
                keyListAdapter.clear() ;
                keys = keyListAdapter.getList() ;
                keys.add(s.getKey()) ;
                keyListAdapter.addAll(keys) ;
                keyListAdapter.notifyDataSetChanged();

            }
            else
            {
                // message d'erreur
                Toast.makeText(context,s.getKey().getKey(), Toast.LENGTH_LONG).show() ;

            }

        }
        else if(method.equals("delete")) // en cas de delete
        {
            if(s.getKey()==null) {
                keyListAdapter.clear() ;
                keyListAdapter.setList(s.getKeys()) ;
                keyListAdapter.notifyDataSetChanged();
                Log.e("kk",keyListAdapter.getCount()+" "+s.getKeys().size()) ;
            }
            else
            {
                // message d'erreur
                Toast.makeText(context,s.getKey().getKey(), Toast.LENGTH_LONG).show() ;

            }
        }

    }


    public class Keys{

        private Key key ;
        private ArrayList<Key> keys ;

        public Keys(Key key,ArrayList<Key> keys)
        {
            this.key = key ;
            this.keys = keys ;
        }

        public Key getKey() {
            return key;
        }

        public void setKey(Key key) {
            this.key = key;
        }

        public ArrayList<Key> getKeys() {
            return keys;
        }

        public void setKeys(ArrayList<Key> keys) {
            this.keys = keys;
        }
    }

}

