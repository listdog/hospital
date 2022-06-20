package com.hos.hospital.service;
import com.github.pagehelper.PageInfo;
import com.hos.hospital.entity.Patient;
import com.hos.hospital.entity.PatientExample;

import java.util.List;

public interface PatientService {

	   int countByExample(PatientExample example);


	    int deleteByPrimaryKey(Integer id);


	    int insertSelective(Patient record);

	    List<Patient> selectByExample(PatientExample example);

	    Patient selectByPrimaryKey(Integer id);



	    int updateByPrimaryKeySelective(Patient record);


		PageInfo<Patient> selectPatientList(Patient patient, Integer page, Integer size);


		List<Patient> selectPatient(Patient patient);


}
