# 使用说明
[https://www.yuque.com/dingyinlong/mbg/usage]
# 功能列表
[https://www.yuque.com/dingyinlong/mbg/overview]
1. 开发规范相关（强制）
- 生成文件按规约命名
- is_xx转Boolean类型
- 生成的DO增加原表字段注释
- 逻辑删除/软删除
- OrderBy防sql注入
- DML语句优化字段
- 屏蔽全字段更新
2. 功能增强（默认）
- MapperExt
- 分页查询
- 支持强行指定字段更新为null
- 支持仅查询指定字段
- 支持字符串类型的正则查询
- 支持自定义条件查询
3. 功能扩展（可选）
- 支持生成对pojo类的Lombok注解
- 支持生成批量插入
- 支持配置使用主键生成策略(数据库自增,UUID)
- 分库分表片键支持
- 提供默认TypeHandler及基类
- 支持配置指定方法禁用一级缓存
- 支持自动生成Repo
- 支持忽略Criterion值为空
- 时间支持修改精度
- 大字段字符串转普通字符串
