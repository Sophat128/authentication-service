create table qrtz_calendars
(
    calendar_name varchar(80)                                             not null,
    calendar      text                                                    not null,
    sched_name    varchar(120) default 'TestScheduler'::character varying not null,
    primary key (sched_name, calendar_name)
);

alter table qrtz_calendars
    owner to my_user;

create table qrtz_fired_triggers
(
    entry_id          varchar(95)                                             not null,
    trigger_name      varchar(80)                                             not null,
    trigger_group     varchar(80)                                             not null,
    instance_name     varchar(80)                                             not null,
    fired_time        bigint                                                  not null,
    priority          integer                                                 not null,
    state             varchar(16)                                             not null,
    job_name          varchar(80),
    job_group         varchar(80),
    is_nonconcurrent  boolean,
    is_update_data    boolean,
    sched_name        varchar(120) default 'TestScheduler'::character varying not null,
    sched_time        bigint                                                  not null,
    requests_recovery boolean,
    primary key (sched_name, entry_id)
);

alter table qrtz_fired_triggers
    owner to my_user;

create index idx_qrtz_ft_j_g
    on qrtz_fired_triggers (sched_name, job_name, job_group);

create index idx_qrtz_ft_jg
    on qrtz_fired_triggers (sched_name, job_group);

create index idx_qrtz_ft_t_g
    on qrtz_fired_triggers (sched_name, trigger_name, trigger_group);

create index idx_qrtz_ft_tg
    on qrtz_fired_triggers (sched_name, trigger_group);

create index idx_qrtz_ft_trig_inst_name
    on qrtz_fired_triggers (sched_name, instance_name);

create table qrtz_job_details
(
    job_name          varchar(128)                                            not null,
    job_group         varchar(80)                                             not null,
    description       varchar(120),
    job_class_name    varchar(200)                                            not null,
    is_durable        boolean,
    is_nonconcurrent  boolean,
    is_update_data    boolean,
    sched_name        varchar(120) default 'TestScheduler'::character varying not null,
    requests_recovery boolean,
    job_data          bytea,
    primary key (sched_name, job_name, job_group)
);

alter table qrtz_job_details
    owner to my_user;

create index idx_qrtz_j_grp
    on qrtz_job_details (sched_name, job_group);

create table qrtz_locks
(
    lock_name  varchar(40)                                             not null,
    sched_name varchar(120) default 'TestScheduler'::character varying not null,
    primary key (sched_name, lock_name)
);

alter table qrtz_locks
    owner to my_user;

create table qrtz_paused_trigger_grps
(
    trigger_group varchar(80)                                             not null,
    sched_name    varchar(120) default 'TestScheduler'::character varying not null,
    primary key (sched_name, trigger_group)
);

alter table qrtz_paused_trigger_grps
    owner to my_user;

create table qrtz_scheduler_state
(
    instance_name     varchar(200)                                            not null,
    last_checkin_time bigint,
    checkin_interval  bigint,
    sched_name        varchar(120) default 'TestScheduler'::character varying not null,
    primary key (sched_name, instance_name)
);

alter table qrtz_scheduler_state
    owner to my_user;

create table qrtz_triggers
(
    trigger_name   varchar(80)                                             not null,
    trigger_group  varchar(80)                                             not null,
    job_name       varchar(80)                                             not null,
    job_group      varchar(80)                                             not null,
    description    varchar(120),
    next_fire_time bigint,
    prev_fire_time bigint,
    priority       integer,
    trigger_state  varchar(16)                                             not null,
    trigger_type   varchar(8)                                              not null,
    start_time     bigint                                                  not null,
    end_time       bigint,
    calendar_name  varchar(80),
    misfire_instr  smallint,
    job_data       bytea,
    sched_name     varchar(120) default 'TestScheduler'::character varying not null,
    primary key (sched_name, trigger_name, trigger_group),
    constraint qrtz_triggers_sched_name_fkey
        foreign key (sched_name, job_name, job_group) references qrtz_job_details
);

