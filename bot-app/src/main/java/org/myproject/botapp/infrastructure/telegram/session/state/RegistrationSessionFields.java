package org.myproject.botapp.infrastructure.telegram.session.state;

import lombok.Getter;

@Getter
public enum RegistrationSessionFields {
    NAME("name"),
    TEL_NUM("telNum");

    private final String fieldName;


    RegistrationSessionFields(String fieldName) {
        this.fieldName = fieldName;
    }
}
