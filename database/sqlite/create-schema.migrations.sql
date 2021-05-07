CREATE TABLE wes_run (
    id TEXT PRIMARY KEY,
    workflow_type TEXT NOT NULL,
    workflow_type_version TEXT,
    workflow_engine TEXT NOT NULL,
    workflow_engine_version TEXT
);