alter table qrtz_triggers
    owner to my_user;

create table qrtz_blob_triggers
(
    trigger_name  varchar(80)                                             not null,
    trigger_group varchar(80)                                             not null,
    blob_data     text,
    sched_name    varchar(120) default 'TestScheduler'::character varying not null,
    primary key (sched_name, trigger_name, trigger_group),
    constraint qrtz_blob_triggers_sched_name_fkey
        foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers
);

alter table qrtz_blob_triggers
    owner to my_user;

create table qrtz_cron_triggers
(
    trigger_name    varchar(80)                                             not null,
    trigger_group   varchar(80)                                             not null,
    cron_expression varchar(80)                                             not null,
    time_zone_id    varchar(80),
    sched_name      varchar(120) default 'TestScheduler'::character varying not null,
    primary key (sched_name, trigger_name, trigger_group),
    constraint qrtz_cron_triggers_sched_name_fkey
        foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers
);

alter table qrtz_cron_triggers
    owner to my_user;

create table qrtz_simple_triggers
(
    trigger_name    varchar(80)                                             not null,
    trigger_group   varchar(80)                                             not null,
    repeat_count    bigint                                                  not null,
    repeat_interval bigint                                                  not null,
    times_triggered bigint                                                  not null,
    sched_name      varchar(120) default 'TestScheduler'::character varying not null,
    primary key (sched_name, trigger_name, trigger_group),
    constraint qrtz_simple_triggers_sched_name_fkey
        foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers
);

alter table qrtz_simple_triggers
    owner to my_user;

create index fki_qrtz_simple_triggers_qrtz_triggers
    on qrtz_simple_triggers (trigger_name, trigger_group);

create table qrtz_simprop_triggers
(
    sched_name    varchar(120) not null,
    trigger_name  varchar(200) not null,
    trigger_group varchar(200) not null,
    str_prop_1    varchar(512),
    str_prop_2    varchar(512),
    str_prop_3    varchar(512),
    int_prop_1    integer,
    int_prop_2    integer,
    long_prop_1   bigint,
    long_prop_2   bigint,
    dec_prop_1    numeric(13, 4),
    dec_prop_2    numeric(13, 4),
    bool_prop_1   boolean,
    bool_prop_2   boolean,
    primary key (sched_name, trigger_name, trigger_group),
    constraint qrtz_simprop_triggers_sched_name_fkey
        foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers
);

alter table qrtz_simprop_triggers
    owner to my_user;

create index fki_qrtz_simple_triggers_job_details_name_group
    on qrtz_triggers (job_name, job_group);

create index idx_qrtz_t_c
    on qrtz_triggers (sched_name, calendar_name);

create index idx_qrtz_t_g
    on qrtz_triggers (sched_name, trigger_group);

create index idx_qrtz_t_j
    on qrtz_triggers (sched_name, job_name, job_group);

create index idx_qrtz_t_jg
    on qrtz_triggers (sched_name, job_group);

create index idx_qrtz_t_n_g_state
    on qrtz_triggers (sched_name, trigger_group, trigger_state);

create index idx_qrtz_t_n_state
    on qrtz_triggers (sched_name, trigger_name, trigger_group, trigger_state);

create index idx_qrtz_t_next_fire_time
    on qrtz_triggers (sched_name, next_fire_time);

create index idx_qrtz_t_nft_misfire
    on qrtz_triggers (sched_name, misfire_instr, next_fire_time);

create index idx_qrtz_t_nft_st
    on qrtz_triggers (sched_name, trigger_state, next_fire_time);

create index idx_qrtz_t_nft_st_misfire
    on qrtz_triggers (sched_name, misfire_instr, next_fire_time, trigger_state);

create index idx_qrtz_t_nft_st_misfire_grp
    on qrtz_triggers (sched_name, misfire_instr, next_fire_time, trigger_group, trigger_state);

create index idx_qrtz_t_state
    on qrtz_triggers (sched_name, trigger_state);