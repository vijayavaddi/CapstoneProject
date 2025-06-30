
INSERT INTO specializations (specialization_id, specialization_name) VALUES (gen_RANDOM_UUID(), 'Cardiology');
INSERT INTO specializations (specialization_id, specialization_name) VALUES (gen_RANDOM_UUID(), 'Neurology');
INSERT INTO specializations (specialization_id, specialization_name) VALUES (gen_RANDOM_UUID(), 'Orthopedics');
INSERT INTO specializations (specialization_id, specialization_name) VALUES (gen_RANDOM_UUID(), 'Pediatrics');
INSERT INTO specializations (specialization_id, specialization_name) VALUES (gen_RANDOM_UUID(), 'Dermatology');
INSERT INTO specializations (specialization_id, specialization_name) VALUES (gen_RANDOM_UUID(), 'Oncology');
INSERT INTO specializations (specialization_id, specialization_name) VALUES (gen_RANDOM_UUID(), 'Psychiatry');
INSERT INTO specializations (specialization_id, specialization_name) VALUES (gen_RANDOM_UUID(), 'Radiology');
INSERT INTO specializations (specialization_id, specialization_name) VALUES (gen_RANDOM_UUID(), 'Gastroenterology');
INSERT INTO specializations (specialization_id, specialization_name) VALUES (gen_RANDOM_UUID(), 'Endocrinology');

-- Insert Users
INSERT INTO users (id, mobile, role, first_name, last_name, password, created_at, updated_at)
VALUES
('11111111-1111-1111-1111-111111111111', '3456789769', 'DOCTOR', 'vijaya', 'vaddi', 'vijaya', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('22222222-2222-2222-2222-222222222222', '9846822222', 'PATIENT', 'john', 'doe', 'john', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Patient
INSERT INTO patient (id, age, gender, blood_group, user_id)
VALUES
('33333333-3333-3333-3333-333333333333', 21, 'MALE', 'A+', '22222222-2222-2222-2222-222222222222');

-- Insert Specialization
INSERT INTO specialization (id, specialization_name)
VALUES
('44444444-4444-4444-4444-444444444444', 'pediatrics');

-- Insert Disease
INSERT INTO disease (id, name, specialization_id)
VALUES
('55555555-5555-5555-5555-555555555555', 'common cold', '44444444-4444-4444-4444-444444444444');

-- Insert Doctor
INSERT INTO doctor (id, qualification, experience, user_id, specialization_id)
VALUES
('66666666-6666-6666-6666-666666666666', 'MBBS', '1', '11111111-1111-1111-1111-111111111111', '44444444-4444-4444-4444-444444444444');

-- Insert Appointment
INSERT INTO appointment (id, from_time, to_time, status, description, disease_id, doctor_id, patient_id)
VALUES
('77777777-7777-7777-7777-777777777777', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP, 'SCHEDULED', ',', '55555555-5555-5555-5555-555555555555', '66666666-6666-6666-6666-666666666666', '33333333-3333-3333-3333-333333333333');

!-------------------diseases---------------------------

