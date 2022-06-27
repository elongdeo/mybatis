package test.test;

import com.elong.deo.mybatis.core.BaseDO;

public class DtechPkgTestDO extends BaseDO {

    /**
     * 款式ID
     */
    private Long styleId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 款式颜色ID
     */
    private Long styleColorId;

    /**
     * 颜色名称
     */
    private String colorName;

    /**
     * 类型：打样、大货
     */
    private String type;

    /**
     * 版本号
     */
    private String version;

    /**
     * 工厂板房的租户编码
     */
    private String tenantCode;

    /**
     * 是否默认版本
     */
    private Boolean defaultVersion;

    /**
     * 工艺分析原文件ID
     */
    private Long processAnalysisSourceFileId;

    /**
     * 样衣图片
     */
    private String sampleImageIds;

    /**
     * 工艺包的状态：NEW:新建,FINISHED:已完成
     */
    private String status;

    /**
     * 设计源文件ID(纸样、全码)
     */
    private String designSourceFileId;

    /**
     * CAD软件提供商
     */
    private String cadVendor;

    /**
     * 款式ID
     *
     * @return style_id 款式ID
     */
    public Long getStyleId() {
        return styleId;
    }

    /**
     * 款式ID
     *
     * @param styleId 款式ID
     */
    public void setStyleId(Long styleId) {
        this.styleId = styleId;
    }

    /**
     * 订单ID
     *
     * @return order_id 订单ID
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * 订单ID
     *
     * @param orderId 订单ID
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * 款式颜色ID
     *
     * @return style_color_id 款式颜色ID
     */
    public Long getStyleColorId() {
        return styleColorId;
    }

    /**
     * 款式颜色ID
     *
     * @param styleColorId 款式颜色ID
     */
    public void setStyleColorId(Long styleColorId) {
        this.styleColorId = styleColorId;
    }

    /**
     * 颜色名称
     *
     * @return color_name 颜色名称
     */
    public String getColorName() {
        return colorName;
    }

    /**
     * 颜色名称
     *
     * @param colorName 颜色名称
     */
    public void setColorName(String colorName) {
        this.colorName = colorName == null ? null : colorName.trim();
    }

    /**
     * 类型：打样、大货
     *
     * @return type 类型：打样、大货
     */
    public String getType() {
        return type;
    }

    /**
     * 类型：打样、大货
     *
     * @param type 类型：打样、大货
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    /**
     * 版本号
     *
     * @return version 版本号
     */
    public String getVersion() {
        return version;
    }

    /**
     * 版本号
     *
     * @param version 版本号
     */
    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    /**
     * 工厂板房的租户编码
     *
     * @return tenant_code 工厂板房的租户编码
     */
    public String getTenantCode() {
        return tenantCode;
    }

    /**
     * 工厂板房的租户编码
     *
     * @param tenantCode 工厂板房的租户编码
     */
    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode == null ? null : tenantCode.trim();
    }

    /**
     * 是否默认版本
     *
     * @return is_default_version 是否默认版本
     */
    public Boolean getDefaultVersion() {
        return defaultVersion;
    }

    /**
     * 是否默认版本
     *
     * @param defaultVersion 是否默认版本
     */
    public void setDefaultVersion(Boolean defaultVersion) {
        this.defaultVersion = defaultVersion;
    }

    /**
     * 工艺分析原文件ID
     *
     * @return process_analysis_source_file_id 工艺分析原文件ID
     */
    public Long getProcessAnalysisSourceFileId() {
        return processAnalysisSourceFileId;
    }

    /**
     * 工艺分析原文件ID
     *
     * @param processAnalysisSourceFileId 工艺分析原文件ID
     */
    public void setProcessAnalysisSourceFileId(Long processAnalysisSourceFileId) {
        this.processAnalysisSourceFileId = processAnalysisSourceFileId;
    }

    /**
     * 样衣图片
     *
     * @return sample_image_ids 样衣图片
     */
    public String getSampleImageIds() {
        return sampleImageIds;
    }

    /**
     * 样衣图片
     *
     * @param sampleImageIds 样衣图片
     */
    public void setSampleImageIds(String sampleImageIds) {
        this.sampleImageIds = sampleImageIds == null ? null : sampleImageIds.trim();
    }

    /**
     * 工艺包的状态：NEW:新建,FINISHED:已完成
     *
     * @return status 工艺包的状态：NEW:新建,FINISHED:已完成
     */
    public String getStatus() {
        return status;
    }

    /**
     * 工艺包的状态：NEW:新建,FINISHED:已完成
     *
     * @param status 工艺包的状态：NEW:新建,FINISHED:已完成
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * 设计源文件ID(纸样、全码)
     *
     * @return design_source_file_id 设计源文件ID(纸样、全码)
     */
    public String getDesignSourceFileId() {
        return designSourceFileId;
    }

    /**
     * 设计源文件ID(纸样、全码)
     *
     * @param designSourceFileId 设计源文件ID(纸样、全码)
     */
    public void setDesignSourceFileId(String designSourceFileId) {
        this.designSourceFileId = designSourceFileId == null ? null : designSourceFileId.trim();
    }

    /**
     * CAD软件提供商
     *
     * @return cad_vendor CAD软件提供商
     */
    public String getCadVendor() {
        return cadVendor;
    }

    /**
     * CAD软件提供商
     *
     * @param cadVendor CAD软件提供商
     */
    public void setCadVendor(String cadVendor) {
        this.cadVendor = cadVendor == null ? null : cadVendor.trim();
    }
}