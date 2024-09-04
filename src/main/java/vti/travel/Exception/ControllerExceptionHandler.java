package vti.travel.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;


@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<AppException> catchExceptionGlobal(AppException exception, HttpServletRequest request) {
        exception.setPath(request.getRequestURI());
        return ResponseEntity.status(exception.getCode()).body(exception);

    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppException> catchExceptionGlobal2(Exception exception, HttpServletRequest request) {
        AppException appException = new AppException(exception);
        appException.setPath(request.getRequestURI());
        return ResponseEntity.status(appException.getCode()).body(appException);

    }

    // Mehtod bắt lỗi validate
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<AppException> handleBindException(BindException e, HttpServletRequest request) {
        String errorMessage = "";
        if (e.getBindingResult().hasErrors()) {
            for (int i = 0; i < e.getBindingResult().getAllErrors().size(); i++) {
                errorMessage += e.getBindingResult().getAllErrors().get(i).getDefaultMessage();
                errorMessage += (i == e.getBindingResult().getAllErrors().size() - 1) ? "." : ", ";
            }
        }
        AppException appException = new AppException(errorMessage, 400, request.getRequestURI());
        return new ResponseEntity<>(appException, HttpStatus.valueOf(appException.getCode()));
    }

//    // Method bắt lỗi từ enum ErrorResponseBase
//    @ExceptionHandler(value = Exception.class)
//    public ResponseEntity<ErrorResponseBase> handleException(Exception e, HttpServletRequest request) {
//        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
//        String message = ErrorResponseBase.INTERNAL_SERVER_ERROR.getMessage();
//
//        for (ErrorResponseBase error : ErrorResponseBase.values()) {
//            if (e.getMessage().contains(error.getMessage())) {
//                status = error.getStatus();
//                message = error.getMessage();
//                break;
//            }
//        }
//
//        ErrorResponseBase errorResponse = ErrorResponseBase.NOT_FOUND;
//
//        HttpStatus status = errorResponse.getStatus();
//        String message = errorResponse.getMessage();
//        return new ResponseEntity<>(errorResponse, status);
//    }

}
