package com.digitalhub.controller;

import com.digitalhub.dto.ApiResponse;
import com.digitalhub.dto.OrderItemDto;
import com.digitalhub.security.UserPrincipal;
import com.digitalhub.service.DownloadService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/downloads")
public class DownloadController {

    @Autowired
    private DownloadService downloadService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderItemDto>>> getUserDownloads(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok("Downloadable assets", downloadService.getCustomerDownloads(principal.getId())));
    }

    @GetMapping("/{orderItemId}")
    public ResponseEntity<Resource> downloadAsset(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long orderItemId,
            HttpServletRequest request
    ) {
        String clientIp = request.getRemoteAddr();
        DownloadService.DownloadPayload payload = downloadService.processDownload(orderItemId, principal.getId(), clientIp);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + payload.getFileName() + "\"")
                .body(payload.getResource());
    }
}