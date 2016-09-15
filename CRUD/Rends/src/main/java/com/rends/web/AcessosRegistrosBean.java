package com.rends.web;

import com.rends.domain.AcessosPermissaoEntity;
import com.rends.domain.AcessosRegistrosEntity;
import com.rends.domain.AutomovelEntity;
import com.rends.domain.EstacionamentoEntity;
import com.rends.service.AcessosPermissaoService;
import com.rends.service.AcessosRegistrosService;
import com.rends.service.AutomovelService;
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

@Named("acessosRegistrosBean")
@ViewScoped
public class AcessosRegistrosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(AcessosRegistrosBean.class.getName());
    
    private List<AcessosRegistrosEntity> acessosRegistrosList;

    private AcessosRegistrosEntity acessosRegistros;
    
    @Inject
    private AcessosRegistrosService acessosRegistrosService;
    
    @Inject
    private EstacionamentoService estacionamentoService;
    
    @Inject
    private AutomovelService automovelService;
    
    @Inject
    private AcessosPermissaoService acessosPermissaoService;
    
    private DualListModel<EstacionamentoEntity> estacionamentos;
    private List<String> transferedEstacionamentoIDs;
    private List<String> removedEstacionamentoIDs;
    
    private DualListModel<AcessosPermissaoEntity> permissaoAcessos;
    private List<String> transferedPermissaoAcessoIDs;
    private List<String> removedPermissaoAcessoIDs;
    
    private List<AutomovelEntity> availableAutomovelList;
    
    public void prepareNewAcessosRegistros() {
        reset();
        this.acessosRegistros = new AcessosRegistrosEntity();
        // set any default values now, if you need
        // Example: this.acessosRegistros.setAnything("test");
    }

    public String persist() {

        if (acessosRegistros.getId() == null && !isPermitted("acessosRegistros:create")) {
            return "accessDenied";
        } else if (acessosRegistros.getId() != null && !isPermitted(acessosRegistros, "acessosRegistros:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (acessosRegistros.getId() != null) {
                acessosRegistros = acessosRegistrosService.update(acessosRegistros);
                message = "message_successfully_updated";
            } else {
                acessosRegistros = acessosRegistrosService.save(acessosRegistros);
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
        
        acessosRegistrosList = null;

        FacesMessage facesMessage = MessageFactory.getMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        
        return null;
    }
    
    public String delete() {
        
        if (!isPermitted(acessosRegistros, "acessosRegistros:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            acessosRegistrosService.delete(acessosRegistros);
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
        acessosRegistros = null;
        acessosRegistrosList = null;
        
        estacionamentos = null;
        transferedEstacionamentoIDs = null;
        removedEstacionamentoIDs = null;
        
        permissaoAcessos = null;
        transferedPermissaoAcessoIDs = null;
        removedPermissaoAcessoIDs = null;
        
        availableAutomovelList = null;
        
    }

    // Get a List of all automovel
    public List<AutomovelEntity> getAvailableAutomovel() {
        if (this.availableAutomovelList == null) {
            this.availableAutomovelList = automovelService.findAvailableAutomovel(this.acessosRegistros);
        }
        return this.availableAutomovelList;
    }
    
    // Update automovel of the current acessosRegistros
    public void updateAutomovel(AutomovelEntity automovel) {
        this.acessosRegistros.setAutomovel(automovel);
        // Maybe we just created and assigned a new automovel. So reset the availableAutomovelList.
        availableAutomovelList = null;
    }
    
    public DualListModel<EstacionamentoEntity> getEstacionamentos() {
        return estacionamentos;
    }

    public void setEstacionamentos(DualListModel<EstacionamentoEntity> estacionamentos) {
        this.estacionamentos = estacionamentos;
    }
    
    public List<EstacionamentoEntity> getFullEstacionamentosList() {
        List<EstacionamentoEntity> allList = new ArrayList<>();
        allList.addAll(estacionamentos.getSource());
        allList.addAll(estacionamentos.getTarget());
        return allList;
    }
    
    public void onEstacionamentosDialog(AcessosRegistrosEntity acessosRegistros) {
        // Prepare the estacionamento PickList
        this.acessosRegistros = acessosRegistros;
        List<EstacionamentoEntity> selectedEstacionamentosFromDB = estacionamentoService
                .findEstacionamentosByAcessosRegistros(this.acessosRegistros);
        List<EstacionamentoEntity> availableEstacionamentosFromDB = estacionamentoService
                .findAvailableEstacionamentos(this.acessosRegistros);
        this.estacionamentos = new DualListModel<>(availableEstacionamentosFromDB, selectedEstacionamentosFromDB);
        
        transferedEstacionamentoIDs = new ArrayList<>();
        removedEstacionamentoIDs = new ArrayList<>();
    }
    
    public void onEstacionamentosPickListTransfer(TransferEvent event) {
        // If a estacionamento is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((EstacionamentoEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedEstacionamentoIDs.add(id);
                removedEstacionamentoIDs.remove(id);
            } else if (event.isRemove()) {
                removedEstacionamentoIDs.add(id);
                transferedEstacionamentoIDs.remove(id);
            }
        }
        
    }
    
    public void updateEstacionamento(EstacionamentoEntity estacionamento) {
        // If a new estacionamento is created, we persist it to the database,
        // but we do not assign it to this acessosRegistros in the database, yet.
        estacionamentos.getTarget().add(estacionamento);
        transferedEstacionamentoIDs.add(estacionamento.getId().toString());
    }
    
    public DualListModel<AcessosPermissaoEntity> getPermissaoAcessos() {
        return permissaoAcessos;
    }

    public void setPermissaoAcessos(DualListModel<AcessosPermissaoEntity> acessosPermissaos) {
        this.permissaoAcessos = acessosPermissaos;
    }
    
    public List<AcessosPermissaoEntity> getFullPermissaoAcessosList() {
        List<AcessosPermissaoEntity> allList = new ArrayList<>();
        allList.addAll(permissaoAcessos.getSource());
        allList.addAll(permissaoAcessos.getTarget());
        return allList;
    }
    
    public void onPermissaoAcessosDialog(AcessosRegistrosEntity acessosRegistros) {
        // Prepare the permissaoAcesso PickList
        this.acessosRegistros = acessosRegistros;
        List<AcessosPermissaoEntity> selectedAcessosPermissaosFromDB = acessosPermissaoService
                .findPermissaoAcessosByAcessosRegistros(this.acessosRegistros);
        List<AcessosPermissaoEntity> availableAcessosPermissaosFromDB = acessosPermissaoService
                .findAvailablePermissaoAcessos(this.acessosRegistros);
        this.permissaoAcessos = new DualListModel<>(availableAcessosPermissaosFromDB, selectedAcessosPermissaosFromDB);
        
        transferedPermissaoAcessoIDs = new ArrayList<>();
        removedPermissaoAcessoIDs = new ArrayList<>();
    }
    
    public void onPermissaoAcessosPickListTransfer(TransferEvent event) {
        // If a permissaoAcesso is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((AcessosPermissaoEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedPermissaoAcessoIDs.add(id);
                removedPermissaoAcessoIDs.remove(id);
            } else if (event.isRemove()) {
                removedPermissaoAcessoIDs.add(id);
                transferedPermissaoAcessoIDs.remove(id);
            }
        }
        
    }
    
    public void updatePermissaoAcesso(AcessosPermissaoEntity acessosPermissao) {
        // If a new permissaoAcesso is created, we persist it to the database,
        // but we do not assign it to this acessosRegistros in the database, yet.
        permissaoAcessos.getTarget().add(acessosPermissao);
        transferedPermissaoAcessoIDs.add(acessosPermissao.getId().toString());
    }
    
    public void onEstacionamentosSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            
            List<EstacionamentoEntity> selectedEstacionamentosFromDB = estacionamentoService.findEstacionamentosByAcessosRegistros(this.acessosRegistros);
            List<EstacionamentoEntity> availableEstacionamentosFromDB = estacionamentoService.findAvailableEstacionamentos(this.acessosRegistros);

            // Because estacionamentos are lazily loaded, we need to fetch them now
            this.acessosRegistros = acessosRegistrosService.fetchEstacionamentos(this.acessosRegistros);
            
            for (EstacionamentoEntity estacionamento : selectedEstacionamentosFromDB) {
                if (removedEstacionamentoIDs.contains(estacionamento.getId().toString())) {
                    
                    this.acessosRegistros.getEstacionamentos().remove(estacionamento);
                    
                }
            }
    
            for (EstacionamentoEntity estacionamento : availableEstacionamentosFromDB) {
                if (transferedEstacionamentoIDs.contains(estacionamento.getId().toString())) {
                    
                    this.acessosRegistros.getEstacionamentos().add(estacionamento);
                    
                }
            }
            
            this.acessosRegistros = acessosRegistrosService.update(this.acessosRegistros);
            
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
    
    public void onPermissaoAcessosSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            
            List<AcessosPermissaoEntity> selectedAcessosPermissaosFromDB = acessosPermissaoService.findPermissaoAcessosByAcessosRegistros(this.acessosRegistros);
            List<AcessosPermissaoEntity> availableAcessosPermissaosFromDB = acessosPermissaoService.findAvailablePermissaoAcessos(this.acessosRegistros);

            // Because permissaoAcessos are lazily loaded, we need to fetch them now
            this.acessosRegistros = acessosRegistrosService.fetchPermissaoAcessos(this.acessosRegistros);
            
            for (AcessosPermissaoEntity acessosPermissao : selectedAcessosPermissaosFromDB) {
                if (removedPermissaoAcessoIDs.contains(acessosPermissao.getId().toString())) {
                    
                    this.acessosRegistros.getPermissaoAcessos().remove(acessosPermissao);
                    
                }
            }
    
            for (AcessosPermissaoEntity acessosPermissao : availableAcessosPermissaosFromDB) {
                if (transferedPermissaoAcessoIDs.contains(acessosPermissao.getId().toString())) {
                    
                    this.acessosRegistros.getPermissaoAcessos().add(acessosPermissao);
                    
                }
            }
            
            this.acessosRegistros = acessosRegistrosService.update(this.acessosRegistros);
            
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
    
    public AcessosRegistrosEntity getAcessosRegistros() {
        if (this.acessosRegistros == null) {
            prepareNewAcessosRegistros();
        }
        return this.acessosRegistros;
    }
    
    public void setAcessosRegistros(AcessosRegistrosEntity acessosRegistros) {
        this.acessosRegistros = acessosRegistros;
    }
    
    public List<AcessosRegistrosEntity> getAcessosRegistrosList() {
        if (acessosRegistrosList == null) {
            acessosRegistrosList = acessosRegistrosService.findAllAcessosRegistrosEntities();
        }
        return acessosRegistrosList;
    }

    public void setAcessosRegistrosList(List<AcessosRegistrosEntity> acessosRegistrosList) {
        this.acessosRegistrosList = acessosRegistrosList;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(AcessosRegistrosEntity acessosRegistros, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
