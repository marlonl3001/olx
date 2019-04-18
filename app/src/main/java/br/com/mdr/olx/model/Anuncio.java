package br.com.mdr.olx.model;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import br.com.mdr.olx.helper.ConfiguracaoFirebase;
import br.com.mdr.olx.helper.UsuarioFirebase;

/**
 * Created by ${USER_NAME} on 17/04/2019.
 */
public class Anuncio {
    private String idAnuncio;
    private String idUsuario;
    private String titulo;
    private String desc;
    private Double valor;
    private String telefone;
    private List<String> fotos;

    public Anuncio() {
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                .child("meusAnuncios");
        setIdAnuncio(anuncioRef.push().getKey());
    }

    public void salvar() {
        String idUsuario = UsuarioFirebase.getIdUsuario();
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase().child("meusAnuncios")
                .child(idUsuario)
                .child(getIdAnuncio());
        anuncioRef.setValue(this);
    }

    public String getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }
}
