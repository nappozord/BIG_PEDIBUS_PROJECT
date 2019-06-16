INSERT INTO users(email, emailverified, name, password, provider, role) VALUES('alenappo123@live.it', true, 'Alessandro Napoletano', '$2a$10$2o3PfMdYtu.8Am.rnnWBIOnmtHSz5paYE.A3R7sZJl42zvTnliZo2', 'local', 'SYS_ADMIN');
INSERT INTO users(email, emailverified, name, password, provider, role) VALUES('alenappo1234@live.it', true, 'Robby Giramondo', '$2a$10$2o3PfMdYtu.8Am.rnnWBIOnmtHSz5paYE.A3R7sZJl42zvTnliZo2', 'local', 'USER');
INSERT INTO users(email, emailverified, name, password, provider, role) VALUES('test_sysadmin@test.it', true, 'SysAdmin User', '$2a$10$I9KzM9Dfzkwge1Ro3WwxoOletz.J2OMLfUbgYCcGxJ0MN9lEhtGca', 'local', 'SYS_ADMIN');
INSERT INTO users(email, emailverified, name, password, provider, role) VALUES('test_user@test.it', true, 'Normal User', '$2a$10$I9KzM9Dfzkwge1Ro3WwxoOletz.J2OMLfUbgYCcGxJ0MN9lEhtGca', 'local', 'USER');
INSERT INTO child(childname, parent_id) VALUES('Mariolino', 1);
INSERT INTO child(childname, parent_id) VALUES('Sandrone', 1);
INSERT INTO child(childname, parent_id) VALUES('Priscilla', 1);
