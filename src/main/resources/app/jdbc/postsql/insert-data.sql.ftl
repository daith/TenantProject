INSERT INTO public.tenant2
(
<#list columnNameList as columnName>
  "${columnName.name}" <#if columnName_has_next>,</#if>
</#list>
)
VALUES
<#list recordList as record>
(
  <#list columnNameList as columnName>
    <#assign  key=columnName.name/>
    <#if columnName.dataType == "BOOL">
      ${record[key]!columnName.defaultValue} <#if columnName_has_next>,</#if>
    <#elseif columnEntity.dataType == "INT">
      ${record[key]!columnName.defaultValue} <#if columnName_has_next>,</#if>
    <#elseif columnEntity.dataType == "BIGINT">
      ${record[key]!columnName.defaultValue} <#if columnName_has_next>,</#if>
    <#elseif columnEntity.dataType == "FLOAT">
      ${record[key]!columnName.defaultValue} <#if columnName_has_next>,</#if>
    <#elseif columnEntity.dataType == "DOUBLE">
      ${record[key]!columnName.defaultValue} <#if columnName_has_next>,</#if>
    <#elseif columnEntity.dataType == "DECIMAL">
      ${record[key]!columnName.defaultValue} <#if columnName_has_next>,</#if>
    <#elseif columnEntity.dataType == "DATE">
     ' ${record[key]!columnName.defaultValue}' <#if columnName_has_next>,</#if>
    <#elseif columnEntity.dataType == "TIME">
     ' ${record[key]!columnName.defaultValue}' <#if columnName_has_next>,</#if>
    <#elseif columnEntity.dataType == "DATETIME">
     ' ${record[key]!columnName.defaultValue} '<#if columnName_has_next>,</#if>
    <#elseif columnEntity.dataType == "TIMESTAMP">
      '${record[key]!columnName.defaultValue}' <#if columnName_has_next>,</#if>
    <#elseif columnEntity.dataType == "CHAR">
      '${record[key]!columnName.defaultValue} '<#if columnName_has_next>,</#if>
    <#elseif columnEntity.dataType == "VARCHAR">
     ' ${record[key]!columnName.defaultValue}' <#if columnName_has_next>,</#if>
    <#elseif columnEntity.dataType == "PASSWORD">
      '${record[key]!columnName.defaultValue}' <#if columnName_has_next>,</#if>
    <#elseif columnEntity.dataType == "ATTACHMENT">
      '${record[key]!columnName.defaultValue} '<#if columnName_has_next>,</#if>
    <#elseif columnEntity.dataType == "TEXT">
      '${record[key]!columnName.defaultValue}' <#if columnName_has_next>,</#if>
    <#elseif columnEntity.dataType == "LONGTEXT">
     ' ${record[key]!columnName.defaultValue}' <#if columnName_has_next>,</#if>
    <#elseif columnEntity.dataType == "BLOB">
      '${record[key]!columnName.defaultValue} '<#if columnName_has_next>,</#if>
    <#elseif columnEntity.dataType == "LONGBLOB">
      '${record[key]!columnName.defaultValue}' <#if columnName_has_next>,</#if>
    <#else>
      '${record[key]!columnName.defaultValue}' <#if columnName_has_next>,</#if>
    </#if>
  </#list>
  )<#if record_has_next>,</#if>
</#list>;