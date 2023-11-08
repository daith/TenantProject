Select
<#if (columnSelectedList?size != 0)>
    <#list columnSelectedList as columnName>
        "${columnName}" <#if columnName_has_next>,</#if>
    </#list>
<#else >
    *
</#if>
From "${tableName}"
Where  1=1
<#if (conditionForEq?size = 1)>
    <#list conditionForEq?keys as prop>
        "${prop}" = '${conditionForEq[prop]}'
        <#if prop_has_next> and </#if>
    </#list>
    ;
</#if>