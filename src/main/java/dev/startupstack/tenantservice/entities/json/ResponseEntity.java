package dev.startupstack.tenantservice.entities.json;


/**
 * ResponseEntity
 */
public class ResponseEntity {

    private String message;
    private int statuscode;
    private Object responseObject;

    public static final String MESSAGE_OK = "ok";

    public ResponseEntity() {

    }

    public ResponseEntity(String message, int statuscode, Object object) {
        this.message = message;
        this.statuscode = statuscode;
        this.responseObject = object;

    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }

    public int getStatusCode() {
        return statuscode;
    }

    public void setStatusCode(int code) {
        this.statuscode = code;
    }

    public Object getResponseObject() {
        return responseObject;
    }
}