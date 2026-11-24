CREATE DATABASE IF NOT EXISTS vet_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE vet_db;

CREATE TABLE IF NOT EXISTS clinics (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(150) NOT NULL,
  adresse VARCHAR(255) NOT NULL,
  ville VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS veterinarians (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  nom VARCHAR(150) NOT NULL,
  specialite VARCHAR(150) NOT NULL,
  clinic_id BIGINT NOT NULL,
  CONSTRAINT fk_vet_clinic FOREIGN KEY (clinic_id) REFERENCES clinics(id)
);

CREATE TABLE IF NOT EXISTS slots (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  debut DATETIME NOT NULL,
  fin DATETIME NOT NULL,
  statut VARCHAR(30) NOT NULL DEFAULT 'LIBRE',
  veterinarian_id BIGINT NOT NULL,
  CONSTRAINT fk_slot_vet FOREIGN KEY (veterinarian_id) REFERENCES veterinarians(id),
  CONSTRAINT uq_slot UNIQUE (veterinarian_id, debut, fin)
);

CREATE INDEX idx_vet_specialite ON veterinarians(specialite);
CREATE INDEX idx_slot_vet ON slots(veterinarian_id);
