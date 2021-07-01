--------------------------- creating all --------------------

CREATE TABLE IF NOT EXISTS tasks (
  id INT AUTO_INCREMENT PRIMARY KEY,
  canonical_name VARCHAR(255) NOT NULL,
  meta_data_tuples_json TEXT, 
  progress DECIMAL(5,2),
  created_at DATETIME NULL,
  started_at DATETIME NULL,
  crashed_at DATETIME NULL,
  finished_at DATETIME NULL
) ENGINE=INNODB;


CREATE TABLE IF NOT EXISTS rmd_suts (
  id INT AUTO_INCREMENT PRIMARY KEY,
  location VARCHAR(255) NOT NULL,
  title VARCHAR(64) NULL,
  description VARCHAR(128) NULL,  
  host VARCHAR(255) NULL,
  base_path VARCHAR(255) NULL,
  created_at DATETIME NULL
) ENGINE=INNODB;



CREATE TABLE IF NOT EXISTS rmd_actions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  path VARCHAR(255) NOT NULL,
  http_method ENUM('GET', 'PATCH', 'POST', 'PUT', 'DELETE', 'HEAD', 'OPTIONS') NOT NULL,
  sut_id INT,
  created_at DATETIME NULL  
) ENGINE=INNODB;

ALTER TABLE rmd_actions ADD FOREIGN KEY(sut_id) REFERENCES rmd_suts(id); 


CREATE TABLE IF NOT EXISTS rmd_parameters (
  id INT AUTO_INCREMENT PRIMARY KEY,
  position INT NOT NULL,
  name VARCHAR(64) NOT NULL,
  required BOOLEAN NOT NULL,
  description VARCHAR(255) NOT NULL,
  type ENUM('BOOLEAN', 'STRING', 'INTEGER', 'ARRAY') NOT NULL,
  context ENUM('FORMDATA', 'HEADER', 'PATH', 'QUERY') NOT NULL,
  meta_data_tuples_json TEXT,
  action_id INT,
  created_at DATETIME NULL  
) ENGINE=INNODB;

ALTER TABLE rmd_parameters ADD FOREIGN KEY(action_id) REFERENCES rmd_actions(id);


CREATE TABLE IF NOT EXISTS rmd_actions_dependencies (
  id INT AUTO_INCREMENT PRIMARY KEY,
  action_id INT NOT NULL,
  parameter_id INT NOT NULL,
  action_depends_on_id INT NOT NULL,
  parameter_depends_on VARCHAR(255) NOT NULL,
  discovery_modus ENUM('AUTOMATIC', 'MANUAL') NOT NULL,
  created_at DATETIME NULL
) ENGINE=INNODB;

ALTER TABLE rmd_actions_dependencies ADD FOREIGN KEY(action_id) REFERENCES rmd_actions(id);
ALTER TABLE rmd_actions_dependencies ADD FOREIGN KEY(parameter_id) REFERENCES rmd_parameters(id);
ALTER TABLE rmd_actions_dependencies ADD FOREIGN KEY(action_depends_on_id) REFERENCES rmd_actions(id);



CREATE TABLE IF NOT EXISTS rmd_responses (
  id INT AUTO_INCREMENT PRIMARY KEY,
  http_status_code INT NOT NULL,
  description VARCHAR(255) NOT NULL,
  action_id INT NOT NULL,
  created_at DATETIME NULL  
) ENGINE=INNODB;


ALTER TABLE rmd_responses ADD FOREIGN KEY(action_id) REFERENCES rmd_actions(id);


CREATE TABLE IF NOT EXISTS fuz_projects (
  id INT AUTO_INCREMENT PRIMARY KEY,
  description VARCHAR(255) NOT NULL,
  type VARCHAR(64) NOT NULL,
  meta_data_tuples_json TEXT, 
  sut_id INT,
  created_at DATETIME NOT NULL
) ENGINE=INNODB;

ALTER TABLE fuz_projects ADD FOREIGN KEY(sut_id) REFERENCES rmd_suts(id);


