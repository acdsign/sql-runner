--sqlrunner.fail-fast: false
DROP TABLE study;

CREATE TABLE study (
  STUDY_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT STUDY_PK PRIMARY KEY,
  study_name VARCHAR(200));