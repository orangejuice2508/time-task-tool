ALTER TABLE agamoTTTo_db.record
  CHANGE COLUMN duration duration_in_minutes BIGINT NOT NULL;
ALTER TABLE agamoTTTo_db.record_log
  CHANGE COLUMN duration duration_in_minutes BIGINT NOT NULL;
