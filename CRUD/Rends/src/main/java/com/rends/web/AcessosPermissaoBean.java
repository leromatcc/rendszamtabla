package com.rends.web;

import com.rends.domain.AcessosPermissaoEntity;
import com.rends.domain.EmpresaEntity;
import com.rends.domain.PessoaEntity;
import com.rends.service.AcessosPermissaoService;
import com.rends.service.EmpresaService;
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

@Named("acessosPermissaoBean")
@ViewScoped
public class AcessosPermissaoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(AcessosPermissaoBean.class.getName());
    
    private List<AcessosPermissaoEntity> acessosPermissaoList;

    private AcessosPermissaoEntity acessosPermissao;
    
    @Inject
    private AcessosPermissaoService acessosPermissaoService;
    
    @Inject
    private PessoaService pessoaService;
    
    @Inject
    private EmpresaService empresaService;
    
    private DualListModel<PessoaEntity> pessoas;
    private List<String> transferedPessoaIDs;
    private List<String> removedPessoaIDs;
    
    private List<EmpresaEntity> availableEmpresaList;
    
    public void prepareNewAcessosPermissao() {
        reset();
        this.acessosPermissao = new AcessosPermissaoEntity();
        // set any default values now, if you need
        // Example: this.acessosPermissao.setAnything("test");
    }

    public String persist() {

        if (acessosPermissao.getId() == null && !isPermitted("acessosPermissao:create")) {
            return "accessDenied";
        } else if (acessosPermissao.getId() != null && !isPermitted(acessosPermissao, "acessosPermissao:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (acessosPermissao.getId() != null) {
                acessosPermissao = acessosPermissaoService.update(acessosPermissao);
                message = "message_successfully_updated";
            } else {
                acessosPermissao = acessosPermissaoService.save(acessosPermissao);
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
        
        acessosPermissaoList = null;

        FacesMessage facesMessage = MessageFactory.getMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        
        return null;
    }
    
    public String delete() {
        
        if (!isPermitted(acessosPermissao, "acessosPermissao:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            acessosPermissaoService.delete(acessosPermissao);
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
        acessosPermissao = null;
        acessosPermissaoList = null;
        
        pessoas = null;
        transferedPessoaIDs = null;
        removedPessoaIDs = null;
        
        availableEmpresaList = null;
        
    }

    // Get a List of all empresa
    public List<EmpresaEntity> getAvailableEmpresa() {
        if (this.availableEmpresaList == null) {
            this.availableEmpresaList = empresaService.findAvailableEmpresa(this.acessosPermissao);
        }
        return this.availableEmpresaList;
    }
    
    // Update empresa of the current acessosPermissao
    public void updateEmpresa(EmpresaEntity empresa) {
        this.acessosPermissao.setEmpresa(empresa);
        // Maybe we just created and assigned a new empresa. So reset the availableEmpresaList.
        availableEmpresaList = null;
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
    
    public void onPessoasDialog(AcessosPermissaoEntity acessosPermissao) {
        // Prepare the pessoa PickList
        this.acessosPermissao = acessosPermissao;
        List<PessoaEntity> selectedPessoasFromDB = pessoaService
                .findPessoasByAcessosPermissao(this.acessosPermissao);
        List<PessoaEntity> availablePessoasFromDB = pessoaService
                .findAvailablePessoas(this.acessosPermissao);
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
        // but we do not assign it to this acessosPermissao in the database, yet.
        pessoas.getTarget().add(pessoa);
        transferedPessoaIDs.add(pessoa.getId().toString());
    }
    
    public void onPessoasSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            
            List<PessoaEntity> selectedPessoasFromDB = pessoaService.findPessoasByAcessosPermissao(this.acessosPermissao);
            List<PessoaEntity> availablePessoasFromDB = pessoaService.findAvailablePessoas(this.acessosPermissao);

            // Because pessoas are lazily loaded, we need to fetch them now
            this.acessosPermissao = acessosPermissaoService.fetchPessoas(this.acessosPermissao);
            
            for (PessoaEntity pessoa : selectedPessoasFromDB) {
                if (removedPessoaIDs.contains(pessoa.getId().toString())) {
                    
                    this.acessosPermissao.getPessoas().remove(pessoa);
                    
                }
            }
    
            for (PessoaEntity pessoa : availablePessoasFromDB) {
                if (transferedPessoaIDs.contains(pessoa.getId().toString())) {
                    
                    this.acessosPermissao.getPessoas().add(pessoa);
                    
                }
            }
            
            this.acessosPermissao = acessosPermissaoService.update(this.acessosPermissao);
            
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
    
    public AcessosPermissaoEntity getAcessosPermissao() {
        if (this.acessosPermissao == null) {
            prepareNewAcessosPermissao();
        }
        return this.acessosPermissao;
    }
    
    public void setAcessosPermissao(AcessosPermissaoEntity acessosPermissao) {
        this.acessosPermissao = acessosPermissao;
    }
    
    public List<AcessosPermissaoEntity> getAcessosPermissaoList() {
        if (acessosPermissaoList == null) {
            acessosPermissaoList = acessosPermissaoService.findAllAcessosPermissaoEntities();
        }
        return acessosPermissaoList;
    }

    public void setAcessosPermissaoList(List<AcessosPermissaoEntity> acessosPermissaoList) {
        this.acessosPermissaoList = acessosPermissaoList;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(AcessosPermissaoEntity acessosPermissao, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
