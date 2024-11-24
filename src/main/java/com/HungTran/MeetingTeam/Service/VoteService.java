package com.HungTran.MeetingTeam.Service;

import com.HungTran.MeetingTeam.Exception.RequestException;
import com.HungTran.MeetingTeam.Model.Message;
import com.HungTran.MeetingTeam.Model.Option;
import com.HungTran.MeetingTeam.Model.VotingEvent;
import com.HungTran.MeetingTeam.Repository.ChannelRepo;
import com.HungTran.MeetingTeam.Repository.MessageRepo;
import com.HungTran.MeetingTeam.Util.Constraint;
import com.HungTran.MeetingTeam.Util.InfoChecking;
import com.HungTran.MeetingTeam.Util.SocketTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final MessageRepo messageRepo;
    private final ChatService chatService;
    private final InfoChecking infoChecking;


    public void createVoting(Message message){
        message.setMessageType(Constraint.VOTING);
        var user=infoChecking.getUserFromContext();
        message.setSenderId(user.getId());
        message.getVoting().setIsBlocked(false);
        var VotingEvent=new VotingEvent(LocalDateTime.now(),user.getNickName()+" has created the vote");
        message.getVoting().setEvents(List.of(VotingEvent));
        messageRepo.save(message);
        chatService.broadcastMessage(message);
    }
    public void chooseOption(Integer messId, List<String> optionNames){
        var message=messageRepo.findById(messId).orElseThrow(()->new RequestException("MessageId "+messId+" does not exists"));
        if(!message.getMessageType().equals(Constraint.VOTING))
            throw new RequestException("This is not voting message");
        var voting=message.getVoting();
        var now=LocalDateTime.now();
        if(voting.getIsBlocked()||(voting.getEndTime()!=null&&voting.getEndTime().isBefore(now)))
            throw new RequestException("This voting has been blocked");
        var user=infoChecking.getUserFromContext();
        if(voting.getIsSingleAnswer()&&optionNames.size()>1)
            throw new RequestException("You are allowed to choose just one option in this vote");
        Boolean isVoted=false;
        for(Option option: voting.getOptions()){
            if(optionNames.contains(option.getName())){
                if(!option.getUserIds().contains(user.getId()))
                    option.getUserIds().add(user.getId());
                else isVoted=true;
            }
            else option.getUserIds().remove(user.getId());
        }
        var content=user.getNickName()+(isVoted?" has changed his options to ":" has choosed ")+
                optionNames.stream().reduce("",(s,name)->s+name+",")+
                " in the vote "+message.getContent();
        var event=new VotingEvent(now,content);
        if(voting.getEvents()==null) voting.setEvents(new ArrayList<>());
        voting.getEvents().add(event);
        message.setCreatedAt(now);
        message.setVoting(voting);
        chatService.broadcastMessage(message);
        messageRepo.save(message);
    }
    public void blockVoting(Integer messId){
        var message=messageRepo.findById(messId).orElseThrow(()->new RequestException("MessageId "+messId+" does not exists"));
        if(!message.getMessageType().equals(Constraint.VOTING))
            throw new RequestException("This is not voting message");
        var user=infoChecking.getUserFromContext();
        if(!message.getSenderId().equals(user.getId()))
            throw new RequestException("You are not allowed to block this vote");
        message.getVoting().setIsBlocked(true);
        var event=new VotingEvent(LocalDateTime.now(),
                user.getNickName()+" has blocked the vote ");
        if(message.getVoting().getEvents()==null) message.getVoting().setEvents(new ArrayList<>());
        message.getVoting().getEvents().add(event);
        chatService.broadcastMessage(message);
        messageRepo.save(message);
    }
}
