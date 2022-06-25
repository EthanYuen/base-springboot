package org.ethanyuen.springboot.utils;

import org.ethanyuen.springboot.bean.BaseEntity;
import org.ethanyuen.springboot.enums.DaoOperator;
import org.ethanyuen.springboot.utilbean.BaseCondition;
import org.ethanyuen.springboot.utilbean.OrderCombo;
import org.ethanyuen.springboot.utilbean.Result;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.Daos;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import java.util.*;

public class SQLUtil {
    public static List<OrderCombo> baseOrder = new ArrayList<>();
    public static String baseFields;
    static {
        OrderCombo orderCombo = new OrderCombo(BaseEntity.Fields.createTime, DaoOperator.DESC);
        baseOrder.add(orderCombo);
        baseFields=joinFields(BaseEntity.Fields.createTime,BaseEntity.Fields.isDeleted,BaseEntity.Fields.author,BaseEntity.Fields.authorId);

    }
    /**获取基础条件查询语句
     * @param baseEntity
     * @return
     */
    public static Map getBaseSearchEXPGroup(BaseEntity baseEntity, BaseCondition baseCondition){
        return getBaseSearch(baseCondition, baseEntity != null, baseEntity.getId(), baseEntity.getIsDeleted());
    }

    private static Map getBaseSearch(BaseCondition baseCondition, boolean b, Long id, Boolean isDeleted) {
        Map map = new HashMap();
        SqlExpressionGroup sql = Cnd.exps("1", "=", "1");
        if (b) {
            sql.andEX(BaseEntity.Fields.id, "=", id );
            sql.andEX(BaseEntity.Fields.isDeleted, "=", isDeleted==null?false:isDeleted);
        }
        if (baseCondition != null) {
            sql.andEX(BaseEntity.Fields.createTime, ">", baseCondition.getStartTime());
            sql.andEX(BaseEntity.Fields.createTime, "<", baseCondition.getEndTime());
        }
        map.put("sql", sql);
        map.put("pager", getPager(baseCondition));
        return map;
    }

    public static Pager getPager(BaseCondition baseCondition) {
        Pager pager=null;
        if (baseCondition != null && baseCondition.getPageNo() > 0 && baseCondition.getPageSize() > 0) {
             pager = new Pager(baseCondition.getPageNo(), baseCondition.getPageSize());
        }
        return pager;
    }

    /**更新实体，不更新createTime和isDeleted
     * @param clazz
     * @param entity
     * @param dao
     */
    public static void updateData(Class clazz, BaseEntity entity, Dao dao, String... fields){
        entity.setUpdateTime(new Date());
        FieldFilter filter =FieldFilter.create(clazz,"",joinFields(true, fields),false);
        Daos.ext(dao, filter).update(entity);
    }
    /**查询数据，自定义排序
     * @param cnd
     * @param orders
     * @return
     */
    public static Cnd addOrders(Cnd cnd,List<OrderCombo> orders) {
        if ( orders== null || orders.size() == 0) {
            orders=baseOrder;
        }
        for (OrderCombo order : orders) {
            cnd.orderBy(order.getField(), order.getOperator().getName());
        }
        return  cnd;
    }

    public static Cnd addOrders(Cnd cnd, BaseCondition baseCondition) {
        if (baseCondition==null) {
            return addOrders(cnd, baseOrder);
        }
        return addOrders(cnd, baseCondition.getOrders());
    }
    /**字段字符串正则
     * @param fields
     * @return
     */
    public static String joinFields(String... fields) {
        if (fields.length==0) {
            return "";
        }
        int i=0;
        StringBuilder sb = new StringBuilder();
        for (; i < fields.length-1; i++) {
            sb.append(fields[i]).append("|");
        }
        sb.append(fields[i]);
        return sb.toString();
    }
    public  static  String joinFields(boolean needBase,String... fields) {
        String joinedStr=joinFields(fields);
        if (!needBase) {
            return joinedStr;
        }else{
            if (joinedStr.length() == 0) {
                return baseFields;
            }else{
                return joinedStr + "|" + baseFields;
            }
        }
    }
    public static Cnd baseCnd() {
        return Cnd.where(BaseEntity.Fields.isDeleted, "=", false);
    }
    public static Result getDeleteResult(List entities) {
        Result result = new Result();
        for (Object entity : entities) {
            if (entity instanceof BaseEntity) {
                ((BaseEntity) entity).setIsDeleted(true);
            }
        }
        Utils.getIocBean(Dao.class).update(entities);
        return result.setOk();
    }
}
