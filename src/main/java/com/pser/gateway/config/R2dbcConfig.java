package com.pser.gateway.config;

import com.pser.gateway.domain.RoleReadingConverter;
import com.pser.gateway.domain.RoleWritingConverter;
import io.r2dbc.spi.ConnectionFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.lang.NonNull;

@Configuration
@RequiredArgsConstructor
public class R2dbcConfig extends AbstractR2dbcConfiguration {
    private final ConnectionFactory connectionFactory;

    @Override
    @NonNull
    protected List<Object> getCustomConverters() {
        return List.of(new RoleWritingConverter(), new RoleReadingConverter());
    }

    @Override
    @NonNull
    public ConnectionFactory connectionFactory() {
        return connectionFactory;
    }
}
