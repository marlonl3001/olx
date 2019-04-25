package br.com.mdr.olx.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import br.com.mdr.olx.R;
import br.com.mdr.olx.model.Anuncio;

public class DetalheProdutoActivity extends AppCompatActivity {
    private CarouselView carouselView;
    private TextView txtEstado, txtTitulo, txtDescricao, txtValor;
    private Anuncio anuncio;
    private ImageListener imageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_produto);
        getSupportActionBar().setTitle("Detalhe anúncio");

        anuncio = (Anuncio) getIntent().getSerializableExtra("anuncio");

        iniciaComponentes();
        setDadosAnuncio(anuncio);

    }

    private void iniciaComponentes() {
        carouselView = findViewById(R.id.carouselView);
        carouselView.setPageCount(anuncio.getFotos().size());
        txtTitulo = findViewById(R.id.txtTitulo);
        txtValor = findViewById(R.id.txtValor);
        txtDescricao = findViewById(R.id.txtDescricao);
        txtEstado = findViewById(R.id.txtEstado);

        imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                String urlImage = anuncio.getFotos().get(position);
                Picasso.get().load(urlImage).into(imageView);
            }
        };
        carouselView.setImageListener(imageListener);
    }

    private void setDadosAnuncio(final Anuncio anuncio) {
        txtTitulo.setText(anuncio.getTitulo());
        txtValor.setText(anuncio.getValor());
        txtDescricao.setText(anuncio.getDesc());
        txtEstado.setText(anuncio.getEstado());
    }

    public void onClick(View v) {
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", anuncio.getTelefone(), null));
        startActivity(i);
    }
}
