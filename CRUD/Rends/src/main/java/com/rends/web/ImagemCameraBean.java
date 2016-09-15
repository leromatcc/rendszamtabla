package com.rends.web;

import com.rends.domain.CameraEntity;
import com.rends.domain.ImagemCameraEntity;
import com.rends.domain.ImagensEntity;
import com.rends.service.CameraService;
import com.rends.service.ImagemCameraService;
import com.rends.service.ImagensService;
import com.rends.service.security.SecurityWrapper;
import com.rends.web.util.MessageFactory;

import java.io.Serializable;
import java.util.ArrayList;
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

import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

@Named("imagemCameraBean")
@ViewScoped
public class ImagemCameraBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(ImagemCameraBean.class.getName());
    
    private List<ImagemCameraEntity> imagemCameraList;

    private ImagemCameraEntity imagemCamera;
    
    @Inject
    private ImagemCameraService imagemCameraService;
    
    @Inject
    private ImagensService imagensService;
    
    @Inject
    private CameraService cameraService;
    
    private DualListModel<CameraEntity> cameras;
    private List<String> transferedCameraIDs;
    private List<String> removedCameraIDs;
    
    private List<ImagensEntity> availableImagemList;
    
    public void prepareNewImagemCamera() {
        reset();
        this.imagemCamera = new ImagemCameraEntity();
        // set any default values now, if you need
        // Example: this.imagemCamera.setAnything("test");
    }

    public String persist() {

        if (imagemCamera.getId() == null && !isPermitted("imagemCamera:create")) {
            return "accessDenied";
        } else if (imagemCamera.getId() != null && !isPermitted(imagemCamera, "imagemCamera:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (imagemCamera.getId() != null) {
                imagemCamera = imagemCameraService.update(imagemCamera);
                message = "message_successfully_updated";
            } else {
                imagemCamera = imagemCameraService.save(imagemCamera);
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
        
        imagemCameraList = null;

        FacesMessage facesMessage = MessageFactory.getMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        
        return null;
    }
    
    public String delete() {
        
        if (!isPermitted(imagemCamera, "imagemCamera:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            imagemCameraService.delete(imagemCamera);
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
        imagemCamera = null;
        imagemCameraList = null;
        
        cameras = null;
        transferedCameraIDs = null;
        removedCameraIDs = null;
        
        availableImagemList = null;
        
    }

    // Get a List of all imagem
    public List<ImagensEntity> getAvailableImagem() {
        if (this.availableImagemList == null) {
            this.availableImagemList = imagensService.findAvailableImagem(this.imagemCamera);
        }
        return this.availableImagemList;
    }
    
    // Update imagem of the current imagemCamera
    public void updateImagem(ImagensEntity imagens) {
        this.imagemCamera.setImagem(imagens);
        // Maybe we just created and assigned a new imagem. So reset the availableImagemList.
        availableImagemList = null;
    }
    
    public DualListModel<CameraEntity> getCameras() {
        return cameras;
    }

    public void setCameras(DualListModel<CameraEntity> cameras) {
        this.cameras = cameras;
    }
    
    public List<CameraEntity> getFullCamerasList() {
        List<CameraEntity> allList = new ArrayList<>();
        allList.addAll(cameras.getSource());
        allList.addAll(cameras.getTarget());
        return allList;
    }
    
    public void onCamerasDialog(ImagemCameraEntity imagemCamera) {
        // Prepare the camera PickList
        this.imagemCamera = imagemCamera;
        List<CameraEntity> selectedCamerasFromDB = cameraService
                .findCamerasByImagemCamera(this.imagemCamera);
        List<CameraEntity> availableCamerasFromDB = cameraService
                .findAvailableCameras(this.imagemCamera);
        this.cameras = new DualListModel<>(availableCamerasFromDB, selectedCamerasFromDB);
        
        transferedCameraIDs = new ArrayList<>();
        removedCameraIDs = new ArrayList<>();
    }
    
    public void onCamerasPickListTransfer(TransferEvent event) {
        // If a camera is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((CameraEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedCameraIDs.add(id);
                removedCameraIDs.remove(id);
            } else if (event.isRemove()) {
                removedCameraIDs.add(id);
                transferedCameraIDs.remove(id);
            }
        }
        
    }
    
    public void updateCamera(CameraEntity camera) {
        // If a new camera is created, we persist it to the database,
        // but we do not assign it to this imagemCamera in the database, yet.
        cameras.getTarget().add(camera);
        transferedCameraIDs.add(camera.getId().toString());
    }
    
    public void onCamerasSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            
            List<CameraEntity> selectedCamerasFromDB = cameraService.findCamerasByImagemCamera(this.imagemCamera);
            List<CameraEntity> availableCamerasFromDB = cameraService.findAvailableCameras(this.imagemCamera);

            // Because cameras are lazily loaded, we need to fetch them now
            this.imagemCamera = imagemCameraService.fetchCameras(this.imagemCamera);
            
            for (CameraEntity camera : selectedCamerasFromDB) {
                if (removedCameraIDs.contains(camera.getId().toString())) {
                    
                    this.imagemCamera.getCameras().remove(camera);
                    
                }
            }
    
            for (CameraEntity camera : availableCamerasFromDB) {
                if (transferedCameraIDs.contains(camera.getId().toString())) {
                    
                    this.imagemCamera.getCameras().add(camera);
                    
                }
            }
            
            this.imagemCamera = imagemCameraService.update(this.imagemCamera);
            
            FacesMessage facesMessage = MessageFactory.getMessage("message_changes_saved");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            
            reset();

        } catch (OptimisticLockException e) {
            FacesMessage facesMessage = MessageFactory.getMessage(
                    "message_optimistic_locking_exception");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        } catch (PersistenceException e) {
            FacesMessage facesMessage = MessageFactory.getMessage(
                    "message_picklist_save_exception");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        }
    }
    
    public ImagemCameraEntity getImagemCamera() {
        if (this.imagemCamera == null) {
            prepareNewImagemCamera();
        }
        return this.imagemCamera;
    }
    
    public void setImagemCamera(ImagemCameraEntity imagemCamera) {
        this.imagemCamera = imagemCamera;
    }
    
    public List<ImagemCameraEntity> getImagemCameraList() {
        if (imagemCameraList == null) {
            imagemCameraList = imagemCameraService.findAllImagemCameraEntities();
        }
        return imagemCameraList;
    }

    public void setImagemCameraList(List<ImagemCameraEntity> imagemCameraList) {
        this.imagemCameraList = imagemCameraList;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(ImagemCameraEntity imagemCamera, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
