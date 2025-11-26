package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.Opportunity.SubscriberDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.service.SubscribersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriber")
public class SubscriberController {

    private final SubscribersService subscribersService;

    public SubscriberController(SubscribersService subscribersService) {
        this.subscribersService = subscribersService;
    }

    @PostMapping("/toggleSubscriber/{athletId}/{opportunityId}")
    public ResponseEntity<ResponseDTO<SubscriberDTO>> toggleSubscriber(@PathVariable Long athletId, @PathVariable Long opportunityId) {
        ResponseDTO<SubscriberDTO> result = subscribersService.toggleSubscriber(athletId, opportunityId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/subscribers/{opportunityId}")
    public ResponseEntity<ResponseDTO<List<SubscriberDTO>>> getSubscribersByOpportunity(@PathVariable Long opportunityId) {
        List<SubscriberDTO> subscribers = subscribersService.getSubscribersByOpportunity(opportunityId);
        ResponseDTO<List<SubscriberDTO>> result = new ResponseDTO<>("Inscritos carregados com sucesso!", subscribers);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/isSubscribed/{athleteId}/{opportunityId}")
    public ResponseEntity<Boolean> isSubscribed(@PathVariable Long athleteId, @PathVariable Long opportunityId) {
        Boolean result = subscribersService.isSubscribed(athleteId, opportunityId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/athlete/{athleteId}")
    public ResponseEntity<List<SubscriberDTO>> getSubscribersByAthlete(@PathVariable Long athleteId) {
        List<SubscriberDTO> result = subscribersService.getSubscribersByAthlete(athleteId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
