package br.com.mdr.olx.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

import br.com.mdr.olx.helper.ConfiguracaoFirebase;
import br.com.mdr.olx.helper.UsuarioFirebase;

/**
 * Created by ${USER_NAME} on 17/04/2019.
 */
public class Anuncio implements Serializable {
    private String idAnuncio;
    private String idUsuario;
    private String titulo;
    private String desc;
    private String valor;
    private String telefone;
    private String estado;
    private String categoria;
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
        salvarPublico();
    }

    private void salvarPublico() {
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase().child("anuncios")
                .child(getEstado())
                .child(getCategoria())
                .child(getIdAnuncio());
        anuncioRef.setValue(this);
    }

    public void remover() {
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase().child("meusAnuncios")
                .child(UsuarioFirebase.getIdUsuario())
                .child(getIdAnuncio());

        anuncioRef.removeValue();
        removerPublico();
    }

    private void removerPublico() {
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase().child("anuncios")
                .child(getEstado())
                .child(getCategoria())
                .child(getIdAnuncio());

        anuncioRef.removeValue();
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

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }
}
