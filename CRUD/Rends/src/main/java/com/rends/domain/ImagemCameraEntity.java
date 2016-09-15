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

@Entity(name="ImagemCamera")
@Table(name="\"IMAGEMCAMERA\"")
public class ImagemCameraEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 1000)
    @Column(length = 1000, name="\"descricao\"")
    private String descricao;

    @OneToOne(optional=true, cascade=CascadeType.DETACH)
    @JoinColumn(name="IMAGEM_ID", nullable=true)
    private ImagensEntity imagem;

    @ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name="IMAGEMCAMERA_CAMERAS",
              joinColumns={@JoinColumn(name="IMAGEMCAMERA_ID", referencedColumnName="ID")},
              inverseJoinColumns={@JoinColumn(name="CAMERA_ID", referencedColumnName="ID")})
    private List<CameraEntity> cameras;

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ImagensEntity getImagem() {
        return this.imagem;
    }

    public void setImagem(ImagensEntity imagem) {
        this.imagem = imagem;
    }

    public List<CameraEntity> getCameras() {
        return cameras;
    }

    public void setCameras(List<CameraEntity> cameras) {
        this.cameras = cameras;
    }

}
