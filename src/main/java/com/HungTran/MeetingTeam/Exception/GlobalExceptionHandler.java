package com.HungTran.MeetingTeam.Exception;

import com.HungTran.MeetingTeam.Util.Constraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.socket.WebSocketMessage;

import com.HungTran.MeetingTeam.Model.Message;
import com.HungTran.MeetingTeam.Util.InfoChecking;
import com.HungTran.MeetingTeam.WebSocket.WebSocketEventListener;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler{
	private final Logger LOGGER=LoggerFactory.getLogger(Exception.class);
	@Autowired
	SimpMessagingTemplate messageTemplate;
	@Autowired
	InfoChecking infoChecking;
	 @MessageExceptionHandler(MessageException.class)
	 public void handleSocketException(Exception e) {
		 LOGGER.error(e.getMessage());
		 Message error=new Message();
		 error.setContent(e.getMessage());
		 error.setMessageType(Constraint.ERROR);
		 messageTemplate.convertAndSendToUser(infoChecking.getUserIdFromContext(),"/user", error);
	  }
	 @ExceptionHandler({AuthenticationCredentialsNotFoundException.class,
			BadCredentialsException.class,
			InternalAuthenticationServiceException.class}) 
	    public ResponseEntity<String> handleTokenException(Exception e) {
		 	LOGGER.error(e.getMessage());
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	    }
	 @ExceptionHandler(PermissionException.class)
	 public ResponseEntity<String> handlePermissionException(Exception e){
		 LOGGER.error(e.getMessage());
	     return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
	 }
	 @ExceptionHandler({RequestException.class, FileException.class})
	 public ResponseEntity<String> handleRequestException(Exception e){
		 LOGGER.error(e.getMessage());
		 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	 }
	 @ExceptionHandler(Exception.class)
	 public ResponseEntity<String> handleUnknownException(Exception e){
		 LOGGER.error(e.getMessage());
		 return ResponseEntity.status(500).body(e.getMessage());
	 }
}
