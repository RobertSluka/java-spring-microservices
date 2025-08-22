package com.pm.patientservice.repository;

import com.pm.patientservice.model.Patient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
  boolean existsByEmail(String email);

  boolean existsByEmailAndIdNot(String email, UUID id);

  Optional<Patient> findByEmail(String email);

  Optional<Patient> findById(UUID id);

  @Query(value = """
    SELECT * FROM patient
    WHERE name LIKE CONCAT('%',:filterName,'%')
      AND date_of_birth < CAST(:dateOfBirth AS DATE)
""", nativeQuery = true)

  List<Patient> findByNameYear(@Param("filterName") String filterName,
                             @Param("dateOfBirth") LocalDate dateOfBirth);


  @Query("""
SELECT p FROM Patient p
ORDER BY p.dateOfBirth ASC
""")
  List<Patient> sortByDateOfBirth();

  List<Patient> getPatientsByName(String name);
  List<Patient> getPatientsByDateOfBirth(LocalDate dateOfBirth);

  @Query("""
SELECT p FROM Patient p
ORDER BY p.name ASC
""")
  List<Patient> sortByName();
}

