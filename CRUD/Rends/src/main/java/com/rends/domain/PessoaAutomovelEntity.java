package com.rends.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity(name="PessoaAutomovel")
@Table(name="\"PESSOAAUTOMOVEL\"")
public class PessoaAutomovelEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name="PESSOAAUTOMOVEL_AUTOMOVELS",
              joinColumns={@JoinColumn(name="PESSOAAUTOMOVEL_ID", referencedColumnName="ID")},
              inverseJoinColumns={@JoinColumn(name="AUTOMOVEL_ID", referencedColumnName="ID")})
    private List<AutomovelEntity> automovels;

    @OneToOne(optional=true, cascade=CascadeType.DETACH)
    @JoinColumn(name="PESSOA_ID", nullable=true)
    private PessoaEntity pessoa;

    @Size(max = 50)
    @Column(length = 50, name="\"descricao\"")
    private String descricao;

    public List<AutomovelEntity> getAutomovels() {
        return automovels;
    }

    public void setAutomovels(List<AutomovelEntity> automovels) {
        this.automovels = automovels;
    }

    public PessoaEntity getPessoa() {
        return this.pessoa;
    }

    public void setPessoa(PessoaEntity pessoa) {
        this.pessoa = pessoa;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
