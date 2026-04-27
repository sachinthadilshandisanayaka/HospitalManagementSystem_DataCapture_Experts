package co.clinic.appointment.loader;

import co.clinic.appointment.entity.Appointment;
import co.clinic.appointment.entity.AppointmentStatus;
import co.clinic.appointment.entity.Patient;
import co.clinic.appointment.entity.Practitioner;
import co.clinic.appointment.repository.AppointmentRepository;
import co.clinic.appointment.repository.PatientRepository;
import co.clinic.appointment.repository.PractitionerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
@Profile("!test")
public class SampleDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SampleDataLoader.class);

    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;
    private final AppointmentRepository appointmentRepository;

    public SampleDataLoader(PatientRepository patientRepository,
                             PractitionerRepository practitionerRepository,
                             AppointmentRepository appointmentRepository) {
        this.patientRepository = patientRepository;
        this.practitionerRepository = practitionerRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public void run(String... args) {
        if (patientRepository.count() > 0) {
            log.info("Sample data already loaded, skipping.");
            return;
        }

        log.info("Loading sample data...");

        Patient p1 = new Patient();
        p1.setFullName("Sachi Dilshan");
        p1.setNationalId("199801234567");
        p1.setDateOfBirth(LocalDate.of(1998, 1, 23));
        p1.setEmail("sachi@example.com");
        p1.setPhone("+94771234567");
        Patient savedP1 = patientRepository.save(p1);

        Patient p2 = new Patient();
        p2.setFullName("Nimali Perera");
        p2.setNationalId("199509876543");
        p2.setDateOfBirth(LocalDate.of(1995, 9, 15));
        p2.setEmail("nimali@example.com");
        patientRepository.save(p2);

        Practitioner dr1 = new Practitioner();
        dr1.setFullName("Dr. Kamal Silva");
        dr1.setRegistrationNo("SLMC-2024-001");
        dr1.setSpecialty("General");
        Practitioner savedDr1 = practitionerRepository.save(dr1);

        Practitioner dr2 = new Practitioner();
        dr2.setFullName("Dr. Dilani Rathnayake");
        dr2.setRegistrationNo("SLMC-2024-002");
        dr2.setSpecialty("Cardiology");
        practitionerRepository.save(dr2);

        Appointment appt = new Appointment();
        appt.setPatientId(savedP1.getId());
        appt.setPractitionerId(savedDr1.getId());
        appt.setStartTime(Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.HOURS));
        appt.setEndTime(Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.HOURS).plus(30, ChronoUnit.MINUTES));
        appt.setStatus(AppointmentStatus.BOOKED);
        appt.setNotes("Initial consultation");
        appointmentRepository.save(appt);

        log.info("Sample data loaded: 2 patients, 2 practitioners, 1 appointment.");
    }
}
