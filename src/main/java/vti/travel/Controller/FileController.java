package vti.travel.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vti.travel.Service.IFileService;
import vti.travel.utils.FileManager;
import java.io.IOException;
@CrossOrigin("*")
@RestController
@RequestMapping(value = "/files")
@Validated
public class FileController {

    @Autowired
    private IFileService fileService;

    @PostMapping(value = "/image")
    public ResponseEntity<?> upLoadImage(@RequestParam(name = "image") MultipartFile image) throws IOException {

        if (!new FileManager().isTypeFileImage(image)) {
            return new ResponseEntity<>("File must be image!", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<>(fileService.uploadImage(image), HttpStatus.OK);
    }
}
