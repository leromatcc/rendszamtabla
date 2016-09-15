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

@Entity(name="Empresa")
@Table(name="\"EMPRESA\"")
public class EmpresaEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 500)
    @Column(length = 500, name="\"descricao\"")
    private String descricao;

    @ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name="EMPRESA_ENDERECOS",
              joinColumns={@JoinColumn(name="EMPRESA_ID", referencedColumnName="ID")},
              inverseJoinColumns={@JoinColumn(name="ENDERECO_ID", referencedColumnName="ID")})
    private List<EnderecoEntity> enderecos;

    @Size(max = 500)
    @Column(length = 500, name="\"nome\"")
    private String nome;

    public void setCameras(List<CameraEntity> cameras) {
        this.cameras = cameras;
    }

    public List<CameraEntity> getCameras() {
        return cameras;
    }

    @ManyToMany(mappedBy="estabelecimentos", fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<CameraEntity> cameras;

    public void setEstacionamentos(List<EstacionamentoEntity> estacionamentos) {
        this.estacionamentos = estacionamentos;
    }

    public List<EstacionamentoEntity> getEstacionamentos() {
        return estacionamentos;
    }

    @ManyToMany(mappedBy="empresas", fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<EstacionamentoEntity> estacionamentos;

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<EnderecoEntity> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<EnderecoEntity> enderecos) {
        this.enderecos = enderecos;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
