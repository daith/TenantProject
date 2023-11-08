<#ftl output_format="XML">
DELETE FROM "${tableName}"
Where  1=1
<#if (records?size != 0)>
    and
     <#list records as record>
         (
         <#list record?keys as prop>
             "${prop}" = '${record[prop]}'
             <#if prop_has_next> and </#if>
         </#list>
         )
         <#if record_has_next> or </#if>
     </#list>
</#if>
;