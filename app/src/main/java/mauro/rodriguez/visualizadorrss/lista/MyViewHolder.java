package mauro.rodriguez.visualizadorrss.lista;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import mauro.rodriguez.visualizadorrss.R;

/**
 * Created by Mauro Rodriguez on 01/06/2015.
 */
public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtTitulo;
    public TextView txtFecha;
    public ImageView imgNoticia;

    private int position;
    private MyItemClick listener;

    public MyViewHolder(View itemView,MyItemClick listener) {
        super(itemView);
        this.txtFecha=(TextView)itemView.findViewById(R.id.txtFecha);
        this.txtTitulo = (TextView) itemView.findViewById(R.id.txtTitulo);
        this.imgNoticia = (ImageView) itemView.findViewById(R.id.imgNoticia);
        itemView.setOnClickListener(this);
        this.listener=listener;
    }


    public void setPosition(int p)
    {
        this.position = p;
    }

    @Override
    public void onClick(View v) {
        listener.clickEnNoticia(position);
    }
}
