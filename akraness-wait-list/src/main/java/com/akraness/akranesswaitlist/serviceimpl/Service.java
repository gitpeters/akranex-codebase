package com.akraness.akranesswaitlist.serviceimpl;

import com.akraness.akranesswaitlist.dto.Response;
import com.akraness.akranesswaitlist.dto.WaitListRequestDto;
import com.akraness.akranesswaitlist.entity.WaitList;
import com.akraness.akranesswaitlist.exception.DuplicateException;
import com.akraness.akranesswaitlist.repository.IWaitList;
import com.akraness.akranesswaitlist.service.IService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class Service implements IService {
    @Autowired
    private IWaitList waitListRepository;
    @Override
    public ResponseEntity<Response> joinWaitList(WaitListRequestDto request) {
        if (waitListRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateException(request.getEmail());
        }
        Response response;
        WaitList entity = new WaitList();
        entity.setEmail(request.getEmail());
        entity.setCreatedDate(LocalDate.now());
        waitListRepository.save(entity);
        response = new Response("200","Successful",null);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Response> getAllWaitingUsers() {
        List<WaitListRequestDto> dtoList =new ArrayList<>();
        Response response = new Response("200","Successful",null);
        List<WaitList> all_waiting = (List<WaitList>) waitListRepository.findAll();
        all_waiting.forEach(x -> {
            WaitListRequestDto dto = new WaitListRequestDto();
            dto.setEmail(x.getEmail());
            dtoList.add(dto);
        });
        response.setData(dtoList);
        return ResponseEntity.ok(response);
    }
}
