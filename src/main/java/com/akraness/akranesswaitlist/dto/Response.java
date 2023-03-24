package com.akraness.akranesswaitlist.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private String code;
    private String description;
    private List<Error> errors;
    private Object data;
    private boolean status;

    public Response(String code, String description, @Nullable List<Error> errors) {
        this.code = code;
        this.description = description;
        this.errors = errors;
        this.data = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (Response) obj;
        return Objects.equals(this.code, that.code) &&
                Objects.equals(this.description, that.description) &&
                Objects.equals(this.errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, description, errors);
    }

    @Override
    public String toString() {
        return "Response[" +
                "code=" + code + ", " +
                "description=" + description + ", " +
                "errors=" + errors + ']';
    }
}
