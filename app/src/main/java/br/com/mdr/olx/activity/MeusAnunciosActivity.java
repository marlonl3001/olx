package br.com.mdr.olx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.mdr.olx.R;
import br.com.mdr.olx.adapter.AnuncioAdapter;
import br.com.mdr.olx.helper.ConfiguracaoFirebase;
import br.com.mdr.olx.helper.UsuarioFirebase;
import br.com.mdr.olx.model.Anuncio;

public class MeusAnunciosActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private DatabaseReference anunciosRef;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AnuncioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeusAnunciosActivity.this, CadastroAnuncioActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        configuraComponentes();
        fetchAnuncios();
    }

    private void configuraComponentes() {
        adapter = new AnuncioAdapter(anuncios);
        progressBar = findViewById(R.id.progressAnuncios);
        progressBar.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.recyclerAnuncios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void fetchAnuncios() {
        progressBar.setVisibility(View.VISIBLE);
        anunciosRef = ConfiguracaoFirebase.getFirebase().child("meusAnuncios")
                .child(UsuarioFirebase.getIdUsuario());
        anunciosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                anuncios.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot item: dataSnapshot.getChildren())
                        anuncios.add(item.getValue(Anuncio.class));

                    adapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
