package com.openclassrooms.paymybuddy.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class that manage the response at the user when an exception is handle the
 * class extend ResponseEntityExceptionHandler
 *
 * @author christine Duarte
 *
 */
@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
    /**
     * Method that return a message when an argument of the request is not valid
     *
     * @param ex      - the exception handle
     * @param request - a web request
     * @return a response entity with the message : containing the error, and the
     *         code HttpStatus 400
     *
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDate.now());
        body.put("status", status.value());

        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());

        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Method that return a message when a UserNotFoundException is thrown when a
     * user is not found in the database
     *
     * @param ex      - the exception handle
     * @param request - a web request
     * @return a response entity with the message :"Person not found", and the code
     *         HttpStatus 404
     */
//    @ExceptionHandler(UserNotFoundException.class)
//    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
//
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("timestamp", LocalDateTime.now());
//        body.put("message", "User not found, you should input an email that exist in database");
//
//        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
//    }

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView catchUserNotFoundException(UserNotFoundException ex) {

        ModelAndView model = new ModelAndView("error");
        model.addObject("errorMessage", ex.getMessage());
        return model;
    }
//
//    @ExceptionHandler(FriendAlreadyExistException.class)
//    public ModelAndView catchFriendAlreadyExistException(FriendAlreadyExistException ex) {
//
//        ModelAndView model = new ModelAndView("error");
//        model.addObject("errorMessage", ex.getMessage());
//        return model;
//    }


}
