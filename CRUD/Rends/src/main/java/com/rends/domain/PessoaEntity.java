package com.rends.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity(name="Pessoa")
@Table(name="\"PESSOA\"")
public class PessoaEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 50)
    @Column(length = 50, name="\"nome\"")
    private String nome;

    @ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name="PESSOA_ENDERECOS",
              joinColumns={@JoinColumn(name="PESSOA_ID", referencedColumnName="ID")},
              inverseJoinColumns={@JoinColumn(name="ENDERECO_ID", referencedColumnName="ID")})
    private List<EnderecoEntity> enderecos;

    @Size(max = 50)
    @Column(length = 50, name="\"documento\"")
    private String documento;

    @Column(name="\"DOCUMENTOTIPO\"")
    @Enumerated(EnumType.STRING)
    private PessoaDocumentoTipo documentoTipo;

    @Size(max = 50)
    @Column(length = 50, name="\"telefone\"")
    @NotNull
    private String telefone;

    @Column(name="\"TELEFONETIPO\"")
    @Enumerated(EnumType.STRING)
    private PessoaTelefoneTIpo telefoneTIpo;

    public void setAcessosPermissaos(List<AcessosPermissaoEntity> acessosPermissaos) {
        this.acessosPermissaos = acessosPermissaos;
    }

    public List<AcessosPermissaoEntity> getAcessosPermissaos() {
        return acessosPermissaos;
    }

    @ManyToMany(mappedBy="pessoas", fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<AcessosPermissaoEntity> acessosPermissaos;

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<EnderecoEntity> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<EnderecoEntity> enderecos) {
        this.enderecos = enderecos;
    }

    public String getDocumento() {
        return this.documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public PessoaDocumentoTipo getDocumentoTipo() {
        return documentoTipo;
    }

    public void setDocumentoTipo(PessoaDocumentoTipo documentoTipo) {
        this.documentoTipo = documentoTipo;
    }

    public String getTelefone() {
        return this.telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public PessoaTelefoneTIpo getTelefoneTIpo() {
        return telefoneTIpo;
    }

    public void setTelefoneTIpo(PessoaTelefoneTIpo telefoneTIpo) {
        this.telefoneTIpo = telefoneTIpo;
    }

}
