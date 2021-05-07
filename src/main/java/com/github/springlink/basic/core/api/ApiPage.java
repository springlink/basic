package com.github.springlink.basic.core.api;

import java.util.List;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "API page")
@NoArgsConstructor
@AllArgsConstructor()
public class ApiPage<T> {
	@Schema(description = "Elements")
	private List<T> elements;

	@Schema(description = "Total")
	private Long total;

	public static <T> ApiPage<T> fromPage(Page<T> page) {
		return new ApiPage<>(page.getContent(), page.getTotalElements());
	}
}
