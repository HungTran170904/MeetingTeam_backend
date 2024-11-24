package com.HungTran.MeetingTeam.DTO;

import com.HungTran.MeetingTeam.Model.Message;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SocketMessage {
    private String topic;
    private Object payload;
}
