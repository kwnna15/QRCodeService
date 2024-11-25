package qrcodeapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import qrcodeapi.service.BufferedImageService;

import java.awt.image.BufferedImage;
import java.util.Set;

@RestController
public class QRCodeController {

    private static final Set<String> VALID_FILE_TYPES = Set.of("png", "jpeg", "gif");
    private final BufferedImageService bufferedImageService;

    public QRCodeController(BufferedImageService bufferedImageService) {
        this.bufferedImageService = bufferedImageService;
    }

    @GetMapping("/api/health")
    public ResponseEntity<?> getHealth() {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping(path = "/api/qrcode")
    public ResponseEntity<?> getImage(@RequestParam("size") int size, @RequestParam("type") String type) {
        //int QRCodeSize = Integer.parseInt(size);
        if (size < 150 || size > 350) {
            return ResponseEntity.badRequest()
                    .body("""
                            {
                              "error": "Image size must be between 150 and 350 pixels"
                            }
                            """);
        }
        if (!VALID_FILE_TYPES.contains(type.toLowerCase())) {
            return ResponseEntity.badRequest()
                    .body("""
                            {
                              "error": "Only png, jpeg and gif image types are supported"
                            }
                            """);
        }

        BufferedImage bufferedImage = bufferedImageService.generateWhiteImage(size, size);

        return ResponseEntity
                .ok()
                .contentType(getContentType(type))
                .body(bufferedImage);
    }

    private static MediaType getContentType(String type) {
        return switch (type.toLowerCase()) {
            case "png" -> MediaType.IMAGE_PNG;
            case "jpeg" -> MediaType.IMAGE_JPEG;
            case "gif" -> MediaType.IMAGE_GIF;
            default -> throw new IllegalStateException("Unsupported type=" + type);
        };
    }
}
