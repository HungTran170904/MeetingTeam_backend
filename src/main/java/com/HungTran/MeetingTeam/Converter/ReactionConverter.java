package com.HungTran.MeetingTeam.Converter;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.HungTran.MeetingTeam.Exception.RequestException;
import com.HungTran.MeetingTeam.Model.MessageReaction;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;

public class ReactionConverter implements AttributeConverter<List<MessageReaction>, String> {
	private final Logger LOGGER=LoggerFactory.getLogger(ReactionConverter.class);
	private final ObjectMapper objectMapper=new ObjectMapper().findAndRegisterModules();
	@Override
	public String convertToDatabaseColumn(List<MessageReaction> attribute) {
		try {	
			String reactionJson="[]";
			if(attribute!=null&&!attribute.isEmpty()) 
				reactionJson=objectMapper.writeValueAsString(attribute);
			return reactionJson;
		} catch (Exception e) {
			throw new RequestException("Unable to convert messageReactions to JSON string");
		}
	}
	
	@Override
	public List<MessageReaction> convertToEntityAttribute(String dbData) {
		try {
			List<MessageReaction> reactions=null;
			if(dbData!=null)
				reactions=objectMapper.readValue(dbData,new TypeReference<List<MessageReaction>>(){});
			return reactions;
		} catch (Exception e) {
			throw new RequestException("Unable to convert JSON string to messageReactions");
		}
	}

}

