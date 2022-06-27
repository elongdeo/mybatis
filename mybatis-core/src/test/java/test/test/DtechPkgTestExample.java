package test.test;

import com.elong.deo.mybatis.core.BaseExample;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DtechPkgTestExample extends BaseExample {
    /**
     * dtech_pkg
     */
    protected String orderByClause;

    /**
     * dtech_pkg
     */
    protected boolean distinct;

    /**
     * dtech_pkg
     */
    protected List<Criteria> oredCriteria;

    public DtechPkgTestExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * dtech_pkg null
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria)this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria)this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria)this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria)this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria)this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria)this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria)this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria)this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria)this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria)this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria)this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria)this;
        }

        public Criteria andGmtCreateIsNull() {
            addCriterion("gmt_create is null");
            return (Criteria)this;
        }

        public Criteria andGmtCreateIsNotNull() {
            addCriterion("gmt_create is not null");
            return (Criteria)this;
        }

        public Criteria andGmtCreateEqualTo(Date value) {
            addCriterion("gmt_create =", value, "gmtCreate");
            return (Criteria)this;
        }

        public Criteria andGmtCreateNotEqualTo(Date value) {
            addCriterion("gmt_create <>", value, "gmtCreate");
            return (Criteria)this;
        }

        public Criteria andGmtCreateGreaterThan(Date value) {
            addCriterion("gmt_create >", value, "gmtCreate");
            return (Criteria)this;
        }

        public Criteria andGmtCreateGreaterThanOrEqualTo(Date value) {
            addCriterion("gmt_create >=", value, "gmtCreate");
            return (Criteria)this;
        }

        public Criteria andGmtCreateLessThan(Date value) {
            addCriterion("gmt_create <", value, "gmtCreate");
            return (Criteria)this;
        }

        public Criteria andGmtCreateLessThanOrEqualTo(Date value) {
            addCriterion("gmt_create <=", value, "gmtCreate");
            return (Criteria)this;
        }

        public Criteria andGmtCreateIn(List<Date> values) {
            addCriterion("gmt_create in", values, "gmtCreate");
            return (Criteria)this;
        }

        public Criteria andGmtCreateNotIn(List<Date> values) {
            addCriterion("gmt_create not in", values, "gmtCreate");
            return (Criteria)this;
        }

        public Criteria andGmtCreateBetween(Date value1, Date value2) {
            addCriterion("gmt_create between", value1, value2, "gmtCreate");
            return (Criteria)this;
        }

        public Criteria andGmtCreateNotBetween(Date value1, Date value2) {
            addCriterion("gmt_create not between", value1, value2, "gmtCreate");
            return (Criteria)this;
        }

        public Criteria andCreatorIsNull() {
            addCriterion("creator is null");
            return (Criteria)this;
        }

        public Criteria andCreatorIsNotNull() {
            addCriterion("creator is not null");
            return (Criteria)this;
        }

        public Criteria andCreatorEqualTo(String value) {
            addCriterion("creator =", value, "creator");
            return (Criteria)this;
        }

        public Criteria andCreatorNotEqualTo(String value) {
            addCriterion("creator <>", value, "creator");
            return (Criteria)this;
        }

        public Criteria andCreatorGreaterThan(String value) {
            addCriterion("creator >", value, "creator");
            return (Criteria)this;
        }

        public Criteria andCreatorGreaterThanOrEqualTo(String value) {
            addCriterion("creator >=", value, "creator");
            return (Criteria)this;
        }

        public Criteria andCreatorLessThan(String value) {
            addCriterion("creator <", value, "creator");
            return (Criteria)this;
        }

        public Criteria andCreatorLessThanOrEqualTo(String value) {
            addCriterion("creator <=", value, "creator");
            return (Criteria)this;
        }

        public Criteria andCreatorLike(String value) {
            addCriterion("creator like", value, "creator");
            return (Criteria)this;
        }

        public Criteria andCreatorNotLike(String value) {
            addCriterion("creator not like", value, "creator");
            return (Criteria)this;
        }

        public Criteria andCreatorIn(List<String> values) {
            addCriterion("creator in", values, "creator");
            return (Criteria)this;
        }

        public Criteria andCreatorNotIn(List<String> values) {
            addCriterion("creator not in", values, "creator");
            return (Criteria)this;
        }

        public Criteria andCreatorBetween(String value1, String value2) {
            addCriterion("creator between", value1, value2, "creator");
            return (Criteria)this;
        }

        public Criteria andCreatorNotBetween(String value1, String value2) {
            addCriterion("creator not between", value1, value2, "creator");
            return (Criteria)this;
        }

        public Criteria andGmtModifiedIsNull() {
            addCriterion("gmt_modified is null");
            return (Criteria)this;
        }

        public Criteria andGmtModifiedIsNotNull() {
            addCriterion("gmt_modified is not null");
            return (Criteria)this;
        }

        public Criteria andGmtModifiedEqualTo(Date value) {
            addCriterion("gmt_modified =", value, "gmtModified");
            return (Criteria)this;
        }

        public Criteria andGmtModifiedNotEqualTo(Date value) {
            addCriterion("gmt_modified <>", value, "gmtModified");
            return (Criteria)this;
        }

        public Criteria andGmtModifiedGreaterThan(Date value) {
            addCriterion("gmt_modified >", value, "gmtModified");
            return (Criteria)this;
        }

        public Criteria andGmtModifiedGreaterThanOrEqualTo(Date value) {
            addCriterion("gmt_modified >=", value, "gmtModified");
            return (Criteria)this;
        }

        public Criteria andGmtModifiedLessThan(Date value) {
            addCriterion("gmt_modified <", value, "gmtModified");
            return (Criteria)this;
        }

        public Criteria andGmtModifiedLessThanOrEqualTo(Date value) {
            addCriterion("gmt_modified <=", value, "gmtModified");
            return (Criteria)this;
        }

        public Criteria andGmtModifiedIn(List<Date> values) {
            addCriterion("gmt_modified in", values, "gmtModified");
            return (Criteria)this;
        }

        public Criteria andGmtModifiedNotIn(List<Date> values) {
            addCriterion("gmt_modified not in", values, "gmtModified");
            return (Criteria)this;
        }

        public Criteria andGmtModifiedBetween(Date value1, Date value2) {
            addCriterion("gmt_modified between", value1, value2, "gmtModified");
            return (Criteria)this;
        }

        public Criteria andGmtModifiedNotBetween(Date value1, Date value2) {
            addCriterion("gmt_modified not between", value1, value2, "gmtModified");
            return (Criteria)this;
        }

        public Criteria andModifierIsNull() {
            addCriterion("modifier is null");
            return (Criteria)this;
        }

        public Criteria andModifierIsNotNull() {
            addCriterion("modifier is not null");
            return (Criteria)this;
        }

        public Criteria andModifierEqualTo(String value) {
            addCriterion("modifier =", value, "modifier");
            return (Criteria)this;
        }

        public Criteria andModifierNotEqualTo(String value) {
            addCriterion("modifier <>", value, "modifier");
            return (Criteria)this;
        }

        public Criteria andModifierGreaterThan(String value) {
            addCriterion("modifier >", value, "modifier");
            return (Criteria)this;
        }

        public Criteria andModifierGreaterThanOrEqualTo(String value) {
            addCriterion("modifier >=", value, "modifier");
            return (Criteria)this;
        }

        public Criteria andModifierLessThan(String value) {
            addCriterion("modifier <", value, "modifier");
            return (Criteria)this;
        }

        public Criteria andModifierLessThanOrEqualTo(String value) {
            addCriterion("modifier <=", value, "modifier");
            return (Criteria)this;
        }

        public Criteria andModifierLike(String value) {
            addCriterion("modifier like", value, "modifier");
            return (Criteria)this;
        }

        public Criteria andModifierNotLike(String value) {
            addCriterion("modifier not like", value, "modifier");
            return (Criteria)this;
        }

        public Criteria andModifierIn(List<String> values) {
            addCriterion("modifier in", values, "modifier");
            return (Criteria)this;
        }

        public Criteria andModifierNotIn(List<String> values) {
            addCriterion("modifier not in", values, "modifier");
            return (Criteria)this;
        }

        public Criteria andModifierBetween(String value1, String value2) {
            addCriterion("modifier between", value1, value2, "modifier");
            return (Criteria)this;
        }

        public Criteria andModifierNotBetween(String value1, String value2) {
            addCriterion("modifier not between", value1, value2, "modifier");
            return (Criteria)this;
        }

        public Criteria andDeletedIsNull() {
            addCriterion("is_deleted is null");
            return (Criteria)this;
        }

        public Criteria andDeletedIsNotNull() {
            addCriterion("is_deleted is not null");
            return (Criteria)this;
        }

        public Criteria andDeletedEqualTo(Boolean value) {
            addCriterion("is_deleted =", value, "deleted");
            return (Criteria)this;
        }

        public Criteria andDeletedNotEqualTo(Boolean value) {
            addCriterion("is_deleted <>", value, "deleted");
            return (Criteria)this;
        }

        public Criteria andDeletedGreaterThan(Boolean value) {
            addCriterion("is_deleted >", value, "deleted");
            return (Criteria)this;
        }

        public Criteria andDeletedGreaterThanOrEqualTo(Boolean value) {
            addCriterion("is_deleted >=", value, "deleted");
            return (Criteria)this;
        }

        public Criteria andDeletedLessThan(Boolean value) {
            addCriterion("is_deleted <", value, "deleted");
            return (Criteria)this;
        }

        public Criteria andDeletedLessThanOrEqualTo(Boolean value) {
            addCriterion("is_deleted <=", value, "deleted");
            return (Criteria)this;
        }

        public Criteria andDeletedIn(List<Boolean> values) {
            addCriterion("is_deleted in", values, "deleted");
            return (Criteria)this;
        }

        public Criteria andDeletedNotIn(List<Boolean> values) {
            addCriterion("is_deleted not in", values, "deleted");
            return (Criteria)this;
        }

        public Criteria andDeletedBetween(Boolean value1, Boolean value2) {
            addCriterion("is_deleted between", value1, value2, "deleted");
            return (Criteria)this;
        }

        public Criteria andDeletedNotBetween(Boolean value1, Boolean value2) {
            addCriterion("is_deleted not between", value1, value2, "deleted");
            return (Criteria)this;
        }

        public Criteria andStyleIdIsNull() {
            addCriterion("style_id is null");
            return (Criteria)this;
        }

        public Criteria andStyleIdIsNotNull() {
            addCriterion("style_id is not null");
            return (Criteria)this;
        }

        public Criteria andStyleIdEqualTo(Long value) {
            addCriterion("style_id =", value, "styleId");
            return (Criteria)this;
        }

        public Criteria andStyleIdNotEqualTo(Long value) {
            addCriterion("style_id <>", value, "styleId");
            return (Criteria)this;
        }

        public Criteria andStyleIdGreaterThan(Long value) {
            addCriterion("style_id >", value, "styleId");
            return (Criteria)this;
        }

        public Criteria andStyleIdGreaterThanOrEqualTo(Long value) {
            addCriterion("style_id >=", value, "styleId");
            return (Criteria)this;
        }

        public Criteria andStyleIdLessThan(Long value) {
            addCriterion("style_id <", value, "styleId");
            return (Criteria)this;
        }

        public Criteria andStyleIdLessThanOrEqualTo(Long value) {
            addCriterion("style_id <=", value, "styleId");
            return (Criteria)this;
        }

        public Criteria andStyleIdIn(List<Long> values) {
            addCriterion("style_id in", values, "styleId");
            return (Criteria)this;
        }

        public Criteria andStyleIdNotIn(List<Long> values) {
            addCriterion("style_id not in", values, "styleId");
            return (Criteria)this;
        }

        public Criteria andStyleIdBetween(Long value1, Long value2) {
            addCriterion("style_id between", value1, value2, "styleId");
            return (Criteria)this;
        }

        public Criteria andStyleIdNotBetween(Long value1, Long value2) {
            addCriterion("style_id not between", value1, value2, "styleId");
            return (Criteria)this;
        }

        public Criteria andOrderIdIsNull() {
            addCriterion("order_id is null");
            return (Criteria)this;
        }

        public Criteria andOrderIdIsNotNull() {
            addCriterion("order_id is not null");
            return (Criteria)this;
        }

        public Criteria andOrderIdEqualTo(Long value) {
            addCriterion("order_id =", value, "orderId");
            return (Criteria)this;
        }

        public Criteria andOrderIdNotEqualTo(Long value) {
            addCriterion("order_id <>", value, "orderId");
            return (Criteria)this;
        }

        public Criteria andOrderIdGreaterThan(Long value) {
            addCriterion("order_id >", value, "orderId");
            return (Criteria)this;
        }

        public Criteria andOrderIdGreaterThanOrEqualTo(Long value) {
            addCriterion("order_id >=", value, "orderId");
            return (Criteria)this;
        }

        public Criteria andOrderIdLessThan(Long value) {
            addCriterion("order_id <", value, "orderId");
            return (Criteria)this;
        }

        public Criteria andOrderIdLessThanOrEqualTo(Long value) {
            addCriterion("order_id <=", value, "orderId");
            return (Criteria)this;
        }

        public Criteria andOrderIdIn(List<Long> values) {
            addCriterion("order_id in", values, "orderId");
            return (Criteria)this;
        }

        public Criteria andOrderIdNotIn(List<Long> values) {
            addCriterion("order_id not in", values, "orderId");
            return (Criteria)this;
        }

        public Criteria andOrderIdBetween(Long value1, Long value2) {
            addCriterion("order_id between", value1, value2, "orderId");
            return (Criteria)this;
        }

        public Criteria andOrderIdNotBetween(Long value1, Long value2) {
            addCriterion("order_id not between", value1, value2, "orderId");
            return (Criteria)this;
        }

        public Criteria andStyleColorIdIsNull() {
            addCriterion("style_color_id is null");
            return (Criteria)this;
        }

        public Criteria andStyleColorIdIsNotNull() {
            addCriterion("style_color_id is not null");
            return (Criteria)this;
        }

        public Criteria andStyleColorIdEqualTo(Long value) {
            addCriterion("style_color_id =", value, "styleColorId");
            return (Criteria)this;
        }

        public Criteria andStyleColorIdNotEqualTo(Long value) {
            addCriterion("style_color_id <>", value, "styleColorId");
            return (Criteria)this;
        }

        public Criteria andStyleColorIdGreaterThan(Long value) {
            addCriterion("style_color_id >", value, "styleColorId");
            return (Criteria)this;
        }

        public Criteria andStyleColorIdGreaterThanOrEqualTo(Long value) {
            addCriterion("style_color_id >=", value, "styleColorId");
            return (Criteria)this;
        }

        public Criteria andStyleColorIdLessThan(Long value) {
            addCriterion("style_color_id <", value, "styleColorId");
            return (Criteria)this;
        }

        public Criteria andStyleColorIdLessThanOrEqualTo(Long value) {
            addCriterion("style_color_id <=", value, "styleColorId");
            return (Criteria)this;
        }

        public Criteria andStyleColorIdIn(List<Long> values) {
            addCriterion("style_color_id in", values, "styleColorId");
            return (Criteria)this;
        }

        public Criteria andStyleColorIdNotIn(List<Long> values) {
            addCriterion("style_color_id not in", values, "styleColorId");
            return (Criteria)this;
        }

        public Criteria andStyleColorIdBetween(Long value1, Long value2) {
            addCriterion("style_color_id between", value1, value2, "styleColorId");
            return (Criteria)this;
        }

        public Criteria andStyleColorIdNotBetween(Long value1, Long value2) {
            addCriterion("style_color_id not between", value1, value2, "styleColorId");
            return (Criteria)this;
        }

        public Criteria andColorNameIsNull() {
            addCriterion("color_name is null");
            return (Criteria)this;
        }

        public Criteria andColorNameIsNotNull() {
            addCriterion("color_name is not null");
            return (Criteria)this;
        }

        public Criteria andColorNameEqualTo(String value) {
            addCriterion("color_name =", value, "colorName");
            return (Criteria)this;
        }

        public Criteria andColorNameNotEqualTo(String value) {
            addCriterion("color_name <>", value, "colorName");
            return (Criteria)this;
        }

        public Criteria andColorNameGreaterThan(String value) {
            addCriterion("color_name >", value, "colorName");
            return (Criteria)this;
        }

        public Criteria andColorNameGreaterThanOrEqualTo(String value) {
            addCriterion("color_name >=", value, "colorName");
            return (Criteria)this;
        }

        public Criteria andColorNameLessThan(String value) {
            addCriterion("color_name <", value, "colorName");
            return (Criteria)this;
        }

        public Criteria andColorNameLessThanOrEqualTo(String value) {
            addCriterion("color_name <=", value, "colorName");
            return (Criteria)this;
        }

        public Criteria andColorNameLike(String value) {
            addCriterion("color_name like", value, "colorName");
            return (Criteria)this;
        }

        public Criteria andColorNameNotLike(String value) {
            addCriterion("color_name not like", value, "colorName");
            return (Criteria)this;
        }

        public Criteria andColorNameIn(List<String> values) {
            addCriterion("color_name in", values, "colorName");
            return (Criteria)this;
        }

        public Criteria andColorNameNotIn(List<String> values) {
            addCriterion("color_name not in", values, "colorName");
            return (Criteria)this;
        }

        public Criteria andColorNameBetween(String value1, String value2) {
            addCriterion("color_name between", value1, value2, "colorName");
            return (Criteria)this;
        }

        public Criteria andColorNameNotBetween(String value1, String value2) {
            addCriterion("color_name not between", value1, value2, "colorName");
            return (Criteria)this;
        }

        public Criteria andTypeIsNull() {
            addCriterion("type is null");
            return (Criteria)this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("type is not null");
            return (Criteria)this;
        }

        public Criteria andTypeEqualTo(String value) {
            addCriterion("type =", value, "type");
            return (Criteria)this;
        }

        public Criteria andTypeNotEqualTo(String value) {
            addCriterion("type <>", value, "type");
            return (Criteria)this;
        }

        public Criteria andTypeGreaterThan(String value) {
            addCriterion("type >", value, "type");
            return (Criteria)this;
        }

        public Criteria andTypeGreaterThanOrEqualTo(String value) {
            addCriterion("type >=", value, "type");
            return (Criteria)this;
        }

        public Criteria andTypeLessThan(String value) {
            addCriterion("type <", value, "type");
            return (Criteria)this;
        }

        public Criteria andTypeLessThanOrEqualTo(String value) {
            addCriterion("type <=", value, "type");
            return (Criteria)this;
        }

        public Criteria andTypeLike(String value) {
            addCriterion("type like", value, "type");
            return (Criteria)this;
        }

        public Criteria andTypeNotLike(String value) {
            addCriterion("type not like", value, "type");
            return (Criteria)this;
        }

        public Criteria andTypeIn(List<String> values) {
            addCriterion("type in", values, "type");
            return (Criteria)this;
        }

        public Criteria andTypeNotIn(List<String> values) {
            addCriterion("type not in", values, "type");
            return (Criteria)this;
        }

        public Criteria andTypeBetween(String value1, String value2) {
            addCriterion("type between", value1, value2, "type");
            return (Criteria)this;
        }

        public Criteria andTypeNotBetween(String value1, String value2) {
            addCriterion("type not between", value1, value2, "type");
            return (Criteria)this;
        }

        public Criteria andVersionIsNull() {
            addCriterion("version is null");
            return (Criteria)this;
        }

        public Criteria andVersionIsNotNull() {
            addCriterion("version is not null");
            return (Criteria)this;
        }

        public Criteria andVersionEqualTo(String value) {
            addCriterion("version =", value, "version");
            return (Criteria)this;
        }

        public Criteria andVersionNotEqualTo(String value) {
            addCriterion("version <>", value, "version");
            return (Criteria)this;
        }

        public Criteria andVersionGreaterThan(String value) {
            addCriterion("version >", value, "version");
            return (Criteria)this;
        }

        public Criteria andVersionGreaterThanOrEqualTo(String value) {
            addCriterion("version >=", value, "version");
            return (Criteria)this;
        }

        public Criteria andVersionLessThan(String value) {
            addCriterion("version <", value, "version");
            return (Criteria)this;
        }

        public Criteria andVersionLessThanOrEqualTo(String value) {
            addCriterion("version <=", value, "version");
            return (Criteria)this;
        }

        public Criteria andVersionLike(String value) {
            addCriterion("version like", value, "version");
            return (Criteria)this;
        }

        public Criteria andVersionNotLike(String value) {
            addCriterion("version not like", value, "version");
            return (Criteria)this;
        }

        public Criteria andVersionIn(List<String> values) {
            addCriterion("version in", values, "version");
            return (Criteria)this;
        }

        public Criteria andVersionNotIn(List<String> values) {
            addCriterion("version not in", values, "version");
            return (Criteria)this;
        }

        public Criteria andVersionBetween(String value1, String value2) {
            addCriterion("version between", value1, value2, "version");
            return (Criteria)this;
        }

        public Criteria andVersionNotBetween(String value1, String value2) {
            addCriterion("version not between", value1, value2, "version");
            return (Criteria)this;
        }

        public Criteria andTenantCodeIsNull() {
            addCriterion("tenant_code is null");
            return (Criteria)this;
        }

        public Criteria andTenantCodeIsNotNull() {
            addCriterion("tenant_code is not null");
            return (Criteria)this;
        }

        public Criteria andTenantCodeEqualTo(String value) {
            addCriterion("tenant_code =", value, "tenantCode");
            return (Criteria)this;
        }

        public Criteria andTenantCodeNotEqualTo(String value) {
            addCriterion("tenant_code <>", value, "tenantCode");
            return (Criteria)this;
        }

        public Criteria andTenantCodeGreaterThan(String value) {
            addCriterion("tenant_code >", value, "tenantCode");
            return (Criteria)this;
        }

        public Criteria andTenantCodeGreaterThanOrEqualTo(String value) {
            addCriterion("tenant_code >=", value, "tenantCode");
            return (Criteria)this;
        }

        public Criteria andTenantCodeLessThan(String value) {
            addCriterion("tenant_code <", value, "tenantCode");
            return (Criteria)this;
        }

        public Criteria andTenantCodeLessThanOrEqualTo(String value) {
            addCriterion("tenant_code <=", value, "tenantCode");
            return (Criteria)this;
        }

        public Criteria andTenantCodeLike(String value) {
            addCriterion("tenant_code like", value, "tenantCode");
            return (Criteria)this;
        }

        public Criteria andTenantCodeNotLike(String value) {
            addCriterion("tenant_code not like", value, "tenantCode");
            return (Criteria)this;
        }

        public Criteria andTenantCodeIn(List<String> values) {
            addCriterion("tenant_code in", values, "tenantCode");
            return (Criteria)this;
        }

        public Criteria andTenantCodeNotIn(List<String> values) {
            addCriterion("tenant_code not in", values, "tenantCode");
            return (Criteria)this;
        }

        public Criteria andTenantCodeBetween(String value1, String value2) {
            addCriterion("tenant_code between", value1, value2, "tenantCode");
            return (Criteria)this;
        }

        public Criteria andTenantCodeNotBetween(String value1, String value2) {
            addCriterion("tenant_code not between", value1, value2, "tenantCode");
            return (Criteria)this;
        }

        public Criteria andDefaultVersionIsNull() {
            addCriterion("is_default_version is null");
            return (Criteria)this;
        }

        public Criteria andDefaultVersionIsNotNull() {
            addCriterion("is_default_version is not null");
            return (Criteria)this;
        }

        public Criteria andDefaultVersionEqualTo(Boolean value) {
            addCriterion("is_default_version =", value, "defaultVersion");
            return (Criteria)this;
        }

        public Criteria andDefaultVersionNotEqualTo(Boolean value) {
            addCriterion("is_default_version <>", value, "defaultVersion");
            return (Criteria)this;
        }

        public Criteria andDefaultVersionGreaterThan(Boolean value) {
            addCriterion("is_default_version >", value, "defaultVersion");
            return (Criteria)this;
        }

        public Criteria andDefaultVersionGreaterThanOrEqualTo(Boolean value) {
            addCriterion("is_default_version >=", value, "defaultVersion");
            return (Criteria)this;
        }

        public Criteria andDefaultVersionLessThan(Boolean value) {
            addCriterion("is_default_version <", value, "defaultVersion");
            return (Criteria)this;
        }

        public Criteria andDefaultVersionLessThanOrEqualTo(Boolean value) {
            addCriterion("is_default_version <=", value, "defaultVersion");
            return (Criteria)this;
        }

        public Criteria andDefaultVersionIn(List<Boolean> values) {
            addCriterion("is_default_version in", values, "defaultVersion");
            return (Criteria)this;
        }

        public Criteria andDefaultVersionNotIn(List<Boolean> values) {
            addCriterion("is_default_version not in", values, "defaultVersion");
            return (Criteria)this;
        }

        public Criteria andDefaultVersionBetween(Boolean value1, Boolean value2) {
            addCriterion("is_default_version between", value1, value2, "defaultVersion");
            return (Criteria)this;
        }

        public Criteria andDefaultVersionNotBetween(Boolean value1, Boolean value2) {
            addCriterion("is_default_version not between", value1, value2, "defaultVersion");
            return (Criteria)this;
        }

        public Criteria andProcessAnalysisSourceFileIdIsNull() {
            addCriterion("process_analysis_source_file_id is null");
            return (Criteria)this;
        }

        public Criteria andProcessAnalysisSourceFileIdIsNotNull() {
            addCriterion("process_analysis_source_file_id is not null");
            return (Criteria)this;
        }

        public Criteria andProcessAnalysisSourceFileIdEqualTo(Long value) {
            addCriterion("process_analysis_source_file_id =", value, "processAnalysisSourceFileId");
            return (Criteria)this;
        }

        public Criteria andProcessAnalysisSourceFileIdNotEqualTo(Long value) {
            addCriterion("process_analysis_source_file_id <>", value, "processAnalysisSourceFileId");
            return (Criteria)this;
        }

        public Criteria andProcessAnalysisSourceFileIdGreaterThan(Long value) {
            addCriterion("process_analysis_source_file_id >", value, "processAnalysisSourceFileId");
            return (Criteria)this;
        }

        public Criteria andProcessAnalysisSourceFileIdGreaterThanOrEqualTo(Long value) {
            addCriterion("process_analysis_source_file_id >=", value, "processAnalysisSourceFileId");
            return (Criteria)this;
        }

        public Criteria andProcessAnalysisSourceFileIdLessThan(Long value) {
            addCriterion("process_analysis_source_file_id <", value, "processAnalysisSourceFileId");
            return (Criteria)this;
        }

        public Criteria andProcessAnalysisSourceFileIdLessThanOrEqualTo(Long value) {
            addCriterion("process_analysis_source_file_id <=", value, "processAnalysisSourceFileId");
            return (Criteria)this;
        }

        public Criteria andProcessAnalysisSourceFileIdIn(List<Long> values) {
            addCriterion("process_analysis_source_file_id in", values, "processAnalysisSourceFileId");
            return (Criteria)this;
        }

        public Criteria andProcessAnalysisSourceFileIdNotIn(List<Long> values) {
            addCriterion("process_analysis_source_file_id not in", values, "processAnalysisSourceFileId");
            return (Criteria)this;
        }

        public Criteria andProcessAnalysisSourceFileIdBetween(Long value1, Long value2) {
            addCriterion("process_analysis_source_file_id between", value1, value2, "processAnalysisSourceFileId");
            return (Criteria)this;
        }

        public Criteria andProcessAnalysisSourceFileIdNotBetween(Long value1, Long value2) {
            addCriterion("process_analysis_source_file_id not between", value1, value2, "processAnalysisSourceFileId");
            return (Criteria)this;
        }

        public Criteria andSampleImageIdsIsNull() {
            addCriterion("sample_image_ids is null");
            return (Criteria)this;
        }

        public Criteria andSampleImageIdsIsNotNull() {
            addCriterion("sample_image_ids is not null");
            return (Criteria)this;
        }

        public Criteria andSampleImageIdsEqualTo(String value) {
            addCriterion("sample_image_ids =", value, "sampleImageIds");
            return (Criteria)this;
        }

        public Criteria andSampleImageIdsNotEqualTo(String value) {
            addCriterion("sample_image_ids <>", value, "sampleImageIds");
            return (Criteria)this;
        }

        public Criteria andSampleImageIdsGreaterThan(String value) {
            addCriterion("sample_image_ids >", value, "sampleImageIds");
            return (Criteria)this;
        }

        public Criteria andSampleImageIdsGreaterThanOrEqualTo(String value) {
            addCriterion("sample_image_ids >=", value, "sampleImageIds");
            return (Criteria)this;
        }

        public Criteria andSampleImageIdsLessThan(String value) {
            addCriterion("sample_image_ids <", value, "sampleImageIds");
            return (Criteria)this;
        }

        public Criteria andSampleImageIdsLessThanOrEqualTo(String value) {
            addCriterion("sample_image_ids <=", value, "sampleImageIds");
            return (Criteria)this;
        }

        public Criteria andSampleImageIdsLike(String value) {
            addCriterion("sample_image_ids like", value, "sampleImageIds");
            return (Criteria)this;
        }

        public Criteria andSampleImageIdsNotLike(String value) {
            addCriterion("sample_image_ids not like", value, "sampleImageIds");
            return (Criteria)this;
        }

        public Criteria andSampleImageIdsIn(List<String> values) {
            addCriterion("sample_image_ids in", values, "sampleImageIds");
            return (Criteria)this;
        }

        public Criteria andSampleImageIdsNotIn(List<String> values) {
            addCriterion("sample_image_ids not in", values, "sampleImageIds");
            return (Criteria)this;
        }

        public Criteria andSampleImageIdsBetween(String value1, String value2) {
            addCriterion("sample_image_ids between", value1, value2, "sampleImageIds");
            return (Criteria)this;
        }

        public Criteria andSampleImageIdsNotBetween(String value1, String value2) {
            addCriterion("sample_image_ids not between", value1, value2, "sampleImageIds");
            return (Criteria)this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria)this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria)this;
        }

        public Criteria andStatusEqualTo(String value) {
            addCriterion("status =", value, "status");
            return (Criteria)this;
        }

        public Criteria andStatusNotEqualTo(String value) {
            addCriterion("status <>", value, "status");
            return (Criteria)this;
        }

        public Criteria andStatusGreaterThan(String value) {
            addCriterion("status >", value, "status");
            return (Criteria)this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(String value) {
            addCriterion("status >=", value, "status");
            return (Criteria)this;
        }

        public Criteria andStatusLessThan(String value) {
            addCriterion("status <", value, "status");
            return (Criteria)this;
        }

        public Criteria andStatusLessThanOrEqualTo(String value) {
            addCriterion("status <=", value, "status");
            return (Criteria)this;
        }

        public Criteria andStatusLike(String value) {
            addCriterion("status like", value, "status");
            return (Criteria)this;
        }

        public Criteria andStatusNotLike(String value) {
            addCriterion("status not like", value, "status");
            return (Criteria)this;
        }

        public Criteria andStatusIn(List<String> values) {
            addCriterion("status in", values, "status");
            return (Criteria)this;
        }

        public Criteria andStatusNotIn(List<String> values) {
            addCriterion("status not in", values, "status");
            return (Criteria)this;
        }

        public Criteria andStatusBetween(String value1, String value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria)this;
        }

        public Criteria andStatusNotBetween(String value1, String value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria)this;
        }

        public Criteria andDesignSourceFileIdIsNull() {
            addCriterion("design_source_file_id is null");
            return (Criteria)this;
        }

        public Criteria andDesignSourceFileIdIsNotNull() {
            addCriterion("design_source_file_id is not null");
            return (Criteria)this;
        }

        public Criteria andDesignSourceFileIdEqualTo(String value) {
            addCriterion("design_source_file_id =", value, "designSourceFileId");
            return (Criteria)this;
        }

        public Criteria andDesignSourceFileIdNotEqualTo(String value) {
            addCriterion("design_source_file_id <>", value, "designSourceFileId");
            return (Criteria)this;
        }

        public Criteria andDesignSourceFileIdGreaterThan(String value) {
            addCriterion("design_source_file_id >", value, "designSourceFileId");
            return (Criteria)this;
        }

        public Criteria andDesignSourceFileIdGreaterThanOrEqualTo(String value) {
            addCriterion("design_source_file_id >=", value, "designSourceFileId");
            return (Criteria)this;
        }

        public Criteria andDesignSourceFileIdLessThan(String value) {
            addCriterion("design_source_file_id <", value, "designSourceFileId");
            return (Criteria)this;
        }

        public Criteria andDesignSourceFileIdLessThanOrEqualTo(String value) {
            addCriterion("design_source_file_id <=", value, "designSourceFileId");
            return (Criteria)this;
        }

        public Criteria andDesignSourceFileIdLike(String value) {
            addCriterion("design_source_file_id like", value, "designSourceFileId");
            return (Criteria)this;
        }

        public Criteria andDesignSourceFileIdNotLike(String value) {
            addCriterion("design_source_file_id not like", value, "designSourceFileId");
            return (Criteria)this;
        }

        public Criteria andDesignSourceFileIdIn(List<String> values) {
            addCriterion("design_source_file_id in", values, "designSourceFileId");
            return (Criteria)this;
        }

        public Criteria andDesignSourceFileIdNotIn(List<String> values) {
            addCriterion("design_source_file_id not in", values, "designSourceFileId");
            return (Criteria)this;
        }

        public Criteria andDesignSourceFileIdBetween(String value1, String value2) {
            addCriterion("design_source_file_id between", value1, value2, "designSourceFileId");
            return (Criteria)this;
        }

        public Criteria andDesignSourceFileIdNotBetween(String value1, String value2) {
            addCriterion("design_source_file_id not between", value1, value2, "designSourceFileId");
            return (Criteria)this;
        }

        public Criteria andCadVendorIsNull() {
            addCriterion("cad_vendor is null");
            return (Criteria)this;
        }

        public Criteria andCadVendorIsNotNull() {
            addCriterion("cad_vendor is not null");
            return (Criteria)this;
        }

        public Criteria andCadVendorEqualTo(String value) {
            addCriterion("cad_vendor =", value, "cadVendor");
            return (Criteria)this;
        }

        public Criteria andCadVendorNotEqualTo(String value) {
            addCriterion("cad_vendor <>", value, "cadVendor");
            return (Criteria)this;
        }

        public Criteria andCadVendorGreaterThan(String value) {
            addCriterion("cad_vendor >", value, "cadVendor");
            return (Criteria)this;
        }

        public Criteria andCadVendorGreaterThanOrEqualTo(String value) {
            addCriterion("cad_vendor >=", value, "cadVendor");
            return (Criteria)this;
        }

        public Criteria andCadVendorLessThan(String value) {
            addCriterion("cad_vendor <", value, "cadVendor");
            return (Criteria)this;
        }

        public Criteria andCadVendorLessThanOrEqualTo(String value) {
            addCriterion("cad_vendor <=", value, "cadVendor");
            return (Criteria)this;
        }

        public Criteria andCadVendorLike(String value) {
            addCriterion("cad_vendor like", value, "cadVendor");
            return (Criteria)this;
        }

        public Criteria andCadVendorNotLike(String value) {
            addCriterion("cad_vendor not like", value, "cadVendor");
            return (Criteria)this;
        }

        public Criteria andCadVendorIn(List<String> values) {
            addCriterion("cad_vendor in", values, "cadVendor");
            return (Criteria)this;
        }

        public Criteria andCadVendorNotIn(List<String> values) {
            addCriterion("cad_vendor not in", values, "cadVendor");
            return (Criteria)this;
        }

        public Criteria andCadVendorBetween(String value1, String value2) {
            addCriterion("cad_vendor between", value1, value2, "cadVendor");
            return (Criteria)this;
        }

        public Criteria andCadVendorNotBetween(String value1, String value2) {
            addCriterion("cad_vendor not between", value1, value2, "cadVendor");
            return (Criteria)this;
        }
    }

    /**
     * dtech_pkg
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * dtech_pkg null
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}