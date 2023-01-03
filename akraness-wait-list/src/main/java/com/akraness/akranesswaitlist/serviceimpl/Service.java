package com.akraness.akranesswaitlist.serviceimpl;

import com.akraness.akranesswaitlist.dto.Response;
import com.akraness.akranesswaitlist.dto.WaitListRequestDto;
import com.akraness.akranesswaitlist.entity.WaitList;
import com.akraness.akranesswaitlist.repository.IWaitList;
import com.akraness.akranesswaitlist.service.IService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class Service implements IService {
    @Autowired
    private IWaitList waitListRepository;
    @Override
    public ResponseEntity<Response> joinWaitList(WaitListRequestDto request) {
        Response response;
        if(waitListRepository.findByEmail(request.getEmail()).isPresent()){
            response = new Response("400", "Email exists already.",null);
            return ResponseEntity.badRequest().body(response);
        }
        WaitList entity = new WaitList();
        entity.setEmail(request.getEmail());
        entity.setCreatedDate(LocalDate.now());
        waitListRepository.save(entity);
        response = new Response("200","Successful",null);
        return ResponseEntity.ok(response);
    }
}
