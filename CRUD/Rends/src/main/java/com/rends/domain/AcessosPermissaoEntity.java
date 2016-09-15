package com.rends.domain;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

@Entity(name="AcessosPermissao")
@Table(name="\"ACESSOSPERMISSAO\"")
public class AcessosPermissaoEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 50)
    @Column(length = 50, name="\"descricao\"")
    private String descricao;

    @ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name="ACESSOSPERMISSAO_PESSOAS",
              joinColumns={@JoinColumn(name="ACESSOSPERMISSAO_ID", referencedColumnName="ID")},
              inverseJoinColumns={@JoinColumn(name="PESSOA_ID", referencedColumnName="ID")})
    private List<PessoaEntity> pessoas;

    @OneToOne(optional=true, cascade=CascadeType.DETACH)
    @JoinColumn(name="EMPRESA_ID", nullable=true)
    private EmpresaEntity empresa;

    @Column(name="\"dataInicio\"")
    @Temporal(TemporalType.DATE)
    private Date dataInicio;

    @Column(name="\"dataFinal\"")
    @Temporal(TemporalType.DATE)
    private Date dataFinal;

    @Column(name="\"ativo\"")
    private Boolean ativo;

    public void setAcessosRegistross(List<AcessosRegistrosEntity> acessosRegistross) {
        this.acessosRegistross = acessosRegistross;
    }

    public List<AcessosRegistrosEntity> getAcessosRegistross() {
        return acessosRegistross;
    }

    @ManyToMany(mappedBy="permissaoAcessos", fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<AcessosRegistrosEntity> acessosRegistross;

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<PessoaEntity> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<PessoaEntity> pessoas) {
        this.pessoas = pessoas;
    }

    public EmpresaEntity getEmpresa() {
        return this.empresa;
    }

    public void setEmpresa(EmpresaEntity empresa) {
        this.empresa = empresa;
    }

    public Date getDataInicio() {
        return this.dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFinal() {
        return this.dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

}
