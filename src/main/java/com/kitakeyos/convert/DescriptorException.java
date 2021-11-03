package com.kitakeyos.convert;

import java.io.IOException;

public class DescriptorException extends IOException {

    DescriptorException(String message, Exception e) {
        super(message, e);
    }

    DescriptorException(String message) {
        super(message);
    }
}
