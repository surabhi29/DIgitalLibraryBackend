# DIgitalLibraryBackend
1.) Used Dropwizard, Guice, JAVA 8 as technologies and MySQL for database.
2.) Queries to create the table, Datatbase Name is Demo:-
CREATE TABLE book_details (id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
book_name VARCHAR(30) NOT NULL,
author VARCHAR(30),
isbn VARCHAR(20) NOT NULL UNIQUE,
status VARCHAR(20));

CREATE TABLE location_details
(shelf_number INT NOT NULL,
row_number INT NOT NULL,
column_number INT NOT NULL,
book_id INT,
PRIMARY KEY (shelf_number, row_number, column_number),
FOREIGN KEY (book_id) REFERENCES book_details(id));

CREATE TABLE bookIssue_details
(name VARCHAR(40) NOT NULL,
issue_date DATE NOT NULL,
to_date DATE NOT NULL,
book_id INT,
FOREIGN KEY (book_id) REFERENCES book_details(id));

3.)Details related to sql server connection and book details information are mentioned in config.yml.
4.)To start an application 2 arguments are passed server and path of the config.yml separated by space.
5.)Checked the services result using insomnia application.
