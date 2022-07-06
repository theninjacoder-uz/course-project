package com.itransition.courseproject.config.component;

import com.itransition.courseproject.model.entity.user.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Optional;


@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditorConfigurer {

    @Bean
    AuditorAware<Long> auditorProvider(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(auth) && auth.isAuthenticated() && !"anonymous".equals(auth.getPrincipal()))
            return () -> Optional.of(((User) auth.getPrincipal()).getId());
        return Optional::empty;
    }
}
