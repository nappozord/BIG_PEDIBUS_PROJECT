INSERT INTO users(email, emailverified, name, password, provider, role) VALUES('alenappo123@live.it', true, 'Alessandro Napoletano', '$2a$10$37yDxnHe8ucrKu5cpoKhZ.baCxY47/DU840eJuy8FrUyXPn1XnoQO', 'local', 'SYS_ADMIN');
INSERT INTO users(email, emailverified, name, password, provider, role) VALUES('alenappo1234@live.it', true, 'Robby Giramondo', '$2a$10$37yDxnHe8ucrKu5cpoKhZ.baCxY47/DU840eJuy8FrUyXPn1XnoQO', 'local', 'USER');
INSERT INTO users(email, emailverified, name, password, provider, role) VALUES('admin1@test.it', true, 'Admin user', '$2a$10$37yDxnHe8ucrKu5cpoKhZ.baCxY47/DU840eJuy8FrUyXPn1XnoQO', 'local', 'ADMIN');
INSERT INTO users(email, emailverified, name, password, provider, role) VALUES('user1@test.it', true, 'Normal User', '$2a$10$37yDxnHe8ucrKu5cpoKhZ.baCxY47/DU840eJuy8FrUyXPn1XnoQO', 'local', 'USER');
INSERT INTO child(childname, parent_id) VALUES('Mariolino', 1);
INSERT INTO child(childname, parent_id) VALUES('Sandrone', 1);
INSERT INTO child(childname, parent_id) VALUES('Priscilla', 1);
