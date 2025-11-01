package com.movieservice.connector;

import com.movieservice.dto.response.MovieResponseDto.MetadataResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static com.movieservice.common.constant.ApiConstant.METADATA_CONNECTOR_PATH;

@FeignClient(
        name = "metadata-service",
        url = "${connector.metadata.service.url}" + METADATA_CONNECTOR_PATH
)
public interface MetadataServiceConnector {

    @GetMapping("/{metadata-id}")
    ResponseEntity<MetadataResponseDto> getMetadata(@PathVariable("metadata-id") Long metadataId);

}
