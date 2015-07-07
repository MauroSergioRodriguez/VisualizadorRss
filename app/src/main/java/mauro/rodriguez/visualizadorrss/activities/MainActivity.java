package mauro.rodriguez.visualizadorrss.activities;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import mauro.rodriguez.visualizadorrss.R;
import mauro.rodriguez.visualizadorrss.datos.AdminSQLite;
import mauro.rodriguez.visualizadorrss.datos.MyThread;
import mauro.rodriguez.visualizadorrss.datos.ParserNoticias;
import mauro.rodriguez.visualizadorrss.entidades.Noticia;
import mauro.rodriguez.visualizadorrss.manejadores.EventManager;
import mauro.rodriguez.visualizadorrss.manejadores.ScreenManager;

/**
 * Created by Mauro Rodriguez on 01/06/2015.
 */
public class MainActivity extends ActionBarActivity implements Handler.Callback, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {
    private List<Noticia> listaNoticias;
    private String rss = null;
    private ScreenManager sm;
    private EventManager em;
    private List<Noticia> listaNoticiasFiltradas;
    private SwipeRefreshLayout swipeRefresh;
    private ThreadPoolExecutor executor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            this.swipeRefresh=(SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
            swipeRefresh.setOnRefreshListener(this);
            em=new EventManager(this);
            sm = new ScreenManager(this,em,this,em);
            em.setSm(sm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.arg1){
            case 0:
                rss= (String) msg.obj;
                StringReader sr = new StringReader(rss);
                ParserNoticias parser = new ParserNoticias(sr);
                try{
                    listaNoticias = parser.parseListaNoticias();
                }catch (Exception ex){
                    Log.d("hilo","salgo");
                    Toast.makeText(this,"Ha fallado la carga, reintente luego",Toast.LENGTH_LONG);
                    return false;
                }

                sm.mostrarLista(listaNoticias);
                em.setListaNoticias(listaNoticias);
                cargarImagenes(listaNoticias);
                break;
            case 1:
                if (msg.obj instanceof byte[]){
                    Log.d("devolvio", msg.obj.toString());
                    byte[] data=(byte[])msg.obj;
                    Bitmap bitmap= BitmapFactory.decodeByteArray(data, 0, data.length);
                    listaNoticias.get(msg.arg2).setImg(bitmap);
                    sm.actualizarImagenes();
                }
                break;
            case -1:
                Toast.makeText(this,"Rss Fallado, ha sido eliminado",Toast.LENGTH_LONG);
                Spinner spinner = (Spinner) findViewById(R.id.spinnerRss);
                String columna = (String)spinner.getSelectedItem();
                Log.d("url",columna);
                AdminSQLite admin = new AdminSQLite(this,"BaseRss",null,1);
                SQLiteDatabase bd = admin.getWritableDatabase();
                bd.execSQL("DELETE FROM rss WHERE rss.nombre = '" + columna + "'");
                bd.close();
                sm.actualizarSpinner();
                break;
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
     public boolean onQueryTextChange(String newText) {
        Log.d("hilo",newText);
        this.listaNoticiasFiltradas=new ArrayList<Noticia>();
        int i=0;
        int j=0;
        for(Noticia aux:this.listaNoticias){
            if(aux.getTitulo().indexOf(newText) != -1){
                listaNoticiasFiltradas.add(j,listaNoticias.get(i));
                j++;
            }
            i++;
        }
        if(this.listaNoticiasFiltradas.size()>0){
            this.sm.mostrarLista(this.listaNoticiasFiltradas);
            this.em.setListaNoticias(this.listaNoticiasFiltradas);
        }
        return false;
    }

    @Override
    public void onRefresh() {
        this.recreate();
        this.swipeRefresh.setRefreshing(false);
    }

    public void cargarImagenes(List<Noticia> lista){
        executor= (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        for(int i =0; i<lista.size();i++){
            if(lista.get(i).getLinkImg()!=null){
                Handler handler = new Handler(this);
                MyThread miHilo = new MyThread(handler,lista.get(i).getLinkImg(), i);
                executor.execute(miHilo);
            }
        }
    }
}
