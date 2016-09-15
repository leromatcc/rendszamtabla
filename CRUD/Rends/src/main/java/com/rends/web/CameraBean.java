package com.rends.web;

import com.rends.domain.CameraEntity;
import com.rends.domain.EstabelecimentoEntity;
import com.rends.service.CameraService;
import com.rends.service.EstabelecimentoService;
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

@Named("cameraBean")
@ViewScoped
public class CameraBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(CameraBean.class.getName());
    
    private List<CameraEntity> cameraList;

    private CameraEntity camera;
    
    @Inject
    private CameraService cameraService;
    
    @Inject
    private EstabelecimentoService estabelecimentoService;
    
    private DualListModel<EstabelecimentoEntity> estabelecimentos;
    private List<String> transferedEstabelecimentoIDs;
    private List<String> removedEstabelecimentoIDs;
    
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
        
        estabelecimentos = null;
        transferedEstabelecimentoIDs = null;
        removedEstabelecimentoIDs = null;
        
    }

    public DualListModel<EstabelecimentoEntity> getEstabelecimentos() {
        return estabelecimentos;
    }

    public void setEstabelecimentos(DualListModel<EstabelecimentoEntity> estabelecimentos) {
        this.estabelecimentos = estabelecimentos;
    }
    
    public List<EstabelecimentoEntity> getFullEstabelecimentosList() {
        List<EstabelecimentoEntity> allList = new ArrayList<>();
        allList.addAll(estabelecimentos.getSource());
        allList.addAll(estabelecimentos.getTarget());
        return allList;
    }
    
    public void onEstabelecimentosDialog(CameraEntity camera) {
        // Prepare the estabelecimento PickList
        this.camera = camera;
        List<EstabelecimentoEntity> selectedEstabelecimentosFromDB = estabelecimentoService
                .findEstabelecimentosByCamera(this.camera);
        List<EstabelecimentoEntity> availableEstabelecimentosFromDB = estabelecimentoService
                .findAvailableEstabelecimentos(this.camera);
        this.estabelecimentos = new DualListModel<>(availableEstabelecimentosFromDB, selectedEstabelecimentosFromDB);
        
        transferedEstabelecimentoIDs = new ArrayList<>();
        removedEstabelecimentoIDs = new ArrayList<>();
    }
    
    public void onEstabelecimentosPickListTransfer(TransferEvent event) {
        // If a estabelecimento is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((EstabelecimentoEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedEstabelecimentoIDs.add(id);
                removedEstabelecimentoIDs.remove(id);
            } else if (event.isRemove()) {
                removedEstabelecimentoIDs.add(id);
                transferedEstabelecimentoIDs.remove(id);
            }
        }
        
    }
    
    public void updateEstabelecimento(EstabelecimentoEntity estabelecimento) {
        // If a new estabelecimento is created, we persist it to the database,
        // but we do not assign it to this camera in the database, yet.
        estabelecimentos.getTarget().add(estabelecimento);
        transferedEstabelecimentoIDs.add(estabelecimento.getId().toString());
    }
    
    public void onEstabelecimentosSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            
            List<EstabelecimentoEntity> selectedEstabelecimentosFromDB = estabelecimentoService.findEstabelecimentosByCamera(this.camera);
            List<EstabelecimentoEntity> availableEstabelecimentosFromDB = estabelecimentoService.findAvailableEstabelecimentos(this.camera);

            // Because estabelecimentos are lazily loaded, we need to fetch them now
            this.camera = cameraService.fetchEstabelecimentos(this.camera);
            
            for (EstabelecimentoEntity estabelecimento : selectedEstabelecimentosFromDB) {
                if (removedEstabelecimentoIDs.contains(estabelecimento.getId().toString())) {
                    
                    this.camera.getEstabelecimentos().remove(estabelecimento);
                    
                }
            }
    
            for (EstabelecimentoEntity estabelecimento : availableEstabelecimentosFromDB) {
                if (transferedEstabelecimentoIDs.contains(estabelecimento.getId().toString())) {
                    
                    this.camera.getEstabelecimentos().add(estabelecimento);
                    
                }
            }
            
            this.camera = cameraService.update(this.camera);
            
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
