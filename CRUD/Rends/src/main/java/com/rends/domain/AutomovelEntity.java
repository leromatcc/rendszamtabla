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
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity(name="Automovel")
@Table(name="\"AUTOMOVEL\"")
public class AutomovelEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 50)
    @Column(length = 50, name="\"placa\"")
    private String placa;

    @Size(max = 50)
    @Column(length = 50, name="\"descricao\"")
    private String descricao;

    @ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name="AUTOMOVEL_AUTOMOVELTIPOS",
              joinColumns={@JoinColumn(name="AUTOMOVEL_ID", referencedColumnName="ID")},
              inverseJoinColumns={@JoinColumn(name="AUTOMOVELTIPO_ID", referencedColumnName="ID")})
    private List<AutomovelTipoEntity> automovelTipos;

    public void setPessoaAutomovels(List<PessoaAutomovelEntity> pessoaAutomovels) {
        this.pessoaAutomovels = pessoaAutomovels;
    }

    public List<PessoaAutomovelEntity> getPessoaAutomovels() {
        return pessoaAutomovels;
    }

    @ManyToMany(mappedBy="automovels", fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<PessoaAutomovelEntity> pessoaAutomovels;

    public String getPlaca() {
        return this.placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<AutomovelTipoEntity> getAutomovelTipos() {
        return automovelTipos;
    }

    public void setAutomovelTipos(List<AutomovelTipoEntity> automovelTipos) {
        this.automovelTipos = automovelTipos;
    }

}
