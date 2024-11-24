package com.HungTran.MeetingTeam.Controller;

import com.HungTran.MeetingTeam.Model.Message;
import com.HungTran.MeetingTeam.Service.VoteService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/voting")
@RequiredArgsConstructor
public class VoteController {
    private final VoteService voteService;
    private final ObjectMapper objectMapper=new ObjectMapper().findAndRegisterModules();

    @PostMapping("/createVoting")
    public ResponseEntity<HttpStatus> createVoting(@RequestBody Message message){
        voteService.createVoting(message);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/chooseOption")
    public ResponseEntity<HttpStatus> chooseOption(
            @RequestParam("messId") Integer messId,
            @RequestParam("optionNames") String namesJson) throws Exception{
        List<String> optionNames=objectMapper.readValue(namesJson, new TypeReference<List<String>>(){});
        voteService.chooseOption(messId,optionNames);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/blockVoting/{id}")
    public ResponseEntity<HttpStatus> cancelVoting(
            @PathVariable("id") Integer id){
        voteService.blockVoting(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
