package qrcodeapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import qrcodeapi.service.QRCodeService;

import java.awt.image.BufferedImage;
import java.util.Set;

@RestController
public class QRCodeController {

    private static final Set<String> VALID_FILE_TYPES = Set.of("png", "jpeg", "gif");
    private static final Set<String> VALID_CORRECTION_TYPES = Set.of("L", "M", "Q", "H");
    private final QRCodeService qrCodeService;

    public QRCodeController(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/api/health")
    public ResponseEntity<?> getHealth() {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    // http://localhost:8080/api/qrcode?contents="hello"&size=250&type=png
    @GetMapping(path = "/api/qrcode")
    public ResponseEntity<?> getImage(@RequestParam(value = "contents") String contents,
                                      @RequestParam(value = "size", defaultValue = "250") int size,
                                      @RequestParam(value = "type", defaultValue = "png") String type,
                                      @RequestParam(value = "correction", defaultValue = "L") String correction) {
        if (contents == null || contents.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("""
                            {
                              "error": "Contents cannot be null or blank"
                            }
                            """);
        }
        if (size < 150 || size > 350) {
            return ResponseEntity.badRequest()
                    .body("""
                            {
                              "error": "Image size must be between 150 and 350 pixels"
                            }
                            """);
        }
        if (!VALID_CORRECTION_TYPES.contains(correction.toUpperCase())){
            return ResponseEntity.badRequest()
                    .body("""
                            {
                              "error": "Permitted error correction levels are L, M, Q, H"
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

        BufferedImage bufferedImage = qrCodeService.generateQRCode(contents, size, size, correction);

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
