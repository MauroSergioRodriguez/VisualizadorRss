package mauro.rodriguez.visualizadorrss.lista;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import mauro.rodriguez.visualizadorrss.R;
import mauro.rodriguez.visualizadorrss.datos.MyThread;
import mauro.rodriguez.visualizadorrss.entidades.Noticia;

/**
 * Created by Mauro Rodriguez on 01/06/2015.
 */
public class MyAdapter extends Adapter<MyViewHolder>{
    private List<Noticia> lista;
    private MyItemClick listener;

    public MyAdapter(List<Noticia> lista, MyItemClick listener)
    {
        this.lista = lista;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.noticias_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(v,this.listener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Noticia n = lista.get(position);
        holder.txtTitulo.setText(n.getTitulo());
        holder.txtFecha.setText(n.getFecha());
        if(n.getImg() != null){
            holder.imgNoticia.setImageBitmap(n.getImg());
        }
        holder.setPosition(position);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}
