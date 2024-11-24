package com.HungTran.MeetingTeam.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;

@Data
@AllArgsConstructor
public class LoginDTO {
    private UserDTO user;
    private Date tokenExpiredDate;
}
