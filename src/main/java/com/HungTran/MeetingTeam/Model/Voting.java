package com.HungTran.MeetingTeam.Model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class Voting {
    private Boolean isSingleAnswer=false;
    private Boolean isBlocked=false;
    private LocalDateTime endTime;
    private List<Option> options;
    private List<VotingEvent> events;
}
