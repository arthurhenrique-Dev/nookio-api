package com.henrique.nookio_api.modules.files.annotations.chain;

import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

public abstract class ValidateProcess {

    @Setter
    private ValidateProcess next;

    public boolean handle(MultipartFile file){
        return validate(file) && (next == null || next.handle(file));
    }

    protected abstract boolean validate(MultipartFile file);
}
