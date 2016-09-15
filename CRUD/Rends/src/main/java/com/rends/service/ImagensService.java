package com.rends.service;

import com.rends.domain.CameraEntity;
import com.rends.domain.ImagemCameraEntity;
import com.rends.domain.ImagensEntity;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceUnitUtil;
import javax.transaction.Transactional;

@Named
public class ImagensService extends BaseService<ImagensEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public ImagensService(){
        super(ImagensEntity.class);
    }
    
    @Inject
    private ImagensAttachmentService attachmentService;
    
    @Transactional
    public List<ImagensEntity> findAllImagensEntities() {
        
        return entityManager.createQuery("SELECT o FROM Imagens o LEFT JOIN FETCH o.image", ImagensEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Imagens o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(ImagensEntity imagens) {

        /* This is called before a Imagens is deleted. Place here all the
           steps to cut dependencies to other entities */
        
        this.attachmentService.deleteAttachmentsByImagens(imagens);
        
        this.cutAllImagemImagemCamerasAssignments(imagens);
        
    }

    // Remove all assignments from all imagemCamera a imagens. Called before delete a imagens.
    @Transactional
    private void cutAllImagemImagemCamerasAssignments(ImagensEntity imagens) {
        entityManager
                .createQuery("UPDATE ImagemCamera c SET c.imagem = NULL WHERE c.imagem = :p")
                .setParameter("p", imagens).executeUpdate();
    }
    
    // Find all imagemCamera which are not yet assigned to a imagens
    @Transactional
    public List<ImagensEntity> findAvailableImagem(ImagemCameraEntity imagemCamera) {
        Long id = -1L;
        if (imagemCamera != null && imagemCamera.getImagem() != null && imagemCamera.getImagem().getId() != null) {
            id = imagemCamera.getImagem().getId();
        }
        return entityManager.createQuery(
                "SELECT o FROM Imagens o where o.id NOT IN (SELECT p.imagem.id FROM ImagemCamera p where p.imagem.id != :id)", ImagensEntity.class)
                .setParameter("id", id).getResultList();    
    }

    @Transactional
    public ImagensEntity lazilyLoadImageToImagens(ImagensEntity imagens) {
        PersistenceUnitUtil u = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        if (!u.isLoaded(imagens, "image") && imagens.getId() != null) {
            imagens = find(imagens.getId());
            imagens.getImage().getId();
        }
        return imagens;
    }
    
}
