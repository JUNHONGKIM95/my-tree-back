CREATE TABLE users (
  user_id VARCHAR2(50) PRIMARY KEY,
  password VARCHAR2(255) NOT NULL,
  name VARCHAR2(50) NOT NULL,
  created_at DATE DEFAULT SYSDATE NOT NULL,
  ip_address VARCHAR2(50) NOT NULL
);

INSERT INTO users (
  user_id,
  password,
  name,
  created_at,
  ip_address
) VALUES (
  'admin',
  '963963',
  '관리자',
  SYSDATE,
  '127.0.0.1'
);

CREATE SEQUENCE SEQ_POST_NO
  START WITH 1
  INCREMENT BY 1
  NOCACHE
  NOCYCLE;

CREATE TABLE posts (
  post_no NUMBER PRIMARY KEY,
  user_id VARCHAR2(50) NOT NULL,
  title VARCHAR2(150) NOT NULL,
  content VARCHAR2(4000) NOT NULL,
  created_at DATE DEFAULT SYSDATE NOT NULL
);
