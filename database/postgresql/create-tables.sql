create table if not exists wes_run (
    id text primary key,
    workflow_type text not null,
    workflow_type_version text,
    workflow_url text not null,
    workflow_params text not null,
    workflow_engine text not null,
    workflow_engine_version text,
    cromwell_run_id text,
    final_run_log_json text
);