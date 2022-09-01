# 使用说明
[https://www.yuque.com/dingyinlong/mbg/usage]
# 功能列表
[https://www.yuque.com/dingyinlong/mbg/overview]
1. 开发规范相关（强制）
- a. 生成文件按规约命名
- b. is_xx转Boolean类型
- c. 生成的DO增加原表字段注释
- d. 逻辑删除/软删除
- e. OrderBy防sql注入
- f. DML语句优化字段
- g. 屏蔽全字段更新
2. 功能增强（默认）
- a. MapperExt
- b. 分页查询
- c. 支持强行指定字段更新为null
- d. 支持仅查询指定字段
- e. 支持字符串类型的正则查询
- f. 支持自定义条件查询
3. 功能扩展（可选）
- a. 支持生成对pojo类的Lombok注解
- b. 支持生成批量插入
- c. 支持配置使用主键生成策略(数据库自增,UUID)
- d. 分库分表片键支持
- e. 提供默认TypeHandler及基类
- f. 支持配置指定方法禁用一级缓存
- g. 支持自动生成Repo
- h. 支持忽略Criterion值为空
- i. 时间支持修改精度
- j. 大字段字符串转普通字符串
