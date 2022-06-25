package org.ethanyuen.springboot.module;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.ethanyuen.springboot.annotation.SysLog;
import org.ethanyuen.springboot.bean.*;
import org.ethanyuen.springboot.bean.form.MidRolePermissionFormParam;
import org.ethanyuen.springboot.bean.form.MidUserPermissionFormParam;
import org.ethanyuen.springboot.bean.form.PermissionFormParam;
import org.ethanyuen.springboot.bean.query.*;
import org.ethanyuen.springboot.enums.BusinessType;
import org.ethanyuen.springboot.utilbean.BaseCondition;
import org.ethanyuen.springboot.utilbean.Result;
import org.ethanyuen.springboot.utilbean.TreeCombo;
import org.ethanyuen.springboot.utils.SQLUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@Api(tags ="权限管理")
@RequestMapping("permission")
public class PermissionModule extends BaseModule<Permission> {
    @Autowired
    private RoleModule roleModule;
    @RequestMapping
    @SaCheckPermission("permission:set")
    @SysLog(type = BusinessType.SET)
    @ApiOperation("设置权限")
    public Result setPermission(PermissionFormParam permission) {
        return setEntity(permission);
    }


    @RequestMapping
    @SaCheckPermission("permission:deletePermission")
    @SysLog(type = BusinessType.DELETE)
    @ApiOperation("删除权限")
    public Result deletePermission(String ids) {
        return deleteEntities(ids);
    }


    @RequestMapping
    @SaCheckPermission("permission:getPermissions")
    @ApiOperation(value = "获取权限列表")
    public Result<Permission> getPermissions(PermissionQueryParam condition, BaseCondition baseCondition) {
        return getEntities(condition,baseCondition);
    }
    @RequestMapping
    @SaCheckPermission("permission:getUserTreePermissions")
    @ApiOperation("获取用户树形权限")
    public Result getUserTreePermissions(Long userId) {
        Result result=new Result();
        List<TreeCombo> combos = new ArrayList<>();
        List<Permission> fathers = dao.query(Permission.class, SQLUtil.baseCnd().and(Permission.Fields.fatherId, "=", 0));
        for (Permission father : fathers) {
            TreeCombo fatherCombo = processPermissionCombo(userId, father.getId(),father.getAlias(), MidUserPermission.class);
            List<Permission> children = dao.query(Permission.class, SQLUtil.baseCnd().and(Permission.Fields.fatherId, "=", father.getId()));
            List<TreeCombo> childrenCombos = new ArrayList<>();
            for (Permission child : children) {
                TreeCombo childCombo = processPermissionCombo(userId, child.getId(),child.getAlias(), MidUserPermission.class);
                childrenCombos.add(childCombo);
            }
            fatherCombo.setChildren(childrenCombos);
            combos.add(fatherCombo);
        }
        result.setList(combos);
        return result.setOk();
    }

    private TreeCombo processPermissionCombo(Long userId, Long id, String title, Class clazz) {
        TreeCombo childCombo = new TreeCombo();
        childCombo.setId(id);
        childCombo.setLabel(title);
        if (dao.count(clazz, SQLUtil.baseCnd().and(BaseMidEntity.Fields.leftId, "=", userId).and(BaseMidEntity.Fields.rightId, "=", id)) > 0) {
            childCombo.setChecked(true);
        }
        return childCombo;
    }

