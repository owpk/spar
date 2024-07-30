package ru.sparural.file.dto;

import lombok.Data;

@Data
public class FileServerResponse<T> {
    private Boolean success;
    private T data;

    public FileServerResponse() {
        this.success = true;
        this.data = null;
    }


}
