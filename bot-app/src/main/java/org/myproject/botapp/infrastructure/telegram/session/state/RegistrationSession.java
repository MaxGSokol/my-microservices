package org.myproject.botapp.infrastructure.telegram.session.state;

import lombok.Getter;
import lombok.Setter;
import org.myproject.botapp.application.dto.UserServiceDto;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RegistrationSession {
    private UserServiceDto userServiceDto;
    private boolean isAdmin = false;
    private RegistrationSessionFields currentField;

    public void reset() {
        this.userServiceDto = new UserServiceDto();
        this.currentField = null;
        this.isAdmin = false;
    }
}
