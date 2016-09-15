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

@Entity(name="Estacionamento")
@Table(name="\"ESTACIONAMENTO\"")
public class EstacionamentoEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 50)
    @Column(length = 50, name="\"descricao\"")
    private String descricao;

    @Size(max = 50)
    @Column(length = 50, name="\"quantidadeVagas\"")
    private String quantidadeVagas;

    @ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name="ESTACIONAMENTO_EMPRESAS",
              joinColumns={@JoinColumn(name="ESTACIONAMENTO_ID", referencedColumnName="ID")},
              inverseJoinColumns={@JoinColumn(name="EMPRESA_ID", referencedColumnName="ID")})
    private List<EmpresaEntity> empresas;

    public void setAcessosRegistross(List<AcessosRegistrosEntity> acessosRegistross) {
        this.acessosRegistross = acessosRegistross;
    }

    public List<AcessosRegistrosEntity> getAcessosRegistross() {
        return acessosRegistross;
    }

    @ManyToMany(mappedBy="estacionamentos", fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<AcessosRegistrosEntity> acessosRegistross;

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getQuantidadeVagas() {
        return this.quantidadeVagas;
    }

    public void setQuantidadeVagas(String quantidadeVagas) {
        this.quantidadeVagas = quantidadeVagas;
    }

    public List<EmpresaEntity> getEmpresas() {
        return empresas;
    }

    public void setEmpresas(List<EmpresaEntity> empresas) {
        this.empresas = empresas;
    }

}
