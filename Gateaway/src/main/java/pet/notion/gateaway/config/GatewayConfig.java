package pet.notion.gateaway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

   @Bean
   public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
      return builder.routes()
            .route("auth-service", r -> r.path("/api/v1/auth/register", "/api/v1/auth/login")
                  .uri("http://localhost:8081"))
            .build();
   }
}