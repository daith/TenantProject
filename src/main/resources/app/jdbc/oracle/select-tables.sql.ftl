SELECT t.TABLE_NAME AS "tableName",
t.TABLESPACE_NAME  AS "tableSpaceName",
c.COMMENTS AS "comment"
FROM USER_TABLES t
LEFT JOIN USER_TAB_COMMENTS c ON c.TABLE_NAME = t.TABLE_NAME
