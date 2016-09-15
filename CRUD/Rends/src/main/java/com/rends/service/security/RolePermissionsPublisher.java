package com.rends.service.security;

import com.rends.domain.security.RolePermission;
import com.rends.domain.security.UserRole;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Singleton
@Startup
public class RolePermissionsPublisher {

    private static final Logger logger = Logger.getLogger(RolePermissionsPublisher.class.getName());
    
    @Inject
    private RolePermissionsService rolePermissionService;
    
    @PostConstruct
    public void postConstruct() {

        if (rolePermissionService.countAllEntries() == 0) {

            rolePermissionService.save(new RolePermission(UserRole.Administrator, "pessoa:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "pessoa:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "pessoa:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "pessoa:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "pessoa:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "pessoa:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "pessoa:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "pessoa:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "estabelecimento:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "estabelecimento:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "estabelecimento:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "estabelecimento:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "automovelTipo:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "automovelTipo:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "automovelTipo:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "automovelTipo:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "automovel:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "automovel:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "automovel:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "automovel:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "automovel:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "automovel:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "automovel:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "automovel:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "pessoaAutomovel:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "pessoaAutomovel:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "pessoaAutomovel:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "pessoaAutomovel:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "pessoaAutomovel:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "pessoaAutomovel:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "pessoaAutomovel:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "pessoaAutomovel:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "estabelecimentoVisita:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "estabelecimentoVisita:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "estabelecimentoVisita:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "estabelecimentoVisita:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "estabelecimentoVisita:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "estabelecimentoVisita:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "estabelecimentoVisita:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "estabelecimentoVisita:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "camera:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "camera:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "camera:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "camera:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "imagens:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "imagens:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "imagens:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "imagens:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "imagens:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "imagens:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "imagens:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "imagens:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "imagemCamera:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "imagemCamera:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "imagemCamera:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "imagemCamera:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "imagemCamera:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "imagemCamera:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "imagemCamera:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "imagemCamera:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "permissaoEstabelecimentoPessoa:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "permissaoEstabelecimentoPessoa:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "permissaoEstabelecimentoPessoa:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "permissaoEstabelecimentoPessoa:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "permissaoEstabelecimentoPessoa:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "permissaoEstabelecimentoPessoa:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "permissaoEstabelecimentoPessoa:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Registered, "permissaoEstabelecimentoPessoa:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "user:*"));
            
            logger.info("Successfully created permissions for user roles.");
        }
    }
}
