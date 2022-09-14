CREATE TABLE tripbuilder.tb_category (
	traj_fk varchar(200) NULL,
	"values" text NULL
);

CREATE TABLE tripbuilder.tb_locatedin (
	traj_fk varchar(200) NULL,
	"values" text NULL
);

CREATE TABLE tripbuilder.tb_move (
	traj_fk varchar(200) NULL,
	"values" text NULL
);

CREATE TABLE tripbuilder.tb_poi (
	traj_fk varchar(200) NULL,
	"values" text NULL
);

CREATE TABLE tripbuilder.tb_trajectory (
	id varchar(200) NOT NULL,
	"values" text NULL,
	CONSTRAINT tripbuilder_tb_trajectory2_pk PRIMARY KEY (id)
);