INSERT INTO public.${tableName}
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
    <#elseif columnName.dataType == "INT">
      ${record[key]!columnName.defaultValue} <#if columnName_has_next>,</#if>
    <#elseif columnName.dataType == "BIGINT">
      ${record[key]!columnName.defaultValue} <#if columnName_has_next>,</#if>
    <#elseif columnName.dataType == "FLOAT">
      ${record[key]!columnName.defaultValue} <#if columnName_has_next>,</#if>
    <#elseif columnName.dataType == "DOUBLE">
      ${record[key]!columnName.defaultValue} <#if columnName_has_next>,</#if>
    <#elseif columnName.dataType == "DECIMAL">
      ${record[key]!columnName.defaultValue} <#if columnName_has_next>,</#if>
    <#elseif columnName.dataType == "DATE">
     ' ${record[key]!columnName.defaultValue}' <#if columnName_has_next>,</#if>
    <#elseif columnName.dataType == "TIME">
     ' ${record[key]!columnName.defaultValue}' <#if columnName_has_next>,</#if>
    <#elseif columnName.dataType == "DATETIME">
     ' ${record[key]!columnName.defaultValue} '<#if columnName_has_next>,</#if>
    <#elseif columnName.dataType == "TIMESTAMP">
      '${record[key]!columnName.defaultValue}' <#if columnName_has_next>,</#if>
    <#elseif columnName.dataType == "CHAR">
      '${record[key]!columnName.defaultValue} '<#if columnName_has_next>,</#if>
    <#elseif columnName.dataType == "VARCHAR">
     ' ${record[key]!columnName.defaultValue}' <#if columnName_has_next>,</#if>
    <#elseif columnName.dataType == "PASSWORD">
      '${record[key]!columnName.defaultValue}' <#if columnName_has_next>,</#if>
    <#elseif columnName.dataType == "ATTACHMENT">
      '${record[key]!columnName.defaultValue} '<#if columnName_has_next>,</#if>
    <#elseif columnName.dataType == "TEXT">
      '${record[key]!columnName.defaultValue}' <#if columnName_has_next>,</#if>
    <#elseif columnName.dataType == "LONGTEXT">
     ' ${record[key]!columnName.defaultValue}' <#if columnName_has_next>,</#if>
    <#elseif columnName.dataType == "BLOB">
      '${record[key]!columnName.defaultValue} '<#if columnName_has_next>,</#if>
    <#elseif columnName.dataType == "LONGBLOB">
      '${record[key]!columnName.defaultValue}' <#if columnName_has_next>,</#if>
    <#else>
      '${record[key]!columnName.defaultValue}' <#if columnName_has_next>,</#if>
    </#if>
  </#list>
  )<#if record_has_next>,</#if>
</#list>;

