package com.innowise.user_service.controller;

import com.innowise.user_service.model.dto.CardInfoDTO;
import com.innowise.user_service.service.CardInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardInfoController {

    private final CardInfoService cardInfoService;

    @GetMapping("/{id}")
    public ResponseEntity<CardInfoDTO> getCardById(@PathVariable Long id) {
        return ResponseEntity.ok(cardInfoService.getCardById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CardInfoDTO>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(cardInfoService.getAllCards(PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity<CardInfoDTO> createCard(@Valid @RequestBody CardInfoDTO cardDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cardInfoService.createCard(cardDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardInfoDTO> updateCard(@PathVariable Long id,
                                                  @Valid @RequestBody CardInfoDTO cardDTO) {
        return ResponseEntity.ok(cardInfoService.updateCard(id, cardDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardInfoService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}