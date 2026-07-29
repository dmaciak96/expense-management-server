package com.example.expense_management_server.adapter.persistence.config

import com.example.expense_management_server.adapter.persistence.model.ApplicationUserEntity
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.mongodb.config.EnableMongoAuditing


@Configuration
@EnableMongoAuditing
class MongoConfiguration {

    @Bean
    fun currentLoggedUserProvider(fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase): AuditorAware<ApplicationUserEntity> {
        return CurrentLoggedUserAuditorAware(fetchCurrentLoginUserUseCase)
    }
}