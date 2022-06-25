package org.ethanyuen.springboot.module;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import org.ethanyuen.springboot.annotation.NotEditable;
import org.ethanyuen.springboot.annotation.Search;
import org.ethanyuen.springboot.bean.*;
import org.ethanyuen.springboot.enums.FlowNodeStatus;
import org.ethanyuen.springboot.utilbean.BaseCondition;
import org.ethanyuen.springboot.utilbean.EntityDataException;
import org.ethanyuen.springboot.utilbean.GeneralCombo;
import org.ethanyuen.springboot.utilbean.Result;
import org.ethanyuen.springboot.utils.SQLUtil;
import org.ethanyuen.springboot.utils.Utils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public abstract class BaseModule<T extends BaseEntity>  {
    String updateFields;
    @Autowired
    Dao dao;
   static final Log log = Logs.get();
    private Class<T> entityClass;
    public BaseModule() {
        Class moduleClazz = getClass();
        if (moduleClazz.getName().endsWith("AOP")) {
            moduleClazz = moduleClazz.getSuperclass();
        }
        Type baseModule = moduleClazz.getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) baseModule;
        entityClass = (Class) (pt.getActualTypeArguments()[0]);
        ArrayList<Field> fields = ListUtil.toList(ReflectUtil.getFields(getTClass()));
        fields.removeIf(item -> (item.isAnnotationPresent(NotEditable.class) && !item.getName().equals(BaseEntity.Fields.updateTime)) || item.getName().equals(BaseEntity.Fields.id));
        updateFields=StrUtil.join("|", fields.stream().map(item -> item.getName()).collect(Collectors.toList()));
    }

    /**获取泛型类
     * @return
     */
    public Class<T> getTClass()
    {
        return entityClass;
    }
    public <R extends BaseMidEntity> Result setEntity(Param source, List<R> midEntities) {
        Result result = setEntity(source);
        Long referenceId = (Long) result.getObj();
        for (BaseMidEntity midEntity: midEntities) {
            midEntity.setLeftId(referenceId);
        }
        dao.insertOrUpdate(midEntities);
        return result;
    }
    @SneakyThrows
    public Result setEntity(Param source) {
        return setEntity(Utils.transferPojo(getTClass(),source)) ;
    }
        /**设置实体
         * @param entity
         * @return
         */
    public Result setEntity(T entity) {
        Result result = new Result();
        if (checkEntityExist(entity)) {
            throw new EntityDataException("该数据已存在");
        }
        Long id =entity.getId();
        result.setObj(id);
        if (id == null) {
            entity= dao.insert(entity);
            result.setObj( entity.getId());
        } else {
//          SQLUtil.updateData(getTClass(), (BaseEntity) entity, dao,notUpdate.toArray(new String[notUpdate.size()]));
            entity.setUpdateTime(new Date());
            dao.update(entity,updateFields );
        }
        return result.setOk();
    }
    @SneakyThrows
    public <M> Result setMidEntity(Class<? extends BaseMidEntity> midClass, Param midEntity) {
        return setMidEntity(midClass, Utils.transferPojo(midClass,midEntity));
    }
    /**设置中间表实体
     * @return
     */
    public <M> Result setMidEntity(Class<M> midClass, BaseMidEntity midEntity){
        Result result = new Result();
        String[] leftIdList = midEntity.getLeftIds().split(",");
        String[] rightIdList =  midEntity.getRightIds().split(",");
        for (String leftId : leftIdList) {
            if (StrUtil.isBlank(leftId)) {
                continue;
            }
            for (String rightId : rightIdList) {
                if (StrUtil.isBlank(rightId)) {
                    continue;
                }
                midEntity.setLeftId(Long.parseLong(leftId));
                midEntity.setRightId(Long.parseLong(rightId));
                if (dao.count(midClass, SQLUtil.baseCnd().and(BaseMidEntity.Fields.leftId,"=",leftId).and(BaseMidEntity.Fields.rightId,"=",rightId))==0) {
                    dao.insert(midEntity);
                }
            }
        }
        result.setOk();
        return result;
    }
    /**删除实体
     * @param ids
     * @return
     */
    public Result deleteEntities(String ids) {
        List<T> entities =  dao.query(getTClass(), SQLUtil.baseCnd().and(BaseEntity.Fields.id, "in", ids));
        return SQLUtil.getDeleteResult(entities);
    }
    public Result deleteEntities(String ids,boolean realDelete) {
        if (realDelete) {
            Result result = new Result();
            dao.clear(getTClass(), Cnd.where(BaseEntity.Fields.id, "in", ids));
            return result.setOk();
        }
        return deleteEntities(ids);
    }
    @SneakyThrows
    public Result batchSetEntity(String ids, String field, Object value) {
        Result result = new Result();
        List<T> entities = dao.query(getTClass(), SQLUtil.baseCnd().and(BaseEntity.Fields.id, "in", ids));
        for (T entity : entities) {
            Field fileField = getTClass().getDeclaredField(field);
            fileField.setAccessible(true);
            fileField.set(entity,value);
        }
        dao.update(entities,field);
        return result.setOk();
    }

    @SneakyThrows
    public <M> Result deleteMidEntities(Class<? extends BaseMidEntity> midClass, Param entity) {
        return deleteMidEntities(midClass, Utils.transferPojo(midClass, entity));
    }
        /**删除中间表实体
         * @param midClass
         * @param entity
         * @param <M>
         * @return
         */
    public <M> Result deleteMidEntities(Class<M> midClass, BaseMidEntity entity) {
        List<M> entities =  dao.query(midClass, SQLUtil.baseCnd().and(BaseMidEntity.Fields.rightId, "in", entity.getRightIds()).and(BaseMidEntity.Fields.leftId, "in", entity.getLeftIds()));
        return SQLUtil.getDeleteResult(entities);
    }
    public <M>Result deleteMidEntities(Class<M> midClass, BaseMidEntity entity,boolean realDelete) {
        if (realDelete) {
            Result result = new Result();
            dao.clear(midClass, Cnd.where(BaseMidEntity.Fields.rightId, "in", entity.getRightIds()).and(BaseMidEntity.Fields.leftId, "in", entity.getLeftIds()));
            return result.setOk();
        }
        return deleteMidEntities(midClass,entity);
    }

    @SneakyThrows
    public <R> Result getRelations(Class<R> relationClass, Param relationCondition, Class<? extends BaseMidEntity> midClass, Param condition, BaseCondition baseCondition, SqlExpressionGroup customSql) {
        return getRelations(relationClass, Utils.transferPojo(relationClass,relationCondition), midClass,Utils.transferPojo(midClass, condition), baseCondition, customSql);
    }
        /**获取相关数据
         * @param relationClass
         * @param midClass
         * @param condition
         * @param baseCondition
         * @param customSql
         * @param <R>
         * @param <M>
         * @return
         */
    @SneakyThrows
    public <R,M> Result getRelations(Class<R> relationClass, R relationCondition, Class<M> midClass, BaseMidEntity condition, BaseCondition baseCondition, SqlExpressionGroup customSql){
        Result result = new Result();
        List<M> midEntities;
        List<Long> relationIds;
        if(condition.getLeftId()!=null){
            midEntities= dao.query(midClass, SQLUtil.baseCnd().and(BaseMidEntity.Fields.leftId, "=", condition.getLeftId()));
            relationIds = midEntities.stream().map(item -> ((BaseMidEntity)item).getRightId()).collect(Collectors.toList());
        }else if (condition.getRightId()!=null) {
            midEntities = dao.query(midClass, SQLUtil.baseCnd().and(BaseMidEntity.Fields.rightId, "=", condition.getRightId()));
            relationIds = midEntities.stream().map(item -> ((BaseMidEntity)item).getLeftId()).collect(Collectors.toList());
        }else{
            return result.setBad("请选择数据");
        }
        Map map = SQLUtil.getBaseSearchEXPGroup( condition, baseCondition);
        SqlExpressionGroup sql = getBaseSqlExpressionGroup(customSql, map);
        if (relationCondition!=null) {
            Class tmpClass = relationClass;
            getFieldFilter(relationCondition, sql, tmpClass,baseCondition);
        }

        if (condition.getNotSelected()==null||condition.getNotSelected()==false) {
            sql.and(BaseEntity.Fields.id, "in", relationIds);
        }else{
            if (relationIds.size()!=0) {
                sql.and(BaseEntity.Fields.id, "not in" , relationIds);
            }
        }
        List<R> relations = dao.query(relationClass, SQLUtil.addOrders(Cnd.where(sql), baseCondition), (Pager) map.get("pager"));
        if (baseCondition!=null&&StrUtil.isNotBlank(baseCondition.getLinks())) {
            dao.fetchLinks(relations,baseCondition.getLinks(), SQLUtil.baseCnd());
        }
        result.setList(relations);
        result.setTotal(dao.count(relationClass, Cnd.where(sql)));
        return result.setOk();
    }

    private <R> void getFieldFilter(R relationCondition, SqlExpressionGroup sql, Class tmpClass, BaseCondition baseCondition) throws IllegalAccessException {
        Field[] fields =ReflectUtil.getFields(tmpClass);
        SqlExpressionGroup orCondition = new SqlExpressionGroup();
        boolean searchKey=false;
        if (baseCondition!=null&&StrUtil.isNotBlank(baseCondition.getKey())) {
            searchKey=true;
        }
        for (Field field : fields) {
            Search search = field.getAnnotation(Search.class);
            String name = field.getName();
            if (search != null) {
                Class fieldType = field.getType();
                field.setAccessible(true);
                Object value = field.get(relationCondition);
                if (fieldType.equals(String.class)) {
                    if (searchKey&&search.key()) {
                        orCondition.or(name, "like", "%" +  baseCondition.getKey() + "%");
                        continue;
                    }
                    if (StrUtil.isNotBlank((String) value)){
                        sql.and(name, "like",   "%" + (String) value + "%" );
                    }
                } else if (fieldType.equals(int.class)) {
                    sql.andEX(name, "=", (int) value == -99 ? null : (int) value);
                } else if (fieldType.equals(Integer.class)) {
                    sql.andEX(name, "=", value);
                } else if (fieldType.equals(long.class)) {
                    sql.andEX(name, "=", (long) value == -99 ? null : (long) value);
                } else if (fieldType.equals(Long.class)) {
                    sql.andEX(name, "=", value);
                } else if (fieldType.isEnum()) {
                    sql.andEX(name, "=", (Enum) value);
                }else if (fieldType.equals(Boolean.class)) {
                    sql.andEX(name, "=", value);
                }
            }
        }
        if (!orCondition.isEmpty()) {
            sql.and(orCondition);
        }
    }
    @SneakyThrows
    public Result getEntities(Param source, BaseCondition baseCondition, SqlExpressionGroup customSql) {
        return getEntities(Utils.transferPojo(getTClass(),source), baseCondition,customSql);
    }
    @SneakyThrows
    public Result getEntities(Param source, BaseCondition baseCondition) {
        return getEntities(Utils.transferPojo(getTClass(),source), baseCondition,null);
    }
    public Result getEntities(T condition, BaseCondition baseCondition) {
        return getEntities(condition, baseCondition,null);
    }
    /**获取实体
     * @param condition
     * @param baseCondition
     * @return
     */
    @SneakyThrows
    public Result getEntities(T condition, BaseCondition baseCondition, SqlExpressionGroup customSql) {
        Result result = new Result();
        Map map = SQLUtil.getBaseSearchEXPGroup( condition, baseCondition);
        SqlExpressionGroup sql = getBaseSqlExpressionGroup(customSql, map);
        if (condition!=null) {
            Class tmpClass = getTClass();
            getFieldFilter(condition, sql, tmpClass, baseCondition);
        }

        List<T> entities = dao.query(getTClass(), SQLUtil.addOrders(Cnd.where(sql), baseCondition), (Pager) map.get("pager"));
        if (baseCondition!=null&&StrUtil.isNotBlank(baseCondition.getLinks())) {
            dao.fetchLinks(entities,baseCondition.getLinks(), SQLUtil.baseCnd());
        }
        if (baseCondition!=null&& Strings.isNotBlank(baseCondition.getMidLinks())) {
            dao.fetchLinks(entities,baseCondition.getLinks(), SQLUtil.baseCnd());
            fetchMidAndLinks(entities,baseCondition.getMidLinks());
        }
        result.setList(entities);
        if (baseCondition.isCombo()) {
            result.setList(getGeneralCombos(entities));
        }
        if (baseCondition.isTree()&&condition instanceof BaseTreeEntity) {
            BaseTreeEntity top = new BaseTreeEntity();
            top.setFatherId(0L);
            top.setName("全部");
            top.setId(null);
            top.setWeight(null);
            entities.add(0, (T) top);
            List<Tree<Long>> trees = TreeUtil.build(entities , 0L, (object, tree) -> {
                tree.setParentId(((BaseTreeEntity)object).getFatherId());
                tree.setId(((BaseTreeEntity)object).getId());
                tree.setName(((BaseTreeEntity)object).getName());
                tree.setWeight(((BaseTreeEntity) object).getWeight());
            });
            result.setList(trees);
        }
        result.setTotal(dao.count(getTClass(), Cnd.where(sql)));

        return result.setOk();
    }

    public List<GeneralCombo> getGeneralCombos(List<T> entities) {
        List<GeneralCombo> combos = new ArrayList<>();
        for (T entity : entities) {
            GeneralCombo combo = new GeneralCombo(entity.getId() + "", ((BaseNameEntity)entity).getName());
            combos.add(combo);
        }
        return combos;
    }

    private SqlExpressionGroup getBaseSqlExpressionGroup(SqlExpressionGroup customSql, Map map) {
        SqlExpressionGroup sql = (SqlExpressionGroup) map.get("sql");
        if (customSql != null && !customSql.isEmpty()) {
            sql.and(customSql);
        }
        return sql;
    }

    @SneakyThrows
    public boolean checkEntityExist(Param source) {
        return checkEntityExist(Utils.transferPojo(getTClass(),source));
    }
        /**
         * 检查实体，存在返回true
         *
         * @param entity
         * @return
         */
    public boolean checkEntityExist(T entity) {
        T preEntity = getPreEntity(entity);
        if (preEntity==null) {
            return false;
        }
        if (preEntity instanceof BaseEntity) {
            if (preEntity.getId()!= entity.getId()){
                return true;
            } else {
                return false;
            }
        }else if (entity instanceof BaseMidEntity) {
            return true;
        }
        return false;
    }

    public T getPreEntity(T entity) {
        if (entity instanceof BaseNameEntity) {
            return dao.fetch(getTClass(), SQLUtil.baseCnd().and(BaseNameEntity.Fields.name, "=", ((BaseNameEntity)entity).getName()));
        }else if (entity instanceof BaseMidEntity) {
            return dao.fetch(getTClass(), SQLUtil.baseCnd().and(BaseMidEntity.Fields.rightId, "=", ((BaseMidEntity)entity).getRightId()).and(BaseMidEntity.Fields.leftId, "=", ((BaseMidEntity)entity).getLeftId()));
        }else{
            return null;
        }
    }
    @SneakyThrows
    public Result uploadSingleFile(Long id, MultipartFile file, String field)  {
        Result result=new Result();
        if (file != null) {
            String filePath = Utils.getIocBean(FileModule.class).uploadFile(file, field);
            if (!StrUtil.isBlank(filePath)) {
                T entity = getTClass().newInstance();
                ReflectUtil.setFieldValue(entity,BaseEntity.Fields.id,id);
                ReflectUtil.setFieldValue(entity,field,filePath);
                dao.update(entity, field);
            }
            result.setObj(filePath);
        }
        return result.setOk();
    }
    @SneakyThrows
    public Result deleteSingleFile(Long id, String field) {
        Result result = new Result();
        T entity = getTClass().newInstance();
        ReflectUtil.setFieldValue(entity,BaseEntity.Fields.id,id);
        ReflectUtil.setFieldValue(entity,field,"");
        dao.update(entity, field);
        return result.setOk();
    }
    public  <F extends BaseFlowNodeEntity> Result insertFlowNode(Class<F> flowNodeClass, F flowNodeEntity, FlowNodeStatus nowNodeStatus) {
        T flowEntity = dao.fetch(getTClass(), flowNodeEntity.getReferenceId());
        FlowNodeStatus preStatus = ((BaseFlowEntity) flowEntity).getStatus();
        if (preStatus!=null&&!nowNodeStatus.isIgnoreSeq()&&preStatus.getOrdinal() != nowNodeStatus.getOrdinal()-1) {
            throw new EntityDataException("流程错误");
        }
        ((BaseFlowEntity) flowEntity).setStatus(nowNodeStatus);
        dao.update(flowEntity, BaseFlowEntity.Fields.status);
        flowNodeEntity.setNode(nowNodeStatus);
        dao.insert(flowNodeEntity);
        return Result.setOk("");
    }
    public  <F extends BaseFlowNodeEntity>Result getVerifierFlowEntities(Class<F> flowNodeClass, FlowNodeStatus selfVerifyStatus,T condition,BaseCondition baseCondition, User user,SqlExpressionGroup exps) {
        if (exps==null) {
            exps = new SqlExpressionGroup();
        }
        FlowNodeStatus filterStatus = ((BaseFlowEntity) condition).getStatus();
        Class<? extends FlowNodeStatus> aClass = selfVerifyStatus.getClass();
        if (filterStatus!=null) {
            if ( filterStatus.isSearchUnverified()) {
                ((BaseFlowEntity) condition).setStatus((FlowNodeStatus) ReflectUtil.getFieldValue(selfVerifyStatus,aClass.getDeclaredFields()[selfVerifyStatus.getOrdinal()-1]));
            } else  {
                exps.and(BaseEntity.Fields.id, "in", dao.query(flowNodeClass, SQLUtil.baseCnd().and(BaseEntity.Fields.authorId, "=", user.getId()).and(BaseFlowNodeEntity.Fields.node, "=", selfVerifyStatus), null, BaseFlowNodeEntity.Fields.referenceId).stream().map(BaseFlowNodeEntity::getReferenceId).collect(Collectors.toList()));
                ((BaseFlowEntity) condition).setStatus(null);
            }
        }
        return getEntities(condition,baseCondition,exps);
    }

    public void fetchMidAndLinks(List<T> entities, String midLinks) {
        String[] linkStrs = midLinks.split("\\|");
        for (String linkStr : linkStrs) {
            Field midLinkField = ReflectUtil.getField(getTClass(), linkStr);
            ParameterizedType type = (ParameterizedType)midLinkField.getGenericType();
            Class midClass = (Class) type.getActualTypeArguments()[0];
            String midLink = StrUtil.lowerFirst(midClass.getSimpleName().split(getTClass().getSimpleName())[1]) ;
            for (T entity : entities) {
                List midEntities = dao.query(midClass, SQLUtil.baseCnd().and(BaseMidEntity.Fields.leftId, "=", entity.getId()));
                dao.fetchLinks(midEntities, midLink);
                ReflectUtil.setFieldValue(entity,midLinkField,midEntities);
            }
        }

    }

    public <M extends BaseMidEntity> Result setSingleMidEntity(Class<M> midClass,Long leftId,Long rightId,String field,Object value) {
        M midEntity = dao.fetch(midClass, SQLUtil.baseCnd().and(BaseMidEntity.Fields.leftId, "=", leftId).and(BaseMidEntity.Fields.rightId, "=", rightId));
        if (midEntity==null) {
            throw new EntityDataException("找不到关联");
        }
        ReflectUtil.setFieldValue(midEntity,field,value);
        dao.update(midEntity,field);
        return Result.setOk("");
    }
}
