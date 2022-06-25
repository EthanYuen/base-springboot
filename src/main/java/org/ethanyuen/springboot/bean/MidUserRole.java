package org.ethanyuen.springboot.bean;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;


@ApiModel("用户角色")
@Table
@Data
@FieldNameConstants
@TableIndexes({@Index( fields = {BaseMidEntity.Fields.leftId, BaseEntity.Fields.isDeleted}, unique = false)})
public class MidUserRole extends BaseMidEntity {
}
