package mauro.rodriguez.visualizadorrss.manejadores;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import mauro.rodriguez.visualizadorrss.activities.AgregarActivity;
import mauro.rodriguez.visualizadorrss.activities.DescActivity;
import mauro.rodriguez.visualizadorrss.activities.MainActivity;
import mauro.rodriguez.visualizadorrss.activities.NavegadorActivity;
import mauro.rodriguez.visualizadorrss.datos.AdminSQLite;
import mauro.rodriguez.visualizadorrss.R;
import mauro.rodriguez.visualizadorrss.entidades.Noticia;
import mauro.rodriguez.visualizadorrss.lista.MyItemClick;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by Mauro Rodriguez on 01/06/2015.
 */
public class EventManager implements MyItemClick, View.OnClickListener, AdapterView.OnItemSelectedListener {
    private List<Noticia> listaNoticias=null;
    private Activity contexto;
    private static ScreenManager sm;
    private EditText edtNombreRss;
    private EditText edtUrlRss;
    public  EventManager(Activity contexto){
        this.contexto=contexto;
    }

    public void setSm(ScreenManager sm){
        this.sm=sm;
    }

    public void setListaNoticias(List<Noticia> lista){
        this.listaNoticias=lista;
    }

    @Override
    public void clickEnNoticia(int pos) {
        Noticia n= this.listaNoticias.get(pos);
        Intent i = new Intent(this.contexto, DescActivity.class);
        i.putExtra("html",n.getDesc());
        i.putExtra("link",n.getLink());
        if(n.getImg() != null){
            Bitmap bmp = n.getImg();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            i.putExtra("img",byteArray);
        }
        this.contexto.startActivity(i);
        contexto.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnVerMas){
            Intent i = new Intent(this.contexto, NavegadorActivity.class);
            i.putExtra("url",ScreenManager.link);
            this.contexto.startActivity(i);
            contexto.overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }else if(v.getId() == R.id.btnAgregarRss){
            Intent i = new Intent(this.contexto, AgregarActivity.class);
            this.contexto.startActivity(i);
            contexto.overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }else if(v.getId() == R.id.btnAceptar){
            this.edtNombreRss = (EditText) contexto.findViewById(R.id.edtNombreRss);
            this.edtUrlRss = (EditText) contexto.findViewById(R.id.edtUrlRss);
            if(this.edtUrlRss.getText().length() != 0 && this.edtNombreRss.getText().length() != 0){
                AdminSQLite admin = new AdminSQLite(contexto,"BaseRss",null,1);
                SQLiteDatabase bd = admin.getWritableDatabase();
                Cursor cantidad= bd.rawQuery("select * from rss", null);
                cantidad.moveToLast();
                int id = cantidad.getInt(0) + 1;
                cantidad.close();
                ContentValues registro = new ContentValues();
                registro.put("id", id);
                registro.put("nombre",this.edtNombreRss.getText().toString());
                registro.put("url", this.edtUrlRss.getText().toString());
                bd.insert("rss", null, registro);
                bd.close();
            }
            Intent i = new Intent(this.contexto,MainActivity.class);
            this.contexto.startActivity(i);
            contexto.overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }else if(v.getId() == R.id.btnCancelar){
            this.contexto.finish();
            this.contexto.overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        AdminSQLite admin = new AdminSQLite(contexto,"BaseRss",null,1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from rss", null);
        while(fila.moveToNext()){
            if(parent.getItemAtPosition(position).equals(fila.getString(1))){
                Log.d("fila", "entro" + fila.getString(2));
                this.listaNoticias=sm.traerDatosRss(fila.getString(2));
            }
        }
        fila.close();
        bd.close();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
