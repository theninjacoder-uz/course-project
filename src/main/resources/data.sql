CREATE MATERIALIZED VIEW simple_search AS
SELECT i.id                                AS id,
       i.name                              AS name,
       'ITEM'                              AS table_type,
       setweight(to_tsvector(i.name), 'A') AS doc
from item i
UNION ALL
SELECT c.id                                                        AS id,
       c.name                                                      AS name,
       'COLLECTION'                                                AS table_type,
       setweight(to_tsvector(c.name), 'A') || setweight(to_tsvector(c.description), 'B')  AS doc
from collection c
UNION ALL
SELECT com.item_id                           AS id,
       com.text                              AS name,
       'COMMENT'                             AS table_type,
       setweight(to_tsvector(com.text), 'B') AS doc
from comment com;

CREATE INDEX idx_fts ON simple_search USING gin (doc);
CREATE INDEX idx_unq ON simple_search (id);


CREATE OR REPLACE FUNCTION refresh_view()
    RETURNS TRIGGER AS
$$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY simple_search;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER item_trigger_update_view
    AFTER INSERT OR UPDATE OR DELETE ON item
    FOR EACH ROW EXECUTE PROCEDURE refresh_view();

CREATE TRIGGER collection_trigger_update_view
    AFTER INSERT OR UPDATE OR DELETE ON collection
    FOR EACH ROW EXECUTE PROCEDURE refresh_view();

CREATE TRIGGER comment_trigger_update_view
    AFTER INSERT OR UPDATE OR DELETE ON comment
    FOR EACH ROW EXECUTE PROCEDURE refresh_view();


CREATE OR REPLACE FUNCTION full_text_search(search_text TEXT)
    RETURNS TABLE
            (
                id   bigint,
                name character varying,
                table_type text
            )
    LANGUAGE plpgsql
AS
$$
BEGIN
RETURN QUERY
SELECT s.id, s.name, s.table_type FROM simple_search s WHERE s.doc @@ plainto_tsquery(search_text);
END;
$$

select * from full_text_search('just');