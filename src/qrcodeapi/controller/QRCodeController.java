package qrcodeapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import qrcodeapi.service.BufferedImageService;

import java.awt.image.BufferedImage;

@RestController
public class QRCodeController {

    private final BufferedImageService bufferedImageService;

    public QRCodeController(BufferedImageService bufferedImageService) {
        this.bufferedImageService = bufferedImageService;
    }

    @GetMapping("/api/health")
    public ResponseEntity<?> getHealth() {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping(path = "/api/qrcode")
    public ResponseEntity<BufferedImage> getImage() {
        BufferedImage bufferedImage = bufferedImageService.generateWhiteImage(250,250);

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(bufferedImage);
    }
}
