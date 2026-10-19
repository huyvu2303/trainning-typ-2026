package com.courseshop.courseshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.courseshop.courseshop.model.Todo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



@Controller 
public class HelloController{

    // private final HelloService helloService;
    

    // public HelloController(HelloService helloService) {
    //     this.helloService = helloService;
    // }


    @RequestMapping("/")
    public String requestMethodName() {
        return "hello";
    }
    
}

// @RestController
// public class HelloController {

//     @GetMapping("/")
//     public String index() {
//         return "Xin chào! con cho tuan dau cat moi.";
//     }

//     @GetMapping("/chao")
//     public String chaoban(@RequestParam(name = "name") String name ) {
//         return "Xin chào! " + name;
//     }


//     @GetMapping("/hello")
//     public ResponseEntity<String> hello() {
//         //return "Hello World từ Course Shop!";
//         return ResponseEntity.ok().body("Hello World");
//     }
//     @GetMapping("/hi")
//     public ResponseEntity<Todo> huyvu() {
//         Todo test = new Todo("huyvu todo", true);
//         //return test;
//         return ResponseEntity.ok().body(test);
//     }
// }