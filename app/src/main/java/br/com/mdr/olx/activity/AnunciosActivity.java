package br.com.mdr.olx.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import br.com.mdr.olx.R;
import br.com.mdr.olx.adapter.AnuncioAdapter;
import br.com.mdr.olx.helper.ConfiguracaoFirebase;
import br.com.mdr.olx.model.Anuncio;

public class AnunciosActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private AnuncioAdapter adapter;
    private List<Anuncio> anuncios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
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
        }
        return super.onOptionsItemSelected(item);
    }
}
