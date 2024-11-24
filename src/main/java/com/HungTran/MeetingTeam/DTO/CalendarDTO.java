package com.HungTran.MeetingTeam.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class CalendarDTO {
    private List<MeetingDTO> meetings;
    private List<LocalDateTime> weekRange;
}
