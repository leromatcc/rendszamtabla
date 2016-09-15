package com.rends.web;

import com.rends.domain.EstabelecimentoEntity;
import com.rends.domain.PermissaoEstabelecimentoPessoaEntity;
import com.rends.domain.PessoaEntity;
import com.rends.service.EstabelecimentoService;
import com.rends.service.PermissaoEstabelecimentoPessoaService;
import com.rends.service.PessoaService;
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

@Named("permissaoEstabelecimentoPessoaBean")
@ViewScoped
public class PermissaoEstabelecimentoPessoaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(PermissaoEstabelecimentoPessoaBean.class.getName());
    
    private List<PermissaoEstabelecimentoPessoaEntity> permissaoEstabelecimentoPessoaList;

    private PermissaoEstabelecimentoPessoaEntity permissaoEstabelecimentoPessoa;
    
    @Inject
    private PermissaoEstabelecimentoPessoaService permissaoEstabelecimentoPessoaService;
    
    @Inject
    private PessoaService pessoaService;
    
    @Inject
    private EstabelecimentoService estabelecimentoService;
    
    private DualListModel<PessoaEntity> pessoas;
    private List<String> transferedPessoaIDs;
    private List<String> removedPessoaIDs;
    
    private List<EstabelecimentoEntity> availableEstabelecimentoList;
    
    public void prepareNewPermissaoEstabelecimentoPessoa() {
        reset();
        this.permissaoEstabelecimentoPessoa = new PermissaoEstabelecimentoPessoaEntity();
        // set any default values now, if you need
        // Example: this.permissaoEstabelecimentoPessoa.setAnything("test");
    }

    public String persist() {

        if (permissaoEstabelecimentoPessoa.getId() == null && !isPermitted("permissaoEstabelecimentoPessoa:create")) {
            return "accessDenied";
        } else if (permissaoEstabelecimentoPessoa.getId() != null && !isPermitted(permissaoEstabelecimentoPessoa, "permissaoEstabelecimentoPessoa:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (permissaoEstabelecimentoPessoa.getId() != null) {
                permissaoEstabelecimentoPessoa = permissaoEstabelecimentoPessoaService.update(permissaoEstabelecimentoPessoa);
                message = "message_successfully_updated";
            } else {
                permissaoEstabelecimentoPessoa = permissaoEstabelecimentoPessoaService.save(permissaoEstabelecimentoPessoa);
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
        
        permissaoEstabelecimentoPessoaList = null;

        FacesMessage facesMessage = MessageFactory.getMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        
        return null;
    }
    
    public String delete() {
        
        if (!isPermitted(permissaoEstabelecimentoPessoa, "permissaoEstabelecimentoPessoa:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            permissaoEstabelecimentoPessoaService.delete(permissaoEstabelecimentoPessoa);
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
        permissaoEstabelecimentoPessoa = null;
        permissaoEstabelecimentoPessoaList = null;
        
        pessoas = null;
        transferedPessoaIDs = null;
        removedPessoaIDs = null;
        
        availableEstabelecimentoList = null;
        
    }

    // Get a List of all estabelecimento
    public List<EstabelecimentoEntity> getAvailableEstabelecimento() {
        if (this.availableEstabelecimentoList == null) {
            this.availableEstabelecimentoList = estabelecimentoService.findAvailableEstabelecimento(this.permissaoEstabelecimentoPessoa);
        }
        return this.availableEstabelecimentoList;
    }
    
    // Update estabelecimento of the current permissaoEstabelecimentoPessoa
    public void updateEstabelecimento(EstabelecimentoEntity estabelecimento) {
        this.permissaoEstabelecimentoPessoa.setEstabelecimento(estabelecimento);
        // Maybe we just created and assigned a new estabelecimento. So reset the availableEstabelecimentoList.
        availableEstabelecimentoList = null;
    }
    
    public DualListModel<PessoaEntity> getPessoas() {
        return pessoas;
    }

    public void setPessoas(DualListModel<PessoaEntity> pessoas) {
        this.pessoas = pessoas;
    }
    
    public List<PessoaEntity> getFullPessoasList() {
        List<PessoaEntity> allList = new ArrayList<>();
        allList.addAll(pessoas.getSource());
        allList.addAll(pessoas.getTarget());
        return allList;
    }
    
    public void onPessoasDialog(PermissaoEstabelecimentoPessoaEntity permissaoEstabelecimentoPessoa) {
        // Prepare the pessoa PickList
        this.permissaoEstabelecimentoPessoa = permissaoEstabelecimentoPessoa;
        List<PessoaEntity> selectedPessoasFromDB = pessoaService
                .findPessoasByPermissaoEstabelecimentoPessoa(this.permissaoEstabelecimentoPessoa);
        List<PessoaEntity> availablePessoasFromDB = pessoaService
                .findAvailablePessoas(this.permissaoEstabelecimentoPessoa);
        this.pessoas = new DualListModel<>(availablePessoasFromDB, selectedPessoasFromDB);
        
        transferedPessoaIDs = new ArrayList<>();
        removedPessoaIDs = new ArrayList<>();
    }
    
    public void onPessoasPickListTransfer(TransferEvent event) {
        // If a pessoa is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((PessoaEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedPessoaIDs.add(id);
                removedPessoaIDs.remove(id);
            } else if (event.isRemove()) {
                removedPessoaIDs.add(id);
                transferedPessoaIDs.remove(id);
            }
        }
        
    }
    
    public void updatePessoa(PessoaEntity pessoa) {
        // If a new pessoa is created, we persist it to the database,
        // but we do not assign it to this permissaoEstabelecimentoPessoa in the database, yet.
        pessoas.getTarget().add(pessoa);
        transferedPessoaIDs.add(pessoa.getId().toString());
    }
    
    public void onPessoasSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            
            List<PessoaEntity> selectedPessoasFromDB = pessoaService.findPessoasByPermissaoEstabelecimentoPessoa(this.permissaoEstabelecimentoPessoa);
            List<PessoaEntity> availablePessoasFromDB = pessoaService.findAvailablePessoas(this.permissaoEstabelecimentoPessoa);

            // Because pessoas are lazily loaded, we need to fetch them now
            this.permissaoEstabelecimentoPessoa = permissaoEstabelecimentoPessoaService.fetchPessoas(this.permissaoEstabelecimentoPessoa);
            
            for (PessoaEntity pessoa : selectedPessoasFromDB) {
                if (removedPessoaIDs.contains(pessoa.getId().toString())) {
                    
                    this.permissaoEstabelecimentoPessoa.getPessoas().remove(pessoa);
                    
                }
            }
    
            for (PessoaEntity pessoa : availablePessoasFromDB) {
                if (transferedPessoaIDs.contains(pessoa.getId().toString())) {
                    
                    this.permissaoEstabelecimentoPessoa.getPessoas().add(pessoa);
                    
                }
            }
            
            this.permissaoEstabelecimentoPessoa = permissaoEstabelecimentoPessoaService.update(this.permissaoEstabelecimentoPessoa);
            
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
    
    public PermissaoEstabelecimentoPessoaEntity getPermissaoEstabelecimentoPessoa() {
        if (this.permissaoEstabelecimentoPessoa == null) {
            prepareNewPermissaoEstabelecimentoPessoa();
        }
        return this.permissaoEstabelecimentoPessoa;
    }
    
    public void setPermissaoEstabelecimentoPessoa(PermissaoEstabelecimentoPessoaEntity permissaoEstabelecimentoPessoa) {
        this.permissaoEstabelecimentoPessoa = permissaoEstabelecimentoPessoa;
    }
    
    public List<PermissaoEstabelecimentoPessoaEntity> getPermissaoEstabelecimentoPessoaList() {
        if (permissaoEstabelecimentoPessoaList == null) {
            permissaoEstabelecimentoPessoaList = permissaoEstabelecimentoPessoaService.findAllPermissaoEstabelecimentoPessoaEntities();
        }
        return permissaoEstabelecimentoPessoaList;
    }

    public void setPermissaoEstabelecimentoPessoaList(List<PermissaoEstabelecimentoPessoaEntity> permissaoEstabelecimentoPessoaList) {
        this.permissaoEstabelecimentoPessoaList = permissaoEstabelecimentoPessoaList;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(PermissaoEstabelecimentoPessoaEntity permissaoEstabelecimentoPessoa, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
