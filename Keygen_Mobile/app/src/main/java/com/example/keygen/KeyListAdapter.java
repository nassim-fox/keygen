package com.example.keygen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.keygen.entity.Key;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class KeyListAdapter extends ArrayAdapter<Key> {

    private ArrayList<Key> keys  ;
    private Context context ;

    public KeyListAdapter(Context context, ArrayList<Key> keys)
    {
       super(context,0,keys) ;

       this.context = context ;
       this.keys = keys ;

    }


    @Override
    public int getCount() {
        if(keys!=null)
            return keys.size() ;
        else
            return 0 ;

    }

    @Override
    public Key getItem(int position) {
        if(keys!=null)
            return keys.get(position) ;
        else
            return null ;

    }

    @Override
    public long getItemId(int position) {
        if(keys!=null)
            return keys.get(position).hashCode() ;
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView ;

        if(v==null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
            v = inflater.inflate(R.layout.activity_listview,null) ;

        }

        Key key = keys.get(position) ;

        TextView key_text = (TextView)v.findViewById(R.id.key) ;
        TextView create_date_text = (TextView)v.findViewById(R.id.create_date) ;
        TextView expire_date_text = (TextView)v.findViewById(R.id.expire_date) ;
        ImageButton delete_button = (ImageButton)v.findViewById(R.id.delete) ;

        // pour les dates
        SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy") ;


        // remplire les champs
        key_text.setText(key.getKey()) ;
        create_date_text.setText(format.format(key.getCreate_date())) ;
        expire_date_text.setText(format.format(key.getExpire_date())) ;
        // bouton pour supprimer
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // trouver le parent et le text qui contient la clé
                View parentRow = (View) v.getParent();
                TextView key_in_row = parentRow.findViewById(R.id.key) ;
                final String current_key = (String) key_in_row.getText() ;


                // message pour supprimer
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                // supprimer la clé
                                new SaveOrDeleteTask(context,keys,MainActivity.keyListAdapter,MainActivity.progressDialog).execute(MainActivity.DELETE_API_URL, current_key,"delete") ;
                              //  MainActivity.keyListAdapter.remove(MainActivity.keyListAdapter.getItem(position)) ;
                              //  MainActivity.keyListAdapter.notifyDataSetChanged() ;

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss() ;
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("êtes vous sure de vouloir supprimer la clé "+current_key).setPositiveButton("oui", dialogClickListener)
                        .setNegativeButton("annuler", dialogClickListener).show();
            }
        });
        return v;
    }

    public ArrayList<Key> getList()
    {
        return this.keys ;
    }

    public void setList(ArrayList<Key> keys)
    {
        this.keys = keys ;
    }

    /*
    @Override
    public void clear() {
        keys.clear();
        notifyDataSetChanged();
        super.clear();
    }*/
}