    @RequestMapping
    @SaCheckPermission("permission:getRoleTreePermissions")
    @ApiOperation("获取角色树形权限")
    public Result getRoleTreePermissions(Long roleId) {
        Result result=new Result();
        List<TreeCombo> combos = new ArrayList<>();
        List<Permission> fathers = dao.query(Permission.class, SQLUtil.baseCnd().and(Permission.Fields.fatherId, "=", 0));
        for (Permission father : fathers) {
            TreeCombo fatherCombo = processPermissionCombo(roleId,  father.getId(),father.getAlias(), MidRolePermission.class);
            List<Permission> children = dao.query(Permission.class, SQLUtil.baseCnd().and(Permission.Fields.fatherId, "=", father.getId()));
            List<TreeCombo> childrenCombos = new ArrayList<>();
            for (Permission child : children) {
                TreeCombo childCombo = processPermissionCombo(roleId, child.getId(),child.getAlias(), MidRolePermission.class);
                childrenCombos.add(childCombo);
            }
            fatherCombo.setChildren(childrenCombos);
            combos.add(fatherCombo);
        }
        result.setList(combos);
        return result.setOk();
    }
    @RequestMapping
    @SaCheckPermission("permission:getUserPermissions")
    @ApiOperation("获取用户已有的权限")
    public Result getUserPermissions(MidUserPermissionQueryParam condition, BaseCondition baseCondition, UserQueryParam userCondition, PermissionQueryParam permissionCondition) {
        if (condition.getLeftId()!=null) {
            return getRelations(Permission.class,permissionCondition, MidUserPermission.class,condition, baseCondition,null);
        }
        return getRelations(User.class,userCondition, MidUserPermission.class,condition, baseCondition,null);
    }
    @RequestMapping
    @SaCheckPermission("permission:getUserAndRolesPermissions")
    @ApiOperation("获取用户已有的权限(包括所属角色的权限)")
    public Result getUserAndRolesPermissions(Long userId) {
        Result result=new Result();
        Set<String> ownedRoles=new HashSet<>();
        Set<String> ownedPermissions=new HashSet<>();
        MidUserRoleQueryParam userRole = new MidUserRoleQueryParam();
        userRole.setLeftId(userId);
        List<Role> roles = roleModule.getUserRoles(userRole, null,null, null).getList();
        MidUserPermissionQueryParam userPermission= new MidUserPermissionQueryParam();
        userPermission.setLeftId(userId);
        List<Permission> userPermissions = getUserPermissions(userPermission,  null,  null, null).getList();
        if (roles != null) {
            for (Role role : roles) {
                MidRolePermissionQueryParam rolePermission = new MidRolePermissionQueryParam();
                rolePermission.setLeftId(role.getId());
                List<Permission> rolePermissions= getRolePermissions(rolePermission,  new BaseCondition(),  new RoleQueryParam(), new PermissionQueryParam()).getList();
                ownedRoles.add(role.getName());
                if (rolePermissions!= null) {
                    for (Permission p : rolePermissions) {
                        ownedPermissions.add(p.getName());
                    }
                }
            }
        }
        if (userPermissions != null) { // 特许/临时分配的权限
            for (Permission p :userPermissions) {
                ownedPermissions.add(p.getName());
            }
        }
        Map<String, Set> authInfo = new HashMap<>();
        authInfo.put("ownedRoles", ownedRoles);
        authInfo.put("ownedPermissions", ownedPermissions);
        result.setObj(authInfo);
        return result.setOk();
    }
    @RequestMapping
    @SaCheckPermission("permission:getRolePermissions")
    @ApiOperation("获取角色已有的权限")
    public Result getRolePermissions(MidRolePermissionQueryParam condition, BaseCondition baseCondition, RoleQueryParam roleCondition, PermissionQueryParam permissionCondition) {
        if (condition.getLeftId()!=null) {
            return getRelations(Permission.class,permissionCondition, MidRolePermission.class,condition, baseCondition,null);
        }
        return getRelations(Role.class,roleCondition, MidRolePermission.class,condition, baseCondition,null);
    }
    @RequestMapping
    @SaCheckPermission("permission:addUserPermission")
    @SysLog(type = BusinessType.ADD)
    @ApiOperation("添加人员权限")
    public Result addUserPermission(MidUserPermissionFormParam entity) {
        return setMidEntity(MidUserPermission.class,entity);
    }

    @RequestMapping
    @SaCheckPermission("permission:delUserPermission")
    @SysLog(type = BusinessType.DELETE)
    @ApiOperation("删除人员权限")
    public Result delUserPermission(MidUserPermissionFormParam entity) {
        return deleteMidEntities(MidUserPermission.class,entity);
    }

    @RequestMapping
    @SaCheckPermission("permission:addRolePermission")
    @SysLog(type = BusinessType.SET)
    @ApiOperation("设置角色权限")
    public Result addRolePermission(MidRolePermissionFormParam entity) {
        return setMidEntity(MidRolePermission.class,entity);
    }

    @RequestMapping
    @SaCheckPermission("permission:delRolePermission")
    @SysLog(type = BusinessType.DELETE)
    @ApiOperation("删除角色权限")
    public Result delRolePermission(MidRolePermissionFormParam entity) {
        return deleteMidEntities(MidRolePermission.class,entity);
    }

}
