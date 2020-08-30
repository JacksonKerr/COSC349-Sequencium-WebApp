CREATE TABLE scores (
       user_name varchar(50) NOT NULL,
       score int,
       seq_score int
PRIMARY KEY (user_name)
);

INSERT INTO scores VALUES ('Megan', 40, 12);
INSERT INTO scores VALUES ('Jackson', 6, 20);
