CREATE TABLE "${tableName}" (
<#list columnEntityList as columnEntity>
  <#if columnEntity.dataType?upper_case == "BOOL">
    "${columnEntity.name}" BOOL<#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != "" > DEFAULT <#if columnEntity.defaultValue == "true">1<#else>0</#if></#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#elseif columnEntity.dataType?upper_case == "INT">
    "${columnEntity.name}"<#if columnEntity.autoIncrement?? && columnEntity.autoIncrement == true> SERIAL<#else> INT</#if><#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT ${columnEntity.defaultValue}</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#elseif columnEntity.dataType?upper_case == "BIGINT">
    "${columnEntity.name}"<#if columnEntity.autoIncrement?? && columnEntity.autoIncrement == true> BIGSERIAL<#else> BIGINT</#if><#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT ${columnEntity.defaultValue}</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#elseif columnEntity.dataType?upper_case == "FLOAT">
    "${columnEntity.name}" REAL<#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT ${columnEntity.defaultValue}</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#elseif columnEntity.dataType?upper_case == "DOUBLE">
    "${columnEntity.name}" DOUBLE PRECISION<#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT ${columnEntity.defaultValue}</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#elseif columnEntity.dataType?upper_case == "DECIMAL">
    "${columnEntity.name}" DECIMAL<#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT ${columnEntity.defaultValue}</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#elseif columnEntity.dataType?upper_case == "DATE">
    "${columnEntity.name}" DATE<#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT '${columnEntity.defaultValue}'</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#elseif columnEntity.dataType?upper_case == "TIME">
    "${columnEntity.name}" TIME<#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT '${columnEntity.defaultValue}'</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#elseif columnEntity.dataType?upper_case == "DATETIME2">
    "${columnEntity.name}" TIMESTAMP<#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT '${columnEntity.defaultValue}'</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#elseif columnEntity.dataType?upper_case == "DATETIME">
    "${columnEntity.name}" TIMESTAMP<#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT '${columnEntity.defaultValue}'</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#elseif columnEntity.dataType?upper_case == "TIMESTAMP">
    "${columnEntity.name}" TIMESTAMP<#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT '${columnEntity.defaultValue}'</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#elseif columnEntity.dataType?upper_case == "CHAR">
    "${columnEntity.name}" CHAR(${columnEntity.length})<#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT '${columnEntity.defaultValue}'</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#elseif columnEntity.dataType?upper_case == "VARCHAR">
    "${columnEntity.name}"
      <#if  columnEntity.length?upper_case == "MAX"> VARCHAR(65535)
      <#elseif  columnEntity.length?upper_case != "MAX"> VARCHAR(${columnEntity.length})
      </#if>

      <#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT '${columnEntity.defaultValue}'</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#elseif columnEntity.dataType?upper_case == "PASSWORD">
    "${columnEntity.name}" VARCHAR(200)<#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT '${columnEntity.defaultValue}'</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#elseif columnEntity.dataType?upper_case == "ATTACHMENT">
    "${columnEntity.name}" VARCHAR(4000)<#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT '${columnEntity.defaultValue}'</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#elseif columnEntity.dataType?upper_case == "TEXT">
    "${columnEntity.name}" TEXT<#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT '${columnEntity.defaultValue}'</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#elseif columnEntity.dataType?upper_case == "LONGTEXT">
    "${columnEntity.name}" TEXT<#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT '${columnEntity.defaultValue}'</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#elseif columnEntity.dataType?upper_case == "BLOB">
    "${columnEntity.name}" BYTEA<#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT ${columnEntity.defaultValue}</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#elseif columnEntity.dataType?upper_case == "LONGBLOB">
    "${columnEntity.name}" BYTEA<#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT ${columnEntity.defaultValue}</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  <#else>
    "${columnEntity.name}" VARCHAR(200)<#if columnEntity.defaultValue?? &&  columnEntity.defaultValue != ""> DEFAULT ${columnEntity.defaultValue}</#if><#if columnEntity.nullable != true> NOT NULL</#if><#if columnEntity_has_next>,</#if>
  </#if>
</#list>
);


<#assign primary_array = [] />
<#list columnEntityList as columnEntity>

  <#if columnEntity.pkType?? && columnEntity.pkType == "PRIMARY">
    <#assign primary_array += ["${columnEntity.name}"] />
  </#if>

  <#if columnEntity.pkType?? && columnEntity.pkType == "UNIQUE">
    ALTER TABLE "${tableName}" ADD CONSTRAINT UN_"${tableName}_${columnEntity.name}" UNIQUE("${columnEntity.name}");
  </#if>

  <#if columnEntity.pkType?? && (columnEntity.pkType == "INDEX" || columnEntity.pkType == "FULLTEXT")>
    CREATE INDEX "${columnEntity.pkType}" ON "${tableName}" ("${columnEntity.name}");
  </#if>
</#list>

<#if primary_array?size gt 0>

  ALTER TABLE "${tableName}" ADD CONSTRAINT "PR_$${tableName}_ ${primary_array?join("_")}" PRIMARY KEY (
  <#list primary_array as primary>
    "${primary}" <#if primary_has_next>,</#if>
  </#list>
  );
</#if>



<#--ALTER TABLE orders ADD CONSTRAINT fk_orders_customers FOREIGN KEY (customer_id) REFERENCES customers (id);-->

<#list columnEntityList as columnEntity>
  <#if columnEntity.fkTableName?? &&  columnEntity.fkTableName != ""  && columnEntity.fkColumn??  &&  columnEntity.fkColumn != "">
    ALTER TABLE "${tableName}" ADD CONSTRAINT "fk_${tableName}_${columnEntity.name}" FOREIGN KEY ("${columnEntity.name}") REFERENCES "${columnEntity.fkTableName}" ("${columnEntity.fkColumn}");;
  </#if>
</#list>


COMMENT ON TABLE "${tableName}" IS '${caption}';

<#list columnEntityList as columnEntity>
  COMMENT ON COLUMN "${tableName}"."${columnEntity.name}" IS '${columnEntity.caption}';
</#list>

