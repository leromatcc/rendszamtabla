package com.rends.web;

import com.rends.domain.AutomovelEntity;
import com.rends.domain.EstabelecimentoEntity;
import com.rends.domain.EstabelecimentoVisitaEntity;
import com.rends.service.AutomovelService;
import com.rends.service.EstabelecimentoService;
import com.rends.service.EstabelecimentoVisitaService;
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

@Named("estabelecimentoVisitaBean")
@ViewScoped
public class EstabelecimentoVisitaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(EstabelecimentoVisitaBean.class.getName());
    
    private List<EstabelecimentoVisitaEntity> estabelecimentoVisitaList;

    private EstabelecimentoVisitaEntity estabelecimentoVisita;
    
    @Inject
    private EstabelecimentoVisitaService estabelecimentoVisitaService;
    
    @Inject
    private EstabelecimentoService estabelecimentoService;
    
    @Inject
    private AutomovelService automovelService;
    
    private List<EstabelecimentoEntity> availableEstabelecimentoList;
    
    private List<AutomovelEntity> availableAutomovelList;
    
    public void prepareNewEstabelecimentoVisita() {
        reset();
        this.estabelecimentoVisita = new EstabelecimentoVisitaEntity();
        // set any default values now, if you need
        // Example: this.estabelecimentoVisita.setAnything("test");
    }

    public String persist() {

        if (estabelecimentoVisita.getId() == null && !isPermitted("estabelecimentoVisita:create")) {
            return "accessDenied";
        } else if (estabelecimentoVisita.getId() != null && !isPermitted(estabelecimentoVisita, "estabelecimentoVisita:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (estabelecimentoVisita.getId() != null) {
                estabelecimentoVisita = estabelecimentoVisitaService.update(estabelecimentoVisita);
                message = "message_successfully_updated";
            } else {
                estabelecimentoVisita = estabelecimentoVisitaService.save(estabelecimentoVisita);
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
        
        estabelecimentoVisitaList = null;

        FacesMessage facesMessage = MessageFactory.getMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        
        return null;
    }
    
    public String delete() {
        
        if (!isPermitted(estabelecimentoVisita, "estabelecimentoVisita:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            estabelecimentoVisitaService.delete(estabelecimentoVisita);
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
        estabelecimentoVisita = null;
        estabelecimentoVisitaList = null;
        
        availableEstabelecimentoList = null;
        
        availableAutomovelList = null;
        
    }

    // Get a List of all estabelecimento
    public List<EstabelecimentoEntity> getAvailableEstabelecimento() {
        if (this.availableEstabelecimentoList == null) {
            this.availableEstabelecimentoList = estabelecimentoService.findAvailableEstabelecimento(this.estabelecimentoVisita);
        }
        return this.availableEstabelecimentoList;
    }
    
    // Update estabelecimento of the current estabelecimentoVisita
    public void updateEstabelecimento(EstabelecimentoEntity estabelecimento) {
        this.estabelecimentoVisita.setEstabelecimento(estabelecimento);
        // Maybe we just created and assigned a new estabelecimento. So reset the availableEstabelecimentoList.
        availableEstabelecimentoList = null;
    }
    
    // Get a List of all automovel
    public List<AutomovelEntity> getAvailableAutomovel() {
        if (this.availableAutomovelList == null) {
            this.availableAutomovelList = automovelService.findAvailableAutomovel(this.estabelecimentoVisita);
        }
        return this.availableAutomovelList;
    }
    
    // Update automovel of the current estabelecimentoVisita
    public void updateAutomovel(AutomovelEntity automovel) {
        this.estabelecimentoVisita.setAutomovel(automovel);
        // Maybe we just created and assigned a new automovel. So reset the availableAutomovelList.
        availableAutomovelList = null;
    }
    
    public EstabelecimentoVisitaEntity getEstabelecimentoVisita() {
        if (this.estabelecimentoVisita == null) {
            prepareNewEstabelecimentoVisita();
        }
        return this.estabelecimentoVisita;
    }
    
    public void setEstabelecimentoVisita(EstabelecimentoVisitaEntity estabelecimentoVisita) {
        this.estabelecimentoVisita = estabelecimentoVisita;
    }
    
    public List<EstabelecimentoVisitaEntity> getEstabelecimentoVisitaList() {
        if (estabelecimentoVisitaList == null) {
            estabelecimentoVisitaList = estabelecimentoVisitaService.findAllEstabelecimentoVisitaEntities();
        }
        return estabelecimentoVisitaList;
    }

    public void setEstabelecimentoVisitaList(List<EstabelecimentoVisitaEntity> estabelecimentoVisitaList) {
        this.estabelecimentoVisitaList = estabelecimentoVisitaList;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(EstabelecimentoVisitaEntity estabelecimentoVisita, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
