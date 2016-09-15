package com.rends.web;

import com.rends.domain.AutomovelEntity;
import com.rends.domain.AutomovelTipoEntity;
import com.rends.service.AutomovelService;
import com.rends.service.AutomovelTipoService;
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

@Named("automovelBean")
@ViewScoped
public class AutomovelBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(AutomovelBean.class.getName());
    
    private List<AutomovelEntity> automovelList;

    private AutomovelEntity automovel;
    
    @Inject
    private AutomovelService automovelService;
    
    @Inject
    private AutomovelTipoService automovelTipoService;
    
    private DualListModel<AutomovelTipoEntity> automovelTipos;
    private List<String> transferedAutomovelTipoIDs;
    private List<String> removedAutomovelTipoIDs;
    
    public void prepareNewAutomovel() {
        reset();
        this.automovel = new AutomovelEntity();
        // set any default values now, if you need
        // Example: this.automovel.setAnything("test");
    }

    public String persist() {

        if (automovel.getId() == null && !isPermitted("automovel:create")) {
            return "accessDenied";
        } else if (automovel.getId() != null && !isPermitted(automovel, "automovel:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (automovel.getId() != null) {
                automovel = automovelService.update(automovel);
                message = "message_successfully_updated";
            } else {
                automovel = automovelService.save(automovel);
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
        
        automovelList = null;

        FacesMessage facesMessage = MessageFactory.getMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        
        return null;
    }
    
    public String delete() {
        
        if (!isPermitted(automovel, "automovel:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            automovelService.delete(automovel);
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
        automovel = null;
        automovelList = null;
        
        automovelTipos = null;
        transferedAutomovelTipoIDs = null;
        removedAutomovelTipoIDs = null;
        
    }

    public DualListModel<AutomovelTipoEntity> getAutomovelTipos() {
        return automovelTipos;
    }

    public void setAutomovelTipos(DualListModel<AutomovelTipoEntity> automovelTipos) {
        this.automovelTipos = automovelTipos;
    }
    
    public List<AutomovelTipoEntity> getFullAutomovelTiposList() {
        List<AutomovelTipoEntity> allList = new ArrayList<>();
        allList.addAll(automovelTipos.getSource());
        allList.addAll(automovelTipos.getTarget());
        return allList;
    }
    
    public void onAutomovelTiposDialog(AutomovelEntity automovel) {
        // Prepare the automovelTipo PickList
        this.automovel = automovel;
        List<AutomovelTipoEntity> selectedAutomovelTiposFromDB = automovelTipoService
                .findAutomovelTiposByAutomovel(this.automovel);
        List<AutomovelTipoEntity> availableAutomovelTiposFromDB = automovelTipoService
                .findAvailableAutomovelTipos(this.automovel);
        this.automovelTipos = new DualListModel<>(availableAutomovelTiposFromDB, selectedAutomovelTiposFromDB);
        
        transferedAutomovelTipoIDs = new ArrayList<>();
        removedAutomovelTipoIDs = new ArrayList<>();
    }
    
    public void onAutomovelTiposPickListTransfer(TransferEvent event) {
        // If a automovelTipo is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((AutomovelTipoEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedAutomovelTipoIDs.add(id);
                removedAutomovelTipoIDs.remove(id);
            } else if (event.isRemove()) {
                removedAutomovelTipoIDs.add(id);
                transferedAutomovelTipoIDs.remove(id);
            }
        }
        
    }
    
    public void updateAutomovelTipo(AutomovelTipoEntity automovelTipo) {
        // If a new automovelTipo is created, we persist it to the database,
        // but we do not assign it to this automovel in the database, yet.
        automovelTipos.getTarget().add(automovelTipo);
        transferedAutomovelTipoIDs.add(automovelTipo.getId().toString());
    }
    
    public void onAutomovelTiposSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            
            List<AutomovelTipoEntity> selectedAutomovelTiposFromDB = automovelTipoService.findAutomovelTiposByAutomovel(this.automovel);
            List<AutomovelTipoEntity> availableAutomovelTiposFromDB = automovelTipoService.findAvailableAutomovelTipos(this.automovel);

            // Because automovelTipos are lazily loaded, we need to fetch them now
            this.automovel = automovelService.fetchAutomovelTipos(this.automovel);
            
            for (AutomovelTipoEntity automovelTipo : selectedAutomovelTiposFromDB) {
                if (removedAutomovelTipoIDs.contains(automovelTipo.getId().toString())) {
                    
                    this.automovel.getAutomovelTipos().remove(automovelTipo);
                    
                }
            }
    
            for (AutomovelTipoEntity automovelTipo : availableAutomovelTiposFromDB) {
                if (transferedAutomovelTipoIDs.contains(automovelTipo.getId().toString())) {
                    
                    this.automovel.getAutomovelTipos().add(automovelTipo);
                    
                }
            }
            
            this.automovel = automovelService.update(this.automovel);
            
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
    
    public AutomovelEntity getAutomovel() {
        if (this.automovel == null) {
            prepareNewAutomovel();
        }
        return this.automovel;
    }
    
    public void setAutomovel(AutomovelEntity automovel) {
        this.automovel = automovel;
    }
    
    public List<AutomovelEntity> getAutomovelList() {
        if (automovelList == null) {
            automovelList = automovelService.findAllAutomovelEntities();
        }
        return automovelList;
    }

    public void setAutomovelList(List<AutomovelEntity> automovelList) {
        this.automovelList = automovelList;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(AutomovelEntity automovel, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
