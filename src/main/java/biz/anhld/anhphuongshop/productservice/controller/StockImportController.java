package biz.anhld.anhphuongshop.productservice.controller;

import biz.anhld.anhphuongshop.productservice.dto.BasePageResponse;
import biz.anhld.anhphuongshop.productservice.dto.StockImportDTO;
import biz.anhld.anhphuongshop.productservice.dto.StockImportRequest;
import biz.anhld.anhphuongshop.productservice.service.StockImportService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stock-imports")
public class StockImportController {

    private final StockImportService stockImportService;

    public StockImportController(StockImportService stockImportService) {
        this.stockImportService = stockImportService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<StockImportDTO> createImport(
        @Valid @RequestBody StockImportRequest request,
        @AuthenticationPrincipal Jwt jwt
    ) throws Exception {
        String createdBy = jwt != null ? jwt.getClaims().get("preferred_username").toString() : "unknown";
        return ResponseEntity.ok(stockImportService.createImport(request, createdBy));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<BasePageResponse<StockImportDTO>> getImports(
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(stockImportService.getImports(pageable));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<StockImportDTO> getImportById(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(stockImportService.getImportById(id));
    }
}
