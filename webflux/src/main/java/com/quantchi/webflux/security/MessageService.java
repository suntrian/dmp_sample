package com.quantchi.webflux.security;

import org.apache.logging.log4j.message.FormattedMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class MessageService {

  public Flux<FormattedMessage> getDefaultMessage(){
    return Flux.just(new FormattedMessage("DEFAULT"));
  }

  public Flux<FormattedMessage> getCustomMessage(String name){
    return Flux.just(new FormattedMessage(name));
  }

}
