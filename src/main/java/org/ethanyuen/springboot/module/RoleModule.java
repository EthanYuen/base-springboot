package org.ethanyuen.springboot.module;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.ethanyuen.springboot.annotation.SysLog;
import org.ethanyuen.springboot.bean.*;
import org.ethanyuen.springboot.bean.form.MidUserRoleFormParam;
import org.ethanyuen.springboot.bean.form.RoleFormParam;
import org.ethanyuen.springboot.bean.query.MidUserRoleQueryParam;
import org.ethanyuen.springboot.bean.query.RoleQueryParam;
import org.ethanyuen.springboot.bean.query.UserQueryParam;
import org.ethanyuen.springboot.enums.BusinessType;
import org.ethanyuen.springboot.utilbean.BaseCondition;
import org.ethanyuen.springboot.utilbean.Result;
import org.ethanyuen.springboot.utilbean.Roles;
import org.ethanyuen.springboot.utils.SQLUtil;
import org.nutz.resource.Scans;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@Api(tags ="角色管理")
@RequestMapping("role")
public class RoleModule extends BaseModule<Role> {
    @RequestMapping
    @SaCheckPermission("role:set")
    @SysLog(type = BusinessType.SET)
    @ApiOperation("设置角色")
    public Result setRole(RoleFormParam role) {
        return setEntity(role);
    }

    @RequestMapping
    @SaCheckPermission("role:delete")
    @SysLog(type = BusinessType.DELETE)
    @ApiOperation("删除角色")
    public Result deleteRole(String ids) {
        return deleteEntities(ids);
    }


    @RequestMapping
    @SaCheckPermission("role:get")
    @ApiOperation(value = "获取角色列表")
    public Result<Role> getRoles(RoleQueryParam condition, BaseCondition baseCondition) {
        return getEntities(condition, baseCondition,null);
    }

    @RequestMapping
    @SaCheckPermission("role:setUserRole")
    @SysLog(type = BusinessType.SET)
    @ApiOperation("设置用户角色")
    public Result setUserRoles(MidUserRoleFormParam entity) {
        return setMidEntity(MidUserRole.class,entity);
    }

    @RequestMapping
    @SaCheckPermission("role:deleteUserRoles")
    @SysLog(type = BusinessType.SET)
    @ApiOperation("删除用户角色")
    public Result deleteUserRoles(MidUserRoleFormParam entity) {
        return deleteMidEntities(MidUserRole.class, entity);
    }
    @RequestMapping
    @SaCheckPermission("role:getUserRole")
    @ApiOperation("获取用户角色")
    public Result getUserRoles(MidUserRoleQueryParam condition, BaseCondition baseCondition, RoleQueryParam roleCondition, UserQueryParam userCondition) {
        if (condition.getLeftId()!=null) {
            return getRelations(Role.class,roleCondition, MidUserRole.class,condition, baseCondition,null);
        }
        return getRelations(User.class,userCondition, MidUserRole.class,condition, baseCondition,null);
    }
    /**
     * 扫描SaCheckPermission和SaCheckRole注解
     * @param pkg 需要扫描的package
     */
    @SneakyThrows
    public void initFormPackage(String pkg)
    {
        final HashMap<String, Permission> permissions = new HashMap<>();
        final Set<String> roles = new HashSet<>();
        for (Class<?> klass : Scans.me().scanPackage(pkg)) {
            Api fatherAlia = klass.getAnnotation(Api.class);
            RequestMapping fatherName = klass.getAnnotation(RequestMapping.class);
            Permission father=null;
            if (fatherAlia != null&&fatherName!=null&&! fatherName.value()[0].equals("auth")) {
                father = dao.fetch(Permission.class, SQLUtil.baseCnd().and(BaseNameEntity.Fields.name, "=", fatherName.value()[0]));
                if (father==null) {
                    father=dao.insert(new Permission(fatherName.value()[0],fatherAlia.tags()[0],0L));
                }
            }
            for (Method method : klass.getMethods()) {
                SaCheckPermission rp = method.getAnnotation(SaCheckPermission.class);
                if (rp != null && rp.value() != null) {
                    for (String permission : rp.value()) {
                        if (permission != null && !permission.endsWith("*")) {
                            ApiOperation desc = method.getAnnotation(ApiOperation.class);
                            String temp = "";
                            if (desc != null) {
                                temp = desc.value();
                            }
                            permissions.put(permission, new Permission(permission,temp,father==null?0:father.getId()));
                        }
                    }
                }
                SaCheckRole rr = method.getAnnotation(SaCheckRole.class);
                if (rr != null && rr.value() != null) {
                    for (String role : rr.value()) {
                        roles.add(role);
                    }
                }
            }
        }
        // 把全部权限查出来筛选没有的权限和角色
        for (Permission dbPermission : dao.query(Permission.class, SQLUtil.baseCnd())) {
            permissions.remove(dbPermission.getName());
        }
        Field[] roleList = Roles.class.getDeclaredFields();
        for (Field field : roleList) {
            String roleName = (String) field.get(Roles.class);
            roles.add(roleName);
        }
        for (Role dbRole : dao.query(Role.class, SQLUtil.baseCnd())) {
            roles.remove(dbRole.getName());
        }
        for (String permission : permissions.keySet()) {
            dao.insert(permissions.get(permission));
        }
        for (String role : roles) {
            addRole(role);
        }
    }

    /**
     * 检查最基础的权限,确保admin用户-admin角色-(用户增删改查-权限增删改查)这一基础权限设置
     * @param admin
     */
    public void checkBasicRoles(User admin) {
        // 检查一下admin的权限
        Role adminRole = dao.fetch(Role.class, SQLUtil.baseCnd().and(BaseNameEntity.Fields.name,"=", Roles.ROLE_PLATFORM));
        if (adminRole == null) {
            adminRole = addRole(Roles.ROLE_PLATFORM);
        }
        // admin账号必须存在与admin组
        if (0 == dao.count(MidUserRole.class, SQLUtil.baseCnd().and(BaseMidEntity.Fields.leftId, "=", admin.getId()).and(BaseMidEntity.Fields.rightId, "=", adminRole.getId()))) {
            MidUserRole userRole=new MidUserRole();
            userRole.setRightId(adminRole.getId());
            userRole.setLeftId(admin.getId());
            dao.insert(userRole);
        }
        // admin组必须有authority:* 也就是权限管理相关的权限
        List<MidRolePermission> res = dao.query(MidRolePermission.class, SQLUtil.baseCnd().and(BaseMidEntity.Fields.leftId, "=", adminRole.getId()));
        List<Permission> permissions = dao.query(Permission.class, SQLUtil.baseCnd());
        OUT: for (Permission permission : permissions) {
            for (MidRolePermission re : res) {
                if (re.getRightId().equals(permission.getId()))
                    continue OUT;
            }
            MidRolePermission rolePermission=new MidRolePermission();
            rolePermission.setRightId(permission.getId());
            rolePermission.setLeftId(adminRole.getId());
            dao.insert(rolePermission);
        };
    }

    public Role addRole(String role)
    {
        Role r = new Role();
        r.setName(role);
//        r.setCompanyId(0);
        return dao.insert(r);
    }
}
