drop database task_time_tracker;
create database task_time_tracker;
use task_time_tracker;
create table ttt_company(
    id_company      int unsigned not null auto_increment,
    company_name    varchar(200) not null,
    primary key(id_company)
);

insert into ttt_company(company_name) values ('PACKT Publishing');
insert into ttt_company(company_name) values ('Gieman It Solutions');
insert into ttt_company(company_name) values ('Serious WebDev');

create table ttt_project(
    id_project      int unsigned not null auto_increment,
    project_name    varchar(200) not null,
    id_company      int unsigned not null,
    primary key(id_project),
    foreign key(id_company) references ttt_company(id_company)
);

insert into ttt_project(project_name, id_company) values ('Enterprise Application Development with Spring and ExtJS', 1);
insert into ttt_project(project_name, id_company) values ('The Spring Framework for Beginners', 1);
insert into ttt_project(project_name, id_company) values ('Advanced Sencha ExtJS4 ', 1);
insert into ttt_project(project_name, id_company) values ('The 3T Project', 2);
insert into ttt_project(project_name, id_company) values ('Breezing', 2);
insert into ttt_project(project_name, id_company) values ('Gieman Website', 2);
insert into ttt_project(project_name, id_company) values ('Internal Office Projects', 3);
insert into ttt_project(project_name, id_company) values ('External Consulting Tasks', 3);

create table ttt_task(
    id_task         int unsigned not null auto_increment,
    id_project      int unsigned not null,      
    task_name       varchar(200) not null,
    primary key(id_task),
    foreign key(id_project) references ttt_project(id_project)
);

insert into ttt_task(id_project, task_name)values ('1', 'Chapter 1');
insert into ttt_task(id_project, task_name)values ('1', 'Chapter 2');
insert into ttt_task(id_project, task_name)values ('1', 'Chapter 3');

insert into ttt_task(id_project, task_name)values ('2', 'Chapter 1');
insert into ttt_task(id_project, task_name)values ('2', 'Chapter 2');
insert into ttt_task(id_project, task_name)values ('2', 'Chapter 3');

insert into ttt_task(id_project, task_name)values ('3', 'Preface');
insert into ttt_task(id_project, task_name)values ('3', 'Appendix');
insert into ttt_task(id_project, task_name)values ('3', 'Illustrations');

insert into ttt_task(id_project, task_name)values ('4', 'Database Development');
insert into ttt_task(id_project, task_name)values ('4', 'Java development');
insert into ttt_task(id_project, task_name)values ('4', 'Sencha Development');
insert into ttt_task(id_project, task_name)values ('4', 'Testing');

create table ttt_user(
    username        varchar(10) not null,
    first_name      varchar(100) not null,
    last_name       varchar(100) not null,
    email           varchar(100) not null unique,
    password        varchar(100) not null,
    admin_role      char(1) default 'N',
    primary key(username)
);

insert into ttt_user(username, first_name, last_name, email, password, admin_role) 
    values ('jsmith', 'John', 'Smith', 'js@tttracker.com', 'admin', 'N');
insert into ttt_user(username, first_name, last_name, email, password, admin_role) 
    values ('bjones', 'Betty', 'Jones', 'bj@tttracker.com', 'admin','Y');

create table ttt_task_log(
    id_task_log         int unsigned not null auto_increment,
    id_task             int unsigned not null,
    username            varchar(10) not null,
    task_description    varchar(2000) not null,
    task_log_date       date not null,
    task_minutes        int unsigned not null,
    primary key(id_task_log),
    foreign key(id_task) references ttt_task(id_task),
    foreign key(username) references ttt_user(username)
);

insert into ttt_task_log (id_task, username, task_description, task_log_date,task_minutes)
values(1,'jsmith','Completed Chapter 1 proof reading',now(), 120);
insert into ttt_task_log (id_task, username, task_description, task_log_date,task_minutes)
values(2,'jsmith','Completed Chapter 2 draft',now(), 240);
insert into ttt_task_log (id_task, username, task_description, task_log_date,task_minutes)
values(3,'jsmith','Completed preparation work for initial draft',now(), 90);
insert into ttt_task_log (id_task, username, task_description, task_log_date,task_minutes)
values(3,'jsmith','Prepared database for Ch3 task',now(), 180);

insert into ttt_task_log (id_task, username, task_description, task_log_date,task_minutes)
values(1,'bjones','Started Chapter 1 ',now(), 340);
insert into ttt_task_log (id_task, username, task_description, task_log_date,task_minutes)
values(2,'bjones','Finished Chapter 2 draft',now(), 140);
insert into ttt_task_log (id_task, username, task_description, task_log_date,task_minutes)
values(3,'bjones','Initial draft work completed',now(), 450);
insert into ttt_task_log (id_task, username, task_description, task_log_date,task_minutes)
values(3,'bjones','Database design started',now(), 600);

/*
SHOW COLLATION WHERE COLLATION LIKE "%_cs"

ALTER TABLE ttt_user MODIFY
    password VARCHAR(100)
      COLLATE latin1_general_cs;
*/