package com.rends.web;

import com.rends.domain.EstabelecimentoEntity;
import com.rends.service.EstabelecimentoService;
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

@Named("estabelecimentoBean")
@ViewScoped
public class EstabelecimentoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(EstabelecimentoBean.class.getName());
    
    private List<EstabelecimentoEntity> estabelecimentoList;

    private EstabelecimentoEntity estabelecimento;
    
    @Inject
    private EstabelecimentoService estabelecimentoService;
    
    public void prepareNewEstabelecimento() {
        reset();
        this.estabelecimento = new EstabelecimentoEntity();
        // set any default values now, if you need
        // Example: this.estabelecimento.setAnything("test");
    }

    public String persist() {

        if (estabelecimento.getId() == null && !isPermitted("estabelecimento:create")) {
            return "accessDenied";
        } else if (estabelecimento.getId() != null && !isPermitted(estabelecimento, "estabelecimento:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (estabelecimento.getId() != null) {
                estabelecimento = estabelecimentoService.update(estabelecimento);
                message = "message_successfully_updated";
            } else {
                estabelecimento = estabelecimentoService.save(estabelecimento);
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
        
        estabelecimentoList = null;

        FacesMessage facesMessage = MessageFactory.getMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        
        return null;
    }
    
    public String delete() {
        
        if (!isPermitted(estabelecimento, "estabelecimento:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            estabelecimentoService.delete(estabelecimento);
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
        estabelecimento = null;
        estabelecimentoList = null;
        
    }

    public EstabelecimentoEntity getEstabelecimento() {
        if (this.estabelecimento == null) {
            prepareNewEstabelecimento();
        }
        return this.estabelecimento;
    }
    
    public void setEstabelecimento(EstabelecimentoEntity estabelecimento) {
        this.estabelecimento = estabelecimento;
    }
    
    public List<EstabelecimentoEntity> getEstabelecimentoList() {
        if (estabelecimentoList == null) {
            estabelecimentoList = estabelecimentoService.findAllEstabelecimentoEntities();
        }
        return estabelecimentoList;
    }

    public void setEstabelecimentoList(List<EstabelecimentoEntity> estabelecimentoList) {
        this.estabelecimentoList = estabelecimentoList;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(EstabelecimentoEntity estabelecimento, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
