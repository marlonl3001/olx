package br.com.mdr.olx.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.mdr.olx.R;
import br.com.mdr.olx.helper.ConfiguracaoFirebase;
import br.com.mdr.olx.helper.UsuarioFirebase;
import br.com.mdr.olx.model.Anuncio;

public class CadastroAnuncioActivity extends AppCompatActivity {
    private ImageView img1, img2, img3;
    private Spinner spinnerEstados, spinnerCategorias;
    private EditText edtTitulo, edtDescricao, edtTelefone;
    private CurrencyEditText edtValor;
    private ProgressBar progressBar;

    private Bitmap bitmapImage;
    private Uri selectedImage;
    private List<String> caminhosImagem = new ArrayList<>();
    private List<String> imagensUrl = new ArrayList<>();
    private Anuncio anuncio = new Anuncio();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_anuncio);

        iniciaComponentes();
    }

    private void iniciaComponentes() {
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);

        edtTitulo = findViewById(R.id.edtTitulo);
        edtValor = findViewById(R.id.edtVlr);
        //Configura a moeda da edittext
        edtValor.setLocale(new Locale("pt", "BR"));
        edtDescricao = findViewById(R.id.edtDesc);
        edtTelefone = findViewById(R.id.edtTelefone);

        //Configura os spinners
        spinnerEstados = findViewById(R.id.spinnerEstados);
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, estados);
        adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstados.setAdapter(adapterEstados);

        spinnerCategorias = findViewById(R.id.spinnerCategorias);
        String[] categorias = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> adapterCaregorias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapterCaregorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategorias.setAdapter(adapterCaregorias);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    public void onClick(View view) {
        int requestCode = view.getId() == R.id.img1 ? 1 : view.getId() == R.id.img2 ? 2 : 3;
        switch (view.getId()) {
            case R.id.img1:
            case R.id.img2:
            case R.id.img3: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
                } else {
                    abreAlbum(requestCode);
                }
                break;
            }
            case R.id.btnCadastrar: {
                salvaAnuncio();
                break;
            }

        }
    }

    private void abreAlbum(int requestCode) {
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                abreAlbum(requestCode);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == 1 || requestCode == 2 || requestCode == 3) && resultCode == Activity.RESULT_OK && data != null) {
            try {
                selectedImage = data.getData();
                //bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                String caminhoImagem = selectedImage.toString();
                boolean fotoSelecionada = false;
                //Verifica se imagem já foi selecionada
                for (String caminho: caminhosImagem)
                    if (caminho.equals(caminhoImagem)) {
                        mostraMensagem("Imagem já selecionada");
                        fotoSelecionada = true;
                        break;
                    }

                if (fotoSelecionada)
                    return;

                switch (requestCode) {
                    case 1: {
                        img1.setImageURI(selectedImage);
                        break;
                    }
                    case 2: {
                        img2.setImageURI(selectedImage);
                        break;
                    }
                    case 3: {
                        img3.setImageURI(selectedImage);
                        break;
                    }
                }
                caminhosImagem.add(caminhoImagem);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void mostraMensagem(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    private void salvaAnuncio() {
        progressBar.setVisibility(View.VISIBLE);
        try {
            if (podeSalvar()) {
                String idUsuario = UsuarioFirebase.getIdUsuario();
                String imageName = "";

                String estado = spinnerEstados.getSelectedItem().toString();
                String categoria = spinnerCategorias.getSelectedItem().toString();
                String titulo = edtTitulo.getText().toString();
                String valor = String.valueOf(edtValor.getText().toString());
                String telefone = edtTelefone.getText().toString();
                String descricao = edtDescricao.getText().toString();

                anuncio.setIdUsuario(idUsuario);
                anuncio.setTitulo(titulo);
                anuncio.setDesc(descricao);
                anuncio.setValor(valor);
                anuncio.setTelefone(telefone);
                anuncio.setEstado(estado);
                anuncio.setCategoria(categoria);

                for (int i = 1; i <= caminhosImagem.size(); i++ ) {
                    imageName = caminhosImagem.get(i - 1);
                    enviaImagem(imageName, caminhosImagem.size(), i);
                }

                //mostraMensagem("Anúncio salvo com sucesso!");
            } else {
                progressBar.setVisibility(View.GONE);
                mostraMensagem("Preencha todos os campos.");
            }

        } catch(Exception e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
            mostraMensagem("Erro ao salvar anúncio: " + e.getLocalizedMessage());
        }
    }

    private boolean podeSalvar() {
        String estado = spinnerEstados.getSelectedItem().toString();
        String categoria = spinnerCategorias.getSelectedItem().toString();
        String titulo = edtTitulo.getText().toString();
        String valor = String.valueOf(edtValor.getText().toString());
        String telefone = edtTelefone.getText().toString();
        String descricao = edtDescricao.getText().toString();

        return !(estado.isEmpty() || categoria.isEmpty() || titulo.isEmpty() || valor.equals("0") ||
                telefone.isEmpty() || descricao.isEmpty());
    }

    private void enviaImagem(final String imageName, final int qtdImagens, final int indiceImagem) {

        final StorageReference storageReference = ConfiguracaoFirebase
                .getFirebaseStorage()
                .child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem"+indiceImagem);

        UploadTask uploadTask = storageReference.putFile(Uri.parse(imageName));

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful())
                    throw task.getException();

                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {
                    Uri imageUrl = task.getResult();
                    imagensUrl.add(imageUrl.toString());
                    if (indiceImagem == qtdImagens) {
                        anuncio.setFotos(imagensUrl);
                        anuncio.salvar();
                        progressBar.setVisibility(View.GONE);
                        finish();
                    }
                } else
                    mostraMensagem("Erro ao salvar imagem." + task.getException().getLocalizedMessage());
            }
        });
    }
}
