package com.rends.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity(name="Endereco")
@Table(name="\"ENDERECO\"")
public class EnderecoEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 50)
    @Column(length = 50, name="\"descricao\"")
    private String descricao;

    @Size(max = 50)
    @Column(length = 50, name="\"cidade\"")
    private String cidade;

    @Size(max = 50)
    @Column(length = 50, name="\"estado\"")
    private String estado;

    @Size(max = 50)
    @Column(length = 50, name="\"pais\"")
    private String pais;

    @Size(max = 50)
    @Column(length = 50, name="\"cep\"")
    private String cep;

    @Size(max = 50)
    @Column(length = 50, name="\"logradouro\"")
    private String logradouro;

    @Size(max = 50)
    @Column(length = 50, name="\"numero\"")
    private String numero;

    @Column(name="\"TIPO\"")
    @Enumerated(EnumType.STRING)
    private EnderecoTipo tipo;

    @Size(max = 50)
    @Column(length = 50, name="\"complemento\"")
    private String complemento;

    @Size(max = 50)
    @Column(length = 50, name="\"referencia\"")
    private String referencia;

    public void setPessoas(List<PessoaEntity> pessoas) {
        this.pessoas = pessoas;
    }

    public List<PessoaEntity> getPessoas() {
        return pessoas;
    }

    @ManyToMany(mappedBy="enderecos", fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<PessoaEntity> pessoas;

    public void setEmpresas(List<EmpresaEntity> empresas) {
        this.empresas = empresas;
    }

    public List<EmpresaEntity> getEmpresas() {
        return empresas;
    }

    @ManyToMany(mappedBy="enderecos", fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<EmpresaEntity> empresas;

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCidade() {
        return this.cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return this.pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCep() {
        return this.cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return this.logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return this.numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public EnderecoTipo getTipo() {
        return tipo;
    }

    public void setTipo(EnderecoTipo tipo) {
        this.tipo = tipo;
    }

    public String getComplemento() {
        return this.complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getReferencia() {
        return this.referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

}
