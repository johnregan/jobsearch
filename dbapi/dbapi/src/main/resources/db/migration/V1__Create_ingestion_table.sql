CREATE TABLE languages (
  id VARCHAR NOT NULL PRIMARY KEY
);

INSERT INTO languages(id) VALUES ('scala'),('java'),('python'),('javascript'),('c#');

CREATE TABLE ingestions (
    id UUID NOT NULL PRIMARY KEY,
    href VARCHAR NOT NULL UNIQUE,
    description VARCHAR NOT NULL UNIQUE,
    created_date TIMESTAMP NOT NULL,
    source VARCHAR(50) NOT NULL,
    language VARCHAR(50) NOT NULL REFERENCES languages(id)
);

