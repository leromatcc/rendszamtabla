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

@Entity(name="AutomovelTipo")
@Table(name="\"AUTOMOVELTIPO\"")
public class AutomovelTipoEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 50)
    @Column(length = 50, name="\"descricao\"")
    private String descricao;

    public void setAutomovels(List<AutomovelEntity> automovels) {
        this.automovels = automovels;
    }

    public List<AutomovelEntity> getAutomovels() {
        return automovels;
    }

    @ManyToMany(mappedBy="automovelTipos", fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<AutomovelEntity> automovels;

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
