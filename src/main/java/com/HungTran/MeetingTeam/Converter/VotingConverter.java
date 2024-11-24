package com.HungTran.MeetingTeam.Converter;

import com.HungTran.MeetingTeam.Exception.RequestException;
import com.HungTran.MeetingTeam.Model.Voting;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

public class VotingConverter implements AttributeConverter<Voting, String> {
    private final ObjectMapper objectMapper =new ObjectMapper().findAndRegisterModules();

    @Override
    public String convertToDatabaseColumn(Voting attribute) {
        try {
            String dbData=null;
            if(attribute!=null)
                dbData=objectMapper.writeValueAsString(attribute);
            return dbData;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RequestException("Unable to convert Option list to JSON string");
        }
    }

    @Override
    public Voting convertToEntityAttribute(String dbData) {
        try {
            Voting voting=null;
            if(dbData!=null) {
                voting=objectMapper.readValue(dbData,new TypeReference<Voting>(){});
            }
            return voting;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RequestException("Unable to convert JSON string to Options list");
        }
    }
}
