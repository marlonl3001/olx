package br.com.mdr.olx.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.mdr.olx.R;
import br.com.mdr.olx.model.Anuncio;

/**
 * Created by ${USER_NAME} on 17/04/2019.
 */
public class AnuncioAdapter extends RecyclerView.Adapter<AnuncioAdapter.MyViewHolder> {
    private List<Anuncio> anuncios = new ArrayList<>();

    public AnuncioAdapter(List<Anuncio> anuncios) {
        this.anuncios = anuncios;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.anuncio_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        Anuncio anuncio = anuncios.get(position);
        Picasso.get().load(anuncio.getFotos().get(0)).into(myViewHolder.imgProduto);
        myViewHolder.txtNomeProduto.setText(anuncio.getTitulo());
        String valorAnuncio = anuncio.getValor();
        myViewHolder.txtVlr.setText(valorAnuncio);
    }

    @Override
    public int getItemCount() {
        return anuncios.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgProduto;
        private TextView txtNomeProduto;
        private TextView txtVlr;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduto = itemView.findViewById(R.id.imgItem);
            txtNomeProduto = itemView.findViewById(R.id.txtDesc);
            txtVlr = itemView.findViewById(R.id.txtVlr);
        }
    }
}
