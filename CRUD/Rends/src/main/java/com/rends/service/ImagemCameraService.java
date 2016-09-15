package com.rends.service;

import com.rends.domain.CameraEntity;
import com.rends.domain.ImagemCameraEntity;
import com.rends.service.CameraService;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
public class ImagemCameraService extends BaseService<ImagemCameraEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public ImagemCameraService(){
        super(ImagemCameraEntity.class);
    }
    
    @Transactional
    public List<ImagemCameraEntity> findAllImagemCameraEntities() {
        
        return entityManager.createQuery("SELECT o FROM ImagemCamera o ", ImagemCameraEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM ImagemCamera o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(ImagemCameraEntity imagemCamera) {

        /* This is called before a ImagemCamera is deleted. Place here all the
           steps to cut dependencies to other entities */
        
    }

    @Transactional
    public List<ImagemCameraEntity> findAvailableImagemCameras(CameraEntity camera) {
        return entityManager.createQuery("SELECT o FROM ImagemCamera o where o.id not in (select o.id from ImagemCamera o join o.cameras p where p = :p)", ImagemCameraEntity.class).setParameter("p", camera).getResultList();
    }

    @Transactional
    public List<ImagemCameraEntity> findImagemCamerasByCamera(CameraEntity camera) {
        return entityManager.createQuery("SELECT o FROM ImagemCamera o where o.id in (select o.id from ImagemCamera o join o.cameras p where p = :p)", ImagemCameraEntity.class).setParameter("p", camera).getResultList();
    }

    @Transactional
    public ImagemCameraEntity fetchCameras(ImagemCameraEntity imagemCamera) {
        imagemCamera = find(imagemCamera.getId());
        imagemCamera.getCameras().size();
        return imagemCamera;
    }
    
}
