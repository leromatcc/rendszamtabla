package com.rends.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity(name="Pessoa")
@Table(name="\"PESSOA\"")
public class PessoaEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 50)
    @Column(length = 50, name="\"nome\"")
    private String nome;

    @Size(max = 50)
    @Column(length = 50, name="\"endereco\"")
    private String endereco;

    @Size(max = 50)
    @Column(length = 50, name="\"idade\"")
    private String idade;

    public void setPermissaoEstabelecimentoPessoas(List<PermissaoEstabelecimentoPessoaEntity> permissaoEstabelecimentoPessoas) {
        this.permissaoEstabelecimentoPessoas = permissaoEstabelecimentoPessoas;
    }

    public List<PermissaoEstabelecimentoPessoaEntity> getPermissaoEstabelecimentoPessoas() {
        return permissaoEstabelecimentoPessoas;
    }

    @ManyToMany(mappedBy="pessoas", fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<PermissaoEstabelecimentoPessoaEntity> permissaoEstabelecimentoPessoas;

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return this.endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getIdade() {
        return this.idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

}
