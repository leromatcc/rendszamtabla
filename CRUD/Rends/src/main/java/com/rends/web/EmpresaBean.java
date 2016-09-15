package com.rends.web;

import com.rends.domain.EmpresaEntity;
import com.rends.domain.EnderecoEntity;
import com.rends.service.EmpresaService;
import com.rends.service.EnderecoService;
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

@Named("empresaBean")
@ViewScoped
public class EmpresaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(EmpresaBean.class.getName());
    
    private List<EmpresaEntity> empresaList;

    private EmpresaEntity empresa;
    
    @Inject
    private EmpresaService empresaService;
    
    @Inject
    private EnderecoService enderecoService;
    
    private DualListModel<EnderecoEntity> enderecos;
    private List<String> transferedEnderecoIDs;
    private List<String> removedEnderecoIDs;
    
    public void prepareNewEmpresa() {
        reset();
        this.empresa = new EmpresaEntity();
        // set any default values now, if you need
        // Example: this.empresa.setAnything("test");
    }

    public String persist() {

        if (empresa.getId() == null && !isPermitted("empresa:create")) {
            return "accessDenied";
        } else if (empresa.getId() != null && !isPermitted(empresa, "empresa:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (empresa.getId() != null) {
                empresa = empresaService.update(empresa);
                message = "message_successfully_updated";
            } else {
                empresa = empresaService.save(empresa);
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
        
        empresaList = null;

        FacesMessage facesMessage = MessageFactory.getMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        
        return null;
    }
    
    public String delete() {
        
        if (!isPermitted(empresa, "empresa:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            empresaService.delete(empresa);
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
        empresa = null;
        empresaList = null;
        
        enderecos = null;
        transferedEnderecoIDs = null;
        removedEnderecoIDs = null;
        
    }

    public DualListModel<EnderecoEntity> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(DualListModel<EnderecoEntity> enderecos) {
        this.enderecos = enderecos;
    }
    
    public List<EnderecoEntity> getFullEnderecosList() {
        List<EnderecoEntity> allList = new ArrayList<>();
        allList.addAll(enderecos.getSource());
        allList.addAll(enderecos.getTarget());
        return allList;
    }
    
    public void onEnderecosDialog(EmpresaEntity empresa) {
        // Prepare the endereco PickList
        this.empresa = empresa;
        List<EnderecoEntity> selectedEnderecosFromDB = enderecoService
                .findEnderecosByEmpresa(this.empresa);
        List<EnderecoEntity> availableEnderecosFromDB = enderecoService
                .findAvailableEnderecos(this.empresa);
        this.enderecos = new DualListModel<>(availableEnderecosFromDB, selectedEnderecosFromDB);
        
        transferedEnderecoIDs = new ArrayList<>();
        removedEnderecoIDs = new ArrayList<>();
    }
    
    public void onEnderecosPickListTransfer(TransferEvent event) {
        // If a endereco is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((EnderecoEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedEnderecoIDs.add(id);
                removedEnderecoIDs.remove(id);
            } else if (event.isRemove()) {
                removedEnderecoIDs.add(id);
                transferedEnderecoIDs.remove(id);
            }
        }
        
    }
    
    public void updateEndereco(EnderecoEntity endereco) {
        // If a new endereco is created, we persist it to the database,
        // but we do not assign it to this empresa in the database, yet.
        enderecos.getTarget().add(endereco);
        transferedEnderecoIDs.add(endereco.getId().toString());
    }
    
    public void onEnderecosSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            
            List<EnderecoEntity> selectedEnderecosFromDB = enderecoService.findEnderecosByEmpresa(this.empresa);
            List<EnderecoEntity> availableEnderecosFromDB = enderecoService.findAvailableEnderecos(this.empresa);

            // Because enderecos are lazily loaded, we need to fetch them now
            this.empresa = empresaService.fetchEnderecos(this.empresa);
            
            for (EnderecoEntity endereco : selectedEnderecosFromDB) {
                if (removedEnderecoIDs.contains(endereco.getId().toString())) {
                    
                    this.empresa.getEnderecos().remove(endereco);
                    
                }
            }
    
            for (EnderecoEntity endereco : availableEnderecosFromDB) {
                if (transferedEnderecoIDs.contains(endereco.getId().toString())) {
                    
                    this.empresa.getEnderecos().add(endereco);
                    
                }
            }
            
            this.empresa = empresaService.update(this.empresa);
            
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
    
    public EmpresaEntity getEmpresa() {
        if (this.empresa == null) {
            prepareNewEmpresa();
        }
        return this.empresa;
    }
    
    public void setEmpresa(EmpresaEntity empresa) {
        this.empresa = empresa;
    }
    
    public List<EmpresaEntity> getEmpresaList() {
        if (empresaList == null) {
            empresaList = empresaService.findAllEmpresaEntities();
        }
        return empresaList;
    }

    public void setEmpresaList(List<EmpresaEntity> empresaList) {
        this.empresaList = empresaList;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(EmpresaEntity empresa, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
