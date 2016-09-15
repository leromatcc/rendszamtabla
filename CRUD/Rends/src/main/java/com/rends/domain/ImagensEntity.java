package com.rends.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity(name="Imagens")
@Table(name="\"IMAGENS\"")
public class ImagensEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private ImagensImage image;
    
    @Size(max = 1000)
    @Column(length = 1000, name="\"descricao\"")
    private String descricao;

    public ImagensImage getImage() {
        return image;
    }

    public void setImage(ImagensImage image) {
        this.image = image;
    }
    
    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
