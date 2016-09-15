package com.rends.web;

import com.rends.domain.CameraEntity;
import com.rends.service.CameraService;
import com.rends.service.security.SecurityWrapper;
import com.rends.web.util.MessageFactory;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

@Named("cameraBean")
@ViewScoped
public class CameraBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(CameraBean.class.getName());
    
    private List<CameraEntity> cameraList;

    private CameraEntity camera;
    
    @Inject
    private CameraService cameraService;
    
    public void prepareNewCamera() {
        reset();
        this.camera = new CameraEntity();
        // set any default values now, if you need
        // Example: this.camera.setAnything("test");
    }

    public String persist() {

        if (camera.getId() == null && !isPermitted("camera:create")) {
            return "accessDenied";
        } else if (camera.getId() != null && !isPermitted(camera, "camera:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (camera.getId() != null) {
                camera = cameraService.update(camera);
                message = "message_successfully_updated";
            } else {
                camera = cameraService.save(camera);
                message = "message_successfully_created";
            }
        } catch (OptimisticLockException e) {
            logger.log(Level.SEVERE, "Error occured", e);
            message = "message_optimistic_locking_exception";
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        } catch (PersistenceException e) {
            logger.log(Level.SEVERE, "Error occured", e);
            message = "message_save_exception";
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        }
        
        cameraList = null;

        FacesMessage facesMessage = MessageFactory.getMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        
        return null;
    }
    
    public String delete() {
        
        if (!isPermitted(camera, "camera:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            cameraService.delete(camera);
            message = "message_successfully_deleted";
            reset();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occured", e);
            message = "message_delete_exception";
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        }
        FacesContext.getCurrentInstance().addMessage(null, MessageFactory.getMessage(message));
        
        return null;
    }
    
    public void reset() {
        camera = null;
        cameraList = null;
        
    }

    public CameraEntity getCamera() {
        if (this.camera == null) {
            prepareNewCamera();
        }
        return this.camera;
    }
    
    public void setCamera(CameraEntity camera) {
        this.camera = camera;
    }
    
    public List<CameraEntity> getCameraList() {
        if (cameraList == null) {
            cameraList = cameraService.findAllCameraEntities();
        }
        return cameraList;
    }

    public void setCameraList(List<CameraEntity> cameraList) {
        this.cameraList = cameraList;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(CameraEntity camera, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
