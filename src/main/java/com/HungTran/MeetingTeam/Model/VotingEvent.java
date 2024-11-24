package com.HungTran.MeetingTeam.Model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class VotingEvent {
    private LocalDateTime createdAt;
    private String content;
}
