package com.rends.web;

import com.rends.domain.EmpresaEntity;
import com.rends.domain.EstacionamentoEntity;
import com.rends.service.EmpresaService;
import com.rends.service.EstacionamentoService;
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

@Named("estacionamentoBean")
@ViewScoped
public class EstacionamentoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(EstacionamentoBean.class.getName());
    
    private List<EstacionamentoEntity> estacionamentoList;

    private EstacionamentoEntity estacionamento;
    
    @Inject
    private EstacionamentoService estacionamentoService;
    
    @Inject
    private EmpresaService empresaService;
    
    private DualListModel<EmpresaEntity> empresas;
    private List<String> transferedEmpresaIDs;
    private List<String> removedEmpresaIDs;
    
    public void prepareNewEstacionamento() {
        reset();
        this.estacionamento = new EstacionamentoEntity();
        // set any default values now, if you need
        // Example: this.estacionamento.setAnything("test");
    }

    public String persist() {

        String message;
        
        try {
            
            if (estacionamento.getId() != null) {
                estacionamento = estacionamentoService.update(estacionamento);
                message = "message_successfully_updated";
            } else {
                estacionamento = estacionamentoService.save(estacionamento);
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
        
        estacionamentoList = null;

        FacesMessage facesMessage = MessageFactory.getMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        
        return null;
    }
    
    public String delete() {
        
        String message;
        
        try {
            estacionamentoService.delete(estacionamento);
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
        estacionamento = null;
        estacionamentoList = null;
        
        empresas = null;
        transferedEmpresaIDs = null;
        removedEmpresaIDs = null;
        
    }

    public DualListModel<EmpresaEntity> getEmpresas() {
        return empresas;
    }

    public void setEmpresas(DualListModel<EmpresaEntity> empresas) {
        this.empresas = empresas;
    }
    
    public List<EmpresaEntity> getFullEmpresasList() {
        List<EmpresaEntity> allList = new ArrayList<>();
        allList.addAll(empresas.getSource());
        allList.addAll(empresas.getTarget());
        return allList;
    }
    
    public void onEmpresasDialog(EstacionamentoEntity estacionamento) {
        // Prepare the empresa PickList
        this.estacionamento = estacionamento;
        List<EmpresaEntity> selectedEmpresasFromDB = empresaService
                .findEmpresasByEstacionamento(this.estacionamento);
        List<EmpresaEntity> availableEmpresasFromDB = empresaService
                .findAvailableEmpresas(this.estacionamento);
        this.empresas = new DualListModel<>(availableEmpresasFromDB, selectedEmpresasFromDB);
        
        transferedEmpresaIDs = new ArrayList<>();
        removedEmpresaIDs = new ArrayList<>();
    }
    
    public void onEmpresasPickListTransfer(TransferEvent event) {
        // If a empresa is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((EmpresaEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedEmpresaIDs.add(id);
                removedEmpresaIDs.remove(id);
            } else if (event.isRemove()) {
                removedEmpresaIDs.add(id);
                transferedEmpresaIDs.remove(id);
            }
        }
        
    }
    
    public void updateEmpresa(EmpresaEntity empresa) {
        // If a new empresa is created, we persist it to the database,
        // but we do not assign it to this estacionamento in the database, yet.
        empresas.getTarget().add(empresa);
        transferedEmpresaIDs.add(empresa.getId().toString());
    }
    
    public void onEmpresasSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            
            List<EmpresaEntity> selectedEmpresasFromDB = empresaService.findEmpresasByEstacionamento(this.estacionamento);
            List<EmpresaEntity> availableEmpresasFromDB = empresaService.findAvailableEmpresas(this.estacionamento);

            // Because empresas are lazily loaded, we need to fetch them now
            this.estacionamento = estacionamentoService.fetchEmpresas(this.estacionamento);
            
            for (EmpresaEntity empresa : selectedEmpresasFromDB) {
                if (removedEmpresaIDs.contains(empresa.getId().toString())) {
                    
                    this.estacionamento.getEmpresas().remove(empresa);
                    
                }
            }
    
            for (EmpresaEntity empresa : availableEmpresasFromDB) {
                if (transferedEmpresaIDs.contains(empresa.getId().toString())) {
                    
                    this.estacionamento.getEmpresas().add(empresa);
                    
                }
            }
            
            this.estacionamento = estacionamentoService.update(this.estacionamento);
            
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
    
    public EstacionamentoEntity getEstacionamento() {
        if (this.estacionamento == null) {
            prepareNewEstacionamento();
        }
        return this.estacionamento;
    }
    
    public void setEstacionamento(EstacionamentoEntity estacionamento) {
        this.estacionamento = estacionamento;
    }
    
    public List<EstacionamentoEntity> getEstacionamentoList() {
        if (estacionamentoList == null) {
            estacionamentoList = estacionamentoService.findAllEstacionamentoEntities();
        }
        return estacionamentoList;
    }

    public void setEstacionamentoList(List<EstacionamentoEntity> estacionamentoList) {
        this.estacionamentoList = estacionamentoList;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(EstacionamentoEntity estacionamento, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
