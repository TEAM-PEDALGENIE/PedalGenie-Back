package com.pedalgenie.pedalgenieback.domain.genre.presentation;

import com.pedalgenie.pedalgenieback.domain.genre.application.GenreService;
import com.pedalgenie.pedalgenieback.domain.genre.dto.request.GenreRequestDto;
import com.pedalgenie.pedalgenieback.domain.genre.dto.response.GenreResponseDto;
import com.pedalgenie.pedalgenieback.domain.member.entity.MemberRole;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.ProductResponse;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import com.pedalgenie.pedalgenieback.global.jwt.AuthUtils;
import com.pedalgenie.pedalgenieback.global.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Genre api", description = "장르 작성, 목록 및 상세 조회 기능을 포함합니다.")
public class GenreController {
    private final GenreService genreService;
    private final TokenProvider tokenProvider;

    @Operation(summary="어드민 - 장르 작성")
    @PostMapping("/admin/genre")
    public ResponseEntity<ResponseTemplate<GenreResponseDto>> createGenre(@RequestBody GenreRequestDto requestDto){
        // 어드민이 아닐 때 예외처리
        if (!AuthUtils.getCurrentRole().equals(MemberRole.ADMIN.name())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ROLE);
        }
        GenreResponseDto responseDto = genreService.createGenre(requestDto);
        return ResponseTemplate.createTemplate(HttpStatus.CREATED, true, "장르별 악기 리스트 생성 성공", responseDto);
    }

    @Operation(summary="어드민 - 장르 전체 조회")
    @GetMapping("/admin/genre")
    public ResponseEntity<ResponseTemplate<List<GenreResponseDto>>> getAdminGenreList(){
        // 어드민이 아닐 때 예외처리
        if (!AuthUtils.getCurrentRole().equals(MemberRole.ADMIN.name())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ROLE);
        }
        List<GenreResponseDto> responseDtos = genreService.getAdminGenreList();
        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "장르별 악기 리스트 목록 조회 성공", responseDtos);
    }

    @Operation(summary="장르 악기 상세 조회")
    @GetMapping("/api/products")
    public ResponseEntity<ResponseTemplate<List<ProductResponse>>> getGenreProduct(@RequestParam String genre,
                                                                                     @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Long memberId = null;

        // 토큰이 있는 경우 memberId 추출
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            memberId = tokenProvider.getMemberIdFromToken(token);
        }

        List<ProductResponse> responseDtos = genreService.getGenreProduct(genre, memberId);

        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "장르별 악기 리스트 목록 조회 성공", responseDtos);
    }
}
