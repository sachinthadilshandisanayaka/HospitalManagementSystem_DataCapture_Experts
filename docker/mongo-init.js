// MongoDB initialization script
db = db.getSiblingDB('clinicdb');

db.createCollection('patients');
db.createCollection('practitioners');
db.createCollection('appointments');
db.createCollection('audit_logs');
db.createCollection('appointment_history');

// Unique indexes
db.patients.createIndex({ nationalId: 1 }, { unique: true });
db.practitioners.createIndex({ registrationNo: 1 }, { unique: true });

// Appointment indexes for performance
db.appointments.createIndex({ practitionerId: 1, startTime: 1 });
db.appointments.createIndex({ practitionerId: 1, status: 1 });
db.appointments.createIndex({ status: 1 });

print('clinicdb initialized with collections and indexes.');
