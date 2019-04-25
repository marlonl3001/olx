package br.com.mdr.olx.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.mdr.ifood.listener.RecyclerItemClickListener;
import br.com.mdr.olx.R;
import br.com.mdr.olx.adapter.AnuncioAdapter;
import br.com.mdr.olx.helper.ConfiguracaoFirebase;
import br.com.mdr.olx.model.Anuncio;
import dmax.dialog.SpotsDialog;

public class AnunciosActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth auth;
    private AnuncioAdapter adapter;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AlertDialog dialog;
    private RecyclerView recyclerAnuncios;
    private Button btnCategoria, btnRegiao;
    private DatabaseReference anunciosRef;
    private String filtroRegiao = "", filtroCategoria = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        configuraComponentes();
        fetchAnuncios();
    }

    private void configuraComponentes() {
        btnCategoria = findViewById(R.id.btnCategoria);
        btnCategoria.setOnClickListener(this);
        btnRegiao = findViewById(R.id.btnRegiao);
        btnRegiao.setOnClickListener(this);
        adapter = new AnuncioAdapter(anuncios);

        recyclerAnuncios = findViewById(R.id.recyclerItens);
        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncios.setHasFixedSize(true);
        recyclerAnuncios.setAdapter(adapter);
        recyclerAnuncios.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerAnuncios,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Anuncio anuncio = anuncios.get(position);
                        Intent i = new Intent(AnunciosActivity.this, DetalheProdutoActivity.class);
                        i.putExtra("anuncio", anuncio);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Anuncio anuncio = anuncios.get(position);
                        anuncio.remover();
                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anuncios_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (auth.getCurrentUser() == null) {
            menu.setGroupVisible(R.id.group_deslogado, true);
            //menu.setGroupVisible(R.id.group_logado, false);
        } else {
            //menu.setGroupVisible(R.id.group_deslogado, false);
            menu.setGroupVisible(R.id.group_logado, true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_cadastro: {
                startActivity(new Intent(AnunciosActivity.this, AutenticacaoActivity.class));
                finish();
                break;
            }
            case R.id.item_sair: {
                auth.signOut();
                invalidateOptionsMenu();
                break;
            }
            case R.id.item_anuncios: {
                startActivity(new Intent(AnunciosActivity.this, MeusAnunciosActivity.class));
                break;
            }
            case R.id.item_limpar: {
                fetchAnuncios();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCategoria: {
                if (filtroRegiao.isEmpty())
                    mostraMensagem("Você precisa selecionar a região para selecionar uma categoria.");
                else
                    mostraDialogCategorias();
                break;
            }
            case R.id.btnRegiao: {
                mostraDialogEstados();
                break;
            }
        }
    }

    private void mostraDialogEstados() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione o estado desejado.");
        View v = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

        final Spinner spinnerRegiao = v.findViewById(R.id.spinnerRegiao);
        String[] estados = getResources().getStringArray(R.array.estados);
        final ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, estados);
        adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegiao.setAdapter(adapterEstados);

        builder.setView(v);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filtroRegiao = spinnerRegiao.getSelectedItem().toString();
                fetchAnunciosEstado();
                dialog.dismiss();
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void mostraDialogCategorias() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione a categoria desejado.");
        View v = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

        final Spinner spinnerCategorias = v.findViewById(R.id.spinnerRegiao);
        String[] categorias = getResources().getStringArray(R.array.categorias);
        final ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategorias.setAdapter(adapterEstados);

        builder.setView(v);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filtroCategoria = spinnerCategorias.getSelectedItem().toString();
                fetchAnunciosCategoria();
                dialog.dismiss();
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void fetchAnuncios() {
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando anúncios.")
                .setCancelable(false)
                .build();
        dialog.show();

        anuncios.clear();
        anunciosRef = ConfiguracaoFirebase.getFirebase().child("anuncios");
        anunciosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Percorre os anúncios por estados
                    for(DataSnapshot estado: dataSnapshot.getChildren()) {
                        //Filtra pelas categorias
                        for(DataSnapshot categoria: estado.getChildren()) {
                            //Recupera os anuncios
                            for(DataSnapshot anuncio: categoria.getChildren()) {
                                Anuncio a = anuncio.getValue(Anuncio.class);
                                anuncios.add(a);

                            }
                        }
                    }
                    Collections.reverse(anuncios);
                    adapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
            }
        });

    }

    private void fetchAnunciosEstado() {
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando anúncios.")
                .setCancelable(false)
                .build();
        dialog.show();
        anuncios.clear();
        anunciosRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios")
                .child(filtroRegiao);
        anunciosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Filtra pelas categorias
                    for(DataSnapshot categoria: dataSnapshot.getChildren()) {
                        //Recupera os anuncios
                        for(DataSnapshot anuncio: categoria.getChildren()) {
                            Anuncio a = anuncio.getValue(Anuncio.class);
                            anuncios.add(a);
                        }
                    }
                    Collections.reverse(anuncios);
                }

                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
            }
        });

    }

    private void fetchAnunciosCategoria() {
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando anúncios.")
                .setCancelable(false)
                .build();
        dialog.show();
        anuncios.clear();
        anunciosRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios")
                .child(filtroRegiao)
                .child(filtroCategoria);
        anunciosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    filtroCategoria = "";
                    for(DataSnapshot anuncio: dataSnapshot.getChildren()) {
                        Anuncio a = anuncio.getValue(Anuncio.class);
                        anuncios.add(a);
                    }
                    Collections.reverse(anuncios);
                }

                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
            }
        });

    }

    private void mostraMensagem(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
