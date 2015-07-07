package mauro.rodriguez.visualizadorrss.manejadores;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import mauro.rodriguez.visualizadorrss.R;
import mauro.rodriguez.visualizadorrss.datos.AdminSQLite;
import mauro.rodriguez.visualizadorrss.datos.MyThread;
import mauro.rodriguez.visualizadorrss.entidades.Noticia;
import mauro.rodriguez.visualizadorrss.lista.MyAdapter;
import mauro.rodriguez.visualizadorrss.lista.MyItemClick;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import mauro.rodriguez.visualizadorrss.activities.AgregarActivity;
import mauro.rodriguez.visualizadorrss.activities.DescActivity;
import mauro.rodriguez.visualizadorrss.activities.MainActivity;
import mauro.rodriguez.visualizadorrss.activities.NavegadorActivity;

/**
 * Created by Mauro Rodriguez on 01/06/2015.
 */
public class ScreenManager{
    private Activity a;
    private RecyclerView list;
    private MyItemClick listener;
    private List<Noticia> listaNoticias = null;
    private MyThread miHilo;
    private Handler handler;
    private EventManager em;
    private MyAdapter adapter;
    private ImageView img;
    private ImageView imgNoticia;
    private Button btnAgregarRss;
    private Spinner spinnerRss;
    private WebView webDescripcion;
    private Button btnVerMas;
    private Button btnAceptar;
    private Button btnCancelar;
    public static String link;
    private WebView webNotica;

    private Handler.Callback listenerHttp;

    public ScreenManager(Activity a,MyItemClick listener,Handler.Callback listenerHttp, EventManager em) {
        this.a=a;
        this.listener=listener;
        this.listenerHttp = listenerHttp;
        this.em=em;

        if(a instanceof MainActivity)
            prepararMain();
        else if(a instanceof DescActivity)
            prepararDesc();
    }
    public ScreenManager(Activity a,EventManager em){
        this.a=a;
        this.em=em;
        if(a instanceof DescActivity){
            prepararDesc();
        }else if(a instanceof AgregarActivity){
            prepararAgregar();
        }else if(a instanceof NavegadorActivity){
            prepararNavegador();
        }
    }

    public void mostrarLista(List<Noticia> listaNoticias) {
        this.adapter = new MyAdapter(listaNoticias,this.listener);
        list.setAdapter(adapter);
    }

    public List<Noticia> traerDatosRss(String link){
        this.handler = new Handler(this.listenerHttp);
        this.miHilo = new MyThread(this.handler,link);
        this.miHilo.start();
        return listaNoticias;
    }

    private void prepararMain(){
        this.list = (RecyclerView) a.findViewById(R.id.list);
        this.img=(ImageView) this.a.findViewById(R.id.imgNoticia);
        this.btnAgregarRss = (Button) this.a.findViewById(R.id.btnAgregarRss);
        this.spinnerRss = (Spinner)this.a.findViewById(R.id.spinnerRss);

        LinearLayoutManager layoutManager = new LinearLayoutManager(a);
        this.list.setLayoutManager(layoutManager);
        this.btnAgregarRss.setOnClickListener(em);
        this.spinnerRss.setOnItemSelectedListener(em);

        actualizarSpinner();
    }

    public void actualizarSpinner() {
        AdminSQLite admin = new AdminSQLite(this.a,"BaseRss",null,1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor mCount= bd.rawQuery("select count(*) from rss", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        if(count == 0){
            ContentValues registro = new ContentValues();
            registro.put("id", 0);
            registro.put("nombre", "Infobae");
            registro.put("url", "http://www.infobae.com/rss/hoy.xml");
            bd.insert("rss", null, registro);

            ContentValues registro2 = new ContentValues();
            registro2.put("id", 1);
            registro2.put("nombre", "NASA");
            registro2.put("url", "http://www.nasa.gov/rss/dyn/lg_image_of_the_day.rss");
            bd.insert("rss", null, registro2);
        }
        Cursor fila = bd.rawQuery("select * from rss", null);
        List<String> opciones = new ArrayList<String>();
        while(fila.moveToNext()){
            opciones.add(fila.getString(1));
        }
        fila.close();
        bd.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.a,android.R.layout.simple_spinner_dropdown_item, opciones);
        this.spinnerRss.setAdapter(adapter);
    }

    private void prepararDesc() {
        this.webDescripcion = (WebView) this.a.findViewById(R.id.webDescripcion);
        this.imgNoticia = (ImageView) this.a.findViewById(R.id.imgNoticia);
        Bundle bundle = this.a.getIntent().getExtras();
        this.webDescripcion.loadData(bundle.getString("html"), "text/html; charset=UTF-8", null);
        if(bundle.getString("html").length() == 0){
            Toast.makeText(a.getApplicationContext(),"No hay informacion para mostrar!",Toast.LENGTH_SHORT);
        }
        if (bundle.getByteArray("img") != null){
            byte[] data = bundle.getByteArray("img");
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            this.imgNoticia.setImageBitmap(bmp);
        }
        this.link=bundle.getString("link");

        this.btnVerMas = (Button) this.a.findViewById(R.id.btnVerMas);
        this.btnVerMas.setOnClickListener(em);
    }

    private void prepararAgregar() {
        this.btnAceptar = (Button) a.findViewById(R.id.btnAceptar);
        this.btnCancelar = (Button) a.findViewById(R.id.btnCancelar);
        this.btnAceptar.setOnClickListener(em);
        this.btnCancelar.setOnClickListener(em);
    }

    private void prepararNavegador() {
        Bundle bundle = this.a.getIntent().getExtras();
        if(bundle.getString("url").length() > 0){
            this.webNotica = (WebView) a.findViewById(R.id.webNoticia);
            this.webNotica.setWebViewClient(new WebViewClient());
            WebSettings webSettings = webNotica.getSettings();
            webSettings.setJavaScriptEnabled(true);
            this.webNotica.loadUrl(bundle.getString("url"));
        }
    }

    public void actualizarImagenes(){
     this.adapter.notifyDataSetChanged();
    }
}
