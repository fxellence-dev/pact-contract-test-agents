package com.example.merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Standard API error response")
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Error category", example = "Not Found")
    private String error;

    @Schema(description = "Detailed error message", example = "Merchant not found: MERCH999")
    private String message;

    @Schema(description = "Timestamp of the error")
    private LocalDateTime timestamp;

    @Schema(description = "Request path that caused the error", example = "/api/merchants/MERCH999")
    private String path;

    public ErrorResponse() {}

    public ErrorResponse(int status, String error, String message, LocalDateTime timestamp, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
    }

    public int getStatus()                { return status; }
    public void setStatus(int status)     { this.status = status; }

    public String getError()              { return error; }
    public void setError(String error)    { this.error = error; }

    public String getMessage()                { return message; }
    public void setMessage(String message)    { this.message = message; }

    public LocalDateTime getTimestamp()               { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getPath()               { return path; }
    public void setPath(String path)      { this.path = path; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private int status;
        private String error, message, path;
        private LocalDateTime timestamp;

        public Builder status(int v)          { this.status = v; return this; }
        public Builder error(String v)        { this.error = v; return this; }
        public Builder message(String v)      { this.message = v; return this; }
        public Builder timestamp(LocalDateTime v) { this.timestamp = v; return this; }
        public Builder path(String v)         { this.path = v; return this; }

        public ErrorResponse build() {
            return new ErrorResponse(status, error, message, timestamp, path);
        }
    }
}
