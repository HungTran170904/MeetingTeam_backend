package com.HungTran.MeetingTeam.Converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.HungTran.MeetingTeam.Exception.RequestException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;

public class IntegerSetConverter implements AttributeConverter<Set<Integer>, String>{
	private final ObjectMapper objectMapper=new ObjectMapper().findAndRegisterModules();
	@Override
	public String convertToDatabaseColumn(Set<Integer> attribute) {
		try {
			String dbData="[]";
			if(attribute!=null&&attribute.size()>0)
				dbData=objectMapper.writeValueAsString(attribute);
			return dbData;
		} catch (Exception e) {
			throw new RequestException("Unable to convert Integer list to JSON string");
		}
	}

	@Override
	public Set<Integer> convertToEntityAttribute(String dbData) {
		try {
			Set<Integer> list=new HashSet();
			if(dbData!=null) {
				list=objectMapper.readValue(dbData,new TypeReference<Set<Integer>>(){});
			}
			return list;
		} catch (Exception e) {
			throw new RequestException("Unable to convvert JSON string to Integer list");
		}
	}
	
}
