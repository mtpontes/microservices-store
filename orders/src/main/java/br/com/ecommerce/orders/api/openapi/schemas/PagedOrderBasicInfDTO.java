package br.com.ecommerce.orders.api.openapi.schemas;

import org.springframework.data.domain.Page;

import br.com.ecommerce.orders.api.dto.order.OrderBasicInfDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Paged response for order basic information")
public class PagedOrderBasicInfDTO {
    private Page<OrderBasicInfDTO> content;
}