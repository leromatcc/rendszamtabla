package com.rends.web;

import com.rends.domain.AutomovelTipoEntity;
import com.rends.service.AutomovelTipoService;
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

@Named("automovelTipoBean")
@ViewScoped
public class AutomovelTipoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(AutomovelTipoBean.class.getName());
    
    private List<AutomovelTipoEntity> automovelTipoList;

    private AutomovelTipoEntity automovelTipo;
    
    @Inject
    private AutomovelTipoService automovelTipoService;
    
    public void prepareNewAutomovelTipo() {
        reset();
        this.automovelTipo = new AutomovelTipoEntity();
        // set any default values now, if you need
        // Example: this.automovelTipo.setAnything("test");
    }

    public String persist() {

        if (automovelTipo.getId() == null && !isPermitted("automovelTipo:create")) {
            return "accessDenied";
        } else if (automovelTipo.getId() != null && !isPermitted(automovelTipo, "automovelTipo:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (automovelTipo.getId() != null) {
                automovelTipo = automovelTipoService.update(automovelTipo);
                message = "message_successfully_updated";
            } else {
                automovelTipo = automovelTipoService.save(automovelTipo);
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
        
        automovelTipoList = null;

        FacesMessage facesMessage = MessageFactory.getMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        
        return null;
    }
    
    public String delete() {
        
        if (!isPermitted(automovelTipo, "automovelTipo:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            automovelTipoService.delete(automovelTipo);
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
        automovelTipo = null;
        automovelTipoList = null;
        
    }

    public AutomovelTipoEntity getAutomovelTipo() {
        if (this.automovelTipo == null) {
            prepareNewAutomovelTipo();
        }
        return this.automovelTipo;
    }
    
    public void setAutomovelTipo(AutomovelTipoEntity automovelTipo) {
        this.automovelTipo = automovelTipo;
    }
    
    public List<AutomovelTipoEntity> getAutomovelTipoList() {
        if (automovelTipoList == null) {
            automovelTipoList = automovelTipoService.findAllAutomovelTipoEntities();
        }
        return automovelTipoList;
    }

    public void setAutomovelTipoList(List<AutomovelTipoEntity> automovelTipoList) {
        this.automovelTipoList = automovelTipoList;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(AutomovelTipoEntity automovelTipo, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