CREATE TABLE IF NOT EXISTS fuz_sequences (
  id INT AUTO_INCREMENT PRIMARY KEY,
  position INT,
  length INT,
  status VARCHAR(64) NOT NULL,
  project_id INT,
  created_at DATETIME NOT NULL  
) ENGINE=INNODB;

ALTER TABLE fuz_sequences ADD FOREIGN KEY(project_id) REFERENCES fuz_projects(id);


CREATE TABLE IF NOT EXISTS fuz_requests (
  id INT AUTO_INCREMENT PRIMARY KEY,
  path VARCHAR(255) NOT NULL,
  http_method ENUM('GET', 'POST', 'PATCH', 'PUT', 'DELETE') NOT NULL,
  formdata_parameters_json TEXT,
  header_parameters_json TEXT,
  path_parameters_json TEXT,
  query_parameters_json TEXT,
  project_id INT NOT NULL,
  sequence_id INT NULL,
  action_id INT NOT NULL,
  created_at DATETIME NOT NULL
) ENGINE=INNODB;

ALTER TABLE fuz_requests ADD FOREIGN KEY(project_id) REFERENCES fuz_projects(id);
ALTER TABLE fuz_requests ADD FOREIGN KEY(action_id) REFERENCES rmd_actions(id);
ALTER TABLE fuz_requests ADD FOREIGN KEY(sequence_id) REFERENCES fuz_sequences(id);

CREATE INDEX idx_fuz_requests_http_method_path ON fuz_requests (http_method, path);

CREATE TABLE IF NOT EXISTS fuz_responses (
  id INT AUTO_INCREMENT PRIMARY KEY,
  status_code INT,
  status_description VARCHAR(255),
  body TEXT,
  failure_reason VARCHAR(255),
  project_id INT,
  request_id INT,
  created_at DATETIME NOT NULL
) ENGINE=INNODB;

ALTER TABLE fuz_responses ADD FOREIGN KEY(project_id) REFERENCES fuz_projects(id);
ALTER TABLE fuz_responses ADD FOREIGN KEY(request_id) REFERENCES fuz_requests(id);

CREATE INDEX idx_fuz_responses_status_code ON fuz_responses (status_code);

CREATE TABLE IF NOT EXISTS fuz_dictionaries (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255),
  items_text TEXT,
  created_at DATETIME NOT NULL  
) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS fuz_configurations (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255),
  items_json TEXT,
  created_at DATETIME NOT NULL  
) ENGINE=INNODB;


CREATE TABLE IF NOT EXISTS reports (
  id INT AUTO_INCREMENT PRIMARY KEY,
  type ENUM('CODE_COVERAGE', 'RESPONSES') NOT NULL,
  description VARCHAR(255) NOT NULL,
  meta_data_tuples_json TEXT,
  output TEXT,
  project_id INT,
  created_at DATETIME NULL,
  completed_at DATETIME NULL
) ENGINE=INNODB;


CREATE INDEX idx_reports_project_id ON fuz_responses (project_id);

--------------------------- dropping all --------------------

drop table fuz_responses;
drop table fuz_requests;
drop table fuz_sequences;
drop table fuz_projects;
drop table fuz_dictionaries;
drop table fuz_configurations;

drop table rmd_responses;
drop table rmd_parameters;
drop table rmd_actions_dependencies;
drop table rmd_actions;
drop table rmd_suts;

drop table tasks;
drop table reports;

--------------------------- other ---------------------------

truncate tasks;
truncate rmd_suts;
truncate rmd_actions;
truncate rmd_parameters;
truncate rmd_responses;

insert into rmd_suts values (1, "http://localhost/wordpress/rest-api/schema", null, null, null, null, null);
insert into rmd_suts values (1, "/ws/git/ou-prototype/rest-fuzzer/backend/src/main/resources/schema.json", null, null, null, null);

insert into tasks values (1, "nl.ou.se.rest.fuzzer.extractor.ExtractorTask", "{\"sut_id\":1}", NULL, NULL, NULL);