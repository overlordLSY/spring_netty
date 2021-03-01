package lsy.spring_netty.service;

import org.springframework.stereotype.Service;

@Service
public class TestService {

    public void printText(String message) {
        System.out.println(message);
    }
}
