package com.rends.web;

import com.rends.domain.PessoaEntity;
import com.rends.service.PessoaService;
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

@Named("pessoaBean")
@ViewScoped
public class PessoaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(PessoaBean.class.getName());
    
    private List<PessoaEntity> pessoaList;

    private PessoaEntity pessoa;
    
    @Inject
    private PessoaService pessoaService;
    
    public void prepareNewPessoa() {
        reset();
        this.pessoa = new PessoaEntity();
        // set any default values now, if you need
        // Example: this.pessoa.setAnything("test");
    }

    public String persist() {

        if (pessoa.getId() == null && !isPermitted("pessoa:create")) {
            return "accessDenied";
        } else if (pessoa.getId() != null && !isPermitted(pessoa, "pessoa:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (pessoa.getId() != null) {
                pessoa = pessoaService.update(pessoa);
                message = "message_successfully_updated";
            } else {
                pessoa = pessoaService.save(pessoa);
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
        
        pessoaList = null;

        FacesMessage facesMessage = MessageFactory.getMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        
        return null;
    }
    
    public String delete() {
        
        if (!isPermitted(pessoa, "pessoa:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            pessoaService.delete(pessoa);
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
        pessoa = null;
        pessoaList = null;
        
    }

    public PessoaEntity getPessoa() {
        if (this.pessoa == null) {
            prepareNewPessoa();
        }
        return this.pessoa;
    }
    
    public void setPessoa(PessoaEntity pessoa) {
        this.pessoa = pessoa;
    }
    
    public List<PessoaEntity> getPessoaList() {
        if (pessoaList == null) {
            pessoaList = pessoaService.findAllPessoaEntities();
        }
        return pessoaList;
    }

    public void setPessoaList(List<PessoaEntity> pessoaList) {
        this.pessoaList = pessoaList;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(PessoaEntity pessoa, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
