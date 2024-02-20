-- Copyright (c) 2022 WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
--
-- WSO2 LLC. licenses this file to you under the Apache License,
-- Version 2.0 (the "License"); you may not use this file except
-- in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied.  See the License for the
-- specific language governing permissions and limitations
-- under the License.

DROP DATABASE IF EXISTS persist;
CREATE DATABASE persist;
USE persist;

CREATE TABLE Patient (
  id INT,
  name VARCHAR(191) NOT NULL,
  gender ENUM ('MALE', 'FEMALE') NOT NULL,
  nic VARCHAR(12) NOT NULL,
  contact CHAR(10) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE Doctor (
  id INT,
  name VARCHAR(20) NOT NULL,
  specialty VARCHAR(191) NOT NULL,
  salary DECIMAL(10,2),
  PRIMARY KEY (id)
);

CREATE TABLE Appointment (
  id INT,
  patientId INT NOT NULL,
  doctorId INT NOT NULL,
  date DATE NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (patientId) REFERENCES Patient(id),
  FOREIGN KEY (doctorId) REFERENCES Doctor(id)
);