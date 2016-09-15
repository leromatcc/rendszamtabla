package com.rends.web;

import com.rends.domain.EnderecoEntity;
import com.rends.domain.EnderecoTipo;
import com.rends.service.EnderecoService;
import com.rends.service.security.SecurityWrapper;
import com.rends.web.util.MessageFactory;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

@Named("enderecoBean")
@ViewScoped
public class EnderecoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(EnderecoBean.class.getName());
    
    private List<EnderecoEntity> enderecoList;

    private EnderecoEntity endereco;
    
    @Inject
    private EnderecoService enderecoService;
    
    public void prepareNewEndereco() {
        reset();
        this.endereco = new EnderecoEntity();
        // set any default values now, if you need
        // Example: this.endereco.setAnything("test");
    }

    public String persist() {

        if (endereco.getId() == null && !isPermitted("endereco:create")) {
            return "accessDenied";
        } else if (endereco.getId() != null && !isPermitted(endereco, "endereco:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (endereco.getId() != null) {
                endereco = enderecoService.update(endereco);
                message = "message_successfully_updated";
            } else {
                endereco = enderecoService.save(endereco);
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
        
        enderecoList = null;

        FacesMessage facesMessage = MessageFactory.getMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        
        return null;
    }
    
    public String delete() {
        
        if (!isPermitted(endereco, "endereco:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            enderecoService.delete(endereco);
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
        endereco = null;
        enderecoList = null;
        
    }

    public SelectItem[] getTipoSelectItems() {
        SelectItem[] items = new SelectItem[EnderecoTipo.values().length];

        int i = 0;
        for (EnderecoTipo tipo : EnderecoTipo.values()) {
            items[i++] = new SelectItem(tipo, getLabelForTipo(tipo));
        }
        return items;
    }
    
    public String getLabelForTipo(EnderecoTipo value) {
        if (value == null) {
            return "";
        }
        String label = MessageFactory.getMessageString(
                "enum_label_endereco_tipo_" + value);
        return label == null? value.toString() : label;
    }
    
    public EnderecoEntity getEndereco() {
        if (this.endereco == null) {
            prepareNewEndereco();
        }
        return this.endereco;
    }
    
    public void setEndereco(EnderecoEntity endereco) {
        this.endereco = endereco;
    }
    
    public List<EnderecoEntity> getEnderecoList() {
        if (enderecoList == null) {
            enderecoList = enderecoService.findAllEnderecoEntities();
        }
        return enderecoList;
    }

    public void setEnderecoList(List<EnderecoEntity> enderecoList) {
        this.enderecoList = enderecoList;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(EnderecoEntity endereco, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
