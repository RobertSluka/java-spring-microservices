package com.pm.patientservice.controller;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import com.pm.patientservice.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cache.annotation.Cacheable;

import jakarta.validation.groups.Default;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patient", description = "API for managing Patients")
public class PatientController {

  private final PatientService patientService;

  public PatientController(PatientService patientService) {
    this.patientService = patientService;
  }


  @GetMapping
  @Operation(summary = "Get Patients")
  public ResponseEntity<List<PatientResponseDTO>> getPatients() {
    List<PatientResponseDTO> patients = patientService.getPatients();
    return ResponseEntity.ok().body(patients);
  }

  @GetMapping("/name")
  @Operation(summary = "Get Patients")
  public ResponseEntity<List<PatientResponseDTO>> getPatientByName(String name) {
    List<PatientResponseDTO> patients = patientService.getPatientByName(name);
    return ResponseEntity.ok().body(patients);
  }



  @GetMapping("/dateOfBirth")
  public ResponseEntity<List<PatientResponseDTO>> getPatientByDateOfBirth(
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth) {

    List<PatientResponseDTO> patients = patientService.getPatientByDateOfBirth(dateOfBirth);
    return ResponseEntity.ok(patients);
  }
//  @GetMapping("/email")
//  @Cacheable(value = "PATIENT_CACHE", key = "#email")
//  @Operation(summary = "Get Patients")
//  public PatientResponseDTO getPatientByEmail(@RequestParam String email) throws ChangeSetPersister.NotFoundException {
//    return patientService.getPatientByEmail(email);
//
//  }

  @GetMapping("/id")
  @Cacheable(value = "PATIENT_CACHE", key = "#id")
  @Operation(summary = "Get Patients")
  public PatientResponseDTO getPatientById(@RequestParam UUID id) throws ChangeSetPersister.NotFoundException {
    return patientService.getPatientById(id);

  }

  @PostMapping
  @Operation(summary = "Create a new Patient")
  @CachePut(value = "PATIENT_CACHE", key = "#result.id")
  public ResponseEntity<PatientResponseDTO> createPatient(
      @Validated({Default.class, CreatePatientValidationGroup.class})
      @RequestBody PatientRequestDTO patientRequestDTO) {

    PatientResponseDTO patientResponseDTO = patientService.createPatient(
        patientRequestDTO);



    return ResponseEntity.ok().body(patientResponseDTO);
  }

  @GetMapping("/filter")
  public ResponseEntity<List<PatientResponseDTO>> getPatientsByFilter(
//          @RequestParam(required = false) String sortBy,
          @RequestParam(required = false) String filterName,
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth) {

    List<PatientResponseDTO> patients = patientService.getPatientsByFilter( filterName, dateOfBirth);
    return ResponseEntity.ok(patients);
  }

  @GetMapping("/sort")
  public ResponseEntity<List<PatientResponseDTO>> sortPatientsByFilter(
          @RequestParam(required = false) String sortBy){
//    if (sortBy == null) {
//      return ResponseEntity.badRequest().body(Collections.emptyList());
//    }

    List<PatientResponseDTO> patients;

    switch (sortBy.toLowerCase()) {
      case "name":
        patients = patientService.sortByName();
        break;
      case "dob":
      case "dateofbirth":
        patients = patientService.sortByDateOfBirth();
        break;
      default:
        return ResponseEntity.badRequest().body(Collections.emptyList());
    }

    return ResponseEntity.ok(patients);

  }

  @PutMapping("/{id}")
  @CachePut(value = "PATIENT_CACHE", key = "#result.id")
  @Operation(summary = "Update a new Patient")
  public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable UUID id,
      @Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO) {

    PatientResponseDTO patientResponseDTO = patientService.updatePatient(id,
        patientRequestDTO);

    return ResponseEntity.ok().body(patientResponseDTO);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a Patient")
  public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
    patientService.deletePatient(id);
    return ResponseEntity.noContent().build();
  }
}
