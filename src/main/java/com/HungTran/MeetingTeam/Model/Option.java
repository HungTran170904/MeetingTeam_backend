package com.HungTran.MeetingTeam.Model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Option {
    private String name;
    private List<String> userIds=new ArrayList<>();
}
