-- VetRDV - schema MySQL/MariaDB pour l'ensemble du projet
-- Importer ce fichier dans phpMyAdmin.
-- Chaque microservice garde sa propre base. Les *_id sont des references
-- logiques vers un autre service et ne sont volontairement pas des FK.

CREATE DATABASE IF NOT EXISTS user_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS animal_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS vet_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS appointment_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS medical_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS notification_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- =========================================================
-- 1. User Service - user_db
-- =========================================================
CREATE TABLE IF NOT EXISTS user_db.users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(180) NOT NULL UNIQUE,
  mot_de_passe VARCHAR(255) NOT NULL,
  role ENUM('OWNER','VET','ADMIN') NOT NULL DEFAULT 'OWNER',
  statut VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_db.owner_profiles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL UNIQUE,
  nom VARCHAR(150) NOT NULL,
  telephone VARCHAR(30),
  adresse VARCHAR(255),
  CONSTRAINT fk_owner_user FOREIGN KEY (user_id) REFERENCES user_db.users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_db.vet_profiles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL UNIQUE,
  nom VARCHAR(150) NOT NULL,
  matricule VARCHAR(100) NOT NULL UNIQUE,
  CONSTRAINT fk_vet_profile_user FOREIGN KEY (user_id) REFERENCES user_db.users(id) ON DELETE CASCADE
);

-- =========================================================
-- 2. Animal Service - animal_db
-- =========================================================
CREATE TABLE IF NOT EXISTS animal_db.animals (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(100) NOT NULL,
  espece ENUM('CHIEN','CHAT','OISEAU','AUTRE') NOT NULL,
  race VARCHAR(100),
  date_naissance DATE,
  owner_id BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_animal_owner (owner_id)
);

-- =========================================================
-- 3. Vet Service - vet_db
-- =========================================================
CREATE TABLE IF NOT EXISTS vet_db.clinics (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(150) NOT NULL,
  adresse VARCHAR(255) NOT NULL,
  ville VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS vet_db.veterinarians (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  nom VARCHAR(150) NOT NULL,
  specialite VARCHAR(150) NOT NULL,
  clinic_id BIGINT NOT NULL,
  CONSTRAINT fk_vet_clinic_all FOREIGN KEY (clinic_id) REFERENCES vet_db.clinics(id) ON DELETE RESTRICT,
  INDEX idx_veterinarian_specialite (specialite),
  INDEX idx_veterinarian_user (user_id)
);

CREATE TABLE IF NOT EXISTS vet_db.slots (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  debut DATETIME NOT NULL,
  fin DATETIME NOT NULL,
  statut VARCHAR(30) NOT NULL DEFAULT 'LIBRE',
  veterinarian_id BIGINT NOT NULL,
  CONSTRAINT fk_slot_veterinarian_all FOREIGN KEY (veterinarian_id) REFERENCES vet_db.veterinarians(id) ON DELETE CASCADE,
  CONSTRAINT uq_slot_all UNIQUE (veterinarian_id, debut, fin),
  INDEX idx_slot_status (statut)
);

-- =========================================================
-- 4. Appointment Service - appointment_db
-- =========================================================
CREATE TABLE IF NOT EXISTS appointment_db.appointments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  owner_id BIGINT NOT NULL,
  animal_id BIGINT NOT NULL,
  vet_id BIGINT NOT NULL,
  slot_id BIGINT NOT NULL,
  motif VARCHAR(255),
  statut ENUM('PENDING','CONFIRMED','CANCELLED','DONE') NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_appointment_owner (owner_id),
  INDEX idx_appointment_animal (animal_id),
  INDEX idx_appointment_vet (vet_id),
  INDEX idx_appointment_slot (slot_id)
);

-- =========================================================
-- 5. MedicalRecord Service - medical_db
-- =========================================================
CREATE TABLE IF NOT EXISTS medical_db.medical_records (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  animal_id BIGINT NOT NULL UNIQUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_medical_animal (animal_id)
);

CREATE TABLE IF NOT EXISTS medical_db.consultations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  record_id BIGINT NOT NULL,
  vet_id BIGINT NOT NULL,
  date_consultation DATE NOT NULL,
  diagnostic TEXT NOT NULL,
  traitement TEXT,
  CONSTRAINT fk_consultation_record FOREIGN KEY (record_id) REFERENCES medical_db.medical_records(id) ON DELETE CASCADE,
  INDEX idx_consultation_vet (vet_id)
);

CREATE TABLE IF NOT EXISTS medical_db.vaccines (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  record_id BIGINT NOT NULL,
  nom VARCHAR(150) NOT NULL,
  date_vaccination DATE NOT NULL,
  rappel DATE,
  CONSTRAINT fk_vaccine_record FOREIGN KEY (record_id) REFERENCES medical_db.medical_records(id) ON DELETE CASCADE
);

-- =========================================================
-- 6. Notification Service - notification_db
-- =========================================================
CREATE TABLE IF NOT EXISTS notification_db.notifications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  type_notif ENUM('CONFIRMATION','RAPPEL','ANNULATION') NOT NULL,
  canal VARCHAR(30) NOT NULL DEFAULT 'EMAIL',
  contenu TEXT NOT NULL,
  statut VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_notification_user (user_id),
  INDEX idx_notification_status (statut)
);

CREATE TABLE IF NOT EXISTS notification_db.templates (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(100) NOT NULL UNIQUE,
  sujet VARCHAR(255) NOT NULL,
  corps TEXT NOT NULL
);

-- Donnees de test minimales pour verifier le Vet Service.
INSERT INTO vet_db.clinics (nom, adresse, ville)
SELECT 'Clinique VetRDV', '12 rue de la Sante animale', 'Tunis'
WHERE NOT EXISTS (SELECT 1 FROM vet_db.clinics WHERE nom = 'Clinique VetRDV');
