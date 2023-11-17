
<#ftl output_format="XML">
<#if (recordList?size != 0)>
    <#list recordList as record>
    UPDATE  "${tableName}"
        SET

         <#list dataColumns as column>
             <#if record[column]?? >
                "${column}" = '${record[column]}'
                <#if column_has_next> , </#if>
            </#if>
        </#list>
        Where  1=1 and
        <#list keyColumns as key>
             "${key}" = '${record[key]}'
        <#if key_has_next> and </#if>
        </#list>
        ;
    </#list>
</#if>
