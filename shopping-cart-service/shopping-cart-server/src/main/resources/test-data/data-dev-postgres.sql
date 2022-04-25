-- Truncate tables

CREATE OR REPLACE PROCEDURE truncate_all_tables() AS $$
DECLARE
    statements CURSOR FOR
        SELECT tablename FROM pg_tables
        WHERE schemaname = 'public' AND tablename NOT LIKE 'flyway%';
BEGIN
    FOR stmt IN statements LOOP
        EXECUTE 'TRUNCATE TABLE ' || quote_ident(stmt.tablename) || ' CASCADE;';
    END LOOP;
END;
$$ LANGUAGE plpgsql;



CALL truncate_all_tables();
