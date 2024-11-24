package com.HungTran.MeetingTeam.Converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.HungTran.MeetingTeam.Exception.RequestException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;

public class SetStringConverter implements AttributeConverter<Set<String>,String>{
	private final ObjectMapper objectMapper=new ObjectMapper().findAndRegisterModules();
	@Override
	public String convertToDatabaseColumn(Set<String> attribute) {
		try {
			String dbData="[]";
			if(attribute!=null&&attribute.size()>0)
				dbData=objectMapper.writeValueAsString(attribute);
			return dbData;
		} catch (Exception e) {
			throw new RequestException("Unable to convert String list to JSON string");
		}
	}

	@Override
	public Set<String> convertToEntityAttribute(String dbData) {
		try {
			Set<String> set=new HashSet();
			if(dbData!=null) {
				set=objectMapper.readValue(dbData,new TypeReference<Set<String>>(){});
			}
			return set;
		} catch (Exception e) {
			throw new RequestException("Unable to convert JSON string to String Set");
		}
	}
	
}
