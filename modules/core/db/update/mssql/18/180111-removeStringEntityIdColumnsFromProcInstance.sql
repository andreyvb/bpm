if exists (select *  FROM sys.objects o INNER JOIN sys.columns c ON o.object_id = c.object_id WHERE o.name = 'BPM_PROC_INSTANCE' AND c.name = 'STRING_ENTITY_ID')
    ALTER TABLE bpm_proc_instance DROP COLUMN string_entity_id^
